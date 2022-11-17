package com.example.myapplication;

import static androidx.fragment.app.FragmentManager.TAG;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {


    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int picId = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //request camera permission
        //  requestPermissions(new String[]{Manifest.permission.CAMERA},MY_CAMERA_PERMISSION_CODE);


        ImageButton openCamera = (ImageButton) findViewById(R.id.imageButton);
        openCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                onActivityResult(CAMERA_REQUEST, 200, cameraIntent);
            }
        });
        //get images from API
        Button GetImageBtn = (Button) findViewById(R.id.Button);
        GetImageBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                try {
                    GetImages();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });


    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("TAG", "request code: " + requestCode + " picId: " + picId);


        Bitmap photo = (Bitmap) data.getExtras().get("data");
        ImageView img = (ImageView) findViewById(R.id.imagecontainer);
        img.setImageBitmap(photo);

    }

    void GetImages() throws MalformedURLException {

        URL url = new URL("http://www.android.com/");
        HttpURLConnection urlConnection = null;
        try {
            urlConnection  = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            int code = urlConnection.getResponseCode();
            Log.i("Response", "status code: " +code);
           /* InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            Bitmap map = BitmapFactory.decodeStream(in);
            Log.i("Response", map.toString());*/

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }

    }
}