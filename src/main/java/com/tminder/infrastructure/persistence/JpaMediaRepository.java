package com.tminder.infrastructure.persistence;

import com.tminder.infrastructure.persistence.entity.MediaEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaMediaRepository extends JpaRepository<MediaEntity, String> {
    List<MediaEntity> findByTitleTypeAndTitleContainingIgnoreCase(String titleType, String title, Pageable pageable);
}
