package com.example.joy.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.joy.Constants;
import com.example.joy.R;
import com.example.joy.adapter.AdapterCartItem;
import com.example.joy.model.ModelCartItem;
import com.example.joy.model.ModelProduct;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class CartActivity extends AppCompatActivity implements PaymentResultListener {

    private RecyclerView cartItemRv;
    private Button checkoutBtn;
    private TextView shopNameTv,addressTv,cart;
    private EditText addressEt;
    private RadioGroup address,payment;
    private RadioButton address1,payment1,otherAddress,homeAddress,cod,op;


    //cart
    private ArrayList<ModelCartItem> cartItemList;
    private ArrayList<ModelProduct> productList;
    private AdapterCartItem adapterCartItem;

    private String shopUid;
    private String myLatitude ,myLongitude ,myPhone, myAddress,buyerAddress;
    private String shopName,shopEmail,shopPhone,shopAddress,shopLatitude,shopLongitude;
    public String deliveryFee;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         setContentView(R.layout.activity_cart);
         cartItemRv = findViewById(R.id.cartItemRv);
        addressEt = findViewById(R.id.addressEt);
        address = findViewById(R.id.address);
        payment = findViewById(R.id.payment);
        otherAddress = findViewById(R.id.otherAddress);
        homeAddress = findViewById(R.id.homeAddress);
        cod = findViewById(R.id.cod);
        op = findViewById(R.id.op);
         cart = findViewById(R.id.cart);
         addressTv = findViewById(R.id.addressTv);
         TextView sTotalLabelTv = findViewById(R.id.sTotalLabelTv);
         sTotalTv = findViewById(R.id.sTotalTv);
         shopNameTv = findViewById(R.id.shopNameTv);
         dFeeTv = findViewById(R.id.dFeeTv);
         allTotalPriceTv = findViewById(R.id.totalTv);
         checkoutBtn = findViewById(R.id.checkoutBtn);

        shopUid = getIntent().getStringExtra("shopUid");
        firebaseAuth = FirebaseAuth.getInstance();
        //init progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);


        loadCart();
        loadMyInfo();
        loadCartItem();

        cart.setVisibility(View.VISIBLE);


        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //first validate delivery address

                if(myAddress.equals("null") || myAddress.equals("")){
                    //user did't enter address in profile
                    Toast.makeText(CartActivity.this,"Please enter your address in you profile before placing order...",Toast.LENGTH_SHORT).show();
                    return;//don't procede further
                }
                if(myPhone.equals("null")){
                    //user did't enter phone number in profile
                    Toast.makeText(CartActivity.this,"Please enter your phone number in you profile before placing order...",Toast.LENGTH_SHORT).show();
                    return;//don't procede further
                }
                if (cartItemList.size() == 0){
                    //cart list is empty
                    Toast.makeText(CartActivity.this,"No item in cart",Toast.LENGTH_SHORT).show();
                    return;//don't procede further
                }

                if(!(otherAddress.isChecked() || homeAddress.isChecked())){
                    Toast.makeText(CartActivity.this,"Please select your address...",Toast.LENGTH_SHORT).show();
                    return;//don't procede further
                }

                if(!(cod.isChecked() || op.isChecked())){
                    Toast.makeText(CartActivity.this,"Please select your payment method...",Toast.LENGTH_SHORT).show();
                    return;//don't procede further
                }

                if (otherAddress.isChecked()){
                    buyerAddress = addressEt.getText().toString().trim();
                }
                else if(homeAddress.isChecked()){
                    buyerAddress = myAddress;
                }

                if (op.isChecked()){
                    String method = "Online Payment";
                    String amount = allTotalPriceTv.getText().toString().trim().replace("₹", "");//remove ₹ if contains
                    onlinePayment(buyerAddress,method,amount);
                }
                else if(cod.isChecked()) {
                    String method1 = "Cash on delivery";

                    AlertDialog.Builder builder =new AlertDialog.Builder(CartActivity.this);
                    builder.setTitle("Confirmation")
                            .setMessage("Are you sure you want to place this order?")
                            .setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    submitOrder(buyerAddress, method1);
                                }
                            })
                            .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //cancel, dismiss dialog
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }

            }
        });

        //Toast.makeText(this, ""+shopName, Toast.LENGTH_SHORT).show();
    }

    private void onlinePayment(String buyerAddress, String method, String amount) {

        final Activity activity =this;

        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_jljHQIGmW8hXph");
        checkout.setImage(R.drawable.logo);

        double finalAmount = Float.parseFloat(amount)*100;

        try {
            JSONObject options = new JSONObject();

            options.put("name", "Joy");
            options.put("description", "Reference No. #123456");
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.jpg");
            //options.put("order_id", "order_DBJOWzybf0sJbb");//from response of step 3.
            options.put("theme.color", "#C83232");
            options.put("currency", "INR");
            options.put("amount", ""+finalAmount);//pass amount in currency subunits
            options.put("prefill.email", "joyapp@gmail.com");
            options.put("prefill.contact","9988776655");
            JSONObject retryObj = new JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            checkout.open(activity, options);

        } catch(Exception e) {
            Log.e(TAG, "Error in starting Razorpay Checkout", e);
        }

    }

    @Override
    public void onPaymentSuccess(String s) {
        String method = "Online Payment";
        submitOrder(buyerAddress, method);
    }

    @Override
    public void onPaymentError(int i, String s) {

    }

    private void submitOrder(String buyerAddress, String method) {
        //show progress dialog
        progressDialog.setMessage("Placing order...");
        progressDialog.show();

        //for order is and order time
        final String timestamp = "" + System.currentTimeMillis();

        String cost = allTotalPriceTv.getText().toString().trim().replace("₹", "");//remove ₹ if contains

        //setup order data
        final HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("orderId", "" + timestamp);
        hashMap.put("orderTime", "" + timestamp);
        hashMap.put("orderStatus", "Ordered");//in progress/completed/cancelled
        hashMap.put("orderCost", "" + cost);
        hashMap.put("orderBy", "" + firebaseAuth.getUid());
        hashMap.put("orderTo", "" + shopUid);
        hashMap.put("latitude", "" + myLatitude);
        hashMap.put("longitude", "" + myLongitude);
        hashMap.put("payment", "" + method);
        hashMap.put("address", "" + buyerAddress);
        hashMap.put("deliveryFee", "" + deliveryFee);
        hashMap.put("phone", "" + myPhone);

        //add to db
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(shopUid).child("Orders");
        ref.child(timestamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //order info added now add order items

                        for (int i = 0; i < cartItemList.size(); i++) {
                            String pId = cartItemList.get(i).getpId();
                            String image = cartItemList.get(i).getImage();
                            String cost = cartItemList.get(i).getCost();
                            String name = cartItemList.get(i).getName();
                            String price = cartItemList.get(i).getPrice();
                            String quantity = cartItemList.get(i).getQuantity();


                            HashMap<String, String> hashMap1 = new HashMap<>();
                            hashMap1.put("pId", pId);
                            hashMap1.put("name", name);
                            hashMap1.put("cost", cost);
                            hashMap1.put("price", price);
                            hashMap1.put("quantity", quantity);
                            hashMap1.put("image", image);

                            ref.child(timestamp).child("Items").child(pId).setValue(hashMap1);

                        }


                        progressDialog.dismiss();
                        Toast.makeText(CartActivity .this,"Order Placed Successfully...",Toast.LENGTH_SHORT).
                                show();
                        deleteItem();
                        //after placing order open order details page
                        //open order details,we need to keys there,orderTo
                        Intent intent = new Intent(CartActivity.this, SuccessActivity.class);
                        intent.putExtra("orderTo",shopUid);
                        intent.putExtra("orderId",timestamp);
                        intent.putExtra("orderBy",firebaseAuth.getUid());
                        startActivity(intent);

                        //prepareNotificationMessage(timestamp);
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed placing order
                        progressDialog.dismiss();
                        Toast.makeText(CartActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }


    //public double allTotalPrice;
    //public double cost1;
    //need to access these views in adapter so making public
    public TextView sTotalTv,dFeeTv, allTotalPriceTv;

    private void loadCart() {


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("items").orderByChild("shopUid").equalTo(shopUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int sum = 0;
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            Map<String,Object> map = (Map<String, Object>) ds.getValue();
                            Object price = map.get("price");
                            int pValue = Integer.parseInt(String.valueOf(price));
                            sum += pValue;

                            sTotalTv.setText(String.valueOf("₹"+sum));
                            int finalSum = sum;

                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                            ref.child(shopUid).addValueEventListener(new ValueEventListener(){
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    //get shop data
                                    deliveryFee = ""+dataSnapshot.child("deliveryFee").getValue();

                                    int d = Integer.parseInt(deliveryFee);
                                    int s = Integer.parseInt(String.valueOf(finalSum));
                                    int cost1 = finalSum + d;//remove ₹ if contains

                                    cod.setEnabled(cost1 <= 2000);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError){

                                }
                            });
//                            String cost1 = sum + deliveryFee;//remove ₹ if contains
//                            int cost2 = Integer.parseInt(cost1);
//                            cod.setEnabled(cost2 <= 2000);
                            //allTotalPriceTv.setText("₹"+(sum + Double.parseDouble(deliveryFee.replace("₹", ""))));
                            loadShopDetails(sum);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }




    private void loadCartItem() {


        cartItemList = new ArrayList<>();
        //get all products
        //String status = "true";


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("items").orderByChild("shopUid").equalTo(shopUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        cartItemList.clear();
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            ModelCartItem modelCartItem = ds.getValue(ModelCartItem.class);
                            cartItemList.add(modelCartItem);
                        }
                        //setup adapter
                        adapterCartItem = new AdapterCartItem(CartActivity.this, cartItemList,productList);
                        //set adapter
                        cartItemRv.setHasFixedSize(true);
                        cartItemRv.setAdapter(adapterCartItem);
                        //favItemRv.setLayoutManager(new GridLayoutManager(ShopDetailsActivity.this,2,GridLayoutManager.VERTICAL,false));
                        cartItemRv.setLayoutManager(new LinearLayoutManager(CartActivity.this));

                        if (cartItemList.size() != 0){
                            cart.setVisibility(View.GONE);
                        }
                        else{
                            cart.setVisibility(View.VISIBLE);
                            sTotalTv.setText("₹0.00");
                            dFeeTv.setText("₹0");
                            allTotalPriceTv.setText("₹0.00");
                            //addressTv.setVisibility(View.GONE);
                            shopNameTv.setVisibility(View.GONE);
                        }
                        //cart.setVisibility(View.GONE);
//                        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
//                            @Override
//                            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//                                return false;
//                            }
//
//                            @Override
//                            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//                             int position = viewHolder.getAdapterPosition();
//
//                             switch (direction){
//                                 case ItemTouchHelper.LEFT:
//                                     cartItemList.get(position);
//                                     adapterCartItem.notifyItemRemoved(position);
//                                     deleteItem();
//
//                                     break;
//                                 case ItemTouchHelper.RIGHT:
//                                     cartItemList.get(position);
//                                     adapterCartItem.notifyItemRemoved(position);
//                                     deleteItem();
//                                     break;
//                             }
//
//                            }
//                        }).attachToRecyclerView(cartItemRv);


                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

//    public String productId;
    private void deleteItem() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.child(firebaseAuth.getUid()).child("items").removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //product deleted

                        Toast.makeText(CartActivity.this,"Item removed...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed deleting product
                        Toast.makeText(CartActivity.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadShopDetails(int sum) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUid).addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //get shop data
                String name = ""+dataSnapshot.child("name").getValue();
                shopName = ""+dataSnapshot.child("shopName").getValue();
                shopEmail = ""+dataSnapshot.child("email").getValue();
                shopPhone = ""+dataSnapshot.child("phone").getValue();
                shopAddress = ""+dataSnapshot.child("address").getValue();
                shopLatitude = ""+dataSnapshot.child("Latitude").getValue();
                shopLongitude = ""+dataSnapshot.child("Longitude").getValue();
                deliveryFee = ""+dataSnapshot.child("deliveryFee").getValue();
                String profileImage = ""+dataSnapshot.child("profileImage").getValue();
                String shopOpen = ""+dataSnapshot.child("shopOpen").getValue();

                dFeeTv.setText("+  ₹"+deliveryFee);
                allTotalPriceTv.setText("₹"+(sum + Double.parseDouble(deliveryFee.replace("₹", ""))));
                shopNameTv.setText(shopName);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){

            }
        });
    }

    private void loadMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener(){
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                        for(DataSnapshot ds: dataSnapshot.getChildren()){
                            //get user data
                            String name = ""+ds.child("name").getValue();
                            String email = ""+ds.child("email").getValue();
                            myPhone = ""+ds.child("phone").getValue();
                            String profileImage = ""+ds.child("profileImage").getValue();
                            String accountType = ""+ds.child("accountType").getValue();
                            String city = ""+ds.child("city").getValue();
                            myLatitude = ""+ds.child("Latitude").getValue();
                            myLongitude = ""+ds.child("Longitude").getValue();
                            myAddress = ""+ds.child("address").getValue();

                            addressTv.setText(myAddress);

                            //Toast.makeText(ShopDetailsActivity.this, ""+myLatitude+""+myLongitude, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError){

                    }
                });
    }

    private void prepareNotificationMessage(String orderId){
        //when user places order, send notification to seller

        //prepare data for notification
        String NOTIFICATION_TOPIC = "/topics/" + Constants.FCM_TOPIC;
        String NOTIFICATION_TITLE = "New Order" + orderId;
        String NOTIFICATION_MESSAGE = "Congratulations...! You have new order.";
        //String NOTIFICATION_TYPE = "NewOrder";

        //prepare json (what to send and where to send)
        JSONObject notificationJo = new JSONObject();
        JSONObject notificationBodyJo = new JSONObject();
        try {
            //what to send
            notificationBodyJo.put("notificationType", "NewOrder");
            notificationBodyJo.put("buyerUid", firebaseAuth.getUid());
            notificationBodyJo.put("sellerUid", shopUid);
            notificationBodyJo.put("orderId", orderId);
            notificationBodyJo.put("notificationTitle", NOTIFICATION_TITLE);
            notificationBodyJo.put("notificationMessage", NOTIFICATION_MESSAGE);

            //where to send
            notificationJo.put("to",NOTIFICATION_TOPIC);
            notificationJo.put("date",notificationBodyJo);
        }
        catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        sendFcmNotification(notificationJo,orderId);
    }

    private void sendFcmNotification(JSONObject notificationJo, final String orderId) {
        //send volley request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notificationJo, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //after sending fcm start order details activity
                Intent intent = new Intent(CartActivity.this, SuccessActivity.class);
                intent.putExtra("orderTo",shopUid);
                intent.putExtra("orderId",orderId);
                intent.putExtra("orderBy",firebaseAuth.getUid());
                startActivity(intent);
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //if failed sending fcm,still start order details activity
                Intent intent = new Intent(CartActivity.this, SuccessActivity.class);
                intent.putExtra("orderTo",shopUid);
                intent.putExtra("orderId",orderId);
                intent.putExtra("orderBy",firebaseAuth.getUid());
                startActivity(intent);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                //put required headers
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "key=" + Constants.FCM_KEY);
                return headers;
            }
        };

        //enqueue the volley request
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

}