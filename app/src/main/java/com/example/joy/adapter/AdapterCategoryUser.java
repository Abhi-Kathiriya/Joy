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
import com.example.joy.activities.ShopProductActivity;
import com.example.joy.model.ModelCategory;
import com.example.joy.model.ModelShop;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterCategoryUser extends RecyclerView.Adapter<AdapterCategoryUser.HolderCategory>{

    private Context context;
    public ArrayList<ModelCategory> categoryList;
    public ArrayList<ModelShop> shopsList;


    public AdapterCategoryUser(Context context, ArrayList<ModelCategory> categoryList, ArrayList<ModelShop> shopsList) {
        this.context = context;
        this.categoryList = categoryList;
        this.shopsList = shopsList;
    }



    @NonNull
    @Override
    public HolderCategory onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.category_grid, parent , false);
        return new HolderCategory(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderCategory holder, final int position) {
        //get data


        final ModelCategory modelCategory = categoryList.get(position);
        //final ModelShop modelShop = shopsList.get(position);
        String categoryId = modelCategory.getCategoryId();
        //String uid = modelCategory.getUid();
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
                Intent intent = new Intent(context, ShopProductActivity.class);
                intent.putExtra("categoryId",categoryId);
                intent.putExtra("categoryTitle",categoryTitle);
                intent.putExtra("shopUid",uid);
                context.startActivity(intent);
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
