package pse.nebula.merchandise.application.controller;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import pse.nebula.merchandise.application.dto.ProductRequest;
import pse.nebula.merchandise.application.dto.ProductResponse;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createThenListProducts_shouldReturnCreatedAndListContainsIt() throws Exception {
        ProductRequest request = new ProductRequest(
                "Test Cap",
                "A cap for testing",
                BigDecimal.valueOf(19.99),
                10,
                "http://example.com/img.png");

        mockMvc.perform(post("/api/v1/merchandise/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name", is("Test Cap")));

        mockMvc.perform(get("/api/v1/merchandise/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$[0].name").exists());
    }

    @Test
    void updateThenDeleteProduct_shouldUpdateAndRemove() throws Exception {
        ProductRequest createRequest = new ProductRequest(
                "Starter Cap",
                "Initial product",
                BigDecimal.valueOf(25.00),
                8,
                "http://example.com/start.png");
        createRequest.setCategory("Apparel");
        createRequest.setBadge("New");
        createRequest.setRating(BigDecimal.valueOf(4.2));
        createRequest.setReviews(12);

        MvcResult createResult = mockMvc.perform(post("/api/v1/merchandise/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        ProductResponse created = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                ProductResponse.class);

        ProductRequest updateRequest = new ProductRequest(
                "Updated Cap",
                "Updated description",
                BigDecimal.valueOf(30.00),
                5,
                "http://example.com/updated.png");
        updateRequest.setCategory("Apparel");
        updateRequest.setBadge("Bestseller");
        updateRequest.setRating(BigDecimal.valueOf(4.8));
        updateRequest.setReviews(30);

        mockMvc.perform(put("/api/v1/merchandise/products/{id}", created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(created.getId().intValue())))
                .andExpect(jsonPath("$.name", is("Updated Cap")))
                .andExpect(jsonPath("$.stock", is(5)));

        mockMvc.perform(delete("/api/v1/merchandise/products/{id}", created.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/merchandise/products/{id}", created.getId()))
                .andExpect(status().isNotFound());
    }
}
