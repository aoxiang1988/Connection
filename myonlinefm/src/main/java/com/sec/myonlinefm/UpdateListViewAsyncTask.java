package com.sec.myonlinefm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sec.myonlinefm.defineview.BitMapCache;
import com.sec.myonlinefm.defineview.FastBlur;

/**
 * Created by Administrator on 2018/5/20.
 */
@SuppressLint("StaticFieldLeak")
public class UpdateListViewAsyncTask extends AsyncTask<String, Bitmap, Bitmap > {

    private ImageView imageView;
    private LinearLayout linearLayout;
    private OnLineFMConnectManager mPlayer;
    private View view;
    private int reqWidth;
    private int reqHeight;
    private Context context;
    private boolean updateImageView = true;
    private boolean isBlurView = false;
    private String key;


    public UpdateListViewAsyncTask(ImageView imageView, String key,
                                   OnLineFMConnectManager mPlayer,
                                   int reqWidth, int reqHeight) {
        super();
        this.imageView = imageView;
        this.key = key;
        this.mPlayer = mPlayer;
        this.reqWidth = reqWidth;
        this.reqHeight = reqHeight;
    }

    UpdateListViewAsyncTask(Context context, String key,
                            LinearLayout linearLayout,
                            OnLineFMConnectManager mPlayer,
                            boolean updateImageView,
                            int reqWidth, int reqHeight) {
        super();
        this.updateImageView = updateImageView;
        this.key = key;
        this.context = context;
        this.linearLayout = linearLayout;
        this.mPlayer = mPlayer;
        this.reqWidth = reqWidth;
        this.reqHeight = reqHeight;
    }

    public UpdateListViewAsyncTask(Context context, String key,
                                   OnLineFMConnectManager mPlayer,
                                   View view,
                                   boolean isBlurView,
                                   int reqWidth, int reqHeight) {
        super();
        this.isBlurView = isBlurView;
        this.key = key;
        this.context = context;
        this.view = view;
        this.mPlayer = mPlayer;
        this.reqWidth = reqWidth;
        this.reqHeight = reqHeight;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        Bitmap bitmap;
        if(BitMapCache.getInstance().getBitmapFromMemCache(key) != null) {
            bitmap = BitMapCache.getInstance().getBitmapFromMemCache(key);
            publishProgress(bitmap);
        }
        else {
            bitmap = mPlayer.getBitmap(strings[0], reqWidth, reqHeight);
            if (bitmap == null) bitmap = getBitmapFromDrawable(context.getDrawable(R.drawable.no_bit));
            BitMapCache.getInstance().addBitmapToMemoryCache(key, bitmap);
            publishProgress(bitmap);
        }
        return bitmap;
    }

    @Override
    protected void onProgressUpdate(Bitmap... values) {
        super.onProgressUpdate(values);
        if (updateImageView && !isBlurView) {
            imageView.setImageBitmap(values[0]);
        }
        if (!updateImageView && !isBlurView)
            linearLayout.setBackground(new BitmapDrawable(context.getResources(),values[0]));
        if(isBlurView)
            blur(values[0],view);
    }

    private void blur(Bitmap bkg, View view) {
        Bitmap overlay = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(overlay);
        canvas.translate(-view.getLeft(), -view.getTop());
        canvas.drawBitmap(scaleBitmap(bkg, view.getMeasuredWidth(), view.getMeasuredHeight()), 0, 0, null);
        overlay = FastBlur.doBlur(overlay, 100, true);
        view.setBackground(new BitmapDrawable(context.getResources(), overlay));
    }
    private Bitmap scaleBitmap(Bitmap origin, int newWidth, int newHeight) {
        if (origin == null) {
            return null;
        }
        int height = origin.getHeight();
        int width = origin.getWidth();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);// 使用后乘
        return Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
    }

    private static final int COLORDRAWABLE_DIMENSION = 1;
    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_4444;

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        try {
            Bitmap bitmap;
            if (drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable) drawable).getBitmap();
            } else if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMENSION, COLORDRAWABLE_DIMENSION, BITMAP_CONFIG);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                        BITMAP_CONFIG);
            }
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

}