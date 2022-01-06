package com.classroom.bookyard.UI;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.classroom.bookyard.Helpers.BaseActivity;
import com.classroom.bookyard.Helpers.DataStatus;
import com.classroom.bookyard.R;
import com.classroom.bookyard.UI.Seller.SellerHome;
import com.classroom.bookyard.login.Login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Register extends BaseActivity implements
        View.OnClickListener {

    private static final String TAG = "EmailPassword";

    private EditText mfnameField;
    private EditText mlastnameField;
    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mRepasswordField;
    private CheckBox sellerCheck;
    private Spinner register_spinner;
    private MaterialTextView fvrt;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ArrayList<String> list = new ArrayList<>();
    // [END declare_auth]

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        // Views

        mfnameField = findViewById(R.id.fieldfname);
        mlastnameField = findViewById(R.id.fieldlastname);
        mEmailField = findViewById(R.id.fieldEmail);
        mPasswordField = findViewById(R.id.fieldPassword);
        mRepasswordField = findViewById(R.id.fieldrePassword);
        sellerCheck = findViewById(R.id.checkSeller);
        fvrt = findViewById(R.id.fvrt);
        // Buttons
        findViewById(R.id.emailCreateAccountButton).setOnClickListener(this);
        findViewById(R.id.signedInButton).setOnClickListener(this);
        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        // [END initialize_auth]

        getCategories(new DataStatus() {
            @Override
            public void onSuccess(ArrayList list) {
                register_spinner = findViewById(R.id.register_spinner);
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Register.this,
                        R.layout.spinner_item, (ArrayList<String>) list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                register_spinner.setAdapter(dataAdapter);
            }

            @Override
            public void onError(String e) {

            }
        });
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    // [END on_start_check_user]

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete( Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //String f_cat = register_spinner.getSelectedItem().toString();
                            String f_cat = (String.valueOf(register_spinner.getSelectedItem()));

                            putData(user.getUid(),user.getEmail(),mfnameField.getText().toString(),mlastnameField.getText().toString(),f_cat);
                            updateUI(user);
                            getIdS();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Register.this, "createUser failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }



    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        String repass = mRepasswordField.getText().toString();
        if(password.equals(repass))
        {
            mPasswordField.setError(null);

        } else {
            mPasswordField.setError("Error");
            mRepasswordField.setError("Error");
            valid = false;
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
            createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
        }
        if(i == R.id.signedInButton){
            Intent my = new Intent(this, Login.class);
            startActivity(my);
            finish();
        }

    }

    public void putData(String id , String email , String fname , String lastname , String f_cat ){
        Map<String, Object> user = new HashMap<>();
        user.put("Email", email);
        user.put("fname", fname);
        user.put("lastname", lastname);
        user.put("Telephone","");
        user.put("image", "");
        //String cat =  String.valueOf(register_spinner.getSelectedItem());
        user.put("f_cat",f_cat);
        if(sellerCheck.isChecked()){
            user.put("seller","true");
        }else{ user.put("seller","false"); }

// Add a new document with a generated ID
        db.collection("Users").document(id)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure( Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
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
                            startActivity(new Intent(Register.this, SellerHome.class));
                            finish();
                        } else {
                            startActivity(new Intent(Register.this, HomeActivity.class));
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

    public void getCategories(final DataStatus callback) {

        db.collection("categories")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //if (document.getString("name") != null)
                                list.add(document.getId());
                            }
                            callback.onSuccess(list);

                        } else {
                            callback.onError("Error in data");
                            //Log.w(TAG, "Error getting documents.", task.getException());
                        }

                    }
                });

    }


}