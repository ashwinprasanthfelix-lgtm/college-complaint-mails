CREATE TABLE IF NOT EXISTS students (
    student_id     NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    full_name      VARCHAR2(100) NOT NULL,
    usn            VARCHAR2(20)  NOT NULL UNIQUE,
    email          VARCHAR2(120) NOT NULL UNIQUE,
    department     VARCHAR2(60)  NOT NULL,
    contact_no     VARCHAR2(15)  NOT NULL,
    password_hash  VARCHAR2(100) NOT NULL,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS admins (
    admin_id       NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username       VARCHAR2(50)  NOT NULL UNIQUE,
    password_hash  VARCHAR2(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS complaints (
    complaint_id   NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    student_id     NUMBER NOT NULL,
    category       VARCHAR2(40)   NOT NULL,
    description    VARCHAR2(1000) NOT NULL,
    priority       VARCHAR2(10) DEFAULT 'Medium' NOT NULL,
    status         VARCHAR2(15) DEFAULT 'Pending' NOT NULL,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_complaint_student FOREIGN KEY (student_id)
        REFERENCES students(student_id) ON DELETE CASCADE,
    CONSTRAINT chk_priority CHECK (priority IN ('Low','Medium','High')),
    CONSTRAINT chk_status   CHECK (status IN ('Pending','In Progress','Resolved'))
);

CREATE INDEX IF NOT EXISTS idx_complaint_student ON complaints(student_id);
CREATE INDEX IF NOT EXISTS idx_complaint_status  ON complaints(status);
