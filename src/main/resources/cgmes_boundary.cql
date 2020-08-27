CREATE TABLE IF NOT EXISTS cgmes_boundary.boundaries (
    id text,
    filename text,
    boundary blob,
    PRIMARY KEY (id)
);
