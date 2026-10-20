package com.internship.urlshortener.controller;

import com.internship.urlshortener.service.UrlShortenerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.NoSuchElementException;

@RestController
public class RedirectController {
    private final UrlShortenerService service;

    public RedirectController(UrlShortenerService service) {
        this.service = service;
    }

    @GetMapping("/{code:[a-zA-Z0-9]{7}}")
    public ResponseEntity<Void> redirect(@PathVariable String code, HttpServletRequest request) {
        String destination = service.recordClickAndGetDestination(
                code, request.getHeader("Referer"), request.getHeader("User-Agent"));
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, URI.create(destination).toASCIIString())
                .build();
    }
}
