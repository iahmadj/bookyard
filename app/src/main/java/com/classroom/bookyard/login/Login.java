package com.classroom.bookyard.login;

import android.content.Intent;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.classroom.bookyard.Helpers.BaseActivity;
import com.classroom.bookyard.R;
import com.classroom.bookyard.UI.HomeActivity;
import com.classroom.bookyard.UI.Register;
import com.classroom.bookyard.UI.Seller.SellerHome;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "EmailPassword";

    private com.google.android.material.textfield.TextInputEditText mEmailField;
    private com.google.android.material.textfield.TextInputEditText mPasswordField;
    FirebaseFirestore db;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);


        // Views
        mEmailField = findViewById(R.id.fieldEmail);
        mPasswordField = findViewById(R.id.fieldPassword);

        // Buttons
        findViewById(R.id.emailSignInButton).setOnClickListener(this);
        findViewById(R.id.emailCreateAccountButton).setOnClickListener(this);


        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // [END initialize_auth]

    }

    @Override
    public void onBackPressed() {
            super.onBackPressed();
            finishAffinity();
        }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
        if(currentUser!= null){
            getIdS();
        }
    }
    // [END on_start_check_user]


    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete( Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            getIdS();
                            Toast.makeText(Login.this, "Authentication Success.",
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }




    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Password Require");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Password Require");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            user.getEmail();
            user.isEmailVerified();
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.emailCreateAccountButton) {
            Intent my = new Intent(this, Register.class);
            startActivity(my);
        } else if (i == R.id.emailSignInButton) {
            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
        }
    }

    protected void getIdS() {
        showProgressDialog();
        db.collection("Users").document(mAuth.getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete( Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if (document.getString("seller").equals("true")) {
                            startActivity(new Intent(Login.this, SellerHome.class));
                            finish();
                        } else {
                            startActivity(new Intent(Login.this, HomeActivity.class));
                            finish();
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

}
