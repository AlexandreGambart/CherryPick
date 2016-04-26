package com.example.nitishbhaskar.cherrypick;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nitish on 4/25/2016.
 */
public class CloudinaryCloud {
    public static Cloudinary getInstance(){
        Map config = new HashMap();
        String CloudName = "drfm6ykw6";
        String ApiKey = "479556712346146";
        String ApiSecret = "kNNaC7FuzvQKe4b-BIesEOktV5E";
        config.put("cloud_name", CloudName);
        config.put("api_key", ApiKey);
        config.put("api_secret", ApiSecret);
        return new Cloudinary(config);
    }

    public static void upload(final File fileLocation,final HashMap product){

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Cloudinary cloudinary = getInstance();
                    //Image compression
                    Bitmap bmp = BitmapFactory.decodeFile(fileLocation.toString());
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.JPEG, 10, bos);
                    InputStream in = new ByteArrayInputStream(bos.toByteArray());

                    Map uploadDetails = cloudinary.uploader().upload(in, ObjectUtils.emptyMap());
                    product.put("image", uploadDetails.get("url").toString());
                    ProductData sellProduct = new ProductData();
                    sellProduct.addItemToServer(product);
                } catch (IOException e) {
                    //TODO: better error handling when image uploading fails
                    e.printStackTrace();
                }
            }
        };

        new Thread(runnable).start();

    }
}
