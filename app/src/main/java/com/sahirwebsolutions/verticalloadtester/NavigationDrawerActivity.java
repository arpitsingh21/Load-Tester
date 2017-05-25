package com.sahirwebsolutions.verticalloadtester;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ntt.customgaugeview.library.GaugeView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Bhavya on 04-07-2016.
 */
public class NavigationDrawerActivity extends AppCompatActivity {



    private static final int REQUEST_CONNECT_DEVICE = 1;
    // Intent request codes

    private static final int REQUEST_ENABLE_BT = 2;

    public static final String KEY_BLUETOOTH_DEVICE="BluetoothDevice";
    public static int fragmentId=0,fragmentId2=0;
    /*
     fragmentId=0 for simple LineChartFragment.
     fragmentId=1 for simple AnalysisFragment.
     */


    public static int fragment_position=0;
    //0 for home
    //1 for analysis
    //2 for settings

    public static ArrayList<Double> arrayListFloatValues;
    public static ArrayList<Long> arrayListTimeLongValues;
    int lastClick=1;

    public static int CONNECTION_STATUS=0; //Not connected
    private ProgressDialog progress=null;
    // Layout Views

    private TextView textViewCountDown;
    public static int lineChartHeight,shaderHeight,frameLayoutHeight,lineChartWidth;

    private String mConnectedDeviceName = null;

    private StringBuffer stringBuffer;

    private BluetoothChatService chatService = null;

    boolean bluetoothOn=false;
    private BluetoothDevice device=null;



    //private Button buttonConnect;
    private BluetoothAdapter bluetoothAdapter = null;
    private boolean pageActive=false;
    public static boolean connectVisible=true;
    private MainFragment mainFragment;
    private ListView listViewNavDrawer;
    private ArrayAdapter<String> arrayAdapter;
    private AnalysisFragment analysisFragment;
    private SettingsFragment settingsFragment;
    private Button buttonConnectMainPage;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private int flag=1;
    private long storedTime,difference;
    private String activityTitle;


    public static int thresholdSmall=10;
    public static int thresholdMedium=10;
    public static int thresholdLarge=10;

    public static int deviceCheck=0;
    //1 for small
    //2 for medium
    //3 for large

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_nav_drawer);

    //    getSupportActionBar().setDisplayShowTitleEnabled(false);
//            getSupportActionBar().setTitle("");

        int width,height;

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        height = metrics.heightPixels;
        width = metrics.widthPixels;

        frameLayoutHeight=(int) (height*0.5);
        lineChartHeight=(int) (height*0.2);
        lineChartWidth=(int) (width*0.2);
        shaderHeight=(int) (height*0.3);



        arrayListFloatValues=new ArrayList<>();
        arrayListTimeLongValues=new ArrayList<>();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
          //      WindowManager.LayoutParams.FLAG_FULLSCREEN);




        // If the adapter is null, then Bluetooth is not supported
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            this.finish();
        }



        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setSubtitle("Not Connected");
        }catch(NullPointerException e){
            e.printStackTrace();
        }




        drawerLayout=(DrawerLayout) findViewById(R.id.drawer_layout);
        activityTitle=getTitle().toString();
        listViewNavDrawer=(ListView) findViewById(R.id.listViewNavDrawer);
        LayoutInflater inflater = getLayoutInflater();

        View listHeaderView = inflater.inflate(R.layout.header_nav,null, false);
        textViewCountDown=(TextView) listHeaderView.findViewById(R.id.textViewCountDown);
        listViewNavDrawer.addHeaderView(listHeaderView);


        listViewNavDrawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                 if(lastClick==position &&  lastClick!=0){

                 }else {

                     switch (position) {
                         case 0:
                                logoAdminPage();
                             break;
                         case 1:
                             fragment_position=0;
                             mainFragment = MainFragment.NewInstance();
                             replaceFragment(mainFragment, "frag1");
                             fragmentId2 = 0;
                             drawerLayout.closeDrawers();
                             break;
                         case 2:
                             fragment_position=1;
                             File directory = new File("/sdcard/VerticalLoadTesterDirectory/");
                             if(!directory.exists())
                                directory.mkdirs();
                             analysisFragment = AnalysisFragment.NewInstance();
                             replaceFragment(analysisFragment, "analysisFragment");
                             fragmentId2 = 1;
                             drawerLayout.closeDrawers();
                             break;
                         case 3:
                             fragment_position=2;
                             if (settingsFragment == null)
                                 settingsFragment = SettingsFragment.NewInstance();
                             replaceFragment(settingsFragment, "settingsFragment");
                             fragmentId2 = 2;
                             drawerLayout.closeDrawers();
                             break;

                         default:
                           //  Toast.makeText(NavigationDrawerActivity.this, "Time for an upgrade!", Toast.LENGTH_SHORT).show();
                     }
                 }
                lastClick=position;

            }
        });
        addDrawerItems();
        setUpDrawer();


        if(savedInstanceState==null){
            mainFragment=MainFragment.NewInstance();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.frame_container, mainFragment,"frag1")
                    .commit();

        }


      /*  buttonConnect=(Button) findViewById(R.id.buttonConnect);
        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(NavigationDrawerActivity.this,DeviceListActivity.class);
                startActivityForResult(intent,REQUEST_CONNECT_DEVICE);
            }
        });
*/
    }

    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }
    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }


    private void logoAdminPage(){

        long startTime=System.currentTimeMillis();
        difference=startTime-storedTime;
        storedTime=startTime;
        String diff=""+difference;

        // Log.i("TIMEDD","storedTime : "+storedTime);
        // Log.i("TIMEDD","startTime : "+startTime);
        Log.i("TIMEDD", diff);

        if(difference<3000) {

            if (flag == 10) {
                textViewCountDown.setVisibility(View.VISIBLE);
                textViewCountDown.setText("5");
            }
            if (flag == 11) {
                textViewCountDown.setText("4");
            } else if (flag == 12)
                textViewCountDown.setText("3");
            else if (flag == 13)
                textViewCountDown.setText("2");
            else if (flag == 14)
                textViewCountDown.setText("1");
            else if (flag == 15) {
                Intent i = new Intent(this, MACupdateActivity.class);
                startActivity(i);
            }

            flag++;
        }else {
            flag = 0;
            textViewCountDown.setVisibility(View.INVISIBLE);
        }
    }

    private void addDrawerItems(){
        String[] navDrawerItems=getResources().getStringArray(R.array.nav_drawer_items);
        arrayAdapter=new ArrayAdapter<String>(this,R.layout.drawer_list_item,navDrawerItems);
        listViewNavDrawer.setAdapter(arrayAdapter);
    }

    private void setUpDrawer(){
        drawerToggle=new ActionBarDrawerToggle(this,drawerLayout,R.string.drawer_open,R.string.drawer_close){

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                try {
                    getSupportActionBar().setTitle("Settings");
                }catch(NullPointerException e){
                    Toast.makeText(NavigationDrawerActivity.this, "ActionBar "+ e, Toast.LENGTH_SHORT).show();
                }
                //invalidateOptionsMenu();
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                try {
                    getSupportActionBar().setTitle(activityTitle);
                }catch(NullPointerException e){
                    Toast.makeText(NavigationDrawerActivity.this, "ActionBar "+ e, Toast.LENGTH_SHORT).show();
                }
                //invalidateOptionsMenu();
            }
        };

        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.setDrawerListener(drawerToggle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }


    private void replaceFragment(Fragment fragment, String tag){


            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_container, fragment,tag)
                    .commit();

    }

    /**
     * Updates the status on the action bar.
     *
     * @param resId a string resource ID
     */
    private void setStatus(int resId) {

        getSupportActionBar().setSubtitle(resId);
        //textViewStatus.setText(resId);

    }

    /**
     * Updates the status on the action bar.
     *
     * @param subTitle status
     */
    private void setStatus(CharSequence subTitle) {
        getSupportActionBar().setSubtitle(subTitle);
        //textViewStatus.setText(subTitle);
    }

    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:

                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:

                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            CONNECTION_STATUS=1;
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            CONNECTION_STATUS=2;
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:

                            setStatus(R.string.title_not_connected);
                            CONNECTION_STATUS=0;
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    break;
                case Constants.MESSAGE_READ:
                    if(pageActive) {
                        byte[] readBuf = (byte[]) msg.obj;
                        // construct a string from the valid bytes in the buffer
                        String readMessage = new String(readBuf, 0, msg.arg1);

                        int batteryValue=msg.arg2;
                        try {
                            if (fragment_position == 1) {
                                analysisFragment.changeBatteryStatus(batteryValue);

                                float floatValue = Float.parseFloat(readMessage);
                               // Toast.makeText(NavigationDrawerActivity.this,floatValue+ "", Toast.LENGTH_SHORT).show();
                                if (fragmentId == 0 && mainFragment.lineChartFragmentMain != null) {
                                    mainFragment.lineChartFragmentMain.feedMultiple(floatValue, NavigationDrawerActivity.this);
                                } else if (fragmentId == 1 && analysisFragment != null) {
                                    //  analysisFragment.buttonStart=analysisFragment.lineChartFragmentLOCAL.imageButtonStart;
                                    //  analysisFragment.buttonStop=analysisFragment.lineChartFragmentLOCAL.imageButtonStop;
                                    if (analysisFragment.readData) {
                                        double doubleValue = (double) floatValue;
                                        arrayListFloatValues.add(doubleValue);
                                        arrayListTimeLongValues.add(AnalysisFragment.timeInMilliSeconds);
                                        AnalysisFragment.textViewGauge.setText(floatValue+"");
                                        GaugeView gaugeView=(GaugeView)findViewById(R.id.gauge_view);

                                        if(floatValue>100)
                                        {
                                            gaugeView.setTargetValue(100);

                                        }
                                        else
                                        {
                                            gaugeView.setTargetValue(floatValue);

                                        }

                                        AnalysisFragment.textViewGauge.setText(String.format("%.3f", floatValue * 9.8));
                                        analysisFragment.lineChartFragmentLOCAL.feedMultiple(floatValue, NavigationDrawerActivity.this);
                                    }
                                } else if (fragmentId == 2) {
                                    //do nothing.
                                }
                            }
                            }catch(NumberFormatException e){
                                Toast.makeText(NavigationDrawerActivity.this, readMessage + " is not a valid value! " + e, Toast.LENGTH_SHORT).show();
                            }catch(IllegalStateException e){
                            }
                        catch(Exception e)
                        {
                            Toast.makeText(NavigationDrawerActivity.this,"Unknown error occurred..", Toast.LENGTH_SHORT).show();}
                    }
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if(progress!=null){
                        progress.dismiss();
                        progress=null;
                    }

                    Toast.makeText(NavigationDrawerActivity.this, getResources().getString(R.string.title_connected_to)
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();

                    CONNECTION_STATUS=1;
                    break;
                case Constants.MESSAGE_TOAST:

                    Toast.makeText(NavigationDrawerActivity.this, msg.getData().getString(Constants.TOAST),
                            Toast.LENGTH_SHORT).show();

                    if(progress!=null){
                        progress.dismiss();
                        progress=null;
                    }
                    setStatus(R.string.title_not_connected);
                    if(progress!=null){
                        progress.dismiss();
                        progress=null;
                    }
                    CONNECTION_STATUS=0;
                    break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    // Log.d(TAG, "BT not enabled");
                    Toast.makeText(NavigationDrawerActivity.this, R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    private void setupChat() {

        // Initialize the BluetoothChatService to perform bluetooth connections
        chatService = new BluetoothChatService(NavigationDrawerActivity.this, mHandler);

    }


    public void connectDevice(String address) {
        // Get the device MAC address
        //String address = data.getExtras()
          //      .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        try {
            device = bluetoothAdapter.getRemoteDevice(address);
        }catch(IllegalArgumentException e){
            Toast.makeText(NavigationDrawerActivity.this, "Mac Address is not available!", Toast.LENGTH_SHORT).show();
        }
        // Attempt to connect to the device
        chatService.connect(device);

      //  replaceFragment(AnalysisFragment.NewInstance(),"analysisfragment");
        listViewNavDrawer.performItemClick(null,2,0);
    }

    @Override
    protected void onStart() {
        super.onStart();

        pageActive=true;
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else if (chatService == null) {
            setupChat();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        pageActive=true;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


        SettingsFragment.visibilityHighest=sharedPref.getBoolean("visibilityHighest",true);
        SettingsFragment.visibilityLowest=sharedPref.getBoolean("visibilityLowest",true);
        SettingsFragment.visibilityAverage=sharedPref.getBoolean("visibilityAverage",true);
        SettingsFragment.flagStopManual=sharedPref.getBoolean("flagStopManual",true);
        SettingsFragment.enableGrid=sharedPref.getBoolean("enableGrid",false);
        SettingsFragment.timeInSeconds=sharedPref.getInt("timeInSeconds",0);

        MainFragment.addressSmall=sharedPref.getString(Extras.Extra_ADDRESS_SMALL,"98:D3:31:90:2B:88");
        MainFragment.addressMedium=sharedPref.getString(Extras.Extra_ADDRESS_MEDIUM,"98:D3:31:90:2B:89");
        MainFragment.addressLarge=sharedPref.getString(Extras.Extra_ADDRESS_LARGE,"98:D3:31:90:2B:89");

        NavigationDrawerActivity.thresholdSmall=sharedPref.getInt(Extras.Extra_THRESHOLD_SMALL,10);
        NavigationDrawerActivity.thresholdMedium=sharedPref.getInt(Extras.Extra_THRESHOLD_MEDIUM,10);
        NavigationDrawerActivity.thresholdLarge=sharedPref.getInt(Extras.Extra_THRESHOLD_LARGE,10);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatService != null) {
            chatService.stop();
        }
        fragmentId=0;
    }

    @Override
    protected void onStop() {
        super.onStop();

        pageActive=false;
    }

  /*  @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save the fragment's instance
        if(settingsFragment!=null)
        getSupportFragmentManager().putFragment(outState, "settingsFragment", settingsFragment);
    }
    */

    @Override
    public void onBackPressed() {

        switch(fragment_position){

            case 0:
                new AlertDialog.Builder(this)
                        .setTitle("EXIT!")
                        .setMessage("Are you sure you want to exit the application?")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                if (chatService != null) {
                                    chatService.stop();
                                }
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .setCancelable(false)
                        .show();

                break;

            case 1:
            case 2:
                /*fragment_position=0;
                mainFragment = MainFragment.NewInstance();
                replaceFragment(mainFragment, "frag1");
                fragmentId2 = 0;
                drawerLayout.closeDrawers();
                */
                listViewNavDrawer.performItemClick(null,1,0);

        }

    }
}
