package presentation.rest;

import application.dto.CreateProduct;
import application.dto.GetProduct;
import application.service.ProductService;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;
import share.ApiResponse;

import java.util.List;
import java.util.UUID;

@Path("/api/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductController {

    @Inject
    ProductService productService;

    @GET
    public Uni<ApiResponse<List<GetProduct>>> getAllProducts(@Context UriInfo uriInfo) {
        String requestId = UUID.randomUUID().toString();
        return productService.getAllProducts()
                .onItem().transform(products -> ApiResponse.ok(products, requestId))
                .onFailure().recoverWithItem(ex -> 
                        ApiResponse.fail("INTERNAL_ERROR", ex.getMessage(), requestId));
    }

    @GET
    @Path("/{id}")
    public Uni<ApiResponse<GetProduct>> getProductById(@PathParam("id") String id, @Context UriInfo uriInfo) {
        String requestId = UUID.randomUUID().toString();
        return productService.getProductById(id)
                .onItem().transform(product -> ApiResponse.ok(product, requestId))
                .onFailure().recoverWithItem(ex -> {
                    if (ex instanceof domain.exception.ProductNotFoundException) {
                        return ApiResponse.fail("NOT_FOUND", ex.getMessage(), requestId);
                    }
                    return ApiResponse.fail("INTERNAL_ERROR", ex.getMessage(), requestId);
                });
    }

    @POST
    public Uni<ApiResponse<GetProduct>> createProduct(@Valid CreateProduct request, @Context UriInfo uriInfo) {
        String requestId = UUID.randomUUID().toString();
        return productService.createProduct(request)
                .onItem().transform(product -> ApiResponse.ok(product, requestId))
                .onFailure().recoverWithItem(ex -> 
                        ApiResponse.fail("CREATION_FAILED", ex.getMessage(), requestId));
    }

    @PUT
    @Path("/{id}")
    public Uni<ApiResponse<GetProduct>> updateProduct(@PathParam("id") String id, @Valid CreateProduct request, @Context UriInfo uriInfo) {
        String requestId = UUID.randomUUID().toString();
        return productService.updateProduct(id, request)
                .onItem().transform(product -> ApiResponse.ok(product, requestId))
                .onFailure().recoverWithItem(ex -> {
                    if (ex instanceof domain.exception.ProductNotFoundException) {
                        return ApiResponse.fail("NOT_FOUND", ex.getMessage(), requestId);
                    }
                    return ApiResponse.fail("UPDATE_FAILED", ex.getMessage(), requestId);
                });
    }

    @DELETE
    @Path("/{id}")
    public Uni<ApiResponse<Void>> deleteProduct(@PathParam("id") String id, @Context UriInfo uriInfo) {
        String requestId = UUID.randomUUID().toString();
        return productService.deleteProduct(id)
                .onItem().transform(v -> ApiResponse.ok(v, requestId))
                .onFailure().recoverWithItem(ex -> {
                    if (ex instanceof domain.exception.ProductNotFoundException) {
                        return ApiResponse.fail("NOT_FOUND", ex.getMessage(), requestId);
                    }
                    return ApiResponse.fail("DELETE_FAILED", ex.getMessage(), requestId);
                });
    }
}
