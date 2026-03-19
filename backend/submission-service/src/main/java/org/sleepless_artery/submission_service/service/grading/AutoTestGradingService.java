package org.sleepless_artery.submission_service.service.grading;

import lombok.RequiredArgsConstructor;
import org.sleepless_artery.submission_service.dto.request.TestSubmissionRequestDto;
import org.sleepless_artery.submission_service.exception.ExternalServiceUnavailableException;
import org.sleepless_artery.submission_service.service.external.assignment.FetchAssignmentDataService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Default implementation of {@link TestGradingService}.
 * <p>
 * Performs automatic grading of test submissions.
 */
@Service
@RequiredArgsConstructor
public class AutoTestGradingService implements TestGradingService {

    private final FetchAssignmentDataService fetchAssignmentDataService;


    /**
     * Calculates the score for a test submission.
     *
     * @param requestDto the test submission request
     * @return calculated score
     * @throws ExternalServiceUnavailableException if assignment service is unavailable
     * @throws RuntimeException if an unexpected error occurs
     */
    @Override
    public double evaluate(TestSubmissionRequestDto requestDto) {
        var assignmentId = requestDto.getAssignmentId();

        List<Integer> correctOptionsIndices;
        double maxScore;

        try {
            correctOptionsIndices = fetchAssignmentDataService.getCorrectOptionsIndices(assignmentId);
            maxScore = fetchAssignmentDataService.getMaxScore(assignmentId);
        } catch (ExternalServiceUnavailableException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error", e);
        }

        Set<Integer> correctSet = new HashSet<>(correctOptionsIndices);
        Set<Integer> selectedSet = new HashSet<>(requestDto.getSelectedOptionsIndices());

        var correctChosen = selectedSet.stream()
                .filter(correctSet::contains)
                .count();

        var missedCorrect = correctSet.stream()
                .filter(i -> !selectedSet.contains(i))
                .count();

        var wrongChosen = selectedSet.stream()
                .filter(i -> !correctSet.contains(i))
                .count();

        var percent = (double) (correctChosen - (missedCorrect + wrongChosen)) / correctOptionsIndices.size();
        return Math.max(Math.round(percent * maxScore * 100) / 100.0, 0);
    }
}
