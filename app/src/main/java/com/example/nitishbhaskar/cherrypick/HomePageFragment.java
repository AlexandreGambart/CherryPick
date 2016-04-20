package com.example.nitishbhaskar.cherrypick;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomePageFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    public static HomePageFragment newInstance(int sectionNumber){
        HomePageFragment homePageFragment = new HomePageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        homePageFragment.setArguments(args);
        return homePageFragment;
    }

    public HomePageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);
        final ITileClickListener tileClickListener;
        try {
            tileClickListener = (ITileClickListener) view.getContext();
        }
        catch(ClassCastException e){
            throw new ClassCastException("Implementation missed out.");
        }
        Button buyTile = (Button) view.findViewById(R.id.buyTile);
        buyTile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tileClickListener.tileClicked(R.id.buyTile);
            }
        });

        return view;
    }

}
