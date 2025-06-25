//package com.products.backend;
//
//import com.products.backend.controller.ProductController;
//import com.products.backend.model.Product;
//import com.products.backend.service.product.ProductService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.time.LocalDate;
//
//import static org.mockito.Mockito.when;
//
//@WebMvcTest(ProductController.class)
//public class ProductControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private ProductService productService;
//
//    @Test
//    void shouldReturnProductList() throws Exception {
//        Product p = new Product("Apple", "Fruit", 1.0, LocalDate.parse("2025-12-01"), 10);
//        when(productService.getAllProducts()).thenReturn(List.of(p));
//
//        mockMvc.perform(get("/products"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].name").value("Apple"));
//    }
//}
