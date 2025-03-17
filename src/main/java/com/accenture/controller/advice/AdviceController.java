package com.accenture.controller.advice;

import com.accenture.exception.IngredientException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class AdviceController {

    @ExceptionHandler(IngredientException.class)
    public ResponseEntity<ErreurReponse> ajoutIngredient(IngredientException ex) {
        ErreurReponse er = new ErreurReponse(LocalDateTime.now(), "Erreur fonctionnelle", ex.getMessage());
        log.error(er.message());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(er);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErreurReponse> trouverID(EntityNotFoundException ex) {
        ErreurReponse er = new ErreurReponse(LocalDateTime.now(), "Mauvaise Requête", ex.getMessage());
        log.error(er.message());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(er);
    }
}
