package com.hotmail.maximglukhov.animatedviewdemo;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hotmail.maximglukhov.animatedviewlib.AnimatedDraw;
import com.hotmail.maximglukhov.windowsxprogressbar.WindowsXProgressBar;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class WindowsXProgressBarDemoFragment extends Fragment implements View.OnClickListener {

    private boolean mIsRunning = true;

    private WindowsXProgressBar mWindowsXProgressBar;

    private Button mSampleButton;

    public WindowsXProgressBarDemoFragment() {
        // Required empty public constructor
    }

    public static WindowsXProgressBarDemoFragment newInstance() {
        WindowsXProgressBarDemoFragment fragment = new WindowsXProgressBarDemoFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment.
        View layout = inflater.inflate(R.layout.fragment_windows_xprogress_bar_demo,
                container, false);

        mWindowsXProgressBar = (WindowsXProgressBar) layout.findViewById(R.id.demo_windowsxprogressbar);
        setAnimatedDrawsListeners();

        mSampleButton = (Button) layout.findViewById(R.id.sampleButton);
        mSampleButton.setOnClickListener(this);

        return layout;
    }

    private void setAnimatedDrawsListeners() {
        List<AnimatedDraw> animatedDraws = mWindowsXProgressBar.getAnimatedDraws();
    }

    @Override
    public void onClick(View view) {
        if (mIsRunning) {
            mIsRunning = false;
            mWindowsXProgressBar.stopAnimations();
            mSampleButton.setText(R.string.start);
        } else {
            mIsRunning = true;
            mWindowsXProgressBar.runAnimations();
            mSampleButton.setText(R.string.stop);
        }
    }
}
