package com.example.nitishbhaskar.cherrypick;


import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.konifar.fab_transformation.FabTransformation;

import org.w3c.dom.Text;

import mehdi.sakout.fancybuttons.FancyButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class DrawingFragment extends Fragment{

    View view;
    ITileClickListener tileClickListener;
    public DrawingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater,final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         view = inflater.inflate(R.layout.fragment_drawing, container, false);
        final FancyButton drawButton = (FancyButton) view.findViewById(R.id.draw);
        final FancyButton clearButton = (FancyButton)view.findViewById(R.id.clearButon);
        final SimpleDrawingView simpleDrawingView = (SimpleDrawingView)view.findViewById(R.id.simpleDrawingView1);

        try {
            tileClickListener = (ITileClickListener) view.getContext();
        }
        catch(ClassCastException e){
            throw new ClassCastException("Implementation missed out.");
        }
        drawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FabTransformation.with(drawButton).transformTo(clearButton);
                simpleDrawingView.setVisibility(View.VISIBLE);
                drawButton.setVisibility(View.INVISIBLE);
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Canvas canvas = new Canvas();
                canvas.drawColor(0, PorterDuff.Mode.CLEAR);
                simpleDrawingView.draw(canvas);
                simpleDrawingView.setVisibility(View.INVISIBLE);
                tileClickListener.tileClicked(R.id.draw, v);
            }
        });


        return view;
    }


}
