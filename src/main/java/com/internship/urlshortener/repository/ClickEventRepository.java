package com.internship.urlshortener.repository;

import com.internship.urlshortener.model.ClickEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClickEventRepository extends JpaRepository<ClickEvent, Long> {
    List<ClickEvent> findByUrlMapping_IdOrderByClickedAtAsc(Long urlMappingId);
}
