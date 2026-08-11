package com.example.petcare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {

    private TextInputEditText etUsername, etPassword;
    private Button btnLogin;

    private FirebaseFirestore db;

    // Animation views
    private View logo;
    private View welcome;
    private View subtitle;
    private View usernameLayout;
    private View passwordLayout;
    private View forgotPassword;
    private View loginButton;
    private View createAccount;
    private View secure;

    private final DecelerateInterpolator interpolator =
            new DecelerateInterpolator(1.5f);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // =====================================================
        // FIND VIEWS
        // =====================================================

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        logo = findViewById(R.id.logoCard);
        welcome = findViewById(R.id.txtWelcome);
        subtitle = findViewById(R.id.txtSubtitle);
        usernameLayout = findViewById(R.id.usernameLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        forgotPassword = findViewById(R.id.txtForgotPassword);
        loginButton = findViewById(R.id.btnLogin);
        createAccount = findViewById(R.id.txtCreateAccount);
        secure = findViewById(R.id.txtSecure);

        // =====================================================
        // FIREBASE
        // =====================================================

        db = FirebaseFirestore.getInstance();

        // =====================================================
        // START LOGIN PAGE ANIMATION
        // =====================================================

        startLoginAnimations();

        // =====================================================
        // LOGIN BUTTON
        // =====================================================

        btnLogin.setOnClickListener(v -> {

            // Button press animation
            v.animate()
                    .scaleX(0.95f)
                    .scaleY(0.95f)
                    .setDuration(100)
                    .withEndAction(() -> {

                        v.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(100)
                                .start();

                        loginUser();
                    })
                    .start();
        });

        // =====================================================
        // CREATE ACCOUNT
        // =====================================================

        createAccount.setOnClickListener(v -> {

            // Small press effect
            v.animate()
                    .scaleX(0.95f)
                    .scaleY(0.95f)
                    .setDuration(80)
                    .withEndAction(() -> {

                        v.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(80)
                                .start();

                        /*
                         * Add your Register Activity here when ready.
                         *
                         * Example:
                         *
                         * Intent intent =
                         *         new Intent(Login.this, Register.class);
                         *
                         * startActivity(intent);
                         */
                    })
                    .start();
        });
    }

    // =====================================================
    // LOGIN PAGE ANIMATIONS
    // =====================================================

    private void startLoginAnimations() {

        // -------------------------------------------------
        // Initial states
        // -------------------------------------------------

        // Logo
        logo.setAlpha(0f);
        logo.setScaleX(0.75f);
        logo.setScaleY(0.75f);

        // Welcome
        welcome.setAlpha(0f);
        welcome.setTranslationY(60f);

        // Subtitle
        subtitle.setAlpha(0f);
        subtitle.setTranslationY(60f);

        // Username
        usernameLayout.setAlpha(0f);
        usernameLayout.setTranslationY(80f);

        // Password
        passwordLayout.setAlpha(0f);
        passwordLayout.setTranslationY(80f);

        // Forgot password
        forgotPassword.setAlpha(0f);
        forgotPassword.setTranslationY(50f);

        // Login button
        loginButton.setAlpha(0f);
        loginButton.setTranslationY(80f);

        // Create account
        createAccount.setAlpha(0f);
        createAccount.setTranslationY(60f);

        // Secure text
        secure.setAlpha(0f);


        // -------------------------------------------------
        // 1. LOGO
        // -------------------------------------------------

        logo.animate()
                .alpha(1f)
                .scaleX(1.05f)
                .scaleY(1.05f)
                .setDuration(600)
                .setInterpolator(interpolator)
                .withEndAction(() -> {

                    logo.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(180)
                            .setInterpolator(interpolator)
                            .start();

                })
                .start();


        // -------------------------------------------------
        // 2. WELCOME TEXT
        // -------------------------------------------------

        welcome.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(300)
                .setDuration(550)
                .setInterpolator(interpolator)
                .start();


        // -------------------------------------------------
        // 3. SUBTITLE
        // -------------------------------------------------

        subtitle.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(450)
                .setDuration(500)
                .setInterpolator(interpolator)
                .start();


        // -------------------------------------------------
        // 4. USERNAME
        // -------------------------------------------------

        usernameLayout.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(600)
                .setDuration(550)
                .setInterpolator(interpolator)
                .start();


        // -------------------------------------------------
        // 5. PASSWORD
        // -------------------------------------------------

        passwordLayout.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(750)
                .setDuration(550)
                .setInterpolator(interpolator)
                .start();


        // -------------------------------------------------
        // 6. FORGOT PASSWORD
        // -------------------------------------------------

        forgotPassword.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(900)
                .setDuration(450)
                .setInterpolator(interpolator)
                .start();


        // -------------------------------------------------
        // 7. LOGIN BUTTON
        // -------------------------------------------------

        loginButton.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(1050)
                .setDuration(550)
                .setInterpolator(interpolator)
                .start();


        // -------------------------------------------------
        // 8. CREATE ACCOUNT
        // -------------------------------------------------

        createAccount.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(1250)
                .setDuration(500)
                .setInterpolator(interpolator)
                .start();


        // -------------------------------------------------
        // 9. FIREBASE TEXT
        // -------------------------------------------------

        secure.animate()
                .alpha(1f)
                .setStartDelay(1400)
                .setDuration(450)
                .setInterpolator(interpolator)
                .start();
    }

    // =====================================================
    // LOGIN USER
    // =====================================================

    private void loginUser() {

        String username =
                etUsername.getText().toString().trim();

        String password =
                etPassword.getText().toString().trim();

        // -------------------------------------------------
        // USERNAME VALIDATION
        // -------------------------------------------------

        if (username.isEmpty()) {

            etUsername.setError("Enter Username");
            etUsername.requestFocus();

            shakeView(usernameLayout);

            return;
        }

        // -------------------------------------------------
        // PASSWORD VALIDATION
        // -------------------------------------------------

        if (password.isEmpty()) {

            etPassword.setError("Enter Password");
            etPassword.requestFocus();

            shakeView(passwordLayout);

            return;
        }

        // -------------------------------------------------
        // LOGIN BUTTON LOADING
        // -------------------------------------------------

        btnLogin.setEnabled(false);
        btnLogin.setText("Logging in...");

        // -------------------------------------------------
        // FIRESTORE LOGIN
        // -------------------------------------------------

        db.collection("users")
                .document(username)
                .get()

                .addOnSuccessListener(documentSnapshot -> {

                    btnLogin.setEnabled(true);
                    btnLogin.setText("LOGIN");

                    // -------------------------------------------------
                    // USER NOT FOUND
                    // -------------------------------------------------

                    if (!documentSnapshot.exists()) {

                        Toast.makeText(
                                Login.this,
                                "User not found",
                                Toast.LENGTH_SHORT
                        ).show();

                        shakeView(usernameLayout);
                        etUsername.requestFocus();

                        return;
                    }

                    // -------------------------------------------------
                    // GET STORED PASSWORD
                    // -------------------------------------------------

                    String storedPassword =
                            documentSnapshot.getString("password");

                    // -------------------------------------------------
                    // CORRECT PASSWORD
                    // -------------------------------------------------

                    if (storedPassword != null &&
                            storedPassword.equals(password)) {

                        Toast.makeText(
                                Login.this,
                                "Login Successful",
                                Toast.LENGTH_SHORT
                        ).show();

                        Intent intent =
                                new Intent(Login.this, Home.class);

                        intent.putExtra(
                                "username",
                                username
                        );

                        startActivity(intent);

                        finish();

                    }

                    // -------------------------------------------------
                    // INCORRECT PASSWORD
                    // -------------------------------------------------

                    else {

                        Toast.makeText(
                                Login.this,
                                "Incorrect Password",
                                Toast.LENGTH_SHORT
                        ).show();

                        shakeView(passwordLayout);
                        etPassword.requestFocus();
                    }

                })

                // -------------------------------------------------
                // FIRESTORE ERROR
                // -------------------------------------------------

                .addOnFailureListener(e -> {

                    btnLogin.setEnabled(true);
                    btnLogin.setText("LOGIN");

                    Toast.makeText(
                            Login.this,
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    // =====================================================
    // SHAKE ANIMATION FOR ERRORS
    // =====================================================

    private void shakeView(View view) {

        view.animate()
                .translationX(25f)
                .setDuration(70)

                .withEndAction(() ->

                        view.animate()
                                .translationX(-25f)
                                .setDuration(70)

                                .withEndAction(() ->

                                        view.animate()
                                                .translationX(20f)
                                                .setDuration(70)

                                                .withEndAction(() ->

                                                        view.animate()
                                                                .translationX(-20f)
                                                                .setDuration(70)

                                                                .withEndAction(() ->

                                                                        view.animate()
                                                                                .translationX(0f)
                                                                                .setDuration(70)
                                                                                .start()

                                                                )
                                                                .start()

                                                )
                                                .start()

                                )
                                .start()

                )
                .start();
    }
}