package org.sleepless_artery.submission_service.service.external.assignment;

import java.util.List;

/**
 * Service responsible for fetching assignment data.
 */
public interface FetchAssignmentDataService {

    List<Integer> getCorrectOptionsIndices(Long assignmentId);

    Double getMaxScore(Long assignmentId);
}
