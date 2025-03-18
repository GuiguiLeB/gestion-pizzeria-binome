package com.accenture.controller;

import com.accenture.repository.Ingredient;
import com.accenture.service.IngredientService;
import com.accenture.service.IngredientServiceImpl;
import com.accenture.service.dto.IngredientRequestDto;
import com.accenture.service.dto.IngredientResponseDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/ingredients")
public class IngredientController {

    private final IngredientServiceImpl service;

    public IngredientController(IngredientServiceImpl service) {
        this.service = service;
    }


    @PostMapping
    ResponseEntity<IngredientResponseDto> ajouter(@RequestBody @Valid IngredientRequestDto ingredientRequestDto){
        IngredientResponseDto ajouter = service.ajouter(ingredientRequestDto);
        URI ingredient = ServletUriComponentsBuilder
                .fromCurrentRequest() .path("/{id}")
                .buildAndExpand(ajouter.id())
                .toUri();
        return ResponseEntity.created(ingredient).body(ajouter);
    }

    @GetMapping
    List<IngredientResponseDto> trouverTous(){
        List<IngredientResponseDto> dto = service.trouverTous();
        return dto;
    }

    @GetMapping("/{id}")
    ResponseEntity<IngredientResponseDto> trouverIngredient(@PathVariable("id") int id) {
        IngredientResponseDto trouver = service.trouver(id);
        return ResponseEntity.ok(trouver);
    }

    @PatchMapping("/{id}")
    ResponseEntity<IngredientResponseDto> modifier(@PathVariable("id") int id, @RequestBody IngredientRequestDto ingredientRequestDto){
        IngredientResponseDto responseDto = service.modifier(id, ingredientRequestDto);
        return ResponseEntity.ok(responseDto);
    }


}
