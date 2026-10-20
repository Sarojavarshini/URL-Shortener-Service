package com.internship.urlshortener.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "url_mappings", indexes = {
        @Index(name = "idx_url_short_code", columnList = "short_code", unique = true)
})
public class UrlMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_url", nullable = false, length = 2048)
    private String originalUrl;

    @Column(name = "short_code", nullable = false, unique = true, length = 20)
    private String shortCode;

    @Column(name = "click_count", nullable = false)
    private long clickCount = 0;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected UrlMapping() {}

    public UrlMapping(String originalUrl, String shortCode) {
        this.originalUrl = originalUrl;
        this.shortCode = shortCode;
        this.createdAt = LocalDateTime.now();
        this.clickCount = 0;
    }

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public void incrementClickCount() { this.clickCount++; }

    public Long getId() { return id; }
    public String getOriginalUrl() { return originalUrl; }
    public String getShortCode() { return shortCode; }
    public long getClickCount() { return clickCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
