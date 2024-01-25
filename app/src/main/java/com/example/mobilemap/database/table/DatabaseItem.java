package com.example.mobilemap.database.table;

public abstract class DatabaseItem {
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    protected long id;

    public DatabaseItem(long _ID) {
        this.id = _ID;
    }
}