package com.sahirwebsolutions.verticalloadtester;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Created by Bhavya on 04-07-2016.
 */
public class LineChartFragment extends Fragment implements
        OnChartValueSelectedListener {


    private YAxis leftAxis;
    private float width,height;
    private int screenWidthDp,screenHeightDp;
    private LineChart mChart;
    public View mainViewShader;
    private Context context;
    public static TextView textViewAnalysis,textViewHighestLabel,textViewAverageLabel,textViewLowestLabel,textViewHighest,
            textViewAverage,textViewLowest;
    public TableLayout tableLayoutAnalysis;

    public static LineChartFragment newInstance(){
        return new LineChartFragment();
    }

    public LineChartFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.layout_linechart_fragment,container,false);


       // mainViewShader= view.findViewById(R.id.mainViewShader);
        textViewHighestLabel=(TextView) view.findViewById(R.id.textViewHighestLabel);
        textViewLowestLabel=(TextView) view.findViewById(R.id.textViewLowestLabel);
        textViewAverageLabel=(TextView) view.findViewById(R.id.textViewAverageLabel);
        textViewHighest=(TextView) view.findViewById(R.id.textViewHighest);
        textViewLowest=(TextView) view.findViewById(R.id.textViewLowest);
        textViewAverage=(TextView) view.findViewById(R.id.textViewAverage);
        tableLayoutAnalysis=(TableLayout) view.findViewById(R.id.tableLayoutAnalysis);

/*        Typeface face= Typeface.createFromAsset(context.getAssets(), "calibri_light.ttf");
        textViewHighestLabel.setTypeface(face);
        textViewLowestLabel.setTypeface(face);
        textViewAverageLabel.setTypeface(face);
        textViewHighest.setTypeface(face);
        textViewLowest.setTypeface(face);
        textViewAverage.setTypeface(face);

*/

        LinearLayout.LayoutParams layoutParamsChart=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                NavigationDrawerActivity.lineChartHeight);
        LinearLayout.LayoutParams layoutParamsShader=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                NavigationDrawerActivity.shaderHeight);

       // tableLayoutAnalysis.setLayoutParams(layoutParamsShader);
        mChart = (LineChart) view.findViewById(R.id.chart1);
        ViewPortHandler handler = mChart.getViewPortHandler();
      //  handler.setChartDimens(NavigationDrawerActivity.lineChartWidth,NavigationDrawerActivity.lineChartHeight);


      //  mChart.setLayoutParams(layoutParamsChart);
        mChart.setOnChartValueSelectedListener(this);

        // no description text
        mChart.setDescription("");
        mChart.setNoDataTextDescription("You need to provide data for the chart.");

        if(NavigationDrawerActivity.fragmentId2==1) {
            // enable touch gestures
            mChart.setTouchEnabled(false);


        }else{
            // enable touch gestures
            mChart.setTouchEnabled(true);

        }
        mChart.setDrawGridBackground(false);
        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);

       // mChart.setAutoScaleMinMaxEnabled(false);
       // mChart.setVisibleYRange(0,3, YAxis.AxisDependency.LEFT);
        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.TRANSPARENT);

        LineData data = new LineData();
        data.setValueTextColor(Color.BLACK);

        // add empty data
        mChart.setData(data);

        // get the legend (only possible after setting data)
        Legend legend= mChart.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextColor(Color.RED);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawGridLines(true);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        //xAxis.setEnabled(true);
        xAxis.setEnabled(false);
        /*xAxis.setGranularity(2000L); // one minute in millis
        xAxis.setValueFormatter(new AxisValueFormatter() {

            private SimpleDateFormat mFormat = new SimpleDateFormat("ss");

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mFormat.format(new Date((long) value));
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });

*/

        leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setDrawGridLines(true);
        leftAxis.setEnabled(false);
/*
        leftAxis.setAxisMaxValue(15);
        leftAxis.setAxisMinValue(0);
*/

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);


        if(SettingsFragment.enableGrid){
            xAxis.setEnabled(true);
            leftAxis.setEnabled(true);
        }else{
            xAxis.setEnabled(false);
            leftAxis.setEnabled(false);
        }



        return view;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;

        if(Build.VERSION.SDK_INT>=13) {
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            width = size.x;
            height = size.y;

            Configuration configuration = getResources().getConfiguration();
            screenWidthDp = configuration.screenWidthDp;
        }else{
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            width = display.getWidth();  // deprecated
            height = display.getHeight();

            float density  = getResources().getDisplayMetrics().density;
            screenWidthDp=(int)(width/density);
        }
    }



    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }


    private void addEntry(float f) {

        LineData data = mChart.getData();

        if (data != null) {

            //leftAxis.resetAxisMaxValue();
            //leftAxis.resetAxisMinValue();
            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            data.addEntry(new Entry(set.getEntryCount(),f), 0);
            //data.addEntry(new Entry(f,data.getXValCount()), 0);
            data.notifyDataChanged();

            // let the chart know it's data has changed
            mChart.notifyDataSetChanged();

            // limit the number of visible entries
            mChart.setVisibleXRangeMaximum(3000);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            mChart.moveViewToX(data.getEntryCount());  // get entry count.............check!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

            // this automatically refreshes the chart (calls invalidate())
            // mChart.moveViewTo(data.getXValCount()-7, 55f,
            // AxisDependency.LEFT);
        }
    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "");
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(Color.WHITE);
        set.setDrawCircles(false);
  //      set.setCircleColor(Color.WHITE);
        set.setLineWidth(2f);

//        set.setCircleRadius(4f);
     //   set.setFillAlpha(65);
       // set.setFillColor(ColorTemplate.getHoloBlue());
       // set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(10f);
        set.setDrawValues(true);
        return set;
    }

    private Thread thread;

    public void feedMultiple(final float floatValue, final Activity activity) {

        if (thread != null)
            thread.interrupt();

        thread = new Thread(new Runnable() {

            @Override
            public void run() {

                activity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        addEntry(floatValue);
                    }
                });

                try {
                    Thread.sleep(25);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (thread != null) {
            thread.interrupt();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(NavigationDrawerActivity.fragmentId2==0){
            NavigationDrawerActivity.fragmentId=0;
        }

        if(SettingsFragment.visibilityHighest){
            textViewHighest.setVisibility(View.VISIBLE);
            textViewHighestLabel.setVisibility(View.VISIBLE);
        }else{
            textViewHighest.setVisibility(View.INVISIBLE);
            textViewHighestLabel.setVisibility(View.INVISIBLE);
        }

        if(SettingsFragment.visibilityAverage){
            textViewAverage.setVisibility(View.VISIBLE);
            textViewAverageLabel.setVisibility(View.VISIBLE);
        }else{
            textViewAverage.setVisibility(View.INVISIBLE);
            textViewAverageLabel.setVisibility(View.INVISIBLE);
        }

        if(SettingsFragment.visibilityLowest){
            textViewLowest.setVisibility(View.VISIBLE);
            textViewLowestLabel.setVisibility(View.VISIBLE);
        }else{
            textViewLowest.setVisibility(View.INVISIBLE);
            textViewLowestLabel.setVisibility(View.INVISIBLE);
        }
    }
}
