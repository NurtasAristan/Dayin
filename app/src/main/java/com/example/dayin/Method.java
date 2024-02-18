package com.example.dayin;

public class Method {
    private int id;
    private String name;
    private int category_id;
    private String description;
    private String gpt;

    public Method() {
    }

    public Method(int id, String name, int category_id, String description) {
        this.id = id;
        this.name = name;
        this.category_id = category_id;
        this.description = description;
    }

    public Method(int id, String name, int category_id, String description, String gpt) {
        this.id = id;
        this.name = name;
        this.category_id = category_id;
        this.description = description;
        this.gpt = gpt;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGpt() {
        return gpt;
    }

    public void setGpt(String gpt) {
        this.gpt = gpt;
    }
}
