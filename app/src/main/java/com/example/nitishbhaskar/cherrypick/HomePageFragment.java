package com.example.nitishbhaskar.cherrypick;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomePageFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String SHOWCASE_ID = "Sequence 1";

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
        Button sellTile = (Button)view.findViewById(R.id.sellTile);
        Button exchangeTile = (Button)view.findViewById(R.id.exchangeTile);

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(getActivity(), SHOWCASE_ID);

        sequence.setConfig(config);

        sequence.addSequenceItem(buyTile,
                "Click this button to see all the products that are on sale", "GOT IT");

        sequence.addSequenceItem(sellTile,
                "Click this button to sell your product", "GOT IT");

        sequence.addSequenceItem(exchangeTile,
                "Click this button to exchange your product with other products", "GOT IT");

        sequence.start();

        buyTile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tileClickListener.tileClicked(R.id.buyTile);
            }
        });

        return view;
    }

}
