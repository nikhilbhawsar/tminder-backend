package com.tminder.api.controller;

import com.tminder.api.dto.MediaResponse;
import com.tminder.application.service.SearchMediaUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/search")
public class SearchController {
    private final SearchMediaUseCase searchMediaUseCase;

    public SearchController(SearchMediaUseCase searchMediaUseCase) {
        this.searchMediaUseCase = searchMediaUseCase;
    }

    @GetMapping
    public List<MediaResponse> search(@RequestParam String q) {
        return new ArrayList<>(searchMediaUseCase.execute(q));
    }
}
