package com.tminder.domain.repository;

import com.tminder.api.dto.EpisodeResponse;

import java.util.List;

public interface TitleEpisodeRepository {
    List<EpisodeResponse> findBySeriesId(String seriesId);
}