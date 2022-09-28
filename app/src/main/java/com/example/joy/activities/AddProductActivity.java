package com.example.joy.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.joy.R;
import com.example.joy.adapter.AdapterProductSeller;
import com.example.joy.model.ModelProduct;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddProductActivity extends AppCompatActivity {

    private FloatingActionButton addProductBtn;
    private ImageButton backBtn;
    private TextView categoryTitle;
    private RecyclerView productRv;

    private FirebaseAuth firebaseAuth;
    private ArrayList<ModelProduct> productList;
    private AdapterProductSeller adapterProductSeller;

    String categoryName,cId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        backBtn = findViewById(R.id.backBtn);
        addProductBtn = findViewById(R.id.addProductBtn);
        categoryTitle = findViewById(R.id.categoryTitle);
        productRv = findViewById(R.id.productRv);

        firebaseAuth = FirebaseAuth.getInstance();

        categoryName = getIntent().getStringExtra("categoryTitle");
        cId = getIntent().getStringExtra("categoryId");
        
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        categoryTitle.setText(categoryName);

        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddProductActivity.this, AddItemActivity.class);
                intent.putExtra("categoryId",cId);
                startActivity(intent);
            }
        });

        loadAllProducts();
    }

    private void loadAllProducts() {

        productList = new ArrayList<>();
        //get all products
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("category").child(cId).child("products").orderByChild("categoryId").equalTo(cId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        productList.clear();
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                            productList.add(modelProduct);
                        }
                        //setup adapter
                        adapterProductSeller = new AdapterProductSeller(AddProductActivity.this, productList);
                        //set adapter
                        productRv.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
                        productRv.setAdapter(adapterProductSeller);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

}