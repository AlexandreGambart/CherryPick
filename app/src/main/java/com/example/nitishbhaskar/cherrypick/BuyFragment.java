package com.example.nitishbhaskar.cherrypick;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class BuyFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private static final String ARG_SECTION_NUMBER = "section_number";
    RecyclerView myRecyclerView;
    LinearLayoutManager myLayoutManager;
    ProductData productList;
    MyFirebaseRecyclerAdapter myFirebaseRecylerAdapter;
    private SupportMapFragment mMapFragment;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    public static BuyFragment newInstance(int sectionNumber) {
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
        } catch (ClassCastException e) {
            throw new ClassCastException("Implementation missed out.");
        }
        myRecyclerView.setHasFixedSize(true);
        myLayoutManager = new LinearLayoutManager(getActivity());
        myRecyclerView.setLayoutManager(myLayoutManager);

        final Firebase ref = new Firebase(getString(R.string.firebaseProducts));

        myFirebaseRecylerAdapter = new MyFirebaseRecyclerAdapter(Product.class, R.layout.fragment_card_view,
                MyFirebaseRecyclerAdapter.ProductViewHolder.class, ref, getActivity());

        //myRecyclerAdapter = new RecyclerViewAdapter(getActivity(), movieList.getMoviesList());

        myRecyclerView.setAdapter(myFirebaseRecylerAdapter);

        if (productList.getSize() == 0) {
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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buildGoogleApiClient();
        mMapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.googlemap_buyPage);

        if (mMapFragment != null) {
            mMapFragment.getMapAsync(this);
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        if (menu.findItem(R.id.action_search) == null) {
            inflater.inflate(R.menu.search_view, menu);
        }

        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    int position = productList.findFirst(query);
                    if (position >= 0) {
                        myRecyclerView.scrollToPosition(position);
                    }

                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onConnected(Bundle bundle) {

        LocationRequest mLocationRequest = createLocationRequest();
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private void buildGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            mMap = googleMap;
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.getUiSettings().setCompassEnabled(false);
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.setMyLocationEnabled(true);

            for (Map product : productList.getProductList()) {
                String location = (String) product.get("location");
                Double latitude = Double.parseDouble(location.split(",")[0]);
                Double longitude = Double.parseDouble(location.split(",")[1]);
                LatLng place = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions().position(place));//.title(getAddress(latitude, longitude)));
            }
            LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, true);
            // Getting Current Location
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                onLocationChanged(location);
            }

        } catch (SecurityException se) {

        }
    }


    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng_Now = new LatLng(location.getLatitude(), location.getLongitude());
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng_Now)      // Sets the center of the map to LatLng (refer to previous snippet)
                .zoom(13)                   // Sets the zoom
                .bearing(90)               // Sets the tilt of the camera to 30 degrees
                .build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        try {
            super.onStop();
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
        } catch (Exception e) {

        }

    }
}
