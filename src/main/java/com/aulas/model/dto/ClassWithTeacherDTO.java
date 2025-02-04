package com.aulas.model.dto;

import java.util.Date;

public class ClassWithTeacherDTO {
    private Long id;
    private String title;
    private String description;
    private String place;
    private Date endDate;
    private Date startDate;
    private String teacherName;

    // Constructor matching the query
    public ClassWithTeacherDTO(Long id, String title, String description, String place, Date startDate, Date endDate,
            String teacherName) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.place = place;
        this.startDate = startDate;
        this.endDate = endDate;
        this.teacherName = teacherName;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }
}
