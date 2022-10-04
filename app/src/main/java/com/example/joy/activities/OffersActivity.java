package com.example.joy.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.joy.R;
import com.example.joy.adapter.AdapterCategory;
import com.example.joy.adapter.AdapterOfferSeller;
import com.example.joy.model.ModelCategory;
import com.example.joy.model.ModelOffer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

public class OffersActivity extends AppCompatActivity {

    private ImageButton homeBtn;
    private RecyclerView offersRv;
    private FloatingActionButton addOffersBtn;
    private ImageView offerIv;
    private EditText titleEt;

    private ArrayList<ModelOffer> offerList;
    private AdapterOfferSeller adapterOfferSeller;

    private static final int STORAGE_REQUEST_CODE = 300;
    //image pick constants
    private static final int IMAGE_PICK_GALLERY_CODE = 400;

    private String[] storagePermissions;

    private Uri image_uri;
    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);


        homeBtn = findViewById(R.id.homeBtn);
        offersRv = findViewById(R.id.offersRv);
        addOffersBtn = findViewById(R.id.addOffersBtn);

        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        firebaseAuth = FirebaseAuth.getInstance();


        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OffersActivity.this, MainSellerActivity.class);
                startActivity(intent);
            }
        });

        addOffersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        loadAllOffers();

    }

    private void loadAllOffers() {

        offerList = new ArrayList<>();
        //get all products
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("offers")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        offerList.clear();
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            ModelOffer modelOffer = ds.getValue(ModelOffer.class);
                            offerList.add(modelOffer);
                        }
                        //setup adapter
                        adapterOfferSeller = new AdapterOfferSeller(OffersActivity.this, offerList);
                        //set adapter

                        offersRv.setLayoutManager(new LinearLayoutManager(OffersActivity.this, LinearLayoutManager.VERTICAL,false));
                        offersRv.setAdapter(adapterOfferSeller);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void showDialog() {
        
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(OffersActivity.this);
        alertDialog.setTitle("Add new offer");
        
        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_offers,null);

        offerIv = add_menu_layout.findViewById(R.id.offerIv);
        titleEt = add_menu_layout.findViewById(R.id.titleEt);

        offerIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show dialog to pick image
                showImagePickDialog();
            }
        });

        alertDialog.setView(add_menu_layout);
        //set button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {

                dialogInterface.dismiss();
                uploadImage();
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {

                dialogInterface.dismiss();
            }
        });
        alertDialog.show();

    }

    private String offerTitle;
    private void uploadImage() {

        offerTitle = titleEt.getText().toString().trim();

        if (TextUtils.isEmpty(offerTitle)){
           offerTitle = "";
        }

        if(image_uri !=null){
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            final String timestamp = ""+System.currentTimeMillis();

            //upload with image
            String filePathAndName = "Offer_Image/" + "" + timestamp;
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            final String timestamp = ""+System.currentTimeMillis();
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            Uri downloadImageUri = uriTask.getResult();
                            //Toast.makeText(AddProductActivity.this,"a "+ downloadImageUri, Toast.LENGTH_SHORT).show();
                            if (uriTask.isSuccessful()){
                                //upload with image
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("offerId", ""+timestamp);
                                hashMap.put("offerIcon", ""+downloadImageUri);
                                hashMap.put("offerTitle", ""+offerTitle);
                                hashMap.put("timestamp", ""+timestamp);
                                hashMap.put("uid", ""+firebaseAuth.getUid());

                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
                                reference.child(firebaseAuth.getUid()).child("offers").child(timestamp).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //added to db
                                                mDialog.dismiss();
                                                Toast.makeText(OffersActivity.this, "Offer added...", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //failed add to db
                                                mDialog.dismiss();
                                                Toast.makeText(OffersActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(OffersActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }

    private void showImagePickDialog() {
        //gallery clicked
        if (checkStoragePermission()){
            //permission granted
            pickFromGallery();
        }
        else{
            //permission not granted, request
            requestStoragePermission();
        }

    }

    private void pickFromGallery() {
        //intent to pick image from gallery
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 400);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);
        return result;//return true/false
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    //handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted){
                        pickFromGallery();
                    }
                    else {
                        Toast.makeText(this, "Storage permission is Required...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //handle image result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        // Toast.makeText(AddProductActivity.this," " + requestCode + " "+resultCode + " "+ data, Toast.LENGTH_SHORT).show();

        if (resultCode == RESULT_OK){

            if (requestCode == 400){
                //save pick image
                image_uri = data.getData();
                // Toast.makeText(AddProductActivity.this," 400" + image_uri, Toast.LENGTH_SHORT).show();
                //set image
                offerIv.setImageURI(image_uri);
            }
            else if (requestCode == 500){
                //offerIv.setImageURI(image_uri);
                //Toast.makeText(AddProductActivity.this," 500" + image_uri, Toast.LENGTH_SHORT).show();
            }

        }

    }


}