package com.internship.urlshortener.controller;

import com.internship.urlshortener.dto.*;
import com.internship.urlshortener.service.UrlShortenerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UrlController {
    private final UrlShortenerService service;

    public UrlController(UrlShortenerService service) {
        this.service = service;
    }

    @PostMapping("/urls")
    public ResponseEntity<UrlResponse> create(@Valid @RequestBody CreateUrlRequest request) {
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        UrlResponse response = service.createShortUrl(request.url(), baseUrl);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/urls")
    public List<UrlResponse> list(HttpServletRequest request) {
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        return service.listUrls(baseUrl);
    }

    @GetMapping("/urls/{code}/analytics")
    public UrlAnalyticsResponse analytics(@PathVariable String code) {
        return service.getAnalytics(code);
    }

    @GetMapping("/analytics/summary")
    public SummaryResponse summary() {
        return service.getSummary();
    }
}
