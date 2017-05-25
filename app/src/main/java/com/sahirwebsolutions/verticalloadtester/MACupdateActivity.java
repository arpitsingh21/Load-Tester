package com.sahirwebsolutions.verticalloadtester;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Bhavya on 11-07-2016.
 */
public class MACupdateActivity extends AppCompatActivity {

    EditText etThresholdSmall,etThresholdMedium,etThresholdLarge;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.mac_update_activity);

        TextView tvsmall,tvmedium,tvlarge;

        tvsmall=(TextView) findViewById(R.id.tvsmall);
        tvmedium=(TextView) findViewById(R.id.tvmedium);
        tvlarge=(TextView) findViewById(R.id.tvlarge);
        final EditText editTextSmall,editTextMedium,editTextLarge;
        editTextSmall=(EditText) findViewById(R.id.editTextSmall);
        editTextMedium=(EditText) findViewById(R.id.editTextMedium);
        editTextLarge=(EditText) findViewById(R.id.editTextLarge);

        etThresholdSmall=(EditText) findViewById(R.id.editTextSmallThreshold);
        etThresholdMedium=(EditText) findViewById(R.id.editTextMediumThreshold);
        etThresholdLarge=(EditText) findViewById(R.id.editTextLargeThreshold);

        editTextSmall.setText(MainFragment.addressSmall);
        editTextMedium.setText(MainFragment.addressMedium);
        editTextLarge.setText(MainFragment.addressLarge);



        Button buttonSave=(Button) findViewById(R.id.buttonSaveDevice);
        Button buttonCancel=(Button) findViewById(R.id.buttonCancel);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String small,medium,large,st,mt,lt;
                small=editTextSmall.getText().toString();
                medium=editTextMedium.getText().toString();
                large=editTextLarge.getText().toString();

                st=etThresholdSmall.getText().toString();
                mt=etThresholdMedium.getText().toString();
                lt=etThresholdLarge.getText().toString();

                if(!small.equals("") && !medium.equals("") && !large.equals("") &&
                        !st.equals("") && !mt.equals("") && !lt.equals("") ){

                    MainFragment.addressSmall=small;
                    MainFragment.addressMedium=medium;
                    MainFragment.addressLarge=large;

                    NavigationDrawerActivity.thresholdSmall=Integer.parseInt(st);
                    NavigationDrawerActivity.thresholdMedium=Integer.parseInt(mt);
                    NavigationDrawerActivity.thresholdLarge=Integer.parseInt(lt);

                    SharedPreferences saved_values = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor=saved_values.edit();
                    editor.putString(Extras.Extra_ADDRESS_SMALL,MainFragment.addressSmall);
                    editor.putString(Extras.Extra_ADDRESS_MEDIUM,MainFragment.addressMedium);
                    editor.putString(Extras.Extra_ADDRESS_LARGE,MainFragment.addressLarge);

                    editor.putInt(Extras.Extra_THRESHOLD_SMALL,NavigationDrawerActivity.thresholdSmall);
                    editor.putInt(Extras.Extra_THRESHOLD_MEDIUM,NavigationDrawerActivity.thresholdMedium);
                    editor.putInt(Extras.Extra_THRESHOLD_LARGE,NavigationDrawerActivity.thresholdLarge);
                    editor.commit();

                    Toast.makeText(MACupdateActivity.this, "Addresses have been updated!", Toast.LENGTH_SHORT).show();
                }else{

                    Toast.makeText(MACupdateActivity.this, "No field can be left empty!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

      /*  Typeface face= Typeface.createFromAsset(getAssets(), "calibri_light.ttf");

        tvsmall.setTypeface(face);
        tvmedium.setTypeface(face);
        tvlarge.setTypeface(face);
        editTextSmall.setTypeface(face);
        editTextMedium.setTypeface(face);
        editTextLarge.setTypeface(face);
        buttonSave.setTypeface(face);
        buttonCancel.setTypeface(face);
*/
    }
}
