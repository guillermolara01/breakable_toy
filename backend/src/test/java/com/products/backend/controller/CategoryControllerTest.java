package com.products.backend.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.products.backend.controller.CategoryController;
import com.products.backend.dto.category.CategoryRequest;
import com.products.backend.exception.CategoryInUseException;
import com.products.backend.exception.DuplicateResourceException;
import com.products.backend.exception.GlobalExceptionHandler;
import com.products.backend.exception.ResourceNotFoundException;
import com.products.backend.model.Category;
import com.products.backend.service.category.ICategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller tests for CategoryController using MockMvc
 */
@WebMvcTest(CategoryController.class)
@ContextConfiguration(classes = {CategoryController.class, GlobalExceptionHandler.class})
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ICategoryService categoryService;

    private Category sampleCategory;
    private CategoryRequest sampleRequest;

    @BeforeEach
    void setUp() {
        sampleCategory = new Category();
        sampleCategory.setId(1L);
        sampleCategory.setName("Electronics");

        sampleRequest = new CategoryRequest();
        sampleRequest.setName("Electronics");
    }

    // ========== GET /categories ==========

    @Test
    void getAllCategories_ShouldReturnCategoryList() throws Exception {
        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Books");

        when(categoryService.getAllCategories()).thenReturn(List.of(sampleCategory, category2));

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Electronics"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Books"));

        verify(categoryService, times(1)).getAllCategories();
    }

    @Test
    void getAllCategories_WhenEmpty_ShouldReturnEmptyArray() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(List.of());

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    // ========== GET /categories/{id} ==========

    @Test
    void getCategory_WhenExists_ShouldReturnCategory() throws Exception {
        when(categoryService.getCategoryById(1L)).thenReturn(sampleCategory);

        mockMvc.perform(get("/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Electronics"));

        verify(categoryService, times(1)).getCategoryById(1L);
    }

    @Test
    void getCategory_WhenNotExists_ShouldReturn404() throws Exception {
        when(categoryService.getCategoryById(999L))
                .thenThrow(new ResourceNotFoundException("Category", 999L));

        mockMvc.perform(get("/categories/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Category not found with id: 999"));
    }

    // ========== POST /categories ==========

    @Test
    void createCategory_WithValidName_ShouldReturnCreatedCategory() throws Exception {
        when(categoryService.createCategory("Electronics")).thenReturn(sampleCategory);

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Electronics"));

        verify(categoryService, times(1)).createCategory("Electronics");
    }

    @Test
    void createCategory_WithBlankName_ShouldReturn400() throws Exception {
        CategoryRequest invalidRequest = new CategoryRequest();
        invalidRequest.setName("");

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(categoryService, never()).createCategory(any());
    }

    @Test
    void createCategory_WithNullName_ShouldReturn400() throws Exception {
        CategoryRequest invalidRequest = new CategoryRequest();
        invalidRequest.setName(null);

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(categoryService, never()).createCategory(any());
    }

    @Test
    void createCategory_WithDuplicateName_ShouldReturn409() throws Exception {
        when(categoryService.createCategory("Electronics"))
                .thenThrow(new DuplicateResourceException("Category", "name", "Electronics"));

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("Category already exists with name: Electronics"));
    }

    // ========== DELETE /categories/{id} ==========

    @Test
    void deleteCategory_WhenExists_ShouldReturn204() throws Exception {
        doNothing().when(categoryService).deleteCategory(1L);

        mockMvc.perform(delete("/categories/1"))
                .andExpect(status().isNoContent());

        verify(categoryService, times(1)).deleteCategory(1L);
    }

    @Test
    void deleteCategory_WhenNotExists_ShouldReturn404() throws Exception {
        doThrow(new ResourceNotFoundException("Category", 999L))
                .when(categoryService).deleteCategory(999L);

        mockMvc.perform(delete("/categories/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Category not found with id: 999"));
    }

    @Test
    void deleteCategory_WhenInUse_ShouldReturn409() throws Exception {
        doThrow(new CategoryInUseException(1L, 5))
                .when(categoryService).deleteCategory(1L);

        mockMvc.perform(delete("/categories/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("Cannot delete category with id 1: 5 product(s) are using this category"));
    }

    @Test
    void deleteCategory_WithInvalidIdFormat_ShouldReturn400() throws Exception {
        mockMvc.perform(delete("/categories/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Type Mismatch"));
    }
}