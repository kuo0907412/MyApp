package com.kuo.myapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kuo.myapp.R;

import java.util.ArrayList;

public class GsensorLogAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> logArr;
    public GsensorLogAdapter(Context context, ArrayList<String> arr){
        mContext = context;
        logArr = arr;
    }
    @Override
    public int getCount() {
        return logArr.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void updateLog(ArrayList<String> arr){
        logArr = arr;
        notifyDataSetChanged();

    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder = null;
        if(view == null){
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.adapter_gsensor_log,viewGroup,false);
            viewHolder = new ViewHolder();
            viewHolder.logView = (TextView)view.findViewById(R.id.adapter_log);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.logView.setText(logArr.get(i));

        return view;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    private class ViewHolder{
        TextView logView;
    }
}
