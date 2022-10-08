package com.example.joy.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.joy.R;

public class SuccessActivity extends AppCompatActivity {

    private ImageView done;
    private TextView textView2;

    String shopUid,timestamp,orderBy;

    AnimatedVectorDrawableCompat avd;
    AnimatedVectorDrawable avd2;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        shopUid = getIntent().getStringExtra("orderTo");
        timestamp = getIntent().getStringExtra("orderId");
        orderBy = getIntent().getStringExtra("orderBy");

        done = findViewById(R.id.done);
        textView2 = findViewById(R.id.textView2);

        Drawable drawable = done.getDrawable();

        if (drawable instanceof AnimatedVectorDrawableCompat){
            avd = (AnimatedVectorDrawableCompat) drawable;
            avd.start();
        } else if(drawable instanceof AnimatedVectorDrawable) {
            avd2 = (AnimatedVectorDrawable) drawable;
            avd2.start();
        }

        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SuccessActivity.this, OrderDetailsUserActivity.class);
                intent.putExtra("orderTo",shopUid);
                intent.putExtra("orderId",timestamp);
                intent.putExtra("orderBy",orderBy);
                startActivity(intent);
            }
        });

    }
}