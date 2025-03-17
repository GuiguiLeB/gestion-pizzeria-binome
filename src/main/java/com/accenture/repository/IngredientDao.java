package com.accenture.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IngredientDao extends JpaRepository<Ingredient,Integer> {
}
