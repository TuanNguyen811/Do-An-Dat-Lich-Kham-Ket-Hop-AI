package com.example.app1.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Department implements Serializable {
    @SerializedName("department_id")
    private int department_id;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("avatar_url")
    private String avatar_url;

    public Department() {

    }

    public Department(int department_id, String name, String description, String avatar_url) {
        this.department_id = department_id;
        this.name = name;
        this.description = description;
        this.avatar_url = avatar_url;
    }

    public int getDepartment_id() {
        return department_id;
    }

    public void setDepartment_id(int department_id) {
        this.department_id = department_id;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public int getId() {
        return department_id;
    }

    public void setId(int id) {
        this.department_id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
