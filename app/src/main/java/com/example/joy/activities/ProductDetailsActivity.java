package com.example.joy.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.joy.R;
import com.example.joy.model.ModelProduct;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class ProductDetailsActivity extends AppCompatActivity {

    private TextView product_name,productDes,
            productIns,dTime,originalPriceTv,
            discountedPriceTv,quantityTv;
    private ImageView img_product;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private FloatingActionButton btnCart,btnRating;
    private ImageButton decrementBtn,incrementBtn;
    private RatingBar ratingBar;
    private Toolbar toolbar;

    private FirebaseAuth firebaseAuth;

    private String shopUid,pId,cId,productTitle,icon,discountAvailable,originalPrice,discountPrice;

    private double cost = 0, finalCost = 0;
    private  int quantity = 0;

    public ArrayList<ModelProduct> productList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        product_name = findViewById(R.id.product_name);
        quantityTv = findViewById(R.id.quantityTv);
        productDes = findViewById(R.id.productDescription);
        productIns = findViewById(R.id.productInstruction);
        dTime = findViewById(R.id.deliveryTime);
        originalPriceTv = findViewById(R.id.originalPriceTv);
        discountedPriceTv = findViewById(R.id.discountedPriceTv);
        img_product = findViewById(R.id.img_product);
        toolbar = findViewById(R.id.toolbar);
        decrementBtn = findViewById(R.id.decrementBtn);
        incrementBtn = findViewById(R.id.incrementBtn);
        btnCart = findViewById(R.id.btnCart);
        btnRating = findViewById(R.id.btnRating);
        ratingBar = findViewById(R.id.ratingBar);


        firebaseAuth = FirebaseAuth.getInstance();

        shopUid = getIntent().getStringExtra("shopUid");
        pId = getIntent().getStringExtra("pId");
        productTitle = getIntent().getStringExtra("title");
        icon = getIntent().getStringExtra("image");
        cId = getIntent().getStringExtra("cId");
        discountAvailable = getIntent().getStringExtra("disAvl");
        discountPrice = getIntent().getStringExtra("DisPrice");
        originalPrice = getIntent().getStringExtra("OrgPrice");


                            final String price;
                            if(discountAvailable.equals("true")){
                                price = discountPrice;
                                originalPriceTv.setPaintFlags(originalPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            }
                            else {
                                price = originalPrice;
                                discountedPriceTv.setVisibility(View.GONE);
                            }

                            cost = Double.parseDouble(price.replaceAll("₹",""));
                            finalCost = Double.parseDouble(price.replaceAll("₹",""));
                            quantity = 1;

                            //increment quantity of the product
                            incrementBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    finalCost = finalCost + cost;
                                    quantity++;

                                    quantityTv.setText(""+quantity);
                                }
                            });

                            //decrement quantity of the product, only if quantity > 1
                            decrementBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(quantity>1){
                                        finalCost = finalCost - cost;
                                        quantity --;

                                        quantityTv.setText(""+quantity);

                                    }
                                }
                            });

                            String title = productTitle;
                            String priceEach = price;
                            String image = icon;

                            btnCart.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    addToCart(pId,title,priceEach, (int) finalCost,quantity,image);
                                    //Toast.makeText(ProductDetailsActivity.this, "    "+pId+"     "+priceEach+"       "+ quantity +"     "+image, Toast.LENGTH_SHORT).show();
                                }
                            });


//                                    String productId = ""+ds.child("productId").getValue();
//                                    String productTitle = ""+ds.child("productTitle").getValue();
//                                    String productIcon = ""+ds.child("productIcon").getValue();
//                                    String originalPrice = ""+ds.child("originalPrice").getValue();
//                                    final String discountAvailable = ""+ds.child("discountAvailable").getValue();
//                                    String discountPrice = ""+ds.child("discountPrice").getValue();


//                                    final String price;
//                                    if(discountAvailable.equals("true")){
//                                        price = discountPrice;
//                                        originalPriceTv.setPaintFlags(originalPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//                                    }
//                                    else {
//                                        price = originalPrice;
//                                        discountedPriceTv.setVisibility(View.GONE);
//                                    }
//
//                                    cost = Double.parseDouble(price.replaceAll("₹",""));
//                                    finalCost = Double.parseDouble(price.replaceAll("₹",""));
//                                    quantity = 1;
//
//
//                                    String title = productTitle;
//                                    String priceEach = price;
//                                    String image = productIcon;
//
//                                    addToCart(productId,title,priceEach, (int) finalCost,quantity,image);
//                                    //Toast.makeText(ProductDetailsActivity.this, "    "+finalCost+"     "+priceEach+"       "+ quantity +"     "+image, Toast.LENGTH_SHORT).show();
//
//
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                            }
//                        });
//


        loadProductInfo();

    }

    private int itemId = 1;
    private void addToCart(String productId, String title, String priceEach, int totalPrice, int quantity, String image) {
    itemId++;

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", ""+itemId);
        hashMap.put("pId", ""+productId);
        hashMap.put("cost", ""+priceEach);
        hashMap.put("name", ""+title);
        hashMap.put("price", ""+totalPrice);
        hashMap.put("quantity", ""+quantity);
        hashMap.put("image", ""+image);
        hashMap.put("shopUid", ""+shopUid);
        hashMap.put("uid", ""+firebaseAuth.getUid());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.child(firebaseAuth.getUid()).child("items").child(pId).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //added to db
                        Toast.makeText(ProductDetailsActivity.this, "Item added...", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed add to db
                        Toast.makeText(ProductDetailsActivity.this, "failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


//        EasyDB easyDB = EasyDB.init(ProductDetailsActivity.this,"ITEM_DB")
//                .setTableName("ITEMS_TABLE")
//                .addColumn(new Column("Item_Id", new String[]{"text","unique"}))
//                .addColumn(new Column("Item_PID", new String[]{"text","not null"}))
//                .addColumn(new Column("Item_Name", new String[]{"text","not null"}))
//                .addColumn(new Column("Item_Price_Each", new String[]{"text","not null"}))
//                .addColumn(new Column("Item_Price", new String[]{"text","not null"}))
//                .addColumn(new Column("Item_Quantity", new String[]{"text","not null"}))
//                .addColumn(new Column("Item_Image", new String[]{"text","not null"}))
//                .doneTableColumn();
//
//        Boolean b = easyDB.addData("Item_Id",itemId)
//                .addData("Item_PID",productId)
//                .addData("Item_Name",title)
//                .addData("Item_Price_Each",priceEach)
//                .addData("Item_Price",totalPrice)
//                .addData("Item_Quantity",quantity)
//                .addData("Item_Image",image)
//                .doneDataAdding();
//
//        Toast.makeText(ProductDetailsActivity.this, "Added to cart..."+itemId, Toast.LENGTH_SHORT).show();


        //Toast.makeText(ProductDetailsActivity.this, "    "+title+"     "+priceEach+"    "+totalPrice+"    "+ quantity +"     "+image, Toast.LENGTH_SHORT).show();

                        }




    private void loadProductInfo() {
        //load user info, and set to views
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(shopUid).child("category").child(cId).child("products").orderByChild("productId").equalTo(pId)
                .addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            String productId = ""+ds.child("productId").getValue();
                            String categoryId = ""+ds.child("categoryId").getValue();
                            String productTitle = ""+ds.child("productTitle").getValue();
                            String productDescription = ""+ds.child("productDescription").getValue();
                            String productInstruction = ""+ds.child("productInstruction").getValue();
                            String productIcon = ""+ds.child("productIcon").getValue();
                            String originalPrice = ""+ds.child("originalPrice").getValue();
                            final String discountAvailable = ""+ds.child("discountAvailable").getValue();
                            String discountPrice = ""+ds.child("discountPrice").getValue();
                            String discountNote = ""+ds.child("discountNote").getValue();
                            String deliveryTime = ""+ds.child("deliveryTime").getValue();
                            String uid = ""+ds.child("uid").getValue();
                            String timestamp = ""+ds.child("timestamp").getValue();

                            toolbar.setTitle(productTitle);

                            collapsingToolbarLayout = findViewById(R.id.collapsing);
                            collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
                            collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

                            product_name.setText(productTitle);
                            productDes.setText(productDescription);
                            productIns.setText(productInstruction);
                            dTime.setText("This product will be shipped within "+deliveryTime);
                            discountedPriceTv.setText("₹"+discountPrice);
                            originalPriceTv.setText("₹"+originalPrice);

                            String discountAvl = discountAvailable;

                            if(discountAvailable.equals("true")){
                                discountedPriceTv.setVisibility(View.VISIBLE);
                                originalPriceTv.setPaintFlags(originalPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            }
                            else {
                                discountedPriceTv.setVisibility(View.GONE);
                            }

                            try {
                                Picasso.get().load(productIcon).placeholder(R.drawable.splashlogo).into(img_product);
                            }
                            catch (Exception e){
                                img_product.setImageResource(R.drawable.splashlogo);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


//        //load user info, and set to views
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
//        reference.child(shopUid).child("category").child(cId).child("products").orderByChild("productId").equalTo(pId)
//                .addValueEventListener(new ValueEventListener() {
//                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        for (DataSnapshot ds: dataSnapshot.getChildren()){
//                            String productId = ""+ds.child("productId").getValue();
//                            String productTitle = ""+ds.child("productTitle").getValue();
//                            String productIcon = ""+ds.child("productIcon").getValue();
//                            String originalPrice = ""+ds.child("originalPrice").getValue();
//                            final String discountAvailable = ""+ds.child("discountAvailable").getValue();
//                            String discountPrice = ""+ds.child("discountPrice").getValue();
//
//
//                            final String price;
//                            if(discountAvailable.equals("true")){
//                                price = discountPrice;
//                                originalPriceTv.setPaintFlags(originalPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//                            }
//                            else {
//                                price = originalPrice;
//                                discountedPriceTv.setVisibility(View.GONE);
//                            }
//
//                            cost = Double.parseDouble(price.replaceAll("₹",""));
//                            finalCost = Double.parseDouble(price.replaceAll("₹",""));
//                            quantity = 1;
//
//                            //increment quantity of the product
//                            incrementBtn.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    finalCost = finalCost + cost;
//                                    quantity++;
//
//                                    quantityTv.setText(""+quantity);
//                                }
//                            });
//
//                            //decrement quantity of the product, only if quantity > 1
//                            decrementBtn.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    if(quantity>1){
//                                        finalCost = finalCost - cost;
//                                        quantity --;
//
//                                        quantityTv.setText(""+quantity);
//
//                                    }
//                                }
//                            });
//
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });

}