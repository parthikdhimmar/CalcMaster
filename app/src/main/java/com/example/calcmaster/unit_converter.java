package com.example.calcmaster;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.getkeepsafe.taptargetview.TapTargetView;

import java.util.Locale;

public class unit_converter extends AppCompatActivity {

    private LinearLayout linearLength, linearArea, linearVolume, linearSpeed,
            linearWeight, linearTemperature, linearTime;
    private boolean isConverting = false;

    private int selectedButtonId;  // Add this line at the top of your class


    private AppCompatButton[] unitButtons;
    private int selectedButtonIndex = -1;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit_converter);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.background));
        }


        // Initialize all LinearLayouts
        linearLength = findViewById(R.id.linear_length);
        linearArea = findViewById(R.id.linear_area);
        linearVolume = findViewById(R.id.linear_volume);
        linearSpeed = findViewById(R.id.linear_speed);
        linearWeight = findViewById(R.id.linear_weight);
        linearTemperature = findViewById(R.id.linear_temperature);
        linearTime = findViewById(R.id.linear_time);

        // Set up spinners for each category
        setupLengthSpinners();
        setupWeightSpinners();
        setupTemperatureSpinners();
        setupVolumeSpinners();
        setupAreaSpinners();
        setupSpeedSpinners();
        setupTimeSpinners();

        updateTheme();

        // Set click listeners for all category buttons
        findViewById(R.id.Length_btn).setOnClickListener(v -> showConversionSection(linearLength));
        findViewById(R.id.Area_btn).setOnClickListener(v -> showConversionSection(linearArea));
        findViewById(R.id.Volume_btn).setOnClickListener(v -> showConversionSection(linearVolume));
        findViewById(R.id.Speed_btn).setOnClickListener(v -> showConversionSection(linearSpeed));
        findViewById(R.id.Weight_btn).setOnClickListener(v -> showConversionSection(linearWeight));
        findViewById(R.id.Temperature_btn).setOnClickListener(v -> showConversionSection(linearTemperature));
        findViewById(R.id.Time_btn).setOnClickListener(v -> showConversionSection(linearTime));

        // Set up bidirectional conversion listeners
        setupBidirectionalConversion();



        // Initialize all unit buttons
        unitButtons = new AppCompatButton[]{
                findViewById(R.id.Length_btn),
                findViewById(R.id.Area_btn),
                findViewById(R.id.Volume_btn),
                findViewById(R.id.Speed_btn),
                findViewById(R.id.Weight_btn),
                findViewById(R.id.Time_btn),
                findViewById(R.id.Temperature_btn)
        };

        prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        boolean isFirstUnitConverter = prefs.getBoolean("isFirstUnitConverter", true);
        if (isFirstUnitConverter) {
            int outerCircleColor;
            switch (ThemeManager.getCurrentTheme(this)) {
                case ThemeManager.THEME_BLUE:
                    outerCircleColor = R.color.background_blue_TT;
                    break;
                case ThemeManager.THEME_GREEN:
                    outerCircleColor = R.color.background_green_TT;
                    break;
                default:
                    outerCircleColor = R.color.background_TT;
                    break;
            }
            TapTargetView.showFor(this,
                    TapTarget.forView(findViewById(R.id.Length_btn), "Length Converter", "Start with length conversion")
                            .outerCircleColor(outerCircleColor)
                            .titleTextColor(android.R.color.white)
                            .descriptionTextColor(android.R.color.white)
                            .titleTypeface(ResourcesCompat.getFont(this, R.font.caudex)) // Replace with your font
                            .descriptionTypeface(ResourcesCompat.getFont(this, R.font.caudex)) // Replace with your font
                            .dimColor(android.R.color.black)
                            .transparentTarget(true)
                            .drawShadow(true)
                            .titleTextSize(20)
                            .descriptionTextSize(16)
                            .targetRadius(40)
                            .cancelable(false),
                    new TapTargetView.Listener() {
                        @Override
                        public void onTargetClick(TapTargetView view) {
                            super.onTargetClick(view);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean("isFirstUnitConverter", false);
                            editor.apply();
                        }
                    });
        }

// Replace the existing click listeners for category buttons in onCreate
        findViewById(R.id.Length_btn).setOnClickListener(v -> {
            showConversionSectionWithAnimation(linearLength);
            updateButtonBackground(0);
            showCategoryTapTargets("length", R.id.fromLength, R.id.enter_from_length);
        });
        findViewById(R.id.Area_btn).setOnClickListener(v -> {
            showConversionSectionWithAnimation(linearArea);
            updateButtonBackground(1);
            showCategoryTapTargets("area", R.id.fromArea, R.id.enter_from_area);
        });
        findViewById(R.id.Volume_btn).setOnClickListener(v -> {
            showConversionSectionWithAnimation(linearVolume);
            updateButtonBackground(2);
            showCategoryTapTargets("volume", R.id.fromVolume, R.id.enter_from_volume);
        });
        findViewById(R.id.Speed_btn).setOnClickListener(v -> {
            showConversionSectionWithAnimation(linearSpeed);
            updateButtonBackground(3);
            showCategoryTapTargets("speed", R.id.fromSpeed, R.id.enter_from_speed);
        });
        findViewById(R.id.Weight_btn).setOnClickListener(v -> {
            showConversionSectionWithAnimation(linearWeight);
            updateButtonBackground(4);
            showCategoryTapTargets("weight", R.id.fromWeight, R.id.enter_from_weight);
        });
        findViewById(R.id.Temperature_btn).setOnClickListener(v -> {
            showConversionSectionWithAnimation(linearTemperature);
            updateButtonBackground(6);
            showCategoryTapTargets("temperature", R.id.fromTemperature, R.id.enter_from_temperature);
        });
        findViewById(R.id.Time_btn).setOnClickListener(v -> {
            showConversionSectionWithAnimation(linearTime);
            updateButtonBackground(5);
            showCategoryTapTargets("time", R.id.fromTime, R.id.enter_from_time);
        });


        // Scroll to center the navigation items initially
        HorizontalScrollView navScroll = findViewById(R.id.nav_scroll_page3);
        navScroll.post(new Runnable() {
            @Override
            public void run() {
                // Calculate the scroll position to center the "Maths Game" item
                TextView mathsGame = findViewById(R.id.maths_question_activity_page3);
                int scrollTo = (mathsGame.getLeft() - (navScroll.getWidth() / 1)) + (mathsGame.getWidth() / 1);
                navScroll.smoothScrollTo(scrollTo, 0);
            }
        });

    }


    private void showCategoryTapTargets(String category, int fromSpinnerId, int fromInputId) {
        boolean isFirstCategory = prefs.getBoolean("isFirstCategoryTutorial", true);
        if (isFirstCategory) {
            int outerCircleColor;
            switch (ThemeManager.getCurrentTheme(this)) {
                case ThemeManager.THEME_BLUE:
                    outerCircleColor = R.color.background_blue_TT;
                    break;
                case ThemeManager.THEME_GREEN:
                    outerCircleColor = R.color.background_green_TT;
                    break;
                default:
                    outerCircleColor = R.color.background_TT;
                    break;
            }
            new TapTargetSequence(this)
                    .targets(
                            TapTarget.forView(findViewById(fromSpinnerId), "From Unit", "Select the source unit")
                                    .outerCircleColor(outerCircleColor)
                                    .titleTextColor(android.R.color.white)
                                    .descriptionTextColor(android.R.color.white)
                                    .titleTypeface(ResourcesCompat.getFont(this, R.font.caudex))
                                    .descriptionTypeface(ResourcesCompat.getFont(this, R.font.caudex))
                                    .dimColor(android.R.color.black)
                                    .transparentTarget(true)
                                    .drawShadow(true)
                                    .titleTextSize(20)
                                    .descriptionTextSize(16)
                                    .targetRadius(20)
                                    .cancelable(false),
                            TapTarget.forView(findViewById(fromInputId), "Input Value", "Enter the value to convert")
                                    .outerCircleColor(outerCircleColor)
                                    .titleTextColor(android.R.color.white)
                                    .descriptionTextColor(android.R.color.white)
                                    .titleTypeface(ResourcesCompat.getFont(this, R.font.caudex))
                                    .descriptionTypeface(ResourcesCompat.getFont(this, R.font.caudex))
                                    .dimColor(android.R.color.black)
                                    .transparentTarget(true)
                                    .drawShadow(true)
                                    .titleTextSize(20)
                                    .descriptionTextSize(16)
                                    .targetRadius(20)
                                    .cancelable(false)
                    )
                    .listener(new TapTargetSequence.Listener() {
                        @Override
                        public void onSequenceFinish() {
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean("isFirstCategoryTutorial", false);
                            editor.apply();
                        }

                        @Override
                        public void onSequenceStep(TapTarget last, boolean targetActivated) {}

                        @Override
                        public void onSequenceCanceled(TapTarget last) {}
                    })
                    .start();
        }
    }
    private void showConversionSection(LinearLayout sectionToShow) {
        // Hide all sections first
        linearLength.setVisibility(View.GONE);
        linearArea.setVisibility(View.GONE);
        linearVolume.setVisibility(View.GONE);
        linearSpeed.setVisibility(View.GONE);
        linearWeight.setVisibility(View.GONE);
        linearTemperature.setVisibility(View.GONE);
        linearTime.setVisibility(View.GONE);

        // Show the selected section
        sectionToShow.setVisibility(View.VISIBLE);
    }

    private void setupBidirectionalConversion() {
        // Length conversion
        setupConversionPair(
                findViewById(R.id.enter_from_length),
                findViewById(R.id.enter_to_length),
                findViewById(R.id.fromLength),
                findViewById(R.id.toLength),
                "length"
        );

        // Weight conversion
        setupConversionPair(
                findViewById(R.id.enter_from_weight),
                findViewById(R.id.enter_to_weight),
                findViewById(R.id.fromWeight),
                findViewById(R.id.toWeight),
                "weight"
        );

        // Temperature conversion
        setupConversionPair(
                findViewById(R.id.enter_from_temperature),
                findViewById(R.id.enter_to_temprature),
                findViewById(R.id.fromTemperature),
                findViewById(R.id.toTemperature),
                "temperature"
        );

        // Volume conversion
        setupConversionPair(
                findViewById(R.id.enter_from_volume),
                findViewById(R.id.enter_to_volume),
                findViewById(R.id.fromVolume),
                findViewById(R.id.toVolume),
                "volume"
        );

        // Area conversion
        setupConversionPair(
                findViewById(R.id.enter_from_area),
                findViewById(R.id.enter_to_area),
                findViewById(R.id.fromArea),
                findViewById(R.id.toArea),
                "area"
        );

        // Speed conversion
        setupConversionPair(
                findViewById(R.id.enter_from_speed),
                findViewById(R.id.enter_to_speed),
                findViewById(R.id.fromSpeed),
                findViewById(R.id.toSpeed),
                "speed"
        );

        // Time conversion
        setupConversionPair(
                findViewById(R.id.enter_from_time),
                findViewById(R.id.enter_to_time),
                findViewById(R.id.fromTime),
                findViewById(R.id.toTime),
                "time"
        );


        TextView calculator_page3 = findViewById(R.id.calculator_page3);

        calculator_page3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(unit_converter.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        TextView currency_converter_activity_page3 = findViewById(R.id.currency_converter_activity_page3);

        currency_converter_activity_page3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(unit_converter.this, currency_converter.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });


        TextView maths_question_activity_page3 = findViewById(R.id.maths_question_activity_page3);

        maths_question_activity_page3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(unit_converter.this, maths_question.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        TextView rating_activity_btn_page3 = findViewById(R.id.rating_activity_btn_page3);

        rating_activity_btn_page3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(unit_converter.this, rating_feedback.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    // Add this method to unit_converter.java
    private void updateTheme() {
        int currentTheme = ThemeManager.getCurrentTheme(this);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            switch (currentTheme) {
                case ThemeManager.THEME_YELLOW:
                    window.setStatusBarColor(ContextCompat.getColor(this, R.color.background));
                    window.setNavigationBarColor(ContextCompat.getColor(this, R.color.background));
                    break;
                case ThemeManager.THEME_BLUE:
                    window.setStatusBarColor(ContextCompat.getColor(this, R.color.background_blue));
                    window.setNavigationBarColor(ContextCompat.getColor(this, R.color.background_blue));
                    break;
                case ThemeManager.THEME_GREEN:
                    window.setStatusBarColor(ContextCompat.getColor(this, R.color.background_green));
                    window.setNavigationBarColor(ContextCompat.getColor(this, R.color.background_green));
                    break;
            }
        }


        // Set background colors
        ConstraintLayout mainLayout = findViewById(R.id.main);
        CardView contentCard = findViewById(R.id.content_card);
        TextView textView_nav3 = findViewById(R.id.unit_converter_activity_page3);

        Spinner fromLength = findViewById(R.id.fromLength);
        Spinner toLength = findViewById(R.id.toLength);
        EditText enter_from_length = findViewById(R.id.enter_from_length);
        EditText enter_to_length = findViewById(R.id.enter_to_length);

        Spinner fromArea = findViewById(R.id.fromArea);
        Spinner toArea = findViewById(R.id.toArea);
        EditText enter_from_area = findViewById(R.id.enter_from_area);
        EditText enter_to_area = findViewById(R.id.enter_to_area);

        Spinner fromVolume = findViewById(R.id.fromVolume);
        Spinner toVolume = findViewById(R.id.toVolume);
        EditText enter_from_volume = findViewById(R.id.enter_from_volume);
        EditText enter_to_volume = findViewById(R.id.enter_to_volume);

        Spinner fromSpeed = findViewById(R.id.fromSpeed);
        Spinner toSpeed = findViewById(R.id.toSpeed);
        EditText enter_from_speed = findViewById(R.id.enter_from_speed);
        EditText enter_to_speed = findViewById(R.id.enter_to_speed);

        Spinner fromWeight = findViewById(R.id.fromWeight);
        Spinner toWeight = findViewById(R.id.toWeight);
        EditText enter_from_weight = findViewById(R.id.enter_from_weight);
        EditText enter_to_weight = findViewById(R.id.enter_to_weight);

        Spinner fromTemperature = findViewById(R.id.fromTemperature);
        Spinner toTemperature = findViewById(R.id.toTemperature);
        EditText enter_from_temperature = findViewById(R.id.enter_from_temperature);
        EditText enter_to_temprature = findViewById(R.id.enter_to_temprature);

        Spinner fromTime = findViewById(R.id.fromTime);
        Spinner toTime = findViewById(R.id.toTime);
        EditText enter_from_time = findViewById(R.id.enter_from_time);
        EditText enter_to_time = findViewById(R.id.enter_to_time);


        // Update unit buttons
        AppCompatButton[] unitButtons = {
                findViewById(R.id.Length_btn),
                findViewById(R.id.Area_btn),
                findViewById(R.id.Volume_btn),
                findViewById(R.id.Speed_btn),
                findViewById(R.id.Weight_btn),
                findViewById(R.id.Temperature_btn),
                findViewById(R.id.Time_btn)
        };



        int buttonActionRes = ThemeManager.getButtonActionResource(this);
        int buttonActionSelectedRes = ThemeManager.getButtonActionSelectedResource(this);

        for (AppCompatButton button : unitButtons) {
            if (button.getId() == selectedButtonId) {
                button.setBackgroundResource(buttonActionSelectedRes);
            } else {
                button.setBackgroundResource(buttonActionRes);
            }
        }

        // Update text colors (txt0 to txt6)
        int[] textViewIds = {R.id.txt0, R.id.txt1, R.id.txt2, R.id.txt3, R.id.txt4, R.id.txt5, R.id.txt6};
        for (int id : textViewIds) {
            TextView textView = findViewById(id);
            switch (currentTheme) {
                case ThemeManager.THEME_BLUE:
                    textView.setTextColor(ContextCompat.getColor(this, R.color.accent_blue));
                    break;
                case ThemeManager.THEME_GREEN:
                    textView.setTextColor(ContextCompat.getColor(this, R.color.accent_green));
                    break;
                default:
                    textView.setTextColor(ContextCompat.getColor(this, R.color.accent));
                    break;
            }
        }

        // Update main layout and card background
        switch (currentTheme) {
            case ThemeManager.THEME_BLUE:
                mainLayout.setBackgroundResource(R.color.background_blue);
                contentCard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.card_background_blue));
                textView_nav3.setBackgroundResource(R.drawable.tab_background2_blue);

                fromLength.setBackgroundResource(R.drawable.input_background_blue);
                toLength.setBackgroundResource(R.drawable.input_background_blue);
                enter_from_length.setBackgroundResource(R.drawable.input_background_blue);
                enter_to_length.setTextColor(ContextCompat.getColor(this, R.color.accent_blue));
                enter_to_length.setBackgroundResource(R.drawable.input_background_blue);

                // Area
                fromArea.setBackgroundResource(R.drawable.input_background_blue);
                toArea.setBackgroundResource(R.drawable.input_background_blue);
                enter_from_area.setBackgroundResource(R.drawable.input_background_blue);
                enter_to_area.setBackgroundResource(R.drawable.input_background_blue);
                enter_to_area.setTextColor(ContextCompat.getColor(this, R.color.accent_blue));

                fromVolume.setBackgroundResource(R.drawable.input_background_blue);
                toVolume.setBackgroundResource(R.drawable.input_background_blue);
                enter_from_volume.setBackgroundResource(R.drawable.input_background_blue);
                enter_to_volume.setBackgroundResource(R.drawable.input_background_blue);
                enter_to_volume.setTextColor(ContextCompat.getColor(this, R.color.accent_blue));

                fromSpeed.setBackgroundResource(R.drawable.input_background_blue);
                toSpeed.setBackgroundResource(R.drawable.input_background_blue);
                enter_from_speed.setBackgroundResource(R.drawable.input_background_blue);
                enter_to_speed.setBackgroundResource(R.drawable.input_background_blue);
                enter_to_speed.setTextColor(ContextCompat.getColor(this, R.color.accent_blue));

                fromWeight.setBackgroundResource(R.drawable.input_background_blue);
                toWeight.setBackgroundResource(R.drawable.input_background_blue);
                enter_from_weight.setBackgroundResource(R.drawable.input_background_blue);
                enter_to_weight.setBackgroundResource(R.drawable.input_background_blue);
                enter_to_weight.setTextColor(ContextCompat.getColor(this, R.color.accent_blue));

                fromTemperature.setBackgroundResource(R.drawable.input_background_blue);
                toTemperature.setBackgroundResource(R.drawable.input_background_blue);
                enter_from_temperature.setBackgroundResource(R.drawable.input_background_blue);
                enter_to_temprature.setBackgroundResource(R.drawable.input_background_blue);
                enter_to_temprature.setTextColor(ContextCompat.getColor(this, R.color.accent_blue));

                fromTime.setBackgroundResource(R.drawable.input_background_blue);
                toTime.setBackgroundResource(R.drawable.input_background_blue);
                enter_from_time.setBackgroundResource(R.drawable.input_background_blue);
                enter_to_time.setBackgroundResource(R.drawable.input_background_blue);
                enter_to_time.setTextColor(ContextCompat.getColor(this, R.color.accent_blue));


                break;
            case ThemeManager.THEME_GREEN:
                mainLayout.setBackgroundResource(R.color.background_green);
                contentCard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.card_background_green));
                textView_nav3.setBackgroundResource(R.drawable.tab_background2_green);

                fromLength.setBackgroundResource(R.drawable.input_background_green);
                toLength.setBackgroundResource(R.drawable.input_background_green);
                enter_from_length.setBackgroundResource(R.drawable.input_background_green);
                enter_to_length.setTextColor(ContextCompat.getColor(this, R.color.accent_green));
                enter_to_length.setBackgroundResource(R.drawable.input_background_green);

                fromArea.setBackgroundResource(R.drawable.input_background_green);
                toArea.setBackgroundResource(R.drawable.input_background_green);
                enter_from_area.setBackgroundResource(R.drawable.input_background_green);
                enter_to_area.setBackgroundResource(R.drawable.input_background_green);
                enter_to_area.setTextColor(ContextCompat.getColor(this, R.color.accent_green));

                fromVolume.setBackgroundResource(R.drawable.input_background_green);
                toVolume.setBackgroundResource(R.drawable.input_background_green);
                enter_from_volume.setBackgroundResource(R.drawable.input_background_green);
                enter_to_volume.setBackgroundResource(R.drawable.input_background_green);
                enter_to_volume.setTextColor(ContextCompat.getColor(this, R.color.accent_green));

                fromSpeed.setBackgroundResource(R.drawable.input_background_green);
                toSpeed.setBackgroundResource(R.drawable.input_background_green);
                enter_from_speed.setBackgroundResource(R.drawable.input_background_green);
                enter_to_speed.setBackgroundResource(R.drawable.input_background_green);
                enter_to_speed.setTextColor(ContextCompat.getColor(this, R.color.accent_green));

                fromWeight.setBackgroundResource(R.drawable.input_background_green);
                toWeight.setBackgroundResource(R.drawable.input_background_green);
                enter_from_weight.setBackgroundResource(R.drawable.input_background_green);
                enter_to_weight.setBackgroundResource(R.drawable.input_background_green);
                enter_to_weight.setTextColor(ContextCompat.getColor(this, R.color.accent_green));

                fromTemperature.setBackgroundResource(R.drawable.input_background_green);
                toTemperature.setBackgroundResource(R.drawable.input_background_green);
                enter_from_temperature.setBackgroundResource(R.drawable.input_background_green);
                enter_to_temprature.setBackgroundResource(R.drawable.input_background_green);
                enter_to_temprature.setTextColor(ContextCompat.getColor(this, R.color.accent_green));

                fromTime.setBackgroundResource(R.drawable.input_background_green);
                toTime.setBackgroundResource(R.drawable.input_background_green);
                enter_from_time.setBackgroundResource(R.drawable.input_background_green);
                enter_to_time.setBackgroundResource(R.drawable.input_background_green);
                enter_to_time.setTextColor(ContextCompat.getColor(this, R.color.accent_green));


                break;
            default:
                mainLayout.setBackgroundResource(R.color.background);
                contentCard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.card_background));
                textView_nav3.setBackgroundResource(R.drawable.tab_background2);

                fromLength.setBackgroundResource(R.drawable.input_background);
                toLength.setBackgroundResource(R.drawable.input_background);
                enter_from_length.setBackgroundResource(R.drawable.input_background);
                enter_to_length.setTextColor(ContextCompat.getColor(this, R.color.accent));
                enter_to_length.setBackgroundResource(R.drawable.input_background);


                fromArea.setBackgroundResource(R.drawable.input_background);
                toArea.setBackgroundResource(R.drawable.input_background);
                enter_from_area.setBackgroundResource(R.drawable.input_background);
                enter_to_area.setBackgroundResource(R.drawable.input_background);
                enter_to_area.setTextColor(ContextCompat.getColor(this, R.color.accent));

                fromVolume.setBackgroundResource(R.drawable.input_background);
                toVolume.setBackgroundResource(R.drawable.input_background);
                enter_from_volume.setBackgroundResource(R.drawable.input_background);
                enter_to_volume.setBackgroundResource(R.drawable.input_background);
                enter_to_volume.setTextColor(ContextCompat.getColor(this, R.color.accent));

                fromSpeed.setBackgroundResource(R.drawable.input_background);
                toSpeed.setBackgroundResource(R.drawable.input_background);
                enter_from_speed.setBackgroundResource(R.drawable.input_background);
                enter_to_speed.setBackgroundResource(R.drawable.input_background);
                enter_to_speed.setTextColor(ContextCompat.getColor(this, R.color.accent));

                fromWeight.setBackgroundResource(R.drawable.input_background);
                toWeight.setBackgroundResource(R.drawable.input_background);
                enter_from_weight.setBackgroundResource(R.drawable.input_background);
                enter_to_weight.setBackgroundResource(R.drawable.input_background);
                enter_to_weight.setTextColor(ContextCompat.getColor(this, R.color.accent));

                fromTemperature.setBackgroundResource(R.drawable.input_background);
                toTemperature.setBackgroundResource(R.drawable.input_background);
                enter_from_temperature.setBackgroundResource(R.drawable.input_background);
                enter_to_temprature.setBackgroundResource(R.drawable.input_background);
                enter_to_temprature.setTextColor(ContextCompat.getColor(this, R.color.accent));

                fromTime.setBackgroundResource(R.drawable.input_background);
                toTime.setBackgroundResource(R.drawable.input_background);
                enter_from_time.setBackgroundResource(R.drawable.input_background);
                enter_to_time.setBackgroundResource(R.drawable.input_background);
                enter_to_time.setTextColor(ContextCompat.getColor(this, R.color.accent));


                break;
        }
    }


    private void showConversionSectionWithAnimation(LinearLayout sectionToShow) {
        // Hide all sections first with slide-out animation
        slideOutAnimation(linearLength);
        slideOutAnimation(linearArea);
        slideOutAnimation(linearVolume);
        slideOutAnimation(linearSpeed);
        slideOutAnimation(linearWeight);
        slideOutAnimation(linearTemperature);
        slideOutAnimation(linearTime);

        // Show the selected section with slide-in animation
        if (sectionToShow.getVisibility() != View.VISIBLE) {
            slideInAnimation(sectionToShow);
        }
    }

    private void slideInAnimation(View view) {
        view.setVisibility(View.VISIBLE);
        Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        view.startAnimation(slideIn);
    }

    private void slideOutAnimation(View view) {
        if (view.getVisibility() == View.VISIBLE) {
            Animation slideOut = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);
            slideOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    view.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            view.startAnimation(slideOut);
        }
    }



    private void updateButtonBackground(int buttonIndex) {
        // Get theme-specific resources
        int buttonActionRes = ThemeManager.getButtonActionResource(this);
        int buttonActionSelectedRes = ThemeManager.getButtonActionSelectedResource(this);

        // Reset all buttons to default background
        for (AppCompatButton button : unitButtons) {
            button.setBackgroundResource(buttonActionRes);
        }

        // Set selected button to highlighted background
        if (buttonIndex >= 0 && buttonIndex < unitButtons.length) {
            unitButtons[buttonIndex].setBackgroundResource(buttonActionSelectedRes);
            selectedButtonIndex = buttonIndex;
            selectedButtonId = unitButtons[buttonIndex].getId();
        }
    }
    private void setupConversionPair(EditText fromEditText, EditText toEditText,
                                     Spinner fromSpinner, Spinner toSpinner, String conversionType) {
        // From -> To conversion
        fromEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isConverting) return;
                isConverting = true;

                try {
                    String fromUnit = fromSpinner.getSelectedItem().toString();
                    String toUnit = toSpinner.getSelectedItem().toString();
                    String inputStr = s.toString().trim();

                    if (inputStr.isEmpty()) {
                        toEditText.setText("");
                        isConverting = false;
                        return;
                    }

                    double inputValue = Double.parseDouble(inputStr);
                    double result = convertValue(inputValue, fromUnit, toUnit, conversionType);
                    toEditText.setText(formatNumber(result));
                } catch (Exception e) {
                    toEditText.setText("");
                } finally {
                    isConverting = false;
                }
            }
        });

        // To -> From conversion
        toEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isConverting) return;
                isConverting = true;

                try {
                    String inputStr = s.toString().trim();

                    if (inputStr.isEmpty()) {
                        fromEditText.setText("");
                        return;
                    }

                    if (inputStr.equals(".")) {
                        fromEditText.setText("0");
                        return;
                    }

                    String fromUnit = fromSpinner.getSelectedItem().toString();
                    String toUnit = toSpinner.getSelectedItem().toString();
                    double inputValue = Double.parseDouble(inputStr);

                    double result = convertValue(inputValue, toUnit, fromUnit, conversionType);
                    fromEditText.setText(formatNumber(result));

                } catch (NumberFormatException e) {
                    fromEditText.setText("");
                } finally {
                    isConverting = false;
                }
            }
        });

        // Spinner change listeners
        fromSpinner.setOnItemSelectedListener(new SpinnerChangeListener(fromEditText, toEditText, fromSpinner, toSpinner, conversionType));
        toSpinner.setOnItemSelectedListener(new SpinnerChangeListener(fromEditText, toEditText, fromSpinner, toSpinner, conversionType));
    }

    // Single class-level number formatting method
    private String formatNumber(double value) {
        if (Double.isNaN(value)) return "";

        if (value == (long) value) {
            return String.format(Locale.US, "%d", (long) value);
        }

        String formatted = String.format(Locale.US, "%.6f", value);
        return formatted.replaceAll("0*$", "").replaceAll("\\.$", "");
    }

    private double convertValue(double inputValue, String fromUnit, String toUnit, String conversionType) {
        switch (conversionType) {
            case "length":
                return convertLength(inputValue, fromUnit, toUnit);
            case "weight":
                return convertWeight(inputValue, fromUnit, toUnit);
            case "temperature":
                return convertTemperature(inputValue, fromUnit, toUnit);
            case "volume":
                return convertVolume(inputValue, fromUnit, toUnit);
            case "area":
                return convertArea(inputValue, fromUnit, toUnit);
            case "speed":
                return convertSpeed(inputValue, fromUnit, toUnit);
            case "time":
                return convertTime(inputValue, fromUnit, toUnit);
            default:
                return 0;
        }
    }

    // Conversion methods (same as before)
    private double convertLength(double inputValue, String fromUnit, String toUnit) {
        double meters = 0;
        switch (fromUnit) {
            case "Kilometer (km)":
                meters = inputValue * 1000;
                break;
            case "Millimeter (mm)":
                meters = inputValue / 1000;
                break;
            case "Centimeter (cm)":
                meters = inputValue / 100;
                break;
            case "Meter (m)":
                meters = inputValue;
                break;
            case "Inch (in)":
                meters = inputValue * 0.0254;
                break;
            case "Foot (ft)":
                meters = inputValue * 0.3048;
                break;
            case "Yard (yd)":
                meters = inputValue * 0.9144;
                break;
        }

        switch (toUnit) {
            case "Meter (m)":
                return meters;
            case "Centimeter (cm)":
                return meters * 100;
            case "Millimeter (mm)":
                return meters * 1000;
            case "Kilometer (km)":
                return meters / 1000;
            case "Inch (in)":
                return meters / 0.0254;
            case "Foot (ft)":
                return meters / 0.3048;
            case "Yard (yd)":
                return meters / 0.9144;
            default:
                return 0;
        }
    }

    private double convertWeight(double inputValue, String fromUnit, String toUnit) {
        double grams = 0;
        switch (fromUnit) {
            case "Kilogram (kg)":
                grams = inputValue * 1000;
                break;
            case "Milligram (mg)":
                grams = inputValue / 1000;
                break;
            case "Gram (g)":
                grams = inputValue;
                break;
            case "Tonne (t)":
                grams = inputValue * 1000000;
                break;
            case "Ounce (oz)":
                grams = inputValue * 28.3495;
                break;
        }

        switch (toUnit) {
            case "Gram (g)":
                return grams;
            case "Milligram (mg)":
                return grams * 1000;
            case "Kilogram (kg)":
                return grams / 1000;
            case "Tonne (t)":
                return grams / 1000000;
            case "Ounce (oz)":
                return grams / 28.3495;
            default:
                return 0;
        }
    }

    private double convertTemperature(double inputValue, String fromUnit, String toUnit) {
        double celsius = 0;
        switch (fromUnit) {
            case "Celsius (°C)":
                celsius = inputValue;
                break;
            case "Fahrenheit (°F)":
                celsius = (inputValue - 32) * 5 / 9;
                break;
            case "Kelvin (K)":
                celsius = inputValue - 273.15;
                break;
        }

        switch (toUnit) {
            case "Fahrenheit (°F)":
                return (celsius * 9 / 5) + 32;
            case "Celsius (°C)":
                return celsius;
            case "Kelvin (K)":
                return celsius + 273.15;
            default:
                return 0;
        }
    }

    private double convertVolume(double inputValue, String fromUnit, String toUnit) {
        double liters = 0;
        switch (fromUnit) {
            case "Liters (L)":
                liters = inputValue;
                break;
            case "Milliliters (mL)":
                liters = inputValue / 1000;
                break;
            case "Gallons (gal)":
                liters = inputValue * 3.78541;
                break;
            case "Cubic meters (m³)":
                liters = inputValue * 1000;
                break;
            case "Fluid ounces (fl oz)":
                liters = inputValue * 0.0295735;
                break;
        }

        switch (toUnit) {
            case "Milliliters (mL)":
                return liters * 1000;
            case "Liters (L)":
                return liters;
            case "Gallons (gal)":
                return liters / 3.78541;
            case "Cubic meters (m³)":
                return liters / 1000;
            case "Fluid ounces (fl oz)":
                return liters / 0.0295735;
            default:
                return 0;
        }
    }

    private double convertArea(double inputValue, String fromUnit, String toUnit) {
        double squareMeters = 0;
        switch (fromUnit) {
            case "Square meters (m²)":
                squareMeters = inputValue;
                break;
            case "Square kilometers (km²)":
                squareMeters = inputValue * 1000000;
                break;
            case "Square feet (ft²)":
                squareMeters = inputValue * 0.092903;
                break;
            case "Acres":
                squareMeters = inputValue * 4046.86;
                break;
            case "Hectares":
                squareMeters = inputValue * 10000;
                break;
        }

        switch (toUnit) {
            case "Square kilometers (km²)":
                return squareMeters / 1000000;
            case "Square meters (m²)":
                return squareMeters;
            case "Square feet (ft²)":
                return squareMeters / 0.092903;
            case "Acres":
                return squareMeters / 4046.86;
            case "Hectares":
                return squareMeters / 10000;
            default:
                return 0;
        }
    }

    private double convertSpeed(double inputValue, String fromUnit, String toUnit) {
        double metersPerSecond = 0;
        switch (fromUnit) {
            case "Meters per second (m/s)":
                metersPerSecond = inputValue;
                break;
            case "Kilometers per hour (km/h)":
                metersPerSecond = inputValue / 3.6;
                break;
            case "Miles per hour (mph)":
                metersPerSecond = inputValue * 0.44704;
                break;
            case "Knots":
                metersPerSecond = inputValue * 0.514444;
                break;
        }

        switch (toUnit) {
            case "Kilometers per hour (km/h)":
                return metersPerSecond * 3.6;
            case "Meters per second (m/s)":
                return metersPerSecond;
            case "Miles per hour (mph)":
                return metersPerSecond / 0.44704;
            case "Knots":
                return metersPerSecond / 0.514444;
            default:
                return 0;
        }
    }

    private double convertTime(double inputValue, String fromUnit, String toUnit) {
        double seconds = 0;
        switch (fromUnit) {
            case "Seconds (s)":
                seconds = inputValue;
                break;
            case "Minutes (min)":
                seconds = inputValue * 60;
                break;
            case "Hours (hr)":
                seconds = inputValue * 3600;
                break;
            case "Days":
                seconds = inputValue * 86400;
                break;
            case "Weeks":
                seconds = inputValue * 604800;
                break;
        }

        switch (toUnit) {
            case "Minutes (min)":
                return seconds / 60;
            case "Seconds (s)":
                return seconds;
            case "Hours (hr)":
                return seconds / 3600;
            case "Days":
                return seconds / 86400;
            case "Weeks":
                return seconds / 604800;
            default:
                return 0;
        }
    }

    // Spinner setup methods (same as before)
    private void setupLengthSpinners() {
        Spinner fromLength = findViewById(R.id.fromLength);
        Spinner toLength = findViewById(R.id.toLength);

        String[] lengthUnits = {
                "Meter (m)", "Millimeter (mm)", "Centimeter (cm)",
                "Kilometer (km)", "Inch (in)", "Foot (ft)", "Yard (yd)"
        };

        // Determine dropdown item layout based on theme
        int dropdownItemLayout;
        switch (ThemeManager.getCurrentTheme(this)) {
            case ThemeManager.THEME_BLUE:
                dropdownItemLayout = R.layout.spinner_dropdown_item_blue;
                break;
            case ThemeManager.THEME_GREEN:
                dropdownItemLayout = R.layout.spinner_dropdown_item_green;
                break;
            case ThemeManager.THEME_YELLOW:
            default:
                dropdownItemLayout = R.layout.spinner_dropdown_item;
                break;
        }

        // Set up Spinner adapter with themed dropdown item
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item, // main selected item layout
                lengthUnits
        );
        adapter.setDropDownViewResource(dropdownItemLayout);

        fromLength.setAdapter(adapter);
        toLength.setAdapter(adapter);

        // Optional: Set dropdown popup background if needed
        int dropdownBackground;
        switch (ThemeManager.getCurrentTheme(this)) {
            case ThemeManager.THEME_BLUE:
                dropdownBackground = R.drawable.dropdown_background_blue;
                break;
            case ThemeManager.THEME_GREEN:
                dropdownBackground = R.drawable.dropdown_background_green;
                break;
            case ThemeManager.THEME_YELLOW:
            default:
                dropdownBackground = R.drawable.dropdown_background;
                break;
        }

        fromLength.setPopupBackgroundResource(dropdownBackground);
        toLength.setPopupBackgroundResource(dropdownBackground);


        // Set default value
        EditText enterFromLength = findViewById(R.id.enter_from_length);
        enterFromLength.setText("1000");  // Set as string directly

        // Set default spinner selections
        int defaultFromPos = adapter.getPosition("Millimeter (mm)");
        int defaultToPos = adapter.getPosition("Meter (m)");
        if (defaultFromPos >= 0) fromLength.setSelection(defaultFromPos);
        if (defaultToPos >= 0) toLength.setSelection(defaultToPos);

        // Perform initial conversion
        String fromUnit = fromLength.getSelectedItem().toString();
        String toUnit = toLength.getSelectedItem().toString();
        double result = convertLength(1000, fromUnit, toUnit);
        EditText enterToLength = findViewById(R.id.enter_to_length);
        enterToLength.setText(formatNumber(result));
    }

    private void setupWeightSpinners() {
        Spinner fromWeight = findViewById(R.id.fromWeight);
        Spinner toWeight = findViewById(R.id.toWeight);

        String[] weightUnits = {
                "Milligram (mg)", "Gram (g)", "Kilogram (kg)",
                "Tonne (t)", "Ounce (oz)"
        };

        // 1. Select dropdown item layout based on current theme
        int dropdownLayout;
        switch (ThemeManager.getCurrentTheme(this)) {
            case ThemeManager.THEME_BLUE:
                dropdownLayout = R.layout.spinner_dropdown_item_blue;
                break;
            case ThemeManager.THEME_GREEN:
                dropdownLayout = R.layout.spinner_dropdown_item_green;
                break;
            case ThemeManager.THEME_YELLOW:
            default:
                dropdownLayout = R.layout.spinner_dropdown_item;
                break;
        }

// 2. Create adapter with themed dropdown item layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.spinner_item, // Selected item view
                weightUnits);
        adapter.setDropDownViewResource(dropdownLayout);

// 3. Assign adapter to both spinners
        fromWeight.setAdapter(adapter);
        toWeight.setAdapter(adapter);

// 4. Optional: Set dropdown popup background
        int popupBackground;
        switch (ThemeManager.getCurrentTheme(this)) {
            case ThemeManager.THEME_BLUE:
                popupBackground = R.drawable.dropdown_background_blue;
                break;
            case ThemeManager.THEME_GREEN:
                popupBackground = R.drawable.dropdown_background_green;
                break;
            case ThemeManager.THEME_YELLOW:
            default:
                popupBackground = R.drawable.dropdown_background;
                break;
        }

        fromWeight.setPopupBackgroundResource(popupBackground);
        toWeight.setPopupBackgroundResource(popupBackground);


        EditText enterFromLength = findViewById(R.id.enter_from_weight);
        enterFromLength.setText("1");  // Set as string directly

        // Set default spinner selections
        int defaultFromPos = adapter.getPosition("Kilogram (kg)");
        int defaultToPos = adapter.getPosition("Gram (g)");
        if (defaultFromPos >= 0) fromWeight.setSelection(defaultFromPos);
        if (defaultToPos >= 0) toWeight.setSelection(defaultToPos);

        // Perform initial conversion
        String fromUnit = fromWeight.getSelectedItem().toString();
        String toUnit = toWeight.getSelectedItem().toString();
        double result = convertLength(1, fromUnit, toUnit);
        EditText enterToLength = findViewById(R.id.enter_to_weight);
        enterToLength.setText(formatNumber(result));
    }

    private void setupTemperatureSpinners() {
        Spinner fromTemperature = findViewById(R.id.fromTemperature);
        Spinner toTemperature = findViewById(R.id.toTemperature);

        String[] temperatureUnits = {
                "Celsius (°C)", "Fahrenheit (°F)", "Kelvin (K)"
        };

        // 1. Select dropdown item layout based on current theme
        int dropdownLayout;
        switch (ThemeManager.getCurrentTheme(this)) {
            case ThemeManager.THEME_BLUE:
                dropdownLayout = R.layout.spinner_dropdown_item_blue;
                break;
            case ThemeManager.THEME_GREEN:
                dropdownLayout = R.layout.spinner_dropdown_item_green;
                break;
            case ThemeManager.THEME_YELLOW:
            default:
                dropdownLayout = R.layout.spinner_dropdown_item;
                break;
        }

// 2. Create adapter with themed dropdown item layout for temperature units
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.spinner_item, // Selected item view
                temperatureUnits); // List of temperature units (e.g., Celsius, Fahrenheit)
        adapter.setDropDownViewResource(dropdownLayout);

// 3. Set the adapter to both spinners (fromTemperature and toTemperature)
        fromTemperature.setAdapter(adapter);
        toTemperature.setAdapter(adapter);

// 4. Optional: Set the dropdown background resource to match the theme
        int popupBackground;
        switch (ThemeManager.getCurrentTheme(this)) {
            case ThemeManager.THEME_BLUE:
                popupBackground = R.drawable.dropdown_background_blue;
                break;
            case ThemeManager.THEME_GREEN:
                popupBackground = R.drawable.dropdown_background_green;
                break;
            case ThemeManager.THEME_YELLOW:
            default:
                popupBackground = R.drawable.dropdown_background;
                break;
        }

        fromTemperature.setPopupBackgroundResource(popupBackground);
        toTemperature.setPopupBackgroundResource(popupBackground);

        EditText enterFromLength = findViewById(R.id.enter_from_temperature);
        enterFromLength.setText("1");  // Set as string directly

        // Set default spinner selections
        int defaultFromPos = adapter.getPosition("Celsius (°C)");
        int defaultToPos = adapter.getPosition("Fahrenheit (°F)");
        if (defaultFromPos >= 0) fromTemperature.setSelection(defaultFromPos);
        if (defaultToPos >= 0) toTemperature.setSelection(defaultToPos);

        // Perform initial conversion
        String fromUnit = fromTemperature.getSelectedItem().toString();
        String toUnit = toTemperature.getSelectedItem().toString();
        double result = convertLength(1, fromUnit, toUnit);
        EditText enterToLength = findViewById(R.id.enter_to_temprature);
        enterToLength.setText(formatNumber(result));
    }

    private void setupVolumeSpinners() {
        Spinner fromVolume = findViewById(R.id.fromVolume);
        Spinner toVolume = findViewById(R.id.toVolume);

        String[] volumeUnits = {
                "Liters (L)", "Milliliters (mL)", "Gallons (gal)",
                "Cubic meters (m³)", "Fluid ounces (fl oz)"
        };

        // 1. Select dropdown item layout based on the current theme
        int dropdownLayout;
        switch (ThemeManager.getCurrentTheme(this)) {
            case ThemeManager.THEME_BLUE:
                dropdownLayout = R.layout.spinner_dropdown_item_blue;
                break;
            case ThemeManager.THEME_GREEN:
                dropdownLayout = R.layout.spinner_dropdown_item_green;
                break;
            case ThemeManager.THEME_YELLOW:
            default:
                dropdownLayout = R.layout.spinner_dropdown_item;
                break;
        }

// 2. Create adapter with themed dropdown item layout for volume units
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.spinner_item, // Selected item view layout
                volumeUnits); // List of volume units (e.g., Liters, Gallons, etc.)
        adapter.setDropDownViewResource(dropdownLayout); // Use the themed dropdown layout

// 3. Set the adapter to both spinners (fromVolume and toVolume)
        fromVolume.setAdapter(adapter);
        toVolume.setAdapter(adapter);

// 4. Optional: Set the dropdown background resource to match the theme
        int popupBackground;
        switch (ThemeManager.getCurrentTheme(this)) {
            case ThemeManager.THEME_BLUE:
                popupBackground = R.drawable.dropdown_background_blue;
                break;
            case ThemeManager.THEME_GREEN:
                popupBackground = R.drawable.dropdown_background_green;
                break;
            case ThemeManager.THEME_YELLOW:
            default:
                popupBackground = R.drawable.dropdown_background;
                break;
        }

        fromVolume.setPopupBackgroundResource(popupBackground);
        toVolume.setPopupBackgroundResource(popupBackground);


        EditText enterFromLength = findViewById(R.id.enter_from_volume);
        enterFromLength.setText("1");  // Set as string directly

        // Set default spinner selections
        int defaultFromPos = adapter.getPosition("Cubic meters (m³)");
        int defaultToPos = adapter.getPosition("Liters (L)");
        if (defaultFromPos >= 0) fromVolume.setSelection(defaultFromPos);
        if (defaultToPos >= 0) toVolume.setSelection(defaultToPos);

        // Perform initial conversion
        String fromUnit = fromVolume.getSelectedItem().toString();
        String toUnit = toVolume.getSelectedItem().toString();
        double result = convertLength(1, fromUnit, toUnit);
        EditText enterToLength = findViewById(R.id.enter_to_volume);
        enterToLength.setText(formatNumber(result));


    }

    private void setupAreaSpinners() {
        Spinner fromArea = findViewById(R.id.fromArea);
        Spinner toArea = findViewById(R.id.toArea);

        String[] areaUnits = {
                "Square meters (m²)", "Square kilometers (km²)",
                "Square feet (ft²)", "Acres", "Hectares"
        };

        // 1. Select dropdown item layout based on the current theme
        int dropdownLayout;
        switch (ThemeManager.getCurrentTheme(this)) {
            case ThemeManager.THEME_BLUE:
                dropdownLayout = R.layout.spinner_dropdown_item_blue; // Blue theme dropdown item layout
                break;
            case ThemeManager.THEME_GREEN:
                dropdownLayout = R.layout.spinner_dropdown_item_green; // Green theme dropdown item layout
                break;
            case ThemeManager.THEME_YELLOW:
            default:
                dropdownLayout = R.layout.spinner_dropdown_item; // Default (Yellow) theme dropdown item layout
                break;
        }

// 2. Create adapter with the selected dropdown item layout for area units
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.spinner_item, // Layout for the selected item
                areaUnits); // List of area units (e.g., Square meters, Square kilometers, etc.)
        adapter.setDropDownViewResource(dropdownLayout); // Set the dropdown item layout based on theme

// 3. Set the adapter to both spinners (fromArea and toArea)
        fromArea.setAdapter(adapter);
        toArea.setAdapter(adapter);

// 4. Optional: Set the dropdown background resource to match the theme
        int popupBackground;
        switch (ThemeManager.getCurrentTheme(this)) {
            case ThemeManager.THEME_BLUE:
                popupBackground = R.drawable.dropdown_background_blue; // Blue theme background
                break;
            case ThemeManager.THEME_GREEN:
                popupBackground = R.drawable.dropdown_background_green; // Green theme background
                break;
            case ThemeManager.THEME_YELLOW:
            default:
                popupBackground = R.drawable.dropdown_background; // Default (Yellow) theme background
                break;
        }

        fromArea.setPopupBackgroundResource(popupBackground);
        toArea.setPopupBackgroundResource(popupBackground);


        EditText enterFromLength = findViewById(R.id.enter_from_area);
        enterFromLength.setText("10000");  // Set as string directly

        // Set default spinner selections
        int defaultFromPos = adapter.getPosition("Square meters (m²)");
        int defaultToPos = adapter.getPosition("Hectares");
        if (defaultFromPos >= 0) fromArea.setSelection(defaultFromPos);
        if (defaultToPos >= 0) toArea.setSelection(defaultToPos);

        // Perform initial conversion
        String fromUnit = fromArea.getSelectedItem().toString();
        String toUnit = toArea.getSelectedItem().toString();
        double result = convertLength(1, fromUnit, toUnit);
        EditText enterToLength = findViewById(R.id.enter_to_area);
        enterToLength.setText(formatNumber(result));

    }

    private void setupSpeedSpinners() {
        Spinner fromSpeed = findViewById(R.id.fromSpeed);
        Spinner toSpeed = findViewById(R.id.toSpeed);

        String[] speedUnits = {
                "Meters per second (m/s)", "Kilometers per hour (km/h)",
                "Miles per hour (mph)", "Knots"
        };

        // 1. Select dropdown item layout based on the current theme
        int dropdownLayout;
        switch (ThemeManager.getCurrentTheme(this)) {
            case ThemeManager.THEME_BLUE:
                dropdownLayout = R.layout.spinner_dropdown_item_blue; // Blue theme dropdown item layout
                break;
            case ThemeManager.THEME_GREEN:
                dropdownLayout = R.layout.spinner_dropdown_item_green; // Green theme dropdown item layout
                break;
            case ThemeManager.THEME_YELLOW:
            default:
                dropdownLayout = R.layout.spinner_dropdown_item; // Default (Yellow) theme dropdown item layout
                break;
        }

// 2. Create adapter with the selected dropdown item layout for speed units
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.spinner_item, // Layout for the selected item
                speedUnits); // List of speed units (e.g., km/h, m/s, mph, etc.)
        adapter.setDropDownViewResource(dropdownLayout); // Set the dropdown item layout based on theme

// 3. Set the adapter to both spinners (fromSpeed and toSpeed)
        fromSpeed.setAdapter(adapter);
        toSpeed.setAdapter(adapter);

// 4. Optional: Set the dropdown background resource to match the theme
        int popupBackground;
        switch (ThemeManager.getCurrentTheme(this)) {
            case ThemeManager.THEME_BLUE:
                popupBackground = R.drawable.dropdown_background_blue; // Blue theme background
                break;
            case ThemeManager.THEME_GREEN:
                popupBackground = R.drawable.dropdown_background_green; // Green theme background
                break;
            case ThemeManager.THEME_YELLOW:
            default:
                popupBackground = R.drawable.dropdown_background; // Default (Yellow) theme background
                break;
        }

        fromSpeed.setPopupBackgroundResource(popupBackground);
        toSpeed.setPopupBackgroundResource(popupBackground);


        EditText enterFromLength = findViewById(R.id.enter_from_speed);
        enterFromLength.setText("1");  // Set as string directly

        // Set default spinner selections
        int defaultFromPos = adapter.getPosition("Miles per hour (mph)");
        int defaultToPos = adapter.getPosition("Kilometers per hour (km/h)");
        if (defaultFromPos >= 0) fromSpeed.setSelection(defaultFromPos);
        if (defaultToPos >= 0) toSpeed.setSelection(defaultToPos);

        // Perform initial conversion
        String fromUnit = fromSpeed.getSelectedItem().toString();
        String toUnit = toSpeed.getSelectedItem().toString();
        double result = convertLength(1, fromUnit, toUnit);
        EditText enterToLength = findViewById(R.id.enter_to_speed);
        enterToLength.setText(formatNumber(result));
    }

    private void setupTimeSpinners() {
        Spinner fromTime = findViewById(R.id.fromTime);
        Spinner toTime = findViewById(R.id.toTime);

        String[] timeUnits = {
                "Seconds (s)", "Minutes (min)", "Hours (hr)",
                "Days", "Weeks"
        };

        // 1. Select dropdown item layout based on the current theme
        int dropdownLayout;
        switch (ThemeManager.getCurrentTheme(this)) {
            case ThemeManager.THEME_BLUE:
                dropdownLayout = R.layout.spinner_dropdown_item_blue; // Blue theme dropdown item layout
                break;
            case ThemeManager.THEME_GREEN:
                dropdownLayout = R.layout.spinner_dropdown_item_green; // Green theme dropdown item layout
                break;
            case ThemeManager.THEME_YELLOW:
            default:
                dropdownLayout = R.layout.spinner_dropdown_item; // Default (Yellow) theme dropdown item layout
                break;
        }

// 2. Create the ArrayAdapter using the selected dropdown layout for time units
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.spinner_item, // Layout for the selected item
                timeUnits); // List of time units (e.g., seconds, minutes, hours)
        adapter.setDropDownViewResource(dropdownLayout); // Set the dropdown item layout based on the theme

// 3. Set the adapter to both spinners (fromTime and toTime)
        fromTime.setAdapter(adapter);
        toTime.setAdapter(adapter);

// 4. Optional: Set the dropdown background resource to match the current theme
        int popupBackground;
        switch (ThemeManager.getCurrentTheme(this)) {
            case ThemeManager.THEME_BLUE:
                popupBackground = R.drawable.dropdown_background_blue; // Blue theme background
                break;
            case ThemeManager.THEME_GREEN:
                popupBackground = R.drawable.dropdown_background_green; // Green theme background
                break;
            case ThemeManager.THEME_YELLOW:
            default:
                popupBackground = R.drawable.dropdown_background; // Default (Yellow) theme background
                break;
        }

        fromTime.setPopupBackgroundResource(popupBackground);
        toTime.setPopupBackgroundResource(popupBackground);

        EditText enterFromLength = findViewById(R.id.enter_from_time);
        enterFromLength.setText("1");  // Set as string directly

        // Set default spinner selections
        int defaultFromPos = adapter.getPosition("Minutes (min)");
        int defaultToPos = adapter.getPosition("Seconds (s)");
        if (defaultFromPos >= 0) fromTime.setSelection(defaultFromPos);
        if (defaultToPos >= 0) toTime.setSelection(defaultToPos);

        // Perform initial conversion
        String fromUnit = fromTime.getSelectedItem().toString();
        String toUnit = toTime.getSelectedItem().toString();
        double result = convertLength(1, fromUnit, toUnit);
        EditText enterToLength = findViewById(R.id.enter_to_time);
        enterToLength.setText(formatNumber(result));

    }

    private class SpinnerChangeListener implements android.widget.AdapterView.OnItemSelectedListener {
        private EditText fromEditText;
        private EditText toEditText;
        private Spinner fromSpinner;
        private Spinner toSpinner;
        private String conversionType;

        public SpinnerChangeListener(EditText fromEditText, EditText toEditText,
                                     Spinner fromSpinner, Spinner toSpinner, String conversionType) {
            this.fromEditText = fromEditText;
            this.toEditText = toEditText;
            this.fromSpinner = fromSpinner;
            this.toSpinner = toSpinner;
            this.conversionType = conversionType;
        }

        @Override
        public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
            String text = fromEditText.getText().toString().trim();
            if (!text.isEmpty()) {
                try {
                    String fromUnit = fromSpinner.getSelectedItem().toString();
                    String toUnit = toSpinner.getSelectedItem().toString();
                    double inputValue = Double.parseDouble(text);
                    double result = convertValue(inputValue, fromUnit, toUnit, conversionType);
                    toEditText.setText(formatNumber(result));  // Use our unified formatter
                } catch (Exception e) {
                    toEditText.setText("");
                }
            }
        }

        @Override
        public void onNothingSelected(android.widget.AdapterView<?> parent) {
        }
    }
}