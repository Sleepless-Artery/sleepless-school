package org.sleepless_artery.course_service.service.core;

import lombok.RequiredArgsConstructor;
import org.sleepless_artery.course_service.dto.CourseRequestDto;
import org.sleepless_artery.course_service.dto.CourseResponseDto;
import org.sleepless_artery.course_service.exception.AuthorDoesNotExistException;
import org.sleepless_artery.course_service.exception.CourseAlreadyExistsException;
import org.sleepless_artery.course_service.exception.CourseNotFoundException;
import org.sleepless_artery.course_service.exception.ExternalServiceUnavailableException;
import org.sleepless_artery.course_service.logging.annotation.BusinessEvent;
import org.sleepless_artery.course_service.logging.event.LogEvent;
import org.sleepless_artery.course_service.service.external.user.UserExistenceChecker;
import org.sleepless_artery.course_service.service.infrastructure.kafka.event.CourseEventPublisher;
import org.sleepless_artery.course_service.mapper.CourseMapper;
import org.sleepless_artery.course_service.repository.CourseRepository;
import org.sleepless_artery.course_service.repository.specifications.CourseSpecifications;
import org.sleepless_artery.course_service.service.util.TransactionUtils;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


/**
 * Course management service.
 * <p>
 * Provides CRUD operations for courses, performs validation,
 * manages cache and publishes domain events.
 */
@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private static final String COURSE_ID_CACHE = "course:id";

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final UserExistenceChecker userExistenceChecker;
    private final CourseEventPublisher eventPublisher;
    private final CacheManager cacheManager;


    /**
     * Checks whether a course exists by identifier.
     *
     * @param id course identifier
     * @return {@code true} if course exists, {@code false} otherwise
     */
    @Override
    public boolean existsById(Long id) {
        return courseRepository.existsById(id);
    }


    /**
     * Returns course by identifier.
     *
     * @param id course identifier
     * @return course data
     * @throws CourseNotFoundException if course does not exist
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            cacheNames = COURSE_ID_CACHE,
            key = "#id",
            condition = "#id != null"
    )
    public CourseResponseDto findById(Long id) {
        return courseMapper.toDto(
                courseRepository.findById(id)
                        .orElseThrow(() ->
                                new CourseNotFoundException("Course not found with ID " + id))
        );
    }


    /**
     * Searches courses using optional filtering criteria.
     *
     * @param title        course title filter
     * @param authorId     author identifier filter
     * @param description  course description filter
     * @param startingDate lower bound of update date
     * @param endingDate   upper bound of update date
     * @param pageable     pagination information
     * @return page of matching courses
     */
    @Override
    @Transactional(readOnly = true)
    public Page<CourseResponseDto> findCourses(
            String title,
            Long authorId,
            String description,
            LocalDate startingDate,
            LocalDate endingDate,
            Pageable pageable
    ) {
        return courseRepository.findAll(
                Specification.where(CourseSpecifications.titleLike(title))
                        .and(CourseSpecifications.hasAuthorId(authorId))
                        .and(CourseSpecifications.descriptionLike(description))
                        .and(CourseSpecifications.updatedAtBetween(startingDate, endingDate)),
                pageable
        ).map(courseMapper::toDto);
    }


    /**
     * Creates a new course.
     * <p>
     * Validates that the author exists and that a course with the same
     * title does not already exist for the author.
     *
     * @param dto course creation request
     * @return created course data
     * @throws CourseAlreadyExistsException if course with the same title already exists
     * @throws AuthorDoesNotExistException if author does not exist
     * @throws ExternalServiceUnavailableException if user service is unavailable
     */
    @Override
    @BusinessEvent(LogEvent.COURSE_CREATION)
    @Transactional
    public CourseResponseDto createCourse(CourseRequestDto dto) {
        var authorId = dto.getAuthorId();

        if (courseRepository.existsByAuthorIdAndTitleIgnoreCase(authorId, dto.getTitle())) {
            throw new CourseAlreadyExistsException(
                    "Course already exists with title `" + dto.getTitle() + "` for author with ID: " + authorId
            );
        }

        switch (userExistenceChecker.verifyUserExistence(authorId)) {
            case NOT_FOUND ->
                    throw new AuthorDoesNotExistException("Author not found with ID " + authorId);
            case SERVICE_UNAVAILABLE ->
                    throw new ExternalServiceUnavailableException("User service unavailable");
        }

        return courseMapper.toDto(
                courseRepository.save(courseMapper.toEntity(dto))
        );
    }


    /**
     * Updates an existing course.
     *
     * @param id  course identifier
     * @param dto updated course data
     * @return updated course data
     * @throws CourseNotFoundException if course does not exist
     * @throws CourseAlreadyExistsException if another course with the same title exists
     */
    @Override
    @BusinessEvent(LogEvent.COURSE_UPDATE)
    @Transactional
    @CacheEvict(
            cacheNames = COURSE_ID_CACHE,
            key = "#id",
            condition = "#id != null"
    )
    public CourseResponseDto updateCourse(Long id, CourseRequestDto dto) {
        var course = courseRepository.findById(id)
                .orElseThrow(() ->
                        new CourseNotFoundException("Course not found with ID " + id));

        var newTitle = dto.getTitle();

        if (!newTitle.equals(course.getTitle())) {
            if (courseRepository.existsByAuthorIdAndTitleIgnoreCase(
                    dto.getAuthorId(), newTitle
            )) {
                throw new CourseAlreadyExistsException(
                        "Course already exists with title `" + newTitle + "` for author with ID: " + dto.getAuthorId()
                );
            }
            course.setTitle(newTitle);
        }

        if (!dto.getDescription().equals(course.getDescription())) {
            course.setDescription(dto.getDescription());
        }

        course.setLastUpdateDate(LocalDate.now());

        return courseMapper.toDto(courseRepository.save(course));
    }


    /**
     * Updates the last update date of a course.
     * <p>
     * This method is triggered by an asynchronous event from the lesson service
     * when course-related content is modified.
     *
     * @param id course identifier
     * @throws CourseNotFoundException if course does not exist
     */
    @Override
    @BusinessEvent(LogEvent.COURSE_UPDATE)
    @Transactional
    @CacheEvict(
            cacheNames = COURSE_ID_CACHE,
            key = "#id",
            condition = "#id != null"
    )
    public void updateCourseLastUpdateDate(Long id) {
        var course = courseRepository.findById(id)
                .orElseThrow(() ->
                        new CourseNotFoundException("Course not found with ID " + id));
        course.setLastUpdateDate(LocalDate.now());
        courseRepository.save(course);
    }


    /**
     * Deletes a course by identifier.
     * <p>
     * After successful transaction commit, cache is evicted and
     * a course deletion event is published.
     *
     * @param id course identifier
     * @throws CourseNotFoundException if course does not exist
     */
    @Override
    @BusinessEvent(LogEvent.COURSE_DELETION)
    @Transactional
    @CacheEvict(
            cacheNames = COURSE_ID_CACHE,
            key = "#id",
            condition = "#id != null"
    )
    public void deleteCourse(Long id) {
        var course = courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException("Course not found with ID " + id));

        courseRepository.delete(course);

        TransactionUtils.runAfterCommit(() ->
                eventPublisher.publishCourseDeletedEvent(id)
        );
    }


    /**
     * Deletes all courses created by the specified author.
     * <p>
     * Performs bulk deletion and clears corresponding cache entries
     * after transaction commit. A deletion event is published for
     * each removed course.
     *
     * @param authorId author identifier
     */
    @Override
    @BusinessEvent(LogEvent.COURSE_DELETION_BY_AUTHOR_ID)
    @Transactional
    public void deleteCoursesByAuthorId(Long authorId) {
        List<Long> courseIds = courseRepository.findIdsByAuthorId(authorId);

        if (courseIds.isEmpty()) {
            return;
        }

        courseRepository.deleteByAuthorId(authorId);

        TransactionUtils.runAfterCommit(() -> {
            var cache = cacheManager.getCache(COURSE_ID_CACHE);

            for (var id : courseIds) {
                if (cache != null) {
                    cache.evict(id);
                }
                eventPublisher.publishCourseDeletedEvent(id);
            }
        });
    }
}