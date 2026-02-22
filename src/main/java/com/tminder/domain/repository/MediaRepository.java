package com.tminder.domain.repository;

import com.tminder.domain.model.Media;
import java.util.List;

public interface MediaRepository {
    List<Media> searchByTitle(String text, String titleType);
}
