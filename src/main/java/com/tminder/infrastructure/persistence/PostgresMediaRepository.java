package com.tminder.infrastructure.persistence;

import com.tminder.api.dto.MediaResponse;
import com.tminder.domain.repository.MediaRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Primary
public class PostgresMediaRepository implements MediaRepository {

    private final JpaMediaRepository jpaMediaRepository;

    public PostgresMediaRepository(JpaMediaRepository jpaMediaRepository) {
        this.jpaMediaRepository = jpaMediaRepository;
    }

    @Override
    public List<MediaResponse> searchByTitle(String text, String titleType) {
        Pageable pageable = PageRequest.of(0, 10);
        String pattern = "%" + text + "%";

        return jpaMediaRepository
                .searchMedia(titleType, pattern, pageable)
                .getContent();
    }
}
