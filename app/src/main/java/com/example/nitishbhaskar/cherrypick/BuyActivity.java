package com.example.nitishbhaskar.cherrypick;

import android.app.SharedElementCallback;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Slide;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuyActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ICardClickListener{

    CollapsingToolbarLayout collapsingToolbarLayout;
    ImageView toolBarImage;
    View mapfragmentView;
    ViewGroup mAppBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        AppBarLayout mAppBarLayout = (AppBarLayout)findViewById(R.id.appBar);
        mAppBar = (ViewGroup)findViewById(R.id.appBar);
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
        mapfragmentView = (View)findViewById(R.id.googlemap_buyPage);
        mapfragmentView.setAlpha(0.75f);
        toolBarImage.setVisibility(View.GONE);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Fetch data from shared preference to set login user details in navigation drawer
        SharedPreferences sharedpreferences = getSharedPreferences(getString(R.string.MyPREFERENCES), Context.MODE_PRIVATE);
        Map<String,?> savedStrings = sharedpreferences.getAll();
        View navHeaderView = navigationView.getHeaderView(0);
        TextView username = (TextView) navHeaderView.findViewById(R.id.username);
        TextView userEmail = (TextView) navHeaderView.findViewById(R.id.useremail);
        ImageView profileImage = (ImageView) navHeaderView.findViewById(R.id.userProfileImage);
        username.setText((String)savedStrings.get(getString(R.string.Name)));
        userEmail.setText((String) savedStrings.get(getString(R.string.Email)));
        Picasso.with(getApplicationContext()).load((String) savedStrings.get(getString(R.string.ProfilePicUri))).into(profileImage);

        getSupportFragmentManager().beginTransaction().
                replace(R.id.buyActivityContainer, BuyFragment.newInstance(R.id.buyPageFragment))
                .commit();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();
        Intent intent;
        if (id == R.id.Homepage) {
            intent = new Intent(this, MainActivity.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(this,this.mAppBar, "testTransition");
                startActivity(intent, options.toBundle());
            }
            else {
                startActivity(intent);
            }
        }
        else if (id == R.id.buyNavigation) {
            intent = new Intent(this, BuyActivity.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(this,this.mAppBar, "testTransition");
                startActivity(intent, options.toBundle());
            }
            else {
                startActivity(intent);
            }

        } else if (id == R.id.sellNavigation) {
            intent = new Intent(this,SellActivity.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(this,this.mAppBar, "testTransition");
                startActivity(intent, options.toBundle());
            }
            else {
                startActivity(intent);
            }

        } else if (id == R.id.exchangeNavigation) {

        }
        else if(id == R.id.logoutApp){
            Firebase ref = new Firebase(getString(R.string.firebaseUrl));
            ref.unauth();
            intent = new Intent(this,LoginActivity.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(this,this.mAppBar, "testTransition");
                startActivity(intent, options.toBundle());
            }
            else {
                startActivity(intent);
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

    @Override
    public void onCardViewClick(HashMap<String, ?> product,View v) {
        mapfragmentView.setVisibility(View.GONE);
        collapsingToolbarLayout.setTitle((String) product.get("productName"));
        toolBarImage.setVisibility(View.VISIBLE);
        toolBarImage.setImageResource(R.drawable.icon);

        ProductDetailsFragment details = ProductDetailsFragment.newInstance(product);
        details.setSharedElementEnterTransition(new DetailsTransition());
        //details.setEnterTransition(new Fade());
         details.setEnterTransition(new Slide());
        details.setExitTransition(new Slide());
        details.setSharedElementReturnTransition(new DetailsTransition());

        TextView productNameTrans = (TextView)v.findViewById(R.id.productTitle);
        getSupportFragmentManager().beginTransaction()
                .addSharedElement(productNameTrans, productNameTrans.getTransitionName())
                .replace(R.id.buyActivityContainer, details)
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
