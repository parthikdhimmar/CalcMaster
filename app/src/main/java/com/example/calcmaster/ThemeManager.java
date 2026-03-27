package com.example.calcmaster;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

public class ThemeManager {
    public static final String THEME_PREF = "theme_prefs";
    public static final String THEME_KEY = "current_theme";
    public static final int THEME_YELLOW = 0;
    public static final int THEME_BLUE = 1;
    public static final int THEME_GREEN = 2;

    public static void applyTheme(AppCompatActivity activity) {
        SharedPreferences prefs = activity.getSharedPreferences(THEME_PREF, Context.MODE_PRIVATE);
        int theme = getCurrentTheme(activity);
        setTheme(activity, theme, false);
    }

    public static void setTheme(AppCompatActivity activity, int theme, boolean restart) {
        SharedPreferences prefs = activity.getSharedPreferences(THEME_PREF, Context.MODE_PRIVATE);
        prefs.edit().putInt(THEME_KEY, theme).apply();

        if (restart) {
            activity.recreate();
        }
    }

    public static int getCurrentTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(THEME_PREF, Context.MODE_PRIVATE);
        try {
            return prefs.getInt(THEME_KEY, THEME_YELLOW);
        } catch (ClassCastException e) {
            String themeString = prefs.getString(THEME_KEY, String.valueOf(THEME_YELLOW));
            try {
                return Integer.parseInt(themeString);
            } catch (NumberFormatException ex) {
                return THEME_YELLOW;
            }
        }
    }

    public static int getSpinnerDropdownItemResource(Context context) {
        int theme = getCurrentTheme(context);
        switch (theme) {
            case THEME_BLUE:
                return R.layout.spinner_dropdown_item_blue;
            case THEME_GREEN:
                return R.layout.spinner_dropdown_item_green;
            default:
                return R.layout.spinner_dropdown_item;
        }
    }

    public static int getDropdownBackgroundResource(Context context) {
        int theme = getCurrentTheme(context);
        switch (theme) {
            case THEME_BLUE:
                return R.drawable.dropdown_background_blue;
            case THEME_GREEN:
                return R.drawable.dropdown_background_green;
            default:
                return R.drawable.dropdown_background;
        }
    }

    public static int getButtonActionResource(Context context) {
        int theme = getCurrentTheme(context);
        switch (theme) {
            case THEME_BLUE:
                return R.drawable.button_action_blue;
            case THEME_GREEN:
                return R.drawable.button_action_green;
            default:
                return R.drawable.button_action;
        }
    }



    public static int getButtonActionSelectedResource(Context context) {
        int theme = getCurrentTheme(context);
        switch (theme) {
            case THEME_BLUE:
                return R.drawable.button_action_selected_blue;
            case THEME_GREEN:
                return R.drawable.button_action_selected_green;
            default:
                return R.drawable.button_action_selected;
        }
    }
    public static int getMathsGameBackgroundResource(Context context) {
        switch (getCurrentTheme(context)) {
            case THEME_BLUE:
                return R.color.background_blue;
            case THEME_GREEN:
                return R.color.background_green;
            case THEME_YELLOW:
            default:
                return R.color.background;
        }
    }

    public static int getMathsGameCardBackgroundResource(Context context) {
        switch (getCurrentTheme(context)) {
            case THEME_BLUE:
                return R.color.card_background_blue;
            case THEME_GREEN:
                return R.color.card_background_green;
            case THEME_YELLOW:
            default:
                return R.color.card_background;
        }
    }



    public static int getMathsGameAccentColor(Context context) {
        switch (getCurrentTheme(context)) {
            case THEME_BLUE:
                return R.color.accent_blue;
            case THEME_GREEN:
                return R.color.accent_green;
            case THEME_YELLOW:
            default:
                return R.color.accent;
        }
    }
}