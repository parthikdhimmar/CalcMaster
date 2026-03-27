package com.example.calcmaster;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import java.util.Locale;

public class CalculatorWidgetProvider extends AppWidgetProvider {
    private static final String ACTION_BUTTON_CLICK = "com.example.calcmaster.BUTTON_CLICK";
    private static final String EXTRA_BUTTON_VALUE = "button_value";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, String displayText, int layoutId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), layoutId);

        // Ensure text is visible
        views.setTextViewText(R.id.widget_display, displayText);
        views.setViewVisibility(R.id.widget_display, View.VISIBLE);

        // Re-set all button click handlers
        setButtonClickHandlers(context, views, appWidgetId);

        // Force update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static void setButtonClickHandlers(Context context, RemoteViews views, int appWidgetId) {
        // Numbers
        setButtonClick(context, views, R.id.widget_0, "0", appWidgetId);
        setButtonClick(context, views, R.id.widget_1, "1", appWidgetId);
        setButtonClick(context, views, R.id.widget_2, "2", appWidgetId);
        setButtonClick(context, views, R.id.widget_3, "3", appWidgetId);
        setButtonClick(context, views, R.id.widget_4, "4", appWidgetId);
        setButtonClick(context, views, R.id.widget_5, "5", appWidgetId);
        setButtonClick(context, views, R.id.widget_6, "6", appWidgetId);
        setButtonClick(context, views, R.id.widget_7, "7", appWidgetId);
        setButtonClick(context, views, R.id.widget_8, "8", appWidgetId);
        setButtonClick(context, views, R.id.widget_9, "9", appWidgetId);
        setButtonClick(context, views, R.id.widget_00, "00", appWidgetId);

        // Operators
        setButtonClick(context, views, R.id.widget_add, "+", appWidgetId);
        setButtonClick(context, views, R.id.widget_subtract, "-", appWidgetId);
        setButtonClick(context, views, R.id.widget_multiply, "×", appWidgetId);
        setButtonClick(context, views, R.id.widget_divide, "÷", appWidgetId);
        setButtonClick(context, views, R.id.widget_dot, ".", appWidgetId);
        setButtonClick(context, views, R.id.widget_equals, "=", appWidgetId);
        setButtonClick(context, views, R.id.widget_clear, "C", appWidgetId);
        setButtonClick(context, views, R.id.widget_backspace, "⌫", appWidgetId);
    }

    private static void setButtonClick(Context context, RemoteViews views,
                                       int buttonId, String value, int appWidgetId) {
        Intent intent = new Intent(context, CalculatorWidgetProvider.class);
        intent.setAction(ACTION_BUTTON_CLICK);
        intent.putExtra(EXTRA_BUTTON_VALUE, value);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, buttonId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        views.setOnClickPendingIntent(buttonId, pendingIntent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            SharedPreferences prefs = context.getSharedPreferences("WidgetPrefs", Context.MODE_PRIVATE);
            String currentText = prefs.getString("widget_display_" + appWidgetId, "0");

            // Start with the small layout by default
            updateAppWidget(context, appWidgetManager, appWidgetId, currentText, R.layout.widget_calculator);
        }
    }
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {


        // Restore the current display text
        SharedPreferences prefs = context.getSharedPreferences("WidgetPrefs", Context.MODE_PRIVATE);
        String currentText = prefs.getString("widget_display_"+appWidgetId, "0");

        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.d("WidgetDebug", "Received intent: " + intent.getAction());

        if (ACTION_BUTTON_CLICK.equals(intent.getAction())) {
            String buttonValue = intent.getStringExtra(EXTRA_BUTTON_VALUE);
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            Log.d("WidgetDebug", "Button clicked: " + buttonValue + " for widget " + appWidgetId);

            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                handleButtonClick(context, buttonValue, appWidgetId);
            }
        }
    }

    private void handleButtonClick(Context context, String buttonValue, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences("WidgetPrefs", Context.MODE_PRIVATE);
        String currentText = prefs.getString("widget_display_"+appWidgetId, "0");
        String newText = currentText;

        if (buttonValue.matches("[0-9]")) {
            // If current text is "0" or ends with operator, replace/start new number
            if (currentText.equals("0") || currentText.matches(".*[+\\-×÷]$")) {
                newText = currentText.equals("0") ? buttonValue : currentText + buttonValue;
            } else {
                newText = currentText + buttonValue;
            }
        } else if (buttonValue.equals("00")) {
            if (currentText.equals("0")) {
                newText = "0";
            } else if (currentText.matches(".*[+\\-×÷]$")) {
                newText = currentText + "0"; // Don't allow "00" after operator
            } else {
                newText = currentText + "00";
            }
        } else if (buttonValue.equals("C")) {
            newText = "0";
        } else if (buttonValue.equals("⌫")) {
            newText = currentText.length() > 1 ?
                    currentText.substring(0, currentText.length()-1) : "0";
        } else if (buttonValue.equals("=")) {
            try {
                double result = eval(currentText.replace("×", "*").replace("÷", "/"));
                newText = formatResult(result);
            } catch (Exception e) {
                newText = "Error";
            }
        } else {
            // Handle operators (+, -, ×, ÷, .)
            if (!currentText.matches(".*[+\\-×÷]$")) {
                newText = currentText + buttonValue;
            }
        }


        prefs.edit().putString("widget_display_"+appWidgetId, newText).apply();

        // After updating text, determine current size and layout
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        int minWidthPx = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        int minHeightPx = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);

        // Convert to dp
        float density = context.getResources().getDisplayMetrics().density;
        int widthDp = (int)(minWidthPx / density);
        int heightDp = (int)(minHeightPx / density);

        // Choose layout based on exact size thresholds
        int layoutId;
        if (widthDp >= 200 && heightDp >= 200) {
            layoutId = R.layout.widget_calculator;
        } else {
            layoutId = R.layout.widget_calculator;
        }

        updateAppWidget(context, appWidgetManager, appWidgetId, newText, layoutId);
    }

    private static double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                return x;
            }
        }.parse();
    }

    private static String formatResult(double result) {
        if (result == (long) result) {
            return String.valueOf((long) result);
        } else {
            String formatted = String.format(Locale.getDefault(), "%.10f", result);
            formatted = formatted.replaceAll("0*$", "");
            formatted = formatted.replaceAll("\\.$", "");
            return formatted;
        }
    }
}