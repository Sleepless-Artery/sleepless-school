package org.sleepless_artery.assignment_service.service.query;

import lombok.RequiredArgsConstructor;
import org.sleepless_artery.assignment_service.config.cache.CacheConfig;
import org.sleepless_artery.assignment_service.dto.response.AssignmentResponseDto;
import org.sleepless_artery.assignment_service.exception.AssignmentNotFoundException;
import org.sleepless_artery.assignment_service.mapper.AssignmentDtoMapper;
import org.sleepless_artery.assignment_service.repository.core.AssignmentRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Service for assignment read operations.
 * <p>
 * Handles query operations with caching strategies for optimal read performance.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class AssignmentQueryServiceImpl implements AssignmentQueryService {

    private final AssignmentRepository assignmentRepository;
    private final AssignmentDtoMapper assignmentDtoMapper;


    /**
     * Retrieves assignment by ID with caching.
     *
     * @param id the assignment ID
     * @return assignment data transfer object
     *
     * @throws AssignmentNotFoundException if assignment does not exist
     */
    @Override
    @Cacheable(cacheNames = CacheConfig.ASSIGNMENT_CACHE_NAME, key = "#id")
    public AssignmentResponseDto findById(Long id) {
        return assignmentDtoMapper.toDto(
                assignmentRepository.findById(id)
                        .orElseThrow(
                                () -> new AssignmentNotFoundException("Assignment not found with ID: " + id)
                        )
        );
    }


    /**
     * Retrieves all assignments by lesson ID with caching.
     *
     * @param lessonId the lesson ID
     * @return list of assignment data transfer objects
     */
    @Override
    @Cacheable(cacheNames = CacheConfig.LESSON_CACHE_NAME, key = "#lessonId")
    public List<AssignmentResponseDto> findAllByLessonId(Long lessonId) {
        return assignmentRepository.findAllByLessonId(lessonId).stream()
                .map(assignmentDtoMapper::toDto).toList();
    }


    /**
     * Checks assignment existence.
     *
     * @param id the assignment ID
     * @return true if assignment exists, false otherwise
     */
    @Override
    public boolean existsById(Long id) {
        return assignmentRepository.existsById(id);
    }
}