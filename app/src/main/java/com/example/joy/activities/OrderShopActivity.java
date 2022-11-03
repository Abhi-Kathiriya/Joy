package com.example.joy.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.joy.R;
import com.example.joy.adapter.AdapterOrderShop;
import com.example.joy.model.ModelOrderShop;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrderShopActivity extends AppCompatActivity {

    private TextView filteredOrdersTv;
    private ImageButton filterOrderBtn,homeBtn;
    private RecyclerView ordersRv;

    private ArrayList<ModelOrderShop> orderShopArrayList;
    private AdapterOrderShop adapterOrderShop;

    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_shop);

        filteredOrdersTv = (TextView) findViewById(R.id.filteredOrdersTv);
        filterOrderBtn = (ImageButton) findViewById(R.id.filterOrderBtn);
        ordersRv = (RecyclerView) findViewById(R.id.ordersRv);
        homeBtn = findViewById(R.id.homeBtn);

        firebaseAuth = FirebaseAuth.getInstance();

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderShopActivity.this, MainSellerActivity.class);
                startActivity(intent);
            }
        });

        filterOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] options = {"All","Ordered", "Packed", "Arriving today", "Delivered", "Cancelled"};
                AlertDialog.Builder builder = new AlertDialog.Builder(OrderShopActivity.this);
                builder.setTitle("Filter Orders:")
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(which==0){
                                    filteredOrdersTv.setText("Showing All Orders");
                                    adapterOrderShop.getFilter().filter("");
                                }
                                else{
                                    String optionClicked = options[which];
                                    filteredOrdersTv.setText("Showing " +optionClicked+ " Orders");
                                    adapterOrderShop.getFilter().filter(optionClicked);
                                }
                            }
                        })
                        .show();
            }
        });

        loadAllOrders();
    }

    private void loadAllOrders() {
        //init array list
        orderShopArrayList = new ArrayList<>();
        //load orders of shop
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Orders")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //clear list before adding new data in it
                        orderShopArrayList.clear();
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            ModelOrderShop modelOrderShop = ds.getValue(ModelOrderShop.class);
                            //add to list
                            orderShopArrayList.add(modelOrderShop);

                        }
                        //setup adapter
                        adapterOrderShop = new AdapterOrderShop(OrderShopActivity.this,orderShopArrayList);
                        //set adapter to recyclerview
                        ordersRv.setAdapter(adapterOrderShop);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}