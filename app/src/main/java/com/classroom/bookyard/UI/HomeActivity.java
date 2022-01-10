package com.classroom.bookyard.UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
//import android.widget.Toolbar;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.classroom.bookyard.Favorite;
import com.classroom.bookyard.Helpers.DataStatus;
import com.classroom.bookyard.Helpers.Singleton;
import com.classroom.bookyard.Model.Categories;
import com.classroom.bookyard.Model.Product;
import com.classroom.bookyard.R;
import com.classroom.bookyard.UI.Adapters.CategoriesAdapter;
import com.classroom.bookyard.UI.ClickListeners.RecyclerViewClickListener;
import com.classroom.bookyard.UI.ClickListeners.RecyclerViewClickListenerProduct;
import com.classroom.bookyard.cart.CartActivity;
import com.classroom.bookyard.products.ProductActivity;
import com.classroom.bookyard.products.ProductAdapter;
import com.classroom.bookyard.products.ProductsListActivity;
import com.classroom.bookyard.profil.ProfilActivity;
import com.classroom.bookyard.search;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "data";
    private RecyclerView recyclerView, recyclerView_products, rec_rv;
    private CategoriesAdapter adapter;
    private ProductAdapter products_adapter, rec_adapter;
    private ArrayList<Categories> catList2 = new ArrayList<>();
    private ArrayList<Product> productsList = new ArrayList<>();
    private ArrayList<Product> productsListTest = new ArrayList<>();

    private FirebaseFirestore db = Singleton.getDb();
    FirebaseAuth mAuth;
    private String favor;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();


    private RecyclerView.LayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        FirebaseApp.initializeApp(this);

        com.google.android.material.appbar.MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.categories_rc);
        recyclerView_products = findViewById(R.id.products_rc);
        rec_rv = findViewById(R.id.rec_rv);

        BottomNavigationView mBottomNav = findViewById(R.id.navigation_home);
        mBottomNav.setSelectedItemId(R.id.home_button);



        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected( MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home_button:
                        Intent home = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(home);
                        break;
                    case R.id.navigation_cart:
                        Intent cart = new Intent(getApplicationContext(), CartActivity.class);
                        startActivity(cart);
                        break;
                    case R.id.search:
                        Intent search = new Intent(getApplicationContext(), search.class);
                        startActivity(search);
                        break;
                    case R.id.fav:
                        Intent fav = new Intent(getApplicationContext(), Favorite.class);
                        startActivity(fav);
                        break;
                    case R.id.profil_button:
                        Intent profil = new Intent(getApplicationContext(), ProfilActivity.class);
                        startActivity(profil);
                        break;
                }
                return true;
            }
        });

        db.collection("Users").document(uid)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete( Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        favor = document.getString("f_cat");
                        fetchRec();

                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });



        DocumentReference query = db.collection("Users").document(uid);
        query.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        favor = document.getString("f_cat");
                        Log.i("entered into record and value of favu is ", favor);
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


        init();
        fetchData();
        fetchProducts();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    public void setSupportActionBar(Toolbar toolbar) {
    }

    private void fetchProducts() {
        getProducts(new DataStatus() {
            @Override
            public void onSuccess(ArrayList products) {

                if (products_adapter == null) {
                    products_adapter = new ProductAdapter(productsList, HomeActivity.this, new RecyclerViewClickListenerProduct() {
                                @Override
                                public void onClick(View view, Product product) {
                                    Intent productPage = new Intent(HomeActivity.this, ProductActivity.class);
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
                    recyclerView_products.setAdapter(products_adapter);
                } else {
                    products_adapter.getItems().clear();
                    products_adapter.getItems().addAll(productsList);
                    products_adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(String e) {
                Toast.makeText(HomeActivity.this, e, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void fetchData() {
        getCategories(new DataStatus() {
            @Override
            public void onSuccess(ArrayList categories) {
                if (adapter == null) {
                    adapter = new CategoriesAdapter(categories, HomeActivity.this,
                            new RecyclerViewClickListener() {
                                @Override
                                public void onClick(View view, Categories categories) {
                                    Intent productPage = new Intent(HomeActivity.this, ProductsListActivity.class);
                                    productPage.putExtra("catName", categories.getId_cat());
                                    productPage.putExtra("isSeller", false);
                                    startActivity(productPage);
                                    //overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);

                                }
                            });
                    recyclerView.setAdapter(adapter);
                } else {
                    adapter.getItems().clear();
                    adapter.getItems().addAll(categories);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(String e) {
                Toast.makeText(HomeActivity.this, e, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getCategories(final DataStatus callback) {

        //final ArrayList<Categories> catList = new ArrayList<>();

        db.collection("categories")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete( Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Map myMap = document.getData();
                                Log.v("dataFire", (String) myMap.toString());

                                // List of categories here
                                Categories categorie = new Categories(document.getId(), (String) myMap.get("name"), (String) myMap.get("icon"), (String) myMap.get("color"));
                                catList2.add(categorie);
                            }
                            callback.onSuccess(catList2);

                        } else {
                            callback.onError("Error in data");
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }

                    }
                });
    }

    private void getProducts(final DataStatus callback) {

        db.collection("products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete( Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.v("dataFireSec",document.getId()+" "+document.get("img"));


                                ArrayList<String> imgs = new ArrayList<>();
                                Map<String, Object> myMap = document.getData();
                                for (Map.Entry<String, Object> entry : myMap.entrySet()) {
                                    if (entry.getKey().equals("img")) {
                                        for (Object s : (ArrayList) entry.getValue()) {
                                            imgs.add((String) s);
                                        }
                                        Log.v("TagImg", entry.getValue().toString());

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
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }

                    }
                });
    }


    private void fetchRec() {


        getRec(new DataStatus() {
            @Override
            public void onSuccess(ArrayList products) {

                if (rec_adapter == null) {
                    rec_adapter = new ProductAdapter(productsListTest, HomeActivity.this,
                            new RecyclerViewClickListenerProduct() {
                                @Override
                                public void onClick(View view, Product product) {
                                    Intent productPage = new Intent(HomeActivity.this, ProductActivity.class);
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
                    rec_rv.setAdapter(rec_adapter);
                } else {
                    rec_adapter.getItems().clear();
                    rec_adapter.getItems().addAll(productsListTest);
                    rec_adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(String e) {
                Toast.makeText(HomeActivity.this, e, Toast.LENGTH_SHORT).show();
            }
        });
    }


    String val=favor;



    private void getRec(final DataStatus callback) {

        db.collection("products").whereEqualTo("id_cat",favor)
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

                                productsListTest.add(product);
                            }
                            callback.onSuccess(productsListTest);

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
        recyclerView.setLayoutManager(linearLayoutManager);
        //rec_rv.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false));


        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rec_rv.setLayoutManager(mLayoutManager);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView_products.setLayoutManager(gridLayoutManager);
        recyclerView_products.setNestedScrollingEnabled(false);

    }





}
