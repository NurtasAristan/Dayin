package com.example.dayin;

public class Task {
    private int id;
    private int project_id;
    private String goal;

    public Task(int id, int project_id, String goal) {
        this.id = id;
        this.project_id = project_id;
        this.goal = goal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProject_id() {
        return project_id;
    }

    public void setProject_id(int project_id) {
        this.project_id = project_id;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }
}
