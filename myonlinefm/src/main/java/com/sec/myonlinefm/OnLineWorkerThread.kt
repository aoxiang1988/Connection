package com.sec.myonlinefmimport

import android.content.*
import android.util.Log
import com.sec.myonlinefm.abstructObserver.RequestCallBack
import java.lang.RuntimeException
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

import com.sec.myonlinefm.OnLineFMConnectManager.Companion.isConnectNet
import android.graphics.Bitmap
import com.sec.myonlinefm.OnLineFMConnectManager
import android.widget.Toast
import com.sec.myonlinefm.classificationprogram.data.ClassifyRecommend
import com.sec.myonlinefm.classificationprogram.data.RequestProgramClassifyListPattern
import com.sec.myonlinefm.classificationprogram.data.WaPiDataPattern
import com.sec.myonlinefm.classificationprogram.data.ChannelProgramPattern
import com.sec.myonlinefm.data.ClassificationAttributePattern
import com.sec.myonlinefm.classificationprogram.data.RecommendsDataPattern
import com.sec.myonlinefm.classificationprogram.dataimport.DemandChannelPattern

/**
 * Created by SRC-TJ-MM-BinYang on 2018/3/8.
 * this is the thread pool, we can request an new thread runnable to do the work which need more time,
 * I do the connect net work and Json resolution here
 * also we can do more work thread here...
 */
class OnLineWorkerThread<T>(context: Context?) {
    private val mExecutorService: ExecutorService?
    private val player: OnLineFMConnectManager? = OnLineFMConnectManager.mMainInfoCode
    fun SendNewRunnable(type: Int) {
        mExecutorService!!.execute(OnLineWorkerRunnable(type))
    }

       /*public synchronized void SendNewRunnable(int type, RequestCallBack<RequestProgramClassifyListPattern> callBack) {
        mExecutorService.execute(new OnLineWorkerRunnable(type, callBack));
    }*/
    fun SendNewRunnable(type: Int, localId: Int, current_page: Int) {
        mExecutorService!!.execute(OnLineWorkerRunnable(type, localId, current_page, null))
    }

    fun SendNewRunnable(type: Int, keyword: String?, string_type: String?, current_page: Int) {
        mExecutorService!!.execute(OnLineWorkerRunnable(type, keyword, string_type, current_page, null))
    }

    @Synchronized
    fun SendNewRunnable(type: Int, requestCallBack: RequestCallBack<*>?, channelID: Int, current_page: Int) {
        if (current_page != -1)
            mExecutorService!!.execute(
                    OnLineWorkerRunnable(type, channelID, current_page, requestCallBack)
            )
        else
            mExecutorService!!.execute(OnLineWorkerRunnable(type, channelID, requestCallBack))
    }

    fun SendNewRunnable(type: Int,
                        callBack: RequestCallBack<RequestProgramClassifyListPattern?>) {
        mExecutorService!!.execute(OnLineWorkerRunnable(type, callBack as RequestCallBack<T?>))
    }

    fun SendNewRunnable(type: Int, callBack: RequestCallBack<ClassifyRecommend?>, category_id: Int) {
        mExecutorService!!.execute(OnLineWorkerRunnable(type, callBack as RequestCallBack<T?>, category_id))
    }

    fun SendNewRunnableWaPi(type: Int, callBack: RequestCallBack<WaPiDataPattern?>, category_id: Int) {
        mExecutorService!!.execute(OnLineWorkerRunnable(type, callBack as RequestCallBack<T?>, category_id))
    }

    fun SendNewRunnable(type: Int, callBack: RequestCallBack<Bitmap?>, url: String) {
        mExecutorService!!.execute(OnLineWorkerRunnable(type, callBack as RequestCallBack<T?>, url))
    }

    fun SendNewRunnableRecommends(type: Int, callBack: RequestCallBack<RecommendsDataPattern>, category_id: Int) {
        mExecutorService!!.execute(OnLineWorkerRunnable(type, callBack as RequestCallBack<T?>, category_id))
    }

    private inner class OnLineWorkerRunnable : Runnable {
        private var mType: Int
        private var mKeyword: String? = null
        private var mString_Type: String? = null
        private var mLocalId = 0
        private var mCurrentPage = 1

        //   private RequestCallBack mCallBack;
        private var mUrl: String? = null
        private var mCategory_id = 0
        private var mCallBack: RequestCallBack<T?>? = null

        constructor(type: Int) {
            mType = type
        }

        constructor(type: Int, callBack: RequestCallBack<T?>) {
            mType = type
            mCallBack = callBack
        }

        constructor(type: Int, mCallBack: RequestCallBack<T?>?, url: String?) {
            mType = type
            mUrl = url
            this.mCallBack = mCallBack
        }

        constructor(type: Int, mCallBack: RequestCallBack<T?>?, category_id: Int) {
            mType = type
            mCategory_id = category_id
            this.mCallBack = mCallBack
        }

        constructor(type: Int, localId: Int, requestCallBack: RequestCallBack<*>?) {
            mType = type
            mLocalId = localId
            if (requestCallBack != null) mCallBack = requestCallBack as RequestCallBack<T?>?
        }

        constructor(type: Int, localId: Int, current_page: Int, requestCallBack: RequestCallBack<*>?) {
            mType = type
            mLocalId = localId
            mCurrentPage = current_page
            if (requestCallBack != null) mCallBack = requestCallBack as RequestCallBack<T?>?
        }

        constructor(type: Int, keyword: String?, string_type: String?, current_page: Int, requestCallBack: RequestCallBack<T?>?) {
            mType = type
            mString_Type = string_type
            mKeyword = keyword
            mCurrentPage = current_page
            if (requestCallBack != null) mCallBack = requestCallBack
        }

        override fun run() {
            when (mType) {
                OnLineWorkerThread.Companion.OPERATION_GET_WAPI_DATA_LIST -> {
                    Log.d(OnLineWorkerThread.Companion._TAG, "####### OPERATION GET WAPIDATA LIST ####$mCategory_id")
                    setBusyState(true)
                    if (isConnectNet) {
                        val callBack = mCallBack as RequestCallBack<WaPiDataPattern?>?
                        val waPiDataPattern = player!!.getWaPiDataListAsyncEx(mCategory_id)
                        if (waPiDataPattern != null && waPiDataPattern.waPiDataList != null && !waPiDataPattern.waPiDataList!!.isEmpty()) {
                            callBack!!.onSuccess(waPiDataPattern)
                        } else callBack!!.onFail("empty")
                    }
                }
                OnLineWorkerThread.Companion.OPERATION_GET_REQUEST_RECOMMEND_LIST -> {
                    Log.d(OnLineWorkerThread.Companion._TAG, "####### OPERATION GET REQUEST RECOMMEND LIST ####$mCategory_id")
                    setBusyState(true)
                    if (isConnectNet) {
                        val callBack = mCallBack as RequestCallBack<RecommendsDataPattern?>?
                        val recommendsDataPattern = player!!.getRecommendsDataListAsyncEx(mCategory_id)
                        if (recommendsDataPattern != null && recommendsDataPattern.recommendsDataList != null && !recommendsDataPattern.recommendsDataList!!.isEmpty()) {
                            callBack!!.onSuccess(recommendsDataPattern)
                        } else callBack!!.onFail("empty")
                    }
                }
                OnLineWorkerThread.Companion.OPERATION_GET_CURRENT_DEMAND_CHANNEL_PROGRAMS -> {
                    Log.d(OnLineWorkerThread.Companion._TAG, "####### OPERATION GET CURRENT DEMAND CHANNEL PROGRAMS ####$mLocalId")
                    setBusyState(true)
                    if (isConnectNet) {
                        val callBack = mCallBack as RequestCallBack<ChannelProgramPattern?>?
                        val channelProgramPattern = player!!.getCurrentDemandChannelProgramsAsyncEx(mLocalId, mCurrentPage)
                        if (channelProgramPattern?.getChannelProgramList() != null
                                && channelProgramPattern.getChannelProgramList()!!.isNotEmpty()) {
                            callBack!!.onSuccess(channelProgramPattern)
                        } else callBack!!.onFail("empty")
                    }
                }
                OnLineWorkerThread.Companion.OPERATION_GET_CURRENT_DEMAND_CHANNEL -> {
                    Log.d(OnLineWorkerThread.Companion._TAG, "####### OPERATION GET CURRENT DEMAND CHANNEL ####$mLocalId")
                    setBusyState(true)
                    if (isConnectNet) {
                        val callBack = mCallBack as RequestCallBack<DemandChannelPattern?>?
                        val demandChannelPattern = DemandChannelPattern()
                        demandChannelPattern.currentDemandChannel = player!!.getCurrentDemandChannelAsyncEx(mLocalId)
                        if (demandChannelPattern.currentDemandChannel != null)
                            callBack!!.onSuccess(demandChannelPattern)
                        else
                            callBack!!.onFail("empty")
                    }
                }
                OnLineWorkerThread.Companion.OPERATION_GET_CLASSIFY_ATTRIBUTE -> {
                    Log.d(OnLineWorkerThread.Companion._TAG, "####### OPERATION GET CLASSIFY ATTRIBUTE ####$mLocalId")
                    setBusyState(true)
                    if (isConnectNet) {
                        val callBack = mCallBack as RequestCallBack<ClassificationAttributePattern?>?
                        val classificationAttributePattern: ClassificationAttributePattern?
                        classificationAttributePattern = player!!.getClassificationAttributeAsyncEx(mLocalId)
                        if (classificationAttributePattern != null)
                            callBack!!.onSuccess(classificationAttributePattern)
                        else
                            callBack!!.onFail("empty")
                    }
                }
                OnLineWorkerThread.Companion.OPERATION_GET_CLASSIFY_CONTEXT -> {
                    Log.d(OnLineWorkerThread.Companion._TAG, "####### OPERATION GET CLASSIFY CONTEXT ####$mLocalId")
                    setBusyState(true)
                    if (isConnectNet) {
                        val callBack = mCallBack as RequestCallBack<DemandChannelPattern?>?
                        val demandChannelPattern = DemandChannelPattern()
                        demandChannelPattern.demandChannelsList = player!!.getDemandChannelContextAsyncEx(mLocalId, mCurrentPage)
                        if (demandChannelPattern.demandChannelsList == null ||
                                demandChannelPattern.demandChannelsList!!.isEmpty())
                                    callBack!!.onFail("empty")
                        else
                            callBack!!.onSuccess(demandChannelPattern)
                    }
                }
                OnLineWorkerThread.Companion.OPERATION_GET_REQUEST_PROGRAM_CLASSIFY_PROGRAM -> {
                    Log.d(OnLineWorkerThread.Companion._TAG, "####### OPERATION GET REQUEST PROGRAM CLASSIFY PROGRAM ####")
                    setBusyState(true)
                    if (isConnectNet) {
                        val callBack = mCallBack as RequestCallBack<RequestProgramClassifyListPattern?>?
                        val requestProgramClassifyListPattern: RequestProgramClassifyListPattern = RequestProgramClassifyListPattern.Companion.getInstance()!!
                        //    RequestProgramClassifyListPattern requestProgramClassifyListPattern = new RequestProgramClassifyListPattern();
                        requestProgramClassifyListPattern.setRequestProgramClassifyList(player!!.getRequestProgramClassifyAsyncEx())
                        if (requestProgramClassifyListPattern.getRequestProgramClassifyList()!!.isEmpty())
                            callBack!!.onFail("empty")
                        else
                            callBack!!.onSuccess(requestProgramClassifyListPattern)
                    }
                }
                OnLineWorkerThread.Companion.OPERATION_GET_ONLINE_INFO -> {
                    Log.d(OnLineWorkerThread.Companion._TAG, "####### OPERATION GET ONLINE INFO ####")
                    setBusyState(true)
                    if (isConnectNet) player!!.getOnlineInfoAsyncEx()
                }
                OnLineWorkerThread.Companion.OPERATION_GET_OTHER_ONLINE_INFO -> {
                    Log.d(OnLineWorkerThread.Companion._TAG, "####### OPERATION GET OTHER ONLINE INFO ####$mLocalId")
                    setBusyState(true)
                    if (isConnectNet) player!!.getDifferentLocalOnlineInfoAsyncEx(mLocalId, mCurrentPage) //mFreq is onLine station ID
                }
                OnLineWorkerThread.Companion.OPERATION_GET_REPLAY_URL -> {
                    Log.d(OnLineWorkerThread.Companion._TAG, "####### OPERATION GET REPLAY URL ####")
                    setBusyState(true)
                    player!!.getReplayUrlAsyncEx()
                }
                OnLineWorkerThread.Companion.OPERATION_GET_SEARCH_RESULT -> {
                    Log.d(OnLineWorkerThread.Companion._TAG, "####### OPERATION GET SEARCH RESULT ####$mKeyword ~~ $mString_Type")
                    setBusyState(true)
                    player!!.getSearchResultAsyncEx(mKeyword!!, mString_Type!!, mCurrentPage)
                }
                OnLineWorkerThread.Companion.OPERATION_GET_ONE_DAY_PROGRAM -> {
                    Log.d(OnLineWorkerThread.Companion._TAG, "####### OPERATION GET ONE DAY PROGRAM ####")
                    setBusyState(true)
                    player!!.getOneDayProgramAsyncEx()
                }
                OnLineWorkerThread.Companion.OPERATION_GET_REQUEST_PROGRAM_RECOMMEND -> {
                    Log.d(OnLineWorkerThread.Companion._TAG, "####### OPERATION GET REQUEST PROGRAM RECOMMEND ####")
                    setBusyState(true)
                    if (isConnectNet) {
                        val callBack = mCallBack as RequestCallBack<ClassifyRecommend?>?
                        val newItem = player!!.getRecommendAsyncEx(mCategory_id)
                        if (newItem != null) {
                            callBack!!.onSuccess(newItem)
                        } else {
                            callBack!!.onFail("null")
                        }
                    }
                }
                OnLineWorkerThread.Companion.OPERATION_GET_REQUEST_RECOMMEND_THUMB -> {
                    Log.d(OnLineWorkerThread.Companion._TAG, "####### OPERATION GET REQUEST RECOMMEND THUMB ####")
                    setBusyState(true)
                    if (isConnectNet) {
                        val callBack = mCallBack as RequestCallBack<Bitmap?>?
                        val thumb = player!!.getThumbAsyncEx(mUrl!!)
                        if (thumb != null) {
                            callBack!!.onSuccess(thumb)
                        } else {
                            callBack!!.onFail("null")
                        }
                    }
                }
                OnLineWorkerThread.Companion.OPERATION_GET_FIVE_RECOMMEND_THUMB -> {
                    Log.d(OnLineWorkerThread.Companion._TAG, "####### OPERATION GET FIVE RECOMMEND THUMB ####")
                    setBusyState(true)
                    if (isConnectNet) {
                        val callBack = mCallBack as RequestCallBack<ClassifyRecommend?>?
                        val newItem = player!!.getFiveRecmAsyncEx(mCategory_id)
                        if (newItem != null) {
                            callBack!!.onSuccess(newItem)
                        } else {
                            callBack!!.onFail("null")
                        }
                    }
                }
            }
        }
    }

    private var mIsBusy = false
    fun isBusy(): Boolean {
        return mIsBusy
    }

    fun setBusyState(isBusy: Boolean) {
        mIsBusy = isBusy
    }

    fun shutdown() {
        mExecutorService!!.shutdown() //not send new task to queue, not stop runnable..
        try {
            if (!mExecutorService.awaitTermination(5, TimeUnit.SECONDS)) {
                mExecutorService.shutdownNow()
                if (!mExecutorService.awaitTermination(5, TimeUnit.SECONDS)) System.err.println("Pool did not terminate")
            }
        } catch (e: InterruptedException) {
            Log.d(OnLineWorkerThread.Companion._TAG, "shut down task now!!!")
            mExecutorService.shutdownNow()
            // Preserve interrupt status
            Thread.currentThread().interrupt()
        }
    }

    companion object {
        private val _TAG: String? = "OnLineWorkerThread"
        const val OPERATION_GET_WAPI_DATA_LIST = 15
        const val OPERATION_GET_REQUEST_RECOMMEND_LIST = 14
        const val OPERATION_GET_FIVE_RECOMMEND_THUMB = 13
        const val OPERATION_GET_REQUEST_RECOMMEND_THUMB = 12
        const val OPERATION_GET_REQUEST_PROGRAM_RECOMMEND = 11
        const val OPERATION_GET_CURRENT_DEMAND_CHANNEL_PROGRAMS = 10
        const val OPERATION_GET_CURRENT_DEMAND_CHANNEL = 9
        const val OPERATION_GET_CLASSIFY_ATTRIBUTE = 8
        const val OPERATION_GET_CLASSIFY_CONTEXT = 7
        const val OPERATION_GET_REQUEST_PROGRAM_CLASSIFY_PROGRAM = 6
        const val OPERATION_GET_ONE_DAY_PROGRAM = 5
        const val OPERATION_GET_SEARCH_RESULT = 4
        const val OPERATION_GET_REPLAY_URL = 3
        const val OPERATION_GET_OTHER_ONLINE_INFO = 2
        const val OPERATION_GET_ONLINE_INFO = 1
    }

    init {
        val mBlockingQueue: BlockingQueue<Runnable?> = LinkedBlockingQueue() //when the queue empty , it cann't get any runnable, after add new runnable, the queue will be wake up and can be got runnable to to work
        val mFactory: ThreadFactory = object : ThreadFactory {
            private val mCount: AtomicInteger? = AtomicInteger(1)
            override fun newThread(r: Runnable): Thread? {
                return Thread(r, "new Thread #" + mCount!!.getAndIncrement())
            }
        }
        val mRejectedExecutionHandler = RejectedExecutionHandler { r, executor ->
            try {
                Toast.makeText(context, "your work has been rejected, please do the work again.", Toast.LENGTH_LONG).show()
            } catch (e: RuntimeException) {
                e.printStackTrace()
            }
        }
        val mCorePoolSize = Runtime.getRuntime().availableProcessors()
        val mMaxiMumPoolSize = 10
        val mKeepAliveTime: Long = 500
        val mUnit = TimeUnit.SECONDS
        mExecutorService = ThreadPoolExecutor(mCorePoolSize,
                mMaxiMumPoolSize, mKeepAliveTime,
                mUnit, mBlockingQueue,
                mFactory, mRejectedExecutionHandler)
        Log.d(OnLineWorkerThread.Companion._TAG, "OnLineWorkerThread init finish")
    }
}