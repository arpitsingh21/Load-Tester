package com.sahirwebsolutions.verticalloadtester;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Bhavya on 10-07-2016.
 */
public class DetailedAnalysisActivity extends AppCompatActivity {

    ListView listViewDetailedAnalysis;
    TextView textViewHighest2,textViewLowest2,textViewAverage2,textViewDate2;
    int ID,fileNumber=1;
    private DBHelper dbHelper;
    int size=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.detailed_analysis_activity);

        textViewHighest2=(TextView) findViewById(R.id.textViewHighest2);
        textViewLowest2=(TextView) findViewById(R.id.textViewLowest2);
        textViewAverage2=(TextView) findViewById(R.id.textViewAverage2);
        textViewDate2=(TextView) findViewById(R.id.textViewDate2);


        dbHelper=new DBHelper(this);
        ID=getIntent().getIntExtra("id",0);

        String highest,least,average;
        highest=dbHelper.getHighest(ID);
        least=dbHelper.getLeast(ID);
        average=dbHelper.getAverage(ID);

        Double h,l,a;
        h=Double.parseDouble(highest);
        l=Double.parseDouble(least);
        a=Double.parseDouble(average);


        textViewHighest2.setText(highest);
        textViewLowest2.setText(least);
        textViewAverage2.setText(average);

        textViewDate2.setText(dbHelper.getDate(ID));
        size=dbHelper.getDeviceConfig(ID);

        switch(size){
            case 1:

                if(NavigationDrawerActivity.thresholdSmall<h){
                    textViewHighest2.setTextColor(getResources().getColor(R.color.colorRed));
                }
                if(NavigationDrawerActivity.thresholdSmall<l){
                    textViewLowest2.setTextColor(getResources().getColor(R.color.colorRed));
                }
                if(NavigationDrawerActivity.thresholdSmall<a){
                    textViewAverage2.setTextColor(getResources().getColor(R.color.colorRed));
                }
                break;
            case 2:

                if(NavigationDrawerActivity.thresholdMedium<h){
                    textViewHighest2.setTextColor(getResources().getColor(R.color.colorRed));
                }
                if(NavigationDrawerActivity.thresholdMedium<l){
                    textViewLowest2.setTextColor(getResources().getColor(R.color.colorRed));
                }
                if(NavigationDrawerActivity.thresholdMedium<a){
                    textViewAverage2.setTextColor(getResources().getColor(R.color.colorRed));
                }
                break;
            case 3:

                if(NavigationDrawerActivity.thresholdLarge<h){
                    textViewHighest2.setTextColor(getResources().getColor(R.color.colorRed));
                }
                if(NavigationDrawerActivity.thresholdLarge<l){
                    textViewLowest2.setTextColor(getResources().getColor(R.color.colorRed));
                }
                if(NavigationDrawerActivity.thresholdLarge<a){
                    textViewAverage2.setTextColor(getResources().getColor(R.color.colorRed));
                }
        }


        listViewDetailedAnalysis =(ListView) findViewById(R.id.listViewDetailedAnalysis);
        CustomAdapter customAdapter=new CustomAdapter(this,dbHelper.getDataValues(ID),dbHelper.getTimeArrayList(ID));

        listViewDetailedAnalysis.setAdapter(customAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         getMenuInflater().inflate(R.menu.menu_analysis,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.save_item){
           sendData(ID);
        }
        return true;
    }

    public void sendData(int id) {


        String filename = "Logs";
        if(isExternalStorageWritable()) {
            File logFile = new File("/sdcard/VerticalLoadTesterDirectory/", filename + fileNumber+".xls");

            while (logFile.exists()) {
                fileNumber++;

                logFile = new File("/sdcard/VerticalLoadTesterDirectory/", filename + fileNumber+".xls");
                //  Toast.makeText(AnalysisListActivity.this, "File exists", Toast.LENGTH_SHORT).show();

            }

            try {
                //FileOutputStream outputStream=new FileOutputStream(logFile,true);
                logFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            String device_config="";
            switch(dbHelper.getDeviceConfig(id)){

                case 1:
                    device_config="NIP/PINT";
                    break;
                case 2:
                    device_config="IB QUART";
                    break;
                case 3:
                    device_config="RS/BP QUART";
            }

            saveLogs(dbHelper.getDate(id) + "  "+ dbHelper.getTime(id),logFile);
            saveLogs("-------------------------------------",logFile);

            saveLogs("Highest : " + dbHelper.getHighest(id),logFile);
            saveLogs("Least : " + dbHelper.getLeast(id),logFile);
            saveLogs("Average : " + dbHelper.getAverage(id),logFile);
            saveLogs("Size : " + device_config,logFile);
            saveLogs("-------------------------------------",logFile);
            saveLogs("Time"+"\t"+"Pressure(Kg)",logFile);


            int size=dbHelper.getTimeArrayList(id).size();

            for(int i=0; i<size;i++){


                long totalTime= dbHelper.getTimeArrayList(id).get(i);
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                Date resultdate = new Date(totalTime);
                String timeString = sdf.format(resultdate);

                saveLogs(timeString+"\t"+dbHelper.getDataValues(id).get(i),logFile);
            }

            Toast.makeText(this, "Logs saved at sdcard/VerticalLoadTesterDirectory/"+(filename+fileNumber)+".xls!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "No external storage available!", Toast.LENGTH_SHORT).show();
        }
    }
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private void saveLogs(String text,File logFile){
        if(isExternalStorageWritable()) {

            BufferedWriter buf = null;
            try {
                //BufferedWriter for performance, true to set append to file flag
                buf= new BufferedWriter(new FileWriter(logFile,true));
                buf.append(text);
                buf.newLine();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                if (buf != null)
                    try {
                        buf.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }

        }
    }
    private void saveLogs1(String text,File logFile){
        if(isExternalStorageWritable()) {

            BufferedWriter buf = null;
            try {
                //BufferedWriter for performance, true to set append to file flag
                buf= new BufferedWriter(new FileWriter(logFile,true));
                buf.append(text);
                //buf.newLine();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                if (buf != null)
                    try {
                        buf.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }

        }
    }

    private class CustomAdapter extends BaseAdapter {

        private ArrayList<Double> dataAL;
        private ArrayList<Long> timeAL;
        private Context context;

        public CustomAdapter(Context context, ArrayList<Double> data,ArrayList<Long> timeList){

            this.context=context;
            dataAL=data;
            timeAL=timeList;
        }

        @Override
        public int getCount() {
            return dataAL.size();
        }

        @Override
        public Object getItem(int i) {
            return dataAL.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            if(view==null){
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view= inflater.inflate(R.layout.message2, null);
            }

            TextView textViewKg = (TextView) view.findViewById(R.id.textViewKg);
            TextView textViewNewton = (TextView) view.findViewById(R.id.textViewNewton);
            TextView textViewTime=(TextView) view.findViewById(R.id.textViewTimeListItem);
            long hours,minutes,seconds;
            long totalSecs=timeAL.get(i);
           /* hours = totalSecs / 3600;
            minutes = (totalSecs % 3600) / 60;
            seconds = totalSecs % 60;
*/

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            Date resultdate = new Date(totalSecs);
            String timeString = sdf.format(resultdate);
            //String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);

            String kg=String.format("%.3f",dataAL.get(i));
            String newton=String.format("%.3f",dataAL.get(i)*9.8);

            switch(size){
                case 1:

                    if(NavigationDrawerActivity.thresholdSmall<dataAL.get(i)){
                        textViewKg.setTextColor(getResources().getColor(R.color.colorRed));
                        textViewNewton.setTextColor(getResources().getColor(R.color.colorRed));
                        textViewTime.setTextColor(getResources().getColor(R.color.colorRed));
                    }else{
                        textViewKg.setTextColor(getResources().getColor(android.R.color.black));
                        textViewNewton.setTextColor(getResources().getColor(android.R.color.black));
                        textViewTime.setTextColor(getResources().getColor(android.R.color.black));
                    }
                    break;
                case 2:

                    if(NavigationDrawerActivity.thresholdMedium<dataAL.get(i)){
                        textViewKg.setTextColor(getResources().getColor(R.color.colorRed));
                        textViewNewton.setTextColor(getResources().getColor(R.color.colorRed));
                        textViewTime.setTextColor(getResources().getColor(R.color.colorRed));
                    }else{
                        textViewKg.setTextColor(getResources().getColor(android.R.color.black));
                        textViewNewton.setTextColor(getResources().getColor(android.R.color.black));
                        textViewTime.setTextColor(getResources().getColor(android.R.color.black));
                    }
                    break;
                case 3:
                    if(NavigationDrawerActivity.thresholdLarge<dataAL.get(i)){
                        textViewKg.setTextColor(getResources().getColor(R.color.colorRed));
                        textViewNewton.setTextColor(getResources().getColor(R.color.colorRed));
                        textViewTime.setTextColor(getResources().getColor(R.color.colorRed));
                    }else{
                        textViewKg.setTextColor(getResources().getColor(android.R.color.black));
                        textViewNewton.setTextColor(getResources().getColor(android.R.color.black));
                        textViewTime.setTextColor(getResources().getColor(android.R.color.black));
                    }
            }

            textViewKg.setText(kg);
            textViewNewton.setText(newton);
            textViewTime.setText(timeString);


            return view;
        }
    }
}
