package com.sec.myonlinefm.defineview

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.AbsListView
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import com.sec.myonlinefm.R

/**
 * Created by SRC-TJ-MM-BinYang on 2018/4/23.
 */
class RefreshListView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null) : ListView(context, attrs), AbsListView.OnScrollListener {
    /**
     * 头布局
     */
    private var headerView: View? = null

    /**
     * 头部布局的高度
     */
    private var headerViewHeight = 0

    /**
     * 头部旋转的图片
     */
    private var iv_arrow: ImageView? = null
    private var footer_iv_arrow: ImageView? = null

    /**
     * 头部下拉刷新时状态的描述
     */
    private var tv_state: TextView? = null
    private var footer_tv_state: TextView? = null

    /**
     * 底部布局
     */
    private var footerView: View? = null

    /**
     * 底部布局的高度
     */
    private var footerViewHeight = 0
    private var animation: RotateAnimation? = null

    /**
     * 按下时的Y坐标
     */
    private var downY = 0
    private val PULL_REFRESH = 0 //下拉刷新的状态
    private val RELEASE_REFRESH = 1 //松开刷新的状态
    private val REFRESHING = 2 //正在刷新的状态

    /**
     * 当前下拉刷新处于的状态
     */
    private var currentState = PULL_REFRESH

    /**
     * 头部布局在下拉刷新改变时，图标的动画
     */
    private var upAnimation: RotateAnimation? = null
    private var downAnimation: RotateAnimation? = null

    /**
     * 当前是否在加载数据
     */
    private var isLoadingMore = false
    private fun init() {
        //设置滑动监听
        setOnScrollListener(this)
        //初始化头布局
        initHeaderView()
        //初始化头布局中图标的旋转动画
        initRotateAnimation()
        //初始化为尾布局
        initFooterView()
    }

    /**
     * 初始化headerView
     */
    private fun initHeaderView() {
        headerView = inflate(context, R.layout.header, null)
        iv_arrow = headerView!!.findViewById<View?>(R.id.refresh) as ImageView
        tv_state = headerView!!.findViewById<View?>(R.id.header_tv) as TextView

        //测量headView的高度
        headerView!!.measure(0, 0)
        //获取高度，并保存
        headerViewHeight = headerView!!.getMeasuredHeight()
        //设置paddingTop = -headerViewHeight;这样，该控件被隐藏
        headerView!!.setPadding(0, -headerViewHeight, 0, 0)
        //添加头布局
        addHeaderView(headerView)
    }

    /**
     * 初始化旋转动画
     */
    private fun initRotateAnimation() {
        animation = RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        animation!!.setDuration(500)
        animation!!.setRepeatCount(15)
        upAnimation = RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        upAnimation!!.setDuration(300)
        upAnimation!!.setFillAfter(true)
        downAnimation = RotateAnimation(180f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        downAnimation!!.setDuration(300)
        downAnimation!!.setFillAfter(true)
    }

    //初始化底布局，与头布局同理
    private fun initFooterView() {
        footerView = inflate(context, R.layout.header, null)
        footerView!!.measure(0, 0)
        footerViewHeight = footerView!!.getMeasuredHeight()
        footerView!!.setPadding(0, -footerViewHeight, 0, 0)
        footer_iv_arrow = footerView!!.findViewById<View?>(R.id.refresh) as ImageView
        footer_tv_state = footerView!!.findViewById<View?>(R.id.header_tv) as TextView
        footer_tv_state!!.setText("下一页")
        //            footer_iv_arrow.startAnimation(animation);
        addFooterView(footerView)
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        when (ev!!.getAction()) {
            MotionEvent.ACTION_DOWN ->                 //获取按下时y坐标
                downY = ev.getY() as Int
            MotionEvent.ACTION_MOVE -> {
                //如果当前处在滑动状态，则不做处理
                if (currentState == REFRESHING) {
                    return true
                }
                //手指滑动偏移量
                val deltaY = (ev.getY() - downY) as Int

                //获取新的padding值
                val paddingTop = -headerViewHeight + deltaY
                if (paddingTop > -headerViewHeight && firstVisiblePosition == 0) {
                    //向下滑，且处于顶部，设置padding值，该方法实现了顶布局慢慢滑动显现
                    headerView!!.setPadding(0, paddingTop, 0, 0)
                    if (paddingTop >= 0 && currentState == PULL_REFRESH) {
                        //从下拉刷新进入松开刷新状态
                        currentState = RELEASE_REFRESH
                        //刷新头布局
                        refreshHeaderView()
                    } else if (paddingTop < 0 && currentState == RELEASE_REFRESH) {
                        //进入下拉刷新状态
                        currentState = PULL_REFRESH
                        refreshHeaderView()
                    }
                    return true //拦截TouchMove，不让listview处理该次move事件,会造成listview无法滑动
                }
            }
            MotionEvent.ACTION_UP -> if (currentState == PULL_REFRESH) {
                //仍处于下拉刷新状态，未滑动一定距离，不加载数据，隐藏headView
                headerView!!.setPadding(0, -headerViewHeight, 0, 0)
            } else if (currentState == RELEASE_REFRESH) {
                //滑倒一定距离，显示无padding值得headcView
                headerView!!.setPadding(0, 0, 0, 0)
                //设置状态为刷新
                currentState = REFRESHING

                //刷新头部布局
                refreshHeaderView()
                if (listener != null) {
                    //接口回调加载数据
                    listener!!.onPullRefresh()
                }
            }
        }
        return super.onTouchEvent(ev)
    }

    /**
     * 根据currentState来更新headerView
     */
    private fun refreshHeaderView() {
        when (currentState) {
            PULL_REFRESH -> {
                tv_state!!.setText("上一页")
                iv_arrow!!.startAnimation(downAnimation)
            }
            RELEASE_REFRESH -> {
                tv_state!!.setText("松开刷新")
                iv_arrow!!.startAnimation(upAnimation)
            }
            REFRESHING -> {
                iv_arrow!!.clearAnimation() //因为向上的旋转动画有可能没有执行完
                iv_arrow!!.startAnimation(animation)
                tv_state!!.setText("正在刷新上页内容...")
            }
        }
    }

    /**
     * 完成刷新操作，重置状态,在你获取完数据并更新完adater之后，去在UI线程中调用该方法
     */
    fun completeRefresh() {
        iv_arrow!!.clearAnimation()
        footer_iv_arrow!!.clearAnimation()
        if (isLoadingMore) {
            //重置footerView状态
            footerView!!.setPadding(0, -footerViewHeight, 0, 0)
            isLoadingMore = false
        } else {
            //重置headerView状态
            headerView!!.setPadding(0, -headerViewHeight, 0, 0)
            currentState = PULL_REFRESH
            iv_arrow!!.setVisibility(VISIBLE)
            tv_state!!.setText("上一页")
        }
    }

    private var listener: OnRefreshListener? = null
    fun setOnRefreshListener(listener: OnRefreshListener?) {
        this.listener = listener
    }

    interface OnRefreshListener {
        open fun onPullRefresh()
        open fun onLoadingMore()
    }

    /**
     * SCROLL_STATE_IDLE:闲置状态，就是手指松开
     * SCROLL_STATE_TOUCH_SCROLL：手指触摸滑动，就是按着来滑动
     * SCROLL_STATE_FLING：快速滑动后松开
     */
    override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && lastVisiblePosition == count - 1 && !isLoadingMore) {
            isLoadingMore = true
            footerView!!.setPadding(0, 0, 0, 0) //显示出footerView
            setSelection(count) //让listview最后一条显示出来，在页面完全显示出底布局
            if (listener != null) {
                listener!!.onLoadingMore()
            }
        }
    }

    fun startScrollAnim() {
        footer_tv_state!!.setText("正在刷新下页内容...")
        footer_iv_arrow!!.startAnimation(animation)
    }

    override fun onScroll(view: AbsListView?, firstVisibleItem: Int,
                          visibleItemCount: Int, totalItemCount: Int) {
    }

    init {
        init()
    }
}