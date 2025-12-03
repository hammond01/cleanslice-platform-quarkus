package presentation.rest;

import application.dto.CreateProduct;
import application.dto.GetProduct;
import application.service.ProductService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
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
    public List<GetProduct> getAllProducts() {
        return productService.getAllProducts();
    }

    @GET
    @Path("/{id}")
    public GetProduct getProductById(@PathParam("id") String id) {
        return productService.getProductById(id);
    }

    @POST
    public Response createProduct(@Valid CreateProduct request) {
        GetProduct response = productService.createProduct(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PUT
    @Path("/{id}")
    public GetProduct updateProduct(@PathParam("id") String id, @Valid CreateProduct request) {
        return productService.updateProduct(id, request);
    }

    @DELETE
    @Path("/{id}")
    public Response deleteProduct(@PathParam("id") String id) {
        productService.deleteProduct(id);
        return Response.noContent().build();
    }
}
