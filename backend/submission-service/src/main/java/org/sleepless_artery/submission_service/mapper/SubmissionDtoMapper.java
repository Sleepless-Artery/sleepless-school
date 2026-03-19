package org.sleepless_artery.submission_service.mapper;

import org.hibernate.Hibernate;
import org.sleepless_artery.submission_service.dto.response.SubmissionResponseDto;
import org.sleepless_artery.submission_service.model.base.Submission;
import org.sleepless_artery.submission_service.model.file.FileSubmission;
import org.sleepless_artery.submission_service.model.test.TestSubmission;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;


/**
 * Mapper responsible for converting {@link Submission} entities
 * to their corresponding {@link SubmissionResponseDto} representations.
 *
 * <p>Uses a type-based mapping strategy to delegate conversion
 * to specific mappers depending on the concrete submission subtype.</p>
 */
@Component
public class SubmissionDtoMapper {

    private final Map<Class<? extends Submission>, Function<Submission, SubmissionResponseDto>> mappers;

    public SubmissionDtoMapper(FileSubmissionMapper fileSubmissionMapper, TestSubmissionMapper testSubmissionMapper) {
        this.mappers = Map.of(
                FileSubmission.class, submission -> fileSubmissionMapper.toDto((FileSubmission) submission),
                TestSubmission.class, submission -> testSubmissionMapper.toDto((TestSubmission) submission)
        );
    }


    /**
     * Converts an {@link Submission} entity to its corresponding response DTO.
     *
     * @param submission submission entity
     * @return submission response DTO
     * @throws IllegalStateException if no mapper is registered for the submission type
     */
    public SubmissionResponseDto toDto(Submission submission) {
        var mapper = mappers.get(Hibernate.getClass(submission));

        if (mapper == null) {
            throw new IllegalStateException("No mapper for " + submission.getClass());
        }
        return mapper.apply(submission);
    }
}
