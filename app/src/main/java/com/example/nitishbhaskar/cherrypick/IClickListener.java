package com.example.nitishbhaskar.cherrypick;

import android.view.View;

/**
 * Created by Nitish on 2/22/2016.
 */
public interface IClickListener {
    void btnClickListener(int buttonId);
    void viewClickListener(View view, int position);
    void viewLongClickListener(View view, int position);
    void cardMenuClickListener(View view, final int position);
}
