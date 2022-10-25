package com.example.joy.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.joy.R;
import com.example.joy.adapter.AdapterCategory;
import com.example.joy.adapter.AdapterCategoryUser;
import com.example.joy.adapter.AdapterProductSeller;
import com.example.joy.adapter.AdapterProductUser;
import com.example.joy.adapter.AdapterReview;
import com.example.joy.model.ModelCategory;
import com.example.joy.model.ModelProduct;
import com.example.joy.model.ModelReview;
import com.example.joy.model.ModelShop;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ShopDetailsActivity extends AppCompatActivity {


    private String shopUid;
    private String myLatitude ,myLongitude ,myPhone, myAddress;
    private String shopName,shopEmail,shopPhone,shopAddress,shopLatitude,shopLongitude;
    public String deliveryFee;
    //declare ui view
    private ImageView shopIv,openCloseTv;
    private TextView shopNameTv,phoneTv,emailTv,deliveryFeeTv,addressTv;
    private RecyclerView categoryGl,favItemRv;
    private RatingBar ratingBar;
    private ImageSlider image_slider;
    private ImageButton shopReview;
    private FloatingActionButton cartBtn;

    private ArrayList<ModelCategory> categoryList;
    private ArrayList<ModelShop> shopsList;
    private AdapterCategoryUser adapterCategoryUser;
    private ArrayList<ModelProduct> productList;
    private AdapterProductUser adapterProductUser;
    private ArrayList<ModelReview> reviewArrayList;// will contain list of all reviews
    private AdapterReview adapterReview;

    private FirebaseAuth firebaseAuth;

    //progress dialog
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_details);

        //init ui views
        shopIv = findViewById(R.id.shopIv);
        shopNameTv = findViewById(R.id.shopNameTv);
        shopReview = findViewById(R.id.shopReview);
        phoneTv = findViewById(R.id.phoneTv);
        //emailTv = findViewById(R.id.emailTv);
        openCloseTv = findViewById(R.id.openCloseTv);
        ratingBar = findViewById(R.id.ratingBar);
        categoryGl = findViewById(R.id.categoryGl);
        cartBtn = findViewById(R.id.cartBtn);
        favItemRv = findViewById(R.id.favItemRv);
        deliveryFeeTv = findViewById(R.id.deliveryFeeTv);
        addressTv = findViewById(R.id.addressTv);
        image_slider = findViewById(R.id.image_slider);
        final List<SlideModel> remoteImages = new ArrayList<>();

        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShopDetailsActivity.this,CartActivity.class);
                intent.putExtra("shopUid",shopUid);
                startActivity(intent);
            }
        });

        shopReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShopDetailsActivity.this, ShopReviewActivity.class);
                intent.putExtra("shopUid",shopUid);
                startActivity(intent);
            }
        });

        //get uid of the shop from intent
        shopUid = getIntent().getStringExtra("shopUid");
        firebaseAuth = FirebaseAuth.getInstance();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUid).child("offers")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds:dataSnapshot.getChildren())
                        {
                            remoteImages.add(new SlideModel(ds.child("offerIcon").getValue().toString(),ds.child("offerTitle").getValue().toString(), ScaleTypes.FIT));

                            image_slider.setImageList(remoteImages,ScaleTypes.FIT);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        phoneTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialPhone();
            }
        });

        addressTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap();
            }
        });


        loadMyInfo();
        loadShopDetails();
        loadAllCategory();
        loadAllFavItem();
        loadReviews();

    }

    private float ratingSum = 0;
    private void loadReviews() {
        //init list
        reviewArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUid).child("Ratings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //clear list before adding data into it
                reviewArrayList.clear();
                ratingSum = 0;
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    float rating = Float.parseFloat(""+ds.child("ratings").getValue());//e.g. 4.3
                    ratingSum = ratingSum + rating; //for avg rating, add(addition of) all ratings,later will divide it by number of reviews
                    ModelReview modelReview = ds.getValue(ModelReview.class);
                    reviewArrayList.add(modelReview);

                }
                //setup adapter
                //adapterReview = new AdapterReview(ShopReviewActivity.this,reviewArrayList);
                // set to recyclerview
                //reviewsRv.setAdapter(adapterReview);
                long numberOfReviews = dataSnapshot.getChildrenCount();
                float avgRating = ratingSum/numberOfReviews;
                //ratingsTv.setText(String.format("%.2f" , avgRating) + " [" +numberOfReviews+"]");// e.g 4.7  [10]
                ratingBar.setRating(avgRating);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void openMap() {
        //saddr means source address
        //daddr means destination address
        String address = "http://maps.google.com/maps?saddr=" + myLatitude + "," + myLongitude + "&daddr=" + shopLatitude + "," + shopLongitude;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(address));
        startActivity(intent);
    }

    private void dialPhone() {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+Uri.encode(shopPhone))));
        Toast.makeText(this, ""+shopPhone, Toast.LENGTH_SHORT).show();
    }

    private void loadMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener(){
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                        for(DataSnapshot ds: dataSnapshot.getChildren()){
                            //get user data
                            String name = ""+ds.child("name").getValue();
                            String email = ""+ds.child("email").getValue();
                            myPhone = ""+ds.child("phone").getValue();
                            String profileImage = ""+ds.child("profileImage").getValue();
                            String accountType = ""+ds.child("accountType").getValue();
                            String city = ""+ds.child("city").getValue();
                            myLatitude = ""+ds.child("latitude").getValue();
                            myLongitude = ""+ds.child("longitude").getValue();
                            myAddress = ""+ds.child("address").getValue();

                            //Toast.makeText(ShopDetailsActivity.this, ""+myLatitude+""+myLongitude, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError){

                    }
                });
    }

    private void loadShopDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUid).addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //get shop data
                String name = ""+dataSnapshot.child("name").getValue();
                shopName = ""+dataSnapshot.child("shopName").getValue();
                shopEmail = ""+dataSnapshot.child("email").getValue();
                shopPhone = ""+dataSnapshot.child("phone").getValue();
                shopAddress = ""+dataSnapshot.child("address").getValue();
                shopLatitude = ""+dataSnapshot.child("latitude").getValue();
                shopLongitude = ""+dataSnapshot.child("longitude").getValue();
                deliveryFee = ""+dataSnapshot.child("deliveryFee").getValue();
                String profileImage = ""+dataSnapshot.child("profileImage").getValue();
                String shopOpen = ""+dataSnapshot.child("shopOpen").getValue();

                //set data
                shopNameTv.setText(shopName);
                //emailTv.setText(shopEmail);
                deliveryFeeTv.setText("Delivery Fee: ₹"+deliveryFee);
                addressTv.setText(shopAddress);
                phoneTv.setText(shopPhone);

                if (shopOpen.equals("true")){
                    openCloseTv.setImageResource(R.drawable.open);
                }
                else{
                    openCloseTv.setImageResource(R.drawable.close1);
                }
                try {
                    Picasso.get().load(profileImage).into(shopIv);
                }
                catch (Exception e){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){

            }
        });
    }


    private void loadAllFavItem() {
        productList = new ArrayList<>();
        //get all products
        //String status = "true";
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(shopUid).child("favourite")
                .orderByChild("favourite").equalTo("true")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        productList.clear();
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                            productList.add(modelProduct);
                        }
                        //setup adapter
                        adapterProductUser = new AdapterProductUser(ShopDetailsActivity.this, productList);
                        //set adapter
                        favItemRv.setHasFixedSize(true);
                        favItemRv.setAdapter(adapterProductUser);
                        //favItemRv.setLayoutManager(new GridLayoutManager(ShopDetailsActivity.this,2,GridLayoutManager.VERTICAL,false));
                        favItemRv.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    private void loadAllCategory() {
        categoryList = new ArrayList<>();
        //get all products
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(shopUid).child("category")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        categoryList.clear();
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            ModelCategory modelCategory = ds.getValue(ModelCategory.class);
                            categoryList.add(modelCategory);
                        }
                        //setup adapter
                        adapterCategoryUser = new AdapterCategoryUser(ShopDetailsActivity.this, categoryList, shopsList);
                        //set adapter

                        categoryGl.setLayoutManager(new GridLayoutManager(ShopDetailsActivity.this,4,GridLayoutManager.VERTICAL,false));
                        categoryGl.setAdapter(adapterCategoryUser);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}