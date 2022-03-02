package com.sec.myonlinefm.defineview

import com.sec.myonlinefm.OnLineFMConnectManager.Companion.mMainInfoCode
import com.sec.myonlinefm.OnLineFMConnectManager.convertToMhz
import com.sec.myonlinefm.OnLineFMConnectManager.getTime
import com.sec.myonlinefm.OnLineFMConnectManager.getCurrentTime
import com.sec.myonlinefm.OnLineFMConnectManager.startGetOnLineInfo
import com.sec.myonlinefm.OnLineFMConnectManager.Companion.mGPS_Name
import com.sec.myonlinefm.OnLineFMConnectManager.getSearchResult
import com.sec.myonlinefm.OnLineFMConnectManager.getWaPiDataList
import com.sec.myonlinefm.OnLineFMConnectManager.getFiveRecmThumb
import com.sec.myonlinefm.OnLineFMConnectManager.getRequestRecmmendProgram
import com.sec.myonlinefm.classificationprogram.data.ClassifyRecommend.getCategoryID
import com.sec.myonlinefm.classificationprogram.data.ClassifyRecommend.getThumbUrl
import com.sec.myonlinefm.OnLineFMConnectManager.getRecommendThumb
import com.sec.myonlinefm.classificationprogram.data.ClassifyRecommend.setThumb
import com.sec.myonlinefm.OnLineFMConnectManager.getRequestProgramClassify
import com.sec.myonlinefm.classificationprogram.data.ClassifyRecommend.getThumb
import com.sec.myonlinefm.classificationprogram.data.ClassifyRecommend.getId
import com.sec.myonlinefm.classificationprogram.data.ClassifyRecommend.getTitle
import com.sec.myonlinefm.classificationprogram.data.ChannelProgram.getChannelID
import com.sec.myonlinefm.classificationprogram.data.ChannelProgram.getId
import com.sec.myonlinefm.OnLineFMConnectManager.getCurrentDemandChannelPrograms
import com.sec.myonlinefm.classificationprogram.data.ChannelProgramPattern.getChannelProgramList
import com.sec.myonlinefm.classificationprogram.data.ChannelProgramPattern.getTotal
import com.sec.myonlinefm.classificationprogram.data.ChannelProgram.getTitle
import com.sec.myonlinefm.classificationprogram.data.ChannelProgram.getUpdateTime
import com.sec.myonlinefm.classificationprogram.data.ChannelProgram.getDuration
import com.sec.myonlinefm.OnLineFMConnectManager.getClassifyInfoContext
import com.sec.myonlinefm.OnLineFMConnectManager.getClassificationAttribute
import com.sec.myonlinefm.OnLineFMConnectManager.getCurrentDemandChannel
import com.sec.myonlinefm.OnLineFMConnectManager.getReplayUrl
import com.sec.myonlinefm.OnLineFMConnectManager.getOneDayProgram
import com.sec.myonlinefm.OnLineFMConnectManager.getOnLineStationMap
import com.sec.myonlinefm.OnLineFMConnectManager.getOnLineStations
import com.sec.myonlinefm.OnLineFMConnectManager.getOnLineStationProgramMap
import com.sec.myonlinefm.OnLineFMConnectManager.getOnLineCenterStations
import com.sec.myonlinefm.OnLineFMConnectManager.getOnLineStationProgramCentermap
import com.sec.myonlinefm.classificationprogram.data.ClassifyRecommend.setCategoryID
import com.sec.myonlinefm.classificationprogram.data.ClassifyRecommend.setId
import com.sec.myonlinefm.classificationprogram.data.ClassifyRecommend.setTitle
import com.sec.myonlinefm.classificationprogram.data.ClassifyRecommend.setThumbUrl
import com.sec.myonlinefm.OnLineFMConnectManager.getBitmap
import com.sec.myonlinefm.classificationprogram.data.ChannelProgramPattern.setTotal
import com.sec.myonlinefm.classificationprogram.data.ChannelProgram.setDescription
import com.sec.myonlinefm.classificationprogram.data.ChannelProgram.setDuration
import com.sec.myonlinefm.classificationprogram.data.ChannelProgram.setId
import com.sec.myonlinefm.classificationprogram.data.ChannelProgram.setTitle
import com.sec.myonlinefm.classificationprogram.data.ChannelProgram.setType
import com.sec.myonlinefm.classificationprogram.data.ChannelProgram.setUpdateTime
import com.sec.myonlinefm.classificationprogram.data.ChannelProgram.setMediaInfo
import com.sec.myonlinefm.classificationprogram.data.ChannelProgram.getMediaInfo
import com.sec.myonlinefm.classificationprogram.data.ChannelProgram.ProgramMediaInfo.setId
import com.sec.myonlinefm.classificationprogram.data.ChannelProgram.ProgramMediaInfo.setDuration
import com.sec.myonlinefm.classificationprogram.data.ChannelProgram.ProgramMediaInfo.setBitrateUrl
import com.sec.myonlinefm.classificationprogram.data.ChannelProgram.ProgramMediaInfo.BitrateUrl.setBitrate
import com.sec.myonlinefm.classificationprogram.data.ChannelProgram.ProgramMediaInfo.BitrateUrl.setFilePath
import com.sec.myonlinefm.classificationprogram.data.ChannelProgram.ProgramMediaInfo.BitrateUrl.setQetag
import com.sec.myonlinefm.classificationprogram.data.ChannelProgram.ProgramMediaInfo.getBitrateUrlList
import com.sec.myonlinefm.classificationprogram.data.ChannelProgramPattern.setChannelProgramList
import com.sec.myonlinefm.OnLineFMConnectManager.Companion.isConnectNet
import com.sec.myonlinefm.OnLineFMConnectManager.getWaPiDataListAsyncEx
import com.sec.myonlinefm.OnLineFMConnectManager.getRecommendsDataListAsyncEx
import com.sec.myonlinefm.OnLineFMConnectManager.getCurrentDemandChannelProgramsAsyncEx
import com.sec.myonlinefm.OnLineFMConnectManager.getCurrentDemandChannelAsyncEx
import com.sec.myonlinefm.OnLineFMConnectManager.getClassificationAttributeAsyncEx
import com.sec.myonlinefm.OnLineFMConnectManager.getDemandChannelContextAsyncEx
import com.sec.myonlinefm.OnLineFMConnectManager.getRequestProgramClassifyAsyncEx
import com.sec.myonlinefm.OnLineFMConnectManager.getOnlineInfoAsyncEx
import com.sec.myonlinefm.OnLineFMConnectManager.getDifferentLocalOnlineInfoAsyncEx
import com.sec.myonlinefm.OnLineFMConnectManager.getReplayUrlAsyncEx
import com.sec.myonlinefm.OnLineFMConnectManager.getSearchResultAsyncEx
import com.sec.myonlinefm.OnLineFMConnectManager.getOneDayProgramAsyncEx
import com.sec.myonlinefm.OnLineFMConnectManager.getRecommendAsyncEx
import com.sec.myonlinefm.OnLineFMConnectManager.getThumbAsyncEx
import com.sec.myonlinefm.OnLineFMConnectManager.getFiveRecmAsyncEx
import com.sec.myonlinefm.MainActivity.checkLocationPermission
import com.sec.myonlinefm.MainActivity.unStartDNSPlay
import com.sec.myonlinefm.MainActivity.StartDNSPlay
import com.sec.myonlinefm.OnLineFMConnectManager.getLocalInfo
import com.sec.myonlinefm.OnLineFMConnectManager.setChangedByUser
import com.sec.myonlinefm.OnLineFMConnectManager.setDifferentLocalID
import com.sec.myonlinefm.OnLineFMConnectManager.Companion.mLocal_ID
import com.sec.myonlinefm.OnLineFMConnectManager.Companion.changedByUser
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteDatabase.CursorFactory
import com.sec.myonlinefm.dbdata.MySQLHelper
import android.database.DatabaseErrorHandler
import android.database.sqlite.SQLiteDatabase
import kotlin.Throws
import org.xmlpull.v1.XmlPullParser
import android.util.Xml
import com.sec.myonlinefm.defineview.BitMapCache
import kotlin.jvm.Synchronized
import android.content.res.TypedArray
import com.sec.myonlinefm.R
import android.graphics.drawable.Drawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import com.sec.myonlinefm.defineview.RoundImageView
import android.annotation.SuppressLint
import kotlin.jvm.JvmOverloads
import android.widget.AbsListView
import android.widget.TextView
import android.view.animation.RotateAnimation
import android.view.animation.Animation
import android.view.MotionEvent
import android.widget.GridView
import android.view.View.MeasureSpec
import org.apache.http.message.BasicNameValuePair
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.params.CoreConnectionPNames
import org.apache.http.util.EntityUtils
import org.json.JSONObject
import org.json.JSONException
import com.sec.myonlinefm.OnLineFMConnectManager
import com.sec.myonlinefm.onlineSearchUI.SearchResultListFragment.SearchListAdapter
import android.os.Bundle
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.sec.myonlinefm.UpdateListViewAsyncTask
import com.sec.myonlinefm.abstructObserver.OnLineInfo
import com.sec.myonlinefm.updataUIListener.ObserverUIListenerManager
import com.sec.myonlinefm.onlineSearchUI.SearchResultListFragment
import org.json.JSONArray
import android.location.LocationManager
import android.location.LocationListener
import android.location.LocationProvider
import android.content.pm.PackageManager
import android.widget.Toast
import android.location.Geocoder
import com.sec.myonlinefm.updataUIListener.ObserverUIListener
import com.sec.myonlinefm.updataUIListener.SubjectUIListener
import com.sec.myonlinefm.onlineinfolistener.ObserverListener
import com.sec.myonlinefm.onlineinfolistener.SubjectListener
import com.sec.myonlinefm.onlineinfolistener.ObserverListenerManager
import android.telephony.TelephonyManager
import com.sec.myonlinefm.NewHttpConnect
import android.widget.TabHost
import com.sec.myonlinefm.SearchActivity
import android.widget.TabWidget
import android.widget.TabHost.TabSpec
import android.widget.TabHost.TabContentFactory
import android.widget.TabHost.OnTabChangeListener
import com.sec.myonlinefm.classificationprogram.data.DemandChannel.PurchaseItem
import com.sec.myonlinefm.classificationprogram.data.DemandChannel.PodCasters
import com.sec.myonlinefm.classificationprogram.data.DemandChannel.AuthorsBroadcasters
import com.sec.myonlinefm.classificationprogram.data.RecommendsData.Recommends
import com.sec.myonlinefm.classificationprogram.data.RecommendsData.Recommends.ParentInfo
import com.sec.myonlinefm.classificationprogram.data.RecommendsData.Recommends.ParentInfo.ParentExtra
import com.sec.myonlinefm.classificationprogram.data.WaPiData
import com.sec.myonlinefm.classificationprogram.data.DemandChannel
import com.sec.myonlinefm.classificationprogram.data.ObservableController
import com.sec.myonlinefm.classificationprogram.data.RecommendsData
import com.sec.myonlinefm.classificationprogram.data.ClassifyRecommend
import com.sec.myonlinefm.classificationprogram.data.ClassifyRecommendPattern
import kotlin.jvm.Volatile
import com.sec.myonlinefm.classificationprogram.data.RequestProgramClassify
import com.sec.myonlinefm.classificationprogram.data.RequestProgramClassifyListPattern
import com.sec.myonlinefm.classificationprogram.fragment.LiveFragment
import com.sec.myonlinefm.classificationprogram.fragment.BoutiqueFragment.RankingAdapter
import android.widget.ExpandableListView
import com.sec.myonlinefm.classificationprogram.fragment.BoutiqueFragment
import com.sec.myonlinefm.classificationprogram.data.WaPiDataPattern
import android.widget.BaseExpandableListAdapter
import com.sec.myonlinefm.classificationprogram.fragment.BoutiqueFragment.ChildViewHolder
import android.widget.ExpandableListView.OnGroupClickListener
import android.widget.ExpandableListView.OnChildClickListener
import com.sec.myonlinefm.classificationprogram.ChannelProgramActivity
import com.sec.myonlinefm.classificationprogram.fragment.ClassifyFragment
import com.sec.myonlinefm.classificationprogram.InfoContextActivity
import com.sec.myonlinefm.classificationprogram.fragment.RadioLiveFragment
import android.widget.LinearLayout
import com.sec.myonlinefm.OnLineStationsActivity
import androidx.viewpager.widget.ViewPager
import com.sec.myonlinefm.classificationprogram.fragment.RecommendFragment.RecommendAdapter
import androidx.viewpager.widget.PagerAdapter
import com.sec.myonlinefm.classificationprogram.fragment.RecommendFragment
import com.sec.myonlinefm.classificationprogram.fragment.RecommendFragment.TimerRunnable
import com.sec.myonlinefm.classificationprogram.fragment.RecommendFragment.CategoryTag
import com.sec.myonlinefm.classificationprogram.fragment.RecommendFragment.RadioIdTag
import com.sec.myonlinefm.classificationprogram.fragment.RecommendFragment.MyPagerAdapter
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.sec.myonlinefm.classificationprogram.data.ChannelProgram
import com.sec.myonlinefm.classificationprogram.fragment.ChannelProgramListFragment.DemandProgramAdapter
import com.sec.myonlinefm.defineview.RefreshListView
import com.sec.myonlinefm.classificationprogram.fragment.ChannelProgramListFragment
import com.sec.myonlinefm.classificationprogram.data.ChannelProgramPattern
import android.os.Build
import com.sec.myonlinefm.classificationprogram.fragment.ChannelProgramListFragment.PageGridAdapter
import android.view.Gravity
import android.view.WindowManager
import com.sec.myonlinefm.classificationprogram.fragment.ChannelRecommendListFragment
import com.sec.myonlinefm.classificationprogram.data.DemandChannelPattern
import com.sec.myonlinefm.classificationprogram.mainfacefragment.OnLineFavFragment
import com.sec.myonlinefm.classificationprogram.data.FavData
import com.sec.myonlinefm.classificationprogram.mainfacefragment.OnLineHomeFragment
import com.sec.myonlinefm.classificationprogram.InfoContextActivity.DemandChannelViewHolder
import android.widget.ScrollView
import com.sec.myonlinefm.classificationprogram.InfoContextActivity.SpinnerListAdapter
import com.sec.myonlinefm.defineview.NoScrollBarGridView
import com.sec.myonlinefm.classificationprogram.InfoContextActivity.ClassifyViewHolder
import com.sec.myonlinefm.data.ClassificationAttributePattern
import com.sec.myonlinefm.classificationprogram.RequestProgramClassifyActivity
import com.sec.myonlinefm.OnLineFMPlayerService
import android.os.IBinder
import android.os.Build.VERSION_CODES
import com.sec.myonlinefm.FMProgramActivity.ProgramList
import android.widget.ImageButton
import android.widget.SeekBar
import com.sec.myonlinefm.FMProgramActivity
import com.sec.myonlinefm.RadioDialogFragment
import com.sec.myonlinefm.classificationprogram.data.ChannelProgram.ProgramMediaInfo
import com.sec.myonlinefm.classificationprogram.data.ChannelProgram.ProgramMediaInfo.BitrateUrl
import com.sec.myonlinefm.classificationprogram.data.RecommendsDataPattern
import android.app.Activity
import android.content.*
import android.graphics.*
import com.sec.myonlinefm.RadioDialogFragment.OnLineLocalNames
import android.media.MediaPlayer
import android.media.AudioManager
import android.media.MediaPlayer.OnPreparedListener
import android.media.AudioManager.OnAudioFocusChangeListener
import android.widget.RelativeLayout
import com.sec.myonlinefm.OnLineStationsActivity.OnLineStationAdapter
import android.os.AsyncTask
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.sec.myonlinefm.defineview.FastBlur

/**
 * Created by SRC-TJ-MM-BinYang on 2018/4/27.
 */
class RoundImageView : AppCompatImageView {
    // 图片的宽高
    private var width = 0
    private var height = 0

    // 圆角半径
    private var radius = 0

    // 图片类型（矩形，圆形）
    private var shapeType = 0

    constructor(context: Context?) : super(context) {
        init(context, null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context?, attrs: AttributeSet?) {
        //初始化默认值
        borderWidth = 6
        borderColor = 0xEE2C2C
        radius = 16
        shapeType = 0
        // 获取控件的属性值
        if (attrs != null) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.MLImageView)
            borderWidth = array.getDimensionPixelOffset(R.styleable.MLImageView_ml_border_width, borderWidth)
            borderColor = array.getColor(R.styleable.MLImageView_ml_border_color, borderColor)
            radius = array.getDimensionPixelOffset(R.styleable.MLImageView_ml_radius, radius)
            shapeType = array.getInteger(R.styleable.MLImageView_ml_shape_type, shapeType)
            array.recycle()
        }
        isClickable = true
        isDrawingCacheEnabled = true
        setWillNotDraw(false)
    }

    /**
     * 重写父类的 onSizeChanged 方法，检测控件宽高的变化
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        width = w
        height = h
    }

    override fun onDraw(canvas: Canvas?) {
        if (shapeType == 0) {
            super.onDraw(canvas)
            return
        }
        // 获取当前控件的 drawable
        val drawable = drawable ?: return
        // 这里 get 回来的宽度和高度是当前控件相对应的宽度和高度（在 xml 设置）
        if (getWidth() == 0 || getHeight() == 0) {
            return
        }
        // 获取 bitmap，即传入 imageview 的 bitmap
        // Bitmap bitmap = ((BitmapDrawable) ((SquaringDrawable)
        // drawable).getCurrent()).getBitmap();
        // 这里参考赵鹏的获取 bitmap 方式，因为上边的获取会导致 Glide 加载的drawable 强转为 BitmapDrawable 出错
        val bitmap = getBitmapFromDrawable(drawable)
        drawDrawable(canvas, bitmap)
        //        drawPress(canvas);
//        drawBorder(canvas);
    }

    private fun getBitmapFromDrawable(drawable: Drawable?): Bitmap? {
        return try {
            val bitmap: Bitmap?
            bitmap = if (drawable is BitmapDrawable) {
                return (drawable as BitmapDrawable?).getBitmap()
            } else if (drawable is ColorDrawable) {
                Bitmap.createBitmap(COLOR_DRAWABLE_DIMENSION, COLOR_DRAWABLE_DIMENSION, BITMAP_CONFIG)
            } else {
                Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
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

    /**
     * 实现圆角的绘制
     *
     * @param canvas
     * @param bitmap
     */
    @SuppressLint("WrongConstant")
    private fun drawDrawable(canvas: Canvas?, bitmap: Bitmap?) {
        // 画笔
        var bitmap = bitmap
        val paint = Paint()
        // 颜色设置
        paint.color = -0x1
        //        paint.setStrokeWidth(StrokeWidth);
        paint.style = Paint.Style.FILL
        // 抗锯齿
        paint.isAntiAlias = true
        //Paint 的 Xfermode，PorterDuff.Mode.SRC_IN 取两层图像的交集部门, 只显示上层图像。
        val xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        // 标志
        val saveFlags = Canvas.ALL_SAVE_FLAG
        canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null, saveFlags)
        if (shapeType == 1) {
            // 画遮罩，画出来就是一个和空间大小相匹配的圆（这里在半径上 -1 是为了不让图片超出边框）
            canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), (width / 2 - 1).toFloat(), paint)
        } else if (shapeType == 2) {
            // 当ShapeType == 2 时 图片为圆角矩形 （这里在宽高上 -1 是为了不让图片超出边框）
            val rectf = RectF(1, 1, getWidth() - 1, getHeight() - 1)
            canvas.drawRoundRect(rectf, (radius + 1).toFloat(), (radius + 1).toFloat(), paint)
        }
        paint.xfermode = xfermode
        // 空间的大小 / bitmap 的大小 = bitmap 缩放的倍数
        val scaleWidth = getWidth() as Float / bitmap.getWidth()
        val scaleHeight = getHeight() as Float / bitmap.getHeight()
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        //bitmap 缩放
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true)
        //draw 上去
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        canvas.restore()
    }

    /**
     * 绘制自定义控件边框
     * @param canvas
     */
    // 边框宽度
    private var borderWidth = 0

    // 边框颜色
    private var borderColor = 0
    private fun drawBorder(canvas: Canvas?) {
        if (borderWidth > 0) {
            val paint = Paint()
            paint.strokeWidth = borderWidth.toFloat()
            paint.style = Paint.Style.STROKE
            paint.color = borderColor
            paint.isAntiAlias = true
            // 根据控件类型的属性去绘制圆形或者矩形
            if (shapeType == 1) {
                canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), ((width - borderWidth) / 2).toFloat(), paint)
                canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), ((width - borderWidth) / 5).toFloat(), paint)
            } else if (shapeType == 2) {
                // 当ShapeType = 1 时 图片为圆角矩形
                val rectf = RectF(borderWidth / 2, borderWidth / 2, getWidth() - borderWidth / 2,
                        getHeight() - borderWidth / 2)
                canvas.drawRoundRect(rectf, radius.toFloat(), radius.toFloat(), paint)
            }
        }
    }

    /**
     * 设置边框颜色
     *
     * @param borderColor
     */
    fun setBorderColor(borderColor: Int) {
        this.borderColor = borderColor
        invalidate()
    }

    /**
     * 设置边框宽度
     *
     * @param borderWidth
     */
    fun setBorderWidth(borderWidth: Int) {
        this.borderWidth = borderWidth
    }

    /**
     * 设置倒角半径
     *
     * @param radius
     */
    fun setRadius(radius: Int) {
        this.radius = radius
        invalidate()
    }

    /**
     * 设置形状类型
     * @param shapeType
     */
    fun setShapeType(shapeType: Int) {
        this.shapeType = shapeType
        invalidate()
    }

    companion object {
        // 定义 Bitmap 的默认配置
        private val BITMAP_CONFIG: Bitmap.Config? = Bitmap.Config.ARGB_8888
        private const val COLOR_DRAWABLE_DIMENSION = 1
    }
}