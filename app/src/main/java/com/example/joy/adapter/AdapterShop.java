package com.example.joy.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joy.FilterProduct;
import com.example.joy.FilterShop;
import com.example.joy.R;
import com.example.joy.activities.ShopDetailsActivity;
import com.example.joy.model.ModelProduct;
import com.example.joy.model.ModelReview;
import com.example.joy.model.ModelShop;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterShop extends RecyclerView.Adapter<com.example.joy.adapter.AdapterShop.HolderShop> implements Filterable {

    private Context context;
    public ArrayList<ModelShop> shopsList;
    public ArrayList<ModelShop> filterList;
    private FilterShop filter;

    public AdapterShop(Context context, ArrayList<ModelShop> shopsList) {
        this.context = context;
        this.shopsList = shopsList;
        this.filterList = shopsList;
    }

    @NonNull
    @Override
    public HolderShop onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout row_shop.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_shop, parent, false);
        return new HolderShop(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderShop holder, int position) {
        //get data
        ModelShop modelShop = shopsList.get(position);
        String accountType = modelShop.getAccountType();
        String address = modelShop.getAddress();
        String city = modelShop.getCity();
        String country = modelShop.getCountry();
        String deliveryFee = modelShop.getDeliveryFee();
        String email = modelShop.getEmail();
        String latitude = modelShop.getLatitude();
        String longitude = modelShop.getLongitude();
        String online = modelShop.getOnline();
        String name = modelShop.getName();
        String phone = modelShop.getPhone();
        final String uid = modelShop.getUid();
        String timestamp = modelShop.getTimestamp();
        String shopOpen = modelShop.getShopOpen();
        String state = modelShop.getState();
        String profileImage = modelShop.getProfileImage();
        String shopName = modelShop.getShopName();

        loadReviews(modelShop,holder);// load avg rating , set to ratingbar

        //set data
        holder.shopNameTv.setText(shopName);
        holder.addressTv.setText(address);
        //check if image

        try {
            Picasso.get().load(profileImage).placeholder(R.drawable.ic_store_gray).into(holder.shopIv);
        }
        catch (Exception e){
            holder.shopIv.setImageResource(R.drawable.ic_store_gray);
        }

        //handle click listener, show shop details
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShopDetailsActivity.class);
                intent.putExtra("shopUid",uid);
                context.startActivity(intent);
            }
        });
    }

    private float ratingSum = 0;
    private void loadReviews(ModelShop modelShop, final HolderShop holder) {
        String shopUid = modelShop.getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUid).child("Ratings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //clear list before adding data into it

                ratingSum = 0;
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    float rating = Float.parseFloat(""+ds.child("ratings").getValue());//e.g. 4.3
                    ratingSum = ratingSum + rating; //for avg rating, add(addition of) all ratings,later will divide it by number of reviews


                }

                long numberOfReviews = dataSnapshot.getChildrenCount();
                float avgRating = ratingSum/numberOfReviews;
                holder.ratingBar.setRating(avgRating);
                holder.ratingTv.setText(String.format("%.1f" , avgRating) + " [" +numberOfReviews+"]");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return shopsList.size(); //return number of records
    }

    @Override
    public Filter getFilter() {
        if(filter==null){
            filter = new FilterShop(this,filterList);
        }
        return filter;
    }
    //view holder
    class HolderShop extends RecyclerView.ViewHolder {

        //ui views of row_shop.xml
        private ImageView shopIv,onlineIv;
        private TextView shopNameTv,addressTv,ratingTv;
        private RatingBar ratingBar;

        public HolderShop(@NonNull View itemView) {
            super(itemView);

            //init uid views
            shopIv = itemView.findViewById(R.id.shopIv);
            shopNameTv = itemView.findViewById(R.id.shopNameTv);
            addressTv = itemView.findViewById(R.id.addressTv);
            ratingTv = itemView.findViewById(R.id.ratingTv);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}
