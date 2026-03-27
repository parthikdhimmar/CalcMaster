package com.example.calcmaster;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class currency_converter extends AppCompatActivity {

     private final String[] currencyCodes = {"INR", "AED", "AFN", "AUD", "BDT", "BRL", "BTN", "CAD", "CHF", "CNY", "EGP", "EUR", "GEL", "HKD", "IDR", "IRR", "JPY", "KRW", "KWD", "LKR", "MVR", "MYR", "NPR", "NZD", "OMR", "PKR", "QAR", "RUB", "SAR", "SGD", "THB", "UAH", "USD", "VND", "ZAR"};
    private EditText amountInput;
    private Spinner fromCurrencySpinner, toCurrencySpinner;
    private AppCompatButton convertButton;
    private TextView resultText;
    private AutoCompleteTextView countrySearchInput;
    private AppCompatButton searchButton;
    private TextView currencyResultText;
    private CurrencyApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_converter);


        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.background));
        }

        // Initialize views
        amountInput = findViewById(R.id.amountInput);
        fromCurrencySpinner = findViewById(R.id.fromCurrencySpinner);
        toCurrencySpinner = findViewById(R.id.toCurrencySpinner);
        convertButton = findViewById(R.id.convertButton);
        resultText = findViewById(R.id.resultText);
        countrySearchInput = findViewById(R.id.countrySearchInput);
        searchButton = findViewById(R.id.searchButton);
        currencyResultText = findViewById(R.id.currencyResultText);



        updateTheme();


        SharedPreferences prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        boolean isFirstCurrencyConverter = prefs.getBoolean("isFirstCurrencyConverter", true);
        if (isFirstCurrencyConverter) {
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
                            TapTarget.forView(findViewById(R.id.amountInput), "Amount", "Enter the amount to convert")
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
                                    .targetRadius(20)
                                    .cancelable(false),
                            TapTarget.forView(findViewById(R.id.fromCurrencySpinner), "From Currency", "Select the source currency")
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
                                    .targetRadius(20)
                                    .cancelable(false),
                            TapTarget.forView(findViewById(R.id.countrySearchInput), "Country Search", "Search for a country or currency")
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
                                    .targetRadius(20)
                                    .cancelable(false)
                    )
                    .listener(new TapTargetSequence.Listener() {
                        @Override
                        public void onSequenceFinish() {
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean("isFirstCurrencyConverter", false);
                            editor.apply();
                        }

                        @Override
                        public void onSequenceStep(TapTarget last, boolean targetActivated) {}

                        @Override
                        public void onSequenceCanceled(TapTarget last) {}
                    })
                    .start();
        }



        TextView calculator_page2 = findViewById(R.id.calculator_page2);

        calculator_page2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(currency_converter.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        TextView unit_converter_activity_page2 = findViewById(R.id.unit_converter_activity_page2);

        unit_converter_activity_page2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(currency_converter.this, unit_converter.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });


        TextView maths_question_activity_page2 = findViewById(R.id.maths_question_activity_page2);

        maths_question_activity_page2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(currency_converter.this, maths_question.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        TextView rating_activity_btn_page2 = findViewById(R.id.rating_activity_btn_page2);

        rating_activity_btn_page2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(currency_converter.this, rating_feedback.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });


        // Set up Retrofit
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.exchangerate-api.com/v4/latest/") // Replace with your API base URL
                .addConverterFactory(GsonConverterFactory.create()).build();

        apiService = retrofit.create(CurrencyApiService.class);

        // Set up Spinners
        String[] currencyCodes = {"INR", "AED", "AFN", "AUD", "BDT", "BRL", "BTN", "CAD", "CHF", "CNY", "EGP", "EUR", "GEL", "HKD", "IDR", "IRR", "JPY", "KRW", "KWD", "LKR", "MVR", "MYR", "NPR", "NZD", "OMR", "PKR", "QAR", "RUB", "SAR", "SGD", "THB", "UAH", "USD", "VND", "ZAR"};


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, currencyCodes);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item); // Set custom dropdown item


        // Determine dropdown item layout based on theme
        int dropdownItemRes;
        switch (ThemeManager.getCurrentTheme(this)) {
            case ThemeManager.THEME_BLUE:
                dropdownItemRes = R.layout.spinner_dropdown_item_blue;
                break;
            case ThemeManager.THEME_GREEN:
                dropdownItemRes = R.layout.spinner_dropdown_item_green;
                break;
            case ThemeManager.THEME_YELLOW:
                dropdownItemRes = R.layout.spinner_dropdown_item;
                break;
            default:
                dropdownItemRes = R.layout.spinner_dropdown_item; // Fallback
                break;
        }

// Set up adapter with themed dropdown item
        adapter.setDropDownViewResource(dropdownItemRes);

// Set adapter and popup background for both spinners
        fromCurrencySpinner.setAdapter(adapter);
        toCurrencySpinner.setAdapter(adapter);

        fromCurrencySpinner.setPopupBackgroundResource(ThemeManager.getDropdownBackgroundResource(this));
        toCurrencySpinner.setPopupBackgroundResource(ThemeManager.getDropdownBackgroundResource(this));






        // Set "USD" as the default selected item in toCurrencySpinner
        String defaultCurrency = "USD";
        int position = Arrays.asList(currencyCodes).indexOf(defaultCurrency);
        if (position >= 0) {
            toCurrencySpinner.setSelection(position);
        }

        // Set up Convert Button
        convertButton.setOnClickListener(v -> convertCurrency());

        // Decide dropdown item layout based on theme
        int dropdownLayout;
        int dropdownBackground;
        switch (ThemeManager.getCurrentTheme(this)) {
            case ThemeManager.THEME_BLUE:
                dropdownLayout = R.layout.dropdown_item_blue;
                dropdownBackground = R.drawable.dropdown_background_blue;
                break;
            case ThemeManager.THEME_GREEN:
                dropdownLayout = R.layout.dropdown_item_green;
                dropdownBackground = R.drawable.dropdown_background_green;
                break;
            case ThemeManager.THEME_YELLOW:
            default:
                dropdownLayout = R.layout.dropdown_item_yellow;
                dropdownBackground = R.drawable.dropdown_background;
                break;
        }

// Set up adapter
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(
                this,
                dropdownLayout,
                new ArrayList<>(CurrencyUtils.COUNTRY_TO_CURRENCY.keySet())
        );

// Apply to AutoCompleteTextView
        countrySearchInput.setDropDownBackgroundResource(dropdownBackground);
        countrySearchInput.setAdapter(countryAdapter);





        // Set up Search Button
        searchButton.setOnClickListener(v -> searchCurrency());
    }

    private void convertCurrency() {
        String fromCurrency = fromCurrencySpinner.getSelectedItem().toString();
        String toCurrency = toCurrencySpinner.getSelectedItem().toString();
        String amount = amountInput.getText().toString();

        if (amount.isEmpty()) {
            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            return;
        }
        // Fetch exchange rates
        Call<ExchangeRates> call = apiService.getExchangeRates(fromCurrency);
        call.enqueue(new Callback<ExchangeRates>() {
            @Override
            public void onResponse(Call<ExchangeRates> call, Response<ExchangeRates> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ExchangeRates rates = response.body();
                    Double exchangeRate = rates.getRates().get(toCurrency);
                    if (exchangeRate != null) {
                        double result = Double.parseDouble(amount) * exchangeRate;
                        resultText.setText(String.format("%.2f %s", result, toCurrency));
                    } else {
                        Toast.makeText(currency_converter.this, "Currency not found in exchange rates", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(currency_converter.this, "Failed to fetch exchange rates", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ExchangeRates> call, Throwable t) {
                Toast.makeText(currency_converter.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchCurrency() {
        String searchText = countrySearchInput.getText().toString().trim();
        if (searchText.isEmpty()) {
            Toast.makeText(this, "Please enter a country name or currency code", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the search text is a currency code (e.g., "INR", "USD")
        String currencyInfo = CurrencyUtils.COUNTRY_TO_CURRENCY.get(searchText.toUpperCase());
        if (currencyInfo != null) {
            // If the search text is a currency code, display the corresponding currency info
            currencyResultText.setText(String.format("Currency code %s is %s", searchText.toUpperCase(), currencyInfo));
        } else {
            // If the search text is not a currency code, assume it's a country name and search for it
            String countryCurrency = CurrencyUtils.getCurrencyForCountry(searchText);
            if (countryCurrency != null) {
                currencyResultText.setText(String.format("Currency used in %s is %s", searchText, countryCurrency));
            } else {
                currencyResultText.setText(String.format("Currency for %s not found", searchText));
            }
        }
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

        // Set background colors
        ConstraintLayout mainLayout = findViewById(R.id.main);
        CardView contentCard = findViewById(R.id.content_card);
        CardView searchCard = findViewById(R.id.search_card);
        TextView textView_nav = findViewById(R.id.currency_converter_activity_page2);
        TextView resultText = findViewById(R.id.resultText);
        TextView currencyResultText = findViewById(R.id.currencyResultText);
        AutoCompleteTextView countrySearchInput = findViewById(R.id.countrySearchInput);
        EditText amountInput = findViewById(R.id.amountInput);

        // Get theme resources
        int dropdownBackgroundRes = ThemeManager.getDropdownBackgroundResource(this);
        int dropdownItemRes = ThemeManager.getSpinnerDropdownItemResource(this);

        // Update the spinners
        Spinner fromSpinner = findViewById(R.id.fromCurrencySpinner);
        Spinner toSpinner = findViewById(R.id.toCurrencySpinner);

        // Get current selections to preserve them
        String fromSelected = fromSpinner.getSelectedItem() != null ? fromSpinner.getSelectedItem().toString() : "";
        String toSelected = toSpinner.getSelectedItem() != null ? toSpinner.getSelectedItem().toString() : "";

        // Create new adapters with the updated dropdown resources
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, currencyCodes);
        adapter.setDropDownViewResource(dropdownItemRes);

        fromSpinner.setAdapter(adapter);
        toSpinner.setAdapter(adapter);

        // Restore selections
        int fromPos = Arrays.asList(currencyCodes).indexOf(fromSelected);
        if (fromPos >= 0) fromSpinner.setSelection(fromPos);

        int toPos = Arrays.asList(currencyCodes).indexOf(toSelected);
        if (toPos >= 0) toSpinner.setSelection(toPos);

        // Set the dropdown background for both spinners
        fromSpinner.setPopupBackgroundResource(dropdownBackgroundRes);
        toSpinner.setPopupBackgroundResource(dropdownBackgroundRes);

        // Update the AutoCompleteTextView dropdown background
        countrySearchInput.setDropDownBackgroundResource(dropdownBackgroundRes);

        // Set button backgrounds
        AppCompatButton convertButton = findViewById(R.id.convertButton);
        AppCompatButton searchButton = findViewById(R.id.searchButton);
        convertButton.setBackgroundResource(ThemeManager.getButtonActionResource(this));
        searchButton.setBackgroundResource(ThemeManager.getButtonActionResource(this));

        // Update text colors based on theme
        switch(currentTheme) {
            case ThemeManager.THEME_BLUE:
                mainLayout.setBackgroundResource(R.color.background_blue);
                resultText.setTextColor(ContextCompat.getColor(this, R.color.accent_blue));
                currencyResultText.setTextColor(ContextCompat.getColor(this, R.color.accent_blue));
                amountInput.setTextColor(ContextCompat.getColor(this, R.color.accent_blue));
                countrySearchInput.setTextColor(ContextCompat.getColor(this, R.color.accent_blue));

                contentCard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.card_background_blue));
                searchCard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.card_background_blue));
                amountInput.setBackgroundResource(R.drawable.input_background_blue);
                fromSpinner.setBackgroundResource(R.drawable.input_background_blue);
                toSpinner.setBackgroundResource(R.drawable.input_background_blue);
                countrySearchInput.setBackgroundResource(R.drawable.input_background_blue);
                textView_nav.setBackgroundResource(R.drawable.tab_background2_blue);
                break;
            case ThemeManager.THEME_GREEN:
                mainLayout.setBackgroundResource(R.color.background_green);
                resultText.setTextColor(ContextCompat.getColor(this, R.color.accent_green));
                currencyResultText.setTextColor(ContextCompat.getColor(this, R.color.accent_green));
                amountInput.setTextColor(ContextCompat.getColor(this, R.color.accent_green));
                countrySearchInput.setTextColor(ContextCompat.getColor(this, R.color.accent_green));

                contentCard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.card_background_green));
                searchCard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.card_background_green));
                amountInput.setBackgroundResource(R.drawable.input_background_green);
                fromSpinner.setBackgroundResource(R.drawable.input_background_green);
                toSpinner.setBackgroundResource(R.drawable.input_background_green);
                countrySearchInput.setBackgroundResource(R.drawable.input_background_green);
                textView_nav.setBackgroundResource(R.drawable.tab_background2_green);
                break;
            default:
                mainLayout.setBackgroundResource(R.color.background);
                resultText.setTextColor(ContextCompat.getColor(this, R.color.accent));
                currencyResultText.setTextColor(ContextCompat.getColor(this, R.color.accent));
                amountInput.setTextColor(ContextCompat.getColor(this, R.color.accent));
                countrySearchInput.setTextColor(ContextCompat.getColor(this, R.color.accent));

                contentCard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.card_background));
                searchCard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.card_background));
                amountInput.setBackgroundResource(R.drawable.input_background);
                fromSpinner.setBackgroundResource(R.drawable.input_background);
                toSpinner.setBackgroundResource(R.drawable.input_background);
                countrySearchInput.setBackgroundResource(R.drawable.input_background);
                textView_nav.setBackgroundResource(R.drawable.tab_background2);
                break;
        }
    }

    public static class CurrencyUtils {
        public static final Map<String, String> COUNTRY_TO_CURRENCY = new HashMap<>();

        static {
            COUNTRY_TO_CURRENCY.put("United States", "USD – United States Dollar – $");
            COUNTRY_TO_CURRENCY.put("India", "INR – Indian Rupee – ₹");
            COUNTRY_TO_CURRENCY.put("European Union", "EUR – Euro – €");
            COUNTRY_TO_CURRENCY.put("United Kingdom", "GBP – British Pound – £");
            COUNTRY_TO_CURRENCY.put("Japan", "JPY – Japanese Yen (Japan) – ¥");
            COUNTRY_TO_CURRENCY.put("United Arab Emirates", "AED – United Arab Emirates Dirham – د.إ");
            COUNTRY_TO_CURRENCY.put("Afghanistan", "AFN – Afghan Afghani  – ؋");
            COUNTRY_TO_CURRENCY.put("Australia", "AUD – Australian Dollar – $ or A$");
            COUNTRY_TO_CURRENCY.put("Bangladesh", "BDT – Bangladeshi Taka – ৳");
            COUNTRY_TO_CURRENCY.put("Brazil", "BRL – Brazilian Real – R$");
            COUNTRY_TO_CURRENCY.put("Bhutan", "BTN – Bhutanese Ngultrum – Nu. or ₼");
            COUNTRY_TO_CURRENCY.put("Canada", "CAD – Canadian Dollar – $ or C$");
            COUNTRY_TO_CURRENCY.put("Switzerland", "CHF – Swiss Franc – ₣ or CHF");
            COUNTRY_TO_CURRENCY.put("China", "CNY – Chinese Yuan – ¥ or 元");
            COUNTRY_TO_CURRENCY.put("Egypt", "EGP – Egyptian Pound – £ or ج.م");
            COUNTRY_TO_CURRENCY.put("Georgia", "GEL – Georgian Lari – ₾");
            COUNTRY_TO_CURRENCY.put("Hong Kong", "HKD – Hong Kong Dollar – $ or HK$");
            COUNTRY_TO_CURRENCY.put("Indonesia", "IDR – Indonesian Rupiah – Rp");
            COUNTRY_TO_CURRENCY.put("Iran", "IRR – Iranian Rial – ﷼");
            COUNTRY_TO_CURRENCY.put("South Korea", "KRW – South Korean Won – ₩");
            COUNTRY_TO_CURRENCY.put("Kuwait", "KWD – Kuwaiti Dinar – د.ك");
            COUNTRY_TO_CURRENCY.put("Sri Lanka", "LKR – Sri Lankan Rupee – Rs or රු");
            COUNTRY_TO_CURRENCY.put("Maldives", "MVR – Maldivian Rufiyaa  – ރ");
            COUNTRY_TO_CURRENCY.put("Malaysia", "MYR – Malaysian Ringgit – RM");
            COUNTRY_TO_CURRENCY.put("Nepal", "NPR – Nepalese Rupee – ₨ or रू");
            COUNTRY_TO_CURRENCY.put("New Zealand", "NZD – New Zealand Dollar – $ or NZ$");
            COUNTRY_TO_CURRENCY.put("Oman", "OMR – Omani Rial – ر.ع.");
            COUNTRY_TO_CURRENCY.put("Pakistan", "PKR – Pakistani Rupee – ₨ or Rs");
            COUNTRY_TO_CURRENCY.put("Qatar", "QAR – Qatari Rial – ر.ق");
            COUNTRY_TO_CURRENCY.put("Russia", "RUB – Russian Ruble – ₽");
            COUNTRY_TO_CURRENCY.put("Saudi Arabia", "SAR – Saudi Riyal – ر.س");
            COUNTRY_TO_CURRENCY.put("Singapore", "SGD – Singapore Dollar – $ or S$");
            COUNTRY_TO_CURRENCY.put("Thailand", "THB – Thai Baht – ฿");
            COUNTRY_TO_CURRENCY.put("Ukraine", "UAH – Ukrainian Hryvnia – ₴");
            COUNTRY_TO_CURRENCY.put("Vietnam", "VND – Vietnamese Đồng – ₫");
            COUNTRY_TO_CURRENCY.put("South Africa", "ZAR – South African Rand – R");

            COUNTRY_TO_CURRENCY.put("USD", "United States Dollar (USA) – $");
            COUNTRY_TO_CURRENCY.put("INR", "Indian Rupee (India) – ₹");
            COUNTRY_TO_CURRENCY.put("EUR", "Euro (European Union) – €");
            COUNTRY_TO_CURRENCY.put("GBP", "British Pound (United Kingdom) – £");
            COUNTRY_TO_CURRENCY.put("JPY", "Japanese Yen (Japan) – ¥");
            COUNTRY_TO_CURRENCY.put("AED", "United Arab Emirates Dirham (United Arab Emirates) – د.إ");
            COUNTRY_TO_CURRENCY.put("AFN", "Afghan Afghani (Afghanistan) – ؋");
            COUNTRY_TO_CURRENCY.put("AUD", "Australian Dollar (Australia) – $ or A$");
            COUNTRY_TO_CURRENCY.put("BDT", "Bangladeshi Taka (Bangladesh) – ৳");
            COUNTRY_TO_CURRENCY.put("BRL", "Brazilian Real (Brazil) – R$");
            COUNTRY_TO_CURRENCY.put("BTN", "Bhutanese Ngultrum (Bhutan) – Nu. or ₼");
            COUNTRY_TO_CURRENCY.put("CAD", "Canadian Dollar (Canada) – $ or C$");
            COUNTRY_TO_CURRENCY.put("CHF", "Swiss Franc (Switzerland) – ₣ or CHF");
            COUNTRY_TO_CURRENCY.put("CNY", "Chinese Yuan (China) – ¥ or 元");
            COUNTRY_TO_CURRENCY.put("EGP", "Egyptian Pound (Egypt) – £ or ج.م");
            COUNTRY_TO_CURRENCY.put("GEL", "Georgian Lari (Georgia) – ₾");
            COUNTRY_TO_CURRENCY.put("HKD", "Hong Kong Dollar (Hong Kong) – $ or HK$");
            COUNTRY_TO_CURRENCY.put("IDR", "Indonesian Rupiah (Indonesia) – Rp");
            COUNTRY_TO_CURRENCY.put("IRR", "Iranian Rial (Iran) – ﷼");
            COUNTRY_TO_CURRENCY.put("KRW", "South Korean Won (South Korea) – ₩");
            COUNTRY_TO_CURRENCY.put("KWD", "Kuwaiti Dinar (Kuwait) – د.ك");
            COUNTRY_TO_CURRENCY.put("LKR", "Sri Lankan Rupee (Sri Lanka) – Rs or රු");
            COUNTRY_TO_CURRENCY.put("MVR", "Maldivian Rufiyaa (Maldives) – ރ");
            COUNTRY_TO_CURRENCY.put("MYR", "Malaysian Ringgit (Malaysia) – RM");
            COUNTRY_TO_CURRENCY.put("NPR", "Nepalese Rupee (Nepal) – ₨ or रू");
            COUNTRY_TO_CURRENCY.put("NZD", "New Zealand Dollar (New Zealand) – $ or NZ$");
            COUNTRY_TO_CURRENCY.put("OMR", "Omani Rial (Oman) – ر.ع.");
            COUNTRY_TO_CURRENCY.put("PKR", "Pakistani Rupee (Pakistan) – ₨ or Rs");
            COUNTRY_TO_CURRENCY.put("QAR", "Qatari Rial (Qatar) – ر.ق");
            COUNTRY_TO_CURRENCY.put("RUB", "Russian Ruble (Russia) – ₽");
            COUNTRY_TO_CURRENCY.put("SAR", "Saudi Riyal (Saudi Arabia) – ر.س");
            COUNTRY_TO_CURRENCY.put("SGD", "Singapore Dollar (Singapore) – $ or S$");
            COUNTRY_TO_CURRENCY.put("THB", "Thai Baht (Thailand) – ฿");
            COUNTRY_TO_CURRENCY.put("UAH", "Ukrainian Hryvnia (Ukraine) – ₴");
            COUNTRY_TO_CURRENCY.put("VND", "Vietnamese Đồng (Vietnam) – ₫");
            COUNTRY_TO_CURRENCY.put("ZAR", "South African Rand (South Africa) – R");


            // India city currency
            COUNTRY_TO_CURRENCY.put("Gujarat", "INR – Indian Rupee (India) – ₹");
            COUNTRY_TO_CURRENCY.put("Delhi", "INR – Indian Rupee (India) – ₹");
            COUNTRY_TO_CURRENCY.put("Maharashtra", "INR – Indian Rupee (India) – ₹");
            COUNTRY_TO_CURRENCY.put("Karnataka", "INR – Indian Rupee (India) – ₹");
            COUNTRY_TO_CURRENCY.put("Tamil Nadu", "INR – Indian Rupee (India) – ₹");
            COUNTRY_TO_CURRENCY.put("Uttar Pradesh", "INR – Indian Rupee (India) – ₹");
            COUNTRY_TO_CURRENCY.put("Bihar", "INR – Indian Rupee (India) – ₹");
            COUNTRY_TO_CURRENCY.put("Rajasthan", "INR – Indian Rupee (India) – ₹");
            COUNTRY_TO_CURRENCY.put("West Bengal", "INR – Indian Rupee (India) – ₹");
            COUNTRY_TO_CURRENCY.put("Andhra Pradesh", "INR – Indian Rupee (India) – ₹");
            COUNTRY_TO_CURRENCY.put("Kerala", "INR – Indian Rupee (India) – ₹");
            COUNTRY_TO_CURRENCY.put("Punjab", "INR – Indian Rupee (India) – ₹");
            COUNTRY_TO_CURRENCY.put("Haryana", "INR – Indian Rupee (India) – ₹");
            COUNTRY_TO_CURRENCY.put("Madhya Pradesh", "INR – Indian Rupee (India) – ₹");
            COUNTRY_TO_CURRENCY.put("Odisha", "INR – Indian Rupee (India) – ₹");


            // USA states currency
            COUNTRY_TO_CURRENCY.put("California", "USD – United States Dollar (USA) – $");
            COUNTRY_TO_CURRENCY.put("Texas", "USD – United States Dollar (USA) – $");
            COUNTRY_TO_CURRENCY.put("Florida", "USD – United States Dollar (USA) – $");
            COUNTRY_TO_CURRENCY.put("New York", "USD – United States Dollar (USA) – $");
            COUNTRY_TO_CURRENCY.put("Illinois", "USD – United States Dollar (USA) – $");
            COUNTRY_TO_CURRENCY.put("Pennsylvania", "USD – United States Dollar (USA) – $");
            COUNTRY_TO_CURRENCY.put("Ohio", "USD – United States Dollar (USA) – $");
            COUNTRY_TO_CURRENCY.put("Georgia", "USD – United States Dollar (USA) – $");
            COUNTRY_TO_CURRENCY.put("North Carolina", "USD – United States Dollar (USA) – $");
            COUNTRY_TO_CURRENCY.put("Virginia", "USD – United States Dollar (USA) – $");
            COUNTRY_TO_CURRENCY.put("Washington", "USD – United States Dollar (USA) – $");

            // European countries currency
            COUNTRY_TO_CURRENCY.put("Germany", "EUR – Euro (European Union) – €");
            COUNTRY_TO_CURRENCY.put("France", "EUR – Euro (European Union) – €");
            COUNTRY_TO_CURRENCY.put("Italy", "EUR – Euro (European Union) – €");
            COUNTRY_TO_CURRENCY.put("Spain", "EUR – Euro (European Union) – €");
            COUNTRY_TO_CURRENCY.put("Netherlands", "EUR – Euro (European Union) – €");
            COUNTRY_TO_CURRENCY.put("Belgium", "EUR – Euro (European Union) – €");
            COUNTRY_TO_CURRENCY.put("Austria", "EUR – Euro (European Union) – €");
            COUNTRY_TO_CURRENCY.put("Portugal", "EUR – Euro (European Union) – €");
            COUNTRY_TO_CURRENCY.put("Greece", "EUR – Euro (European Union) – €");
            COUNTRY_TO_CURRENCY.put("Ireland", "EUR – Euro (European Union) – €");


// United Kingdom regions currency
            COUNTRY_TO_CURRENCY.put("England", "GBP – British Pound (United Kingdom) – £");
            COUNTRY_TO_CURRENCY.put("Scotland", "GBP – British Pound (United Kingdom) – £");
            COUNTRY_TO_CURRENCY.put("Wales", "GBP – British Pound (United Kingdom) – £");
            COUNTRY_TO_CURRENCY.put("Northern Ireland", "GBP – British Pound (United Kingdom) – £");
            COUNTRY_TO_CURRENCY.put("Jersey", "GBP – British Pound (United Kingdom) – £");
            COUNTRY_TO_CURRENCY.put("Guernsey", "GBP – British Pound (United Kingdom) – £");
            COUNTRY_TO_CURRENCY.put("Isle of Man", "GBP – British Pound (United Kingdom) – £");
            COUNTRY_TO_CURRENCY.put("Gibraltar", "GBP – British Pound (United Kingdom) – £");
            COUNTRY_TO_CURRENCY.put("Saint Helena", "GBP – British Pound (United Kingdom) – £");
            COUNTRY_TO_CURRENCY.put("British Antarctic Territory", "GBP – British Pound (United Kingdom) – £");

// Japan regions currency
            COUNTRY_TO_CURRENCY.put("Tokyo", "JPY – Japanese Yen (Japan) – ¥");
            COUNTRY_TO_CURRENCY.put("Osaka", "JPY – Japanese Yen (Japan) – ¥");
            COUNTRY_TO_CURRENCY.put("Kyoto", "JPY – Japanese Yen (Japan) – ¥");
            COUNTRY_TO_CURRENCY.put("Hokkaido", "JPY – Japanese Yen (Japan) – ¥");
            COUNTRY_TO_CURRENCY.put("Fukuoka", "JPY – Japanese Yen (Japan) – ¥");
            COUNTRY_TO_CURRENCY.put("Aichi", "JPY – Japanese Yen (Japan) – ¥");
            COUNTRY_TO_CURRENCY.put("Hyogo", "JPY – Japanese Yen (Japan) – ¥");
            COUNTRY_TO_CURRENCY.put("Okinawa", "JPY – Japanese Yen (Japan) – ¥");
            COUNTRY_TO_CURRENCY.put("Hiroshima", "JPY – Japanese Yen (Japan) – ¥");
            COUNTRY_TO_CURRENCY.put("Chiba", "JPY – Japanese Yen (Japan) – ¥");


// United Arab Emirates regions currency
            COUNTRY_TO_CURRENCY.put("Dubai", "AED – United Arab Emirates Dirham (United Arab Emirates) – د.إ");
            COUNTRY_TO_CURRENCY.put("Abu Dhabi", "AED – United Arab Emirates Dirham (United Arab Emirates) – د.إ");
            COUNTRY_TO_CURRENCY.put("Sharjah", "AED – United Arab Emirates Dirham (United Arab Emirates) – د.إ");
            COUNTRY_TO_CURRENCY.put("Ras Al Khaimah", "AED – United Arab Emirates Dirham (United Arab Emirates) – د.إ");
            COUNTRY_TO_CURRENCY.put("Ajman", "AED – United Arab Emirates Dirham (United Arab Emirates) – د.إ");
            COUNTRY_TO_CURRENCY.put("Fujairah", "AED – United Arab Emirates Dirham (United Arab Emirates) – د.إ");
            COUNTRY_TO_CURRENCY.put("Umm Al-Quwain", "AED – United Arab Emirates Dirham (United Arab Emirates) – د.إ");
            COUNTRY_TO_CURRENCY.put("Al Ain", "AED – United Arab Emirates Dirham (United Arab Emirates) – د.إ");
            COUNTRY_TO_CURRENCY.put("Khalifa City", "AED – United Arab Emirates Dirham (United Arab Emirates) – د.إ");
            COUNTRY_TO_CURRENCY.put("Jumeirah", "AED – United Arab Emirates Dirham (United Arab Emirates) – د.إ");

// Afghanistan regions currency
            COUNTRY_TO_CURRENCY.put("Kabul", "AFN – Afghan Afghani (Afghanistan) – ؋");
            COUNTRY_TO_CURRENCY.put("Herat", "AFN – Afghan Afghani (Afghanistan) – ؋");
            COUNTRY_TO_CURRENCY.put("Kandahar", "AFN – Afghan Afghani (Afghanistan) – ؋");
            COUNTRY_TO_CURRENCY.put("Balkh", "AFN – Afghan Afghani (Afghanistan) – ؋");
            COUNTRY_TO_CURRENCY.put("Nangarhar", "AFN – Afghan Afghani (Afghanistan) – ؋");
            COUNTRY_TO_CURRENCY.put("Mazar-i-Sharif", "AFN – Afghan Afghani (Afghanistan) – ؋");
            COUNTRY_TO_CURRENCY.put("Logar", "AFN – Afghan Afghani (Afghanistan) – ؋");
            COUNTRY_TO_CURRENCY.put("Baghlan", "AFN – Afghan Afghani (Afghanistan) – ؋");
            COUNTRY_TO_CURRENCY.put("Ghazni", "AFN – Afghan Afghani (Afghanistan) – ؋");
            COUNTRY_TO_CURRENCY.put("Paktia", "AFN – Afghan Afghani (Afghanistan) – ؋");

// Australia regions currency
            COUNTRY_TO_CURRENCY.put("New South Wales", "AUD – Australian Dollar (Australia) – $");
            COUNTRY_TO_CURRENCY.put("Victoria", "AUD – Australian Dollar (Australia) – $");
            COUNTRY_TO_CURRENCY.put("Queensland", "AUD – Australian Dollar (Australia) – $");
            COUNTRY_TO_CURRENCY.put("Western Australia", "AUD – Australian Dollar (Australia) – $");
            COUNTRY_TO_CURRENCY.put("South Australia", "AUD – Australian Dollar (Australia) – $");
            COUNTRY_TO_CURRENCY.put("Tasmania", "AUD – Australian Dollar (Australia) – $");
            COUNTRY_TO_CURRENCY.put("Australian Capital Territory", "AUD – Australian Dollar (Australia) – $");
            COUNTRY_TO_CURRENCY.put("Northern Territory", "AUD – Australian Dollar (Australia) – $");
            COUNTRY_TO_CURRENCY.put("Newcastle (New South Wales)", "AUD – Australian Dollar (Australia) – $");
            COUNTRY_TO_CURRENCY.put("Gold Coast (Queensland)", "AUD – Australian Dollar (Australia) – $");

// Bangladesh regions currency
            COUNTRY_TO_CURRENCY.put("Dhaka", "BDT – Bangladeshi Taka (Bangladesh) – ৳");
            COUNTRY_TO_CURRENCY.put("Chittagong (Chattogram)", "BDT – Bangladeshi Taka (Bangladesh) – ৳");
            COUNTRY_TO_CURRENCY.put("Rajshahi", "BDT – Bangladeshi Taka (Bangladesh) – ৳");
            COUNTRY_TO_CURRENCY.put("Khulna", "BDT – Bangladeshi Taka (Bangladesh) – ৳");
            COUNTRY_TO_CURRENCY.put("Barisal (Barishal)", "BDT – Bangladeshi Taka (Bangladesh) – ৳");
            COUNTRY_TO_CURRENCY.put("Sylhet", "BDT – Bangladeshi Taka (Bangladesh) – ৳");
            COUNTRY_TO_CURRENCY.put("Rangpur", "BDT – Bangladeshi Taka (Bangladesh) – ৳");
            COUNTRY_TO_CURRENCY.put("Mymensingh", "BDT – Bangladeshi Taka (Bangladesh) – ৳");
            COUNTRY_TO_CURRENCY.put("Tangail", "BDT – Bangladeshi Taka (Bangladesh) – ৳");
            COUNTRY_TO_CURRENCY.put("Jessore", "BDT – Bangladeshi Taka (Bangladesh) – ৳");

// Brazil states currency
            COUNTRY_TO_CURRENCY.put("São Paulo", "BRL – Brazilian Real (Brazil) – R$");
            COUNTRY_TO_CURRENCY.put("Rio de Janeiro", "BRL – Brazilian Real (Brazil) – R$");
            COUNTRY_TO_CURRENCY.put("Minas Gerais", "BRL – Brazilian Real (Brazil) – R$");
            COUNTRY_TO_CURRENCY.put("Bahia", "BRL – Brazilian Real (Brazil) – R$");
            COUNTRY_TO_CURRENCY.put("Paraná", "BRL – Brazilian Real (Brazil) – R$");
            COUNTRY_TO_CURRENCY.put("Rio Grande do Sul", "BRL – Brazilian Real (Brazil) – R$");
            COUNTRY_TO_CURRENCY.put("Pernambuco", "BRL – Brazilian Real (Brazil) – R$");
            COUNTRY_TO_CURRENCY.put("Ceará", "BRL – Brazilian Real (Brazil) – R$");
            COUNTRY_TO_CURRENCY.put("Espírito Santo", "BRL – Brazilian Real (Brazil) – R$");
            COUNTRY_TO_CURRENCY.put("Santa Catarina", "BRL – Brazilian Real (Brazil) – R$");

// Bhutan regions currency
            COUNTRY_TO_CURRENCY.put("Thimphu", "BTN – Bhutanese Ngultrum (Bhutan) – Nu.");
            COUNTRY_TO_CURRENCY.put("Paro", "BTN – Bhutanese Ngultrum (Bhutan) – Nu.");
            COUNTRY_TO_CURRENCY.put("Chukha", "BTN – Bhutanese Ngultrum (Bhutan) – Nu.");
            COUNTRY_TO_CURRENCY.put("Bumthang", "BTN – Bhutanese Ngultrum (Bhutan) – Nu.");
            COUNTRY_TO_CURRENCY.put("Punakha", "BTN – Bhutanese Ngultrum (Bhutan) – Nu.");
            COUNTRY_TO_CURRENCY.put("Wangdue Phodrang", "BTN – Bhutanese Ngultrum (Bhutan) – Nu.");
            COUNTRY_TO_CURRENCY.put("Trashigang", "BTN – Bhutanese Ngultrum (Bhutan) – Nu.");
            COUNTRY_TO_CURRENCY.put("Samdrup Jongkhar", "BTN – Bhutanese Ngultrum (Bhutan) – Nu.");
            COUNTRY_TO_CURRENCY.put("Haa", "BTN – Bhutanese Ngultrum (Bhutan) – Nu.");
            COUNTRY_TO_CURRENCY.put("Gasa", "BTN – Bhutanese Ngultrum (Bhutan) – Nu.");

// Canada provinces currency
            COUNTRY_TO_CURRENCY.put("Ontario", "CAD – Canadian Dollar (Canada) – $");
            COUNTRY_TO_CURRENCY.put("Quebec", "CAD – Canadian Dollar (Canada) – $");
            COUNTRY_TO_CURRENCY.put("British Columbia", "CAD – Canadian Dollar (Canada) – $");
            COUNTRY_TO_CURRENCY.put("Alberta", "CAD – Canadian Dollar (Canada) – $");
            COUNTRY_TO_CURRENCY.put("Manitoba", "CAD – Canadian Dollar (Canada) – $");
            COUNTRY_TO_CURRENCY.put("Nova Scotia", "CAD – Canadian Dollar (Canada) – $");
            COUNTRY_TO_CURRENCY.put("New Brunswick", "CAD – Canadian Dollar (Canada) – $");
            COUNTRY_TO_CURRENCY.put("Saskatchewan", "CAD – Canadian Dollar (Canada) – $");
            COUNTRY_TO_CURRENCY.put("Prince Edward Island", "CAD – Canadian Dollar (Canada) – $");
            COUNTRY_TO_CURRENCY.put("Newfoundland and Labrador", "CAD – Canadian Dollar (Canada) – $");

// Switzerland regions currency
            COUNTRY_TO_CURRENCY.put("Zurich", "CHF – Swiss Franc (Switzerland) – ₣");
            COUNTRY_TO_CURRENCY.put("Geneva", "CHF – Swiss Franc (Switzerland) – ₣");
            COUNTRY_TO_CURRENCY.put("Bern", "CHF – Swiss Franc (Switzerland) – ₣");
            COUNTRY_TO_CURRENCY.put("Basel-Stadt", "CHF – Swiss Franc (Switzerland) – ₣");
            COUNTRY_TO_CURRENCY.put("Vaud", "CHF – Swiss Franc (Switzerland) – ₣");
            COUNTRY_TO_CURRENCY.put("Lucerne", "CHF – Swiss Franc (Switzerland) – ₣");
            COUNTRY_TO_CURRENCY.put("Aargau", "CHF – Swiss Franc (Switzerland) – ₣");
            COUNTRY_TO_CURRENCY.put("St. Gallen", "CHF – Swiss Franc (Switzerland) – ₣");
            COUNTRY_TO_CURRENCY.put("Ticino", "CHF – Swiss Franc (Switzerland) – ₣");
            COUNTRY_TO_CURRENCY.put("Thurgau", "CHF – Swiss Franc (Switzerland) – ₣");

// China regions currency
            COUNTRY_TO_CURRENCY.put("Beijing", "CNY – Chinese Yuan (China) – ¥");
            COUNTRY_TO_CURRENCY.put("Shanghai", "CNY – Chinese Yuan (China) – ¥");
            COUNTRY_TO_CURRENCY.put("Guangdong", "CNY – Chinese Yuan (China) – ¥");
            COUNTRY_TO_CURRENCY.put("Zhejiang", "CNY – Chinese Yuan (China) – ¥");
            COUNTRY_TO_CURRENCY.put("Jiangsu", "CNY – Chinese Yuan (China) – ¥");
            COUNTRY_TO_CURRENCY.put("Shandong", "CNY – Chinese Yuan (China) – ¥");
            COUNTRY_TO_CURRENCY.put("Sichuan", "CNY – Chinese Yuan (China) – ¥");
            COUNTRY_TO_CURRENCY.put("Henan", "CNY – Chinese Yuan (China) – ¥");
            COUNTRY_TO_CURRENCY.put("Hunan", "CNY – Chinese Yuan (China) – ¥");
            COUNTRY_TO_CURRENCY.put("Chongqing", "CNY – Chinese Yuan (China) – ¥");


// Egypt regions currency
            COUNTRY_TO_CURRENCY.put("Cairo", "EGP – Egyptian Pound (Egypt) – £");
            COUNTRY_TO_CURRENCY.put("Alexandria", "EGP – Egyptian Pound (Egypt) – £");
            COUNTRY_TO_CURRENCY.put("Giza", "EGP – Egyptian Pound (Egypt) – £");
            COUNTRY_TO_CURRENCY.put("Sharqia", "EGP – Egyptian Pound (Egypt) – £");
            COUNTRY_TO_CURRENCY.put("Dakahlia", "EGP – Egyptian Pound (Egypt) – £");
            COUNTRY_TO_CURRENCY.put("Qalyubia", "EGP – Egyptian Pound (Egypt) – £");
            COUNTRY_TO_CURRENCY.put("Luxor", "EGP – Egyptian Pound (Egypt) – £");
            COUNTRY_TO_CURRENCY.put("Suez", "EGP – Egyptian Pound (Egypt) – £");
            COUNTRY_TO_CURRENCY.put("Aswan", "EGP – Egyptian Pound (Egypt) – £");
            COUNTRY_TO_CURRENCY.put("Port Said", "EGP – Egyptian Pound (Egypt) – £");

// Georgia regions currency
            COUNTRY_TO_CURRENCY.put("Tbilisi", "GEL – Georgian Lari (Georgia) – ₾");
            COUNTRY_TO_CURRENCY.put("Batumi", "GEL – Georgian Lari (Georgia) – ₾");
            COUNTRY_TO_CURRENCY.put("Kutaisi", "GEL – Georgian Lari (Georgia) – ₾");
            COUNTRY_TO_CURRENCY.put("Zugdidi", "GEL – Georgian Lari (Georgia) – ₾");
            COUNTRY_TO_CURRENCY.put("Rustavi", "GEL – Georgian Lari (Georgia) – ₾");
            COUNTRY_TO_CURRENCY.put("Telavi", "GEL – Georgian Lari (Georgia) – ₾");
            COUNTRY_TO_CURRENCY.put("Gori", "GEL – Georgian Lari (Georgia) – ₾");
            COUNTRY_TO_CURRENCY.put("Mtskheta", "GEL – Georgian Lari (Georgia) – ₾");
            COUNTRY_TO_CURRENCY.put("Senaki", "GEL – Georgian Lari (Georgia) – ₾");
            COUNTRY_TO_CURRENCY.put("Khashuri", "GEL – Georgian Lari (Georgia) – ₾");

// Hong Kong regions currency
            COUNTRY_TO_CURRENCY.put("Hong Kong Island", "HKD – Hong Kong Dollar (Hong Kong) – $");
            COUNTRY_TO_CURRENCY.put("Kowloon", "HKD – Hong Kong Dollar (Hong Kong) – $");
            COUNTRY_TO_CURRENCY.put("New Territories", "HKD – Hong Kong Dollar (Hong Kong) – $");
            COUNTRY_TO_CURRENCY.put("Central and Western District", "HKD – Hong Kong Dollar (Hong Kong) – $");
            COUNTRY_TO_CURRENCY.put("Wan Chai", "HKD – Hong Kong Dollar (Hong Kong) – $");
            COUNTRY_TO_CURRENCY.put("Yau Tsim Mong", "HKD – Hong Kong Dollar (Hong Kong) – $");
            COUNTRY_TO_CURRENCY.put("Kowloon City", "HKD – Hong Kong Dollar (Hong Kong) – $");
            COUNTRY_TO_CURRENCY.put("Sha Tin", "HKD – Hong Kong Dollar (Hong Kong) – $");
            COUNTRY_TO_CURRENCY.put("Tsuen Wan", "HKD – Hong Kong Dollar (Hong Kong) – $");
            COUNTRY_TO_CURRENCY.put("Tuen Mun", "HKD – Hong Kong Dollar (Hong Kong) – $");

// Indonesia regions currency
            COUNTRY_TO_CURRENCY.put("Java", "IDR – Indonesian Rupiah (Indonesia) – Rp");
            COUNTRY_TO_CURRENCY.put("Bali", "IDR – Indonesian Rupiah (Indonesia) – Rp");
            COUNTRY_TO_CURRENCY.put("Sumatra", "IDR – Indonesian Rupiah (Indonesia) – Rp");
            COUNTRY_TO_CURRENCY.put("Kalimantan", "IDR – Indonesian Rupiah (Indonesia) – Rp");
            COUNTRY_TO_CURRENCY.put("Sulawesi", "IDR – Indonesian Rupiah (Indonesia) – Rp");
            COUNTRY_TO_CURRENCY.put("Papua", "IDR – Indonesian Rupiah (Indonesia) – Rp");
            COUNTRY_TO_CURRENCY.put("West Java", "IDR – Indonesian Rupiah (Indonesia) – Rp");
            COUNTRY_TO_CURRENCY.put("East Java", "IDR – Indonesian Rupiah (Indonesia) – Rp");
            COUNTRY_TO_CURRENCY.put("North Sumatra", "IDR – Indonesian Rupiah (Indonesia) – Rp");
            COUNTRY_TO_CURRENCY.put("Lampung", "IDR – Indonesian Rupiah (Indonesia) – Rp");

// Iran regions currency
            COUNTRY_TO_CURRENCY.put("Tehran", "IRR – Iranian Rial (Iran) – ﷼");
            COUNTRY_TO_CURRENCY.put("Isfahan", "IRR – Iranian Rial (Iran) – ﷼");
            COUNTRY_TO_CURRENCY.put("Khorasan Razavi", "IRR – Iranian Rial (Iran) – ﷼");
            COUNTRY_TO_CURRENCY.put("Fars", "IRR – Iranian Rial (Iran) – ﷼");
            COUNTRY_TO_CURRENCY.put("Khuzestan", "IRR – Iranian Rial (Iran) – ﷼");
            COUNTRY_TO_CURRENCY.put("Mazandaran", "IRR – Iranian Rial (Iran) – ﷼");
            COUNTRY_TO_CURRENCY.put("Golestan", "IRR – Iranian Rial (Iran) – ﷼");
            COUNTRY_TO_CURRENCY.put("East Azerbaijan", "IRR – Iranian Rial (Iran) – ﷼");
            COUNTRY_TO_CURRENCY.put("Semnan", "IRR – Iranian Rial (Iran) – ﷼");
            COUNTRY_TO_CURRENCY.put("West Azerbaijan", "IRR – Iranian Rial (Iran) – ﷼");

// South Korea regions currency
            COUNTRY_TO_CURRENCY.put("Seoul", "KRW – South Korean Won (South Korea) – ₩");
            COUNTRY_TO_CURRENCY.put("Busan", "KRW – South Korean Won (South Korea) – ₩");
            COUNTRY_TO_CURRENCY.put("Incheon", "KRW – South Korean Won (South Korea) – ₩");
            COUNTRY_TO_CURRENCY.put("Gyeonggi", "KRW – South Korean Won (South Korea) – ₩");
            COUNTRY_TO_CURRENCY.put("Gyeongsangnam-do", "KRW – South Korean Won (South Korea) – ₩");
            COUNTRY_TO_CURRENCY.put("Jeollanam-do", "KRW – South Korean Won (South Korea) – ₩");
            COUNTRY_TO_CURRENCY.put("Daegu", "KRW – South Korean Won (South Korea) – ₩");
            COUNTRY_TO_CURRENCY.put("Daejeon", "KRW – South Korean Won (South Korea) – ₩");
            COUNTRY_TO_CURRENCY.put("Jeju", "KRW – South Korean Won (South Korea) – ₩");
            COUNTRY_TO_CURRENCY.put("Gangwon-do", "KRW – South Korean Won (South Korea) – ₩");

// Kuwait regions currency
            COUNTRY_TO_CURRENCY.put("Capital Governorate (Kuwait City)", "KWD – Kuwaiti Dinar (Kuwait) – د.ك");
            COUNTRY_TO_CURRENCY.put("Hawalli", "KWD – Kuwaiti Dinar (Kuwait) – د.ك");
            COUNTRY_TO_CURRENCY.put("Mubarak Al-Kabeer", "KWD – Kuwaiti Dinar (Kuwait) – د.ك");
            COUNTRY_TO_CURRENCY.put("Al Jahra", "KWD – Kuwaiti Dinar (Kuwait) – د.ك");
            COUNTRY_TO_CURRENCY.put("Farwaniya", "KWD – Kuwaiti Dinar (Kuwait) – د.ك");
            COUNTRY_TO_CURRENCY.put("Ahmadi", "KWD – Kuwaiti Dinar (Kuwait) – د.ك");
            COUNTRY_TO_CURRENCY.put("Al Asimah", "KWD – Kuwaiti Dinar (Kuwait) – د.ك");
            COUNTRY_TO_CURRENCY.put("Al-Farwaniyah", "KWD – Kuwaiti Dinar (Kuwait) – د.ك");
            COUNTRY_TO_CURRENCY.put("Al-Ahmadi", "KWD – Kuwaiti Dinar (Kuwait) – د.ك");
            COUNTRY_TO_CURRENCY.put("Al-Jahra", "KWD – Kuwaiti Dinar (Kuwait) – د.ك");

// Sri Lanka provinces currency
            COUNTRY_TO_CURRENCY.put("Western Province", "LKR – Sri Lankan Rupee (Sri Lanka) – Rs");
            COUNTRY_TO_CURRENCY.put("Central Province", "LKR – Sri Lankan Rupee (Sri Lanka) – Rs");
            COUNTRY_TO_CURRENCY.put("Southern Province", "LKR – Sri Lankan Rupee (Sri Lanka) – Rs");
            COUNTRY_TO_CURRENCY.put("Eastern Province", "LKR – Sri Lankan Rupee (Sri Lanka) – Rs");
            COUNTRY_TO_CURRENCY.put("Northern Province", "LKR – Sri Lankan Rupee (Sri Lanka) – Rs");
            COUNTRY_TO_CURRENCY.put("North Western Province", "LKR – Sri Lankan Rupee (Sri Lanka) – Rs");
            COUNTRY_TO_CURRENCY.put("North Central Province", "LKR – Sri Lankan Rupee (Sri Lanka) – Rs");
            COUNTRY_TO_CURRENCY.put("Uva Province", "LKR – Sri Lankan Rupee (Sri Lanka) – Rs");
            COUNTRY_TO_CURRENCY.put("Sabaragamuwa Province", "LKR – Sri Lankan Rupee (Sri Lanka) – Rs");
            COUNTRY_TO_CURRENCY.put("Western Province", "LKR – Sri Lankan Rupee (Sri Lanka) – Rs");

// Maldives regions currency
            COUNTRY_TO_CURRENCY.put("Malé", "MVR – Maldivian Rufiyaa (Maldives) – ރ");
            COUNTRY_TO_CURRENCY.put("Addu City", "MVR – Maldivian Rufiyaa (Maldives) – ރ");
            COUNTRY_TO_CURRENCY.put("Maafannu", "MVR – Maldivian Rufiyaa (Maldives) – ރ");
            COUNTRY_TO_CURRENCY.put("Hinnavaru", "MVR – Maldivian Rufiyaa (Maldives) – ރ");
            COUNTRY_TO_CURRENCY.put("Fuvahmulah", "MVR – Maldivian Rufiyaa (Maldives) – ރ");
            COUNTRY_TO_CURRENCY.put("Dhiggaru", "MVR – Maldivian Rufiyaa (Maldives) – ރ");
            COUNTRY_TO_CURRENCY.put("Thulusdhoo", "MVR – Maldivian Rufiyaa (Maldives) – ރ");
            COUNTRY_TO_CURRENCY.put("Laamu Atoll", "MVR – Maldivian Rufiyaa (Maldives) – ރ");
            COUNTRY_TO_CURRENCY.put("Baa Atoll", "MVR – Maldivian Rufiyaa (Maldives) – ރ");
            COUNTRY_TO_CURRENCY.put("Noonu Atoll", "MVR – Maldivian Rufiyaa (Maldives) – ރ");

// Malaysia regions currency
            COUNTRY_TO_CURRENCY.put("Kuala Lumpur", "MYR – Malaysian Ringgit (Malaysia) – RM");
            COUNTRY_TO_CURRENCY.put("Selangor", "MYR – Malaysian Ringgit (Malaysia) – RM");
            COUNTRY_TO_CURRENCY.put("Penang", "MYR – Malaysian Ringgit (Malaysia) – RM");
            COUNTRY_TO_CURRENCY.put("Johor", "MYR – Malaysian Ringgit (Malaysia) – RM");
            COUNTRY_TO_CURRENCY.put("Sabah", "MYR – Malaysian Ringgit (Malaysia) – RM");
            COUNTRY_TO_CURRENCY.put("Sarawak", "MYR – Malaysian Ringgit (Malaysia) – RM");
            COUNTRY_TO_CURRENCY.put("Perak", "MYR – Malaysian Ringgit (Malaysia) – RM");
            COUNTRY_TO_CURRENCY.put("Melaka", "MYR – Malaysian Ringgit (Malaysia) – RM");
            COUNTRY_TO_CURRENCY.put("Kedah", "MYR – Malaysian Ringgit (Malaysia) – RM");
            COUNTRY_TO_CURRENCY.put("Negeri Sembilan", "MYR – Malaysian Ringgit (Malaysia) – RM");

// Nepal regions currency
            COUNTRY_TO_CURRENCY.put("Bagmati Province", "NPR – Nepalese Rupee (Nepal) – ₨");
            COUNTRY_TO_CURRENCY.put("Lumbini Province", "NPR – Nepalese Rupee (Nepal) – ₨");
            COUNTRY_TO_CURRENCY.put("Karnali Province", "NPR – Nepalese Rupee (Nepal) – ₨");
            COUNTRY_TO_CURRENCY.put("Gandaki Province", "NPR – Nepalese Rupee (Nepal) – ₨");
            COUNTRY_TO_CURRENCY.put("Province No. 1", "NPR – Nepalese Rupee (Nepal) – ₨");
            COUNTRY_TO_CURRENCY.put("Sudurpashchim Province", "NPR – Nepalese Rupee (Nepal) – ₨");
            COUNTRY_TO_CURRENCY.put("Makwanpur", "NPR – Nepalese Rupee (Nepal) – ₨");
            COUNTRY_TO_CURRENCY.put("Chitwan", "NPR – Nepalese Rupee (Nepal) – ₨");
            COUNTRY_TO_CURRENCY.put("Kathmandu Valley", "NPR – Nepalese Rupee (Nepal) – ₨");
            COUNTRY_TO_CURRENCY.put("Pokhara", "NPR – Nepalese Rupee (Nepal) – ₨");

// New Zealand regions currency
            COUNTRY_TO_CURRENCY.put("Auckland", "NZD – New Zealand Dollar (New Zealand) – $");
            COUNTRY_TO_CURRENCY.put("Wellington", "NZD – New Zealand Dollar (New Zealand) – $");
            COUNTRY_TO_CURRENCY.put("Canterbury", "NZD – New Zealand Dollar (New Zealand) – $");
            COUNTRY_TO_CURRENCY.put("Otago", "NZD – New Zealand Dollar (New Zealand) – $");
            COUNTRY_TO_CURRENCY.put("Waikato", "NZD – New Zealand Dollar (New Zealand) – $");
            COUNTRY_TO_CURRENCY.put("Bay of Plenty", "NZD – New Zealand Dollar (New Zealand) – $");
            COUNTRY_TO_CURRENCY.put("Manawatu-Wanganui", "NZD – New Zealand Dollar (New Zealand) – $");
            COUNTRY_TO_CURRENCY.put("Hawke's Bay", "NZD – New Zealand Dollar (New Zealand) – $");
            COUNTRY_TO_CURRENCY.put("Southland", "NZD – New Zealand Dollar (New Zealand) – $");
            COUNTRY_TO_CURRENCY.put("Northland", "NZD – New Zealand Dollar (New Zealand) – $");

// Oman regions currency
            COUNTRY_TO_CURRENCY.put("Muscat", "OMR – Omani Rial (Oman) – ر.ع.");
            COUNTRY_TO_CURRENCY.put("Dhofar", "OMR – Omani Rial (Oman) – ر.ع.");
            COUNTRY_TO_CURRENCY.put("Al Batinah North", "OMR – Omani Rial (Oman) – ر.ع.");
            COUNTRY_TO_CURRENCY.put("Al Batinah South", "OMR – Omani Rial (Oman) – ر.ع.");
            COUNTRY_TO_CURRENCY.put("Al Dakhiliyah", "OMR – Omani Rial (Oman) – ر.ع.");
            COUNTRY_TO_CURRENCY.put("Ash Sharqiyah North", "OMR – Omani Rial (Oman) – ر.ع.");
            COUNTRY_TO_CURRENCY.put("Ash Sharqiyah South", "OMR – Omani Rial (Oman) – ر.ع.");
            COUNTRY_TO_CURRENCY.put("Al Hajar Mountains", "OMR – Omani Rial (Oman) – ر.ع.");
            COUNTRY_TO_CURRENCY.put("Al Wusta", "OMR – Omani Rial (Oman) – ر.ع.");
            COUNTRY_TO_CURRENCY.put("Musandam", "OMR – Omani Rial (Oman) – ر.ع.");

// Pakistan regions currency
            COUNTRY_TO_CURRENCY.put("Punjab", "PKR – Pakistani Rupee (Pakistan) – ₨");
            COUNTRY_TO_CURRENCY.put("Sindh", "PKR – Pakistani Rupee (Pakistan) – ₨");
            COUNTRY_TO_CURRENCY.put("Khyber Pakhtunkhwa", "PKR – Pakistani Rupee (Pakistan) – ₨");
            COUNTRY_TO_CURRENCY.put("Balochistan", "PKR – Pakistani Rupee (Pakistan) – ₨");
            COUNTRY_TO_CURRENCY.put("Islamabad Capital Territory", "PKR – Pakistani Rupee (Pakistan) – ₨");
            COUNTRY_TO_CURRENCY.put("Azad Jammu & Kashmir", "PKR – Pakistani Rupee (Pakistan) – ₨");
            COUNTRY_TO_CURRENCY.put("Gilgit-Baltistan", "PKR – Pakistani Rupee (Pakistan) – ₨");
            COUNTRY_TO_CURRENCY.put("FATA (Federally Administered Tribal Areas)", "PKR – Pakistani Rupee (Pakistan) – ₨");
            COUNTRY_TO_CURRENCY.put("Karachi (Sindh)", "PKR – Pakistani Rupee (Pakistan) – ₨");
            COUNTRY_TO_CURRENCY.put("Lahore (Punjab)", "PKR – Pakistani Rupee (Pakistan) – ₨");


// Qatar regions currency
            COUNTRY_TO_CURRENCY.put("Doha", "QAR – Qatari Rial (Qatar) – ر.ق");
            COUNTRY_TO_CURRENCY.put("Al Rayyan", "QAR – Qatari Rial (Qatar) – ر.ق");
            COUNTRY_TO_CURRENCY.put("Al Wakrah", "QAR – Qatari Rial (Qatar) – ر.ق");
            COUNTRY_TO_CURRENCY.put("Al Khor", "QAR – Qatari Rial (Qatar) – ر.ق");
            COUNTRY_TO_CURRENCY.put("Al Daayen", "QAR – Qatari Rial (Qatar) – ر.ق");
            COUNTRY_TO_CURRENCY.put("Umm Salal", "QAR – Qatari Rial (Qatar) – ر.ق");
            COUNTRY_TO_CURRENCY.put("Al Shamal", "QAR – Qatari Rial (Qatar) – ر.ق");
            COUNTRY_TO_CURRENCY.put("Al Sheehaniya", "QAR – Qatari Rial (Qatar) – ر.ق");
            COUNTRY_TO_CURRENCY.put("Al Zubarah", "QAR – Qatari Rial (Qatar) – ر.ق");
            COUNTRY_TO_CURRENCY.put("Mesaieed", "QAR – Qatari Rial (Qatar) – ر.ق");

// Russia regions currency
            COUNTRY_TO_CURRENCY.put("Moscow", "RUB – Russian Ruble (Russia) – ₽");
            COUNTRY_TO_CURRENCY.put("Saint Petersburg", "RUB – Russian Ruble (Russia) – ₽");
            COUNTRY_TO_CURRENCY.put("Krasnoyarsk Krai", "RUB – Russian Ruble (Russia) – ₽");
            COUNTRY_TO_CURRENCY.put("Tatarstan", "RUB – Russian Ruble (Russia) – ₽");
            COUNTRY_TO_CURRENCY.put("Sverdlovsk Oblast", "RUB – Russian Ruble (Russia) – ₽");
            COUNTRY_TO_CURRENCY.put("Chelyabinsk Oblast", "RUB – Russian Ruble (Russia) – ₽");
            COUNTRY_TO_CURRENCY.put("Novosibirsk Oblast", "RUB – Russian Ruble (Russia) – ₽");
            COUNTRY_TO_CURRENCY.put("Samara Oblast", "RUB – Russian Ruble (Russia) – ₽");
            COUNTRY_TO_CURRENCY.put("Moscow Oblast", "RUB – Russian Ruble (Russia) – ₽");
            COUNTRY_TO_CURRENCY.put("Krasnodar Krai", "RUB – Russian Ruble (Russia) – ₽");

// Saudi Arabia regions currency
            COUNTRY_TO_CURRENCY.put("Riyadh", "SAR – Saudi Riyal (Saudi Arabia) – ر.س");
            COUNTRY_TO_CURRENCY.put("Makkah", "SAR – Saudi Riyal (Saudi Arabia) – ر.س");
            COUNTRY_TO_CURRENCY.put("Madinah", "SAR – Saudi Riyal (Saudi Arabia) – ر.س");
            COUNTRY_TO_CURRENCY.put("Eastern Province (Ash Sharqiyah)", "SAR – Saudi Riyal (Saudi Arabia) – ر.س");
            COUNTRY_TO_CURRENCY.put("Qassim", "SAR – Saudi Riyal (Saudi Arabia) – ر.س");
            COUNTRY_TO_CURRENCY.put("Asir", "SAR – Saudi Riyal (Saudi Arabia) – ر.س");
            COUNTRY_TO_CURRENCY.put("Tabuk", "SAR – Saudi Riyal (Saudi Arabia) – ر.س");
            COUNTRY_TO_CURRENCY.put("Jizan", "SAR – Saudi Riyal (Saudi Arabia) – ر.س");
            COUNTRY_TO_CURRENCY.put("Hail", "SAR – Saudi Riyal (Saudi Arabia) – ر.س");
            COUNTRY_TO_CURRENCY.put("Najran", "SAR – Saudi Riyal (Saudi Arabia) – ر.س");


// Singapore regions currency
            COUNTRY_TO_CURRENCY.put("Central Area", "SGD – Singapore Dollar (Singapore) – $");
            COUNTRY_TO_CURRENCY.put("Downtown Core", "SGD – Singapore Dollar (Singapore) – $");
            COUNTRY_TO_CURRENCY.put("Orchard", "SGD – Singapore Dollar (Singapore) – $");
            COUNTRY_TO_CURRENCY.put("Bukit Timah", "SGD – Singapore Dollar (Singapore) – $");
            COUNTRY_TO_CURRENCY.put("Sentosa", "SGD – Singapore Dollar (Singapore) – $");
            COUNTRY_TO_CURRENCY.put("Marina Bay", "SGD – Singapore Dollar (Singapore) – $");
            COUNTRY_TO_CURRENCY.put("Changi", "SGD – Singapore Dollar (Singapore) – $");
            COUNTRY_TO_CURRENCY.put("Clementi", "SGD – Singapore Dollar (Singapore) – $");
            COUNTRY_TO_CURRENCY.put("Jurong", "SGD – Singapore Dollar (Singapore) – $");
            COUNTRY_TO_CURRENCY.put("Tampines", "SGD – Singapore Dollar (Singapore) – $");

// Thailand regions currency
            COUNTRY_TO_CURRENCY.put("Bangkok", "THB – Thai Baht (Thailand) – ฿");
            COUNTRY_TO_CURRENCY.put("Chiang Mai", "THB – Thai Baht (Thailand) – ฿");
            COUNTRY_TO_CURRENCY.put("Phuket", "THB – Thai Baht (Thailand) – ฿");
            COUNTRY_TO_CURRENCY.put("Chonburi", "THB – Thai Baht (Thailand) – ฿");
            COUNTRY_TO_CURRENCY.put("Krabi", "THB – Thai Baht (Thailand) – ฿");
            COUNTRY_TO_CURRENCY.put("Nakhon Ratchasima", "THB – Thai Baht (Thailand) – ฿");
            COUNTRY_TO_CURRENCY.put("Ayutthaya", "THB – Thai Baht (Thailand) – ฿");
            COUNTRY_TO_CURRENCY.put("Surat Thani", "THB – Thai Baht (Thailand) – ฿");
            COUNTRY_TO_CURRENCY.put("Pattani", "THB – Thai Baht (Thailand) – ฿");
            COUNTRY_TO_CURRENCY.put("Udon Thani", "THB – Thai Baht (Thailand) – ฿");

// Ukraine regions currency
            COUNTRY_TO_CURRENCY.put("Kyiv", "UAH – Ukrainian Hryvnia (Ukraine) – ₴");
            COUNTRY_TO_CURRENCY.put("Lviv", "UAH – Ukrainian Hryvnia (Ukraine) – ₴");
            COUNTRY_TO_CURRENCY.put("Odessa", "UAH – Ukrainian Hryvnia (Ukraine) – ₴");
            COUNTRY_TO_CURRENCY.put("Kharkiv", "UAH – Ukrainian Hryvnia (Ukraine) – ₴");
            COUNTRY_TO_CURRENCY.put("Dnipro", "UAH – Ukrainian Hryvnia (Ukraine) – ₴");
            COUNTRY_TO_CURRENCY.put("Donetsk", "UAH – Ukrainian Hryvnia (Ukraine) – ₴");
            COUNTRY_TO_CURRENCY.put("Zaporizhzhia", "UAH – Ukrainian Hryvnia (Ukraine) – ₴");
            COUNTRY_TO_CURRENCY.put("Kherson", "UAH – Ukrainian Hryvnia (Ukraine) – ₴");
            COUNTRY_TO_CURRENCY.put("Vinnytsia", "UAH – Ukrainian Hryvnia (Ukraine) – ₴");
            COUNTRY_TO_CURRENCY.put("Poltava", "UAH – Ukrainian Hryvnia (Ukraine) – ₴");

// Vietnam regions currency
            COUNTRY_TO_CURRENCY.put("Hanoi", "VND – Vietnamese Đồng (Vietnam) – ₫");
            COUNTRY_TO_CURRENCY.put("Ho Chi Minh City", "VND – Vietnamese Đồng (Vietnam) – ₫");
            COUNTRY_TO_CURRENCY.put("Da Nang", "VND – Vietnamese Đồng (Vietnam) – ₫");
            COUNTRY_TO_CURRENCY.put("Hải Phòng", "VND – Vietnamese Đồng (Vietnam) – ₫");
            COUNTRY_TO_CURRENCY.put("Can Tho", "VND – Vietnamese Đồng (Vietnam) – ₫");
            COUNTRY_TO_CURRENCY.put("Nghe An", "VND – Vietnamese Đồng (Vietnam) – ₫");
            COUNTRY_TO_CURRENCY.put("Quang Ninh", "VND – Vietnamese Đồng (Vietnam) – ₫");
            COUNTRY_TO_CURRENCY.put("Binh Duong", "VND – Vietnamese Đồng (Vietnam) – ₫");
            COUNTRY_TO_CURRENCY.put("Dong Nai", "VND – Vietnamese Đồng (Vietnam) – ₫");
            COUNTRY_TO_CURRENCY.put("Thua Thien-Hue", "VND – Vietnamese Đồng (Vietnam) – ₫");

// South Africa regions currency
            COUNTRY_TO_CURRENCY.put("Gauteng", "ZAR – South African Rand (South Africa) – R");
            COUNTRY_TO_CURRENCY.put("Western Cape", "ZAR – South African Rand (South Africa) – R");
            COUNTRY_TO_CURRENCY.put("KwaZulu-Natal", "ZAR – South African Rand (South Africa) – R");
            COUNTRY_TO_CURRENCY.put("Eastern Cape", "ZAR – South African Rand (South Africa) – R");
            COUNTRY_TO_CURRENCY.put("Limpopo", "ZAR – South African Rand (South Africa) – R");
            COUNTRY_TO_CURRENCY.put("Mpumalanga", "ZAR – South African Rand (South Africa) – R");
            COUNTRY_TO_CURRENCY.put("Free State", "ZAR – South African Rand (South Africa) – R");
            COUNTRY_TO_CURRENCY.put("North West", "ZAR – South African Rand (South Africa) – R");
            COUNTRY_TO_CURRENCY.put("Northern Cape", "ZAR – South African Rand (South Africa) – R");


        }

        public static String getCurrencyForCountry(String country) {
            return COUNTRY_TO_CURRENCY.get(country);
        }

    }

}
