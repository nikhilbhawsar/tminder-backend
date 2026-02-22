package com.tminder.domain.repository;

import com.tminder.api.dto.MediaResponse;

import java.util.List;

public interface MediaRepository {
    List<MediaResponse> searchByTitle(String text, String titleType);
}
