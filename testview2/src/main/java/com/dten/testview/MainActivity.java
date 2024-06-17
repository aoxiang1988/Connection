package com.dten.testview;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dten.testview.placeholder.PlaceholderContent;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static boolean ifHasChangeView = false;
    private static final int PERMISSION_REQUEST_CODE = 1;

    public static final List<PlaceholderContent.PlaceholderItem> PICTURE_ITEMS = new ArrayList<PlaceholderContent.PlaceholderItem>();
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        } else {
            readExternalFile("picture1.png");
        }

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
        imageView.setOnLongClickListener(v -> {
            startFragment(savedInstanceState);
            return true;
        });
    }

    void startFragment (Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, PictureItemFragment.newInstance(1))
                    .commitNow();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                readExternalFile("picture2.png");
            }
        }
    }

    private void readExternalFile(String name) {
        // 假设文件在外部存储的根目录下，文件名为example.txt
        File file = new File(Environment.getExternalStorageDirectory(), name);
        try (FileInputStream fis = new FileInputStream(file)) {
            // 读取文件内容
            while (fis.read() != -1) {
                // 处理读取的内容
                // ...
                //Bitmap bitmap = getBitmapFromExternalStorage(file.getPath());
                PICTURE_ITEMS.add(new PlaceholderContent.PlaceholderItem(file.getName(), file.getParent(), file.getPath()));
            }
        } catch (IOException e) {
            e.printStackTrace();
            // 处理异常
            // ...
        }
    }

    public Bitmap getBitmapFromExternalStorage(String imagePath) {
        try {
            File imageFile = new File(imagePath);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            return BitmapFactory.decodeStream(new FileInputStream(imageFile), null, options);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}