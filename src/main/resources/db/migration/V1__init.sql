CREATE TABLE school (
                        id VARCHAR(36) PRIMARY KEY,
                        name VARCHAR(255) UNIQUE NOT NULL,
                        capacity INT NOT NULL
);

CREATE TABLE student (
                         id VARCHAR(36) PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         school_id VARCHAR(36),
                         CONSTRAINT fk_school FOREIGN KEY (school_id) REFERENCES school(id)
);

CREATE TABLE enrollment_job (
                                id VARCHAR(36) PRIMARY KEY,
                                student_id VARCHAR(36),
                                school_id VARCHAR(36),
                                enrollment_status VARCHAR(20),
                                error_message TEXT,
                                created_at TIMESTAMP,
                                updated_at TIMESTAMP
);