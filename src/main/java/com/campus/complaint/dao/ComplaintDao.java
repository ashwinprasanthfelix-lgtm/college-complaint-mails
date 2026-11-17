package com.campus.complaint.dao;

import com.campus.complaint.model.Complaint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.*;

@Repository
public class ComplaintDao {

    private final JdbcTemplate jdbc;

    public ComplaintDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final String BASE = """
            SELECT c.complaint_id, c.student_id, c.category, c.description, c.priority, c.status,
                   c.created_at, c.updated_at, s.full_name, s.usn
            FROM complaints c JOIN students s ON s.student_id = c.student_id
            """;

    private static final RowMapper<Complaint> MAPPER = (rs, i) -> {
        Complaint c = new Complaint();
        c.setComplaintId(rs.getLong("complaint_id"));
        c.setStudentId(rs.getLong("student_id"));
        c.setCategory(rs.getString("category"));
        c.setDescription(rs.getString("description"));
        c.setPriority(rs.getString("priority"));
        c.setStatus(rs.getString("status"));
        Timestamp created = rs.getTimestamp("created_at");
        Timestamp updated = rs.getTimestamp("updated_at");
        c.setCreatedAt(created == null ? null : created.toLocalDateTime());
        c.setUpdatedAt(updated == null ? null : updated.toLocalDateTime());
        c.setStudentName(rs.getString("full_name"));
        c.setUsn(rs.getString("usn"));
        return c;
    };

    // ----- Create -----
    public void save(Long studentId, String category, String description) {
        jdbc.update("INSERT INTO complaints (student_id, category, description) VALUES (?, ?, ?)",
                studentId, category, description.trim());
    }

    // ----- Read -----
    public List<Complaint> findByStudent(Long studentId) {
        return jdbc.query(BASE + " WHERE c.student_id = ? ORDER BY c.created_at DESC, c.complaint_id DESC",
                MAPPER, studentId);
    }

    public Optional<Complaint> findByIdAndStudent(Long complaintId, Long studentId) {
        return jdbc.query(BASE + " WHERE c.complaint_id = ? AND c.student_id = ?",
                MAPPER, complaintId, studentId).stream().findFirst();
    }

    /** Admin search: any filter may be null/blank. */
    public List<Complaint> search(Long complaintId, String category, String status) {
        StringBuilder sql = new StringBuilder(BASE).append(" WHERE 1 = 1");
        List<Object> args = new ArrayList<>();
        if (complaintId != null) {
            sql.append(" AND c.complaint_id = ?");
            args.add(complaintId);
        }
        if (category != null && !category.isBlank()) {
            sql.append(" AND c.category = ?");
            args.add(category);
        }
        if (status != null && !status.isBlank()) {
            sql.append(" AND c.status = ?");
            args.add(status);
        }
        sql.append(" ORDER BY c.created_at DESC, c.complaint_id DESC");
        return jdbc.query(sql.toString(), MAPPER, args.toArray());
    }

    // ----- Update (student: only while Pending) -----
    public int updateByStudent(Long complaintId, Long studentId, String category, String description) {
        return jdbc.update("""
                UPDATE complaints SET category = ?, description = ?, updated_at = CURRENT_TIMESTAMP
                WHERE complaint_id = ? AND student_id = ? AND status = 'Pending'
                """, category, description.trim(), complaintId, studentId);
    }

    // ----- Update (admin) -----
    public int updateByAdmin(Long complaintId, String status, String priority) {
        return jdbc.update("""
                UPDATE complaints SET status = ?, priority = ?, updated_at = CURRENT_TIMESTAMP
                WHERE complaint_id = ?
                """, status, priority, complaintId);
    }

    // ----- Delete (student: only while Pending) -----
    public int deleteByStudent(Long complaintId, Long studentId) {
        return jdbc.update("DELETE FROM complaints WHERE complaint_id = ? AND student_id = ? AND status = 'Pending'",
                complaintId, studentId);
    }

    // ----- Stats -----
    public Map<String, Integer> countsByStatus(Long studentId) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        counts.put("Pending", 0);
        counts.put("In Progress", 0);
        counts.put("Resolved", 0);
        String sql = "SELECT status, COUNT(*) AS cnt FROM complaints"
                + (studentId != null ? " WHERE student_id = ?" : "") + " GROUP BY status";
        RowMapper<Void> rm = (rs, i) -> {
            counts.put(rs.getString("status"), rs.getInt("cnt"));
            return null;
        };
        if (studentId != null) jdbc.query(sql, rm, studentId);
        else jdbc.query(sql, rm);
        return counts;
    }
}
