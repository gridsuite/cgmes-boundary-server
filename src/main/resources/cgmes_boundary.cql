CREATE TABLE IF NOT EXISTS boundaries (
    id text,
    filename text,
    boundary blob,
    scenarioTime timestamp,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS tsos (
    name text,
    tsos blob,
    PRIMARY KEY (name)
);

CREATE TABLE IF NOT EXISTS business_processes (
    name text,
    businessProcesses blob,
    PRIMARY KEY (name)
);
