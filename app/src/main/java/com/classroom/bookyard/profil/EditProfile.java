package com.classroom.bookyard.profil;

import static com.google.android.gms.common.util.CollectionUtils.mapOf;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import java.util.List;
import java.util.Locale;
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
    LocationManager locationManager;
    LocationListener locationListener;


    // [START declare_auth]

    private FirebaseFirestore db = Singleton.getDb();
    private FirebaseUser user = Singleton.getUser();
    String UID = user.getUid();
    String user_current_address;
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


        locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener= new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                user_current_address=update_location_info(location);
                Eaddress.setText(user_current_address);
            }
        };


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


    public void find_user_current_location(View view)
    {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Getting location wait",Toast.LENGTH_SHORT).show();
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,6000,50,locationListener);
        }

    }

    private String update_location_info(Location location)
    {
        String address="";
        Geocoder geocoder= new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses=geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            if(addresses!=null && addresses.size()>0)
            {
                if(addresses.get(0).getAddressLine(0)!=null)
                {
                    address+=addresses.get(0).getAddressLine(0)+"\n";
                }
                if(addresses.get(0).getPostalCode()!=null)
                {
                    address+="Postal Code: "+addresses.get(0).getPostalCode();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return address;
    }


}