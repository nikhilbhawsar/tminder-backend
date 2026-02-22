package com.tminder.application.service;

import com.tminder.domain.model.Media;
import com.tminder.domain.repository.MediaRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SearchMediaUseCase {
    private final MediaRepository mediaRepository;

    public SearchMediaUseCase(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    public List<Media> execute(String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        return mediaRepository.searchByTitle(query, "tvSeries");
    }
}
