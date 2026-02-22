package com.tminder.infrastructure.persistence;

import com.tminder.api.dto.EpisodeResponse;
import com.tminder.infrastructure.persistence.entity.TitleEpisodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaTitleEpisodeRepository
        extends JpaRepository<TitleEpisodeEntity, String> {

    @Query("""
    SELECT new com.tminder.api.dto.EpisodeResponse(
        e.tconst,
        e.seasonNumber,
        e.episodeNumber,
        m.averageRating,
        m.numVotes
    )
    FROM TitleEpisodeEntity e
    JOIN MediaEntity m ON m.id = e.tconst
    WHERE e.parentTconst = :seriesId
    ORDER BY e.seasonNumber, e.episodeNumber
""")
    List<EpisodeResponse> findEpisodesBySeries(
            @Param("seriesId") String seriesId
    );
}