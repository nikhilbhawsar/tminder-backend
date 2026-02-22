package com.tminder.infrastructure.persistence;

import com.tminder.api.dto.MediaResponse;
import com.tminder.domain.repository.MediaRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Profile("test")
public class InMemoryMediaRepository implements MediaRepository {

    private final List<MediaResponse> mediaList = new ArrayList<>();

    public InMemoryMediaRepository() {
        mediaList.add(new MediaResponse("1", "The Dark Knight"));
        mediaList.add(new MediaResponse("2", "Inception"));
        mediaList.add(new MediaResponse("3", "Interstellar"));
        mediaList.add(new MediaResponse("4", "The Matrix"));
        mediaList.add(new MediaResponse("5", "Breaking Bad"));
    }

    @Override
    public List<MediaResponse> searchByTitle(String text, String titleType) {
        return mediaList.stream()
                .filter(m -> m.title().toLowerCase().contains(text.toLowerCase()))
                .limit(10)
                .toList();
    }
}
