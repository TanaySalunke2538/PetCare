package com.example.petcare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class Landingpage extends AppCompatActivity {

    // =====================================================
    // FIREBASE / GOOGLE
    // =====================================================

    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;


    // =====================================================
    // BUTTONS
    // =====================================================

    private Button btnLogin;
    private Button btnCreateAccount;
    private SignInButton btnGoogle;


    // =====================================================
    // ANIMATION VIEWS
    // =====================================================

    private View landingCard;
    private View introLogoCard;


    // =====================================================
    // GOOGLE SIGN-IN RESULT
    // =====================================================

    private final ActivityResultLauncher<Intent> googleSignInLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (result.getData() == null) {
                            return;
                        }

                        Task<GoogleSignInAccount> task =
                                GoogleSignIn.getSignedInAccountFromIntent(
                                        result.getData()
                                );

                        try {

                            GoogleSignInAccount account =
                                    task.getResult(ApiException.class);

                            firebaseAuthWithGoogle(
                                    account.getIdToken()
                            );

                        } catch (ApiException e) {

                            Toast.makeText(
                                    this,
                                    "Google Sign-In Failed",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
            );


    // =====================================================
    // ON CREATE
    // =====================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.landingpage);


        // =================================================
        // FIREBASE
        // =================================================

        firebaseAuth =
                FirebaseAuth.getInstance();


        // =================================================
        // BUTTONS
        // =================================================

        btnLogin =
                findViewById(R.id.btnLogin);

        btnCreateAccount =
                findViewById(R.id.btnCreateAccount);

        btnGoogle =
                findViewById(R.id.btnGoogle);


        // =================================================
        // ANIMATION VIEWS
        // =================================================

        landingCard =
                findViewById(R.id.landingCard);

        introLogoCard =
                findViewById(R.id.introLogoCard);


        // =================================================
        // GOOGLE SIGN-IN CONFIGURATION
        // =================================================

        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(
                        GoogleSignInOptions.DEFAULT_SIGN_IN
                )
                        .requestIdToken(
                                getString(
                                        R.string.default_web_client_id
                                )
                        )
                        .requestEmail()
                        .build();


        googleSignInClient =
                GoogleSignIn.getClient(
                        this,
                        gso
                );


        // =================================================
        // BUTTON ACTIONS
        // =================================================

        btnLogin.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            Landingpage.this,
                            Login.class
                    )
            );
        });


        btnCreateAccount.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            Landingpage.this,
                            Createacc.class
                    )
            );
        });


        btnGoogle.setOnClickListener(v -> {

            Intent signInIntent =
                    googleSignInClient.getSignInIntent();

            googleSignInLauncher.launch(
                    signInIntent
            );
        });


        // =================================================
        // START INTRO ANIMATION
        // =================================================

        startIntroAnimation();
    }


    // =====================================================
    // INTRO ANIMATION
    // =====================================================

    private void startIntroAnimation() {

        AccelerateDecelerateInterpolator interpolator =
                new AccelerateDecelerateInterpolator();


        // =================================================
        // INITIAL STATE
        // =================================================

        landingCard.setAlpha(0f);

        introLogoCard.setAlpha(0f);

        introLogoCard.setScaleX(0.75f);

        introLogoCard.setScaleY(0.75f);


        // =================================================
        // STEP 1
        // LOGO FADES IN + SCALES UP
        // =================================================

        introLogoCard.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(800)
                .setInterpolator(interpolator)
                .withEndAction(() -> {


                    // =====================================
                    // STEP 2
                    // HOLD LOGO
                    // =====================================

                    introLogoCard.postDelayed(() -> {


                        // ================================
                        // STEP 3
                        // LOGO SLOWLY FADES OUT
                        // ================================

                        introLogoCard.animate()
                                .alpha(0f)
                                .scaleX(1.08f)
                                .scaleY(1.08f)
                                .setDuration(1200)
                                .setInterpolator(interpolator)
                                .withEndAction(() -> {


                                    // ============================
                                    // STEP 4
                                    // LANDING PAGE FADES IN
                                    // ============================

                                    introLogoCard.setVisibility(
                                            View.GONE
                                    );


                                    landingCard.animate()
                                            .alpha(1f)
                                            .setDuration(900)
                                            .setInterpolator(
                                                    interpolator
                                            )
                                            .start();

                                })
                                .start();

                    }, 500);

                })
                .start();
    }


    // =====================================================
    // FIREBASE GOOGLE AUTHENTICATION
    // =====================================================

    private void firebaseAuthWithGoogle(
            String idToken
    ) {

        AuthCredential credential =
                GoogleAuthProvider.getCredential(
                        idToken,
                        null
                );


        firebaseAuth
                .signInWithCredential(credential)
                .addOnCompleteListener(
                        this,
                        task -> {

                            if (task.isSuccessful()) {


                                Toast.makeText(
                                        this,
                                        "Login Successful",
                                        Toast.LENGTH_SHORT
                                ).show();


                                String name =
                                        firebaseAuth
                                                .getCurrentUser()
                                                .getDisplayName();


                                Intent intent =
                                        new Intent(
                                                Landingpage.this,
                                                Home.class
                                        );


                                intent.putExtra(
                                        "username",
                                        name
                                );


                                startActivity(intent);

                                finish();


                            } else {


                                Exception e =
                                        task.getException();


                                Toast.makeText(
                                        this,
                                        "Authentication Failed",
                                        Toast.LENGTH_LONG
                                ).show();


                                if (e != null) {

                                    e.printStackTrace();


                                    Toast.makeText(
                                            this,
                                            e.getMessage(),
                                            Toast.LENGTH_LONG
                                    ).show();
                                }
                            }
                        }
                );
    }
}