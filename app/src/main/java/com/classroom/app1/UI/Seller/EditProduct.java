package com.classroom.app1.UI.Seller;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import com.classroom.app1.Helpers.BaseActivity;
import com.classroom.app1.Helpers.DataStatus;
import com.classroom.app1.Helpers.Singleton;
import com.classroom.app1.R;
import com.classroom.app1.products.ProductsListActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class EditProduct extends BaseActivity {


    private com.google.android.material.textfield.TextInputEditText Etitle;
    private com.google.android.material.textfield.TextInputEditText Edes;
    private com.google.android.material.textfield.TextInputEditText Eprice;
    private com.google.android.material.button.MaterialButton update_product;
    ImageView back_button_eproduct;


    // [START declare_auth]

    private FirebaseFirestore db = Singleton.getDb();
    private FirebaseUser user = Singleton.getUser();
    String UID = user.getUid();
    String idItem;
    private Spinner Espinner;
    private ArrayList<String> list = new ArrayList<>();
    // [END declare_auth]

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        // Views

        Etitle = findViewById(R.id.Etitle);
        Edes = findViewById(R.id.Edes);
        Eprice = findViewById(R.id.Eprice);
        update_product = findViewById(R.id.update_product);
        back_button_eproduct = findViewById(R.id.back_button_eproduct);
        idItem = (String) getIntent().getSerializableExtra("idItem");

        db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        String value = intent.getStringExtra("value1");


         DocumentReference reference = db.collection("products").document(value);

        db.collection("products").document(value)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    String fname, des, price, Address;
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        fname = document.getString("fname");
                       des = document.getString("description");
                        price = document.getDouble("price").toString();

                        Etitle.setText(fname);
                        Edes.setText(des);
                        Eprice.setText(price);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Data not found", Toast.LENGTH_SHORT);
                }
            }
        });


        update_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> noteTitle = new HashMap<>();
                noteTitle.put("fname", Etitle.getText().toString());
                noteTitle.put("description", Edes.getText().toString());
                String s = Eprice.getText().toString();
                double d = Double.parseDouble(s);
                noteTitle.put("price", d);
                String cat = (String.valueOf(Espinner.getSelectedItem()));
                noteTitle.put("id_cat", cat);

                reference.update(noteTitle);
                Toast.makeText(getApplicationContext(), "Product Updated", Toast.LENGTH_SHORT).show();
                Intent profile = new Intent(EditProduct.this, SellerHome.class);
                startActivity(profile);
            }
        });



        getCategories(new DataStatus() {
            @Override
            public void onSuccess(ArrayList list) {
                Espinner = findViewById(R.id.Espinner);
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(EditProduct.this,
                        R.layout.spinner_item, (ArrayList<String>) list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                Espinner.setAdapter(dataAdapter);
            }

            @Override
            public void onError(String e) {

            }
        });



        back_button_eproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditProduct.this, SellerHome.class));
                finish();
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