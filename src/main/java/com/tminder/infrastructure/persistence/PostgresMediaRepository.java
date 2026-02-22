package com.tminder.infrastructure.persistence;

import com.tminder.domain.model.Media;
import com.tminder.domain.repository.MediaRepository;
import com.tminder.infrastructure.persistence.entity.MediaEntity;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Primary
public class PostgresMediaRepository implements MediaRepository {

    private final JpaMediaRepository jpaMediaRepository;

    public PostgresMediaRepository(JpaMediaRepository jpaMediaRepository) {
        this.jpaMediaRepository = jpaMediaRepository;
    }

    @Override
    public List<Media> searchByTitle(String text, String titleType) {
        Pageable pageable = PageRequest.of(0, 10);
        return jpaMediaRepository.findByTitleTypeAndTitleContainingIgnoreCase(titleType,text, pageable)
                .stream()
                .map(entity -> new Media(entity.getId(), entity.getTitle()))
                .collect(Collectors.toList());
    }
}
