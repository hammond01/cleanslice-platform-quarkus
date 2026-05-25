package io.cleanslice.platform.common.exception;

import java.util.UUID;

import io.cleanslice.platform.common.response.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {
        String requestId = UUID.randomUUID().toString();
        ApiResponse<Void> errorResponse;

        switch (exception) {
            case final ResourceNotFoundException resourceNotFoundException -> {
                errorResponse = ApiResponse.fail("NOT_FOUND", resourceNotFoundException.getMessage(), requestId);
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(errorResponse)
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }
            case final ConstraintViolationException constraintViolationException -> {
                errorResponse = ApiResponse.fail("VALIDATION_ERROR", constraintViolationException.getMessage(),
                        requestId);
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorResponse)
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }
            case final IllegalArgumentException illegalArgumentException -> {
                errorResponse = ApiResponse.fail("BAD_REQUEST", illegalArgumentException.getMessage(), requestId);
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorResponse)
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }
            case final WebApplicationException webApplicationException -> {
                errorResponse = ApiResponse.fail("HTTP_ERROR", webApplicationException.getMessage(), requestId);
                return Response.status(webApplicationException.getResponse().getStatusInfo())
                        .entity(errorResponse)
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }
            case Exception e -> {
                if (e.getCause() instanceof ConstraintViolationException cause) {
                    errorResponse = ApiResponse.fail(
                            "DATABASE_CONSTRAINT_VIOLATION",
                            cause.getMessage(),
                            requestId);

                    return Response.status(Response.Status.CONFLICT)
                            .entity(errorResponse)
                            .type(MediaType.APPLICATION_JSON)
                            .build();
                }

                GlobalExceptionLogger.logException(e, null, requestId);

                errorResponse = ApiResponse.fail(
                        "INTERNAL_SERVER_ERROR",
                        "An unexpected error occurred",
                        requestId);

                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(errorResponse)
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }
        }
    }
}
