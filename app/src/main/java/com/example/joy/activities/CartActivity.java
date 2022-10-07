package com.example.joy.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.joy.R;
import com.example.joy.adapter.AdapterCartItem;
import com.example.joy.model.ModelCartItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    private RecyclerView cartItemRv;
    private Button checkoutBtn;
    //cart
    private ArrayList<ModelCartItem> cartItemList;
    private AdapterCartItem adapterCartItem;

    private String shopUid;
    private String myLatitude ,myLongitude ,myPhone, myAddress;
    private String shopName,shopEmail,shopPhone,shopAddress,shopLatitude,shopLongitude;
    public String deliveryFee;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cart);
        cartItemRv = findViewById(R.id.cartItemRv);
         TextView sTotalLabelTv = findViewById(R.id.sTotalLabelTv);
         sTotalTv = findViewById(R.id.sTotalTv);
         dFeeTv = findViewById(R.id.dFeeTv);
         allTotalPriceTv = findViewById(R.id.totalTv);
         checkoutBtn = findViewById(R.id.checkoutBtn);

        shopUid = getIntent().getStringExtra("shopUid");
        firebaseAuth = FirebaseAuth.getInstance();
        //init progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        loadCart();
        loadMyInfo();
        loadCartItem();

        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //first validate delivery address

                if(myAddress.equals("null") || myAddress.equals("")){
                    //user did't enter address in profile
                    Toast.makeText(CartActivity.this,"Please enter your address in you profile before placing order...",Toast.LENGTH_SHORT).show();
                    return;//don't procede further
                }
                if(myPhone.equals("null")){
                    //user did't enter phone number in profile
                    Toast.makeText(CartActivity.this,"Please enter your phone number in you profile before placing order...",Toast.LENGTH_SHORT).show();
                    return;//don't procede further
                }
                if (cartItemList.size() == 0){
                    //cart list is empty
                    Toast.makeText(CartActivity.this,"No item in cart",Toast.LENGTH_SHORT).show();
                    return;//don't procede further
                }
                submitOrder();
            }
        });

    }

    private void submitOrder() {
        //show progress dialog
        progressDialog.setMessage("Placing order...");
        progressDialog.show();

        //for order is and order time
        final String timestamp = "" + System.currentTimeMillis();

        String cost = allTotalPriceTv.getText().toString().trim().replace("₹", "");//remove ₹ if contains

        //setup order data
        final HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("orderId", "" + timestamp);
        hashMap.put("orderTime", "" + timestamp);
        hashMap.put("orderStatus", "In Progress");//in progress/completed/cancelled
        hashMap.put("orderCost", "" + cost);
        hashMap.put("orderBy", "" + firebaseAuth.getUid());
        hashMap.put("orderTo", "" + shopUid);
        hashMap.put("latitude", "" + myLatitude);
        hashMap.put("longitude", "" + myLongitude);
        hashMap.put("address", "" + myAddress);
        hashMap.put("deliveryFee", "" + deliveryFee);
        hashMap.put("phone", "" + myPhone);

        //add to db
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(shopUid).child("Orders");
        ref.child(timestamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //order info added now add order items

                        for (int i = 0; i < cartItemList.size(); i++) {
                            String pId = cartItemList.get(i).getpId();
                            String image = cartItemList.get(i).getImage();
                            String cost = cartItemList.get(i).getCost();
                            String name = cartItemList.get(i).getName();
                            String price = cartItemList.get(i).getPrice();
                            String quantity = cartItemList.get(i).getQuantity();


                            HashMap<String, String> hashMap1 = new HashMap<>();
                            hashMap1.put("pId", pId);
                            hashMap1.put("name", name);
                            hashMap1.put("cost", cost);
                            hashMap1.put("price", price);
                            hashMap1.put("quantity", quantity);
                            hashMap1.put("image", image);

                            ref.child(timestamp).child("Items").child(pId).setValue(hashMap1);

                        }


                        progressDialog.dismiss();
                        Toast.makeText(CartActivity .this,"Order Placed Successfully...",Toast.LENGTH_SHORT).
                                show();

                        deleteItem();

                        //after placing order open order details page
                        //open order details,we need to keys there,orderTo
                        Intent intent = new Intent(CartActivity.this, SuccessActivity.class);
                        intent.putExtra("orderTo",shopUid);
                        intent.putExtra("orderId",timestamp);
                        startActivity(intent);
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed placing order
                        progressDialog.dismiss();
                        Toast.makeText(CartActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }


    //public double allTotalPrice;
    //public double cost1;
    //need to access these views in adapter so making public
    public TextView sTotalTv,dFeeTv, allTotalPriceTv;

    private void loadCart() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("items").orderByChild("shopUid").equalTo(shopUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int sum = 0;
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            Map<String,Object> map = (Map<String, Object>) ds.getValue();
                            Object price = map.get("price");
                            int pValue = Integer.parseInt(String.valueOf(price));
                            sum += pValue;

                            sTotalTv.setText(String.valueOf("₹"+sum));
                            //allTotalPriceTv.setText("₹"+(sum + Double.parseDouble(deliveryFee.replace("₹", ""))));
                            loadShopDetails(sum);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }




    private void loadCartItem() {


        cartItemList = new ArrayList<>();
        //get all products
        //String status = "true";

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("items").orderByChild("shopUid").equalTo(shopUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        cartItemList.clear();
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            ModelCartItem modelCartItem = ds.getValue(ModelCartItem.class);
                            cartItemList.add(modelCartItem);
                        }
                        //setup adapter
                        adapterCartItem = new AdapterCartItem(CartActivity.this, cartItemList);
                        //set adapter
                        cartItemRv.setHasFixedSize(true);
                        cartItemRv.setAdapter(adapterCartItem);
                        //favItemRv.setLayoutManager(new GridLayoutManager(ShopDetailsActivity.this,2,GridLayoutManager.VERTICAL,false));
                        cartItemRv.setLayoutManager(new LinearLayoutManager(CartActivity.this));

//                        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
//                            @Override
//                            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//                                return false;
//                            }
//
//                            @Override
//                            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//                             int position = viewHolder.getAdapterPosition();
//
//                             switch (direction){
//                                 case ItemTouchHelper.LEFT:
//                                     cartItemList.get(position);
//                                     adapterCartItem.notifyItemRemoved(position);
//                                     deleteItem();
//
//                                     break;
//                                 case ItemTouchHelper.RIGHT:
//                                     cartItemList.get(position);
//                                     adapterCartItem.notifyItemRemoved(position);
//                                     deleteItem();
//                                     break;
//                             }
//
//                            }
//                        }).attachToRecyclerView(cartItemRv);


                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

//    public String productId;
    private void deleteItem() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.child(firebaseAuth.getUid()).child("items").removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //product deleted

                        Toast.makeText(CartActivity.this,"Item removed...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed deleting product
                        Toast.makeText(CartActivity.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadShopDetails(int sum) {
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
                shopLatitude = ""+dataSnapshot.child("Latitude").getValue();
                shopLongitude = ""+dataSnapshot.child("Longitude").getValue();
                deliveryFee = ""+dataSnapshot.child("deliveryFee").getValue();
                String profileImage = ""+dataSnapshot.child("profileImage").getValue();
                String shopOpen = ""+dataSnapshot.child("shopOpen").getValue();

                dFeeTv.setText("₹"+deliveryFee);
                allTotalPriceTv.setText("₹"+(sum + Double.parseDouble(deliveryFee.replace("₹", ""))));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){

            }
        });
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
                            myLatitude = ""+ds.child("Latitude").getValue();
                            myLongitude = ""+ds.child("Longitude").getValue();
                            myAddress = ""+ds.child("address").getValue();

                            //Toast.makeText(ShopDetailsActivity.this, ""+myLatitude+""+myLongitude, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError){

                    }
                });
    }


}