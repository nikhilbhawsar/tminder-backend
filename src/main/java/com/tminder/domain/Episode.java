package com.tminder.domain;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class Episode {
    private final UUID id;
    private final UUID seriesId;
    private int seasonNumber;
    private int episodeNumber;
    private String title;
    private LocalDate airDate;

    public Episode(UUID id, UUID seriesId, int seasonNumber, int episodeNumber, String title, LocalDate airDate) {
        this.id = id;
        this.seriesId = seriesId;
        this.seasonNumber = seasonNumber;
        this.episodeNumber = episodeNumber;
        this.title = title;
        this.airDate = airDate;
    }

    public UUID getId() { return id; }
    public UUID getSeriesId() { return seriesId; }
    public int getSeasonNumber() { return seasonNumber; }
    public int getEpisodeNumber() { return episodeNumber; }
    public String getTitle() { return title; }
    public LocalDate getAirDate() { return airDate; }

    public void setSeasonNumber(int seasonNumber) { this.seasonNumber = seasonNumber; }
    public void setEpisodeNumber(int episodeNumber) { this.episodeNumber = episodeNumber; }
    public void setTitle(String title) { this.title = title; }
    public void setAirDate(LocalDate airDate) { this.airDate = airDate; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Episode episode = (Episode) o;
        return Objects.equals(id, episode.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
