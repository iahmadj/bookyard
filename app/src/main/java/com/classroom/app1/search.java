package com.classroom.app1;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
//import android.widget.Toolbar;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.classroom.app1.Helpers.DataStatus;
import com.classroom.app1.Helpers.Singleton;
import com.classroom.app1.Model.Product;
import com.classroom.app1.UI.ClickListeners.RecyclerViewClickListenerProduct;
import com.classroom.app1.UI.HomeActivity;
import com.classroom.app1.UI.Seller.SellersOrders;
import com.classroom.app1.products.ProductActivity;
import com.classroom.app1.products.ProductAdapter;
import com.classroom.app1.profil.ProfilActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class search extends AppCompatActivity {

    private static final String TAG = "data";
    private RecyclerView recyclerView_products;
    private ProductAdapter rec_adapter;
    private ArrayList<Product> productsList = new ArrayList<>();
    private FirebaseFirestore db = Singleton.getDb();
    EditText inputSearch;
    ImageView search,back_button_search;
    String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        FirebaseApp.initializeApp(this);
        inputSearch = findViewById(R.id.inputSearch);
        back_button_search = findViewById(R.id.back_button_search);
        com.google.android.material.appbar.MaterialToolbar toolbar = findViewById(R.id.toolbar);
        search = findViewById(R.id.search);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);

        recyclerView_products = findViewById(R.id.recycler_view);
        init();



        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data = inputSearch.getText().toString();
                fetchRec();
            }
        });

        back_button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(search.this, HomeActivity.class));
            }
        });

    }


    private void fetchRec() {


        getRec(new DataStatus() {
            @Override
            public void onSuccess(ArrayList products) {

                if (rec_adapter == null) {
                    rec_adapter = new ProductAdapter(productsList, search.this,
                            new RecyclerViewClickListenerProduct() {
                                @Override
                                public void onClick(View view, Product product) {
                                    Intent productPage = new Intent(search.this, ProductActivity.class);
                                    productPage.putExtra("fnameItem", product.getfname());
                                    productPage.putExtra("descItem", product.getDescription());
                                    productPage.putExtra("idItem", product.getId_product());
                                    productPage.putExtra("imgItem", product.getImg());
                                    productPage.putExtra("priceItem", product.getPrice());
                                    productPage.putExtra("idSeller", product.getId_seller());
                                    productPage.putExtra("idCat", product.getId_cat());
                                    startActivity(productPage);
                                }
                            });
                    recyclerView_products.setAdapter(rec_adapter);
                } else {
                    rec_adapter.getItems().clear();
                    rec_adapter.getItems().addAll(productsList);
                    rec_adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(String e) {
                Toast.makeText(search.this, e, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getRec(final DataStatus callback) {

        db.collection("products").orderBy("fname").startAt(data.trim()).endAt(data + '\uf8ff')
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete( Task<QuerySnapshot> task) {


                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Log.v("dataFireSec", document.getData().toString());

                                ArrayList<String> imgs = new ArrayList<>();

                                Map<String, Object> myMap = document.getData();
                                for (Map.Entry<String, Object> entry : myMap.entrySet()) {
                                    if (entry.getKey().equals("img")) {
                                        for (Object s : (ArrayList) entry.getValue()) {
                                            imgs.add((String) s);
                                        }

                                    }
                                }

                                Product product = new Product(document.getId(),
                                        (String) myMap.get("id_seller"),
                                        (String) myMap.get("id_cat"),
                                        (String) myMap.get("fname"),
                                        (String) myMap.get("description"),
                                        imgs,
                                        (Double) myMap.get("price"));

                                productsList.add(product);
                            }
                            callback.onSuccess(productsList);

                        } else {
                            callback.onError("Error in data");
                            Log.w("error", "Error getting documents.", task.getException());
                        }

                    }
                });

    }


    private void init() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.HORIZONTAL, false);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);

        recyclerView_products.setLayoutManager(gridLayoutManager);
        recyclerView_products.setNestedScrollingEnabled(false);
    }


}
