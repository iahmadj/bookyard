package com.classroom.app1.profil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;


import com.bumptech.glide.Glide;
import com.classroom.app1.Helpers.BaseActivity;
import com.classroom.app1.Helpers.DataStatusImage;
import com.classroom.app1.R;
import com.classroom.app1.UI.HomeActivity;
import com.classroom.app1.UI.Seller.SellerHome;
import com.classroom.app1.UI.Seller.SellersOrders;
import com.classroom.app1.orders.UsersOrders;
import com.classroom.app1.login.Login;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "Profile";
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    private Uri filePath;
    StorageReference storageReference;
    FirebaseStorage storage;
    private CircleImageView profilImg;
    private static final int PICK_IMAGE_REQUEST = 1;
    ImageView back_button_profile, settingss;
    Button logout_button,my_orders;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        back_button_profile = findViewById(R.id.back_button_profile);
        logout_button = findViewById(R.id.logout_button);
        my_orders = findViewById(R.id.my_orders);
        profilImg = findViewById(R.id.ImgUserV);
        settingss = findViewById(R.id.settingss);
        profilImg.setOnClickListener(this);
        getProfile();

        back_button_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIdS();
            }
        });
        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Toast.makeText(ProfilActivity.this, "Logout Successfully!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        my_orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getid();
            }
        });

        settingss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditProfile.class);
                startActivity(intent);
            }
        });
    }

    private void setSupportActionBar(Toolbar toolbar) {
    }


    protected void getProfile() {
        showProgressDialog();

        db.collection("Users").document(mAuth.getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete( Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        setProfil(document.getString("fname"), document.getString("lastname"), document.getString("Telephone"), document.getString("Email"), document.getString("Address"));
                        if (!document.getString("image").isEmpty()){
                            bindModel(document.getString("image"));
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                    hideProgressDialog();
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
            pickFromGallery();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();

            uploadImage(new DataStatusImage() {
                @Override
                public void onSuccess(final Uri uri) {
                    Glide.with(ProfilActivity.this).load(uri).into(profilImg);
                    Map<String, Object> data = new HashMap<>();
                    data.put("image", uri.toString());

                    db.collection("Users").document(mAuth.getUid())
                            .set(data, SetOptions.merge());
                    Toast.makeText(ProfilActivity.this, "uploaded", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String e) {

                }
            });
        }


    }

    void bindModel(String url) {
        CircleImageView img = findViewById(R.id.ImgUserV);
        Glide
                .with(this.getApplicationContext())
                .load(url)
                .into(img);
    }

    private void uploadImage(final DataStatusImage statusImageCallback) {

        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            final StorageReference ref = storageReference.child("images/" + mAuth.getUid() + "/" + UUID.randomUUID().toString());


            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            //statusImageCallback.onSuccess("images/"+user.getUid()+"/" + UUID.randomUUID().toString());
                            Log.v("getDownloadUrl", ref.getDownloadUrl().toString());

                            //final StorageReference refs = ref.child("images/mountains.jpg");
                            UploadTask uploadTask = ref.putFile(filePath);

                            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then( Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if (!task.isSuccessful()) {
                                        throw task.getException();
                                    }

                                    // Continue with the task to get the download URL
                                    Log.v("getDownloadUrl", ref.getDownloadUrl().toString());
                                    return ref.getDownloadUrl();

                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete( Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Uri downloadUri = task.getResult();
                                        statusImageCallback.onSuccess(downloadUri);
                                    } else {
                                        Toast.makeText(ProfilActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                            //Toast.makeText(AddProduct.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure( Exception e) {
                            progressDialog.dismiss();
                            statusImageCallback.onError("Error");
                            Toast.makeText(ProfilActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploading " + (int) progress + "%");
                        }
                    });
        }
    }


    public void setProfil(String fname, String lastname, String tel, String mail, String Address) {
        TextView Fullname, Fullprename;
        EditText name, prename, phone, email, city;
        Fullname = findViewById(R.id.tv_prename);
        Fullname.setText(lastname);
        Fullprename = findViewById(R.id.tv_name);
        Fullprename.setText(fname);
        name = findViewById(R.id.firstName);
        name.setText(fname);
        prename = findViewById(R.id.lastName);
        prename.setText(lastname);
        phone = findViewById(R.id.phone);
        phone.setText(tel);
        email = findViewById(R.id.email);
        email.setText(mail);
        city = findViewById(R.id.address);
        city.setText(Address);
    }

    private void pickFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
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
                            startActivity(new Intent(ProfilActivity.this, SellerHome.class));
                            finish();
                        } else {
                            startActivity(new Intent(ProfilActivity.this, HomeActivity.class));
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

    protected void getid() {
        showProgressDialog();
        db.collection("Users").document(mAuth.getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete( Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if (document.getString("seller").equals("true")) {
                            startActivity(new Intent(ProfilActivity.this, SellersOrders.class));
                            finish();
                        } else {
                            startActivity(new Intent(ProfilActivity.this, UsersOrders.class));
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

