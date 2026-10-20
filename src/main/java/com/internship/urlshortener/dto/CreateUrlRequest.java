package com.internship.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUrlRequest(
        @NotBlank(message = "URL is required")
        @Size(max = 2048, message = "URL must be at most 2048 characters")
        String url
) {}
