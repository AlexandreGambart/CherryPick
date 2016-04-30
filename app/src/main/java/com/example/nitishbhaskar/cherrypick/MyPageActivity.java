package com.example.nitishbhaskar.cherrypick;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MyPageActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ITileClickListener {
    MyPageViewPagerAdapter viewPagerAdapter;
    ViewPager viewPager;
    ViewGroup mAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAppBar = (ViewGroup)findViewById(R.id.appBarViewPager);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        setTitle("My Page");
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);

        viewPagerAdapter = new MyPageViewPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                final float normalized_position = Math.abs(Math.abs(position) - 1);
                // page.setScaleX(normalized_position / 2 + 0.5f);
                // page.setScaleY(normalized_position/2+0.5f);


                //page.setAlpha(normalized_position);
                page.setRotationY(position * -30);
                page.setTranslationX(page.getWidth() * -position);

                if (position <= -1.0F || position >= 1.0F) {
                    page.setAlpha(0.0F);
                } else if (position == 0.0F) {
                    page.setAlpha(1.0F);
                } else {
                    // position is between -1.0F & 0.0F OR 0.0F & 1.0F
                    page.setAlpha(1.0F - Math.abs(position));
                }
            }
        });
        viewPager.setCurrentItem(0);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        SharedPreferences sharedpreferences = getSharedPreferences(getString(R.string.MyPREFERENCES), Context.MODE_PRIVATE);
        Map<String, ?> savedStrings = sharedpreferences.getAll();
        View navHeaderView = navigationView.getHeaderView(0);
        TextView username = (TextView) navHeaderView.findViewById(R.id.username);
        TextView userEmail = (TextView) navHeaderView.findViewById(R.id.useremail);
        ImageView profileImage = (ImageView) navHeaderView.findViewById(R.id.userProfileImage);
        username.setText((String) savedStrings.get(getString(R.string.Name)));
        userEmail.setText((String) savedStrings.get(getString(R.string.Email)));
        Picasso.with(getApplicationContext()).load((String) savedStrings.get(getString(R.string.ProfilePicUri))).into(profileImage);

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
    public void tileClicked(int tileId, View v) {
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(1);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
//we have a result
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();

// display it on screen
            //formatTxt.setText("FORMAT: " + scanFormat);
            //contentTxt.setText("CONTENT: " + scanContent);
            try {

                DownLoadTask task = new DownLoadTask();
                task.execute("http://api.upcdatabase.org/json/8e268419dbb13d51c153f31bc7884c88/" +scanContent);
                //task.execute("http://www.searchupc.com/handlers/upcsearch.ashx?request_type=3&access_token=E9CB1A54-9B91-4AD3-B4A3-77B3E7E261AB&upc=/"+scanContent);
                // task.execute("http://localhost:59798/api/BarCode");
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Could not find product", Toast.LENGTH_LONG);
            }

        }
    }

    public class DownLoadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result = result + current;
                    data = reader.read();
                }
                return result;
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Could not Product", Toast.LENGTH_LONG);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            String itemName = "";
            String description = "";
            String message="";
            HashMap product;
            JSONObject jsonObject = null;
            product = new HashMap();
            try {

                jsonObject = new JSONObject(result);
                //String details = jsonObject.toString();
                //JSONArray arr = new JSONArray(result);
                itemName = (String) jsonObject.get("itemname");
                description = (String) jsonObject.get("description");
                if(!itemName.isEmpty()){
                    product.put("productName", itemName);
                }else{
                    product.put("productName", " ");
                }

                if(!description.isEmpty()){
                    product.put("description", description);
                }else {
                    product.put("description", "");
                }
                product.put("datePostedOn", "");
                product.put("price", "");
                //Log.i("Product Name", itemName);
                //Log.i("Product Description",description);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            if(product.isEmpty()){
                  Toast.makeText(getApplicationContext(),"Could not find product",Toast.LENGTH_LONG);
            }else{
                Intent intent = new Intent(MyPageActivity.this,SellActivity.class);
                intent.putExtra("currentProduct",product);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(MyPageActivity.this,mAppBar, "testTransition");
                    startActivity(intent, options.toBundle());
                }
                else {
                    startActivity(intent);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {

        int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0) {

            super.onBackPressed();
            Intent intent = new Intent(MyPageActivity.this,MainActivity.class);
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
