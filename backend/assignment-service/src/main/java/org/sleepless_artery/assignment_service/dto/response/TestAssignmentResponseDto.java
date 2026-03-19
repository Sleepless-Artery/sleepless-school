package org.sleepless_artery.assignment_service.dto.response;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


/**
 * Response DTO representing a test-based assignment.
 */
@Getter @Setter
@NoArgsConstructor
@JsonTypeName("TEST")
public class TestAssignmentResponseDto extends AssignmentResponseDto {

    private String condition;

    private List<String> options;

    private List<Integer> correctOptionsIndices;
}

