package com.campus.complaint.model;

public class Admin {
    private Long adminId;
    private String username;
    private String passwordHash;

    public Long getAdminId() { return adminId; }
    public void setAdminId(Long v) { this.adminId = v; }
    public String getUsername() { return username; }
    public void setUsername(String v) { this.username = v; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String v) { this.passwordHash = v; }
}
