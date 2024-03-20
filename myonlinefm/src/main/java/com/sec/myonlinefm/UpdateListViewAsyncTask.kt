package com.sec.myonlinefm

import android.content.*
import android.graphics.*
import android.view.*
import android.widget.*

import com.sec.myonlinefm.defineview.BitMapCache

import android.graphics.drawable.Drawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.annotation.SuppressLint

import android.os.AsyncTask
import com.sec.myonlinefm.defineview.FastBlur

/**
 * Created by Administrator on 2018/5/20.
 */
@SuppressLint("StaticFieldLeak")
class UpdateListViewAsyncTask : AsyncTask<String?, Bitmap?, Bitmap?> {
    private var imageView: ImageView? = null
    private var linearLayout: LinearLayout? = null
    private var mPlayer: OnLineFMConnectManager?
    private var view: View? = null
    private var reqWidth: Int
    private var reqHeight: Int
    private var context: Context? = null
    private var updateImageView = true
    private var isBlurView = false
    private var key: String?

    constructor(imageView: ImageView?, key: String?,
                mPlayer: OnLineFMConnectManager?,
                reqWidth: Int, reqHeight: Int) : super() {
        this.imageView = imageView
        this.key = key
        this.mPlayer = mPlayer
        this.reqWidth = reqWidth
        this.reqHeight = reqHeight
    }

    internal constructor(context: Context?, key: String?,
                         linearLayout: LinearLayout?,
                         mPlayer: OnLineFMConnectManager?,
                         updateImageView: Boolean,
                         reqWidth: Int, reqHeight: Int) : super() {
        this.updateImageView = updateImageView
        this.key = key
        this.context = context
        this.linearLayout = linearLayout
        this.mPlayer = mPlayer
        this.reqWidth = reqWidth
        this.reqHeight = reqHeight
    }

    constructor(context: Context?, key: String?,
                mPlayer: OnLineFMConnectManager?,
                view: View?,
                isBlurView: Boolean,
                reqWidth: Int, reqHeight: Int) : super() {
        this.isBlurView = isBlurView
        this.key = key
        this.context = context
        this.view = view
        this.mPlayer = mPlayer
        this.reqWidth = reqWidth
        this.reqHeight = reqHeight
    }

    override fun doInBackground(vararg strings: String?): Bitmap? {
        var bitmap: Bitmap?
        if (BitMapCache.getInstance()!!.getBitmapFromMemCache(key) != null) {
            bitmap = BitMapCache.getInstance()!!.getBitmapFromMemCache(key)
            publishProgress(bitmap)
        } else {
            bitmap = mPlayer!!.getBitmap(strings[0], reqWidth, reqHeight)
            if (bitmap == null) bitmap = getBitmapFromDrawable(context!!.getDrawable(R.drawable.no_bit))
            BitMapCache.getInstance()!!.addBitmapToMemoryCache(key, bitmap)
            publishProgress(bitmap)
        }
        return bitmap
    }

    override fun onProgressUpdate(vararg values: Bitmap?) {
        super.onProgressUpdate(*values)
        if (updateImageView && !isBlurView) {
            imageView!!.setImageBitmap(values[0])
        }
        if (!updateImageView && !isBlurView) linearLayout!!.setBackground(BitmapDrawable(context!!.getResources(), values[0]))
        if (isBlurView) blur(values[0], view)
    }

    private fun blur(bkg: Bitmap?, view: View?) {
        var overlay = Bitmap.createBitmap(view!!.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_4444)
        val canvas = Canvas(overlay)
        canvas.translate(-view.getLeft().toFloat(), -view.getTop().toFloat())
        canvas.drawBitmap(scaleBitmap(bkg, view.getMeasuredWidth(), view.getMeasuredHeight())!!, 0f, 0f, null)
        overlay = FastBlur.doBlur(overlay, 100, true)
        view.setBackground(BitmapDrawable(context!!.getResources(), overlay))
    }

    private fun scaleBitmap(origin: Bitmap?, newWidth: Int, newHeight: Int): Bitmap? {
        if (origin == null) {
            return null
        }
        val height = origin.height
        val width = origin.width
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight) // 使用后乘
        return Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false)
    }

    private fun getBitmapFromDrawable(drawable: Drawable?): Bitmap? {
        return try {
            val bitmap: Bitmap?
            bitmap = if (drawable is BitmapDrawable) {
                return (drawable as BitmapDrawable?)!!.getBitmap()
            } else if (drawable is ColorDrawable) {
                Bitmap.createBitmap(COLORDRAWABLE_DIMENSION, COLORDRAWABLE_DIMENSION, BITMAP_CONFIG)
            } else {
                Bitmap.createBitmap(drawable!!.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                        BITMAP_CONFIG)
            }
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            null
        }
    }

    companion object {
        private const val COLORDRAWABLE_DIMENSION = 1
        private val BITMAP_CONFIG: Bitmap.Config = Bitmap.Config.ARGB_4444
    }
}