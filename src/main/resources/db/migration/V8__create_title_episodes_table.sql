CREATE TABLE IF NOT EXISTS title_episodes (
    tconst TEXT PRIMARY KEY REFERENCES media(id),
    parent_tconst TEXT REFERENCES media(id),
    season_number INTEGER,
    episode_number INTEGER
);

CREATE INDEX IF NOT EXISTS idx_episodes_parent ON title_episodes(parent_tconst);
