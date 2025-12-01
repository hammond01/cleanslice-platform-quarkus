package presentation.rest;

import domain.entity.AuditLog;
import domain.entity.AuditType;
import application.usecase.QueryAuditLogsUseCase;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

/**
 * REST API for querying audit logs
 */
@Path("/api/audit")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuditResource {

    @Inject
    QueryAuditLogsUseCase queryAuditLogsUseCase;

    @GET
    public List<AuditLog> getAllLogs(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("50") int size) {
        return queryAuditLogsUseCase.getAllLogs(page, size);
    }

    @GET
    @Path("/type/{type}")
    public List<AuditLog> getLogsByType(
            @PathParam("type") AuditType type,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("50") int size) {
        return queryAuditLogsUseCase.getLogsByType(type, page, size);
    }

    @GET
    @Path("/user/{userId}")
    public List<AuditLog> getLogsByUser(
            @PathParam("userId") Long userId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("50") int size) {
        return queryAuditLogsUseCase.getLogsByUser(userId, page, size);
    }

    @GET
    @Path("/entity/{entityType}/{entityId}")
    public List<AuditLog> getLogsByEntity(
            @PathParam("entityType") String entityType,
            @PathParam("entityId") Long entityId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("50") int size) {
        return queryAuditLogsUseCase.getLogsByEntity(entityType, entityId, page, size);
    }

    @GET
    @Path("/service/{serviceName}")
    public List<AuditLog> getLogsByService(
            @PathParam("serviceName") String serviceName,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("50") int size) {
        return queryAuditLogsUseCase.getLogsByService(serviceName, page, size);
    }

    @GET
    @Path("/correlation/{correlationId}")
    public List<AuditLog> getLogsByCorrelationId(@PathParam("correlationId") String correlationId) {
        return queryAuditLogsUseCase.getLogsByCorrelationId(correlationId);
    }

    @GET
    @Path("/errors/recent")
    public List<AuditLog> getRecentErrors(
            @QueryParam("limit") @DefaultValue("20") int limit) {
        return queryAuditLogsUseCase.getRecentErrors(limit);
    }

    @GET
    @Path("/security/recent")
    public List<AuditLog> getRecentSecurityEvents(
            @QueryParam("limit") @DefaultValue("20") int limit) {
        return queryAuditLogsUseCase.getRecentSecurityEvents(limit);
    }

    @GET
    @Path("/stats/type/{type}")
    public long countByType(@PathParam("type") AuditType type) {
        return queryAuditLogsUseCase.countByType(type);
    }

    @GET
    @Path("/stats/user/{userId}")
    public long countByUser(@PathParam("userId") Long userId) {
        return queryAuditLogsUseCase.countByUser(userId);
    }
}
