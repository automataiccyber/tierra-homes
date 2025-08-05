package com.example.tierrahomes;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.TextViewCompat;
import android.util.TypedValue;

public class LoginScreenActivity extends AppCompatActivity {

    private static final String TAG = "LoginScreenActivity";
    private EditText emailEditText, passwordEditText;
    private TextView forgotPasswordTextView, notRegisteredTextView;
    private Button loginButton, createAccountButton;
    private CheckBox rememberCheckBox;
    private ImageView passwordToggleImageView;
    private DatabaseHelper dbHelper;
    private boolean isPasswordVisible = false;

    @SuppressLint({"WrongViewCast", "HardwareIds"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Initialize UI elements
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        createAccountButton = findViewById(R.id.createAccountButton);
        forgotPasswordTextView = findViewById(R.id.forgotTextView);
        notRegisteredTextView = findViewById(R.id.notRegisteredTextView);
        rememberCheckBox = findViewById(R.id.rememberCheckBox);
        passwordToggleImageView = findViewById(R.id.passwordToggleImageView);

        // Make EditText fields responsive to smaller devices
        emailEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f); // Set minimum size
        emailEditText.setAutoSizeTextTypeWithDefaults(android.widget.TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        emailEditText.setAutoSizeTextTypeUniformWithConfiguration(1, 40, 1, TypedValue.COMPLEX_UNIT_SP);

        passwordEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f); // Set minimum size
        passwordEditText.setAutoSizeTextTypeWithDefaults(android.widget.TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        passwordEditText.setAutoSizeTextTypeUniformWithConfiguration(1, 40, 1, TypedValue.COMPLEX_UNIT_SP);

        // Make TextView components responsive to smaller devices
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
            forgotPasswordTextView,
            1,  // min size in sp
            40, // max size in sp
            1,  // step size in sp
            TypedValue.COMPLEX_UNIT_SP
        );
        forgotPasswordTextView.setMaxLines(1);

        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
            notRegisteredTextView,
            1,  // min size in sp
            40, // max size in sp
            1,  // step size in sp
            TypedValue.COMPLEX_UNIT_SP
        );
        notRegisteredTextView.setMaxLines(1);

        // Make CheckBox text responsive to smaller devices
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
            rememberCheckBox,
            1,  // min size in sp
            40, // max size in sp
            1,  // step size in sp
            TypedValue.COMPLEX_UNIT_SP
        );
        rememberCheckBox.setMaxLines(1);

        // Make button text responsive and limit to one line
        createAccountButton.setMaxLines(1);
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
            createAccountButton,
            1,  // min size in sp
            40, // max size in sp
            1,  // step size in sp
            TypedValue.COMPLEX_UNIT_SP
        );

        // Check if previously logged in but Remember Me wasn't checked
        SharedPreferences preferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);
        boolean rememberMe = preferences.getBoolean("rememberMe", false);
        String userEmail = preferences.getString("userEmail", "");

        // If logged in but Remember Me is not checked, pre-fill the email
        if (isLoggedIn && !rememberMe && !userEmail.isEmpty()) {
            emailEditText.setText(userEmail);
        }

        // Password visibility toggle
        passwordToggleImageView.setOnClickListener(v -> {
            if (isPasswordVisible) {
                passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                passwordToggleImageView.setImageResource(R.drawable.eye_close);
                isPasswordVisible = false;
            } else {
                passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                passwordToggleImageView.setImageResource(R.drawable.eye_open);
                isPasswordVisible = true;
            }
            passwordEditText.setSelection(passwordEditText.length());
        });

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
            } else {
                boolean success = dbHelper.loginUser(email, password);
                if (success) {
                    // Get unique device ID
                    String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

                    // Get setup status for this specific user from the database
                    boolean setupComplete = dbHelper.isUserSetupComplete(email);
                    Log.d(TAG, "User setup status from database: " + setupComplete);

                    // Save login state and Remember Me preference
                    boolean rememberMeValue = rememberCheckBox.isChecked();
                    Log.d(TAG, "Remember Me checked: " + rememberMeValue);

                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("isLoggedIn", true);
                    editor.putString("userEmail", email);
                    editor.putString("deviceId", deviceId);
                    editor.putBoolean("isSetupComplete", setupComplete);
                    editor.putBoolean("rememberMe", rememberMeValue);
                    editor.apply();

                    Log.d(TAG, "Login successful for: " + email);

                    // Navigate to appropriate screen based on setup completion
                    Intent intent;
                    if (setupComplete) {
                        Log.d(TAG, "Redirecting to MainScreenActivity after login");
                        intent = new Intent(LoginScreenActivity.this, MainScreenActivity.class);
                    } else {
                        Log.d(TAG, "Redirecting to BudgetAndLocationActivity after login");
                        intent = new Intent(LoginScreenActivity.this, BudgetAndLocationActivity.class);
                    }
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                }
            }

        });

        createAccountButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginScreenActivity.this, SignupScreenActivity.class);
            startActivity(intent);
        });

        forgotPasswordTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginScreenActivity.this, ForgotPasswordScreenActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");
    }
}