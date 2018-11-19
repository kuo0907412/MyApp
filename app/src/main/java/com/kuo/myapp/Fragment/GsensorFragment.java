package com.kuo.myapp.Fragment;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.kuo.myapp.Adapter.GsensorLogAdapter;
import com.kuo.myapp.MainActivity;
import com.kuo.myapp.R;

import java.util.ArrayList;

import static android.content.Context.SENSOR_SERVICE;

public class GsensorFragment extends BaseFragment implements SensorEventListener,View.OnClickListener {

    private ArrayList<Float> x_arr,y_arr,z_arr;
    private ArrayList<String> mLog_list;
    private Button mStartBtn;
    private Boolean mStartGsensorFlg;
    private GsensorLogAdapter mGsensorAdapter;
    private ListView mLogView;
    private LineChart mChart;
    private SensorManager mSensorManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x_arr = new ArrayList<>();
        y_arr = new ArrayList<>();
        z_arr = new ArrayList<>();
        mLog_list = new ArrayList<>();
        mStartGsensorFlg = false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_gsensor,container,false);
        mChart = (LineChart) rootView.findViewById(R.id.lineChart);
        mLogView = (ListView) rootView.findViewById(R.id.text_log);
        mStartBtn = (Button) rootView.findViewById(R.id.start_btn);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mGsensorAdapter = new GsensorLogAdapter(getActivity(),mLog_list);
        mLogView.setAdapter(mGsensorAdapter);
        mStartBtn.setOnClickListener(this);
        mChart.setDrawGridBackground(true);
        mChart.getDescription().setEnabled(true);
        // Grid縦軸を破線
        XAxis xAxis = mChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        YAxis leftAxis = mChart.getAxisLeft();
        // Y軸最大最小設定
        leftAxis.setAxisMaximum(50f);
        leftAxis.setAxisMinimum(-50f);
        // Grid横軸を破線
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(true);
        // 右側の目盛り
        mChart.getAxisRight().setEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager = (SensorManager) getContext().getSystemService(SENSOR_SERVICE);
        if(mSensorManager != null) {
            Sensor accel = mSensorManager.getDefaultSensor(
                    Sensor.TYPE_ACCELEROMETER);
            mSensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY));
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float sensorX, sensorY, sensorZ;

        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            if(mStartGsensorFlg) {
                sensorX = sensorEvent.values[0];
                sensorY = sensorEvent.values[1];
                sensorZ = sensorEvent.values[2];
                showLog("X:" + String.valueOf(sensorX) + "Y:" + String.valueOf(sensorY) + "Z:" + String.valueOf(sensorZ), mGsensorAdapter, mLog_list);
                setData(sensorX, sensorY, sensorZ);
                mChart.animateX(0);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    private void setData(float x_value,float y_value,float z_value) {
        final int MAX_POINT_NUM = 30;
        LineDataSet set_x,set_y,set_z;
        x_arr.add(x_value);
        y_arr.add(y_value);
        z_arr.add(z_value);
        if(x_arr.size() > MAX_POINT_NUM){
            x_arr.remove(0);
        }
        if(y_arr.size() > MAX_POINT_NUM){
            y_arr.remove(0);
        }
        if(z_arr.size() > MAX_POINT_NUM){
            z_arr.remove(0);
        }
        ArrayList<Entry> values_x = new ArrayList<>();
        ArrayList<Entry> values_y = new ArrayList<>();
        ArrayList<Entry> values_z = new ArrayList<>();

        for (int i = 0; i < x_arr.size(); i++) {
            values_x.add(new Entry(i, x_arr.get(i), null, null));
            values_y.add(new Entry(i, y_arr.get(i), null, null));
            values_z.add(new Entry(i, z_arr.get(i), null, null));
        }

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set_x = (LineDataSet) mChart.getData().getDataSetByIndex(0);
            set_x.setValues(values_x);
            set_y = (LineDataSet) mChart.getData().getDataSetByIndex(1);
            set_y.setValues(values_y);
            set_z = (LineDataSet) mChart.getData().getDataSetByIndex(2);
            set_z.setValues(values_z);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set_x = new LineDataSet(values_x, "X");
            setLinChartInfo(set_x,false,Color.BLUE,Color.BLUE,1f,3f,false,0f,true,1f,new DashPathEffect(new float[]{10f,5f},0f),15.f,Color.BLUE);
            set_y = new LineDataSet(values_y, "Y");
            setLinChartInfo(set_y,false,Color.RED,Color.RED,1f,3f,false,0f,true,1f,new DashPathEffect(new float[]{10f,5f},0f),15.f,Color.RED);
            set_z = new LineDataSet(values_z, "Z");
            setLinChartInfo(set_z,false,Color.GREEN,Color.GREEN,1f,3f,false,0f,true,1f,new DashPathEffect(new float[]{10f,5f},0f),15.f,Color.GREEN);

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set_x);
            dataSets.add(set_y); // add the datasets
            dataSets.add(set_z);
            // create a data object with the datasets
            LineData lineData = new LineData(dataSets);

            // set data
            mChart.setData(lineData);
        }
    }

    private void setLinChartInfo(LineDataSet setData,boolean drawIcon,int color,int circleColor, float lineWidth,
                                 float circleRadius, boolean drawCircleHole,float valueTextSize,boolean drawFilled,
                                 float formLineWidth,DashPathEffect formLineDashEffect,float FormSize,int fillColor){
        setData.setDrawIcons(drawIcon);
        setData.setColor(color);
        setData.setCircleColor(circleColor);
        setData.setLineWidth(lineWidth);
        setData.setCircleRadius(circleRadius);
        setData.setDrawCircleHole(drawCircleHole);
        setData.setValueTextSize(valueTextSize);
        setData.setDrawFilled(drawFilled);
        setData.setFormLineWidth(formLineWidth);
        setData.setFormLineDashEffect(formLineDashEffect);
        setData.setFormSize(FormSize);
        setData.setFillColor(fillColor);
    }

    private void showLog(String msg, GsensorLogAdapter adapter, ArrayList<String> log_arr){
        log_arr.add(msg);
        adapter.updateLog(log_arr);
        mLogView.setSelection(log_arr.size()-1);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.start_btn:
                if(mStartBtn.getText().toString().equals(getString(R.string.start))){
                    mStartGsensorFlg = true;
                    mStartBtn.setText(getString(R.string.stop));
                } else {
                    mStartGsensorFlg = false;
                    mStartBtn.setText(getString(R.string.start));
                }
                break;
        }
    }
}
