package io.cleanslice.platform.controller;

import io.cleanslice.platform.domain.ErrorLog;
import io.cleanslice.platform.service.QueryErrorLogsUseCase;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST API for querying error logs
 */
@Path("/api/logs/error")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ErrorLogResource {

    @Inject
    QueryErrorLogsUseCase queryUseCase;

    @GET
    public Uni<List<ErrorLog>> getAllLogs(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("50") int size) {
        return queryUseCase.getAllLogs(page, size);
    }

    @GET
    @Path("/exception/{exceptionType}")
    public Uni<List<ErrorLog>> getLogsByExceptionType(
            @PathParam("exceptionType") String exceptionType,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("50") int size) {
        return queryUseCase.getLogsByExceptionType(exceptionType, page, size);
    }

    @GET
    @Path("/service/{serviceName}")
    public Uni<List<ErrorLog>> getLogsByService(
            @PathParam("serviceName") String serviceName,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("50") int size) {
        return queryUseCase.getLogsByService(serviceName, page, size);
    }

    @GET
    @Path("/user/{userId}")
    public Uni<List<ErrorLog>> getLogsByUser(
            @PathParam("userId") String userId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("50") int size) {
        return queryUseCase.getLogsByUser(userId, page, size);
    }

    @GET
    @Path("/http-method/{httpMethod}")
    public Uni<List<ErrorLog>> getLogsByHttpMethod(
            @PathParam("httpMethod") String httpMethod,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("50") int size) {
        return queryUseCase.getLogsByHttpMethod(httpMethod, page, size);
    }

    @GET
    @Path("/correlation/{correlationId}")
    public Uni<List<ErrorLog>> getLogsByCorrelationId(
            @PathParam("correlationId") String correlationId) {
        return queryUseCase.getLogsByCorrelationId(correlationId);
    }

    @GET
    @Path("/recent")
    public Uni<List<ErrorLog>> getRecentErrors(
            @QueryParam("limit") @DefaultValue("20") int limit) {
        return queryUseCase.getRecentErrors(limit);
    }

    @GET
    @Path("/search")
    public Uni<List<ErrorLog>> searchLogs(
            @QueryParam("keyword") String keyword,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("50") int size) {
        return queryUseCase.searchLogs(keyword, page, size);
    }

    @GET
    @Path("/date-range")
    public Uni<List<ErrorLog>> getLogsByDateRange(
            @QueryParam("from") String from,
            @QueryParam("to") String to,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("50") int size) {
        LocalDateTime fromDate = LocalDateTime.parse(from);
        LocalDateTime toDate = LocalDateTime.parse(to);
        return queryUseCase.getLogsByDateRange(fromDate, toDate, page, size);
    }

    @GET
    @Path("/stats/exception/{exceptionType}")
    public Uni<Long> countByExceptionType(@PathParam("exceptionType") String exceptionType) {
        return queryUseCase.countByExceptionType(exceptionType);
    }

    @GET
    @Path("/stats/service/{serviceName}")
    public Uni<Long> countByService(@PathParam("serviceName") String serviceName) {
        return queryUseCase.countByService(serviceName);
    }

    @GET
    @Path("/stats/http-method/{httpMethod}")
    public Uni<Long> countByHttpMethod(@PathParam("httpMethod") String httpMethod) {
        return queryUseCase.countByHttpMethod(httpMethod);
    }
}
