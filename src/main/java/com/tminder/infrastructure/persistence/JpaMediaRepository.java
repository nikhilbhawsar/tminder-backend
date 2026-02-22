package com.tminder.infrastructure.persistence;

import com.tminder.api.dto.MediaResponse;
import com.tminder.infrastructure.persistence.entity.MediaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaMediaRepository extends JpaRepository<MediaEntity, String> {

    @Query("""
        SELECT new com.tminder.api.dto.MediaResponse(m.id, m.title)
        FROM MediaEntity m
        WHERE m.titleType = :type
        AND m.title ILIKE :pattern
    """)
    Page<MediaResponse> searchMedia(
            @Param("type") String type,
            @Param("pattern") String pattern,
            Pageable pageable);
}
