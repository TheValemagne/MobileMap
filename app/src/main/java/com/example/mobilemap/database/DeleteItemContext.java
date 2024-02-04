package com.example.mobilemap.database;

import android.net.Uri;

public class DeleteItemContext {
    public Uri getDatabaseUri() {
        return databaseUri;
    }

    public int getDialogTitleId() {
        return dialogTitleId;
    }

    public int getDialogMsgId() {
        return dialogMsgId;
    }

    private final Uri databaseUri;
    private final int dialogTitleId;
    private final int dialogMsgId;

    public DeleteItemContext(Uri databaseUri, int dialogTitleId, int dialogMsgId) {
        this.databaseUri = databaseUri;
        this.dialogTitleId = dialogTitleId;
        this.dialogMsgId = dialogMsgId;
    }
}
