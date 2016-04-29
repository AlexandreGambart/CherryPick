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


}
