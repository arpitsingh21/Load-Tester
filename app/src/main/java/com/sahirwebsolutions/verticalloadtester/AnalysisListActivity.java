package com.sahirwebsolutions.verticalloadtester;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
public class AnalysisListActivity extends AppCompatActivity {


    private DBHelper dbHelper;
    ListView listViewAnalysis;
    CustomAdapter customAdapter;
    int fileNumber=1;

    public AnalysisListActivity(){
        dbHelper=new DBHelper(this);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.analysis_list_activity);

        TextView tvAnalysis=(TextView) findViewById(R.id.textViewAnalysis);
  //      Typeface face= Typeface.createFromAsset(getAssets(), "calibri_light.ttf");
    //    tvAnalysis.setTypeface(face);

        listViewAnalysis=(ListView) findViewById(R.id.listViewAnalysis);
        customAdapter=new CustomAdapter(this,dbHelper.getHighestList(),dbHelper.getLeastList(),dbHelper.getAverageList(),
                dbHelper.getDateList(),dbHelper.getTimeList(),dbHelper.getDeviceConfigList());

       // LayoutInflater inflater=getLayoutInflater();
       // View view=inflater.inflate(R.layout.analysis_list_header,null);
       // listViewAnalysis.addHeaderView(view);
     //   listViewAnalysis.setHeaderDividersEnabled(true);
        listViewAnalysis.setAdapter(customAdapter);


        listViewAnalysis.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                ArrayList<Integer> ids=dbHelper.getIds();
                Intent intent=new Intent(AnalysisListActivity.this,DetailedAnalysisActivity.class);
                intent.putExtra("id",ids.get(i));
                startActivity(intent);
            }
        });

        registerForContextMenu(listViewAnalysis);

    }



    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        //  menu.setHeaderTitle("Select The Action");
        menu.add(0, v.getId(), 0, "Delete");//groupId, itemId, order, title
        menu.add(0, v.getId(), 0, "Save in file");//groupId, itemId, order, title

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int index = info.position;
        int id=dbHelper.getIds().get(index);
        if (item.getTitle() == "Delete") {
            dbHelper.removeRecord(id);
            customAdapter.refresh();
//            customAdapter.notifyDataSetChanged();
        } else if (item.getTitle() == "Save in file") {

            sendData(id);

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

            int size=dbHelper.getTimeArrayList(id).size();

            for(int i=0; i<size;i++){

               long totalTime= dbHelper.getTimeArrayList(id).get(i);
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                Date resultdate = new Date(totalTime);
                String timeString = sdf.format(resultdate);
                saveLogs(timeString+" : "+dbHelper.getDataValues(id).get(i),logFile);
            }

            Toast.makeText(this, "Logs saved at sdcard/VerticalLoadTesterDirectory/"+(filename+fileNumber)+".xls!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(AnalysisListActivity.this, "No external storage available!", Toast.LENGTH_SHORT).show();
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
                buf = new BufferedWriter(new FileWriter(logFile, true));
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




    private class CustomAdapter extends BaseAdapter{

        private ArrayList<String> highestAL,leastAL,averageAL,dateAL,timeAL;
        private ArrayList<Integer> deviceAL;
        private Context context;

        public CustomAdapter(Context context, ArrayList<String> highest,ArrayList<String> least,ArrayList<String> average,
                             ArrayList<String> date,ArrayList<String> time,ArrayList<Integer> device){

            this.context=context;
            highestAL=highest;
            leastAL=least;
            averageAL=average;
            dateAL=date;
            timeAL=time;
            deviceAL=device;
        }

        public void refresh(){
            //highestAL.clear();


            highestAL=dbHelper.getHighestList();
            leastAL=dbHelper.getLeastList();
            averageAL=dbHelper.getAverageList();
            dateAL=dbHelper.getDateList();
            timeAL=dbHelper.getTimeList();
            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return highestAL.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            if(view==null){
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view= inflater.inflate(R.layout.message, null);
            }

            TextView textViewHighest1 = (TextView) view.findViewById(R.id.textViewHighest1);
            TextView textViewLowest1 = (TextView) view.findViewById(R.id.textViewLowest1);
            TextView textViewAverage1 = (TextView) view.findViewById(R.id.textViewAverage1);
            TextView textViewDate=(TextView) view.findViewById(R.id.textViewDate);
            TextView textViewTime=(TextView) view.findViewById(R.id.textViewTime);
            TextView textViewDeviceConfig=(TextView) view.findViewById(R.id.textViewDeviceConfig);
            TextView textViewHighestLabel1 = (TextView) view.findViewById(R.id.textViewHighestLabel1);
            TextView textViewLowestLabel1 = (TextView) view.findViewById(R.id.textViewLowestLabel1);
            TextView textViewAverageLabel1 = (TextView) view.findViewById(R.id.textViewAverageLabel1);

            textViewHighest1.setText(highestAL.get(i));
            textViewLowest1.setText(leastAL.get(i));
            textViewAverage1.setText(averageAL.get(i));
            textViewDate.setText(dateAL.get(i));
            textViewTime.setText(timeAL.get(i));

            switch(deviceAL.get(i)){
                case 1:
                    textViewDeviceConfig.setText("NIP/PINT");

                    if(NavigationDrawerActivity.thresholdSmall<Float.parseFloat(highestAL.get(i))){
                        textViewHighest1.setTextColor(getResources().getColor(R.color.colorRed));
                    }else{
                        textViewHighest1.setTextColor(getResources().getColor(android.R.color.black));
                    }
                    break;
                case 2:
                    textViewDeviceConfig.setText("IB QUART");

                    if(NavigationDrawerActivity.thresholdMedium<Float.parseFloat(highestAL.get(i))){
                        textViewHighest1.setTextColor(getResources().getColor(R.color.colorRed));
                    }else{
                        textViewHighest1.setTextColor(getResources().getColor(android.R.color.black));
                    }
                    break;
                case 3:
                    textViewDeviceConfig.setText("RS/BP QUART");

                    if(NavigationDrawerActivity.thresholdLarge<Float.parseFloat(highestAL.get(i))){
                        textViewHighest1.setTextColor(getResources().getColor(R.color.colorRed));
                    }else{
                        textViewHighest1.setTextColor(getResources().getColor(android.R.color.black));
                    }
            }

         /*   Typeface face= Typeface.createFromAsset(context.getAssets(), "calibri_light.ttf");

            textViewHighest1.setTypeface(face);
            textViewLowest1.setTypeface(face);
            textViewAverage1.setTypeface(face);
            textViewDate.setTypeface(face);
            textViewTime.setTypeface(face);
            textViewHighestLabel1.setTypeface(face);
            textViewLowestLabel1.setTypeface(face);
            textViewAverageLabel1.setTypeface(face);
*/
            return view;
        }
    }
}

