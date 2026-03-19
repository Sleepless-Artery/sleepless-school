package org.sleepless_artery.assignment_service.service.deletion;

import lombok.RequiredArgsConstructor;
import org.sleepless_artery.assignment_service.config.cache.CacheConfig;
import org.sleepless_artery.assignment_service.logging.annotation.BusinessEvent;
import org.sleepless_artery.assignment_service.logging.event.LogEvent;
import org.sleepless_artery.assignment_service.repository.core.AssignmentRepository;
import org.sleepless_artery.assignment_service.repository.file.FileAssignmentRepository;
import org.sleepless_artery.assignment_service.service.infrastructure.minio.MinioService;
import org.springframework.cache.CacheManager;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Service for bulk assignment deletion.
 * <p>
 * Handles cascading deletion of assignments, file cleanup from MinIO,
 * and cache invalidation for consistency.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class AssignmentDeletionServiceImpl implements AssignmentDeletionService {

    private final AssignmentRepository assignmentRepository;
    private final FileAssignmentRepository fileAssignmentRepository;
    private final MinioService minioService;
    private final CacheManager cacheManager;


    /**
     * Deletes all assignments for a lesson with full cleanup.
     * <p>
     * Transactional operation that removes database records, deletes files from MinIO,
     * and evicts assignment cache.
     * </p>
     *
     * @param lessonId the lesson ID
     */
    @Override
    @BusinessEvent(LogEvent.ASSIGNMENT_DELETION_BY_LESSON_ID)
    @Transactional
    public void deleteAssignmentsByLessonId(Long lessonId) {

        var fileKeys = fileAssignmentRepository.findFileKeysByLessonId(lessonId);
        var assignmentIds = assignmentRepository.findIdsByLessonId(lessonId);

        assignmentRepository.deleteAllByLessonId(lessonId);
        minioService.removeAll(fileKeys);

        var assignmentCache = cacheManager.getCache(CacheConfig.ASSIGNMENT_CACHE_NAME);
        var lessonCache = cacheManager.getCache(CacheConfig.LESSON_CACHE_NAME);

        if (lessonCache != null) {
            lessonCache.evict(lessonId);
        }

        if (assignmentCache != null) {
            assignmentIds.forEach(assignmentCache::evict);
        }
    }
}
