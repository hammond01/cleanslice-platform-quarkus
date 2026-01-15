package presentation.rest;

import domain.entity.PerformanceLog;
import application.usecase.QueryPerformanceLogsUseCase;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST API for querying performance logs (DB operations, timing)
 */
@Path("/api/logs/performance")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PerformanceLogResource {

    @Inject
    QueryPerformanceLogsUseCase queryUseCase;

    @GET
    public Uni<List<PerformanceLog>> getAllLogs(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("50") int size) {
        return queryUseCase.getAllLogs(page, size);
    }

    @GET
    @Path("/operation/{operation}")
    public Uni<List<PerformanceLog>> getLogsByOperation(
            @PathParam("operation") String operation,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("50") int size) {
        return queryUseCase.getLogsByOperation(operation, page, size);
    }

    @GET
    @Path("/service/{serviceName}")
    public Uni<List<PerformanceLog>> getLogsByService(
            @PathParam("serviceName") String serviceName,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("50") int size) {
        return queryUseCase.getLogsByService(serviceName, page, size);
    }

    @GET
    @Path("/slow")
    public Uni<List<PerformanceLog>> getSlowOperations(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("50") int size) {
        return queryUseCase.getSlowOperations(page, size);
    }

    @GET
    @Path("/min-duration/{minMs}")
    public Uni<List<PerformanceLog>> getLogsByMinDuration(
            @PathParam("minMs") Long minDuration,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("50") int size) {
        return queryUseCase.getLogsByMinDuration(minDuration, page, size);
    }

    @GET
    @Path("/correlation/{correlationId}")
    public Uni<List<PerformanceLog>> getLogsByCorrelationId(
            @PathParam("correlationId") String correlationId) {
        return queryUseCase.getLogsByCorrelationId(correlationId);
    }

    @GET
    @Path("/search")
    public Uni<List<PerformanceLog>> searchLogs(
            @QueryParam("keyword") String keyword,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("50") int size) {
        return queryUseCase.searchLogs(keyword, page, size);
    }

    @GET
    @Path("/date-range")
    public Uni<List<PerformanceLog>> getLogsByDateRange(
            @QueryParam("from") String from,
            @QueryParam("to") String to,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("50") int size) {
        LocalDateTime fromDate = LocalDateTime.parse(from);
        LocalDateTime toDate = LocalDateTime.parse(to);
        return queryUseCase.getLogsByDateRange(fromDate, toDate, page, size);
    }

    @GET
    @Path("/stats/slow")
    public Uni<Long> countSlowOperations() {
        return queryUseCase.countSlowOperations();
    }

    @GET
    @Path("/stats/service/{serviceName}")
    public Uni<Long> countByService(@PathParam("serviceName") String serviceName) {
        return queryUseCase.countByService(serviceName);
    }

    @GET
    @Path("/stats/average/{operation}")
    public Uni<Double> getAverageDuration(@PathParam("operation") String operation) {
        return queryUseCase.getAverageDuration(operation);
    }
}
