package presentation.rest;

import application.dto.GetCategoryDto;
import application.dto.CreateCategoryDto;
import application.dto.UpdateCategoryDto;
import application.service.CategoryService;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;
import share.ApiResponse;

import java.util.List;
import java.util.UUID;

@Path("/api/categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoryController {

    @Inject
    CategoryService categoryService;

    @GET
    public Uni<ApiResponse<List<GetCategoryDto>>> getAllCategories(@Context UriInfo uriInfo) {
        String requestId = UUID.randomUUID().toString();
        return categoryService.getAllCategories()
                .onItem().transform(categories -> ApiResponse.ok(categories, requestId))
                .onFailure().recoverWithItem(ex -> 
                        ApiResponse.fail("INTERNAL_ERROR", ex.getMessage(), requestId));
    }

    @GET
    @Path("/{number}")
    public Uni<ApiResponse<GetCategoryDto>> getCategoryById(@PathParam("number") String number, @Context UriInfo uriInfo) {
        String requestId = UUID.randomUUID().toString();
        return categoryService.getCategoryById(number)
                .onItem().transform(category -> ApiResponse.ok(category, requestId))
                .onFailure().recoverWithItem(ex -> {
                    if (ex instanceof domain.exception.CategoryNotFoundException) {
                        return ApiResponse.fail("NOT_FOUND", ex.getMessage(), requestId);
                    }
                    return ApiResponse.fail("INTERNAL_ERROR", ex.getMessage(), requestId);
                });
    }

    @POST
    public Uni<ApiResponse<GetCategoryDto>> createCategory(CreateCategoryDto dto, @Context UriInfo uriInfo) {
        String requestId = UUID.randomUUID().toString();
        return categoryService.createCategory(dto)
                .onItem().transform(category -> ApiResponse.ok(category, requestId))
                .onFailure().recoverWithItem(ex -> 
                        ApiResponse.fail("CREATION_FAILED", ex.getMessage(), requestId));
    }

    @PUT
    @Path("/{number}")
    public Uni<ApiResponse<GetCategoryDto>> updateCategory(@PathParam("number") String number, UpdateCategoryDto dto, @Context UriInfo uriInfo) {
        String requestId = UUID.randomUUID().toString();
        return categoryService.updateCategory(number, dto)
                .onItem().transform(category -> ApiResponse.ok(category, requestId))
                .onFailure().recoverWithItem(ex -> {
                    if (ex instanceof domain.exception.CategoryNotFoundException) {
                        return ApiResponse.fail("NOT_FOUND", ex.getMessage(), requestId);
                    }
                    return ApiResponse.fail("UPDATE_FAILED", ex.getMessage(), requestId);
                });
    }

    @DELETE
    @Path("/{number}")
    public Uni<ApiResponse<Void>> deleteCategory(@PathParam("number") String number, @Context UriInfo uriInfo) {
        String requestId = UUID.randomUUID().toString();
        return categoryService.deleteCategory(number)
                .onItem().transform(v -> ApiResponse.ok(v, requestId))
                .onFailure().recoverWithItem(ex -> {
                    if (ex instanceof domain.exception.CategoryNotFoundException) {
                        return ApiResponse.fail("NOT_FOUND", ex.getMessage(), requestId);
                    }
                    return ApiResponse.fail("DELETE_FAILED", ex.getMessage(), requestId);
                });
    }
}
