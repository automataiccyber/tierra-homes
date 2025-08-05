package com.example.tierrahomes;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.TextViewCompat;
import android.util.TypedValue;

public class ForgotPasswordScreenActivity extends AppCompatActivity {

    private EditText newPasswordEditText, confirmPasswordEditText, emailEditText, usernameEditText;
    private ImageView passwordToggleImageView, confirmPasswordToggleImageView;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password_screen); // Ensure this points to your XML

        dbHelper = new DatabaseHelper(this);

        // Initialize UI components
        newPasswordEditText = findViewById(R.id.newpasswordEditText);
        confirmPasswordEditText = findViewById(R.id.newconfirmPasswordEditText);
        emailEditText = findViewById(R.id.emailEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordToggleImageView = findViewById(R.id.passwordToggleImageView);
        confirmPasswordToggleImageView = findViewById(R.id.confirmPasswordToggleImageView);
        Button changePasswordButton = findViewById(R.id.signupButton);

        // Make EditText fields responsive to smaller devices
        usernameEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f); // Set minimum size
        usernameEditText.setAutoSizeTextTypeWithDefaults(android.widget.TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        usernameEditText.setAutoSizeTextTypeUniformWithConfiguration(1, 40, 1, TypedValue.COMPLEX_UNIT_SP);

        emailEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f); // Set minimum size
        emailEditText.setAutoSizeTextTypeWithDefaults(android.widget.TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        emailEditText.setAutoSizeTextTypeUniformWithConfiguration(1, 40, 1, TypedValue.COMPLEX_UNIT_SP);

        newPasswordEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f); // Set minimum size
        newPasswordEditText.setAutoSizeTextTypeWithDefaults(android.widget.TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        newPasswordEditText.setAutoSizeTextTypeUniformWithConfiguration(1, 40, 1, TypedValue.COMPLEX_UNIT_SP);

        confirmPasswordEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f); // Set minimum size
        confirmPasswordEditText.setAutoSizeTextTypeWithDefaults(android.widget.TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        confirmPasswordEditText.setAutoSizeTextTypeUniformWithConfiguration(1, 40, 1, TypedValue.COMPLEX_UNIT_SP);

        // Make button text responsive and limit to one line
        changePasswordButton.setMaxLines(1);
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
            changePasswordButton,
            1,  // min size in sp
            40, // max size in sp
            1,  // step size in sp
            TypedValue.COMPLEX_UNIT_SP
        );

        // Toggle password visibility for "New password" field
        passwordToggleImageView.setOnClickListener(v -> togglePasswordVisibility(newPasswordEditText, passwordToggleImageView));

        // Toggle password visibility for "Confirm password" field
        confirmPasswordToggleImageView.setOnClickListener(v -> togglePasswordVisibility(confirmPasswordEditText, confirmPasswordToggleImageView));

        // Handle change password button click
        changePasswordButton.setOnClickListener(v -> {
            String newPassword = newPasswordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String username = usernameEditText.getText().toString().trim();

            // Perform data validation
            if (newPassword.isEmpty() || confirmPassword.isEmpty() || email.isEmpty() || username.isEmpty()) {
                Toast.makeText(ForgotPasswordScreenActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else if (newPassword.length() < 6) {
                Toast.makeText(ForgotPasswordScreenActivity.this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
            } else if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(ForgotPasswordScreenActivity.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            } else {
                // Check if the username and email exist in the database
                boolean emailExists = dbHelper.checkEmailExists(email);
                boolean usernameExists = dbHelper.checkUsernameExists(username);

                if (!emailExists || !usernameExists) {
                    Toast.makeText(ForgotPasswordScreenActivity.this, "Invalid email or username", Toast.LENGTH_SHORT).show();
                    return; // Prevent further actions if invalid
                } else {
                    // Check if new password matches the old password from the database
                    String oldPassword = dbHelper.getOldPassword(email, username);
                    if (newPassword.equals(oldPassword)) {
                        // New password is the same as the old password
                        Toast.makeText(ForgotPasswordScreenActivity.this, "New password cannot be the same as the old password", Toast.LENGTH_SHORT).show();
                    } else {
                        // Proceed with updating the password in the database
                        boolean success = dbHelper.resetPassword(email, username, newPassword);
                        if (success) {
                            Toast.makeText(ForgotPasswordScreenActivity.this, "Password successfully updated!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ForgotPasswordScreenActivity.this, LoginScreenActivity.class));
                            finish();
                        } else {
                            Toast.makeText(ForgotPasswordScreenActivity.this, "Error updating password", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

        });
    }

    // Toggle password visibility
    private void togglePasswordVisibility(EditText passwordEditText, ImageView eyeImageView) {
        // Handle the password visibility toggle
        if (passwordEditText.getTransformationMethod() instanceof PasswordTransformationMethod) {
            // If the password is hidden, show it
            passwordEditText.setTransformationMethod(null);
            eyeImageView.setImageResource(R.drawable.eye_open); // Open eye icon
        } else {
            // If the password is visible, hide it
            passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            eyeImageView.setImageResource(R.drawable.eye_close); // Closed eye icon
        }

        // Move cursor to the end of the text after toggling
        passwordEditText.setSelection(passwordEditText.getText().length());
    }
}
