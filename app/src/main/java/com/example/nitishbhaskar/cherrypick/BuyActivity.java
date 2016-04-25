package com.example.nitishbhaskar.cherrypick;

import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.firebase.client.Firebase;

import java.util.HashMap;

public class BuyActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ICardClickListener{

    CollapsingToolbarLayout collapsingToolbarLayout;
    ImageView toolBarImage;
    View mapfragmentView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        AppBarLayout mAppBarLayout = (AppBarLayout)findViewById(R.id.appBar);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mAppBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = new AppBarLayout.Behavior();
        behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
            @Override
            public boolean canDrag(AppBarLayout appBarLayout) {
                return false;
            }
        });
        params.setBehavior(behavior);
        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("Buy Products");
        toolBarImage = (ImageView)findViewById(R.id.toolbarImage);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        mapfragmentView = (View)findViewById(R.id.googlemap);
        toolBarImage.setVisibility(View.GONE);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        getSupportFragmentManager().beginTransaction().
                replace(R.id.buyActivityContainer, BuyFragment.newInstance(R.id.buyPageFragment))
                .commit();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();
        Intent intent;

        if (id == R.id.buyNavigation) {
            intent = new Intent(this, BuyActivity.class);
            startActivity(intent);

        } else if (id == R.id.sellNavigation) {


        } else if (id == R.id.exchangeNavigation) {

        }
        else if(id == R.id.logoutApp){
            Firebase ref = new Firebase(getString(R.string.firebaseUrl));
            ref.unauth();
            intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

    @Override
    public void onCardViewClick(HashMap<String, ?> product) {
        mapfragmentView.setVisibility(View.GONE);
        collapsingToolbarLayout.setTitle((String)product.get("productName"));
        toolBarImage.setVisibility(View.VISIBLE);
        toolBarImage.setImageResource(R.drawable.icon);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.buyActivityContainer, ProductDetailsFragment.newInstance(product))
                .addToBackStack("productInfo")
                .commit();
    }

    @Override
    public void onBackPressed() {

        int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            collapsingToolbarLayout.setTitle("Buy Products");
            super.onBackPressed();
            toolBarImage.setVisibility(View.GONE);
            mapfragmentView.setVisibility(View.VISIBLE);
            //additional code
        } else {
            getFragmentManager().popBackStack();
        }

    }
}
