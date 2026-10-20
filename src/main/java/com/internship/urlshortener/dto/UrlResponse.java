package com.internship.urlshortener.dto;

import java.time.LocalDateTime;

public record UrlResponse(
        Long id,
        String originalUrl,
        String shortCode,
        String shortUrl,
        long clicks,
        LocalDateTime createdAt
) {}
