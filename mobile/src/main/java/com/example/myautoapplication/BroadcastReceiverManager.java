// BroadcastReceiverManager.java
package com.example.myautoapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class BroadcastReceiverManager {
    private final Context context;
    private final MainViewModel viewModel;
    
    private final IntentFilter intentFilter = new IntentFilter();
    
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MyService.SERVICE_INTENT_ACTION)) {
                String info = intent.getStringExtra(MyService.SERVICE_INTENT_INFO);
                viewModel.setServiceInfo(info);
            } else if (intent.getAction().equals(MyService.MEDIA_SOURCE_STOP_ACTION)) {
                viewModel.setPlayState(false);
            }
        }
    };

    public BroadcastReceiverManager(Context context, MainViewModel viewModel) {
        this.context = context;
        this.viewModel = viewModel;
        
        intentFilter.addAction(MyService.SERVICE_INTENT_ACTION);
        intentFilter.addAction(MyService.MEDIA_SOURCE_STOP_ACTION);
    }

    public void registerReceiver() {
        context.registerReceiver(receiver, intentFilter, Context.RECEIVER_NOT_EXPORTED);
    }

    public void unregisterReceiver() {
        try {
            context.unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
            // Receiver not registered
        }
    }
}
