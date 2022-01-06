package com.classroom.bookyard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.classroom.bookyard.Helpers.Singleton;
import com.classroom.bookyard.Helpers.TinyDB;
import com.classroom.bookyard.Model.Product;
import com.classroom.bookyard.R;
import com.classroom.bookyard.UI.HomeActivity;
import com.classroom.bookyard.cart.CartProductAdapter;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;

public class Favorite extends AppCompatActivity {

    private CartProductAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<Product> listItems = new ArrayList<>();
    private FirebaseFirestore db = Singleton.getDb();
    private TinyDB tinydbb;
    private FirebaseUser user = Singleton.getUser();
    com.google.android.material.button.MaterialButton delete_all;
    ImageView back_button_cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);

        recyclerView = findViewById(R.id.fav_rc);
        delete_all = findViewById(R.id.delete_fav);
        back_button_cart = findViewById(R.id.back_button_cart);
        LinearLayoutManager layoutManager = new LinearLayoutManager(Favorite.this,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        fetchRemoteData();

        delete_all.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tinydbb.remove((user.getUid()));
                Toast.makeText(Favorite.this, "Favorite is Cleared",
                        Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Favorite.this, HomeActivity.class));
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
    }

    private void fetchRemoteData() {
        tinydbb = new TinyDB(Favorite.this);
        ArrayList<Product> productsOnFav = tinydbb.getListObject(user.getUid(), Product.class);
        for (int i = 0; i < productsOnFav.size(); i++) {
            Product item = new Product(productsOnFav.get(i).getId_product(),
                    productsOnFav.get(i).getId_seller(),
                    productsOnFav.get(i).getId_cat(),
                    productsOnFav.get(i).getfname(),
                    productsOnFav.get(i).getDescription(),
                    productsOnFav.get(i).getImg_product(),
                    productsOnFav.get(i).getPrice()
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
        if (adapter == null) {
            adapter = new CartProductAdapter(posts, Favorite.this);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.getItems().clear();
            adapter.getItems().addAll(posts);
            adapter.notifyDataSetChanged();
        }
    }

}
