package com.tminder.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity representing a TV Series.
 * In Clean Architecture, this class remains pure and independent of any framework (no JPA or Spring annotations).
 */
public class Series {
    private final UUID id;
    private String title;
    private String description;
    private SeriesStatus status;
    private Double rating;

    public Series(UUID id, String title, String description, SeriesStatus status, Double rating) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.rating = rating;
    }

    // Getters
    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public SeriesStatus getStatus() { return status; }
    public Double getRating() { return rating; }

    // Setters
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setStatus(SeriesStatus status) { this.status = status; }
    public void setRating(Double rating) { this.rating = rating; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Series series = (Series) o;
        return Objects.equals(id, series.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Series{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", status=" + status +
                '}';
    }
}
