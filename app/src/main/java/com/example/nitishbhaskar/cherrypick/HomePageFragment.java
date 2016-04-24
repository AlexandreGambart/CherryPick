package com.example.nitishbhaskar.cherrypick;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomePageFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String SHOWCASE_ID = "Sequence 1";
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private ITileClickListener tileClickListener = null;

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

        try {
            tileClickListener = (ITileClickListener) view.getContext();
        }
        catch(ClassCastException e){
            throw new ClassCastException("Implementation missed out.");
        }
        Button buyTile = (Button) view.findViewById(R.id.buyTile);
        Button sellTile = (Button)view.findViewById(R.id.sellTile);
        Button exchangeTile = (Button)view.findViewById(R.id.exchangeTile);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);

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

        sequence.addSequenceItem(fab,"Click this button to give voice commands to navigate to Buy, Sell, Exchange pages","GOT IT");
        sequence.start();

        buyTile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tileClickListener.tileClicked(R.id.buyTile);
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptSpeechInput();
            }
        });

        return view;
    }


    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                "en-IN");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == -1 && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    for(String text : result ){
                        if(text.toLowerCase().contains("buy")){
                            tileClickListener.tileClicked(R.id.buyTile);
                        }
                    }
                }
            }

        }
    }



}
