package com.tminder.api.dto;

public record EpisodeResponse(
        String id,
        Integer seasonNumber,
        Integer episodeNumber,
        Double averageRating,
        Integer numVotes
) {}