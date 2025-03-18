package com.accenture.controller;

import com.accenture.repository.Ingredient;
import com.accenture.service.dto.IngredientRequestDto;
import com.accenture.service.dto.IngredientResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc

public class IngredientControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void testPostIngredient() throws Exception {

        IngredientResponseDto tomate = new IngredientResponseDto(1,"tomate", 1);
        mockMvc.perform(MockMvcRequestBuilders.post("/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tomate)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.id").value(Matchers.not(0)))
                .andExpect(jsonPath("$.nom").value("tomate"))
                .andExpect(jsonPath("$.quantite").isNumber())
                .andExpect(jsonPath("$.quantite").value(1));

    }

    @Test
    void testTrouverTous() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/ingredients")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));

    }

    @Test
    void testTrouverParNomPasOk() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/ingredients/77"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("ingrédient non présent"));

    }

    @Test
    void testTrouverParId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/ingredients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nom").value("Tomate"))
                .andExpect(jsonPath("$.quantite").isNumber())
                .andExpect(jsonPath("$.quantite").value(15));
    }

     @Test
    void testModifier() throws Exception{
        int id = 1;
        IngredientRequestDto requestDto = new IngredientRequestDto("Tomate", 15);

         mockMvc.perform(MockMvcRequestBuilders.patch("/ingredients/{id}", id)
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(requestDto)))
                 .andExpect(status().isOk())
                 .andExpect(jsonPath("$.id").value(id))
                 .andExpect(jsonPath("$.nom").value("Tomate"))
                 .andExpect(jsonPath("$.quantite").value(15));


     }






}
