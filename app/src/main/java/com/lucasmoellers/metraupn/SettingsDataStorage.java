package com.lucasmoellers.metraupn;

import android.content.Context;

public class SettingsDataStorage extends ObjectDataStorage<Settings> {
    public SettingsDataStorage(Context context) {
        super(context);
    }

    public String getFileName() {
        return "settings";
    }
}
