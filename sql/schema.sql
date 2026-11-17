-- Run as a privileged user (e.g. SYSTEM) once:
--   CREATE USER complaint_user IDENTIFIED BY complaint_pass QUOTA UNLIMITED ON USERS;
--   GRANT CONNECT, RESOURCE TO complaint_user;
-- Then connect as complaint_user and run the script below.
-- (On XE 18c/21c, first run: ALTER SESSION SET CONTAINER = XEPDB1;)

CREATE TABLE students (
    student_id     NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    full_name      VARCHAR2(100) NOT NULL,
    usn            VARCHAR2(20)  NOT NULL UNIQUE,
    email          VARCHAR2(120) NOT NULL UNIQUE,
    department     VARCHAR2(60)  NOT NULL,
    contact_no     VARCHAR2(15)  NOT NULL,
    password_hash  VARCHAR2(100) NOT NULL,
    created_at     TIMESTAMP DEFAULT SYSTIMESTAMP
);

CREATE TABLE admins (
    admin_id       NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username       VARCHAR2(50)  NOT NULL UNIQUE,
    password_hash  VARCHAR2(100) NOT NULL
);

CREATE TABLE complaints (
    complaint_id   NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    student_id     NUMBER NOT NULL,
    category       VARCHAR2(40)   NOT NULL,
    description    VARCHAR2(1000) NOT NULL,
    priority       VARCHAR2(10) DEFAULT 'Medium' NOT NULL,
    status         VARCHAR2(15) DEFAULT 'Pending' NOT NULL,
    created_at     TIMESTAMP DEFAULT SYSTIMESTAMP,
    updated_at     TIMESTAMP DEFAULT SYSTIMESTAMP,
    CONSTRAINT fk_complaint_student FOREIGN KEY (student_id)
        REFERENCES students(student_id) ON DELETE CASCADE,
    CONSTRAINT chk_priority CHECK (priority IN ('Low','Medium','High')),
    CONSTRAINT chk_status   CHECK (status IN ('Pending','In Progress','Resolved'))
);

CREATE INDEX idx_complaint_student ON complaints(student_id);
CREATE INDEX idx_complaint_status  ON complaints(status);
