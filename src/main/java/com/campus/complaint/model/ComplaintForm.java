package com.campus.complaint.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ComplaintForm {

    @NotBlank(message = "Please select a category")
    private String category;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 1000, message = "Description must be 10-1000 characters")
    private String description;

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
