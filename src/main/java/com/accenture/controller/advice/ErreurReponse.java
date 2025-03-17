package com.accenture.controller.advice;

import java.time.LocalDateTime;

public record ErreurReponse(LocalDateTime temporalite, String type, String message) {
}
