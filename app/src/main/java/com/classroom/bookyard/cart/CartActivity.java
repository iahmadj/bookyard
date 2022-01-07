package com.classroom.bookyard.cart;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.classroom.bookyard.Helpers.Singleton;
import com.classroom.bookyard.Helpers.TinyDB;
import com.classroom.bookyard.Model.Product;
import com.classroom.bookyard.R;
import com.classroom.bookyard.UI.HomeActivity;
import com.classroom.bookyard.profil.EditProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CartActivity extends AppCompatActivity  {

    private CartProductAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<Product> listItems = new ArrayList<>();
    private FirebaseFirestore db = Singleton.getDb();
    private TinyDB tinydb;
    com.google.android.material.button.MaterialButton rate;
    private Dialog myDialog;
    private FirebaseUser user = Singleton.getUser();
    com.google.android.material.button.MaterialButton delete_all,order_button;
    ImageView back_button_cart;
    String UID = user.getUid();
    String Address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.cart_rc);
        delete_all = findViewById(R.id.delete_all);
        order_button = findViewById(R.id.order_button);

        back_button_cart = findViewById(R.id.back_button_cart);
        LinearLayoutManager layoutManager = new LinearLayoutManager(CartActivity.this,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        fetchRemoteData();


        db.collection("Users").document(UID)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Address = document.getString("Address");
                       // Toast.makeText(CartActivity.this, Address, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Data not found", Toast.LENGTH_SHORT);
                }
            }
        });


        delete_all.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tinydb.remove((user.getUid()));
                Toast.makeText(CartActivity.this, "Cart is Cleared",
                        Toast.LENGTH_SHORT).show();

                startActivity(new Intent(CartActivity.this, HomeActivity.class));
            }
        });
        back_button_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });


        order_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (Address == null || Address ==  " " || Address == "")
                {
                    Toast.makeText(getApplicationContext(), "Please Enter your Address Before Checkout", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CartActivity.this, EditProfile.class));
                }
                else {
                    myDialog = new Dialog(CartActivity.this);
                    myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    assert myDialog.getWindow() != null;
                    myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    myDialog.setCancelable(false);
                    myDialog.setContentView(R.layout.rate_us_dialog);
                    rate = myDialog.findViewById(R.id.rateButton);
                    myDialog.show();

                    myDialog.findViewById(R.id.close_rate_dialog).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            myDialog.dismiss();
                        }
                    });
                    rate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            addOrder();
                            startActivity(new Intent(CartActivity.this, HomeActivity.class));
                            myDialog.dismiss();
                        }
                    });
                }
            }
        });



    }

    private void fetchRemoteData() {
        tinydb = new TinyDB(CartActivity.this);
        ArrayList<Product> productsOnCart = tinydb.getListObject(user.getUid(), Product.class);
        for (int i = 0; i < productsOnCart.size(); i++) {
            Product item = new Product(productsOnCart.get(i).getId_product(),
                    productsOnCart.get(i).getId_seller(),
                    productsOnCart.get(i).getId_cat(),
                    productsOnCart.get(i).getfname(),
                    productsOnCart.get(i).getDescription(),
                    productsOnCart.get(i).getImg_product(),
                    productsOnCart.get(i).getPrice()
            );
            listItems.add(item);
        }
        if (listItems.size() != 0) {
            //favtext.setVisibility(View.GONE);
        }

        Collections.reverse(listItems);
        onSuccess(listItems);



    }

    public void onSuccess(ArrayList<Product> posts) {
        TextView total_price = findViewById(R.id.total_price);
        if (adapter == null) {
            adapter = new CartProductAdapter(posts, CartActivity.this);
            recyclerView.setAdapter(adapter);

            if (!posts.isEmpty()) {
                int totalPrice = 0;
                for (int i = 0; i < posts.size(); i++) {
                    totalPrice += posts.get(i).getPrice();
                }
                total_price.setText("Your Total Price Rs. " + totalPrice + "/-");
            } else {
                total_price.setVisibility(View.GONE);
            }
        } else {
            adapter.getItems().clear();
            adapter.getItems().addAll(posts);
            adapter.notifyDataSetChanged();
        }
    }


    public void addOrder() {

        Map<String, Object> productOrder = new HashMap<>();
        productOrder.put("products", listItems);
        productOrder.put("id_user", user.getUid());
        productOrder.put("Address",Address);
        productOrder.put("status", 0);

        db.collection("orders")
                .add(productOrder)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.v("dataAdded", documentReference.getId());
                        tinydb.remove(user.getUid());

                    }
                }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure( Exception e) {

                Log.v("dataAdded", "failed while adding order");
            }
        });

    }

//    @Override
//    public void onClick(View v) {
//
//
//            if (v.getId() == R.id.order_button) {
//
//            }
//            else
//            {
//                Toast.makeText(getApplicationContext(), "Data not found", Toast.LENGTH_SHORT);
//            }
//        }


}
