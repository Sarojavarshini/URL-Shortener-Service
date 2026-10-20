package com.internship.urlshortener.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "click_events", indexes = {
        @Index(name = "idx_click_url_time", columnList = "url_mapping_id, clicked_at")
})
public class ClickEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "url_mapping_id", nullable = false)
    private UrlMapping urlMapping;

    @Column(name = "clicked_at", nullable = false)
    private LocalDateTime clickedAt;

    @Column(name = "referer", length = 2048)
    private String referer;

    @Column(name = "user_agent", length = 1000)
    private String userAgent;

    protected ClickEvent() {}

    public ClickEvent(UrlMapping urlMapping, String referer, String userAgent) {
        this.urlMapping = urlMapping;
        this.referer = referer;
        this.userAgent = userAgent;
        this.clickedAt = LocalDateTime.now();
    }

    public LocalDateTime getClickedAt() { return clickedAt; }
}
