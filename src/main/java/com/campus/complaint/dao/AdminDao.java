package com.campus.complaint.dao;

import com.campus.complaint.model.Admin;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AdminDao {

    private final JdbcTemplate jdbc;

    public AdminDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Optional<Admin> findByUsername(String username) {
        return jdbc.query("SELECT admin_id, username, password_hash FROM admins WHERE username = ?",
                (rs, i) -> {
                    Admin a = new Admin();
                    a.setAdminId(rs.getLong("admin_id"));
                    a.setUsername(rs.getString("username"));
                    a.setPasswordHash(rs.getString("password_hash"));
                    return a;
                }, username).stream().findFirst();
    }

    public int count() {
        Integer n = jdbc.queryForObject("SELECT COUNT(*) FROM admins", Integer.class);
        return n == null ? 0 : n;
    }

    public void save(String username, String passwordHash) {
        jdbc.update("INSERT INTO admins (username, password_hash) VALUES (?, ?)", username, passwordHash);
    }
}
