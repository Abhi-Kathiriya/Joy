package com.example.joy.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joy.R;
import com.example.joy.model.ModelOffer;
import com.example.joy.model.ModelProduct;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterOfferSeller extends RecyclerView.Adapter<AdapterOfferSeller.HolderOfferSeller> {

    private Context context;
    public ArrayList<ModelOffer> offerList;

    public AdapterOfferSeller(Context context, ArrayList<ModelOffer> offerList) {
        this.context = context;
        this.offerList = offerList;
    }

    @NonNull
    @Override
    public AdapterOfferSeller.HolderOfferSeller onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_offers, parent , false);
        return new HolderOfferSeller(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterOfferSeller.HolderOfferSeller holder, int position) {

        ModelOffer modelOffer = offerList.get(position);
        String uid = modelOffer.getUid();
        String offerId = modelOffer.getOfferId();
        String icon = modelOffer.getOfferIcon();
        String title = modelOffer.getOfferTitle();

        holder.offerTitle.setText(title);

        try {
            Picasso.get().load(icon).placeholder(R.drawable.splashlogo).into(holder.offerIv);
        }
        catch (Exception e){
            holder.offerIv.setImageResource(R.drawable.splashlogo);
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //show delete confirm
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete")
                        .setMessage("Are you sure you want to delete this offer?")
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //delete
                                deleteProduct(offerId);//id is the product id
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
                return false;
            }
        });

    }

    private void deleteProduct(String offerId) {

        //delete offer using its id

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.child(firebaseAuth.getUid()).child("offers").child(offerId).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //product deleted
                        Toast.makeText(context,"Offer deleted...", Toast.LENGTH_SHORT).show();
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
        return offerList.size();
    }

    public class HolderOfferSeller extends RecyclerView.ViewHolder {

        private ImageView offerIv;
        private TextView offerTitle;

        public HolderOfferSeller(@NonNull View itemView) {
            super(itemView);

            offerIv = (itemView).findViewById(R.id.offerIv);
            offerTitle = (itemView).findViewById(R.id.offerTitle);
        }
    }
}
