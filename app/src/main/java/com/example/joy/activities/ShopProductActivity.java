package com.example.joy.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.joy.R;
import com.example.joy.adapter.AdapterProductSeller;
import com.example.joy.adapter.AdapterProductUser;
import com.example.joy.model.ModelProduct;
import com.example.joy.model.ModelShop;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShopProductActivity extends AppCompatActivity {

    private ImageButton backBtn;
    private TextView categoryTitle;
    private RecyclerView productRv;
    private EditText searchEt;

    private FirebaseAuth firebaseAuth;
    private ArrayList<ModelProduct> productList;
    private ArrayList<ModelShop> shopsList;
    private AdapterProductUser adapterProductUser;

    String categoryName,cId,shopId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_product);

        backBtn = findViewById(R.id.backBtn);
        categoryTitle = findViewById(R.id.categoryTitle);
        productRv = findViewById(R.id.productRv);
        searchEt = findViewById(R.id.searchEt);

        firebaseAuth = FirebaseAuth.getInstance();

        categoryName = getIntent().getStringExtra("categoryTitle");
        cId = getIntent().getStringExtra("categoryId");
        shopId = getIntent().getStringExtra("shopUid");

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    adapterProductUser.getFilter().filter(s);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //System.out.println(shopId);
        categoryTitle.setText(categoryName);

        loadAllProducts();
    }

    private void loadAllProducts() {

        productList = new ArrayList<>();
        //get all products
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(shopId).child("category").child(cId).child("products").orderByChild("categoryId").equalTo(cId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        productList.clear();
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                            productList.add(modelProduct);
                        }
                        //setup adapter
                        adapterProductUser = new AdapterProductUser(ShopProductActivity.this, productList);
                        //set adapter
                        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(productRv.getContext(),
                                R.anim.layout_fall_down);
                        productRv.setLayoutAnimation(controller);
                        //productRv.setLayoutManager(new GridLayoutManager(ShopProductActivity.this,2,GridLayoutManager.VERTICAL,false));
                        productRv.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
                        productRv.setAdapter(adapterProductUser);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

}