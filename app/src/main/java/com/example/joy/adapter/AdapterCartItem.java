package com.example.joy.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joy.R;
import com.example.joy.activities.CartActivity;
import com.example.joy.activities.ProductDetailsActivity;
import com.example.joy.activities.ShopDetailsActivity;
import com.example.joy.model.ModelCartItem;
import com.example.joy.model.ModelProduct;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import p32929.androideasysql_library.EasyDB;

public class AdapterCartItem extends RecyclerView.Adapter<com.example.joy.adapter.AdapterCartItem.HolderCartItem> {

    public ArrayList<ModelProduct> productList;
    private EasyDB easyDB;

    public AdapterCartItem(Context context, ArrayList<ModelCartItem> cartItemList, ArrayList<ModelProduct> productList) {
        this.context = context;
        this.cartItemList = cartItemList;
        this.productList = productList;
    }

    private Context context;
    private ArrayList<ModelCartItem> cartItemList;

    @NonNull
    @Override
    public HolderCartItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout of row_cartitem.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_cartitem, parent, false);
        return new HolderCartItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderCartItem holder, final int position) {
        //get data
        ModelCartItem modelCartItem = cartItemList.get(position);
        final String id = modelCartItem.getpId();
        String title = modelCartItem.getName();
        String price = modelCartItem.getPrice();
        String icon = modelCartItem.getImage();
        String shopUid = modelCartItem.getShopUid();
        final int quantity = Integer.parseInt(modelCartItem.getQuantity());
        final String cost = modelCartItem.getCost();
        final String uid = modelCartItem.getUid();

//        final ModelProduct modelProduct = productList.get(position);
//
//        final String price1;
//        if(modelProduct.getDiscountAvailable().equals("true")){
//            //product have discount
//            price1 = modelProduct.getDiscountPrice();
//
//        }
//        else{
//
//            price1 = modelProduct.getOriginalPrice();
//        }


        //set data
        holder.itemTitleTv.setText("" + title);
        holder.itemPriceTv.setText("₹" + cost);
        holder.itemQuantityTv.setText("[" + quantity + "]");
        holder.finalPrice.setText("TOTAL : ₹" +price);

        try {
            Picasso.get().load(icon).placeholder(R.drawable.splashlogo).into(holder.image);
        }
        catch (Exception e){
            holder.image.setImageResource(R.drawable.splashlogo);
        }


        //double allTotalPrice = 0.00;
        //double tx = Double.parseDouble((((CartActivity) context).allTotalPriceTv.getText().toString().trim().replace("₹", "")));
        double totalPrice =  Double.parseDouble(price.replace("₹", ""));
        //totalPrice = Double.parseDouble(totalPrice + price);
        //double deliveryFee = Double.parseDouble((((CartActivity) context).deliveryFee.replace("₹", "")));
        double sTotalPrice = Double.parseDouble(String.format("%.2f", totalPrice));
        //double fTotal = Double.parseDouble(totalPrice + price);
        //double allTotal = Double.parseDouble(String.format("%.2f", sTotalPrice)) + Double.parseDouble(String.format("%.2f", deliveryFee));
        //((CartActivity) context).allTotalPrice = Double.parseDouble(String.format("%.2f", totalPrice));
        //((CartActivity) context).sTotalTv.setText("₹" + String.format("%.2f", sTotalPrice));
        //((CartActivity) context).allTotalPriceTv.setText("₹" + String.format("%.2f", Double.parseDouble(String.format("%.2f", allTotal))));
        //((CartActivity) context).productId = id;
        //((CartActivity) context).cost1 = Double.parseDouble(price.replace("₹", ""));;

        //Toast.makeText(context, ""+id, Toast.LENGTH_SHORT).show();



        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                deleteItem(uid,id);

                return false;
            }
        });
       // FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    }


    private void deleteItem(String uid, String id) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.child(uid).child("items").child(id).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //product deleted
                        Toast.makeText(context,"Item deleted...", Toast.LENGTH_SHORT).show();
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
        return cartItemList.size();//return number of records
    }


    class HolderCartItem extends RecyclerView.ViewHolder{

        //ui view of row_cartitem.xml
        private TextView itemTitleTv,itemPriceTv,itemQuantityTv,finalPrice;
        //private ImageButton decrementBtn,incrementBtn;
        private ImageView image;

        public HolderCartItem(@NonNull View itemView) {
            super(itemView);

            itemTitleTv = itemView.findViewById(R.id.itemName);
            itemPriceTv = itemView.findViewById(R.id.itemPrice);
            itemQuantityTv = itemView.findViewById(R.id.quantityTv);
            finalPrice = itemView.findViewById(R.id.finalPrice);
//            incrementBtn = itemView.findViewById(R.id.incrementBtn);
//            decrementBtn = itemView.findViewById(R.id.decrementBtn);
            image = itemView.findViewById(R.id.itemIv);

        }
    }
}
