package com.accenture.service.mapper;

import com.accenture.repository.Ingredient;
import com.accenture.service.dto.IngredientRequestDto;
import com.accenture.service.dto.IngredientResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")

public interface IngredientMapper {
    Ingredient toIngredient(IngredientRequestDto ingredientRequestDto);
    IngredientResponseDto toIngredientResponseDto  (Ingredient ingredient);
    IngredientRequestDto toIngredientRequestDto (Ingredient ingredient);
}
