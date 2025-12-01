package presentation.rest;

import application.dto.ProductRequest;
import application.dto.ProductResponse;
import application.service.ProductService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResource {

    @Inject
    ProductService productService;

    @GET
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }

    @GET
    @Path("/{id}")
    public ProductResponse getProductById(@PathParam("id") Long id) {
        return productService.getProductById(id);
    }

    @POST
    public Response createProduct(ProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PUT
    @Path("/{id}")
    public ProductResponse updateProduct(@PathParam("id") Long id, ProductRequest request) {
        return productService.updateProduct(id, request);
    }

    @DELETE
    @Path("/{id}")
    public Response deleteProduct(@PathParam("id") Long id) {
        productService.deleteProduct(id);
        return Response.noContent().build();
    }
}
