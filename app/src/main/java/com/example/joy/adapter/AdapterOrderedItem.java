package com.example.joy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joy.R;
import com.example.joy.model.ModelCartItem;
import com.example.joy.model.ModelCartItem;
import com.example.joy.model.ModelOrderedItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterOrderedItem extends RecyclerView.Adapter<com.example.joy.adapter.AdapterOrderedItem.HolderOrderedItem> {

    private Context context;
    private ArrayList<ModelOrderedItem> orderedItemArrayList;

    public AdapterOrderedItem(Context context, ArrayList<ModelOrderedItem> orderedItemArrayList) {
        this.context = context;
        this.orderedItemArrayList = orderedItemArrayList;
    }

    @NonNull
    @Override
    public HolderOrderedItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_ordereditem,parent,false);

        return new  HolderOrderedItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderOrderedItem holder, int position) {

        //get data at position
        ModelOrderedItem modelOrderedItem = orderedItemArrayList.get(position);
        String image = modelOrderedItem.getImage();
        String name = modelOrderedItem.getName();
        String cost = modelOrderedItem.getCost();
        String price = modelOrderedItem.getPrice();
        String quantity = modelOrderedItem.getQuantity();


        //set data
        holder.itemTitleTv.setText(name);
        holder.itemPriceEachTv.setText("₹"+cost);
        holder.itemPriceTv.setText("₹"+price);
        holder.itemQuantityTv.setText("[" +quantity+ "]");

        try {
            Picasso.get().load(image).placeholder(R.drawable.splashlogo).into(holder.imageView);
        }
        catch (Exception e){
            holder.imageView.setImageResource(R.drawable.splashlogo);
        }
    }

    @Override
    public int getItemCount() {
        return orderedItemArrayList.size();//return list size
    }

    //view holder class
    class HolderOrderedItem extends RecyclerView.ViewHolder {

        //views of row_ordereditem.xml
        private TextView itemTitleTv,itemPriceTv,itemPriceEachTv,itemQuantityTv;
        private ImageView imageView;

        public HolderOrderedItem(@NonNull View itemView) {
            super(itemView);

            //init views
            itemTitleTv = itemView.findViewById(R.id.itemName);
            itemPriceTv = itemView.findViewById(R.id.finalPrice);
            itemPriceEachTv = itemView.findViewById(R.id.itemPrice);
            itemQuantityTv = itemView.findViewById(R.id.quantityTv);
            imageView = itemView.findViewById(R.id.itemIv);
        }
    }
}
