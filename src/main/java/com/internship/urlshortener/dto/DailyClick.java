package com.internship.urlshortener.dto;

import java.time.LocalDate;

public record DailyClick(LocalDate date, long clicks) {}
