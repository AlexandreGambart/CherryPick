package com.example.nitishbhaskar.cherrypick;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class SellActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, INavigate{
        ViewGroup mAppBar;
    HashMap<String, ?> currentProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Sell Product");
        mAppBar = (ViewGroup)findViewById(R.id.appBarSell);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        currentProduct = (HashMap<String, ?>) getIntent().getSerializableExtra("currentProduct");

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        SharedPreferences sharedpreferences = getSharedPreferences(getString(R.string.MyPREFERENCES), Context.MODE_PRIVATE);
        Map<String, ?> savedStrings = sharedpreferences.getAll();
        View navHeaderView = navigationView.getHeaderView(0);
        TextView username = (TextView) navHeaderView.findViewById(R.id.username);
        TextView userEmail = (TextView) navHeaderView.findViewById(R.id.useremail);
        ImageView profileImage = (ImageView) navHeaderView.findViewById(R.id.userProfileImage);
        username.setText((String) savedStrings.get(getString(R.string.Name)));
        userEmail.setText((String) savedStrings.get(getString(R.string.Email)));
        Picasso.with(getApplicationContext()).load((String) savedStrings.get(getString(R.string.ProfilePicUri))).into(profileImage);

        navigationView.setNavigationItemSelectedListener(this);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.sellActivityContainer, SellFragment.newInstance(R.id.sellPageFragment, currentProduct))
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
            intent = new Intent(this, MyPageActivity.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(this,this.mAppBar, "testTransition");
                startActivity(intent, options.toBundle());
            }
            else {
                startActivity(intent);
            }
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
        else if (id == R.id.aboutUs) {
            intent = new Intent(this, AboutUsActivity.class);
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
    public void navigateToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {

        int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0) {

            super.onBackPressed();
            Intent intent = new Intent(SellActivity.this,MainActivity.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(intent);
            }
            else {
                startActivity(intent);
            }


            //additional code
        } else {
            getFragmentManager().popBackStack();
        }

    }
}
