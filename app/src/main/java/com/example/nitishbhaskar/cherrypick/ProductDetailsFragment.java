package com.example.nitishbhaskar.cherrypick;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.security.Permission;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.lang.Object;
import java.util.Locale;


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
    TextToSpeech tts;
    ImageView productIcon;
    Toolbar toolbar;
    File screenshotImage;
    String productAddress;

    SubActionButton messageBtn;
    SubActionButton emailBtn;

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

        final AppCompatActivity act = (AppCompatActivity) getActivity();
        if (act.getSupportActionBar() != null) {
            productIcon = (ImageView)act.findViewById(R.id.toolbarImage);
        }
        if(currentProduct.get("image") != null)
            Picasso.with(getContext()).load((String) currentProduct.get("image")).into(productIcon);
        //TextView productId = (TextView) view.findViewById(R.id.details_productId);
        TextView productDatePosted = (TextView) view.findViewById(R.id.details_datePosted);
        TextView productDescription = (TextView) view.findViewById(R.id.details_productDescription);
        //TextView productLocation = (TextView) view.findViewById(R.id.details_productLocation);
        TextView productPrice = (TextView) view.findViewById(R.id.details_productPrice);
        productName.setText((String) currentProduct.get("productName"));
        productName.setTransitionName((String)currentProduct.get("productName"));
        //productId.setText((String) currentProduct.get("productId"));
        productDatePosted.setText("Date: " + (String) currentProduct.get("datePostedOn"));
        productDescription.setText((String) currentProduct.get("description"));
        //productLocation.setText((String) currentProduct.get("location"));
        productPrice.setText("Price: $" + (String) currentProduct.get("price"));

        notification();
        ImageView icon = new ImageView(getActivity());
        icon.setImageDrawable(getResources().getDrawable(R.mipmap.share));
        com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton fab =
                new com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton.Builder(getActivity())
                        .setContentView(icon).build();

        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(getActivity());

        ImageView messageImageView = new ImageView(getActivity());
        messageImageView.setImageDrawable(getResources().getDrawable(R.mipmap.message));
        messageBtn = itemBuilder.setContentView(messageImageView).build();

        ImageView emailImageView = new ImageView(getActivity());
        emailImageView.setImageDrawable(getResources().getDrawable(R.mipmap.email));
        emailBtn = itemBuilder.setContentView(emailImageView).build();

        FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(getActivity())
                .addSubActionView(messageBtn)
                .addSubActionView(emailBtn)
                .attachTo(fab)
                .build();

        floatingButtonsListeners();

        return view;
    }

    private void floatingButtonsListeners(){
        messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setData(Uri.parse("sms:"));
                String message = "Checkout the "+(String) currentProduct.get("productName")+ " on CherryPick app which is available at $"+(String) currentProduct.get("price");
                sendIntent.putExtra("sms_body", message);
                startActivity(sendIntent);
            }
        });

        emailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeScreenshot();
                String location = (String) currentProduct.get("location");
                Double latitude = Double.parseDouble(location.split(",")[0]);
                Double longitude = Double.parseDouble(location.split(",")[1]);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                String subject = "CherryPick : "+(String) currentProduct.get("productName")+" @ $"+(String) currentProduct.get("price");
                String message = "Checkout the "+(String) currentProduct.get("productName")+ " on CherryPick app which is available at $"+(String) currentProduct.get("price");

                intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                intent.putExtra(Intent.EXTRA_TEXT, message);
                Uri uri = Uri.fromFile(screenshotImage);
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                Intent mailer = Intent.createChooser(intent, null);
                startActivity(mailer);
            }
        });
    }

    public void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            View v1 = getActivity().getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            screenshotImage = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(screenshotImage);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            e.printStackTrace();
        }
    }

    public void notification(){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getContext())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000})
                        .setContentTitle((String) currentProduct.get("productName") + " @ $"+(String) currentProduct.get("price"))
                        .setContentText((String) currentProduct.get("description"));
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(getActivity(), MainActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getActivity());
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(10, mBuilder.build());
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        try {
            if(Settings.Secure.getInt(getContext().getContentResolver(), Settings.Secure.LOCATION_MODE) == 0) {
                Intent gpsOptionsIntent = new Intent(
                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(gpsOptionsIntent);
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

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
                            //.icon(BitmapDescriptorFactory.fromResource(R.drawable.flag))
                    );
                }
            });


            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(final Marker marker) {
                    Toast.makeText(getContext(), marker.getTitle().toString(), Toast.LENGTH_SHORT).show();
                     tts = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            if (status == TextToSpeech.SUCCESS) {
                                int result = tts.setLanguage(Locale.US);
                                if (result == TextToSpeech.LANG_MISSING_DATA ||
                                        result == TextToSpeech.LANG_NOT_SUPPORTED) {
                                    Log.e("error", "This Language is not supported");
                                } else {
                                    ConvertTextToSpeech(marker.getTitle().toString());
                                }
                            } else
                                Log.e("error", "Initilization Failed!");
                        }
                    });

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

    private void ConvertTextToSpeech(String text) {
        // TODO Auto-generated method stub

        if(text==null||"".equals(text))
        {
            text = "Content not available";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ttsGreater21(text);
            } else {
                ttsUnder20(text);
            }
        }else
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ttsGreater21(text);
        } else {
            ttsUnder20(text);
        }
    }

    @SuppressWarnings("deprecation")
    private void ttsUnder20(String text) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        tts.speak("The Product is available at "+text, TextToSpeech.QUEUE_FLUSH, map);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text) {
        String utteranceId=this.hashCode() + "";
        tts.speak("The Product is available at "+text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
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
