package com.example.joy.adapter;


import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joy.R;
import com.example.joy.model.ModelProductReview;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterProductReview extends RecyclerView.Adapter<AdapterProductReview.HolderProductReview> {

    private Context context;
    private ArrayList<ModelProductReview> productReviews;

    public AdapterProductReview(Context context, ArrayList<ModelProductReview> productReviews) {
        this.context = context;
        this.productReviews = productReviews;
    }

    @NonNull
    @Override
    public HolderProductReview onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout row_review
        View view = LayoutInflater.from(context).inflate(R.layout.row_product_review,parent,false);

        return new HolderProductReview(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductReview holder, int position) {

        ModelProductReview modelProductReview = productReviews.get(position);
        String uid = modelProductReview.getUid();
        String pid = modelProductReview.getpId();
        String timestamp = modelProductReview.getTimestamp();
        String rating = modelProductReview.getRating();
        String comments = modelProductReview.getComments();
        String cid = modelProductReview.getcId();
        // we also need info (profile image,name) of user who wrote the review:we can do it using uid of user
        loadUserDetail(modelProductReview,holder);
        //convert timestamp to proper format dd/MM/yyyy
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        String dateFormat = DateFormat.format("dd/MM/yyyy",calendar).toString();

        //set data
        holder.ratingBar.setRating(Float.parseFloat(rating));
        holder.reviewTv.setText(comments);
        holder.dateTv.setText(dateFormat);

    }

    private void loadUserDetail(ModelProductReview modelReview, final HolderProductReview holder) {
        //uid of user who wrote review
        String uid = modelReview.getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //get user info, use  same key names as in firebase
                String name = ""+ dataSnapshot.child("name").getValue();
                String profileImage = "" + dataSnapshot.child("profileImage").getValue();

                //set data
                holder.nameTv.setText(name);
                try {
                    Picasso.get().load(profileImage).placeholder(R.drawable.ic_person_gray).into(holder.profileIv);

                }
                catch (Exception e)
                {
                    //if anything goes wrong setting image(exception occurs),set default image
                    holder.profileIv.setImageResource(R.drawable.ic_person_gray);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return productReviews.size();
    }

    public class HolderProductReview extends RecyclerView.ViewHolder {

        private ImageView profileIv;
        private TextView nameTv;
        private TextView dateTv;
        public TextView reviewTv;
        private RatingBar ratingBar;

        public HolderProductReview(@NonNull View itemView) {
            super(itemView);

            profileIv = itemView.findViewById(R.id.profileIv);
            nameTv =itemView.findViewById(R.id.nameTv);
            ratingBar=itemView.findViewById(R.id.ratingBar);
            dateTv =itemView.findViewById(R.id.dateTv);
            reviewTv = itemView.findViewById(R.id.reviewTv);
        }
    }
}
