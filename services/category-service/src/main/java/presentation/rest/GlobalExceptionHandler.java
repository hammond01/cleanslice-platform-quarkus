package presentation.rest;

import domain.exception.CategoryNotFoundException;
import io.quarkus.logging.Log;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import share.ApiResponse;

import java.util.UUID;

import org.hibernate.exception.ConstraintViolationException;

@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {
        String requestId = UUID.randomUUID().toString();
        ApiResponse<Void> errorResponse;

        if (exception instanceof CategoryNotFoundException) {
            errorResponse = ApiResponse.fail("NOT_FOUND", exception.getMessage(), requestId);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(errorResponse)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } else if (exception instanceof ConstraintViolationException) {
            errorResponse = ApiResponse.fail("VALIDATION_ERROR", exception.getMessage(), requestId);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorResponse)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } else if (exception instanceof IllegalArgumentException) {
            errorResponse = ApiResponse.fail("BAD_REQUEST", exception.getMessage(), requestId);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorResponse)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } else if (exception.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
            errorResponse = ApiResponse.fail("DATABASE_CONSTRAINT_VIOLATION", "Database constraint violation", requestId);
            return Response.status(Response.Status.CONFLICT)
                    .entity(errorResponse)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } else {
            Log.errorf(exception, "Unhandled exception: %s - %s", exception.getClass().getName(), exception.getMessage());
            errorResponse = ApiResponse.fail("INTERNAL_SERVER_ERROR", "An unexpected error occurred", requestId);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorResponse)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }
}
