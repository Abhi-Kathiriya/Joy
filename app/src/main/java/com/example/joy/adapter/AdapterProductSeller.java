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
import com.example.joy.activities.MainSellerActivity;
import com.example.joy.model.ModelProduct;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class AdapterProductSeller extends RecyclerView.Adapter<AdapterProductSeller.HolderProductSeller> {

    private Context context;
    public ArrayList<ModelProduct> productList;


    public AdapterProductSeller(Context context, ArrayList<ModelProduct> productList) {
        this.context = context;
        this.productList = productList;
    }


    @NonNull
    @Override
    public AdapterProductSeller.HolderProductSeller onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_product, parent , false);
        return new HolderProductSeller(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterProductSeller.HolderProductSeller holder, int position) {

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

        //set data
        holder.titleTv.setText(title);
        holder.discountedNoteTv.setText(discountNote);
        holder.discountedPriceTv.setText("₹"+discountPrice);
        holder.originalPriceTv.setText("₹"+originalPrice);
        holder.reviewTv.setText("4.5");
        holder.reviewNum.setText("(57)");



        if(favourite.equals("false")){
            holder.favouriteIb.setImageResource(R.drawable.ic_outline_favorite_border_24);
        }
        else {
            holder.favouriteIb.setImageResource(R.drawable.ic_baseline_favorite_24);
        }


        holder.favouriteIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            if (favourite.equals("false")){
                addToFav(cId,timestamp,id,uid,discountAvailable,deliveryTime,discountNote,discountPrice,productDescription,icon,title,instruction,originalPrice);
                //holder.favouriteIb.setImageResource(R.drawable.ic_baseline_favorite_24);
              }
             else if(favourite.equals("true")){
                removeToFav(cId,timestamp);
                //holder.favouriteIb.setImageResource(R.drawable.ic_outline_favorite_border_24);
              }

            }
        });

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
            Picasso.get().load(icon).placeholder(R.drawable.ic_baseline_add_shopping_cart_white).into(holder.productIconIv);
        }
        catch (Exception e){
            holder.productIconIv.setImageResource(R.drawable.ic_baseline_add_shopping_cart_white);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailsBottomSheet(modelProduct);
            }
        });

    }

    private void removeToFav(String cId, String id) {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("favourite", "false");

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.child(firebaseAuth.getUid()).child("category").child(cId).child("products").child(id)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        remove(id);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void remove(String id) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.child(firebaseAuth.getUid()).child("favourite").child(id).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

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


    private void addToFav(String cId, String timestamp, String id, String uid, String discountAvailable, String deliveryTime, String discountNote, String discountPrice, String productDescription, String icon, String title, String instruction, String originalPrice) {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("favourite", "true");
        hashMap.put("productId", ""+id);
        hashMap.put("categoryId", ""+cId);
        hashMap.put("productTitle", ""+title);
        hashMap.put("productDescription", ""+productDescription);
        hashMap.put("productInstruction", ""+instruction);
        hashMap.put("productIcon", ""+icon);//no image, empty set
        hashMap.put("originalPrice", ""+originalPrice);
        hashMap.put("discountPrice", ""+discountPrice);
        hashMap.put("discountNote", ""+discountNote);
        hashMap.put("discountAvailable", ""+discountAvailable);
        hashMap.put("deliveryTime", ""+deliveryTime);
        hashMap.put("timestamp", ""+timestamp);
        hashMap.put("uid", ""+uid);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.child(firebaseAuth.getUid()).child("favourite").child(id)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                       addtoFavourite(cId,id);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addtoFavourite(String cId, String id) {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("favourite", "true");

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.child(firebaseAuth.getUid()).child("category").child(cId).child("products").child(id)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void detailsBottomSheet(ModelProduct modelProduct) {
//bottom sheet
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context,R.style.AppBottomSheetDialogTheme);
        View view = LayoutInflater.from(context).inflate(R.layout.bs_details_product_seller,null);
        //
        bottomSheetDialog.setContentView(view);

//init views of bottomSheet
        ImageButton backBtn = view.findViewById(R.id.backBtn);
        ImageButton deleteBtn = view.findViewById(R.id.deleteBtn);
        ImageButton editBtn = view.findViewById(R.id.editBtn);
        ImageView productIconIv = view.findViewById(R.id.productIconIv);
        TextView discountNoteTv = view.findViewById(R.id.discountedNoteTv);
        TextView productTitleTv = view.findViewById(R.id.productTitleTv);
        TextView descriptionTv = view.findViewById(R.id.descriptionTv);
        RatingBar ratingbar = view.findViewById(R.id.ratingbar);
        TextView ratingTv = view.findViewById(R.id.ratingTv);
        TextView ratingNum = view.findViewById(R.id.RatingNum);
        TextView discountedPriceTv = view.findViewById(R.id.discountedPriceTv);
        TextView originalPriceTv = view.findViewById(R.id.originalPriceTv);
        TextView instructionTv = view.findViewById(R.id.instructionTv);
        TextView deliveryTimeTv = view.findViewById(R.id.deliveryTimeTv);


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
        String timestamp = modelProduct.getTimestamp();
        String originalPrice = modelProduct.getOriginalPrice();
        String favourite = modelProduct.getFavourite();
        String instruction = modelProduct.getProductInstruction();

        deliveryTimeTv.setText("This product will be shipped within "+deliveryTime);
        productTitleTv.setText(title);
        descriptionTv.setText(productDescription);
        discountNoteTv.setText(discountNote);
        discountedPriceTv.setText("₹"+ discountPrice);
        originalPriceTv.setText("₹"+ originalPrice);
        instructionTv.setText(instruction);
        ratingTv.setText("4.5");
        ratingNum.setText("(57)");

        if(discountAvailable.equals("true")){
            discountedPriceTv.setVisibility(View.VISIBLE);
            discountNoteTv.setVisibility(View.VISIBLE);
            originalPriceTv.setPaintFlags(originalPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else {
            discountedPriceTv.setVisibility(View.GONE);
            discountNoteTv.setVisibility(View.GONE);
        }
        try {
            Picasso.get().load(icon).placeholder(R.drawable.ic_baseline_add_shopping_cart_white).into(productIconIv);
        }
        catch (Exception e){
            productIconIv.setImageResource(R.drawable.ic_baseline_add_shopping_cart_white);
        }

        // show dialog
        bottomSheetDialog.show();

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                //open edit product activity,pass id of product
                Intent intent = new Intent(context, EditProductSellerActivity.class);
                intent.putExtra("productId",id);
                intent.putExtra("categoryId",cId);
                context.startActivity(intent);
            }
        });
        //delete click
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                //show delete confirm
                AlertDialog.Builder builder =new AlertDialog.Builder(context);
                builder.setTitle("Delete")
                        .setMessage("Are you sure you want to delete product" + title + " ?")
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //delete
                                deleteProduct(id,cId);
                                remove(id);//id is the product id
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //cancel, dismiss dialog
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
        //back click
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dismiss bottom sheet
                bottomSheetDialog.dismiss();
            }
        });

    }

    private void deleteProduct(String id, String cid) {

        //delete product using its id

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.child(firebaseAuth.getUid()).child("category").child(cid).child("products").child(id).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //product deleted
                        Toast.makeText(context,"Product deleted...", Toast.LENGTH_SHORT).show();
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
