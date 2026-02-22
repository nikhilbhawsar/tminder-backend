CREATE TABLE IF NOT EXISTS names (
    nconst TEXT PRIMARY KEY,
    primary_name TEXT NOT NULL,
    birth_year INTEGER,
    death_year INTEGER,
    primary_profession TEXT[],
    known_for_titles TEXT[]
);

CREATE INDEX IF NOT EXISTS idx_names_primary_name ON names(primary_name);
