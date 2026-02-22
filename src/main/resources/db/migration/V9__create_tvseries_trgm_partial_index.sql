CREATE INDEX IF NOT EXISTS idx_media_tvseries_title_trgm
ON media
USING GIN (title gin_trgm_ops)
WHERE title_type = 'tvSeries';