package org.sleepless_artery.user_service.service.infrastructure.grpc.server;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

import net.devh.boot.grpc.server.service.GrpcService;
import org.sleepless_artery.user_service.CreateUserRequest;
import org.sleepless_artery.user_service.CreateUserResponse;
import org.sleepless_artery.user_service.UserCreationServiceGrpc;
import org.sleepless_artery.user_service.dto.UserRequestDto;
import org.sleepless_artery.user_service.exception.EmailAddressAlreadyExistsException;
import org.sleepless_artery.user_service.service.core.UserService;


/**
 * gRPC service for user creation.
 * <p>
 * Delegates user creation to application service.
 */
@GrpcService
@RequiredArgsConstructor
public class UserCreationServiceImpl extends UserCreationServiceGrpc.UserCreationServiceImplBase {

    private final UserService userService;


    /**
     * Creates a new user.
     *
     * @param request gRPC user creation request
     * @param responseStreamObserver response observer
     */
    @Override
    public void createUser(CreateUserRequest request, StreamObserver<CreateUserResponse> responseStreamObserver) {
        try {
            userService.createUser(
                    new UserRequestDto(request.getEmailAddress(), "User", "")
            );

            responseStreamObserver.onNext(CreateUserResponse.newBuilder()
                    .setSuccess(true)
                    .build()
            );

            responseStreamObserver.onCompleted();

        } catch (EmailAddressAlreadyExistsException e) {
            responseStreamObserver.onNext(CreateUserResponse.newBuilder()
                    .setSuccess(false)
                    .build()
            );
            
            responseStreamObserver.onCompleted();

        } catch (Exception e) {
            responseStreamObserver.onError(Status.INTERNAL
                    .withDescription("Error creating user")
                    .withCause(e)
                    .asRuntimeException()
            );
        }
    }
}
