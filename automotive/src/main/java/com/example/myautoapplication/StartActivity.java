package com.example.myautoapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        if (ToolUtils.checkPermission(this)) {
            startActivity(new Intent(StartActivity.this, MainActivity.class));
        } else {
            finish();
        }
    }
}