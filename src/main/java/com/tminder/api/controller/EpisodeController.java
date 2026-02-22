package com.tminder.api.controller;

import com.tminder.api.dto.EpisodeResponse;
import com.tminder.application.service.GetEpisodesUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/media")
public class EpisodeController {

    private final GetEpisodesUseCase getEpisodesUseCase;

    public EpisodeController(GetEpisodesUseCase getEpisodesUseCase) {
        this.getEpisodesUseCase = getEpisodesUseCase;
    }

    @GetMapping("/{id}/episodes")
    public List<EpisodeResponse> getEpisodes(@PathVariable String id) {
        return getEpisodesUseCase.execute(id);
    }
}