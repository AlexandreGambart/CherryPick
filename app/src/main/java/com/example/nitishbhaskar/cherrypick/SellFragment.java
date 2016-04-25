package com.example.nitishbhaskar.cherrypick;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class SellFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    public SellFragment() {
        // Required empty public constructor
    }

    public static SellFragment newInstance(int sectionNumber){
        SellFragment sellFragment = new SellFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        sellFragment.setArguments(args);
        return sellFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sell, container, false);
    }

}
