package com.accenture.service;

import com.accenture.exception.IngredientException;
import com.accenture.repository.Ingredient;
import com.accenture.service.dto.IngredientRequestDto;
import com.accenture.service.dto.IngredientResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;


public interface IngredientService {

    IngredientResponseDto ajouter(IngredientRequestDto ingredientRequestDto) throws IngredientException;

    List<IngredientResponseDto> trouverTous();

    IngredientResponseDto trouver(int id);

    IngredientResponseDto modifier(int id, IngredientRequestDto request);
}
