package com.example.dayin;

import java.time.LocalDateTime;

public class Project {
    private int id;
    private String name;
    private int category_id;
    private int status_id;
    private String goal;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Project() {
    }

    public Project(int id, String name, int category_id, int status_id, String goal,
                   LocalDateTime startTime, LocalDateTime endTime) {
        this.id = id;
        this.name = name;
        this.category_id = category_id;
        this.status_id = status_id;
        this.goal = goal;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public int getStatus_id() {
        return status_id;
    }

    public void setStatus_id(int status_id) {
        this.status_id = status_id;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

}
