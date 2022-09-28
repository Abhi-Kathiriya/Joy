package com.example.joy.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joy.R;
import com.example.joy.activities.AddProductActivity;
import com.example.joy.model.ModelCategory;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.HolderCategory> {

    private Context context;
    public ArrayList<ModelCategory> categoryList;

    public AdapterCategory(Context context, ArrayList<ModelCategory> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public HolderCategory onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.category_grid, parent , false);
        return new HolderCategory(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderCategory holder, int position) {
        //get data
        final ModelCategory modelCategory = categoryList.get(position);
        String categoryId = modelCategory.getCategoryId();
        String uid = modelCategory.getUid();
        String categoryTitle = modelCategory.getCategoryTitle();
        String icon = modelCategory.getProductIcon();

        //set data
        holder.categoryTv.setText(categoryTitle);
        try {
            Picasso.get().load(icon).placeholder(R.drawable.ic_baseline_insert_photo_24).into(holder.categoryImage);
        }
        catch (Exception e){
            holder.categoryImage.setImageResource(R.drawable.ic_baseline_insert_photo_24);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddProductActivity.class);
                intent.putExtra("categoryId",categoryId);
                intent.putExtra("categoryTitle",categoryTitle);
                //intent.putExtra("uid",uid);
                context.startActivity(intent);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //show delete confirm
                AlertDialog.Builder builder =new AlertDialog.Builder(context);
                builder.setTitle("Delete")
                        .setMessage("Are you sure you want to delete this " + categoryTitle + " category?")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //cancel, dismiss dialog
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //delete
                                deleteCategory(categoryId);//id is the product id
                            }
                        }).show();
                return false;
            }
        });

    }

    private void deleteCategory(String id) {

        //delete product using its id

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.child(firebaseAuth.getUid()).child("category").child(id).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //product deleted
                        Toast.makeText(context,"Category deleted", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed deleting product
                        Toast.makeText(context,""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class HolderCategory extends RecyclerView.ViewHolder {

        private ImageView categoryImage;
        private TextView categoryTv;

        public HolderCategory(@NonNull View itemView) {
            super(itemView);

            categoryTv = itemView.findViewById(R.id.categoryTv);
            categoryImage = itemView.findViewById(R.id.categoryImage);
        }
    }
}
