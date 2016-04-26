package com.example.nitishbhaskar.cherrypick;


import android.Manifest;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import mehdi.sakout.fancybuttons.FancyButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class SellFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    TextInputEditText productName;
    TextInputEditText productDescription;
    TextInputEditText productPrice;
    TextInputEditText productDate;
    Calendar myCalendar;
    TextInputEditText myLocation;
    FancyButton submit;
    DatePickerDialog.OnDateSetListener date;
    ProductData sellProduct;
    Location location;

    public SellFragment() {
        // Required empty public constructor
    }

    public static SellFragment newInstance(int sectionNumber) {
        SellFragment sellFragment = new SellFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        sellFragment.setArguments(args);
        return sellFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sell, container, false);
        productName = (TextInputEditText) view.findViewById(R.id.pName);
        productDescription = (TextInputEditText) view.findViewById(R.id.pDescription);
        productPrice = (TextInputEditText) view.findViewById(R.id.pPrice);
        productDate = (TextInputEditText) view.findViewById(R.id.pDate);
        myLocation = (TextInputEditText) view.findViewById(R.id.pLocation);
        myCalendar = Calendar.getInstance();
        submit = (FancyButton) view.findViewById(R.id.submitButton);
        sellProduct = new ProductData();
        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        productDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                String provider = locationManager.getBestProvider(criteria, true);
                // Getting Current Location
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                location = locationManager.getLastKnownLocation(provider);
                updateLocation(location);
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkProductName() && checkProductDescription() && checkProductPrice() && checkProductDate()) {
                    HashMap product = new HashMap();
                    product.put("productName", productName.getText().toString());
                    product.put("description", productDescription.getText().toString());
                    product.put("datePostedOn", productDate.getText().toString());
                    product.put("price", productPrice.getText().toString());
                    product.put("location", "" + location.getLatitude() + ", " + location.getLongitude());
                    product.put("productId", productName.getText().toString().replace(" ","") + "_" + productDate.getText().toString() + "_" +getTime());
                    product.put("image","https://www.google.com");
                    sellProduct.addItemToServer(product);
                    Toast.makeText(getContext(),"Product Successfully added",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Please fill out all the fields given above", Toast.LENGTH_SHORT).show();
                }
            }
        });


        return view;
    }

    private String getTime(){
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => "+c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    private void updateLabel() {

        String myFormat = "MM-dd-yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        productDate.setText(sdf.format(myCalendar.getTime()));

    }

    private void updateLocation(Location location) {

        Double lat = location.getLatitude();
        Double lgt = location.getLongitude();
        String locationText = getAddress(lat, lgt);
        myLocation.setText(locationText);
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

    private boolean checkProductName(){
        if( productName.getText().toString().trim().equals("")) {
            productName.setError("Product name is required!");
            return false;
        }
        return true;
    }

    private boolean checkProductDescription(){
        if( productDescription.getText().toString().trim().equals("")) {
            productDescription.setError("Product Description is required!");
            return false;
        }
        return true;
    }

    private boolean checkProductPrice(){
        if( productPrice.getText().toString().trim().equals("") ) {
            productPrice.setError("Product price is required!");
            return false;
        }
        return true;
    }

    private boolean checkProductDate(){
        if( productDate.getText().toString().trim().equals("") ) {
            productDate.setError("Product price is required!");
            return false;
        }
        return true;
    }

    private boolean userLocation(){
        if( myLocation.getText().toString().trim().equals("") ) {
            myLocation.setError("Product location is required!");
            return false;
        }
        return true;
    }



}
