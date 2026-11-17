package com.campus.complaint.dao;

import com.campus.complaint.model.Student;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class StudentDao {

    private final JdbcTemplate jdbc;

    public StudentDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RowMapper<Student> MAPPER = (rs, i) -> {
        Student s = new Student();
        s.setStudentId(rs.getLong("student_id"));
        s.setFullName(rs.getString("full_name"));
        s.setUsn(rs.getString("usn"));
        s.setEmail(rs.getString("email"));
        s.setDepartment(rs.getString("department"));
        s.setContactNo(rs.getString("contact_no"));
        s.setPasswordHash(rs.getString("password_hash"));
        return s;
    };

    public void save(Student s) {
        jdbc.update("""
                INSERT INTO students (full_name, usn, email, department, contact_no, password_hash)
                VALUES (?, ?, ?, ?, ?, ?)
                """,
                s.getFullName(), s.getUsn().toUpperCase(), s.getEmail().toLowerCase(),
                s.getDepartment(), s.getContactNo(), s.getPasswordHash());
    }

    public Optional<Student> findByUsn(String usn) {
        return jdbc.query("SELECT * FROM students WHERE usn = ?", MAPPER, usn.toUpperCase())
                .stream().findFirst();
    }

    public boolean existsByUsn(String usn) {
        Integer n = jdbc.queryForObject("SELECT COUNT(*) FROM students WHERE usn = ?",
                Integer.class, usn.toUpperCase());
        return n != null && n > 0;
    }

    public boolean existsByEmail(String email) {
        Integer n = jdbc.queryForObject("SELECT COUNT(*) FROM students WHERE email = ?",
                Integer.class, email.toLowerCase());
        return n != null && n > 0;
    }

    public Optional<Student> findById(Long id) {
        return jdbc.query("SELECT * FROM students WHERE student_id = ?", MAPPER, id)
                .stream().findFirst();
    }

    public List<Student> findAll() {
        return jdbc.query("SELECT * FROM students ORDER BY full_name", MAPPER);
    }

    public int updateProfile(Long id, String fullName, String email, String department, String contactNo) {
        return jdbc.update("""
                UPDATE students SET full_name = ?, email = ?, department = ?, contact_no = ?
                WHERE student_id = ?
                """, fullName, email.toLowerCase(), department, contactNo, id);
    }

    public boolean emailUsedByOther(String email, Long id) {
        Integer n = jdbc.queryForObject(
                "SELECT COUNT(*) FROM students WHERE email = ? AND student_id <> ?",
                Integer.class, email.toLowerCase(), id);
        return n != null && n > 0;
    }

    public int deleteById(Long id) {
        return jdbc.update("DELETE FROM students WHERE student_id = ?", id);
    }
}
