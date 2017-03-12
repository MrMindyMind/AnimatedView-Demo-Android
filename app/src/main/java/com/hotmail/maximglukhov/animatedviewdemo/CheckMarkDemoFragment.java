package com.hotmail.maximglukhov.animatedviewdemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hotmail.maximglukhov.checkmarkviewlib.CheckMarkView;

public class CheckMarkDemoFragment extends Fragment implements View.OnClickListener {

    private CheckMarkView mCheckMark;

    public CheckMarkDemoFragment() {
        // Required empty public constructor
    }

    public static CheckMarkDemoFragment newInstance() {
        CheckMarkDemoFragment fragment = new CheckMarkDemoFragment();
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
        View layout = inflater.inflate(R.layout.fragment_check_mark_demo, container, false);

        mCheckMark = (CheckMarkView) layout.findViewById(R.id.demo_checkmark);

        Button sampleButton = (Button) layout.findViewById(R.id.sampleButton);
        sampleButton.setOnClickListener(this);

        return layout;
    }

    @Override
    public void onClick(View view) {
        mCheckMark.clearAnimation();
        mCheckMark.runAnimations();
    }
}
