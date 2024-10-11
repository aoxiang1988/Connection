package com.sec.connection.vpview.FragmentViewPager;

import android.util.Log;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BaseFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the  factory method to
 * create an instance of this fragment.
 */
class BaseFragment extends Fragment {
    private static final String TAG = "BaseFragment";
    public BaseFragment() {
        // Required empty public constructor
    }

    public void RemoveRunnable() {
        Log.d(TAG,"RemoveRunnable");
    }
}
