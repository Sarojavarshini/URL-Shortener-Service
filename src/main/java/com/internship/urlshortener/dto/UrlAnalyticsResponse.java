package com.internship.urlshortener.dto;

import java.time.LocalDateTime;
import java.util.List;

public record UrlAnalyticsResponse(
        String shortCode,
        String originalUrl,
        long totalClicks,
        LocalDateTime createdAt,
        List<DailyClick> dailyClicks
) {}
