package com.example.nitishbhaskar.cherrypick;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import mehdi.sakout.fancybuttons.FancyButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class BarCodeFragment extends Fragment {

    TextView contentTxt;
    TextView formatTxt;
    TextView outputTextView;
    CameraManager cameraManager;
    DownLoadTask task;
    FancyButton scan;

    public BarCodeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bar_code, container, false);
        contentTxt = (TextView)view.findViewById(R.id.scan_content);
        formatTxt = (TextView)view.findViewById(R.id.scan_format);
        outputTextView =(TextView)view.findViewById(R.id.details);
        scan = (FancyButton)view.findViewById(R.id.scan_button);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanNow(v);
            }
        });
        cameraManager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
        return view;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private String findFrontFacingCameraID(CameraManager cManager){
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                for (final String cameraId : cManager.getCameraIdList()) {
                    CameraCharacteristics characteristics = cManager.getCameraCharacteristics(cameraId);
                    int cOrientation = characteristics.get(CameraCharacteristics.LENS_FACING);
                    if (cOrientation == CameraCharacteristics.LENS_FACING_BACK) return cameraId;
                }
                return null;
            }else{
                int cameraId = -1;
                // Search for the back facing camera
                int numberOfCameras = Camera.getNumberOfCameras();
                for (int i = 0; i < numberOfCameras; i++) {
                    Camera.CameraInfo info = new Camera.CameraInfo();
                    Camera.getCameraInfo(i, info);
                    if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                        cameraId = i;
                        break;
                    }
                }
                return ""+cameraId;
            }

        }catch (CameraAccessException e){
            e.printStackTrace();

        }
        return null;
    }

    public void scanNow(View view){
        IntentIntegrator integrator = new IntentIntegrator(getActivity());
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        integrator.setPrompt("Scan a Bar Code");
        integrator.setResultDisplayDuration(0);

        integrator.setWide();// Wide scanning rectangle, may work better for 1D barcodes
        int c = Integer.parseInt(findFrontFacingCameraID(cameraManager));
        //int c = findFrontFacingCameraID();
        integrator.setCameraId(c);// Use a specific camera of the device
        integrator.initiateScan();
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
                Toast.makeText(getContext(), "Could not Product", Toast.LENGTH_LONG);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            JSONObject jsonObject = null;
            try {
                String message="";
                jsonObject = new JSONObject(result);
                // String weatherInfo = jsonObject.getString("weather");
                String productDetails = jsonObject.toString();
                Log.i("Product details", productDetails);
                // JSONArray arr = new JSONArray(productDetails);
                //for (int i = 0; i < arr.length(); i++) {
                //  JSONObject jsonPart = arr.getJSONObject(i);
                // String main = "";
                //String description = "";
                //main = jsonPart.getString("main");
                //description = jsonPart.getString("description");
                //if (main != "" && description != "") {
                //  message = message + main + ": " + description + "\r\n";
                //}
                //}
                //if(message!=""){
                outputTextView.setText("ProductDetails: "+productDetails);
                //}else{
                //  Toast.makeText(getApplicationContext(),"Could not find weather",Toast.LENGTH_LONG);
                //}
            } catch (JSONException e) {
                Toast.makeText(getContext(), "Could not find product", Toast.LENGTH_LONG);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if (scanningResult != null) {
//we have a result
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();

// display it on screen
            formatTxt.setText("FORMAT: " + scanFormat);
            contentTxt.setText("CONTENT: " + scanContent);
            try {

                task = new DownLoadTask();
                task.execute("http://api.upcdatabase.org/json/8e268419dbb13d51c153f31bc7884c88/" +scanContent);
                //task.execute("http://www.searchupc.com/handlers/upcsearch.ashx?request_type=3&access_token=E9CB1A54-9B91-4AD3-B4A3-77B3E7E261AB&upc=/"+scanContent);
                // task.execute("http://localhost:59798/api/BarCode");
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Could not find product", Toast.LENGTH_LONG);
            }
            super.onActivityResult(requestCode, resultCode, intent);
        }
    }

}
