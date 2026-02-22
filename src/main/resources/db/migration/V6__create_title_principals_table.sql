CREATE TABLE IF NOT EXISTS title_principals (
    tconst TEXT NOT NULL,
    ordering INTEGER NOT NULL,
    nconst TEXT NOT NULL,
    category TEXT,
    job TEXT,
    characters TEXT,
    PRIMARY KEY (tconst, ordering)
);

CREATE INDEX IF NOT EXISTS idx_principals_tconst ON title_principals(tconst);
CREATE INDEX IF NOT EXISTS idx_principals_nconst ON title_principals(nconst);
