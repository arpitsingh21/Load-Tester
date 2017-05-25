package com.sahirwebsolutions.verticalloadtester;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Bhavya on 05-07-2016.
 */
public class SettingsFragment extends Fragment {


    private Context context;

    private CheckBox checkBoxHighest,checkBoxLowest,checkBoxAverage,checkBoxGrid;
    private RadioGroup radioGroup;
    private RadioButton radioButtonManual,radioButtonTime;
    private Button buttonSave;
    private TextView textViewSetTimeLabel;
    private NumberPicker timePicker;
    public static boolean flagStopManual=true,visibilityHighest=true,visibilityLowest=true,visibilityAverage=true,enableGrid=false;
    public static int timeInSeconds=0;
    SharedPreferences saved_values;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static SettingsFragment NewInstance(){
        return new SettingsFragment();
    }

    public SettingsFragment(){
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.layout_settings_fragment,container,false);

        TextView tvAnalysisType,tvGraphView,tvSetDuration;

        tvAnalysisType=(TextView) view.findViewById(R.id.tvAnalysisType);
        tvGraphView=(TextView) view.findViewById(R.id.tvGraphView);
        tvSetDuration=(TextView) view.findViewById(R.id.tvSetDuration);
        checkBoxGrid=(CheckBox) view.findViewById(R.id.checkBoxGrid);
        checkBoxHighest=(CheckBox) view.findViewById(R.id.checkBoxHighest);
        checkBoxAverage=(CheckBox) view.findViewById(R.id.checkBoxAverage);
        checkBoxLowest=(CheckBox) view.findViewById(R.id.checkBoxLowest);
        radioGroup=(RadioGroup) view.findViewById(R.id.radioGroup);
        radioButtonManual=(RadioButton) view.findViewById(R.id.radioButtonManual);
        radioButtonTime=(RadioButton) view.findViewById(R.id.radioButtonTime);
        buttonSave=(Button) view.findViewById(R.id.buttonSave);
        timePicker=(NumberPicker) view.findViewById(R.id.timePicker);
        textViewSetTimeLabel=(TextView) view.findViewById(R.id.textViewSetTimeLabel);

/*        Typeface face= Typeface.createFromAsset(context.getAssets(), "calibri_light.ttf");

        tvAnalysisType.setTypeface(face);
        tvGraphView.setTypeface(face);
        tvSetDuration.setTypeface(face);
        checkBoxGrid.setTypeface(face);
        checkBoxHighest.setTypeface(face);
        checkBoxAverage.setTypeface(face);
        checkBoxLowest.setTypeface(face);
        radioButtonManual.setTypeface(face);
        radioButtonTime.setTypeface(face);
        buttonSave.setTypeface(face);
        textViewSetTimeLabel.setTypeface(face);

*/

        timePicker.setEnabled(false);
        timePicker.setMinValue(1);
        timePicker.setMaxValue(1000);
        timePicker.setValue(10);
      //  timePicker.setWrapSelectorWheel(false);
        if(timeInSeconds!=0) {
            timePicker.setValue(timeInSeconds);
        }
        if(visibilityHighest)
            checkBoxHighest.setChecked(true);
        else
            checkBoxHighest.setChecked(false);

        if(visibilityLowest)
            checkBoxLowest.setChecked(true);
        else
            checkBoxLowest.setChecked(false);

        if(visibilityAverage)
            checkBoxAverage.setChecked(true);
        else
            checkBoxAverage.setChecked(false);

        if(flagStopManual) {
            radioButtonManual.setChecked(true);
        }
        else {
            radioButtonTime.setChecked(true);
            timePicker.setEnabled(true);
        }
        if(enableGrid)
            checkBoxGrid.setChecked(true);
        else
            checkBoxGrid.setChecked(false);

        timePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // TODO Auto-generated method stub

           //    timeInSeconds=newVal;

            }
        });

        radioButtonTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    timePicker.setEnabled(true);

                }
                else {
                    timePicker.setEnabled(false);
                }
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkBoxHighest.isChecked()){
                    visibilityHighest=true;
                }else{
                    visibilityHighest=false;
                }

                if(checkBoxAverage.isChecked()){
                    visibilityAverage=true;
                }else{
                    visibilityAverage=false;
                }

                if(checkBoxLowest.isChecked()){
                    visibilityLowest=true;

                }else{
                    visibilityLowest=false;

                }
                if(checkBoxGrid.isChecked()){
                    enableGrid=true;

                }else{
                    enableGrid=false;

                }

                if(radioGroup.getCheckedRadioButtonId()==radioButtonManual.getId()){
                    flagStopManual=true;
                    timeInSeconds=0;

                }else{
                    flagStopManual=false;
                    timeInSeconds=timePicker.getValue();

                }
                SharedPreferences saved_values = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                SharedPreferences.Editor editor=saved_values.edit();
                editor.putBoolean("visibilityHighest",visibilityHighest);
                editor.putBoolean("visibilityLowest",visibilityLowest);
                editor.putBoolean("visibilityAverage",visibilityAverage);
                editor.putBoolean("flagStopManual",flagStopManual);
                editor.putBoolean("enableGrid",enableGrid);
                editor.putInt("timeInSeconds",timeInSeconds);
                editor.commit();




                Toast.makeText(context, "Changes have been saved!", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context=context;
    }

    @Override
    public void onResume() {
        super.onResume();
       // getRetainInstance();
        if(NavigationDrawerActivity.fragmentId2==2)
            NavigationDrawerActivity.fragmentId=2;
    }


    @Override
    public void onPause() {
        super.onPause();
      //  setRetainInstance(true);
    }
}
