package org.sleepless_artery.assignment_service.service.infrastructure.grpc.server;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.sleepless_artery.assignment_service.*;
import org.sleepless_artery.assignment_service.dto.response.TestAssignmentResponseDto;
import org.sleepless_artery.assignment_service.exception.AssignmentNotFoundException;
import org.sleepless_artery.assignment_service.service.query.AssignmentQueryService;

import java.util.List;


/**
 * gRPC service for fetching assignment data.
 * <p>
 * Provides remote procedure calls for retrieving assignment-specific data
 * such as correct options indices and maximum scores.
 * </p>
 */
@GrpcService
@RequiredArgsConstructor
public class FetchAssignmentDataServiceImpl extends FetchAssignmentDataServiceGrpc.FetchAssignmentDataServiceImplBase {

    private final AssignmentQueryService queryService;


    /**
     * Retrieves correct option indices for a test assignment.
     *
     * @param request contains assignment ID
     * @param responseObserver returns list of correct option indices or error
     * @throws AssignmentNotFoundException if assignment is not a test assignment
     */
    @Override
    public void getCorrectOptionsIndices(
            GetCorrectOptionsIndicesRequest request,
            StreamObserver<GetCorrectOptionsIndicesResponse> responseObserver
    ) {
        try {
            var assignmentResponseDto = queryService.findById(request.getAssignmentId());
            List<Integer> correctOptionsIndices;

            if (assignmentResponseDto instanceof TestAssignmentResponseDto testAssignmentResponseDto) {
                correctOptionsIndices = testAssignmentResponseDto.getCorrectOptionsIndices();
            } else {
                throw new AssignmentNotFoundException("Test assignment not found with ID: " + request.getAssignmentId());
            }

            responseObserver.onNext(
                    GetCorrectOptionsIndicesResponse.newBuilder()
                            .addAllCorrectOptionsIndices(correctOptionsIndices)
                            .build()
            );

            responseObserver.onCompleted();
        } catch (AssignmentNotFoundException e) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .asRuntimeException()
            );
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error getting correct options indices")
                    .withCause(e)
                    .asRuntimeException()
            );
        }
    }


    /**
     * Retrieves maximum score for an assignment.
     *
     * @param request contains assignment ID
     * @param responseObserver returns maximum score value or error
     * @throws AssignmentNotFoundException if assignment does not exist
     */
    @Override
    public void getMaxScore(GetMaxScoreRequest request, StreamObserver<GetMaxScoreResponse> responseObserver) {
        try {
            responseObserver.onNext(
                    GetMaxScoreResponse.newBuilder()
                            .setMaxScore(
                                    queryService.findById(request.getAssignmentId()).getMaxScore()
                            )
                            .build()
            );

            responseObserver.onCompleted();
        } catch (AssignmentNotFoundException e) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .asRuntimeException()
            );
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error getting max score")
                    .withCause(e)
                    .asRuntimeException()
            );
        }
    }
}
