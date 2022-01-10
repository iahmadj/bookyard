package com.classroom.bookyard.products;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.classroom.bookyard.Helpers.Singleton;
import com.classroom.bookyard.Helpers.TinyDB;
import com.classroom.bookyard.Model.Product;
import com.classroom.bookyard.R;
import com.classroom.bookyard.UI.HomeActivity;
import com.classroom.bookyard.UI.Seller.EditProduct;
import com.classroom.bookyard.UI.Seller.SellerHome;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.smarteist.autoimageslider.DefaultSliderView;
import com.smarteist.autoimageslider.SliderLayout;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;


public class ProductActivity extends AppCompatActivity {
    private static final String TAG = "Data";
    String fnameItem, descItem, idItem, idSeller, idCat;
    ArrayList imgItem;
    Double priceItem;
    TextView title_item, price_item, description_item;
    CircleImageView profile;
    //private F ancyButton buy;
    private FloatingActionButton buy,fav_now;
    private TinyDB tinydb, tinydbb;
    private boolean clicked;
    TextView seller_name;
    String emails, tels;
    Product productHolder;
    SliderLayout sliderLayout;
    ImageView back_button_product, eproduct;
    FirebaseAuth mAuth;
    private FirebaseUser user = Singleton.getUser();

    private FirebaseFirestore db = Singleton.getDb();


    //BottomSheetBehavior sheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_page);

        fnameItem = (String) getIntent().getSerializableExtra("fnameItem");
        descItem = (String) getIntent().getSerializableExtra("descItem");
        imgItem = (ArrayList) getIntent().getSerializableExtra("imgItem");
        idItem = (String) getIntent().getSerializableExtra("idItem");
        priceItem = (Double) getIntent().getSerializableExtra("priceItem");
        mAuth = FirebaseAuth.getInstance();
        idSeller = (String) getIntent().getSerializableExtra("idSeller");
        idCat = (String) getIntent().getSerializableExtra("idCat");
        tinydb = new TinyDB(this);
        tinydbb = new TinyDB(this);
        back_button_product = findViewById(R.id.back_button_product);
        eproduct = findViewById(R.id.eproduct);
        //cuser();


        Log.v("imgsArray", imgItem.get(0).toString());

        //Setting items
        title_item = findViewById(R.id.title_item);
        description_item = findViewById(R.id.description_item);
        description_item = findViewById(R.id.description_item);
        price_item = findViewById(R.id.price_item);
        //image = findViewById(R.id.image);

        seller_name = findViewById(R.id.seller_name);
        profile = findViewById(R.id.profile_image);



        buy = findViewById(R.id.buy_now);
        fav_now = findViewById(R.id.fav_now);
        getuser();


        if (alreadyExist(idItem, tinydb)) {
            buy.setImageResource(R.drawable.ic_shopping_cart_full);
            clicked = true;
        } else {
            //buy.setBackgroundColor(getResources().getColor(R.color.md_black_1000));
            clicked = false;
        }

        if (alreadyfav(idItem, tinydbb)) {
            fav_now.setImageResource(R.drawable.ic_heart);
            clicked = true;
        } else {
            clicked = false;
        }

        sliderLayout = findViewById(R.id.image);
        //sliderLayout.setIndicatorAnimation(SliderLayout.; //set indicator animation by using SliderLayout.Animations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderLayout.setScrollTimeInSec(3); //set scroll delay in seconds :

        title_item.setText(fnameItem);
        description_item.setText(descItem);
        price_item.setText("Rs. " + priceItem + "/-");

        //Log.v("imgfromArray", imgItem.get(1).toString());

        back_button_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIdS();
            }
        });

         eproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = idItem;
                Intent product = new Intent(ProductActivity.this, EditProduct.class);
                product.putExtra("value1", value);
                startActivity(product);
            }
        });


        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!clicked) {
                    productHolder = new Product(idItem, idSeller, idCat, fnameItem, descItem, (String) imgItem.get(0), priceItem);
                    buy.setImageResource(R.drawable.ic_shopping_cart_full);
                    WriteInShared(productHolder, tinydb);
                    Toast.makeText(ProductActivity.this, "Added", Toast.LENGTH_SHORT).show();
                    buy.setBackgroundColor(getResources().getColor(R.color.md_red_400));
                    clicked = true;
                } else {
                    removeFromArrayList(idItem, tinydb);
                    Toast.makeText(ProductActivity.this, "Removed", Toast.LENGTH_SHORT).show();
                    buy.setBackgroundColor(getResources().getColor(R.color.md_black_1000));
                    clicked = false;
                }
            }
        });


        fav_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!clicked) {
                    productHolder = new Product(idItem, idSeller, idCat, fnameItem, descItem, (String) imgItem.get(0), priceItem);
                    fav_now.setImageResource(R.drawable.ic_heart);
                    WriteInFav(productHolder, tinydbb);
                    Toast.makeText(ProductActivity.this, "Added into Favorites", Toast.LENGTH_SHORT).show();
                    buy.setBackgroundColor(getResources().getColor(R.color.md_red_400));
                    clicked = true;
                } else {
                    removeFromFav(idItem, tinydbb);
                    Toast.makeText(ProductActivity.this, "Removed from Favorites", Toast.LENGTH_SHORT).show();
                    buy.setBackgroundColor(getResources().getColor(R.color.md_black_1000));
                    clicked = false;
                }
            }
        });


        db.collection("Users").document(idSeller)
                .get()
                .addOnCompleteListener(
                        new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete( Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    // Document found in the offline cache
                                    DocumentSnapshot document = task.getResult();
                                    seller_name.setText(document.getString("fname"));
                                    tels = document.getString("Telephone");
                                    emails = document.getString("Email");
                                    Glide.with(ProductActivity.this)
                                            .load(document.getString("image"))
                                            .into(profile);
                                    Log.d(TAG, "Cached document data: " + document.getData());
                                } else {
                                    Log.d(TAG, "Cached get failed: ", task.getException());
                                }
                            }
                        });

        setSliderViews();


        ButterKnife.bind(this);


    }

    public void WriteInShared(Product product, TinyDB tinydb) {
        tinydb = new TinyDB(getApplicationContext());

        ArrayList<Product> postObjects = tinydb.getListObject(user.getUid(), Product.class);

        ArrayList<Product> myfav = new ArrayList<>();

        for (Object objs : postObjects) {
            myfav.add((Product) objs);
        }

        myfav.add(product);
        tinydb.putListObject(user.getUid(), myfav);
    }

    public void WriteInFav(Product product, TinyDB tinydbb) {
        tinydbb = new TinyDB(getApplicationContext());

        ArrayList<Product> postObjects = tinydbb.getListObject(user.getUid(), Product.class);

        ArrayList<Product> fav = new ArrayList<>();

        for (Object objs : postObjects) {
            fav.add((Product) objs);
        }

        fav.add(product);
        tinydbb.putListObject(user.getUid(), fav);
    }



    public void removeFromArrayList(String id_product, TinyDB tinydb) {
        ArrayList<Product> myfavSaved;
        myfavSaved = tinydb.getListObject(user.getUid(), Product.class);
        for (int i = 0; i < myfavSaved.size(); i++) {
            if (myfavSaved.get(i).getId_product().equalsIgnoreCase(id_product)) {
                myfavSaved.remove(i);
                saveFav(myfavSaved, tinydb);
            }
        }
    }

    public void removeFromFav(String id_product, TinyDB tinydbb) {
        ArrayList<Product> favSaved;
        favSaved = tinydbb.getListObject(user.getUid(), Product.class);
        for (int i = 0; i < favSaved.size(); i++) {
            if (favSaved.get(i).getId_product().equalsIgnoreCase(id_product)) {
                favSaved.remove(i);
                saveFavo(favSaved, tinydb);
            }
        }
    }



    public void saveFav(ArrayList<Product> arrayList, TinyDB tinydb) {
        tinydb.putListObject(user.getUid(), arrayList);
    }

    public void saveFavo(ArrayList<Product> arrayList, TinyDB tinydbb) {
        tinydbb.putListObject(user.getUid(), arrayList);
    }


    public boolean alreadyExist(String id_product, TinyDB tinydb) {
        ArrayList<Product> myfavSaved;
        myfavSaved = tinydb.getListObject(user.getUid(), Product.class);
        for (int i = 0; i < myfavSaved.size(); i++) {
            if (myfavSaved.get(i).getId_product().equalsIgnoreCase(id_product)) {
                return true;
            }
        }
        return false;
    }

    public boolean alreadyfav(String id_product, TinyDB tinydbb) {
        ArrayList<Product> favSaved;
        favSaved = tinydbb.getListObject(user.getUid(), Product.class);
        for (int i = 0; i < favSaved.size(); i++) {
            if (favSaved.get(i).getId_product().equalsIgnoreCase(id_product)) {
                return true;
            }
        }
        return false;
    }


    private void setSliderViews() {

        for (int i = 0; i < imgItem.size(); i++) {

            DefaultSliderView sliderView = new DefaultSliderView(this);
            sliderView.setImageUrl(imgItem.get(i).toString());
            sliderView.setImageScaleType(ImageView.ScaleType.FIT_CENTER);
            sliderView.setOnSliderClickListener(new SliderView.OnSliderClickListener() {
                @Override
                public void onSliderClick(SliderView sliderView) {
                    //Toast.makeText(ProductActivity.this, "This is slider " + (finalI + 1), Toast.LENGTH_SHORT).show();
                }
            });

            sliderLayout.addSliderView(sliderView);
        }
    }



    @OnClick(R.id.btn_bottom_sheet_dialog)
    public void showBottomSheetDialog() {

        View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet, null);
        TextView email = view.findViewById(R.id.emailSeller);
        TextView tel = view.findViewById(R.id.TelSeller);
        email.setText(emails);
        tel.setText(tels);
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);
        dialog.show();
    }

    protected void getIdS() {
        db.collection("Users").document(mAuth.getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete( Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if (document.getString("seller").equals("true")) {
                            startActivity(new Intent(ProductActivity.this, SellerHome.class));
                            finish();
                        } else {
                            startActivity(new Intent(ProductActivity.this, HomeActivity.class));
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

    protected void getuser() {
        db.collection("Users").document(mAuth.getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete( Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if (document.getString("seller").equals("true")) {
                            buy.setVisibility(View.INVISIBLE);
                            fav_now.setVisibility(View.GONE);
                        } else {
                            Log.d(TAG, "Customer will see product page", task.getException());
                            eproduct.setVisibility(View.GONE);

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



