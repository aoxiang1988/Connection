package com.sec.myonlinefm;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;
import com.sec.myonlinefm.abstructObserver.RequestCallBack;
import com.sec.myonlinefm.classificationprogram.data.ChannelProgramPattern;
import com.sec.myonlinefm.classificationprogram.data.ClassifyRecommend;
import com.sec.myonlinefm.classificationprogram.data.DemandChannelPattern;
import com.sec.myonlinefm.classificationprogram.data.RecommendsDataPattern;
import com.sec.myonlinefm.classificationprogram.data.RequestProgramClassifyListPattern;
import com.sec.myonlinefm.classificationprogram.data.WaPiDataPattern;
import com.sec.myonlinefm.data.ClassificationAttributePattern;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/3/8.
 * this is the thread pool, we can request an new thread runnable to do the work which need more time,
 * I do the connect net work and Json resolution here
 * also we can do more work thread here...
 */

public class OnLineWorkerThread<T> {

    private static String _TAG = "OnLineWorkerThread";
    public static final int OPERATION_GET_WAPI_DATA_LIST = 15;
    public static final int OPERATION_GET_REQUEST_RECOMMEND_LIST = 14;
    public static final int OPERATION_GET_FIVE_RECOMMEND_THUMB = 13;
    public static final int OPERATION_GET_REQUEST_RECOMMEND_THUMB = 12;
    public static final int OPERATION_GET_REQUEST_PROGRAM_RECOMMEND = 11;
    public static final int OPERATION_GET_CURRENT_DEMAND_CHANNEL_PROGRAMS = 10;
    public static final int OPERATION_GET_CURRENT_DEMAND_CHANNEL = 9;
    public static final int OPERATION_GET_CLASSIFY_ATTRIBUTE = 8;
    public static final int OPERATION_GET_CLASSIFY_CONTEXT = 7;
    public static final int OPERATION_GET_REQUEST_PROGRAM_CLASSIFY_PROGRAM = 6;
    public static final int OPERATION_GET_ONE_DAY_PROGRAM = 5;
    public static final int OPERATION_GET_SEARCH_RESULT = 4;
    public static final int OPERATION_GET_REPLAY_URL = 3;
    public static final int OPERATION_GET_OTHER_ONLINE_INFO = 2;
    public static final int OPERATION_GET_ONLINE_INFO = 1;

    private ExecutorService mExecutorService;
    private OnLineFMConnectManager player;

    public OnLineWorkerThread(final Context context){

        player = OnLineFMConnectManager.Companion.getMMainInfoCode();

        BlockingQueue<Runnable> mBlockingQueue = new LinkedBlockingQueue<>();//when the queue empty , it cann't get any runnable, after add new runnable, the queue will be wake up and can be got runnable to to work

        ThreadFactory mFactory = new ThreadFactory() {

            private final AtomicInteger mCount = new AtomicInteger(1);

            @Override
            public Thread newThread(@NonNull Runnable r) {
                return new Thread(r, "new Thread #" + mCount.getAndIncrement());
            }
        };
        RejectedExecutionHandler mRejectedExecutionHandler = new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                try {
                    Toast.makeText(context,"your work has been rejected, please do the work again.", Toast.LENGTH_LONG).show();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        };

        int mCorePoolSize = Runtime.getRuntime().availableProcessors();
        int mMaxiMumPoolSize = 10;
        long mKeepAliveTime = 500;
        TimeUnit mUnit = TimeUnit.SECONDS;

        mExecutorService = new ThreadPoolExecutor(mCorePoolSize,
                mMaxiMumPoolSize, mKeepAliveTime,
                mUnit, mBlockingQueue,
                mFactory, mRejectedExecutionHandler);

        Log.d(_TAG,"OnLineWorkerThread init finish");
    }

    public void SendNewRunnable(int type) {
        mExecutorService.execute(new OnLineWorkerRunnable(type));
    }
 /*   public synchronized void SendNewRunnable(int type, RequestCallBack<RequestProgramClassifyListPattern> callBack) {
        mExecutorService.execute(new OnLineWorkerRunnable(type, callBack));
    }*/

    public void SendNewRunnable(int type, final int localId, int current_page) {
        mExecutorService.execute(new OnLineWorkerRunnable(type, localId, current_page, null));
    }
    public void SendNewRunnable(int type, String keyword, String string_type, int current_page) {
        mExecutorService.execute(new OnLineWorkerRunnable(type, keyword, string_type, current_page, null));
    }

    public synchronized void SendNewRunnable(int type, RequestCallBack requestCallBack, int channelID, int current_page) {
        if(current_page != -1)
            mExecutorService.execute(new OnLineWorkerRunnable(type, channelID, current_page, requestCallBack));
        else
            mExecutorService.execute(new OnLineWorkerRunnable(type, channelID, requestCallBack));
    }

    public void SendNewRunnable(int type,
                                @org.jetbrains.annotations.NotNull RequestCallBack<RequestProgramClassifyListPattern> callBack) {
        mExecutorService.execute(new OnLineWorkerRunnable(type, (RequestCallBack<T>) callBack));
    }

    public void SendNewRunnable(int type, @NotNull RequestCallBack<ClassifyRecommend> callBack, int category_id) {
        mExecutorService.execute(new OnLineWorkerRunnable(type, (RequestCallBack<T>) callBack, category_id));
    }

    public void SendNewRunnableWaPi(int type, @NotNull RequestCallBack<WaPiDataPattern> callBack, int category_id) {
        mExecutorService.execute(new OnLineWorkerRunnable(type, (RequestCallBack<T>) callBack, category_id));
    }

    public void SendNewRunnable(int type, @NotNull RequestCallBack<Bitmap> callBack, @NotNull String url) {
        mExecutorService.execute(new OnLineWorkerRunnable(type, (RequestCallBack<T>) callBack, url));
    }

    public void SendNewRunnableRecommends(int type, @NotNull RequestCallBack<RecommendsDataPattern> callBack, int category_id) {
        mExecutorService.execute(new OnLineWorkerRunnable(type, (RequestCallBack<T>) callBack, category_id));
    }

    private class OnLineWorkerRunnable implements Runnable {

        private int mType;
        private String mKeyword;
        private String mString_Type;
        private int mLocalId;
        private int mCurrentPage = 1;
     //   private RequestCallBack mCallBack;

        private String mUrl;
        private int mCategory_id;
        private RequestCallBack<T> mCallBack;

        private OnLineWorkerRunnable(int type) {
            mType = type;
        }

        private OnLineWorkerRunnable(int type , RequestCallBack<T> callBack) {
            mType = type;
            mCallBack = callBack;
        }

        private OnLineWorkerRunnable(int type, RequestCallBack<T> mCallBack, String url) {
            mType = type;
            mUrl = url;
            this.mCallBack = mCallBack;
        }

        private OnLineWorkerRunnable(int type, RequestCallBack<T> mCallBack, int category_id) {
            mType = type;
            mCategory_id = category_id;
            this.mCallBack = mCallBack;
        }

        private OnLineWorkerRunnable(int type, final int localId, RequestCallBack requestCallBack) {
            mType = type;
            mLocalId = localId;
            if(requestCallBack != null) mCallBack = requestCallBack;
        }

        private OnLineWorkerRunnable(int type, final int localId, int current_page, RequestCallBack requestCallBack) {
            mType = type;
            mLocalId = localId;
            mCurrentPage = current_page;
            if(requestCallBack != null) mCallBack = requestCallBack;
        }

        private OnLineWorkerRunnable(int type, String keyword, String string_type, int current_page, RequestCallBack<T> requestCallBack) {
            mType = type;
            mString_Type = string_type;
            mKeyword = keyword;
            mCurrentPage = current_page;
            if(requestCallBack != null) mCallBack = requestCallBack;
        }

        @Override
        public void run() {
            switch (mType) {
                case OPERATION_GET_WAPI_DATA_LIST:
                    Log.d(_TAG, "####### OPERATION GET WAPIDATA LIST ####"+ mCategory_id);
                    setBusyState(true);
                    if(OnLineFMConnectManager.Companion.isConnectNet()) {
                        RequestCallBack<WaPiDataPattern> callBack = (RequestCallBack<WaPiDataPattern>) mCallBack;
                        WaPiDataPattern waPiDataPattern = player.getWaPiDataListAsyncEx(mCategory_id);
                        if(waPiDataPattern != null
                                && waPiDataPattern.getWaPiDataList() != null
                                && !waPiDataPattern.getWaPiDataList().isEmpty()) {
                            callBack.onSuccess(waPiDataPattern);
                        } else
                            callBack.onFail("empty");
                    }
                    break;
                case OPERATION_GET_REQUEST_RECOMMEND_LIST:
                    Log.d(_TAG, "####### OPERATION GET REQUEST RECOMMEND LIST ####"+ mCategory_id);
                    setBusyState(true);
                    if(OnLineFMConnectManager.Companion.isConnectNet()) {
                        RequestCallBack<RecommendsDataPattern> callBack = (RequestCallBack<RecommendsDataPattern>) mCallBack;
                        RecommendsDataPattern recommendsDataPattern = player.getRecommendsDataListAsyncEx(mCategory_id);
                        if(recommendsDataPattern != null
                                && recommendsDataPattern.getRecommendsDataList() != null
                                && !recommendsDataPattern.getRecommendsDataList().isEmpty()) {
                            callBack.onSuccess(recommendsDataPattern);
                        } else
                            callBack.onFail("empty");
                    }
                    break;
                case OPERATION_GET_CURRENT_DEMAND_CHANNEL_PROGRAMS:
                    Log.d(_TAG, "####### OPERATION GET CURRENT DEMAND CHANNEL PROGRAMS ####"+ mLocalId);
                    setBusyState(true);
                    if(OnLineFMConnectManager.Companion.isConnectNet()) {
                        RequestCallBack<ChannelProgramPattern> callBack = (RequestCallBack<ChannelProgramPattern>) mCallBack;
                        ChannelProgramPattern channelProgramPattern = player.getCurrentDemandChannelProgramsAsyncEx(mLocalId, mCurrentPage);
                        if(channelProgramPattern != null
                                && channelProgramPattern.getChannelProgramList() != null
                                && !channelProgramPattern.getChannelProgramList().isEmpty()) {
                            callBack.onSuccess(channelProgramPattern);
                        } else
                            callBack.onFail("empty");
                    }
                    break;
                case OPERATION_GET_CURRENT_DEMAND_CHANNEL:
                    Log.d(_TAG, "####### OPERATION GET CURRENT DEMAND CHANNEL ####"+ mLocalId);
                    setBusyState(true);
                    if(OnLineFMConnectManager.Companion.isConnectNet()) {
                        RequestCallBack<DemandChannelPattern> callBack = (RequestCallBack<DemandChannelPattern>) mCallBack;
                        DemandChannelPattern demandChannelPattern = new DemandChannelPattern();
                        demandChannelPattern.setCurrentDemandChannel(player.getCurrentDemandChannelAsyncEx(mLocalId));
                        if(demandChannelPattern.getCurrentDemandChannel() != null)
                            callBack.onSuccess(demandChannelPattern);
                        else
                            callBack.onFail("empty");
                    }
                break;
                case OPERATION_GET_CLASSIFY_ATTRIBUTE:
                    Log.d(_TAG, "####### OPERATION GET CLASSIFY ATTRIBUTE ####"+ mLocalId);
                    setBusyState(true);
                    if(OnLineFMConnectManager.Companion.isConnectNet()) {
                        RequestCallBack<ClassificationAttributePattern> callBack = (RequestCallBack<ClassificationAttributePattern>) mCallBack;
                        ClassificationAttributePattern classificationAttributePattern ;
                        classificationAttributePattern = player.getClassificationAttributeAsyncEx(mLocalId);
                        if(classificationAttributePattern != null)
                            callBack.onSuccess(classificationAttributePattern);
                        else
                            callBack.onFail("empty");
                    }
                    break;
                case OPERATION_GET_CLASSIFY_CONTEXT:
                    Log.d(_TAG, "####### OPERATION GET CLASSIFY CONTEXT ####"+ mLocalId);
                    setBusyState(true);
                    if(OnLineFMConnectManager.Companion.isConnectNet()) {
                        RequestCallBack<DemandChannelPattern> callBack = (RequestCallBack<DemandChannelPattern>) mCallBack;
                        DemandChannelPattern demandChannelPattern = new DemandChannelPattern();
                        demandChannelPattern.setDemandChannelsList(player.getDemandChannelContextAsyncEx(mLocalId, mCurrentPage));
                        if(demandChannelPattern.getDemandChannelsList()== null ||
                                demandChannelPattern.getDemandChannelsList().isEmpty())
                            callBack.onFail("empty");
                        else
                            callBack.onSuccess(demandChannelPattern);
                    }
                    break;
                case OPERATION_GET_REQUEST_PROGRAM_CLASSIFY_PROGRAM:
                    Log.d(_TAG, "####### OPERATION GET REQUEST PROGRAM CLASSIFY PROGRAM ####");
                    setBusyState(true);
                    if(OnLineFMConnectManager.Companion.isConnectNet()) {
                        RequestCallBack<RequestProgramClassifyListPattern> callBack = (RequestCallBack<RequestProgramClassifyListPattern>) mCallBack;
                        RequestProgramClassifyListPattern requestProgramClassifyListPattern = RequestProgramClassifyListPattern.getInstance();
                    //    RequestProgramClassifyListPattern requestProgramClassifyListPattern = new RequestProgramClassifyListPattern();
                        requestProgramClassifyListPattern.setRequestProgramClassifyList(player.getRequestProgramClassifyAsyncEx());
                        if(requestProgramClassifyListPattern.getRequestProgramClassifyList().isEmpty())
                            callBack.onFail("empty");
                        else
                            callBack.onSuccess(requestProgramClassifyListPattern);
                    }
                    break;
                case OPERATION_GET_ONLINE_INFO:
                    Log.d(_TAG, "####### OPERATION GET ONLINE INFO ####");
                    setBusyState(true);
                    if(OnLineFMConnectManager.Companion.isConnectNet())
                        player.getOnlineInfoAsyncEx();
                    break;
                case OPERATION_GET_OTHER_ONLINE_INFO:
                    Log.d(_TAG, "####### OPERATION GET OTHER ONLINE INFO ####"+mLocalId);
                    setBusyState(true);
                    if(OnLineFMConnectManager.Companion.isConnectNet())
                        player.getDifferentLocalOnlineInfoAsyncEx(mLocalId, mCurrentPage);//mFreq is onLine station ID
                    break;
                case OPERATION_GET_REPLAY_URL:
                    Log.d(_TAG, "####### OPERATION GET REPLAY URL ####");
                    setBusyState(true);
                    player.getReplayUrlAsyncEx();
                    break;
                case OPERATION_GET_SEARCH_RESULT:
                    Log.d(_TAG, "####### OPERATION GET SEARCH RESULT ####"+mKeyword+" ~~ "+mString_Type);
                    setBusyState(true);
                    player.getSearchResultAsyncEx(mKeyword, mString_Type, mCurrentPage);
                    break;
                case OPERATION_GET_ONE_DAY_PROGRAM:
                    Log.d(_TAG, "####### OPERATION GET ONE DAY PROGRAM ####");
                    setBusyState(true);
                    player.getOneDayProgramAsyncEx();
                    break;
                case OPERATION_GET_REQUEST_PROGRAM_RECOMMEND:
                    Log.d(_TAG, "####### OPERATION GET REQUEST PROGRAM RECOMMEND ####");
                    setBusyState(true);
                    if(OnLineFMConnectManager.Companion.isConnectNet()) {
                        RequestCallBack<ClassifyRecommend> callBack = (RequestCallBack<ClassifyRecommend>) mCallBack;
                        ClassifyRecommend newItem = player.getRecommendAsyncEx(mCategory_id);
                        if (newItem != null) {
                            callBack.onSuccess(newItem);
                        } else {
                            callBack.onFail("null");
                        }
                    }
                    break;
                case OPERATION_GET_REQUEST_RECOMMEND_THUMB:
                    Log.d(_TAG, "####### OPERATION GET REQUEST RECOMMEND THUMB ####");
                    setBusyState(true);
                    if (OnLineFMConnectManager.Companion.isConnectNet()) {
                        RequestCallBack<Bitmap> callBack = (RequestCallBack<Bitmap>) mCallBack;
                        Bitmap thumb = player.getThumbAsyncEx(mUrl);
                        if (thumb != null) {
                            callBack.onSuccess(thumb);
                        } else {
                            callBack.onFail("null");
                        }
                    }
                    break;
                case OPERATION_GET_FIVE_RECOMMEND_THUMB:
                    Log.d(_TAG, "####### OPERATION GET FIVE RECOMMEND THUMB ####");
                    setBusyState(true);
                    if (OnLineFMConnectManager.Companion.isConnectNet()) {
                        RequestCallBack<ClassifyRecommend> callBack = (RequestCallBack<ClassifyRecommend>) mCallBack;
                        ClassifyRecommend newItem = player.getFiveRecmAsyncEx(mCategory_id);
                        if (newItem != null) {
                            callBack.onSuccess(newItem);
                        } else {
                            callBack.onFail("null");
                        }
                    }
                    break;
            }
        }
    }

    private boolean mIsBusy = false;

    public boolean isBusy() {
        return mIsBusy;
    }

    public void setBusyState(boolean isBusy) {
        mIsBusy = isBusy;
    }

    public void shutdown(){
        mExecutorService.shutdown();//not send new task to queue, not stop runnable..
        try {
            if (!mExecutorService.awaitTermination(5, TimeUnit.SECONDS)) {
                mExecutorService.shutdownNow();
                if (!mExecutorService.awaitTermination(5, TimeUnit.SECONDS))
                    System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException e) {
            Log.d(_TAG, "shut down task now!!!");
            mExecutorService.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }
}
