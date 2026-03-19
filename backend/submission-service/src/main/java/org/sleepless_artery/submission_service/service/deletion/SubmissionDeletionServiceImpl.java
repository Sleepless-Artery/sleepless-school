package org.sleepless_artery.submission_service.service.deletion;

import lombok.RequiredArgsConstructor;
import org.sleepless_artery.submission_service.config.cache.CacheConfig;
import org.sleepless_artery.submission_service.logging.annotations.BusinessEvent;
import org.sleepless_artery.submission_service.logging.event.LogEvent;
import org.sleepless_artery.submission_service.repository.core.SubmissionRepository;
import org.sleepless_artery.submission_service.repository.file.FileSubmissionRepository;
import org.sleepless_artery.submission_service.service.infrastructure.minio.MinioService;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Service for bulk submission deletion.
 * <p>
 * Handles cascading deletion of submissions, file cleanup from MinIO,
 * and cache invalidation for consistency.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class SubmissionDeletionServiceImpl implements SubmissionDeletionService {

    private final SubmissionRepository submissionRepository;
    private final FileSubmissionRepository fileSubmissionRepository;
    private final MinioService minioService;
    private final CacheManager cacheManager;


    /**
     * Deletes all submissions for an assignment with full cleanup.
     * <p>
     * Transactional operation that removes database records, deletes files from MinIO,
     * and evicts submissions cache.
     * </p>
     *
     * @param assignmentId the assignment ID
     */
    @Override
    @BusinessEvent(LogEvent.SUBMISSION_DELETION_BY_ASSIGNMENT_ID)
    @Transactional
    public void deleteAllByAssignmentId(Long assignmentId) {
        var fileKeys = fileSubmissionRepository.findFileKeysByAssignmentId(assignmentId);
        var submissionIds = submissionRepository.findIdsByAssignmentId(assignmentId);

        submissionRepository.deleteAllByAssignmentId(assignmentId);
        minioService.removeAll(fileKeys);

        var assignmentCache = cacheManager.getCache(CacheConfig.ASSIGNMENT_CACHE_NAME);
        var submissionCache = cacheManager.getCache(CacheConfig.SUBMISSION_CACHE_NAME);

        if (assignmentCache != null) {
            assignmentCache.evict(assignmentId);
        }

        if (submissionCache != null) {
            submissionIds.forEach(submissionCache::evict);
        }
    }


    /**
     * Deletes all submissions of the student with full cleanup.
     * <p>
     * Transactional operation that removes database records, deletes files from MinIO,
     * and evicts submissions cache.
     * </p>
     *
     * @param studentId the student ID
     */
    @Override
    @BusinessEvent(LogEvent.SUBMISSION_DELETION_BY_STUDENT_ID)
    @Transactional
    public void deleteAllByStudentId(Long studentId) {
        var fileKeys = fileSubmissionRepository.findFileKeysByStudentId(studentId);
        var submissionIds = submissionRepository.findIdsByStudentId(studentId);

        submissionRepository.deleteAllByStudentId(studentId);
        minioService.removeAll(fileKeys);

        var submissionCache = cacheManager.getCache(CacheConfig.SUBMISSION_CACHE_NAME);
        if (submissionCache != null) {
            submissionIds.forEach(submissionCache::evict);
        }
    }
}
