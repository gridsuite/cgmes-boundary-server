CREATE TABLE IF NOT EXISTS cgmes_boundary.boundaries (
    id text,
    filename text,
    boundary blob,
    scenarioTime timestamp,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS cgmes_boundary.tsos (
    name text,
    tsos blob,
    PRIMARY KEY (name)
);

CREATE TABLE IF NOT EXISTS cgmes_boundary.business_processes (
    name text,
    businessProcesses blob,
    PRIMARY KEY (name)
);
