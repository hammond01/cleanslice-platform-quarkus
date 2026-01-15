package presentation.rest;

import domain.entity.AccessLog;
import application.usecase.QueryAccessLogsUseCase;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST API for querying access logs (HTTP requests/responses)
 */
@Path("/api/logs/access")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccessLogResource {

    @Inject
    QueryAccessLogsUseCase queryUseCase;

    @GET
    public Uni<List<AccessLog>> getAllLogs(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("50") int size) {
        return queryUseCase.getAllLogs(page, size);
    }

    @GET
    @Path("/method/{httpMethod}")
    public Uni<List<AccessLog>> getLogsByMethod(
            @PathParam("httpMethod") String httpMethod,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("50") int size) {
        return queryUseCase.getLogsByMethod(httpMethod, page, size);
    }

    @GET
    @Path("/endpoint")
    public Uni<List<AccessLog>> getLogsByEndpoint(
            @QueryParam("path") String endpoint,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("50") int size) {
        return queryUseCase.getLogsByEndpoint(endpoint, page, size);
    }

    @GET
    @Path("/status/{statusCode}")
    public Uni<List<AccessLog>> getLogsByStatusCode(
            @PathParam("statusCode") Integer statusCode,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("50") int size) {
        return queryUseCase.getLogsByStatusCode(statusCode, page, size);
    }

    @GET
    @Path("/service/{serviceName}")
    public Uni<List<AccessLog>> getLogsByService(
            @PathParam("serviceName") String serviceName,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("50") int size) {
        return queryUseCase.getLogsByService(serviceName, page, size);
    }

    @GET
    @Path("/user/{userId}")
    public Uni<List<AccessLog>> getLogsByUser(
            @PathParam("userId") String userId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("50") int size) {
        return queryUseCase.getLogsByUser(userId, page, size);
    }

    @GET
    @Path("/correlation/{correlationId}")
    public Uni<List<AccessLog>> getLogsByCorrelationId(
            @PathParam("correlationId") String correlationId) {
        return queryUseCase.getLogsByCorrelationId(correlationId);
    }

    @GET
    @Path("/slow")
    public Uni<List<AccessLog>> getSlowRequests(
            @QueryParam("minMs") @DefaultValue("1000") Long minResponseTime,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("50") int size) {
        return queryUseCase.getSlowRequests(minResponseTime, page, size);
    }

    @GET
    @Path("/errors")
    public Uni<List<AccessLog>> getErrorResponses(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("50") int size) {
        return queryUseCase.getErrorResponses(page, size);
    }

    @GET
    @Path("/date-range")
    public Uni<List<AccessLog>> getLogsByDateRange(
            @QueryParam("from") String from,
            @QueryParam("to") String to,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("50") int size) {
        LocalDateTime fromDate = LocalDateTime.parse(from);
        LocalDateTime toDate = LocalDateTime.parse(to);
        return queryUseCase.getLogsByDateRange(fromDate, toDate, page, size);
    }

    @GET
    @Path("/stats/status/{statusCode}")
    public Uni<Long> countByStatusCode(@PathParam("statusCode") Integer statusCode) {
        return queryUseCase.countByStatusCode(statusCode);
    }

    @GET
    @Path("/stats/method/{httpMethod}")
    public Uni<Long> countByMethod(@PathParam("httpMethod") String httpMethod) {
        return queryUseCase.countByMethod(httpMethod);
    }

    @GET
    @Path("/stats/slow")
    public Uni<Long> countSlowRequests(
            @QueryParam("minMs") @DefaultValue("1000") Long minResponseTime) {
        return queryUseCase.countSlowRequests(minResponseTime);
    }
}
