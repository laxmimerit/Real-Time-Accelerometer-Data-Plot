package com.example.mbreath.realtimeaccelerometer;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private static final String TAG = "MainActivity";
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private  Sensor sensors;

    private LineChart mChart;
    private Thread thread;
    private boolean plotData = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // if you wanna checkout gravity, set this "TYPE_ACCELEROMETER_UNCALIBRATED" flag and change
        // the LeftAxis.setAxisMinimum to -13f
        // mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER_UNCALIBRATED);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        for(int i=0; i<sensors.size(); i++){
            Log.d(TAG, "onCreate: Sensor "+ i + ": " + sensors.get(i).toString());
        }

        if (mAccelerometer != null) {
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        }

        mChart = (LineChart) findViewById(R.id.chart1);

        // enable description text
        mChart.getDescription().setEnabled(true);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.WHITE);

        LineData data = new LineData();
        data.setValueTextColor(Color.BLACK);

        // add empty data
        mChart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.BLACK);

        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.BLACK);
        xl.setDrawGridLines(true);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMaximum(10f);
        leftAxis.setAxisMinimum(-10f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.setDrawBorders(false);

        feedMultiple();

    }

    private void addEntry(SensorEvent event) {

        LineData data = mChart.getData();
        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);
            if (set == null) {
                LineData newData = createLinesSet();
                mChart.setData(newData);
            }
            else{
                ILineDataSet set1 = data.getDataSetByLabel("X",false);
                ILineDataSet set2 = data.getDataSetByLabel("Y",false);
                ILineDataSet set3 = data.getDataSetByLabel("Z",false);
                set1.addEntry(new Entry(set1.getEntryCount(), event.values[0]));
                set2.addEntry(new Entry(set2.getEntryCount(), event.values[1]));
                set3.addEntry(new Entry(set3.getEntryCount(), -event.values[2]));
                data.notifyDataChanged();
                mChart.notifyDataSetChanged();
                mChart.setVisibleXRangeMaximum(150);
                mChart.moveViewToX(data.getEntryCount());
            }
        }
    }



    private LineData createLinesSet() {
        List<Entry> valsComp1 = new ArrayList<Entry>();
        Entry c1e1 = new Entry(0f, 0f);
        valsComp1.add(c1e1);
        List<Entry> valsComp2 = new ArrayList<Entry>();
        Entry c2e1 = new Entry(0f, 0f);
        valsComp2.add(c2e1);
        List<Entry> valsComp3 = new ArrayList<Entry>();
        Entry c3e1 = new Entry(0f, 0f);
        valsComp3.add(c3e1);

        LineDataSet set1 = new LineDataSet(valsComp1, "X");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setLineWidth(3f);
        set1.setColor(Color.MAGENTA);
        set1.setHighlightEnabled(false);
        set1.setDrawValues(false);
        set1.setDrawCircles(false);
        set1.setMode(LineDataSet.Mode.LINEAR);
        set1.setCubicIntensity(0.2f);

        LineDataSet set2 = new LineDataSet(valsComp2, "Y");
        set2.setAxisDependency(YAxis.AxisDependency.LEFT);
        set2.setLineWidth(3f);
        set2.setColor(Color.BLUE);
        set2.setHighlightEnabled(false);
        set2.setDrawValues(false);
        set2.setDrawCircles(false);
        set2.setMode(LineDataSet.Mode.LINEAR);
        set2.setCubicIntensity(0.2f);

        LineDataSet set3 = new LineDataSet(valsComp3, "Z");
        set3.setAxisDependency(YAxis.AxisDependency.LEFT);
        set3.setLineWidth(3f);
        set3.setColor(Color.BLACK);
        set3.setHighlightEnabled(false);
        set3.setDrawValues(false);
        set3.setDrawCircles(false);
        set3.setMode(LineDataSet.Mode.LINEAR);
        set3.setCubicIntensity(0.2f);

        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set1);
        dataSets.add(set2);
        dataSets.add(set3);
        LineData output_data = new LineData(dataSets);
        return output_data;
    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3f);
        set.setColor(Color.MAGENTA);
        set.setHighlightEnabled(false);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        return set;
    }

    private void feedMultiple() {

        if (thread != null){
            thread.interrupt();
        }

        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true){
                    plotData = true;
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (thread != null) {
            thread.interrupt();
        }
        mSensorManager.unregisterListener(this);

    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        if(plotData){
            addEntry(event);
            plotData = false;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onDestroy() {
        mSensorManager.unregisterListener(MainActivity.this);
        thread.interrupt();
        super.onDestroy();
    }
}
