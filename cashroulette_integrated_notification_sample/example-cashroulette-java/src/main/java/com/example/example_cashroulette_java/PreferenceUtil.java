package com.example.example_cashroulette_java;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtil {
    private static final String PREFERENCE_NAME = "notification_prefs";
    private Context context;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public PreferenceUtil(final Context context) {
        this.context = context;
        this.prefs = this.context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        this.editor = prefs.edit();
    }

    public Boolean getChecked(final String key, final Boolean defValue) {
        return prefs.getBoolean(key, defValue);

    }

    public void setChecked(final String key, final Boolean value) {
        editor.putBoolean(key, value).apply();
    }
}
