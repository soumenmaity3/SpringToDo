package com.soumen.springtodo;

public class CompleteToDoItemModel {
    private String title;
    private String description;

    public CompleteToDoItemModel(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }
    public String getDescription() { return description; }
}

