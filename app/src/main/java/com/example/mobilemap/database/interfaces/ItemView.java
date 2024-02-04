package com.example.mobilemap.database.interfaces;

import com.example.mobilemap.database.table.DatabaseItem;

public interface ItemView<T extends DatabaseItem> {
    boolean check();

    T getValues();
}
