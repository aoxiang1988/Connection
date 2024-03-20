package com.dten.testview;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private static boolean ifHasChangeView = false;
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView imageView = findViewById(R.id.image_view);
        imageView.setClickable(true);
        imageView.setOnClickListener(view -> {
            if (ifHasChangeView) {
                imageView.setBackground(getResources().getDrawable(R.color.white, null));
                ifHasChangeView = false;
            } else {
                imageView.setBackground(getResources().getDrawable(R.color.black, null));
                ifHasChangeView = true;
            }
        });
    }
}