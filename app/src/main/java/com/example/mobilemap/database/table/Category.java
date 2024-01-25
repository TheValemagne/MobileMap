package com.example.mobilemap.database.table;

import androidx.annotation.NonNull;

import java.text.MessageFormat;

public class Category extends DatabaseItem {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category(long id, String name) {
        super(id);
        this.name = name;
    }

    public Category(String name) {
        this(-1, name);
    }

    @NonNull
    @Override
    public String toString() {
        return MessageFormat.format("Category(_id={0}, name={1})", id, name);
    }
}
