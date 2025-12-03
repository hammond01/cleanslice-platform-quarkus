package infrastructure.web;

import jakarta.ws.rs.container.*;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.MDC;

import java.io.IOException;
import java.util.UUID;

@Provider
public class RequestIdFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final String HEADER = "x-request-id";

    @Override
    public void filter(ContainerRequestContext requestContext)
            throws IOException {
        String requestId = requestContext.getHeaderString(HEADER);

        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }

        MDC.put("requestId", requestId);

        requestContext.setProperty("requestId", requestId);
    }

    @Override
    public void filter(
            ContainerRequestContext requestContext,
            ContainerResponseContext responseContext
    ) throws IOException {

        String requestId = (String) requestContext.getProperty("requestId");
        responseContext.getHeaders().add(HEADER, requestId);

        MDC.remove("requestId");
    }
}
