package com.example.nitishbhaskar.cherrypick;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.Firebase;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class BuyFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    RecyclerView myRecyclerView;
    LinearLayoutManager myLayoutManager;
    ProductData productList;
    MyFirebaseRecyclerAdapter myFirebaseRecylerAdapter;

    public static BuyFragment newInstance(int sectionNumber){
        BuyFragment buyFragment = new BuyFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        buyFragment.setArguments(args);
        return buyFragment;
    }

    public BuyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_buy, container, false);
        myRecyclerView = (RecyclerView) rootView.findViewById(R.id.cardList);
        productList = new ProductData();
        final ICardClickListener activityClickListener;
        try {
            activityClickListener = (ICardClickListener) rootView.getContext();
        }
        catch(ClassCastException e){
            throw new ClassCastException("Implementation missed out.");
        }
        myRecyclerView.setHasFixedSize(true);
        myLayoutManager = new LinearLayoutManager(getActivity());
        myRecyclerView.setLayoutManager(myLayoutManager);

        final Firebase ref = new Firebase(getString(R.string.firebaseProducts));

        myFirebaseRecylerAdapter = new MyFirebaseRecyclerAdapter(Product.class,R.layout.fragment_card_view,
                MyFirebaseRecyclerAdapter.ProductViewHolder.class, ref, getActivity());

        //myRecyclerAdapter = new RecyclerViewAdapter(getActivity(), movieList.getMoviesList());

        myRecyclerView.setAdapter(myFirebaseRecylerAdapter);

        if(productList.getSize() ==0){
            productList.setAdapter(myFirebaseRecylerAdapter);
            productList.setContext(getActivity());
            productList.initializeDataFromCloud();
        }

        myFirebaseRecylerAdapter.setOnItemClickListener(new IClickListener() {
            @Override
            public void btnClickListener(int buttonId) {

            }

            @Override
            public void viewClickListener(View view, int position) {
                HashMap<String, ?> product = (HashMap<String, ?>) productList.getItem(position);
                activityClickListener.onCardViewClick(product);
            }

            @Override
            public void viewLongClickListener(View view, int position) {

            }

            @Override
            public void cardMenuClickListener(View view, final int position) {

            }
        });

        return rootView;
    }

}
