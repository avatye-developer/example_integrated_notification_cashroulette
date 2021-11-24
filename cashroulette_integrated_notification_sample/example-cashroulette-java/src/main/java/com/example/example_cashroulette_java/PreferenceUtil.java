package com.example.example_cashroulette_java;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtil {
    private Context context;
    private SharedPreferences prefs = context.getSharedPreferences("notibar_prefs", Context.MODE_PRIVATE);

    public PreferenceUtil(final Context context) {
        this.context = context;
    }

    public Boolean getChecked(final String key, final Boolean defValue) {
        return prefs.getBoolean(key, defValue);
    }

    public void setChecked(final String key, final Boolean value) {
        prefs.edit().putBoolean(key, value);
    }
}
