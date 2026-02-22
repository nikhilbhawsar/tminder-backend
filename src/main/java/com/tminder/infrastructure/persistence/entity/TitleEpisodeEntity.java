package com.tminder.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "title_episodes")
public class TitleEpisodeEntity {

    @Id
    private String tconst;

    @Column(name = "parent_tconst")
    private String parentTconst;

    @Column(name = "season_number")
    private Integer seasonNumber;

    @Column(name = "episode_number")
    private Integer episodeNumber;

    // getters & setters
}