package com.kuo.myapp.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.kuo.myapp.CarRecordActivity;
import com.kuo.myapp.R;

public class HomeFragment extends BaseFragment implements View.OnClickListener{

    Button mStartRecBtn;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home,container,false);
        mStartRecBtn = (Button) rootView.findViewById(R.id.start_rec);
        mStartRecBtn.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.start_rec:
                Intent intent = new Intent(getContext(),CarRecordActivity.class);
                getContext().startActivity(intent);
                break;
        }
    }
}
