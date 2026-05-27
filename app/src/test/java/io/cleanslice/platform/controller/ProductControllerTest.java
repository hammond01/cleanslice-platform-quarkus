package io.cleanslice.platform.controller;

import io.cleanslice.platform.common.exception.ResourceNotFoundException;
import io.cleanslice.platform.common.response.ApiResponse;
import io.cleanslice.platform.dto.ProductResponse;
import io.cleanslice.platform.service.ProductService;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static io.cleanslice.platform.testing.UnitTestSupport.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    ProductService productService;

    @InjectMocks
    ProductController productController;

    @Test
    void getProductById_whenFound_shouldReturnOkResponse() {
        ProductResponse response = new ProductResponse();
        response.name = "Paracetamol";
        when(productService.getProductById("P-001")).thenReturn(Uni.createFrom().item(response));

        ApiResponse<ProductResponse> result = await(productController.getProductById("P-001", null));

        assertTrue(result.success);
        assertNotNull(result.requestId);
        assertEquals("Paracetamol", result.data.name);
    }

    @Test
    void getProductById_whenNotFound_shouldMapNotFoundError() {
        when(productService.getProductById("missing"))
                .thenReturn(Uni.createFrom().failure(new ResourceNotFoundException("missing")));

        ApiResponse<ProductResponse> result = await(productController.getProductById("missing", null));

        assertFalse(result.success);
        assertNotNull(result.error);
        assertEquals("NOT_FOUND", result.error.code);
        assertEquals("missing", result.error.message);
    }
}
