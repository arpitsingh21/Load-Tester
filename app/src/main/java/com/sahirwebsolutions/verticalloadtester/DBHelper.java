package com.sahirwebsolutions.verticalloadtester;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Bhavya on 02-04-2016.
 */
public class DBHelper extends SQLiteOpenHelper {


    public static final String DB_NAME="VerticalLoaderTester.db";
    private static final int DATABASE_VERSION = 2;
    public static final String TABLE_NAME="DataLogs";

    private static final String create_table="create table "+ TABLE_NAME + "(id integer primary key autoincrement default 1,time text not null," +
            "date text not null,highest text not null,least text not null,device_config integer not null, average text not null,data text not null,timearray text not null)";

    private static final String delete_table="DROP TABLE IF EXISTS "+TABLE_NAME;

    private Context context;


    public DBHelper(Context context){
        super(context, DB_NAME, null, DATABASE_VERSION);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(create_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL(delete_table);

        onCreate(sqLiteDatabase);
    }

    public void saveRecord(String highest,String least,String average,ArrayList<Double> data,ArrayList<Long> timearray,int device_config){

        JSONObject object=new JSONObject();
        JSONObject object2=new JSONObject();
        try{
        object.put("arrayList",new JSONArray(data));
            object2.put("timearraylist",new JSONArray(timearray));
        }catch(JSONException e){
            e.printStackTrace();
        }
        String stringArrayList=object.toString();
        String stringTimeArray=object2.toString();

        SimpleDateFormat dateFormat=new SimpleDateFormat("dd.MM.yyyy");
        Date date=new Date();

        SimpleDateFormat timeFormat=new SimpleDateFormat("HH:mm");
        Date time=new Date();

        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("date", dateFormat.format(date));
        contentValues.put("time", timeFormat.format(time));
        contentValues.put("highest", highest);
        contentValues.put("least", least);
        contentValues.put("average",average);
        contentValues.put("data",stringArrayList);
        contentValues.put("timearray",stringTimeArray);
        contentValues.put("device_config",device_config);

        db.insert(TABLE_NAME, null, contentValues);
    }

    public void removeRecord(int id){

        SQLiteDatabase db=this.getWritableDatabase();
        db.delete(TABLE_NAME, "id = ? ", new String[]{Integer.toString(id)});
    }

    public ArrayList<Integer> getIds(){

        ArrayList<Integer> tableIds=new ArrayList<>();
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery("select id from " + TABLE_NAME + " order by id asc", null);
        if(cursor!=null) {
            // Toast.makeText(context, "Cursor is nottttt null", Toast.LENGTH_SHORT).show();
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                tableIds.add(cursor.getInt(0));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return tableIds;
    }

    public String getHighest(int id){

        String highest=null;
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select highest from "+TABLE_NAME+" where id= ?",new String[]{String.valueOf(id)});

        int idColumnHighest=cursor.getColumnIndexOrThrow("highest");
        if(cursor!=null) {
          //  Toast.makeText(context, "Cursor is nottttt null", Toast.LENGTH_SHORT).show();
            cursor.moveToFirst();
            highest = cursor.getString(idColumnHighest);
        }else
            Toast.makeText(context, "Cursor is null", Toast.LENGTH_SHORT).show();
        cursor.close();

        return highest;
    }

    public String getLeast(int id){

        String least=null;
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select least from "+TABLE_NAME+" where id= ?",new String[]{String.valueOf(id)});

        int idColumnLeast=cursor.getColumnIndexOrThrow("least");
        if(cursor!=null) {
            //  Toast.makeText(context, "Cursor is nottttt null", Toast.LENGTH_SHORT).show();
            cursor.moveToFirst();
            least = cursor.getString(idColumnLeast);
        }else
            Toast.makeText(context, "Cursor is null", Toast.LENGTH_SHORT).show();
        cursor.close();

        return least;
    }

    public String getAverage(int id){

        String average=null;
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select average from "+TABLE_NAME+" where id= ?",new String[]{String.valueOf(id)});

        int idColumnAverage=cursor.getColumnIndexOrThrow("average");
        if(cursor!=null) {
            //  Toast.makeText(context, "Cursor is nottttt null", Toast.LENGTH_SHORT).show();
            cursor.moveToFirst();
            average = cursor.getString(idColumnAverage);
        }else
            Toast.makeText(context, "Cursor is null", Toast.LENGTH_SHORT).show();
        cursor.close();

        return average;
    }

    public int getDeviceConfig(int id){

        int device=0;
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select device_config from "+TABLE_NAME+" where id= ?",new String[]{String.valueOf(id)});

        int idColumnHighest=cursor.getColumnIndexOrThrow("device_config");
        if(cursor!=null) {
            //  Toast.makeText(context, "Cursor is nottttt null", Toast.LENGTH_SHORT).show();
            cursor.moveToFirst();
            device = cursor.getInt(idColumnHighest);
        }else
            Toast.makeText(context, "Cursor is null", Toast.LENGTH_SHORT).show();
        cursor.close();

        return device;
    }



    public ArrayList<String> getAverageList(){

        ArrayList<String> arrayList=new ArrayList<>();
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select average from "+TABLE_NAME,null);

        int idColumnAverage=cursor.getColumnIndexOrThrow("average");
        if(cursor!=null) {
            //  Toast.makeText(context, "Cursor is nottttt null", Toast.LENGTH_SHORT).show();
            cursor.moveToFirst();
            do{
                arrayList.add(cursor.getString(idColumnAverage));
            }while(cursor.moveToNext());

        }else
            Toast.makeText(context, "Cursor is null", Toast.LENGTH_SHORT).show();
        cursor.close();

        return arrayList;
    }

    public ArrayList<String> getLeastList(){

        ArrayList<String> arrayList=new ArrayList<>();
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select least from "+TABLE_NAME,null);

        int idColumnAverage=cursor.getColumnIndexOrThrow("least");
        if(cursor!=null) {
            //  Toast.makeText(context, "Cursor is nottttt null", Toast.LENGTH_SHORT).show();
            cursor.moveToFirst();
            do{
                arrayList.add(cursor.getString(idColumnAverage));
            }while(cursor.moveToNext());

        }else
            Toast.makeText(context, "Cursor is null", Toast.LENGTH_SHORT).show();
        cursor.close();

        return arrayList;
    }
    public ArrayList<String> getHighestList(){

        ArrayList<String> arrayList=new ArrayList<>();
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select highest from "+TABLE_NAME,null);

        int idColumnAverage=cursor.getColumnIndexOrThrow("highest");
        if(cursor!=null) {
            //  Toast.makeText(context, "Cursor is nottttt null", Toast.LENGTH_SHORT).show();
            cursor.moveToFirst();
            do{
                arrayList.add(cursor.getString(idColumnAverage));
            }while(cursor.moveToNext());

        }else
            Toast.makeText(context, "Cursor is null", Toast.LENGTH_SHORT).show();
        cursor.close();

        return arrayList;
    }

    public ArrayList<String> getTimeList(){

        ArrayList<String> arrayList=new ArrayList<>();
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select time from "+TABLE_NAME,null);

        int idColumnAverage=cursor.getColumnIndexOrThrow("time");
        if(cursor!=null) {
            //  Toast.makeText(context, "Cursor is nottttt null", Toast.LENGTH_SHORT).show();
            cursor.moveToFirst();
            do{
                arrayList.add(cursor.getString(idColumnAverage));
            }while(cursor.moveToNext());

        }else
            Toast.makeText(context, "Cursor is null", Toast.LENGTH_SHORT).show();
        cursor.close();

        return arrayList;
    }
    public ArrayList<String> getDateList(){

        ArrayList<String> arrayList=new ArrayList<>();
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select date from "+TABLE_NAME,null);

        int idColumnAverage=cursor.getColumnIndexOrThrow("date");
        if(cursor!=null) {
            //  Toast.makeText(context, "Cursor is nottttt null", Toast.LENGTH_SHORT).show();
            cursor.moveToFirst();
            do{
                arrayList.add(cursor.getString(idColumnAverage));
            }while(cursor.moveToNext());

        }else
            Toast.makeText(context, "Cursor is null", Toast.LENGTH_SHORT).show();
        cursor.close();

        return arrayList;
    }

    public String getDate(int id){

        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select date from "+TABLE_NAME+" where id="+id+"",null);
        cursor.moveToFirst();
        String string=cursor.getString(0);
        cursor.close();
        return string;
    }

    public String getTime(int id){

        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select time from "+TABLE_NAME+" where id="+id+"",null);
        cursor.moveToFirst();
        String string=cursor.getString(0);
        cursor.close();
        return string;
    }



    public ArrayList<Double> getDataValues(int id){

        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select data from " + TABLE_NAME + " where id=" + id + "", null);
        cursor.moveToFirst();
        String string=cursor.getString(0);

        ArrayList<Double> arrayList=new ArrayList<>();

        try{
            JSONObject object=new JSONObject(string);
            JSONArray array=object.optJSONArray("arrayList");

            for(int i=0;i<array.length();i++){

                arrayList.add(array.optDouble(i));
            }

        }catch(JSONException e){
            e.printStackTrace();
        }

        cursor.close();
        return arrayList;
    }


    public ArrayList<Long> getTimeArrayList(int id){

        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select timearray from " + TABLE_NAME + " where id=" + id + "", null);
        cursor.moveToFirst();
        String string=cursor.getString(0);

        ArrayList<Long> arrayList=new ArrayList<>();

        try{
            JSONObject object=new JSONObject(string);
            JSONArray array=object.optJSONArray("timearraylist");

            for(int i=0;i<array.length();i++){

                arrayList.add(array.optLong(i));
            }

        }catch(JSONException e){
            e.printStackTrace();
        }

        cursor.close();
        return arrayList;
    }


    public ArrayList<Integer> getDeviceConfigList(){

        ArrayList<Integer> arrayList=new ArrayList<>();
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select device_config from "+TABLE_NAME,null);

        int idColumnAverage=cursor.getColumnIndexOrThrow("device_config");
        if(cursor!=null) {
            //  Toast.makeText(context, "Cursor is nottttt null", Toast.LENGTH_SHORT).show();
            cursor.moveToFirst();
            do{
                arrayList.add(cursor.getInt(idColumnAverage));
            }while(cursor.moveToNext());

        }else
            Toast.makeText(context, "Cursor is null", Toast.LENGTH_SHORT).show();
        cursor.close();

        return arrayList;
    }


}
