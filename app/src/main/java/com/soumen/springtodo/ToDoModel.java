package com.soumen.springtodo;

import java.io.Serializable;

public class ToDoModel implements Serializable {
    private String title;
    private String task;
    private String email;
    private boolean complete;
    int id;

    public ToDoModel(String title, String task, boolean complete) {
        this.title = title;
        this.task = task;
        this.complete = complete;
    }

//    public ToDoModel(String title, String task, String email, boolean complete) {
//        this.title = title;
//        this.task = task;
//        this.email = email;
//        this.complete = complete;
//    }

    public boolean isComplete() {return complete;}
    public void setComplete(boolean complete) {this.complete = complete;}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
