package com.example.calcmaster;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;

import java.util.Random;

public class maths_question extends AppCompatActivity {
    private TextView maths_question_TXT, Timer, score_TXT;
    private EditText maths_answer;
    private Button leave_game_btn, restart_game_btn;
    private CountDownTimer countDownTimer;
    private int score = 0;
    private String currentDifficulty = "";
    private Random random = new Random();
    private boolean isTimerRunning = false;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maths_question);


        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            int statusBarColor;
            switch (ThemeManager.getCurrentTheme(this)) {
                case ThemeManager.THEME_BLUE:
                    statusBarColor = ContextCompat.getColor(this, R.color.background_blue);
                    break;
                case ThemeManager.THEME_GREEN:
                    statusBarColor = ContextCompat.getColor(this, R.color.background_green);
                    break;
                case ThemeManager.THEME_YELLOW:
                default:
                    statusBarColor = ContextCompat.getColor(this, R.color.background);
                    break;
            }
            window.setStatusBarColor(statusBarColor);

        }

        prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE);


        EditText editText = findViewById(R.id.maths_answer);
        editText.requestFocus(); // Request focus
        editText.setCursorVisible(true); // Ensure cursor is visible

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT); // Show keyboard



        maths_question_TXT = findViewById(R.id.maths_question_TXT);
        maths_answer = findViewById(R.id.maths_answer);
        Timer = findViewById(R.id.Timer);
        score_TXT = findViewById(R.id.score_TXT);
        leave_game_btn = findViewById(R.id.leave_game_btn);
        restart_game_btn = findViewById(R.id.restart_game_btn);

        // Button click listeners
        leave_game_btn.setOnClickListener(v -> showLeaveDialog());
        restart_game_btn.setOnClickListener(v -> showRestartDialog());


        updateTheme();


        TextView calculator_page4 = findViewById(R.id.calculator_page4);
        calculator_page4.setOnClickListener(v -> showNavigationDialog("calculator"));

        TextView currency_converter_activity_page4 = findViewById(R.id.currency_converter_activity_page4);
        currency_converter_activity_page4.setOnClickListener(v -> showNavigationDialog("currency"));

        TextView unit_converter_activity_page4 = findViewById(R.id.unit_converter_activity_page4);
        unit_converter_activity_page4.setOnClickListener(v -> showNavigationDialog("unit"));

        TextView rating_activity_btn_page4 = findViewById(R.id.rating_activity_btn_page4);
        rating_activity_btn_page4.setOnClickListener(v -> showNavigationDialog("rating"));



        // Scroll to center the navigation items initially
        HorizontalScrollView navScroll = findViewById(R.id.nav_scroll_page4);
        navScroll.post(new Runnable() {
            @Override
            public void run() {
                // Calculate the scroll position to center the "Maths Game" item
                TextView mathsGame = findViewById(R.id.rating_activity_btn_page4);
                int scrollTo = (mathsGame.getLeft() - (navScroll.getWidth() / 1)) + (mathsGame.getWidth() / 1);
                navScroll.smoothScrollTo(scrollTo, 0);
            }
        });

        // Answer input validation
        maths_answer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Reset background when user modifies the text
                if (s.length() == 0) {
                    applyThemeToUI(ThemeManager.getCurrentTheme(maths_question.this));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String answer = s.toString().trim();
                if (!answer.isEmpty()) {
                    checkAnswer(answer);
                } else {
                    applyThemeToUI(ThemeManager.getCurrentTheme(maths_question.this));
                }
            }
        });


        // Show difficulty selection dialog if no difficulty is set
        if (savedInstanceState != null) {
            // Restore state if available
            currentDifficulty = savedInstanceState.getString("currentDifficulty", "");
            score = savedInstanceState.getInt("score", 0);
            isTimerRunning = savedInstanceState.getBoolean("isTimerRunning", false);
            score_TXT.setText(String.valueOf(score));
            if (isTimerRunning) {
                long timeLeft = savedInstanceState.getLong("timeLeft", 120000);
                startTimer(timeLeft);
            }
        }

        if (currentDifficulty.isEmpty()) {
            showDifficultyDialog();
        } else {
            generateQuestion();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("currentDifficulty", currentDifficulty);
        outState.putInt("score", score);
        outState.putBoolean("isTimerRunning", isTimerRunning);
        if (countDownTimer != null) {
            String timeText = Timer.getText().toString();
            String[] parts = timeText.split(":");
            long minutes = Long.parseLong(parts[0]);
            long seconds = Long.parseLong(parts[1]);
            long timeLeft = (minutes * 60 + seconds) * 1000;
            outState.putLong("timeLeft", timeLeft);
        }
    }

    private void updateTheme() {
        int currentTheme = ThemeManager.getCurrentTheme(this);

        ConstraintLayout mainLayout = findViewById(R.id.main);
        CardView cardView = findViewById(R.id.cardView);
        TextView mathsGameTab = findViewById(R.id.maths_question_activity_page4);

        // Update main background
        mainLayout.setBackgroundResource(ThemeManager.getMathsGameBackgroundResource(this));

        // Update card background
        cardView.setCardBackgroundColor(ContextCompat.getColor(this, ThemeManager.getMathsGameCardBackgroundResource(this)));


        // Update timer background
        ConstraintLayout timerLayout = findViewById(R.id.constraintLayout5);

        // Update maths game tab
        switch (currentTheme) {
            case ThemeManager.THEME_BLUE:
                mathsGameTab.setBackgroundResource(R.drawable.tab_background2_blue);
                timerLayout.setBackgroundResource(R.color.accent_blue);
                break;
            case ThemeManager.THEME_GREEN:
                mathsGameTab.setBackgroundResource(R.drawable.tab_background2_green);
                timerLayout.setBackgroundResource(R.color.maths_green);

                break;
            case ThemeManager.THEME_YELLOW:
            default:
                mathsGameTab.setBackgroundResource(R.drawable.tab_background2);
                timerLayout.setBackgroundResource(R.color.accent);
                break;
        }

        // Update text colors
        TextView[] textViews = {
                findViewById(R.id.tv_score_label),
                findViewById(R.id.score_TXT),
                findViewById(R.id.Timer),
                findViewById(R.id.maths_question_TXT),
                findViewById(R.id.maths_answer)
        };

        for (TextView tv : textViews) {
            tv.setTextColor(ContextCompat.getColor(this, currentTheme == ThemeManager.THEME_BLUE ?
                    R.color.white : currentTheme == ThemeManager.THEME_GREEN ?
                    R.color.white : R.color.white));
        }
    }

    private void showDifficultyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.maths_level, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // Apply theme to dialog
        CardView cardView = dialogView.findViewById(R.id.cardView);
        LinearLayout linearLayout = dialogView.findViewById(R.id.linear_level_card);

        int currentTheme = ThemeManager.getCurrentTheme(this);
        int buttonBackgroundRes = ThemeManager.getButtonActionResource(this);

        cardView.setCardBackgroundColor(ContextCompat.getColor(this, ThemeManager.getMathsGameCardBackgroundResource(this)));
        linearLayout.setBackgroundResource(currentTheme == ThemeManager.THEME_BLUE ?
                R.drawable.input_background_blue : currentTheme == ThemeManager.THEME_GREEN ?
                R.drawable.input_background_green : R.drawable.input_background);

        AppCompatButton easyBtn = dialogView.findViewById(R.id.easy_level_btn);
        AppCompatButton mediumBtn = dialogView.findViewById(R.id.medium_level_btn);
        AppCompatButton hardBtn = dialogView.findViewById(R.id.hard_level_btn);

        easyBtn.setBackgroundResource(buttonBackgroundRes);
        mediumBtn.setBackgroundResource(buttonBackgroundRes);
        hardBtn.setBackgroundResource(buttonBackgroundRes);

        easyBtn.setTextColor(ContextCompat.getColor(this, R.color.white));
        mediumBtn.setTextColor(ContextCompat.getColor(this, R.color.white));
        hardBtn.setTextColor(ContextCompat.getColor(this, R.color.white));

        dialog.setCancelable(true);
        dialog.setOnCancelListener(dialogInterface -> finish());

        // Set up button click listeners with TapTargetSequence
        View.OnClickListener difficultyListener = v -> {
            if (v.getId() == R.id.easy_level_btn) {
                currentDifficulty = "Easy";
            } else if (v.getId() == R.id.medium_level_btn) {
                currentDifficulty = "Medium";
            } else if (v.getId() == R.id.hard_level_btn) {
                currentDifficulty = "Hard";
            }
            generateQuestion();
            dialog.dismiss();
            boolean isFirstMathsGame = prefs.getBoolean("isFirstMathsGame", true);
            if (isFirstMathsGame) {
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
                                TapTarget.forView(findViewById(R.id.score_TXT), "Score", "Track your score here")
                                        .outerCircleColor(outerCircleColor)
                                        .titleTextColor(android.R.color.white)
                                        .descriptionTextColor(android.R.color.white)
                                        .titleTypeface(ResourcesCompat.getFont(this, R.font.castoro))
                                        .descriptionTypeface(ResourcesCompat.getFont(this, R.font.castoro))
                                        .dimColor(android.R.color.black)
                                        .transparentTarget(true)
                                        .drawShadow(true)
                                        .titleTextSize(20)
                                        .descriptionTextSize(16)
                                        .targetRadius(20)
                                        .cancelable(false),
                                TapTarget.forView(findViewById(R.id.Timer), "Timer", "Time remaining for the round")
                                        .outerCircleColor(outerCircleColor)
                                        .titleTextColor(android.R.color.white)
                                        .descriptionTextColor(android.R.color.white)
                                        .titleTypeface(ResourcesCompat.getFont(this, R.font.castoro))
                                        .descriptionTypeface(ResourcesCompat.getFont(this, R.font.castoro))
                                        .dimColor(android.R.color.black)
                                        .transparentTarget(true)
                                        .drawShadow(true)
                                        .titleTextSize(20)
                                        .descriptionTextSize(16)
                                        .targetRadius(30)
                                        .cancelable(false),
                                TapTarget.forView(findViewById(R.id.maths_answer), "Answer Input", "Enter your answer here")
                                        .outerCircleColor(outerCircleColor)
                                        .titleTextColor(android.R.color.white)
                                        .descriptionTextColor(android.R.color.white)
                                        .titleTypeface(ResourcesCompat.getFont(this, R.font.castoro))
                                        .descriptionTypeface(ResourcesCompat.getFont(this, R.font.castoro))
                                        .dimColor(android.R.color.black)
                                        .transparentTarget(true)
                                        .drawShadow(true)
                                        .titleTextSize(20)
                                        .descriptionTextSize(16)
                                        .targetRadius(20)
                                        .cancelable(false),
                                TapTarget.forView(findViewById(R.id.leave_game_btn), "Leave Game", "Exit the game")
                                        .outerCircleColor(outerCircleColor)
                                        .titleTextColor(android.R.color.white)
                                        .descriptionTextColor(android.R.color.white)
                                        .titleTypeface(ResourcesCompat.getFont(this, R.font.castoro))
                                        .descriptionTypeface(ResourcesCompat.getFont(this, R.font.castoro))
                                        .dimColor(android.R.color.black)
                                        .transparentTarget(true)
                                        .drawShadow(true)
                                        .titleTextSize(20)
                                        .descriptionTextSize(16)
                                        .targetRadius(20)
                                        .cancelable(false),
                                TapTarget.forView(findViewById(R.id.restart_game_btn), "Restart Game", "Start a new round")
                                        .outerCircleColor(outerCircleColor)
                                        .titleTextColor(android.R.color.white)
                                        .descriptionTextColor(android.R.color.white)
                                        .titleTypeface(ResourcesCompat.getFont(this, R.font.castoro))
                                        .descriptionTypeface(ResourcesCompat.getFont(this, R.font.castoro))
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
                                editor.putBoolean("isFirstMathsGame", false);
                                editor.apply();
                                startTimer(120000); // Start timer only after sequence finishes
                            }

                            @Override
                            public void onSequenceStep(TapTarget last, boolean targetActivated) {}

                            @Override
                            public void onSequenceCanceled(TapTarget last) {}
                        })
                        .start();
            } else {
                startTimer(120000); // Start timer if not first time
            }
        };

        easyBtn.setOnClickListener(difficultyListener);
        mediumBtn.setOnClickListener(difficultyListener);
        hardBtn.setOnClickListener(difficultyListener);

        dialog.show();
        applyThemeToUI(currentTheme);
    }
    private void applyThemeToUI(int currentTheme) {
        // Get references to the views
        ConstraintLayout constraintLayout4 = findViewById(R.id.constraintLayout4);
        AppCompatButton restartGameBtn = findViewById(R.id.restart_game_btn);
        AppCompatButton leaveGameBtn = findViewById(R.id.leave_game_btn);
        EditText mathsAnswer = findViewById(R.id.maths_answer);

        // Apply background color for constraintLayout4 based on the current theme
        if (currentTheme == ThemeManager.THEME_BLUE) {
            constraintLayout4.setBackgroundResource(R.drawable.input_background_blue);
        } else if (currentTheme == ThemeManager.THEME_GREEN) {
            constraintLayout4.setBackgroundResource(R.drawable.input_background_green);
        } else { // Default (Yellow theme or other)
            constraintLayout4.setBackgroundResource(R.drawable.input_background);
        }

        // Apply background for restart_game_btn
        if (currentTheme == ThemeManager.THEME_BLUE) {
            restartGameBtn.setBackgroundResource(R.drawable.button_action_blue);
        } else if (currentTheme == ThemeManager.THEME_GREEN) {
            restartGameBtn.setBackgroundResource(R.drawable.button_action_green);
        } else {
            restartGameBtn.setBackgroundResource(R.drawable.button_action);
        }

        // Apply background for leave_game_btn
        if (currentTheme == ThemeManager.THEME_BLUE) {
            leaveGameBtn.setBackgroundResource(R.drawable.input_background_blue);
        } else if (currentTheme == ThemeManager.THEME_GREEN) {
            leaveGameBtn.setBackgroundResource(R.drawable.input_background_green);
        } else {
            leaveGameBtn.setBackgroundResource(R.drawable.button_action2);
        }

        // Apply background for maths_answer input field
        if (currentTheme == ThemeManager.THEME_BLUE) {
            mathsAnswer.setBackgroundResource(R.drawable.input_background_blue);
        } else if (currentTheme == ThemeManager.THEME_GREEN) {
            mathsAnswer.setBackgroundResource(R.drawable.input_background_green);
        } else {
            mathsAnswer.setBackgroundResource(R.drawable.input_background);
        }
    }
    private void startTimer(long millis) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        isTimerRunning = true;
        countDownTimer = new CountDownTimer(millis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int minutes = (int) (millisUntilFinished / 1000) / 60;
                int seconds = (int) (millisUntilFinished / 1000) % 60;
                Timer.setText(String.format("%d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                Timer.setText("0:00");
                isTimerRunning = false;
                showWinRoundDialog();
            }
        }.start();
    }

    private void generateQuestion() {
        int num1, num2;
        String operator;
        int answer;

        do {
            switch (currentDifficulty) {
                case "Easy":
                    int opIndexEasy = random.nextInt(4);
                    operator = opIndexEasy == 0 ? "+" : opIndexEasy == 1 ? "×" : opIndexEasy == 2 ? "÷" : "-";
                    // Prioritize subtraction, multiplication, division around 100
                    if (operator.equals("-")) {
                        num1 = random.nextInt(51) + 50; // 50-100
                        num2 = random.nextInt(41) + 10; // 10-50
                    } else if (operator.equals("×")) {
                        num1 = random.nextInt(11) + 10; // 10-20
                        num2 = random.nextInt(6) + 1;   // 1-6
                    } else if (operator.equals("÷")) {
                        num2 = random.nextInt(5) + 1;   // 1-5
                        answer = random.nextInt(20) + 1; // 1-20
                        num1 = num2 * answer;           // Ensure remainder = 0
                    } else {
                        num1 = random.nextInt(51) + 50; // 50-100
                        num2 = random.nextInt(41) + 10; // 10-50
                    }
                    break;
                case "Medium":
                    int opIndexMedium = random.nextInt(4);
                    operator = opIndexMedium == 0 ? "+" : opIndexMedium == 1 ? "×" : opIndexMedium == 2 ? "÷" : "-";
                    // Prioritize subtraction, multiplication, division around 200
                    if (operator.equals("-")) {
                        num1 = random.nextInt(51) + 150; // 150-200
                        num2 = random.nextInt(51) + 50;  // 50-100
                    } else if (operator.equals("×")) {
                        num1 = random.nextInt(11) + 10;  // 10-20
                        num2 = random.nextInt(6) + 2;    // 2-7
                    } else if (operator.equals("÷")) {
                        num2 = random.nextInt(6) + 5;    // 5-10
                        answer = random.nextInt(20) + 5; // 5-24
                        num1 = num2 * answer;            // Ensure remainder = 0
                    } else {
                        num1 = random.nextInt(51) + 150; // 150-200
                        num2 = random.nextInt(51) + 50;  // 50-100
                    }
                    break;
                case "Hard":
                    int opIndexHard = random.nextInt(4);
                    operator = opIndexHard == 0 ? "+" : opIndexHard == 1 ? "×" : opIndexHard == 2 ? "÷" : "-";
                    // Prioritize subtraction, multiplication, division around 300
                    if (operator.equals("-")) {
                        num1 = random.nextInt(51) + 250; // 250-300
                        num2 = random.nextInt(51) + 100; // 100-150
                    } else if (operator.equals("×")) {
                        num1 = random.nextInt(11) + 15;  // 15-25
                        num2 = random.nextInt(6) + 3;    // 3-8
                    } else if (operator.equals("÷")) {
                        num2 = random.nextInt(6) + 10;   // 10-15
                        answer = random.nextInt(20) + 10;// 10-29
                        num1 = num2 * answer;            // Ensure remainder = 0
                    } else {
                        num1 = random.nextInt(51) + 250; // 250-300
                        num2 = random.nextInt(51) + 100; // 100-150
                    }
                    break;
                default:
                    return;
            }
            answer = calculateAnswer(num1, num2, operator);
        } while (answer >= 400 || (operator.equals("÷") && num2 != 0 && num1 % num2 != 0) ||
                (operator.equals("-") && num1 < num2) || // No negative results
                (operator.equals("×") && answer > 99) || // 2-digit product limit
                (operator.equals("-") && answer > 99) || // 2-digit subtraction limit
                (num1 == 0 || num2 == 0)); // Avoid trivial questions like 10+0

        maths_question_TXT.setText(num1 + " " + operator + " " + num2);
        maths_answer.setText("");
    }

    private int calculateAnswer(int num1, int num2, String operator) {
        switch (operator) {
            case "+": return num1 + num2;
            case "-": return num1 - num2;
            case "×": return num1 * num2;
            case "÷": return num1 / num2;
            default: return 0;
        }
    }

    private void checkAnswer(String userAnswer) {
        try {
            if (userAnswer.isEmpty()) {
                applyThemeToUI(ThemeManager.getCurrentTheme(this)); // Reapply theme
                return;
            }

            int answer = Integer.parseInt(userAnswer);
            String question = maths_question_TXT.getText().toString();
            String[] parts = question.split(" ");
            int num1 = Integer.parseInt(parts[0]);
            String operator = parts[1];
            int num2 = Integer.parseInt(parts[2]);
            int correctAnswer = calculateAnswer(num1, num2, operator);

            if (answer == correctAnswer) {
                score++;
                score_TXT.setText(String.valueOf(score));
                applyThemeToUI(ThemeManager.getCurrentTheme(this)); // Reapply theme
                generateQuestion();
            } else {
                maths_answer.setBackground(ContextCompat.getDrawable(this, R.drawable.maths_wrong_answer_bg));
            }
        } catch (NumberFormatException e) {
            maths_answer.setBackground(ContextCompat.getDrawable(this, R.drawable.maths_wrong_answer_bg));
        }
    }

    private void showWinRoundDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.maths_win_round, null);
        builder.setView(dialogView);

        // Make dialog non-cancelable
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        // Apply theme to dialog
        int currentTheme = ThemeManager.getCurrentTheme(this);

        ConstraintLayout constraintLayout = dialogView.findViewById(R.id.constraintLayout);
        ConstraintLayout linear_GameOver_card = dialogView.findViewById(R.id.linear_GameOver_card);
        AppCompatButton restartBtn = dialogView.findViewById(R.id.win_restart_game_btn);
        AppCompatButton leaveBtn = dialogView.findViewById(R.id.win_leave_game_btn);

        // Dynamically change the background based on theme
        if (currentTheme == ThemeManager.THEME_BLUE) {
            constraintLayout.setBackgroundResource(R.drawable.input_background_blue);
            linear_GameOver_card.setBackgroundResource(R.drawable.input_background_blue);
            restartBtn.setBackgroundResource(R.drawable.button_action_blue);
            leaveBtn.setBackgroundResource(R.drawable.input_background_blue);
        } else if (currentTheme == ThemeManager.THEME_GREEN) {
            constraintLayout.setBackgroundResource(R.drawable.input_background_green);
            linear_GameOver_card.setBackgroundResource(R.drawable.input_background_green);
            restartBtn.setBackgroundResource(R.drawable.button_action_green);
            leaveBtn.setBackgroundResource(R.drawable.input_background_green);
        } else {
            constraintLayout.setBackgroundResource(R.drawable.input_background);
            linear_GameOver_card.setBackgroundResource(R.drawable.input_background);
            restartBtn.setBackgroundResource(R.drawable.button_action);
            leaveBtn.setBackgroundResource(R.drawable.input_background);
        }

        // Set the score text
        TextView winScoreTXT = dialogView.findViewById(R.id.win_score_TXT);
        winScoreTXT.setText(String.valueOf(score));

        // Restart button click listener
        restartBtn.setOnClickListener(v -> {
            score = 0;
            score_TXT.setText(String.valueOf(score));
            startTimer(120000);
            generateQuestion();
            maths_answer.setText("");
            dialog.dismiss();
        });

        // Leave button click listener
        leaveBtn.setOnClickListener(v -> {
            dialog.dismiss();
            showLeaveDialog();
        });

        // Prevent clicks on the dialog background
        dialogView.setOnClickListener(v -> {
            // Do nothing - prevents clicks from passing through
        });

        dialog.show();
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        showLeaveDialog();
    }


    private void showLeaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Leave Game ?")
                .setMessage("Do you want to leave the game?")
                .setPositiveButton("OK", (dialog, which) -> {
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                    }
                    finish(); // End the current activity
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        // Set custom font
        Typeface typeface = ResourcesCompat.getFont(this, R.font.caudex); // Replace with your actual font file name

        // Set font for message text
        TextView messageView = dialog.findViewById(android.R.id.message);
        if (messageView != null) {
            messageView.setTypeface(typeface);
        }

        // Set font for title text
        int titleId = getResources().getIdentifier("alertTitle", "id", "android");
        TextView titleView = dialog.findViewById(titleId);
        if (titleView != null) {
            titleView.setTypeface(typeface);
        }

        // Set font for buttons
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTypeface(typeface);
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTypeface(typeface);
    }

    private void showNavigationDialog(String targetActivity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Leave Game?")
                .setMessage("Do you want to leave the game?")
                .setPositiveButton("OK", (dialog, which) -> {
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                    }
                    Class<?> activityClass;
                    switch (targetActivity) {
                        case "calculator":
                            activityClass = MainActivity.class;
                            break;
                        case "currency":
                            activityClass = currency_converter.class;
                            break;
                        case "unit":
                            activityClass = unit_converter.class;
                            break;
                        case "rating":
                            activityClass = rating_feedback.class;
                            break;
                        default:
                            return;
                    }
                    Intent intent = new Intent(maths_question.this, activityClass);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();

        // Set Caudex font for dialog text
        Typeface caudex = ResourcesCompat.getFont(this, R.font.caudex);
        TextView messageView = dialog.findViewById(android.R.id.message);
        if (messageView != null) {
            messageView.setTypeface(caudex);
        }
        int titleId = getResources().getIdentifier("alertTitle", "id", "android");
        TextView titleView = dialog.findViewById(titleId);
        if (titleView != null) {
            titleView.setTypeface(caudex);
        }
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTypeface(caudex);
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTypeface(caudex);
    }
    private void showRestartDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Restart Game")
                .setMessage("Do you want to restart your game?")
                .setPositiveButton("OK", (dialog, which) -> {
                    score = 0;
                    score_TXT.setText(String.valueOf(score));
                    startTimer(120000); // Restart the timer (2 minutes)
                    generateQuestion(); // Generate a new question
                    maths_answer.setText(""); // Clear the answer input
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        // Load your custom font from res/font/your_custom_font.ttf
        Typeface typeface = ResourcesCompat.getFont(this, R.font.castoro); // Replace with actual font file name

        // Set font for message
        TextView messageView = dialog.findViewById(android.R.id.message);
        if (messageView != null) {
            messageView.setTypeface(typeface);
        }

        // Set font for title
        int titleId = getResources().getIdentifier("alertTitle", "id", "android");
        TextView titleView = dialog.findViewById(titleId);
        if (titleView != null) {
            titleView.setTypeface(typeface);
        }

        // Set font for dialog buttons
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTypeface(typeface);
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTypeface(typeface);
    }

    @Override
    protected void onDestroy() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        super.onDestroy();
    }
}