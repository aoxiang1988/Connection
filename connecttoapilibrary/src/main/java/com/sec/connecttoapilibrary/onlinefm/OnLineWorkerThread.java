package com.sec.connecttoapilibrary.onlinefm;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.sec.connecttoapilibrary.RequestCallBack;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
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

public class OnLineWorkerThread {

    private static String _TAG = "OnLineWorkerThread";
    static final int OPERATION_GET_ONE_DAY_PROGRAM = 6;
    static final int OPERATION_GET_SEARCH_RESULT = 5;
    static final int OPERATION_GET_REPLAY_URL = 4;
    static final int OPERATION_GET_OTHER_ONLINE_INFO = 3;
    static final int OPERATION_GET_ONLINE_LOCAL_LIST_INFO = 2;
    static final int OPERATION_GET_ONLINE_CENTER_LIST_INFO = 1;

    private static final int mCorePoolSize = Runtime.getRuntime().availableProcessors(); // the Android runtime support most thread num
    private static final int mMaxiMumPoolSize = 10; // the pool of this work thread pool
    private static final long mKeepAliveTime = 500; // the time that every thread can keep alive
    private static final TimeUnit mUnit = TimeUnit.SECONDS; // can set as second, minute, hour, day...
    private ExecutorService mExecutorService;

    private RequestCallBack mCallBack;
    private int mChannelID;

    OnLineWorkerThread(final Context context){
        BlockingQueue<Runnable> mBlockingQueue = new LinkedBlockingQueue<>();//when the queue empty , it cann't get any runnable, after add new runnable, the queue will be wake up and can be got runnable to to work

//        mCallBackQueue = new ConcurrentLinkedQueue<>();
//        mCallBackQueue.add(mCallBack);
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
                Toast.makeText(context,"your work has been rejected, please do the work again.", Toast.LENGTH_LONG).show();
            }
        };
        mExecutorService = new ThreadPoolExecutor(mCorePoolSize,
                mMaxiMumPoolSize, mKeepAliveTime, mUnit, mBlockingQueue, mFactory, mRejectedExecutionHandler);
        Log.d(_TAG,"OnLineWorkerThread init finish");
    }

    void SendNewRunnable(int type) {
        mExecutorService.execute(new OnLineWorkerRunnable(type));
    }
    public synchronized void SendNewRunnable(int type, RequestCallBack callBack) {
        mCallBack = callBack;
        mExecutorService.execute(new OnLineWorkerRunnable(type));
    }

    public void SendNewRunnable(int type, final int localId) {
        mExecutorService.execute(new OnLineWorkerRunnable(type, localId));
    }
    void SendNewRunnable(int type, String keyword, String string_type, int current_page) {
        mExecutorService.execute(new OnLineWorkerRunnable(type, keyword, string_type, current_page));
    }

    public synchronized void SendNewRunnable(int type, RequestCallBack requestCallBack, int channelID) {
        mCallBack = requestCallBack;
        mExecutorService.execute(new OnLineWorkerRunnable(type, channelID));
    }

    private class OnLineWorkerRunnable implements Runnable {
        private int mType;
        private MainOnLineInfoManager manager;

        private String mKeyword;
        private String mString_Type;
        private int mLocalId;
        private int mCurrentPage = 1;

        private OnLineWorkerRunnable(int type) {
            mType = type;
            manager = MainOnLineInfoManager.getIntense();
        }

        private OnLineWorkerRunnable(int type, final int localId) {
            mType = type;
            mLocalId = localId;
            manager = MainOnLineInfoManager.getIntense();
        }

        private OnLineWorkerRunnable(int type, String keyword, String string_type, int currentpage) {
            mType = type;
            manager = MainOnLineInfoManager.getIntense();
            mString_Type = string_type;
            mKeyword = keyword;
            mCurrentPage = currentpage;
        }

        @Override
        public void run() {
            switch (mType) {
                case OPERATION_GET_ONLINE_LOCAL_LIST_INFO:
                    Log.d(_TAG, "####### OPERATION GET ONLINE LOCAL LIST INFO ####");
                    setBusyState(true);
                    if(manager.isConnectNet)
                        manager.getOnlineLocalListInfoAsyncEx();
                    break;
                case OPERATION_GET_ONLINE_CENTER_LIST_INFO:
                    Log.d(_TAG, "####### OPERATION GET ONLINE CENTER LIST INFO ####");
                    setBusyState(true);
                    if(manager.isConnectNet)
                        manager.getOnlineCenterListInfoAsyncEx();
                    break;
                case OPERATION_GET_OTHER_ONLINE_INFO:
                    Log.d(_TAG, "####### OPERATION GET OTHER ONLINE INFO ####"+mLocalId);
                    setBusyState(true);
                    if(manager.isConnectNet)
                        manager.getDifferentLocalOnlineInfoAsyncEx(mLocalId);//mFreq is onLine station ID
                    break;
                case OPERATION_GET_REPLAY_URL:
                    Log.d(_TAG, "####### OPERATION GET REPLAY URL ####");
                    setBusyState(true);
                    manager.getReplayUrlAsyncEx();
                    break;
                case OPERATION_GET_SEARCH_RESULT:
                    Log.d(_TAG, "####### OPERATION GET SEARCH RESULT ####"+mKeyword+" ~~ "+mString_Type);
                    setBusyState(true);
                    manager.getSearchResultAsyncEx(mKeyword, mString_Type, mCurrentPage);
                    break;
                case OPERATION_GET_ONE_DAY_PROGRAM:
                    Log.d(_TAG, "####### OPERATION GET ONE DAY PROGREM ####");
                    setBusyState(true);
                    manager.getOneDayProgramAsyncEx();
                    break;
            }
        }
    }

    private boolean mIsBusy = false;

    public boolean isBusy() {
        return mIsBusy;
    }

    private void setBusyState(boolean isBusy) {
        mIsBusy = isBusy;
    }

    void shutdown(){
        mExecutorService.shutdown();
    }

}
