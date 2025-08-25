package com.dten.myclientdemo.ui.main;

import androidx.lifecycle.ViewModelProvider;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dten.myclientdemo.R;
import com.dten.myservicedemo.IMyService;
import com.google.android.material.textfield.TextInputEditText;

public class MainFragment extends Fragment {

    private static final String TAG = "MainFragment";

    private MainViewModel mViewModel;

    private TextInputEditText mTextInputEditText;
    private Button mSendTextButton;

    private TextView mMessageText;

    private IMyService mIMyService;
    private boolean mIsBind = false;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG,"onServiceConnected");
            mIMyService = IMyService.Stub.asInterface(iBinder);
            mIsBind = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mIMyService = null;
            mIsBind = false;
        }
    };


    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        // TODO: Use the ViewModel
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(
                "com.dten.myservicedemo",
                "com.dten.myservicedemo.MyService"));
        boolean result = getContext().bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "bindService result: " + result);
        Log.d(TAG,"has send intent : " + intent.getComponent().getClassName());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mIsBind) {
            getContext().unbindService(mServiceConnection);
            mIsBind = false;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mTextInputEditText = view.findViewById(R.id.text_input_view);
        mSendTextButton = view.findViewById(R.id.button);
        mMessageText = view.findViewById(R.id.message);
        mSendTextButton.setOnClickListener(mClickListener);
        return view;
    }

    private final View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.button) {
                String sendText = mTextInputEditText.getText().toString();
                mViewModel.setSendText(sendText);

                if (mIsBind) {
                    mViewModel.sendTextToService(mIMyService);
                    mMessageText.setText(mViewModel.getGetFromServiceText(mIMyService));
                }
            }
        }
    };

}