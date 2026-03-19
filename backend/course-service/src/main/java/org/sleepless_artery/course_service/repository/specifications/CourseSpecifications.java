package org.sleepless_artery.course_service.repository.specifications;

import org.sleepless_artery.course_service.model.Course;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;


/**
 * Utility class containing {@link Specification} builders
 * used for dynamic filtering of {@link Course} entities.
 *
 * <p>Each method returns a specification that can be combined
 * with other specifications using {@code and()} / {@code or()}
 * when constructing complex queries.</p>
 */
public class CourseSpecifications {

    /**
     * Creates a specification that filters courses
     * whose title contains the provided pattern.
     *
     * @param pattern part of the course title to search for
     * @return specification filtering by title or a no-op specification
     * if the pattern is null or blank
     */
    public static Specification<Course> titleLike(String pattern) {
        return ((root, query, criteriaBuilder) ->
                pattern == null || pattern.isBlank()
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.like(root.get("title"), "%" + pattern + "%")
        );
    }


    /**
     * Creates a specification that filters courses by author identifier.
     *
     * @param authorId identifier of the course author
     * @return specification filtering by author id or a no-op specification
     * if the authorId is null
     */
    public static Specification<Course> hasAuthorId(Long authorId) {
        return ((root, query, criteriaBuilder) ->
                authorId == null
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.equal(root.get("authorId"), authorId)
        );
    }


    /**
     * Creates a specification that filters courses by the last update date range.
     *
     * <p>If one of the bounds is null, the range will be constructed
     * using a default value:</p>
     * <ul>
     *     <li>If {@code startingDate} is null — the range starts from {@link LocalDate#EPOCH}</li>
     *     <li>If {@code endingDate} is null — the range ends at {@link LocalDate#now()}</li>
     * </ul>
     *
     * @param startingDate lower bound of the update date range
     * @param endingDate upper bound of the update date range
     * @return specification filtering by update date range
     */
    public static Specification<Course> updatedAtBetween(LocalDate startingDate, LocalDate endingDate) {
        return ((root, query, criteriaBuilder) -> {
            if (startingDate == null && endingDate == null) {
                return criteriaBuilder.conjunction();
            }
            if (startingDate == null) {
                return criteriaBuilder.between(root.get("lastUpdateDate"), LocalDate.EPOCH, endingDate);
            }
            if (endingDate == null) {
                return criteriaBuilder.between(root.get("lastUpdateDate"), startingDate, LocalDate.now());
            }
            return criteriaBuilder.between(root.get("lastUpdateDate"), startingDate, endingDate);
        });
    }


    /**
     * Creates a specification that filters courses
     * whose description contains the provided pattern.
     *
     * @param pattern part of the description text to search for
     * @return specification filtering by description or a no-op specification
     * if the pattern is null or blank
     */
    public static Specification<Course> descriptionLike(String pattern) {
        return ((root, query, criteriaBuilder) ->
                pattern == null || pattern.isBlank()
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.like(root.get("description"), "%" + pattern + "%")
        );
    }
}