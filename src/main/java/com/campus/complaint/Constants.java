package com.campus.complaint;

import java.util.List;

public final class Constants {
    private Constants() {}

    public static final List<String> CATEGORIES = List.of(
            "Classroom", "Laboratory", "Computer Systems", "Internet / Wi-Fi",
            "Sanitation", "Infrastructure", "Other");
    public static final List<String> STATUSES = List.of("Pending", "In Progress", "Resolved");
    public static final List<String> PRIORITIES = List.of("Low", "Medium", "High");
    public static final List<String> DEPARTMENTS = List.of(
            "Computer Science", "Information Science", "Electronics", "Mechanical", "Civil", "Electrical");
}
