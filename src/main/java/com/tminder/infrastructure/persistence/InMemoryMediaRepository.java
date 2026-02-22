package com.tminder.infrastructure.persistence;

import com.tminder.domain.model.Media;
import com.tminder.domain.repository.MediaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class InMemoryMediaRepository implements MediaRepository {
    private final List<Media> mediaList = new ArrayList<>();

    public InMemoryMediaRepository() {
        // Sample data for testing
        mediaList.add(new Media("1", "The Dark Knight"));
        mediaList.add(new Media("2", "Inception"));
        mediaList.add(new Media("3", "Interstellar"));
        mediaList.add(new Media("4", "The Matrix"));
        mediaList.add(new Media("5", "Breaking Bad"));
    }

    @Override
    public List<Media> searchByTitle(String text , String titleType) {
        return mediaList.stream()
                .filter(m -> m.getTitle().toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());
    }
}
