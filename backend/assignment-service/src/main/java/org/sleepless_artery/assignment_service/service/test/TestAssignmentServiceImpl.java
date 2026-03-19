package org.sleepless_artery.assignment_service.service.test;

import lombok.RequiredArgsConstructor;
import org.sleepless_artery.assignment_service.config.cache.CacheConfig;
import org.sleepless_artery.assignment_service.dto.request.TestAssignmentRequestDto;
import org.sleepless_artery.assignment_service.dto.response.AssignmentResponseDto;
import org.sleepless_artery.assignment_service.dto.response.TestAssignmentResponseDto;
import org.sleepless_artery.assignment_service.exception.AssignmentAlreadyExistsException;
import org.sleepless_artery.assignment_service.exception.AssignmentNotFoundException;
import org.sleepless_artery.assignment_service.exception.ExternalServiceUnavailableException;
import org.sleepless_artery.assignment_service.exception.InvalidLessonIdException;
import org.sleepless_artery.assignment_service.logging.event.LogEvent;
import org.sleepless_artery.assignment_service.logging.annotation.BusinessEvent;
import org.sleepless_artery.assignment_service.mapper.TestAssignmentMapper;
import org.sleepless_artery.assignment_service.model.test.TestAssignment;
import org.sleepless_artery.assignment_service.repository.test.TestAssignmentRepository;
import org.sleepless_artery.assignment_service.service.external.lesson.LessonExistenceChecker;
import org.sleepless_artery.assignment_service.service.infrastructure.kafka.producer.KafkaProducer;
import org.sleepless_artery.assignment_service.service.util.CommonAssignmentUpdater;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Service for test-based assignment management.
 * <p>
 * Handles CRUD operations for test assignments with validation of test data,
 * caching strategies, and Kafka event notifications.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class TestAssignmentServiceImpl implements TestAssignmentService {

    private final TestAssignmentRepository testAssignmentRepository;
    private final TestAssignmentMapper assignmentMapper;
    private final CommonAssignmentUpdater assignmentUpdater;
    private final LessonExistenceChecker lessonExistenceChecker;
    private final KafkaProducer kafkaProducer;

    @Value("${spring.kafka.topic.prefix}")
    private String prefix;

    @Value("${spring.kafka.topic.domain}")
    private String domain;


    /**
     * Creates a new test assignment with validation.
     *
     * @param requestDto the test assignment creation data
     * @return created test assignment data transfer object
     */
    @Override
    @BusinessEvent(LogEvent.ASSIGNMENT_CREATION)
    @CacheEvict(cacheNames = CacheConfig.LESSON_CACHE_NAME, key = "#result.lessonId")
    @Transactional
    public TestAssignmentResponseDto createAssignment(TestAssignmentRequestDto requestDto) {
        var lessonId = requestDto.getLessonId();

        switch (lessonExistenceChecker.verifyLessonExistence(lessonId)) {
            case NOT_FOUND -> throw new InvalidLessonIdException("Lesson not found with ID: " + lessonId);
            case SERVICE_UNAVAILABLE -> throw new ExternalServiceUnavailableException("Service unavailable");
        }

        if (testAssignmentRepository.findByLessonIdAndTitleIgnoreCase(lessonId, requestDto.getTitle()) != null) {
            throw new AssignmentAlreadyExistsException(
                    "Test assignment already exists with title: " + requestDto.getTitle() +
                            " for lesson: " + lessonId
            );
        }

        validateTestData(requestDto);

        return assignmentMapper.toDto(testAssignmentRepository.save(assignmentMapper.toEntity(requestDto)));
    }


    /**
     * Updates test assignment with full cache invalidation.
     *
     * @param id the assignment ID
     * @param requestDto the update data
     * @return updated test assignment data transfer object
     */
    @Override
    @BusinessEvent(LogEvent.ASSIGNMENT_UPDATE)
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheConfig.ASSIGNMENT_CACHE_NAME, key = "#id"),
            @CacheEvict(cacheNames = CacheConfig.LESSON_CACHE_NAME, key = "#result.lessonId")
    })
    @Transactional
    public TestAssignmentResponseDto updateAssignment(Long id, TestAssignmentRequestDto requestDto) {
        var assignment = findById(id);
        validateTestData(requestDto);

        var existingAssignment = testAssignmentRepository.findByLessonIdAndTitleIgnoreCase(
                requestDto.getLessonId(), requestDto.getTitle()
        );
        if (existingAssignment != null && !existingAssignment.getId().equals(assignment.getId())) {
            throw new AssignmentAlreadyExistsException(
                    "Test assignment already exists with title: " + requestDto.getTitle() +
                            " for lesson: " + requestDto.getLessonId()
            );
        }

        assignmentUpdater.updateAssignment(assignment, requestDto);

        assignment.setCondition(requestDto.getCondition());
        assignment.setOptions(requestDto.getOptions());
        assignment.setCorrectOptionsIndices(requestDto.getCorrectOptionsIndices());

        return assignmentMapper.toDto(testAssignmentRepository.save(assignment));
    }


    /**
     * Deletes test assignment by ID.
     * <p>
     * Removes database record, evicts caches, and sends deletion event to Kafka.
     * </p>
     *
     * @param id the assignment ID
     * @return deleted assignment data transfer object
     */
    @Override
    @BusinessEvent(LogEvent.ASSIGNMENT_DELETION)
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheConfig.ASSIGNMENT_CACHE_NAME, key = "#id"),
            @CacheEvict(cacheNames = CacheConfig.LESSON_CACHE_NAME, key = "#result.lessonId")
    })
    @Transactional
    public AssignmentResponseDto deleteAssignmentById(Long id) {
        var assignment = findById(id);
        testAssignmentRepository.deleteById(id);

        kafkaProducer.send(String.format("%s.%s.%s", prefix, domain, "deleted"), id.toString());

        return assignmentMapper.toDto(assignment);
    }


    /**
     * Retrieves test-based assignment by ID.
     *
     * @param id the assignment ID
     * @return test assignment entity
     * @throws AssignmentNotFoundException if assignment does not exist
     */
    private TestAssignment findById(Long id) {
        return testAssignmentRepository.findById(id)
                .orElseThrow(() ->  new AssignmentNotFoundException("Test assignment not found with ID: " + id));
    }


    /**
     * Validates test assignment data structure.
     *
     * @param requestDto the test assignment data
     * @throws IllegalArgumentException if validation fails
     */
    private void validateTestData(TestAssignmentRequestDto requestDto) {
        if (requestDto.getOptions() == null || requestDto.getCorrectOptionsIndices() == null) {
            throw new IllegalArgumentException("Options and correctOptionsIndices must be provided");
        }

        var outOfBounds = requestDto.getCorrectOptionsIndices().stream()
                .anyMatch(index -> index < 0 || index >= requestDto.getOptions().size());

        if (outOfBounds) {
            throw new IllegalArgumentException("Correct options indices are out of array bounds");
        }
    }
}