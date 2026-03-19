package org.sleepless_artery.lesson_service.service.core;

import lombok.RequiredArgsConstructor;
import org.sleepless_artery.lesson_service.dto.LessonContentDto;
import org.sleepless_artery.lesson_service.dto.LessonInfoDto;
import org.sleepless_artery.lesson_service.dto.LessonRequestDto;
import org.sleepless_artery.lesson_service.exception.ExternalServiceUnavailableException;
import org.sleepless_artery.lesson_service.exception.CourseDoesNotExistException;
import org.sleepless_artery.lesson_service.exception.LessonAlreadyExistsException;
import org.sleepless_artery.lesson_service.exception.LessonNotFoundException;
import org.sleepless_artery.lesson_service.logging.annotation.BusinessEvent;
import org.sleepless_artery.lesson_service.logging.event.LogEvent;
import org.sleepless_artery.lesson_service.service.external.course.CourseExistenceChecker;
import org.sleepless_artery.lesson_service.service.infrastructure.kafka.event.LessonEventPublisher;
import org.sleepless_artery.lesson_service.mapper.LessonMapper;
import org.sleepless_artery.lesson_service.repository.LessonRepository;
import org.sleepless_artery.lesson_service.service.util.TransactionUtils;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;


/**
 * Lesson management service.
 * <p>
 * Provides operations for retrieving, creating, updating and deleting lessons,
 * manages cache invalidation and publishes lesson-related domain events.
 */
@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final LessonMapper lessonMapper;
    private final CacheManager cacheManager;
    private final CourseExistenceChecker courseExistenceChecker;
    private final LessonEventPublisher eventPublisher;

    private static final String LESSON_CACHE = "lesson:id";
    private static final String COURSE_LESSONS_CACHE = "lesson:course:id";


    /**
     * Checks whether a lesson exists by identifier.
     *
     * @param id lesson identifier
     * @return {@code true} if lesson exists, {@code false} otherwise
     */
    @Override
    public boolean existsById(Long id) {
        return lessonRepository.existsById(id);
    }


    /**
     * Returns lesson content by identifier.
     *
     * @param lessonId lesson identifier
     * @return lesson content data
     * @throws LessonNotFoundException if lesson does not exist
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            cacheNames = LESSON_CACHE,
            key = "#lessonId"
    )
    public LessonContentDto getLessonById(Long lessonId) {
        return lessonMapper.toContentDto(
                lessonRepository.findById(lessonId)
                        .orElseThrow(() ->
                                new LessonNotFoundException("Lesson not found with ID: " + lessonId)
                        )
        );
    }


    /**
     * Returns paginated list of lessons belonging to the specified course.
     *
     * @param courseId course identifier
     * @param pageable pagination information
     * @return page of lessons ordered by sequence number
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            cacheNames = COURSE_LESSONS_CACHE,
            key = "#courseId"
    )
    public Page<LessonInfoDto> getLessonsByCourseId(Long courseId, Pageable pageable) {
        return lessonRepository
                .findAllByCourseIdOrderBySequenceNumber(courseId, pageable)
                .map(lessonMapper::toInfoDto);
    }


    /**
     * Creates a new lesson.
     * <p>
     * Validates course existence and ensures that both lesson title
     * and sequence number are unique within the course.
     * <p>
     * After successful transaction commit, publishes a lesson update event
     * to notify dependent services.
     *
     * @param lessonRequestDto lesson creation request
     * @return created lesson content
     * @throws CourseDoesNotExistException if course does not exist
     * @throws LessonAlreadyExistsException if lesson with the same title or sequence already exists
     * @throws ExternalServiceUnavailableException if course service is unavailable
     */
    @Override
    @BusinessEvent(LogEvent.LESSON_CREATION)
    @Transactional
    @CacheEvict(
            cacheNames = COURSE_LESSONS_CACHE,
            key = "#lessonRequestDto.courseId"
    )
    public LessonContentDto createLesson(LessonRequestDto lessonRequestDto) {
        var courseId = lessonRequestDto.getCourseId();

        switch (courseExistenceChecker.verifyCourseExistence(courseId)) {
            case NOT_FOUND ->
                    throw new CourseDoesNotExistException("Course not found with ID: " + courseId);

            case SERVICE_UNAVAILABLE ->
                    throw new ExternalServiceUnavailableException("Course service is unavailable");
        }

        var title = lessonRequestDto.getTitle();
        var sequenceNumber = lessonRequestDto.getSequenceNumber();

        if (lessonRepository.existsByCourseIdAndTitle(courseId, title)) {
            throw new LessonAlreadyExistsException("Lesson '" + title + "' already exists");
        }

        if (lessonRepository.existsByCourseIdAndSequenceNumber(courseId, sequenceNumber)) {
            throw new LessonAlreadyExistsException(
                    "Lesson with sequence number '" + sequenceNumber + "' already exists"
            );
        }

        var lesson = lessonRepository.save(
                lessonMapper.toEntity(lessonRequestDto)
        );

        var dto = lessonMapper.toContentDto(lesson);

        TransactionUtils.runAfterCommit(() ->
                eventPublisher.publishLessonUpdatedEvent(courseId)
        );

        return dto;
    }


    /**
     * Updates an existing lesson.
     * <p>
     * Validates that the lesson belongs to the specified course and
     * ensures uniqueness of lesson title and sequence number within the course.
     * <p>
     * After successful transaction commit, publishes a lesson update event.
     *
     * @param lessonId lesson identifier
     * @param lessonRequestDto updated lesson data
     * @return updated lesson content
     * @throws LessonNotFoundException if lesson does not exist
     * @throws CourseDoesNotExistException if course identifier does not match the original one
     * @throws LessonAlreadyExistsException if another lesson with the same title or sequence exists
     */
    @Override
    @BusinessEvent(LogEvent.LESSON_UPDATE)
    @Transactional
    @Caching(
            put = {
                    @CachePut(cacheNames = LESSON_CACHE, key = "#lessonId")
            },
            evict = {
                    @CacheEvict(cacheNames = COURSE_LESSONS_CACHE, key = "#lessonRequestDto.courseId")
            }
    )
    public LessonContentDto updateLesson(Long lessonId, LessonRequestDto lessonRequestDto) {
        var lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new LessonNotFoundException("Lesson not found with ID: " + lessonId));

        var courseId = lesson.getCourseId();

        if (!courseId.equals(lessonRequestDto.getCourseId())) {
            throw new CourseDoesNotExistException("Course ID does not match the source one");
        }

        var newTitle = lessonRequestDto.getTitle();
        var order = lessonRequestDto.getSequenceNumber();

        if (!lesson.getTitle().equals(newTitle)) {
            if (lessonRepository.existsByCourseIdAndTitle(courseId, newTitle)) {
                throw new LessonAlreadyExistsException("Lesson '" + newTitle + "' already exists");
            }
            lesson.setTitle(newTitle);
        }

        if (!Objects.equals(lesson.getSequenceNumber(), order)) {
            if (lessonRepository.existsByCourseIdAndSequenceNumber(courseId, order)) {
                throw new LessonAlreadyExistsException(
                        "Lesson with sequence number '" + order + "' already exists"
                );
            }
            lesson.setSequenceNumber(order);
        }

        if (!lesson.getDescription().equals(lessonRequestDto.getDescription())) {
            lesson.setDescription(lessonRequestDto.getDescription());
        }

        if (!lesson.getContent().equals(lessonRequestDto.getContent())) {
            lesson.setContent(lessonRequestDto.getContent());
        }

        var updatedLesson = lessonRepository.save(lesson);
        var dto = lessonMapper.toContentDto(updatedLesson);

        TransactionUtils.runAfterCommit(() ->
                eventPublisher.publishLessonUpdatedEvent(courseId)
        );

        return dto;
    }


    /**
     * Deletes a lesson by identifier.
     * <p>
     * Evicts corresponding cache entries and publishes lesson deletion
     * and lesson update events after successful transaction commit.
     *
     * @param lessonId lesson identifier
     * @throws LessonNotFoundException if lesson does not exist
     */
    @Override
    @BusinessEvent(LogEvent.LESSON_UPDATE)
    @Transactional
    public void deleteLesson(Long lessonId) {
        var lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new LessonNotFoundException("Lesson not found with ID: " + lessonId));

        var courseId = lesson.getCourseId();
        lessonRepository.delete(lesson);

        var lessonCache = cacheManager.getCache(LESSON_CACHE);
        var courseLessonsCache = cacheManager.getCache(COURSE_LESSONS_CACHE);

        if (lessonCache != null) {
            lessonCache.evict(lessonId);
        }

        if (courseLessonsCache != null) {
            courseLessonsCache.evict(courseId);
        }

        TransactionUtils.runAfterCommit(() -> {
            eventPublisher.publishLessonDeletedEvent(lessonId);
            eventPublisher.publishLessonUpdatedEvent(courseId);
        });
    }


    /**
     * Deletes all lessons belonging to the specified course.
     * <p>
     * Performs batch deletion and clears related cache entries.
     * After transaction commit, publishes deletion events for each lesson
     * and a lesson update event for the course.
     *
     * @param courseId course identifier
     */
    @Override
    @BusinessEvent(LogEvent.LESSON_DELETION_BY_COURSE_ID)
    @Transactional
    @CacheEvict(cacheNames = COURSE_LESSONS_CACHE, key = "#courseId")
    public void deleteLessonsByCourseId(Long courseId) {
        var lessonIds = lessonRepository.findAllLessonIdsByCourseId(courseId);

        if (lessonIds.isEmpty()) {
            return;
        }
        lessonRepository.deleteAllByIdInBatch(lessonIds);

        var lessonCache = cacheManager.getCache(LESSON_CACHE);
        if (lessonCache != null) {
            lessonIds.forEach(lessonCache::evict);
        }

        TransactionUtils.runAfterCommit(() -> {
            lessonIds.forEach(eventPublisher::publishLessonDeletedEvent);
            eventPublisher.publishLessonUpdatedEvent(courseId);
        });
    }
}