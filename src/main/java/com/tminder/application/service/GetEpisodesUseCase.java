package com.tminder.application.service;

import com.tminder.api.dto.EpisodeResponse;
import com.tminder.domain.repository.TitleEpisodeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetEpisodesUseCase {

    private final TitleEpisodeRepository titleEpisodeRepository;

    public GetEpisodesUseCase(TitleEpisodeRepository titleEpisodeRepository) {
        this.titleEpisodeRepository = titleEpisodeRepository;
    }

    public List<EpisodeResponse> execute(String seriesId) {
        if (seriesId == null || seriesId.isBlank()) {
            return List.of();
        }
        return titleEpisodeRepository.findBySeriesId(seriesId);
    }
}