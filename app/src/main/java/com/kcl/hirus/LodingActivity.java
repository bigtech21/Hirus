package com.kcl.hirus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

public class LodingActivity extends AppCompatActivity {
    ImageView um;
    BitmapDrawable bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loding);
        startLoding();

        um = findViewById(R.id.UmLoding);

        Resources res = getResources();
        bitmap = (BitmapDrawable)res.getDrawable(R.drawable.um);
        int bitmapWidth = bitmap.getIntrinsicWidth();
        int bitmapHeight = bitmap.getIntrinsicHeight();

        um.setImageDrawable(bitmap);
        um.getLayoutParams().width = bitmapWidth;
        um.getLayoutParams().height = bitmapHeight;

    }

    private void startLoding() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                Log.d("load","메인화면");
                finish();
            }
        }, 2000);
    }
}