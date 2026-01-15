package presentation.rest;

import domain.entity.ApplicationLog;
import application.usecase.QueryApplicationLogsUseCase;
import share.enums.LogLevel;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST API for querying application logs
 */
@Path("/api/logs/application")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ApplicationLogResource {

    @Inject
    QueryApplicationLogsUseCase queryUseCase;

    @GET
    public Uni<List<ApplicationLog>> getAllLogs(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("50") int size) {
        return queryUseCase.getAllLogs(page, size);
    }

    @GET
    @Path("/level/{level}")
    public Uni<List<ApplicationLog>> getLogsByLevel(
            @PathParam("level") LogLevel level,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("50") int size) {
        return queryUseCase.getLogsByLevel(level, page, size);
    }

    @GET
    @Path("/service/{serviceName}")
    public Uni<List<ApplicationLog>> getLogsByService(
            @PathParam("serviceName") String serviceName,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("50") int size) {
        return queryUseCase.getLogsByService(serviceName, page, size);
    }

    @GET
    @Path("/user/{userId}")
    public Uni<List<ApplicationLog>> getLogsByUser(
            @PathParam("userId") String userId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("50") int size) {
        return queryUseCase.getLogsByUser(userId, page, size);
    }

    @GET
    @Path("/correlation/{correlationId}")
    public Uni<List<ApplicationLog>> getLogsByCorrelationId(
            @PathParam("correlationId") String correlationId) {
        return queryUseCase.getLogsByCorrelationId(correlationId);
    }

    @GET
    @Path("/search")
    public Uni<List<ApplicationLog>> searchLogs(
            @QueryParam("keyword") String keyword,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("50") int size) {
        return queryUseCase.searchLogs(keyword, page, size);
    }

    @GET
    @Path("/date-range")
    public Uni<List<ApplicationLog>> getLogsByDateRange(
            @QueryParam("from") String from,
            @QueryParam("to") String to,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("50") int size) {
        LocalDateTime fromDate = LocalDateTime.parse(from);
        LocalDateTime toDate = LocalDateTime.parse(to);
        return queryUseCase.getLogsByDateRange(fromDate, toDate, page, size);
    }

    @GET
    @Path("/stats/level/{level}")
    public Uni<Long> countByLevel(@PathParam("level") LogLevel level) {
        return queryUseCase.countByLevel(level);
    }

    @GET
    @Path("/stats/service/{serviceName}")
    public Uni<Long> countByService(@PathParam("serviceName") String serviceName) {
        return queryUseCase.countByService(serviceName);
    }
}
