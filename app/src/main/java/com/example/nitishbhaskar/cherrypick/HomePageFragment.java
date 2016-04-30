package com.example.nitishbhaskar.cherrypick;


import android.animation.Animator;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import link.fls.swipestack.SwipeStack;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomePageFragment extends Fragment implements SwipeStack.SwipeStackListener {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String SHOWCASE_ID = "Sequence 1";
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private ITileClickListener tileClickListener = null;
    RelativeLayout relativeLayout;
    private ArrayList<String> mData;

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
        Button myPageTile = (Button)view.findViewById(R.id.myPageTile);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        relativeLayout = (RelativeLayout)view.findViewById(R.id.homePageFragment);
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(getActivity(), SHOWCASE_ID);

        sequence.setConfig(config);

        sequence.addSequenceItem(buyTile,
                "Click this button to see all the products that are on sale", "GOT IT");

        sequence.addSequenceItem(sellTile,
                "Click this button to sell your product", "GOT IT");

        sequence.addSequenceItem(myPageTile,
                "Click this button to view your products", "GOT IT");

        sequence.addSequenceItem(fab,"Click this button to give voice commands to navigate to Buy, Sell, Exchange pages","GOT IT");
        sequence.start();


        buyTile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tileClickListener.tileClicked(R.id.buyTile, v);
            }
        });

        sellTile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tileClickListener.tileClicked(R.id.sellTile, v);
            }
        });

        myPageTile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tileClickListener.tileClicked(R.id.myPageTile, v);
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptSpeechInput();
            }
        });

        mData = new ArrayList<>();
        SwipeStack swipeStack = (SwipeStack)view.findViewById(R.id.swipeStack);
        fillWithData();
        swipeStack.setAdapter(new SwipeStackAdapter(mData));

        return view;
    }

    private void fillWithData() {
        mData.add("Tips and Tricks");
        mData.add("Did you know?");
        mData.add("You can navigate using voice commands.");
        mData.add("Go to My Page to explore beta features");
        mData.add("Go to About me to know about this app makers");
        mData.add("You can share the product information via email and sms");
        mData.add("There is a barcode scanner in My Page.");
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
                        if(text.toLowerCase().contains("buy")||text.toLowerCase().contains("products")){
                            tileClickListener.tileClicked(R.id.buyTile,getView());
                        }
                        if(text.toLowerCase().contains("sell")){
                            tileClickListener.tileClicked(R.id.sellTile,getView());
                        }
                        if(text.toLowerCase().contains("my page")){
                            tileClickListener.tileClicked(R.id.exchangeTile,getView());
                        }
                    }
                }
            }

        }
    }


    @Override
    public void onViewSwipedToLeft(int position) {

    }

    @Override
    public void onViewSwipedToRight(int position) {

    }

    @Override
    public void onStackEmpty() {

    }

    public class SwipeStackAdapter extends BaseAdapter {

        private List<String> mData;

        public SwipeStackAdapter(List<String> data) {
            this.mData = data;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public String getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.card, parent, false);
            }

            TextView textViewCard = (TextView) convertView.findViewById(R.id.textViewCard);
            textViewCard.setText(mData.get(position));
            textViewCard.setTextColor(getResources().getColor(R.color.textColor));
            textViewCard.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            textViewCard.setAlpha(0.85f);
            return convertView;
        }
    }
}
