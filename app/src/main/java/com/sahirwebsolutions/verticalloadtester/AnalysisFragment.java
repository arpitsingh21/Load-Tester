package com.sahirwebsolutions.verticalloadtester;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ntt.customgaugeview.library.GaugeView;

import java.util.ArrayList;

/**
 * Created by Bhavya on 05-07-2016.
 */
public class AnalysisFragment extends Fragment {


    public static ImageButton buttonStart, buttonStop;
    public static TextView textViewTimer,textViewGauge;
    public LineChartFragment lineChartFragmentLOCAL;
    //   private int count=0,graphs=1,usedgraphs=0;
    private Context context;
    public boolean readData = false,graphused=false;
    private ArrayList<Double> arrayListHighest, arrayListLowest, arrayListAverage;
    double highest, lowest, average;
    private DBHelper dbHelper;
    private FrameLayout frameLayoutTest;
    public static  Chronometer chronometer;
    public static long  timeInMilliSeconds=0L;
    public  Thread t;

    private int batteryValue=4;

    ImageView imageViewBattery;

    public static AnalysisFragment NewInstance() {
        return new AnalysisFragment();
    }

    public AnalysisFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setHasOptionsMenu(true);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_analysis_fragment, container, false);





        buttonStart = (ImageButton) view.findViewById(R.id.buttonStart);
        buttonStop = (ImageButton) view.findViewById(R.id.buttonStop);
        textViewTimer = (TextView) view.findViewById(R.id.textViewTimer);
        textViewGauge = (TextView) view.findViewById(R.id.textViewGauge);
        imageViewBattery = (ImageView) view.findViewById(R.id.imageView2);
        final GaugeView gaugeView = (GaugeView)view.findViewById(R.id.gauge_view);
        //final Button btnStart = (Button) findViewById(R.id.btn_start);
        gaugeView.setShowRangeValues(true);
        gaugeView.setTargetValue(0);
      //  RelativeLayout relativeLayoutGauge=(RelativeLayout) view.findViewById(R.id.relativeLayoutGauge);

        Button buttonAnalysis=(Button) view.findViewById(R.id.buttonAnalysis);
        frameLayoutTest=(FrameLayout) view.findViewById(R.id.frameLayoutTest);


        LinearLayout.LayoutParams layoutParamsFrame=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                NavigationDrawerActivity.frameLayoutHeight);
        //frameLayoutTest.setLayoutParams(layoutParamsFrame);
        dbHelper=new DBHelper(context);
        buttonAnalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dbHelper.getIds().size()>0) {
                    Intent intent = new Intent(context, AnalysisListActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(context, "No data has been saved yet!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        arrayListAverage = new ArrayList<>();
        arrayListLowest = new ArrayList<>();
        arrayListHighest = new ArrayList<>();

        if (SettingsFragment.flagStopManual) {
            AnalysisFragment.buttonStop.setVisibility(View.VISIBLE);
            AnalysisFragment.textViewTimer.setVisibility(View.INVISIBLE);
        } else {
            AnalysisFragment.buttonStop.setVisibility(View.INVISIBLE);
            AnalysisFragment.textViewTimer.setVisibility(View.VISIBLE);
        }

        chronometer=new Chronometer(context);
        buttonStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (NavigationDrawerActivity.CONNECTION_STATUS == 1) {
                    if(graphused)
                        addCard();
                    readData = true;
                    timeInMilliSeconds= System.currentTimeMillis();
                    t = new Thread() {

                        @Override
                        public void run() {
                            try {
                                while (!isInterrupted()) {
                                    Thread.sleep(1000);
                                    timeInMilliSeconds+=1000;
                                }
                            } catch (InterruptedException e) {
                            }
                        }
                    };

                    t.start();
                    buttonStop.setClickable(true);
                    buttonStart.setClickable(false);

                    if (!SettingsFragment.flagStopManual) {
                        final int timeInSeconds = SettingsFragment.timeInSeconds;
                        new CountDownTimer(timeInSeconds * 1000, 1000) {

                            public void onTick(long millisUntilFinished) {
                                textViewTimer.setText("" + millisUntilFinished / 1000);
                            }

                            public void onFinish() {
                                Toast.makeText(context, "Time Up!", Toast.LENGTH_SHORT).show();
                                textViewTimer.setText("" + timeInSeconds);
                                buttonStop.performClick();
                            }
                        }.start();
                    }
                } else if (NavigationDrawerActivity.CONNECTION_STATUS == 0) {
                    Toast.makeText(context, "Not Connected!\nTry connecting again.", Toast.LENGTH_SHORT).show();
                } else if (NavigationDrawerActivity.CONNECTION_STATUS == 2) {
                    Toast.makeText(context, "Connecting to device..\nPlease try after a moment!", Toast.LENGTH_SHORT).show();
                }

            }
        });


        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readData = false;
                chronometer.stop();
                t.interrupt();
                timeInMilliSeconds=0L;
                buttonStop.setClickable(false);
                graphused=true;
                saveAnalysisValues();
                buttonStart.setClickable(true);
            }
        });

        buttonStop.setClickable(false);

        addCard();

        setImageBattery();

        return view;
    }



    @Override
    public void onResume() {
        super.onResume();
        if (NavigationDrawerActivity.fragmentId2 == 1)
            NavigationDrawerActivity.fragmentId = 1;


    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

    }

    private void saveAnalysisValues() {

        calculateAnalysisValues();

        lineChartFragmentLOCAL.tableLayoutAnalysis.setVisibility(View.VISIBLE);
//        lineChartFragmentLOCAL.mainViewShader.setVisibility(View.GONE);


        String h= String.format("%.3f", highest);
        String l= String.format("%.3f", lowest);
        String a= String.format("%.3f", average);
        dbHelper.saveRecord(h,l,a,NavigationDrawerActivity.arrayListFloatValues,NavigationDrawerActivity.arrayListTimeLongValues,NavigationDrawerActivity.deviceCheck);

        NavigationDrawerActivity.arrayListFloatValues.clear();
        NavigationDrawerActivity.arrayListTimeLongValues.clear();
    }

    private void calculateAnalysisValues() {
        ArrayList<Double> arrayList = NavigationDrawerActivity.arrayListFloatValues;
        float sum = 0;
        highest = arrayList.get(0);
        lowest = arrayList.get(0);
        for (Double f : arrayList) {
            if (f > highest)
                highest = f;
            if (f < lowest)
                lowest = f;
            sum += f;
        }

        average = sum / arrayList.size();

        lineChartFragmentLOCAL.textViewHighest.setText(String.format("%.2f", highest));
        lineChartFragmentLOCAL.textViewAverage.setText(String.format("%.2f", average));
        lineChartFragmentLOCAL.textViewLowest.setText(String.format("%.2f", lowest));

        arrayListHighest.add(highest);
        arrayListAverage.add(average);
        arrayListLowest.add(lowest);

    }





    private void addCard() {

        //  Log.e("Tag100","linechart new instanc")
        lineChartFragmentLOCAL = LineChartFragment.newInstance();
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayoutTest, lineChartFragmentLOCAL)
                .commit();

    }

    public void changeBatteryStatus(int value){
        switch(value){

            case 0:
                batteryValue=0;
                break;
            case 1:
                batteryValue=1;
                break;
            case 2:
                batteryValue=2;
                break;
            case 3:
                batteryValue=3;
                break;
            case 4:
                batteryValue=4;
                break;
            default:
                batteryValue=10;

        }

        setImageBattery();
    }


    private void setImageBattery(){

        switch(batteryValue){

            case 0:
                imageViewBattery.setImageDrawable(getResources().getDrawable(R.drawable.battery_5));
                break;
            case 1:
                imageViewBattery.setImageDrawable(getResources().getDrawable(R.drawable.battery_4));
                break;
            case 2:
                imageViewBattery.setImageDrawable(getResources().getDrawable(R.drawable.battery_3));
                break;
            case 3:
                imageViewBattery.setImageDrawable(getResources().getDrawable(R.drawable.battery_2));
                break;
            case 4:
                imageViewBattery.setImageDrawable(getResources().getDrawable(R.drawable.battery_1));
                break;
            case 10:
                imageViewBattery.setImageDrawable(getResources().getDrawable(R.drawable.battery_1));
                Toast.makeText(context, "Battery value not found!", Toast.LENGTH_SHORT).show();
                break;

        }
    }


}
