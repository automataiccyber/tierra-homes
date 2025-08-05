package com.example.tierrahomes;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.TextViewCompat;
import android.util.TypedValue;

public class SignupScreenActivity extends AppCompatActivity {

    private EditText usernameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private Button signupButton;
    private TextView loginTextView, alreadyHaveAccountTextView;
    private DatabaseHelper dbHelper;
    private ImageView eyeImageViewPassword, eyeImageViewConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_screen);

        dbHelper = new DatabaseHelper(this);

        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        signupButton = findViewById(R.id.signupButton);
        loginTextView = findViewById(R.id.loginTextView);
        alreadyHaveAccountTextView = findViewById(R.id.alreadyHaveAccountTextView);
        eyeImageViewPassword = findViewById(R.id.passwordToggleImageView); // Eye icon for password
        eyeImageViewConfirmPassword = findViewById(R.id.confirmPasswordToggleImageView); // Eye icon for confirm password

        // Make EditText fields responsive to smaller devices
        usernameEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f); // Set minimum size
        usernameEditText.setAutoSizeTextTypeWithDefaults(android.widget.TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        usernameEditText.setAutoSizeTextTypeUniformWithConfiguration(1, 40, 1, TypedValue.COMPLEX_UNIT_SP);

        emailEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f); // Set minimum size
        emailEditText.setAutoSizeTextTypeWithDefaults(android.widget.TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        emailEditText.setAutoSizeTextTypeUniformWithConfiguration(1, 40, 1, TypedValue.COMPLEX_UNIT_SP);

        passwordEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f); // Set minimum size
        passwordEditText.setAutoSizeTextTypeWithDefaults(android.widget.TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        passwordEditText.setAutoSizeTextTypeUniformWithConfiguration(1, 40, 1, TypedValue.COMPLEX_UNIT_SP);

        confirmPasswordEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f); // Set minimum size
        confirmPasswordEditText.setAutoSizeTextTypeWithDefaults(android.widget.TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        confirmPasswordEditText.setAutoSizeTextTypeUniformWithConfiguration(1, 40, 1, TypedValue.COMPLEX_UNIT_SP);

        // Make TextView components responsive to smaller devices
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
            loginTextView,
            1,  // min size in sp
            40, // max size in sp
            1,  // step size in sp
            TypedValue.COMPLEX_UNIT_SP
        );
        loginTextView.setMaxLines(1);

        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
            alreadyHaveAccountTextView,
            1,  // min size in sp
            40, // max size in sp
            1,  // step size in sp
            TypedValue.COMPLEX_UNIT_SP
        );
        alreadyHaveAccountTextView.setMaxLines(1);

        // Make button text responsive and limit to one line
        signupButton.setMaxLines(1);
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
            signupButton,
            1,  // min size in sp
            40, // max size in sp
            1,  // step size in sp
            TypedValue.COMPLEX_UNIT_SP
        );

        // Toggle password visibility on eye icon click
        eyeImageViewPassword.setOnClickListener(v -> togglePasswordVisibility(passwordEditText, eyeImageViewPassword));
        eyeImageViewConfirmPassword.setOnClickListener(v -> togglePasswordVisibility(confirmPasswordEditText, eyeImageViewConfirmPassword));

        signupButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            // Input validation
            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            } else if (!isPasswordStrong(password)) {
                Toast.makeText(this, "Password too weak. Use at least 6 characters with letters and numbers.", Toast.LENGTH_LONG).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else if (dbHelper.checkEmailExists(email)) {
                Toast.makeText(this, "Email already registered", Toast.LENGTH_SHORT).show();
            } else if (dbHelper.checkUsernameExists(username)) {
                Toast.makeText(this, "Username already taken", Toast.LENGTH_SHORT).show();
            } else {
                // Proceed with user registration
                boolean success = dbHelper.registerUser(username, email, password);
                if (success) {
                    Toast.makeText(this, "Sign Up Successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignupScreenActivity.this, LoginScreenActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Sign Up Failed. Try again.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        // Redirect to Login screen when "Already have an account?" is clicked
        loginTextView.setOnClickListener(v -> {
            startActivity(new Intent(SignupScreenActivity.this, LoginScreenActivity.class));

        });
    }

    // Toggle password visibility
    private void togglePasswordVisibility(EditText passwordEditText, ImageView eyeImageView) {
        if (passwordEditText.getTransformationMethod() instanceof android.text.method.PasswordTransformationMethod) {
            // If password is hidden, show it
            passwordEditText.setTransformationMethod(null);
            eyeImageView.setImageResource(R.drawable.eye_open); // Open eye icon
        } else {
            // If password is visible, hide it
            passwordEditText.setTransformationMethod(android.text.method.PasswordTransformationMethod.getInstance());
            eyeImageView.setImageResource(R.drawable.eye_close); // Closed eye icon
        }

        // Move cursor to the end of the text after toggling
        passwordEditText.setSelection(passwordEditText.getText().length());
    }

    // Password strength checker
    private boolean isPasswordStrong(String password) {
        return password.length() >= 6 && password.matches(".*[a-zA-Z].*") && password.matches(".*[0-9].*");
    }
}

