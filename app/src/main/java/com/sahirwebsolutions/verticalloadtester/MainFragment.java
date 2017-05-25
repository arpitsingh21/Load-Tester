    package com.sahirwebsolutions.verticalloadtester;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Bhavya on 07-07-2016.
 */
public class MainFragment extends Fragment {




    public Button buttonSmall,buttonMedium,buttonLarge;
    public LineChartFragment lineChartFragmentMain;

    public static String addressSmall="98:D3:31:90:2B:88";
    public static String addressMedium="98:D3:31:90:2B:89";
    public static String addressLarge="98:D3:31:90:2B:89";



    //   private int count=0,graphs=1,usedgraphs=0;
    private Context context;

    public static MainFragment NewInstance(){
        return new MainFragment();
    }

    public MainFragment(){

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.layout_main_fragment,container,false);

        buttonSmall=(Button) view.findViewById(R.id.buttonSmall);
        buttonMedium=(Button) view.findViewById(R.id.buttonMedium);
        buttonLarge=(Button) view.findViewById(R.id.buttonLarge);

        buttonSmall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationDrawerActivity.deviceCheck=1;
                ((NavigationDrawerActivity)getActivity()).connectDevice(addressSmall);
            }
        });

        buttonMedium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationDrawerActivity.deviceCheck=2;
                ((NavigationDrawerActivity)getActivity()).connectDevice(addressMedium);
            }
        });

        buttonLarge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationDrawerActivity.deviceCheck=3;
                ((NavigationDrawerActivity)getActivity()).connectDevice(addressLarge);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(NavigationDrawerActivity.fragmentId2==0)
            NavigationDrawerActivity.fragmentId=0;


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;

    }


}
