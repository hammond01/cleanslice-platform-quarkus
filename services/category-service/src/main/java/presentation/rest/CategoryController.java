package presentation.rest;

import application.dto.GetCategoryDto;
import application.dto.CreateCategoryDto;
import application.dto.UpdateCategoryDto;
import application.service.CategoryService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoryController {

    @Inject
    CategoryService categoryService;

    @GET
    public List<GetCategoryDto> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @GET
    @Path("/{id}")
    public GetCategoryDto getCategoryById(@PathParam("id") Long id) {
        return categoryService.getCategoryById(id);
    }

    @POST
    public Response createCategory(CreateCategoryDto dto) {
        GetCategoryDto response = categoryService.createCategory(dto);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PUT
    @Path("/{id}")
    public GetCategoryDto updateCategory(@PathParam("id") Long id, UpdateCategoryDto dto) {
        return categoryService.updateCategory(id, dto);
    }

    @DELETE
    @Path("/{id}")
    public Response deleteCategory(@PathParam("id") Long id) {
        categoryService.deleteCategory(id);
        return Response.noContent().build();
    }
}
