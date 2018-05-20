package com.sec.myonlinefm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

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


    public UpdateListViewAsyncTask(ImageView imageView,
                                   OnLineFMConnectManager mPlayer,
                                   int reqWidth, int reqHeight) {
        super();
        this.imageView = imageView;
        this.mPlayer = mPlayer;
        this.reqWidth = reqWidth;
        this.reqHeight = reqHeight;
    }

    public UpdateListViewAsyncTask(Context context,
                                   LinearLayout linearLayout,
                                   OnLineFMConnectManager mPlayer,
                                   boolean updateImageView,
                                   int reqWidth, int reqHeight) {
        super();
        this.updateImageView = updateImageView;
        this.context = context;
        this.linearLayout = linearLayout;
        this.mPlayer = mPlayer;
        this.reqWidth = reqWidth;
        this.reqHeight = reqHeight;
    }

    public UpdateListViewAsyncTask(Context context,
                                   OnLineFMConnectManager mPlayer,
                                   View view,
                                   boolean isBlurView,
                                   int reqWidth, int reqHeight) {
        super();
        this.isBlurView = isBlurView;
        this.context = context;
        this.view = view;
        this.mPlayer = mPlayer;
        this.reqWidth = reqWidth;
        this.reqHeight = reqHeight;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        Bitmap bitmap = mPlayer.getBitmap(strings[0], reqWidth, reqHeight);
        publishProgress(bitmap);
        return bitmap;
    }

    @Override
    protected void onProgressUpdate(Bitmap... values) {
        super.onProgressUpdate(values);
        if (updateImageView && !isBlurView)
            imageView.setImageBitmap(values[0]);
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
}