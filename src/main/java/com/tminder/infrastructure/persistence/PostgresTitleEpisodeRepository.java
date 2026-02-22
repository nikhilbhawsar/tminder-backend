package com.tminder.infrastructure.persistence;

import com.tminder.api.dto.EpisodeResponse;
import com.tminder.domain.repository.TitleEpisodeRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PostgresTitleEpisodeRepository implements TitleEpisodeRepository {

    private final JpaTitleEpisodeRepository jpaTitleEpisodeRepository;

    public PostgresTitleEpisodeRepository(JpaTitleEpisodeRepository jpaTitleEpisodeRepository) {
        this.jpaTitleEpisodeRepository = jpaTitleEpisodeRepository;
    }

    @Override
    public List<EpisodeResponse> findBySeriesId(String seriesId) {
        return jpaTitleEpisodeRepository.findEpisodesBySeries(seriesId);
    }
}