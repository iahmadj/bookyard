package com.classroom.bookyard.profil;

import static com.google.android.gms.common.util.CollectionUtils.mapOf;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.classroom.bookyard.Helpers.BaseActivity;
import com.classroom.bookyard.Helpers.Singleton;
import com.classroom.bookyard.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.Map;


public class EditProfile extends BaseActivity {


    private com.google.android.material.textfield.TextInputEditText EfName;
    private com.google.android.material.textfield.TextInputEditText ElastName;
    private com.google.android.material.textfield.TextInputEditText Ephone;
    private com.google.android.material.textfield.TextInputEditText Efacebook;
    private com.google.android.material.textfield.TextInputEditText Eaddress;
    private com.google.android.material.textfield.TextInputEditText fvrt;
    private com.google.android.material.button.MaterialButton update_profile;
    ImageView back_button_eprofile;


    // [START declare_auth]

    private FirebaseFirestore db = Singleton.getDb();
    private FirebaseUser user = Singleton.getUser();
    String UID = user.getUid();
    // [END declare_auth]

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Views

        EfName = findViewById(R.id.EfName);
        ElastName = findViewById(R.id.ElastName);
        Ephone = findViewById(R.id.Ephone);
        Eaddress = findViewById(R.id.Eaddress);
        fvrt = findViewById(R.id.fvrt);
        update_profile = findViewById(R.id.update_profile);
        back_button_eprofile = findViewById(R.id.back_button_eprofile);


        db = FirebaseFirestore.getInstance();

        DocumentReference reference = db.collection("Users").document(UID);

        db.collection("Users").document(UID)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    String fname, lastname, phone, Address;
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        fname = document.getString("fname");
                        lastname = document.getString("lastname");
                        phone = document.getString("Telephone");
                        Address = document.getString("Address");
                        EfName.setText(fname);
                        ElastName.setText(lastname);
                        Ephone.setText(phone);
                        Eaddress.setText(Address);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Data not found", Toast.LENGTH_SHORT);
                }
            }
        });


        update_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> noteTitle = new HashMap<>();
                noteTitle.put("fname", EfName.getText().toString());
                noteTitle.put("lastname", ElastName.getText().toString());
                noteTitle.put("Telephone", Ephone.getText().toString());
                noteTitle.put("Address", Eaddress.getText().toString());

                reference.update(noteTitle);
                Intent profile = new Intent(EditProfile.this, ProfilActivity.class);
                startActivity(profile);
            }
        });

        back_button_eprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditProfile.this, ProfilActivity.class));
                finish();
            }
        });
    }
        }