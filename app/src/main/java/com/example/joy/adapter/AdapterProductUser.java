package com.example.joy.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joy.R;
import com.example.joy.activities.EditProductSellerActivity;
import com.example.joy.activities.ProductDetailsActivity;
import com.example.joy.activities.ShopProductActivity;
import com.example.joy.model.ModelProduct;
import com.example.joy.model.ModelProductReview;
import com.example.joy.model.ModelShop;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class AdapterProductUser extends RecyclerView.Adapter<AdapterProductUser.HolderProductSeller> {

    private Context context;
    public ArrayList<ModelProduct> productList;

    public AdapterProductUser(Context context, ArrayList<ModelProduct> productList) {
        this.context = context;
        this.productList = productList;

    }


    @NonNull
    @Override
    public AdapterProductUser.HolderProductSeller onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_product, parent , false);
        return new HolderProductSeller(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterProductUser.HolderProductSeller holder, int position) {

        final ModelProduct modelProduct = productList.get(position);
        String id = modelProduct.getProductId();
        String cId = modelProduct.getCategoryId();
        String uid = modelProduct.getUid();
        String discountAvailable = modelProduct.getDiscountAvailable();
        String discountNote = modelProduct.getDiscountNote();
        String discountPrice = modelProduct.getDiscountPrice();
        String productDescription = modelProduct.getProductDescription();
        String icon = modelProduct.getProductIcon();
        String deliveryTime = modelProduct.getDeliveryTime();
        String title = modelProduct.getProductTitle();
        String instruction = modelProduct.getProductInstruction();
        String timestamp = modelProduct.getTimestamp();
        String originalPrice = modelProduct.getOriginalPrice();
        String favourite = modelProduct.getFavourite();

        loadReviews(modelProduct,holder);
        
        //set data
        holder.titleTv.setText(title);
        holder.discountedNoteTv.setText(discountNote);
        holder.discountedPriceTv.setText("₹"+discountPrice);
        holder.originalPriceTv.setText("₹"+originalPrice);
//        holder.reviewTv.setText("4.5");
//        holder.reviewNum.setText("(57)");



        if(favourite.equals("false")){
            holder.favouriteIb.setVisibility(View.GONE);
            //holder.favouriteIb.setImageResource(R.drawable.ic_outline_favorite_border_24);
        }
        else {
            holder.favouriteIb.setImageResource(R.drawable.ic_baseline_favorite_24);
        }


        if(discountAvailable.equals("true")){
            holder.discountedPriceTv.setVisibility(View.VISIBLE);
            holder.discountedNoteTv.setVisibility(View.VISIBLE);
            holder.originalPriceTv.setPaintFlags(holder.originalPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else {
            holder.discountedPriceTv.setVisibility(View.GONE);
            holder.discountedNoteTv.setVisibility(View.GONE);
            holder.originalPriceTv.setPaintFlags(0);
        }
        try {
            Picasso.get().load(icon).placeholder(R.drawable.splashlogo).into(holder.productIconIv);
        }
        catch (Exception e){
            holder.productIconIv.setImageResource(R.drawable.splashlogo);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ProductDetailsActivity.class);
                intent.putExtra("shopUid",uid);
                intent.putExtra("pId",id);
                intent.putExtra("cId",cId);
                intent.putExtra("title",title);
                intent.putExtra("disAvl",discountAvailable);
                intent.putExtra("DisPrice",discountPrice);
                intent.putExtra("OrgPrice",originalPrice);
                intent.putExtra("image",icon);
                context.startActivity(intent);
            }
        });

    }


    private float ratingSum = 0;
    private void loadReviews(ModelProduct modelProduct, final AdapterProductUser.HolderProductSeller holder) {

        String id = modelProduct.getProductId();
        String cId = modelProduct.getCategoryId();
        String uid = modelProduct.getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid).child("category").child(cId).child("products").child(id).child("Rating").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ratingSum = 0;
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    float rating = Float.parseFloat(""+ds.child("rating").getValue());//e.g. 4.3
                    ratingSum = ratingSum + rating; //for avg rating, add(addition of) all ratings,later will divide it by number of reviews

                }

                long numberOfReviews = dataSnapshot.getChildrenCount();
                float avgRating = ratingSum/numberOfReviews;
                holder.reviewTv.setText(String.format("%.1f" , avgRating) + " [" +numberOfReviews+"]");// e.g 4.7  [10]
                //holder.reviewNum.setText(String.format("%.2f" , " [" +numberOfReviews+"]"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class HolderProductSeller extends RecyclerView.ViewHolder {

        private ImageView productIconIv;
        private ImageButton favouriteIb;
        private TextView titleTv,discountedPriceTv,originalPriceTv,reviewTv,reviewNum,discountedNoteTv;

        public HolderProductSeller(@NonNull View itemView) {
            super(itemView);

            productIconIv = (itemView).findViewById(R.id.productIconIv);
            favouriteIb = (itemView).findViewById(R.id.favouriteIb);
            titleTv = (itemView).findViewById(R.id.titleTv);
            originalPriceTv = (itemView).findViewById(R.id.originalPriceTv);
            discountedPriceTv = (itemView).findViewById(R.id.discountedPriceTv);
            reviewTv = (itemView).findViewById(R.id.reviewTv);
            reviewNum = (itemView).findViewById(R.id.reviewNum);
            discountedNoteTv = (itemView).findViewById(R.id.discountedNoteTv);

        }
    }
}
