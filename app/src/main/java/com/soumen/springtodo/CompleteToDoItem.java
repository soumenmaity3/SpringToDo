package com.soumen.springtodo;

public class CompleteToDoItem {
    private String title;
    private String description;

    public CompleteToDoItem(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }
    public String getDescription() { return description; }
}

