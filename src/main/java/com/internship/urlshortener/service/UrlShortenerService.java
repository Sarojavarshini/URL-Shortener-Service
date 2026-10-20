package com.internship.urlshortener.service;

import com.internship.urlshortener.dto.*;
import com.internship.urlshortener.model.ClickEvent;
import com.internship.urlshortener.model.UrlMapping;
import com.internship.urlshortener.repository.ClickEventRepository;
import com.internship.urlshortener.repository.UrlMappingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UrlShortenerService {
    private final UrlMappingRepository urlRepository;
    private final ClickEventRepository clickRepository;

    public UrlShortenerService(UrlMappingRepository urlRepository, ClickEventRepository clickRepository) {
        this.urlRepository = urlRepository;
        this.clickRepository = clickRepository;
    }

    @Transactional
    public UrlResponse createShortUrl(String rawUrl, String baseUrl) {
        String normalizedUrl = validateAndNormalizeUrl(rawUrl.trim());

        // Retry on the very unlikely event of a short-code collision.
        String code;
        int attempts = 0;
        do {
            code = generateCode();
            attempts++;
            if (attempts > 10) {
                throw new IllegalStateException("Could not generate a unique short code. Please retry.");
            }
        } while (urlRepository.existsByShortCode(code));

        UrlMapping mapping = urlRepository.save(new UrlMapping(normalizedUrl, code));
        return toResponse(mapping, baseUrl);
    }

    @Transactional(readOnly = true)
    public List<UrlResponse> listUrls(String baseUrl) {
        return urlRepository.findAll().stream()
                .sorted(Comparator.comparing(UrlMapping::getCreatedAt).reversed())
                .map(mapping -> toResponse(mapping, baseUrl))
                .toList();
    }

    @Transactional
    public String recordClickAndGetDestination(String code, String referer, String userAgent) {
        UrlMapping mapping = urlRepository.findByShortCode(code)
                .orElseThrow(() -> new NoSuchElementException("Short URL not found"));
        mapping.incrementClickCount();
        clickRepository.save(new ClickEvent(mapping, safeHeader(referer), safeHeader(userAgent)));
        return mapping.getOriginalUrl();
    }

    @Transactional(readOnly = true)
    public UrlAnalyticsResponse getAnalytics(String code) {
        UrlMapping mapping = urlRepository.findByShortCode(code)
                .orElseThrow(() -> new NoSuchElementException("Short URL not found"));

        Map<LocalDate, Long> counts = clickRepository
                .findByUrlMapping_IdOrderByClickedAtAsc(mapping.getId())
                .stream()
                .collect(Collectors.groupingBy(
                        event -> event.getClickedAt().toLocalDate(),
                        TreeMap::new,
                        Collectors.counting()
                ));

        List<DailyClick> daily = counts.entrySet().stream()
                .map(entry -> new DailyClick(entry.getKey(), entry.getValue()))
                .toList();

        return new UrlAnalyticsResponse(mapping.getShortCode(), mapping.getOriginalUrl(),
                mapping.getClickCount(), mapping.getCreatedAt(), daily);
    }

    @Transactional(readOnly = true)
    public SummaryResponse getSummary() {
        long totalUrls = urlRepository.count();
        long totalClicks = urlRepository.findAll().stream().mapToLong(UrlMapping::getClickCount).sum();
        return new SummaryResponse(totalUrls, totalClicks);
    }

    private UrlResponse toResponse(UrlMapping mapping, String baseUrl) {
        return new UrlResponse(mapping.getId(), mapping.getOriginalUrl(), mapping.getShortCode(),
                baseUrl + "/" + mapping.getShortCode(), mapping.getClickCount(), mapping.getCreatedAt());
    }

    private String validateAndNormalizeUrl(String rawUrl) {
        try {
            URI uri = URI.create(rawUrl);
            String scheme = uri.getScheme();
            if (scheme == null || !(scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))
                    || uri.getHost() == null) {
                throw new IllegalArgumentException("Only valid http:// or https:// URLs are accepted");
            }
            return uri.toASCIIString();
        } catch (IllegalArgumentException ex) {
            if (ex.getMessage() != null && ex.getMessage().startsWith("Only valid")) throw ex;
            throw new IllegalArgumentException("Enter a valid URL beginning with http:// or https://");
        }
    }

    private String generateCode() {
        final String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder result = new StringBuilder(7);
        for (int i = 0; i < 7; i++) {
            result.append(alphabet.charAt(new Random().nextInt(alphabet.length())));
        }
        return result.toString();
    }

    private String safeHeader(String value) {
        if (value == null) return null;
        return value.length() > 2048 ? value.substring(0, 2048) : value;
    }
}
