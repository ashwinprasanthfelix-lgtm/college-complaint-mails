package com.campus.complaint.model;

import java.time.LocalDateTime;

public class Complaint {
    private Long complaintId;
    private Long studentId;
    private String category;
    private String description;
    private String priority;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String studentName;
    private String usn;

    public Long getComplaintId() { return complaintId; }
    public void setComplaintId(Long v) { this.complaintId = v; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long v) { this.studentId = v; }
    public String getCategory() { return category; }
    public void setCategory(String v) { this.category = v; }
    public String getDescription() { return description; }
    public void setDescription(String v) { this.description = v; }
    public String getPriority() { return priority; }
    public void setPriority(String v) { this.priority = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime v) { this.updatedAt = v; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String v) { this.studentName = v; }
    public String getUsn() { return usn; }
    public void setUsn(String v) { this.usn = v; }
}
