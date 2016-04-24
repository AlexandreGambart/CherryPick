package com.example.nitishbhaskar.cherrypick;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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

import java.security.Permission;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProductDetailsFragment extends Fragment implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private static final String PRODUCT_INDEX = "product_index";
    private static HashMap<String, ?> currentProduct;
    TextView productDistance;
    LatLng productLatitudeLongitude;

    /***
     * define Parameters here
     ****************************/
    private GoogleApiClient mGoogleApiClient;
    private SupportMapFragment mMapFragment;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Location mLastLocation;

    /*****************************************************/

    public static ProductDetailsFragment newInstance(HashMap<String, ?> productData) {
        ProductDetailsFragment productDetailsFragment = new ProductDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(PRODUCT_INDEX, productData);
        productDetailsFragment.setArguments(args);
        currentProduct = productData;
        return productDetailsFragment;
    }

    public ProductDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_details, container, false);
        //super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null)
            currentProduct = (HashMap<String, ?>) getArguments().getSerializable(PRODUCT_INDEX);

        TextView productName = (TextView) view.findViewById(R.id.details_productName);
        productDistance = (TextView) view.findViewById(R.id.details_productDistance);
        //TextView productId = (TextView) view.findViewById(R.id.details_productId);
        TextView productDatePosted = (TextView) view.findViewById(R.id.details_datePosted);
        TextView productDescription = (TextView) view.findViewById(R.id.details_productDescription);
        //TextView productLocation = (TextView) view.findViewById(R.id.details_productLocation);
        TextView productPrice = (TextView) view.findViewById(R.id.details_productPrice);

        productName.setText((String) currentProduct.get("productName"));
        //productId.setText((String) currentProduct.get("productId"));
        productDatePosted.setText("Date: " + (String) currentProduct.get("datePostedOn"));
        productDescription.setText((String) currentProduct.get("description"));
        //productLocation.setText((String) currentProduct.get("location"));
        productPrice.setText("Price: $" + (String) currentProduct.get("price"));
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    250);
        } else {
            if (savedInstanceState == null) {
                buildGoogleApiClient();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 250: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    buildGoogleApiClient();

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.googlemap);
        if (mMapFragment != null) {
            mMapFragment.getMapAsync(this);
        }
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

    private LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng_Now = new LatLng(location.getLatitude(), location.getLongitude());
        Double distance = CalculationByDistance(latLng_Now, productLatitudeLongitude);
        String result = String.format("%.2f", distance);
        productDistance.setText(result + " mi");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c * 0.62137;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            mMap = googleMap;
            //mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            //mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
            mMap.setMyLocationEnabled(true);

            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng lat) {
                    Toast.makeText(getContext(), "Latitude: " + lat.latitude + "\nLongitude: " + lat.longitude, Toast.LENGTH_SHORT).show();
                }
            });
            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng lat) {
                    final Marker marker = mMap.addMarker(new MarkerOptions()
                            .title("self defined marker")
                            .snippet("Hello!")
                            .position(lat).visible(true)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))//.icon(BitmapDescriptorFactory.fromResource(R.drawable.flag))
                    );
                }
            });


            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Toast.makeText(getContext(), marker.getTitle().toString(), Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
            // Add a marker in Sydney and move the camera
            String location = (String) currentProduct.get("location");
            Double latitude = Double.parseDouble(location.split(",")[0]);
            Double longitude = Double.parseDouble(location.split(",")[1]);
            LatLng place = new LatLng(latitude, longitude);
            productLatitudeLongitude = place;
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(place)      // Sets the center of the map to LatLng (refer to previous snippet)
                    .zoom(17)                   // Sets the zoom
                    .bearing(90)               // Sets the tilt of the camera to 30 degrees
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            mMap.addMarker(new MarkerOptions().position(place).title(getAddress(latitude, longitude)));
        } catch (SecurityException se) {

        }
    }

    public String getAddress(double latitude, double longitude) {
        String placeAddress = "";
        if (latitude != 0 && longitude != 0) {
            try {
                Geocoder geocoder = new Geocoder(getContext());
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getAddressLine(1);
                String country = addresses.get(0).getAddressLine(2);
                placeAddress = address + ", " + city + ", " + country;
                Log.d("TAG", "address = " + address + ", city = " + city + ", country = " + country);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getContext(), "latitude and longitude are null", Toast.LENGTH_LONG).show();
        }
        return placeAddress;
    }
}
