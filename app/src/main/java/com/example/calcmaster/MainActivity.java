package com.example.calcmaster;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.getkeepsafe.taptargetview.TapTargetView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    private static final int SPEECH_REQUEST_CODE = 100;
    private static final String VOICE_OUTPUT_KEY = "voice_output_key";
    private static final String THEME_PREFERENCE = "theme_preference";
    private static final String THEME_KEY = "theme_key";

    private static final String STOP_VOICE_PREF = "stop_voice_pref";

    private static final String STOP_VOICE_COLOR_KEY = "stop_voice_color";
    private EditText input;
    private TextView output;
    private String currentInput = "";
    private boolean isHindiEnabled = false;
    private boolean isGujaratiEnabled = false;
    private DatabaseHelper dbHelper;
    private TextToSpeech textToSpeech;
    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private CalculationAdapter adapter;
    private ImageButton menuClickBtn;

    private TextView stopVoiceButton;
    private CardView optionLayout;
    private boolean isOptionLayoutVisible = false;
    private boolean lastCharIsOperator = false;
    private SpeechRecognizer speechRecognizer;
    private boolean isVoiceOutputEnabled = true; // Default to true (voice output enabled)
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeManager.applyTheme(this);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("voice_prefs", Context.MODE_PRIVATE);
        isVoiceOutputEnabled = sharedPreferences.getBoolean(VOICE_OUTPUT_KEY, true);





        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.background));
        }



        input = findViewById(R.id.input);
        output = findViewById(R.id.output);


        stopVoiceButton = findViewById(R.id.stop_voice_btn);
        menuClickBtn = findViewById(R.id.menu_click_btn);
        optionLayout = findViewById(R.id.option_layout);

        dbHelper = new DatabaseHelper(this);

        // Initialize DrawerLayout and RecyclerView
        SharedPreferences prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        boolean isFirstDrawerOpen = prefs.getBoolean("isFirstDrawerOpen", true);
        boolean isFirstOptionLayoutOpen = prefs.getBoolean("isFirstOptionLayoutOpen", true);
        drawerLayout = findViewById(R.id.drawer_layout);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        // Load history data
        loadHistory();
        setupThemeButton();
        updateTheme();

        SharedPreferences voicePrefs = getSharedPreferences(STOP_VOICE_PREF, Context.MODE_PRIVATE);
        boolean isVoiceStopped = voicePrefs.getBoolean(STOP_VOICE_COLOR_KEY, false);
        updateStopVoiceButtonColor(isVoiceStopped);
        isVoiceOutputEnabled = sharedPreferences.getBoolean(VOICE_OUTPUT_KEY, true); // Default to true


        int[] numberButtonIds = {R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btn10};

        for (int id : numberButtonIds) {
            findViewById(id).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Button button = (Button) v;
                    String buttonText = button.getText().toString();
                    String englishNumber = convertToEnglish(buttonText);
                    int buttonId = v.getId();

                    // Get current selection from the input EditText display
                    int selectionStart = input.getSelectionStart();
                    int selectionEnd = input.getSelectionEnd();

                    // Get the displayed text (with commas)
                    String displayText = input.getText().toString();

                    // Convert displayed text to currentInput format (without commas) for processing
                    String plainBeforeSelection = convertDisplayToPlain(displayText.substring(0, selectionStart));
                    String plainAfterSelection = convertDisplayToPlain(displayText.substring(selectionEnd));

                    // Calculate plain positions
                    int plainStart = plainBeforeSelection.length();
                    int plainEnd = plainStart + convertDisplayToPlain(displayText.substring(selectionStart, selectionEnd)).length();

                    // Handle invalid positions
                    if (plainStart < 0) plainStart = 0;
                    if (plainEnd < 0) plainEnd = 0;
                    if (plainStart > currentInput.length()) plainStart = currentInput.length();
                    if (plainEnd > currentInput.length()) plainEnd = currentInput.length();

                    String beforeSelection = currentInput.substring(0, plainStart);
                    String afterSelection = currentInput.substring(plainEnd);

                    // Handle empty input case
                    if (currentInput.isEmpty()) {
                        if (buttonId == R.id.btn0) {
                            if (currentInput.equals("")) {
                                currentInput = "0";
                                updateInputDisplay();
                                updateOutput();
                            }
                            return;
                        } else if (buttonId == R.id.btn10) {
                            currentInput = "0";
                            updateInputDisplay();
                            updateOutput();
                            return;
                        }
                        currentInput = englishNumber;
                        updateInputDisplay();
                        updateOutput();
                        // Set cursor at end
                        input.setSelection(input.getText().length());
                        return;
                    }

                    // Handle button-specific conditions
                    if (buttonId == R.id.btn0) {
                        if (currentInput.equals("0")) {
                            return;
                        }

                        boolean atStartOrAfterOperator = plainStart == 0 || beforeSelection.matches(".*[+\\-×÷(]$");

                        if (atStartOrAfterOperator) {
                            if (afterSelection.startsWith(".")) {
                                currentInput = beforeSelection + "0" + afterSelection;
                            } else {
                                currentInput = beforeSelection + "0" + afterSelection;
                            }
                        } else {
                            boolean afterValidChar = !beforeSelection.isEmpty() &&
                                    (Character.isDigit(beforeSelection.charAt(beforeSelection.length() - 1)) ||
                                            beforeSelection.charAt(beforeSelection.length() - 1) == '.');

                            if (afterValidChar) {
                                currentInput = beforeSelection + "0" + afterSelection;
                            }
                        }
                    } else if (buttonId == R.id.btn10) {
                        if (beforeSelection.isEmpty()) {
                            if (!currentInput.startsWith("0")) {
                                currentInput = "00" + afterSelection;
                            }
                            updateInputDisplay();
                            // Set cursor after the inserted "00"
                            input.setSelection(findCursorPositionInDisplay(input.getText().toString(), plainStart + 2));
                            updateOutput();
                            return;
                        }

                        boolean inMiddleOfNumber = Character.isDigit(beforeSelection.charAt(beforeSelection.length() - 1));

                        if (inMiddleOfNumber) {
                            if (!beforeSelection.matches(".*\\D0$") && !beforeSelection.matches("^0$")) {
                                currentInput = beforeSelection + "00" + afterSelection;
                            }
                        }
                    } else {
                        currentInput = beforeSelection + englishNumber + afterSelection;
                    }

                    updateInputDisplay();
                    updateOutput();
                    lastCharIsOperator = false;

                    // Set cursor position correctly after insertion
                    int newPlainPos = plainStart + englishNumber.length();
                    String newDisplayText = input.getText().toString();
                    int newDisplayPos = findCursorPositionInDisplay(newDisplayText, newPlainPos);
                    input.setSelection(newDisplayPos);
                }
            });
        }

        // Operator Buttons
        findViewById(R.id.add).setOnClickListener(v -> appendOperator("+"));
        findViewById(R.id.subtract).setOnClickListener(v -> appendOperator("-"));
        findViewById(R.id.multiply).setOnClickListener(v -> appendOperator("×"));
        findViewById(R.id.divide).setOnClickListener(v -> appendOperator("÷"));

        // Other Buttons
        findViewById(R.id.clear).setOnClickListener(v -> clear());
        findViewById(R.id.equal).setOnClickListener(v -> calculateResult());
        findViewById(R.id.backspace).setOnClickListener(v -> backspace());
        findViewById(R.id.percent).setOnClickListener(v -> handlePercentButtonClick());

        findViewById(R.id.btnDot).setOnClickListener(v -> {
            Log.d("CalcMaster", "Dot button clicked, calling handleDotButtonClick");
            handleDotButtonClick();
        });


        // History Button Click Listener
        ImageButton historyButton = findViewById(R.id.history_btn);
        historyButton.setOnClickListener(v -> {
            drawerLayout.openDrawer(findViewById(R.id.navigation_drawer)); // Open the drawer
        });


        TextView currencyPage_btn = findViewById(R.id.currencyPage_btn);


        currencyPage_btn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, currency_converter.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        TextView unit_converter_btn = findViewById(R.id.unit_converter_activity);

        unit_converter_btn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, unit_converter.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        });

        TextView maths_question_btn = findViewById(R.id.maths_question_activity);

        maths_question_btn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, maths_question.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        });

        TextView rating_converter_btn = findViewById(R.id.rating_activity_btn);

        rating_converter_btn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, rating_feedback.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        });

        boolean isFirstLaunch = prefs.getBoolean("isFirstLaunch", true);
        if (isFirstLaunch) {
            new TapTargetSequence(this)
                    .targets(
                            TapTarget.forView(findViewById(R.id.menu_click_btn), "Menu", "Access additional options here")
                                    .outerCircleColor(R.color.background_TT) // Use your theme's color
                                    .titleTextColor(android.R.color.white)
                                    .descriptionTextColor(android.R.color.white)
                                    .titleTypeface(ResourcesCompat.getFont(this, R.font.caudex)) // Replace with your font
                                    .descriptionTypeface(ResourcesCompat.getFont(this, R.font.caudex)) // Replace with your font
                                    .dimColor(android.R.color.black)
                                    .transparentTarget(true)
                                    .drawShadow(true)
                                    .titleTextSize(20)
                                    .descriptionTextSize(16)
                                    .targetRadius(20)
                                    .cancelable(false),
                            TapTarget.forView(findViewById(R.id.calculator_page1), "Calculator", "This is the main calculator tab")
                                    .outerCircleColor(R.color.background_TT)
                                    .titleTextColor(android.R.color.white)
                                    .descriptionTextColor(android.R.color.white)
                                    .titleTypeface(ResourcesCompat.getFont(this, R.font.caudex))
                                    .descriptionTypeface(ResourcesCompat.getFont(this, R.font.caudex))
                                    .dimColor(android.R.color.black)
                                    .transparentTarget(true)
                                    .drawShadow(true)
                                    .titleTextSize(20)
                                    .descriptionTextSize(16)
                                    .targetRadius(17)
                                    .cancelable(false),
                            TapTarget.forView(findViewById(R.id.history_btn), "History", "View your calculation history here")
                                    .outerCircleColor(R.color.background_TT)
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
                            TapTarget.forView(findViewById(R.id.voice_btn), "Voice Input", "Use voice to input calculations")
                                    .outerCircleColor(R.color.background_TT)
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
                            TapTarget.forView(findViewById(R.id.stop_voice_btn), "Voice Output", "Toggle voice output for results")
                                    .outerCircleColor(R.color.background_TT)
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
                            // Update the preference to mark the tutorial as shown
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean("isFirstLaunch", false);
                            editor.apply();
                        }

                        @Override
                        public void onSequenceStep(TapTarget last, boolean targetActivated) {
                            // Optional: Handle each step transition if needed
                        }

                        @Override
                        public void onSequenceCanceled(TapTarget last) {
                            // Optional: Handle cancellation if needed
                        }
                    })
                    .start();
        }


        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                if (isFirstDrawerOpen) {
                    TapTargetView.showFor(MainActivity.this,
                            TapTarget.forView(findViewById(R.id.clear_btn), "Clear History", "Clear all calculation history here")
                                    .outerCircleColor(ThemeManager.getCurrentTheme(MainActivity.this) == ThemeManager.THEME_BLUE ? R.color.background_blue_TT :
                                            ThemeManager.getCurrentTheme(MainActivity.this) == ThemeManager.THEME_GREEN ? R.color.background_green_TT : R.color.background_TT)
                                    .titleTextColor(android.R.color.white)
                                    .descriptionTextColor(android.R.color.white)
                                    .titleTypeface(ResourcesCompat.getFont(MainActivity.this, R.font.caudex))
                                    .descriptionTypeface(ResourcesCompat.getFont(MainActivity.this, R.font.caudex))
                                    .dimColor(android.R.color.black)
                                    .transparentTarget(true)
                                    .drawShadow(true)
                                    .titleTextSize(20)
                                    .descriptionTextSize(16)
                                    .targetRadius(18)
                                    .cancelable(false),
                            new TapTargetView.Listener() {
                                @Override
                                public void onTargetClick(TapTargetView view) {
                                    super.onTargetClick(view);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putBoolean("isFirstDrawerOpen", false);
                                    editor.apply();
                                }

                            });
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {}
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {}
            @Override
            public void onDrawerStateChanged(int newState) {}
        });
        EditText inputEditText = findViewById(R.id.input);

        inputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int after) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editable.toString();

                // Check the length of the input text and adjust the size accordingly
                if (text.length() > 10) {
                    inputEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);  // Smaller text size
                } else {
                    inputEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36);  // Default text size
                }
            }
        });




        ConstraintLayout rootLayout = findViewById(R.id.constraint_main_layout);

        // Set touch listener for the root layout
        rootLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isOptionLayoutVisible) {
                    // Check if the touch was outside the optionLayout
                    Rect outRect = new Rect();
                    optionLayout.getGlobalVisibleRect(outRect);
                    if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                        optionLayout.setVisibility(View.GONE);
                        isOptionLayoutVisible = false;
                    }
                }
                return false; // Return false to allow other touch events to continue
            }
        });

        menuClickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
                boolean isFirstOptionLayoutOpen = prefs.getBoolean("isFirstOptionLayoutOpen", true);
                if (isOptionLayoutVisible) {
                    optionLayout.setVisibility(View.GONE);
                    isOptionLayoutVisible = false;
                } else {
                    optionLayout.setVisibility(View.VISIBLE);
                    isOptionLayoutVisible = true;
                    if (isFirstOptionLayoutOpen) {
                        TapTargetView.showFor(MainActivity.this,
                                TapTarget.forView(findViewById(R.id.option_layout), "Options Menu", "Access language and theme options here")
                                        .outerCircleColor(ThemeManager.getCurrentTheme(MainActivity.this) == ThemeManager.THEME_BLUE ? R.color.background_blue_TT :
                                                ThemeManager.getCurrentTheme(MainActivity.this) == ThemeManager.THEME_GREEN ? R.color.background_green_TT : R.color.background_TT)
                                        .titleTextColor(android.R.color.white)
                                        .descriptionTextColor(android.R.color.white)
                                        .titleTypeface(ResourcesCompat.getFont(MainActivity.this, R.font.caudex))
                                        .descriptionTypeface(ResourcesCompat.getFont(MainActivity.this, R.font.caudex))
                                        .dimColor(android.R.color.black)
                                        .transparentTarget(true)
                                        .drawShadow(true)
                                        .titleTextSize(20)
                                        .descriptionTextSize(16)
                                        .targetRadius(90)
                                        .cancelable(false),
                                new TapTargetView.Listener() {
                                    @Override
                                    public void onTargetClick(TapTargetView view) {
                                        super.onTargetClick(view);
                                        SharedPreferences.Editor editor = prefs.edit();
                                        editor.putBoolean("isFirstOptionLayoutOpen", false);
                                        editor.apply();
                                    }
                                });
                    }
                }
            }
        });

        // Voice input button
        findViewById(R.id.voice_btn).setOnClickListener(v -> {
            startVoiceInput();
        });

        //C button clear screen method
        TextView clearButton = findViewById(R.id.clear_btn);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear all data from the database
                dbHelper.clearAllCalculations();

                // Clear the RecyclerView
                loadHistory(); // This will reload the RecyclerView with an empty list

                // Show a toast message
                Toast.makeText(MainActivity.this, "Cleared", Toast.LENGTH_SHORT).show();
            }
        });

        // Add this in your onCreate() method after initializing the input EditText
        input.setShowSoftInputOnFocus(false);
        input.setCursorVisible(true); // Make sure cursor is visible

        input.setOnClickListener(v -> {
            // Just ensure the cursor is visible when clicking the field
            input.setCursorVisible(true);
        });

        input.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                // Show cursor
                input.setCursorVisible(true);
            }
        });


        TextView addWidgetBtn = findViewById(R.id.scientefic_calc);
        addWidgetBtn.setOnClickListener(v -> {
            // Request to add widget to home screen
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                AppWidgetManager appWidgetManager = getSystemService(AppWidgetManager.class);
                ComponentName widgetProvider = new ComponentName(this, CalculatorWidgetProvider.class);

                if (appWidgetManager.isRequestPinAppWidgetSupported()) {
                    Intent pinnedWidgetCallbackIntent = new Intent(this, CalculatorWidgetProvider.class);
                    PendingIntent successCallback = PendingIntent.getBroadcast(this, 0, pinnedWidgetCallbackIntent, PendingIntent.FLAG_IMMUTABLE);
                    appWidgetManager.requestPinAppWidget(widgetProvider, null, successCallback);
                } else {
                    Toast.makeText(this, "Widget pinning not supported", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Requires Android 8.0+", Toast.LENGTH_SHORT).show();
            }
        });
        /*************************************************************************/

        // Hindi Converter Button Click Listener
        TextView hindiConverterButton = findViewById(R.id.hindi_converter);
        hindiConverterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isHindiEnabled = true;
                isGujaratiEnabled = false;
                updateNumberButtons(); // This will update all number buttons
                Toast.makeText(MainActivity.this, "Hindi Numbers Enabled", Toast.LENGTH_SHORT).show();
            }
        });

        // Gujarati Converter Button Click Listener
        TextView gujaratiConverterButton = findViewById(R.id.gujarati_converter);
        gujaratiConverterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isGujaratiEnabled = true;
                isHindiEnabled = false;
                updateNumberButtons(); // This will update all number buttons
                Toast.makeText(MainActivity.this, "Gujarati Numbers Enabled", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.english_btn).setOnClickListener(v -> {
            isHindiEnabled = false;
            isGujaratiEnabled = false;
            updateNumberButtons();
            Toast.makeText(this, "English Numbers Enabled", Toast.LENGTH_SHORT).show();
        });


        // Modify your equal button click listener in onCreate()
        findViewById(R.id.equal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateResult();
                speakResult();
            }
        });

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeech.setLanguage(Locale.getDefault());
                } else {
                    Toast.makeText(MainActivity.this, "Text-to-speech initialization failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        stopVoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle the voice output state
                isVoiceOutputEnabled = !isVoiceOutputEnabled;

                // Save both states
                SharedPreferences.Editor voiceEditor = voicePrefs.edit();
                voiceEditor.putBoolean(STOP_VOICE_COLOR_KEY, !isVoiceOutputEnabled);
                voiceEditor.apply();

                SharedPreferences.Editor outputEditor = sharedPreferences.edit();
                outputEditor.putBoolean(VOICE_OUTPUT_KEY, isVoiceOutputEnabled);
                outputEditor.apply();

                // Update the button color
                updateStopVoiceButtonColor(!isVoiceOutputEnabled);
            }
        });
    }

    private void updateStopVoiceButtonColor(boolean isStopped) {
        Drawable drawable = AppCompatResources.getDrawable(this, R.drawable.ic_stop);
        if (drawable != null) {
            drawable = DrawableCompat.wrap(drawable).mutate(); // Important!
            int color = ContextCompat.getColor(this, isStopped ? R.color.red : R.color.white);
            DrawableCompat.setTint(drawable, color);

            // Set bounds or the drawable won't show up
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

            // Apply drawable to the TextView (top, bottom, left, right)
            stopVoiceButton.setCompoundDrawables(null, drawable, null, null);
        }
    }

    private void setupThemeButton() {
        TextView themeButton = findViewById(R.id.theme_btn);
        themeButton.setOnClickListener(v -> {
            // Create an AlertDialog to show theme options
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Theme");

            // Theme options
            String[] themes = {"Yellow", "Blue", "Green"};
            builder.setItems(themes, (dialog, which) -> {
                int newTheme;
                switch (which) {
                    case 0: // Yellow
                        newTheme = ThemeManager.THEME_YELLOW;
                        break;
                    case 1: // Blue
                        newTheme = ThemeManager.THEME_BLUE;
                        break;
                    case 2: // Green
                        newTheme = ThemeManager.THEME_GREEN;
                        break;
                    default:
                        newTheme = ThemeManager.THEME_YELLOW;
                }

                // Only apply theme if it's different from the current one
                if (newTheme != ThemeManager.getCurrentTheme(this)) {
                    ThemeManager.setTheme(this, newTheme, true);
                }
            });

            // Allow dialog to be canceled by touching outside
            builder.setCancelable(true);
            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    private void updateTheme() {
        int currentTheme = ThemeManager.getCurrentTheme(this);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            switch(currentTheme) {
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


        ConstraintLayout containerBG = findViewById(R.id.constraint_main_layout);

        CardView option_cardview=findViewById(R.id.option_layout);
        // Calculator tabs
        TextView calculatorTab = findViewById(R.id.calculator_page1);
        TextView currencyTab = findViewById(R.id.currencyPage_btn);
        TextView unitTab = findViewById(R.id.unit_converter_activity);
        TextView mathTab = findViewById(R.id.maths_question_activity);
        TextView ratingTab = findViewById(R.id.rating_activity_btn);
        TextView clear_btn = findViewById(R.id.clear_btn);

        // Input/Output
        EditText input = findViewById(R.id.input);
        TextView output = findViewById(R.id.output);

        // Buttons
        ImageButton menuBtn = findViewById(R.id.menu_click_btn);
        ImageButton historyBtn = findViewById(R.id.history_btn);
        ImageButton voice_btn = findViewById(R.id.voice_btn);
        TextView stopVoiceBtn = findViewById(R.id.stop_voice_btn);

        // Operator buttons
        Button clear = findViewById(R.id.clear);
        Button percent = findViewById(R.id.percent);
        Button backspace = findViewById(R.id.backspace);
        Button divide = findViewById(R.id.divide);
        Button multiply = findViewById(R.id.multiply);
        Button subtract = findViewById(R.id.subtract);
        Button add = findViewById(R.id.add);

        // Number buttons
        Button[] numberButtons = {
                findViewById(R.id.btn0), findViewById(R.id.btn1), findViewById(R.id.btn2),
                findViewById(R.id.btn3), findViewById(R.id.btn4), findViewById(R.id.btn5),
                findViewById(R.id.btn6), findViewById(R.id.btn7), findViewById(R.id.btn8),
                findViewById(R.id.btn9), findViewById(R.id.btn10), findViewById(R.id.btnDot)
        };

        // Equal button
        Button equal = findViewById(R.id.equal);

        // Drawer layout
        ConstraintLayout drawerLayout = findViewById(R.id.navigation_drawer);

        switch(currentTheme) {
            case ThemeManager.THEME_YELLOW:


                // Set yellow theme

                containerBG.setBackgroundResource(R.color.background);
                option_cardview.setBackgroundResource(R.drawable.input_background);
                clear_btn.setBackgroundResource(R.drawable.input_background);
                drawerLayout.setBackgroundResource(R.color.background);
                clear_btn.setTextColor(ContextCompat.getColor(this, R.color.accent));



                calculatorTab.setBackgroundResource(R.drawable.tab_background2);
                currencyTab.setBackgroundResource(R.drawable.tab_background);
                unitTab.setBackgroundResource(R.drawable.tab_background);
                mathTab.setBackgroundResource(R.drawable.tab_background);
                ratingTab.setBackgroundResource(R.drawable.tab_background);

                input.setBackgroundResource(R.drawable.input_background);
                output.setTextColor(ContextCompat.getColor(this, R.color.accent));

                menuBtn.setBackgroundResource(R.drawable.circle_button);
                historyBtn.setBackgroundResource(R.drawable.circle_button);
                stopVoiceBtn.setBackgroundResource(R.drawable.circle_button);
                voice_btn.setBackgroundResource(R.drawable.circle_button);

                clear.setBackgroundResource(R.drawable.button_operator);
                percent.setBackgroundResource(R.drawable.button_operator);
                backspace.setBackgroundResource(R.drawable.button_operator);
                divide.setBackgroundResource(R.drawable.button_operator);
                multiply.setBackgroundResource(R.drawable.button_operator);
                subtract.setBackgroundResource(R.drawable.button_operator);
                add.setBackgroundResource(R.drawable.button_operator);

                for (Button btn : numberButtons) {
                    btn.setBackgroundResource(R.drawable.button_number);
                    btn.setTextColor(ContextCompat.getColor(this, R.color.white));
                }

                equal.setBackgroundResource(R.drawable.button_equal);
                equal.setTextColor(ContextCompat.getColor(this, R.color.white));

                drawerLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.background));
                break;

            case ThemeManager.THEME_BLUE:
                // Set blue theme

                containerBG.setBackgroundResource(R.color.background_blue);
                drawerLayout.setBackgroundResource(R.color.background_blue);
                option_cardview.setBackgroundResource(R.drawable.input_background_blue);
                clear_btn.setBackgroundResource(R.drawable.input_background_blue);
                clear_btn.setTextColor(ContextCompat.getColor(this, R.color.accent_blue));





                calculatorTab.setBackgroundResource(R.drawable.tab_background2_blue);
                currencyTab.setBackgroundResource(R.drawable.tab_background);
                unitTab.setBackgroundResource(R.drawable.tab_background);
                mathTab.setBackgroundResource(R.drawable.tab_background);
                ratingTab.setBackgroundResource(R.drawable.tab_background);

                input.setBackgroundResource(R.drawable.input_background_blue);
                output.setTextColor(ContextCompat.getColor(this, R.color.accent_blue));

                menuBtn.setBackgroundResource(R.drawable.circle_button_blue);
                historyBtn.setBackgroundResource(R.drawable.circle_button_blue);
                stopVoiceBtn.setBackgroundResource(R.drawable.circle_button_blue);
                voice_btn.setBackgroundResource(R.drawable.circle_button_blue);

                clear.setBackgroundResource(R.drawable.button_operator_blue);
                clear.setTextColor(ContextCompat.getColor(this, R.color.accent_blue));

                percent.setBackgroundResource(R.drawable.button_operator_blue);
                percent.setTextColor(ContextCompat.getColor(this, R.color.accent_blue));

                backspace.setBackgroundResource(R.drawable.button_operator_blue);
                backspace.setTextColor(ContextCompat.getColor(this, R.color.accent_blue));

                divide.setBackgroundResource(R.drawable.button_operator_blue);
                divide.setTextColor(ContextCompat.getColor(this, R.color.accent_blue));

                multiply.setBackgroundResource(R.drawable.button_operator_blue);
                multiply.setTextColor(ContextCompat.getColor(this, R.color.accent_blue));

                subtract.setBackgroundResource(R.drawable.button_operator_blue);
                subtract.setTextColor(ContextCompat.getColor(this, R.color.accent_blue));

                add.setBackgroundResource(R.drawable.button_operator_blue);
                add.setTextColor(ContextCompat.getColor(this, R.color.accent_blue));


                for (Button btn : numberButtons) {
                    btn.setBackgroundResource(R.drawable.button_number_blue);
                    btn.setTextColor(ContextCompat.getColor(this, R.color.white));
                }


                equal.setBackgroundResource(R.drawable.button_equal_blue);
                equal.setTextColor(ContextCompat.getColor(this, R.color.white));

                drawerLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.background_blue));
                break;

            case ThemeManager.THEME_GREEN:
                // Set green theme

                containerBG.setBackgroundResource(R.color.background_green);
                option_cardview.setBackgroundResource(R.drawable.input_background_green);
                drawerLayout.setBackgroundResource(R.color.background_green);
                clear_btn.setBackgroundResource(R.drawable.input_background_green);
                clear_btn.setTextColor(ContextCompat.getColor(this, R.color.accent_green));




                calculatorTab.setBackgroundResource(R.drawable.tab_background2_green);
                currencyTab.setBackgroundResource(R.drawable.tab_background);
                unitTab.setBackgroundResource(R.drawable.tab_background);
                mathTab.setBackgroundResource(R.drawable.tab_background);
                ratingTab.setBackgroundResource(R.drawable.tab_background);

                input.setBackgroundResource(R.drawable.input_background_green);
                output.setTextColor(ContextCompat.getColor(this, R.color.accent_green));

                menuBtn.setBackgroundResource(R.drawable.circle_button_green);
                historyBtn.setBackgroundResource(R.drawable.circle_button_green);
                stopVoiceBtn.setBackgroundResource(R.drawable.circle_button_green);
                voice_btn.setBackgroundResource(R.drawable.circle_button_green);

                clear.setBackgroundResource(R.drawable.button_operator_green);
                clear.setTextColor(ContextCompat.getColor(this, R.color.accent_green));

                percent.setBackgroundResource(R.drawable.button_operator_green);
                percent.setTextColor(ContextCompat.getColor(this, R.color.accent_green));

                backspace.setBackgroundResource(R.drawable.button_operator_green);
                backspace.setTextColor(ContextCompat.getColor(this, R.color.accent_green));

                divide.setBackgroundResource(R.drawable.button_operator_green);
                divide.setTextColor(ContextCompat.getColor(this, R.color.accent_green));

                multiply.setBackgroundResource(R.drawable.button_operator_green);
                multiply.setTextColor(ContextCompat.getColor(this, R.color.accent_green));

                subtract.setBackgroundResource(R.drawable.button_operator_green);
                subtract.setTextColor(ContextCompat.getColor(this, R.color.accent_green));

                add.setBackgroundResource(R.drawable.button_operator_green);
                add.setTextColor(ContextCompat.getColor(this, R.color.accent_green));


                for (Button btn : numberButtons) {
                    btn.setBackgroundResource(R.drawable.button_number_green);
                    btn.setTextColor(ContextCompat.getColor(this, R.color.white));
                }

                equal.setBackgroundResource(R.drawable.button_equal_green);
                equal.setTextColor(ContextCompat.getColor(this, R.color.white));

                drawerLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.background_green));
                break;
        }
    }


    private void updateNumberButtons() {
        int[] numberButtonIds = {R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btn10};

        for (int id : numberButtonIds) {
            AppCompatButton button = findViewById(id);
            String originalNumber;

            // Replace switch with if-else
            if (id == R.id.btn0) {
                originalNumber = "0";
            } else if (id == R.id.btn1) {
                originalNumber = "1";
            } else if (id == R.id.btn2) {
                originalNumber = "2";
            } else if (id == R.id.btn3) {
                originalNumber = "3";
            } else if (id == R.id.btn4) {
                originalNumber = "4";
            } else if (id == R.id.btn5) {
                originalNumber = "5";
            } else if (id == R.id.btn6) {
                originalNumber = "6";
            } else if (id == R.id.btn7) {
                originalNumber = "7";
            } else if (id == R.id.btn8) {
                originalNumber = "8";
            } else if (id == R.id.btn9) {
                originalNumber = "9";
            } else if (id == R.id.btn10) {
                originalNumber = "00";
            } else {
                originalNumber = button.getText().toString();
            }

            if (isHindiEnabled) {
                // Convert to Hindi numbers
                String hindiText = convertToHindi(originalNumber);
                button.setText(hindiText);
            } else if (isGujaratiEnabled) {
                // Convert to Gujarati numbers
                String gujaratiText = convertToGujarati(originalNumber);
                button.setText(gujaratiText);
            } else {
                // Convert back to English numbers
                button.setText(originalNumber);
            }
        }
    }

    private String convertToHindi(String number) {
        switch (number) {
            case "0":
                return "०";
            case "1":
                return "१";
            case "2":
                return "२";
            case "3":
                return "३";
            case "4":
                return "४";
            case "5":
                return "५";
            case "6":
                return "६";
            case "7":
                return "७";
            case "8":
                return "८";
            case "9":
                return "९";
            case "00":
                return "००";
            default:
                return number;
        }
    }

    private String convertToGujarati(String number) {
        switch (number) {
            case "0":
                return "૦";
            case "1":
                return "૧";
            case "2":
                return "૨";
            case "3":
                return "૩";
            case "4":
                return "૪";
            case "5":
                return "૫";
            case "6":
                return "૬";
            case "7":
                return "૭";
            case "8":
                return "૮";
            case "9":
                return "૯";
            case "00":
                return "૦૦";
            default:
                return number;
        }
    }

    private String convertToDisplayNumber(String number) {
        Log.d("CalcMaster", "convertToDisplayNumber: input: " + number);
        // First convert to English digits if input contains Hindi/Gujarati numbers
        number = convertToEnglish(number);

        String result;
        if (isHindiEnabled) {
            result = number.replace("0", "०")
                    .replace("1", "१")
                    .replace("2", "२")
                    .replace("3", "३")
                    .replace("4", "४")
                    .replace("5", "५")
                    .replace("6", "६")
                    .replace("7", "७")
                    .replace("8", "८")
                    .replace("9", "९");
        } else if (isGujaratiEnabled) {
            result = number.replace("0", "૦")
                    .replace("1", "૧")
                    .replace("2", "૨")
                    .replace("3", "૩")
                    .replace("4", "૪")
                    .replace("5", "૫")
                    .replace("6", "૬")
                    .replace("7", "૭")
                    .replace("8", "૮")
                    .replace("9", "૯");
        } else {
            result = number; // English numbers remain as is
        }
        Log.d("CalcMaster", "convertToDisplayNumber: output: " + result);
        return result;
    }

    private String convertToEnglish(String number) {

        if (number == null) return "";

        // Hindi to English
        number = number.replace("०", "0")
                .replace("१", "1")
                .replace("२", "2")
                .replace("३", "3")
                .replace("४", "4")
                .replace("५", "5")
                .replace("६", "6")
                .replace("७", "7")
                .replace("८", "8")
                .replace("९", "9");

        // Gujarati to English
        number = number.replace("૦", "0")
                .replace("૧", "1")
                .replace("૨", "2")
                .replace("૩", "3")
                .replace("૪", "4")
                .replace("૫", "5")
                .replace("૬", "6")
                .replace("૭", "7")
                .replace("૮", "8")
                .replace("૯", "9");

        return number;
    }

    private String convertDisplayToEnglish(String displayNumber) {
        if (isHindiEnabled) {
            return displayNumber.replace("०", "0")
                    .replace("१", "1")
                    .replace("२", "2")
                    .replace("३", "3")
                    .replace("४", "4")
                    .replace("५", "5")
                    .replace("६", "6")
                    .replace("७", "7")
                    .replace("८", "8")
                    .replace("९", "9");
        } else if (isGujaratiEnabled) {
            return displayNumber.replace("૦", "0")
                    .replace("૧", "1")
                    .replace("૨", "2")
                    .replace("૩", "3")
                    .replace("૪", "4")
                    .replace("૫", "5")
                    .replace("૬", "6")
                    .replace("૭", "7")
                    .replace("૮", "8")
                    .replace("૯", "9");
        }
        return displayNumber;
    }

    private String convertWordsToNumbersAndOperators(String text) {
        // English number words (case insensitive)
        text = text.replaceAll("(?i)ten", "10")
                .replaceAll("(?i)one", "1")
                .replaceAll("(?i)two", "2")
                .replaceAll("(?i)three", "3")
                .replaceAll("(?i)four", "4")
                .replaceAll("(?i)five", "5")
                .replaceAll("(?i)six", "6")
                .replaceAll("(?i)seven", "7")
                .replaceAll("(?i)eight", "8")
                .replaceAll("(?i)nine", "9")
                .replaceAll("(?i)zero", "0")
                .replaceAll("(?i)point", ".");

        // Operation words (case insensitive)
        text = text.replaceAll("(?i)plus", "+")
                .replaceAll("(?i)minus", "-")
                .replaceAll("(?i)multiply", "×")
                .replaceAll("(?i)divide", "÷")
                .replaceAll("(?i)times", "×")
                .replaceAll("(?i)by", "÷")
                .replaceAll("(?i)percent", "%")
                .replaceAll("(?i)modulus", "%")
                .replaceAll("(?i)equals", "=")
                .replaceAll("(?i)equal", "=");

        // Remove any remaining words that weren't converted to math symbols
//        text = text.replaceAll("[^0-9+\\-×÷.%()=]", "");
        text = text.replaceAll("[^0-9+\\-×÷.%()=\\s]", "");

        return text;
    }

    private String convertHindiWordsToMath(String text) {
        // Convert Hindi numbers to digits
        text = text.replaceAll("शून्य", "0")
                .replaceAll("एक", "1")
                .replaceAll("दो", "2")
                .replaceAll("तीन", "3")
                .replaceAll("चार", "4")
                .replaceAll("पाँच", "5")
                .replaceAll("छह", "6")
                .replaceAll("सात", "7")
                .replaceAll("आठ", "8")
                .replaceAll("नौ", "9")
                .replaceAll("दस", "10");

        // Convert Hindi operations to symbols
        text = text.replaceAll("\\b(जोड़|प्लस)\\b", "+")
                .replaceAll("\\b(घटाव|घटाओ|माइनस)\\b", "-")
                .replaceAll("\\b(गुना|गुणा|मल्टीप्लाई)\\b", "×")
                .replaceAll("\\b(भाग|डिवाइड)\\b", "÷")
                .replaceAll("\\b(प्रतिशत|टका)\\b", "%")
                .replaceAll("\\bबराबर\\b", "=");

        // Remove any remaining Hindi words
        text = text.replaceAll("[^0-9+\\-×÷.%()=]", "");
        return text;
    }

    private String convertGujaratiWordsToMath(String text) {
        // Convert Gujarati numbers to digits
        text = text.replaceAll("શૂન્ય", "0")
                .replaceAll("એક", "1")
                .replaceAll("બે", "2")
                .replaceAll("ત્રણ", "3")
                .replaceAll("ચાર", "4")
                .replaceAll("પાંચ", "5")
                .replaceAll("છ ", "6")//છ between space because i can tell ઓછા to print ૬
                .replaceAll("સાત", "7")
                .replaceAll("આઠ", "8")
                .replaceAll("નવ", "9")
                .replaceAll("દસ", "10");

        // Convert Gujarati operations to symbols
        text = text.replaceAll("\\b(વત્તા|પ્લસ)\\b", "+")
                .replaceAll("\\b(ઓ૬ા|ઓછા|માઈનસ)\\b", "-")
                .replaceAll("\\b(ગુણાકાર|મલ્ટીપ્લાય)\\b", "×")
                .replaceAll("\\b(ભાગ|ડિવાઈડ)\\b", "÷")
                .replaceAll("\\b(પર્સનતેજ|ટકા)\\b", "%")
                .replaceAll("\\bબરાબર\\b", "=");


        // Remove any remaining Gujarati wordsા
        text = text.replaceAll("[^0-9+\\-×÷.%()=]", "");
        return text;
    }

    private String convertNumberToHindiWords(int number) {
        String[] units = {"शून्य", "एक", "दो", "तीन", "चार", "पाँच", "छह", "सात", "आठ", "नौ", "दस"};
        if (number >= 0 && number <= 10) {
            return units[number];
        }
        return String.valueOf(number);
    }

    private String convertNumberToGujaratiWords(int number) {
        String[] units = {"શૂન્ય", "એક", "બે", "ત્રણ", "ચાર", "પાંચ", "છ", "સાત", "આઠ", "નવ", "દસ"};
        if (number >= 0 && number <= 10) {
            return units[number];
        }
        return String.valueOf(number);
    }


    // Add this to handle cursor position when updating input
    private void updateInputDisplay() {
        String displayText = processCommasInExpression(currentInput);
        int oldCursorPos = input.getSelectionStart();

        input.setText(displayText);

        // Try to maintain cursor position if possible
        if (oldCursorPos <= displayText.length() && oldCursorPos > 0) {
            input.setSelection(oldCursorPos);
        } else {
            input.setSelection(displayText.length());
        }
    }
    private void updateOutputDisplay(double result) {
        String formattedResult = formatResult(result);
        String displayText = formatNumberWithCommas(formattedResult);
        output.setText(displayText);
    }


    private String formatNumberWithCommas(String number) {

        // First convert to English digits for processing
        String englishNumber = convertToEnglish(number);

        // Check if the number ends with a decimal point
        boolean endsWithDecimal = englishNumber.endsWith(".");

        // Split into parts (for decimal numbers), use -1 to keep trailing empty parts
        String[] parts = englishNumber.split("\\.", -1);
        String integerPart = parts[0];
        String decimalPart = parts.length > 1 ? "." + parts[1] : (endsWithDecimal ? "." : "");

        // Add commas to integer part
        String formattedInteger = addCommasToNumber(integerPart);

        // Combine integer and decimal parts
        String displayFormatted = formattedInteger + decimalPart;

        String finalResult = convertToDisplayNumber(displayFormatted);
        return finalResult;
    }


    private String addCommasToNumber(String number) {
        StringBuilder result = new StringBuilder();
        int len = number.length();
        int commaPosition = len % 3 == 0 ? 3 : len % 3;

        for (int i = 0; i < len; i++) {
            result.append(number.charAt(i));
            if (i == commaPosition - 1 && i != len - 1) {
                result.append(",");
                commaPosition += 3;
            }
        }

        return result.toString();
    }

    private String processCommasInExpression(String expression) {

        // Convert to English for processing
        String englishExpr = convertToEnglish(expression);

        // This will find all numbers in the expression
        StringBuilder result = new StringBuilder();
        StringBuilder currentNumber = new StringBuilder();

        for (int i = 0; i < englishExpr.length(); i++) {
            char c = englishExpr.charAt(i);

            if (Character.isDigit(c) || c == '.') {
                currentNumber.append(c);
            } else {
                if (currentNumber.length() > 0) {
                    String formattedNumber = formatNumberWithCommas(currentNumber.toString());
                    result.append(formattedNumber);
                    currentNumber = new StringBuilder();
                }
                result.append(c);
            }
        }

        // Add any remaining number at the end
        if (currentNumber.length() > 0) {
            String formattedNumber = formatNumberWithCommas(currentNumber.toString());
            result.append(formattedNumber);
        }

        String finalResult = result.toString();
        return convertToDisplayNumber(finalResult);
    }


    private void handleDotButtonClick() {
        // Get current selection from display
        int selectionStart = input.getSelectionStart();
        int selectionEnd = input.getSelectionEnd();

        // Get displayed text
        String displayText = input.getText().toString();

        // Convert to plain positions
        String plainBeforeSelection = convertDisplayToPlain(displayText.substring(0, selectionStart));
        String plainAfterSelection = convertDisplayToPlain(displayText.substring(selectionEnd));

        int plainStart = plainBeforeSelection.length();
        int plainEnd = plainStart + convertDisplayToPlain(displayText.substring(selectionStart, selectionEnd)).length();

        // Handle invalid positions
        if (plainStart < 0) plainStart = 0;
        if (plainEnd < 0) plainEnd = 0;
        if (plainStart > currentInput.length()) plainStart = currentInput.length();
        if (plainEnd > currentInput.length()) plainEnd = currentInput.length();

        // Get text before and after the selection
        String beforeSelection = currentInput.substring(0, plainStart);
        String afterSelection = currentInput.substring(plainEnd);

        // Handle empty input: start with "0."
        if (currentInput.isEmpty()) {
            currentInput = "0.";
            updateInputDisplay();
            input.setSelection(input.getText().length());
            updateOutput();
            return;
        }

        // Find the current number segment to check for existing dots
        String currentNumberSegment = "";
        int startOfNumber = plainStart;
        // Move backward to find the start of the current number
        for (int i = plainStart - 1; i >= 0; i--) {
            char c = currentInput.charAt(i);
            if (!Character.isDigit(c) && c != '.') {
                break;
            }
            startOfNumber = i;
        }
        // Move forward to include digits/decimal after cursor
        StringBuilder segmentBuilder = new StringBuilder();
        for (int i = startOfNumber; i < currentInput.length(); i++) {
            char c = currentInput.charAt(i);
            if (!Character.isDigit(c) && c != '.') {
                break;
            }
            segmentBuilder.append(c);
        }
        currentNumberSegment = segmentBuilder.toString();

        // Prevent multiple dots in the same number segment
        if (currentNumberSegment.contains(".")) {
            return;
        }

        // Insert the dot
        int newPlainPos;
        if (beforeSelection.isEmpty() || beforeSelection.matches(".*[+\\-×÷(]$")) {
            currentInput = beforeSelection + "0." + afterSelection;
            newPlainPos = plainStart + 2;
        } else {
            currentInput = beforeSelection + "." + afterSelection;
            newPlainPos = plainStart + 1;
        }

        // Update display and adjust cursor position
        updateInputDisplay();
        String newDisplayText = input.getText().toString();
        int newDisplayPos = findCursorPositionInDisplay(newDisplayText, newPlainPos);
        input.setSelection(newDisplayPos);
        updateOutput();
        lastCharIsOperator = false;
    }
    private void handlePercentButtonClick() {
        // Get current selection
        int selectionStart = input.getSelectionStart();
        int selectionEnd = input.getSelectionEnd();

        // Handle invalid positions
        if (selectionStart < 0) selectionStart = 0;
        if (selectionEnd < 0) selectionEnd = 0;
        if (selectionStart > currentInput.length()) selectionStart = currentInput.length();
        if (selectionEnd > currentInput.length()) selectionEnd = currentInput.length();

        String beforeSelection = currentInput.substring(0, selectionStart);
        String afterSelection = currentInput.substring(selectionEnd);

        // Only allow % after a number or another percentage
        if (beforeSelection.matches(".*\\d$") || beforeSelection.endsWith("%")) {
            currentInput = beforeSelection + "%" + afterSelection;
            updateInputDisplay();
            String displayText = input.getText().toString();
            int percentPos = displayText.lastIndexOf('%');
            if (percentPos >= 0) {
                input.setSelection(percentPos + 1);
            } else {
                input.setSelection(displayText.length());
            }
            updateOutput();
        }
    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        // Set language and prompt based on selection
        String promptText = "Speak now...";
        if (isHindiEnabled) {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN");
            promptText = "बोलो..."; // Hindi prompt
        } else if (isGujaratiEnabled) {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "gu-IN");
            promptText = "બોલો..."; // Gujarati prompt
        } else {
            // Default to English
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
        }

        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, promptText);

        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Speech recognition not supported", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && !matches.isEmpty()) {
                String spokenText = matches.get(0);
                processSpokenText(spokenText); // Always process the spoken text
            }
        }
    }

    private void processSpokenText(String spokenText) {
        String processedText = spokenText.toLowerCase().trim();

        // Convert the spoken text to math based on the selected language
        if (isHindiEnabled) {
            processedText = convertHindiWordsToMath(processedText);
        } else if (isGujaratiEnabled) {
            processedText = convertGujaratiWordsToMath(processedText);
        } else {
            processedText = convertWordsToNumbersAndOperators(processedText);
        }

        // Handle direct number input and preserve operators
        processedText = convertToEnglish(processedText);

        // Remove extra spaces but keep single spaces between numbers and operators
        processedText = processedText.replaceAll("\\s+", " ").trim();

        // Update current input and display
        currentInput = processedText;
        updateInputDisplay();
        updateOutput();

        // Auto-calculate if expression ends with equals
        if (processedText.contains("=") || processedText.contains("बराबर") || processedText.contains("બરાબર")) {
            calculateResult();
        }
    }
    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }



    /****************************************************************************/
    private void appendOperator(String operator) {
        // First remove all commas from current input for processing
        String cleanInput = currentInput.replace(",", "");

        // Get current selection from display
        int selectionStart = input.getSelectionStart();
        int selectionEnd = input.getSelectionEnd();

        // Get displayed text
        String displayText = input.getText().toString();

        // Convert to plain positions
        String plainBeforeSelection = convertDisplayToPlain(displayText.substring(0, selectionStart));
        String plainAfterSelection = convertDisplayToPlain(displayText.substring(selectionEnd));

        int plainStart = plainBeforeSelection.length();
        int plainEnd = plainStart + convertDisplayToPlain(displayText.substring(selectionStart, selectionEnd)).length();

        if (cleanInput.isEmpty()) {
            // Only allow minus at start
            if (operator.equals("-")) {
                currentInput = "-";
                updateInputDisplay();
                input.setSelection(currentInput.length());
            }
            return;
        }

        // Handle invalid selection positions
        if (plainStart < 0) plainStart = 0;
        if (plainEnd < 0) plainEnd = 0;
        if (plainStart > currentInput.length()) plainStart = currentInput.length();
        if (plainEnd > currentInput.length()) plainEnd = currentInput.length();

        String beforeSelection = currentInput.substring(0, plainStart);
        String afterSelection = currentInput.substring(plainEnd);

        // Special handling for percentage operator
        if (operator.equals("%")) {
            // Only allow % after a number
            if (beforeSelection.matches(".*\\d$")) {
                currentInput = beforeSelection + "%" + afterSelection;
                updateInputDisplay();

                // Calculate new cursor position after %
                String newDisplay = input.getText().toString();
                int newPlainPos = plainStart + 1;
                int newDisplayPos = findCursorPositionInDisplay(newDisplay, newPlainPos);
                input.setSelection(newDisplayPos);
                updateOutput();
                return;
            }
            return;
        }

        // Don't allow operator after another operator (except for minus after certain operators)
        if (beforeSelection.matches(".*[+×÷]$") && !operator.equals("-")) {
            return;
        }

        // Remove any trailing operators before adding new one
        if (beforeSelection.matches(".*[+\\-×÷]$")) {
            beforeSelection = beforeSelection.substring(0, beforeSelection.length() - 1);
        }

        // Build new input without commas
        currentInput = beforeSelection + operator + afterSelection;
        lastCharIsOperator = true;

        updateInputDisplay();
        updateOutput();

        // Calculate new cursor position
        String newDisplay = input.getText().toString();
        int newPlainPos = plainStart + operator.length();
        int newDisplayPos = findCursorPositionInDisplay(newDisplay, newPlainPos);
        input.setSelection(newDisplayPos);
    }

    private String formatResult(double result) {
        // Remove decimal part if it's .0
        if (result == (long) result) {
            return String.valueOf((long) result);
        } else {
            // Format to show up to 10 decimal places, removing trailing zeros
            String formatted = String.format(Locale.getDefault(), "%.10f", result);
            formatted = formatted.replaceAll("0*$", ""); // Remove trailing zeros
            formatted = formatted.replaceAll("\\.$", ""); // Remove trailing decimal point
            return formatted;
        }
    }

    private void updateOutput() {
        try {
            // Only show output if there's at least one operator in the expression
            if (isValidExpression(currentInput) && currentInput.matches(".*[+\\-×÷%].*")) {
                double result = evaluateExpression(currentInput);
                updateOutputDisplay(result);
            } else {
                output.setText("");
            }
        } catch (Exception e) {
            output.setText("");
            Log.e("CalcError", "Error evaluating expression", e);
        }
    }

    // Modify evaluateExpression to handle percentages
    private double evaluateExpression(String expression) {


        if (expression == null || expression.isEmpty()) {
            throw new RuntimeException("Empty expression");
        }
        // Convert to English equivalents for calculation
        expression = convertToEnglish(expression);

        // Replace non-standard operators with their correct mathematical equivalents
        expression = expression.replaceAll("×", "*") // Replace × with *
                .replaceAll("÷", "/") // Replace ÷ with /
                .replaceAll("\\s+", ""); // Remove all spaces


        // Handle percentage operations
        // Case 1: X%Y → X*(Y/100)
        expression = expression.replaceAll("(\\d+)%\\s*(\\d+)", "$1*($2/100)");
        // Case 2: X% → X/100
        expression = expression.replaceAll("(\\d+)%", "($1/100)");

        // Validate expression
        if (!isValidMathExpression(expression)) {
            throw new RuntimeException("Invalid expression");
        }

        // Evaluate the expression using Java's built-in evaluation method
        return eval(expression);
    }

    private boolean isValidExpression(String expression) {
        expression = convertDisplayToEnglish(expression);
        expression = expression.replaceAll("\\s+", "");


        if (expression.isEmpty()) {
            return false;
        }

        // Check for balanced parentheses
        int balance = 0;
        for (char c : expression.toCharArray()) {
            if (c == '(') balance++;
            if (c == ')') balance--;
            if (balance < 0) return false;
        }
        if (balance != 0) return false;

        // Check for valid characters
        if (!expression.matches("^[0-9+\\-×÷.%()]+$")) {
            return false;
        }

        // Check for valid operator placement (no consecutive operators)
        if (expression.matches(".*[+\\-×÷]{2,}.*")) {
            return false;
        }

        // Check for operators at start or end (except minus at start)
        if (expression.matches("[+×÷].*") || expression.matches(".*[+\\-×÷]$")) {
            return false;
        }

        // Check for valid percentage placement
        if (expression.matches(".*%[^0-9].*") ||  // % followed by non-digit
                expression.matches(".*[+\\-×÷.]%.*") ||  // operator before %
                expression.matches("^%.*")) {  // % at start
            return false;
        }

        // Check for valid decimal points
        if (expression.matches(".*\\..*\\..*")) {  // multiple dots in same number
            return false;
        }

        return true;
    }


    private boolean isValidMathExpression(String expression) {
        if (expression.isEmpty()) {
            return false;
        }

        // Check for balanced parentheses
        int balance = 0;
        for (char c : expression.toCharArray()) {
            if (c == '(') balance++;
            if (c == ')') balance--;
            if (balance < 0) return false;
        }
        if (balance != 0) return false;

        // Check for valid characters
        if (!expression.matches("^[0-9+\\-*/.()%]+$")) {
            return false;
        }

        // Check for valid operator placement
        if (expression.matches(".*[+\\-×÷]{2,}.*")) {
            return false;
        }

        return true;
    }

    private double eval(final String str) {
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
                if (pos < str.length()) {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (; ; ) {
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (; ; ) {
                    if (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    if (!eat(')')) throw new RuntimeException("Missing ')'");
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    String numStr = str.substring(startPos, this.pos);
                    try {
                        x = Double.parseDouble(numStr);
                    } catch (NumberFormatException e) {
                        throw new RuntimeException("Invalid number: " + numStr);
                    }
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }

                return x;
            }
        }.parse();
    }

    private void clear() {
        currentInput = "";
        input.setText("");
        output.setText("");
        input.setSelection(0); // Set cursor to start
    }


    private void backspace() {
        if (currentInput.isEmpty()) return;

        // Get current selection from the input EditText display
        int selectionStart = input.getSelectionStart();
        int selectionEnd = input.getSelectionEnd();

        // Get the displayed text (with commas)
        String displayText = input.getText().toString();

        // Convert displayed text to currentInput format (without commas) for processing
        String plainBeforeSelection = convertDisplayToPlain(displayText.substring(0, selectionStart));
        String plainSelected = convertDisplayToPlain(displayText.substring(selectionStart, selectionEnd));

        // Calculate plain positions
        int plainStart = plainBeforeSelection.length();
        int plainEnd = plainStart + plainSelected.length();

        // Handle invalid positions
        if (plainStart < 0) plainStart = 0;
        if (plainEnd < 0) plainEnd = 0;
        if (plainStart > currentInput.length()) plainStart = currentInput.length();
        if (plainEnd > currentInput.length()) plainEnd = currentInput.length();

        if (plainStart == plainEnd) {
            // No selection - delete character before cursor
            if (plainStart > 0) {
                currentInput = currentInput.substring(0, plainStart - 1) + currentInput.substring(plainStart);
                updateInputDisplay();
                // Set cursor at the position where deletion occurred (not at end)
                int newPlainPos = plainStart - 1;
                String newDisplayText = input.getText().toString();
                int newDisplayPos = findCursorPositionInDisplay(newDisplayText, newPlainPos);
                input.setSelection(newDisplayPos);
            }
        } else {
            // Delete selected text
            currentInput = currentInput.substring(0, plainStart) + currentInput.substring(plainEnd);
            updateInputDisplay();
            // Set cursor at the start of the deleted area
            String newDisplayText = input.getText().toString();
            int newDisplayPos = findCursorPositionInDisplay(newDisplayText, plainStart);
            input.setSelection(newDisplayPos);
        }

        if (currentInput.isEmpty()) {
            output.setText("");
        } else {
            updateOutput();
        }
    }

    // Add this helper method to convert display text (with commas) to plain text (without commas):
    private String convertDisplayToPlain(String displayText) {
        if (displayText == null) return "";
        // Remove all commas from the display text
        return displayText.replace(",", "");
    }
    private int findCursorPositionInDisplay(String displayText, int cursorPosInPlain) {
        if (displayText == null || displayText.isEmpty()) return 0;

        int plainCount = 0;
        for (int i = 0; i < displayText.length(); i++) {
            if (displayText.charAt(i) != ',') {
                if (plainCount == cursorPosInPlain) {
                    return i;
                }
                plainCount++;
            }
        }
        return displayText.length();
    }

    // Modify your calculateResult method to handle language-specific output

    private void calculateResult() {
        try {
            if (!isValidExpression(currentInput)) {
                output.setText("Error");
                if (textToSpeech != null) {
                    textToSpeech.speak("Invalid expression", TextToSpeech.QUEUE_FLUSH, null, null);
                }
                return;
            }

            double result = evaluateExpression(currentInput);
            String formattedResult = formatResult(result);

            // Process input and result with commas for display
            String displayInput = processCommasInExpression(currentInput);
            String displayResult = formatNumberWithCommas(formattedResult);

            // Save to database (with commas)
            dbHelper.addCalculation(displayInput, displayResult);

            // Update UI
            currentInput = formattedResult; // Store without commas for calculations
            input.setText(displayResult); // Display with commas
            output.setText("");
            input.setSelection(input.getText().length());


            loadHistory();
            speakResult();
        } catch (Exception e) {
            output.setText("Error");
            if (textToSpeech != null) {
                textToSpeech.speak("Calculation error", TextToSpeech.QUEUE_FLUSH, null, null);
            }
        }
    }

    private void speakResult() {
        if (!isVoiceOutputEnabled || textToSpeech == null) {
            return;
        }

        if (isValidExpression(currentInput)) {
            try {
                double result = evaluateExpression(currentInput);
                String resultText;

                if (isHindiEnabled) {
                    resultText = "आपका उत्तर है " + convertNumberToHindiWords((int) result);
                } else if (isGujaratiEnabled) {
                    resultText = "તમારો જવાબ છે " + convertNumberToGujaratiWords((int) result);
                } else {
                    resultText = "Your answer is " + formatResult(result);
                }

                if (textToSpeech != null && textToSpeech.getEngines().size() > 0) {
                    if (isHindiEnabled) {
                        textToSpeech.setLanguage(new Locale("hi", "IN"));
                    } else if (isGujaratiEnabled) {
                        textToSpeech.setLanguage(new Locale("gu", "IN"));
                    } else {
                        textToSpeech.setLanguage(Locale.getDefault());
                    }
                    textToSpeech.speak(resultText, TextToSpeech.QUEUE_FLUSH, null, null);
                }
            } catch (Exception e) {
                // Handle any errors silently
            }
        }
    }

    private void loadHistory() {
        List<Calculation> calculations = dbHelper.getAllCalculations();

        // No need to convert numbers here since they're already stored as display numbers
        adapter = new CalculationAdapter(calculations);
        recyclerView.setAdapter(adapter);
    }
}