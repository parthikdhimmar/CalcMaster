package com.example.calcmaster;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;

public class rating_feedback extends AppCompatActivity {

    private EditText userIdEditText, feedbackEditText;
    private RatingBar ratingBar;
    private AppCompatButton sendButton;
    private TextView myEmailText;
    private ConstraintLayout mainLayout;
    private CardView contentCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_feedback);

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

        // Initialize views
        userIdEditText = findViewById(R.id.enter_user_Id);
        feedbackEditText = findViewById(R.id.feedbackEditText);
        ratingBar = findViewById(R.id.ratingBar);
        sendButton = findViewById(R.id.sendButton);
        myEmailText = findViewById(R.id.my_id_txt);
        mainLayout = findViewById(R.id.main);
        contentCard = findViewById(R.id.content_card);

        updateTheme();


        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        boolean isFirstLaunch = prefs.getBoolean("isFirstFeedbackLaunch", true);
        if (isFirstLaunch) {
            new TapTargetSequence(this)
                    .targets(
                            TapTarget.forView(userIdEditText, "User ID", "Enter your user ID here")
                                    .outerCircleColor(ThemeManager.getCurrentTheme(this) == ThemeManager.THEME_BLUE ? R.color.background_blue_TT :
                                            ThemeManager.getCurrentTheme(this) == ThemeManager.THEME_GREEN ? R.color.background_green_TT : R.color.background_TT)
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
                            TapTarget.forView(ratingBar, "Rating", "Rate the app here")
                                    .outerCircleColor(ThemeManager.getCurrentTheme(this) == ThemeManager.THEME_BLUE ? R.color.background_blue_TT :
                                            ThemeManager.getCurrentTheme(this) == ThemeManager.THEME_GREEN ? R.color.background_green_TT : R.color.background_TT)
                                    .titleTextColor(android.R.color.white)
                                    .descriptionTextColor(android.R.color.white)
                                    .titleTypeface(ResourcesCompat.getFont(this, R.font.caudex))
                                    .descriptionTypeface(ResourcesCompat.getFont(this, R.font.caudex))
                                    .dimColor(android.R.color.black)
                                    .transparentTarget(true)
                                    .drawShadow(true)
                                    .titleTextSize(20)
                                    .descriptionTextSize(16)
                                    .targetRadius(30)
                                    .cancelable(false),
                            TapTarget.forView(feedbackEditText, "Feedback", "Provide your feedback here")
                                    .outerCircleColor(ThemeManager.getCurrentTheme(this) == ThemeManager.THEME_BLUE ? R.color.background_blue_TT :
                                            ThemeManager.getCurrentTheme(this) == ThemeManager.THEME_GREEN ? R.color.background_green_TT : R.color.background_TT)
                                    .titleTextColor(android.R.color.white)
                                    .descriptionTextColor(android.R.color.white)
                                    .titleTypeface(ResourcesCompat.getFont(this, R.font.caudex))
                                    .descriptionTypeface(ResourcesCompat.getFont(this, R.font.caudex))
                                    .dimColor(android.R.color.black)
                                    .transparentTarget(true)
                                    .drawShadow(true)
                                    .titleTextSize(20)
                                    .descriptionTextSize(16)
                                    .targetRadius(40)
                                    .cancelable(false)
                    )
                    .listener(new TapTargetSequence.Listener() {
                        @Override
                        public void onSequenceFinish() {
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean("isFirstFeedbackLaunch", false);
                            editor.apply();
                        }
                        @Override
                        public void onSequenceStep(TapTarget last, boolean targetActivated) {}
                        @Override
                        public void onSequenceCanceled(TapTarget last) {}
                    })
                    .start();
        }


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFeedbackEmail();
            }
        });

        TextView calculator_page5 = findViewById(R.id.calculator_page5);

        calculator_page5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(rating_feedback.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        TextView currency_converter_activity_page5 = findViewById(R.id.currency_converter_activity_page5);

        currency_converter_activity_page5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(rating_feedback.this, currency_converter.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });


        TextView maths_question_activity_page5 = findViewById(R.id.maths_question_activity_page5);

        maths_question_activity_page5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(rating_feedback.this, maths_question.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        TextView unit_converter_activity_page5 = findViewById(R.id.unit_converter_activity_page5);

        unit_converter_activity_page5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(rating_feedback.this, rating_feedback.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        // Scroll to center the navigation items initially
        HorizontalScrollView navScroll = findViewById(R.id.nav_scroll_page5);
        navScroll.post(new Runnable() {
            @Override
            public void run() {
                // Calculate the scroll position to center the "Maths Game" item
                TextView mathsGame = findViewById(R.id.rating_activity_btn_page5);
                int scrollTo = (mathsGame.getLeft() - (navScroll.getWidth() / 2)) + (mathsGame.getWidth() / 2);
                navScroll.smoothScrollTo(scrollTo, 0);
            }
        });
    }

    private void sendFeedbackEmail() {
        // Get user inputs
        String userId = userIdEditText.getText().toString().trim();
        String feedback = feedbackEditText.getText().toString().trim();
        float rating = ratingBar.getRating();
        String recipientEmail = myEmailText.getText().toString().trim();

        // Validate inputs
        if (userId.isEmpty() || feedback.isEmpty() || rating == 0) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create email content
        String subject = "App Feedback from User ID: " + userId;
        String message = "User ID: " + userId + "\n\n" +
                "Rating: " + rating + "/5\n\n" +
                "Feedback:\n" + feedback;

        // Create email intent
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{recipientEmail});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send email using..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "No email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateTheme() {
        int currentTheme = ThemeManager.getCurrentTheme(this);


        TextView rating_activity_btn_page5=findViewById(R.id.rating_activity_btn_page5);
        TextView feedback_txt=findViewById(R.id.feedback_txt);
        TextView email_txt=findViewById(R.id.email_txt);
        TextView my_id_txt=findViewById(R.id.my_id_txt);
        EditText my_enter_user_Id=findViewById(R.id.enter_user_Id);
        EditText feedbackEditText=findViewById(R.id.feedbackEditText);

        // Update main background
        mainLayout.setBackgroundResource(ThemeManager.getMathsGameBackgroundResource(this));
        // Update card background
        contentCard.setCardBackgroundColor(ContextCompat.getColor(this, ThemeManager.getMathsGameCardBackgroundResource(this)));

        // Update button background
        if (currentTheme == ThemeManager.THEME_BLUE) {
            sendButton.setBackgroundResource(R.drawable.button_action_blue);
            ratingBar.setProgressTintList(ContextCompat.getColorStateList(this, R.color.accent_blue));
            my_enter_user_Id.setBackgroundResource(R.drawable.input_background_blue);
            feedbackEditText.setBackgroundResource(R.drawable.input_background_blue);
            rating_activity_btn_page5.setBackgroundResource(R.drawable.tab_background2_blue);
            feedback_txt.setTextColor(ContextCompat.getColor(this, R.color.accent_blue));
            email_txt.setTextColor(ContextCompat.getColor(this, R.color.accent_blue));
            my_id_txt.setTextColor(ContextCompat.getColor(this, R.color.accent_blue));

        } else if (currentTheme == ThemeManager.THEME_GREEN) {
            sendButton.setBackgroundResource(R.drawable.button_action_green);
            ratingBar.setProgressTintList(ContextCompat.getColorStateList(this, R.color.accent_green));
            my_enter_user_Id.setBackgroundResource(R.drawable.input_background_green);
            feedbackEditText.setBackgroundResource(R.drawable.input_background_green);
            rating_activity_btn_page5.setBackgroundResource(R.drawable.tab_background2_green);
            feedback_txt.setTextColor(ContextCompat.getColor(this, R.color.accent_green));
            email_txt.setTextColor(ContextCompat.getColor(this, R.color.accent_green));
            my_id_txt.setTextColor(ContextCompat.getColor(this, R.color.accent_green));

        } else {
            sendButton.setBackgroundResource(R.drawable.button_action);
            ratingBar.setProgressTintList(ContextCompat.getColorStateList(this, R.color.accent));

            my_enter_user_Id.setBackgroundResource(R.drawable.input_background);
            feedbackEditText.setBackgroundResource(R.drawable.input_background);
            rating_activity_btn_page5.setBackgroundResource(R.drawable.tab_background2);
            feedback_txt.setTextColor(ContextCompat.getColor(this, R.color.accent));
            email_txt.setTextColor(ContextCompat.getColor(this, R.color.accent));
            my_id_txt.setTextColor(ContextCompat.getColor(this, R.color.accent));


        }


    }

}