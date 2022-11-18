package com.example.myapplication;

import static androidx.fragment.app.FragmentManager.TAG;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements AsyncCallBack {


    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int picId = 123;
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //request camera permission
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 100);

        }


        ImageButton openCamera = (ImageButton) findViewById(R.id.imageButton);
        openCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                /* onActivityResult(CAMERA_REQUEST, 200, cameraIntent);*/
                startActivityForResult(cameraIntent, 100);
            }
        });
        //get images from API
        Button GetImageBtn = (Button) findViewById(R.id.Button);
        GetImageBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                GetImages();
            }
        });


    }


    void GetImages() {

        new GetAsyncTask().setInstance(MainActivity.this).execute();
    }

    void PostImages(String photo) {
        new PostasyncTask().setInstance(MainActivity.this).execute(photo);
    }

    //take picture
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            String base64 = ConvertBitmap(photo);
            ImageView view = findViewById(R.id.imagecontainer);
          //  view.setImageBitmap(photo);
            PostImages(base64);

            //test encoding and decoding of images
          // Bitmap img = ConvertToBitmap(base64);
            //view.setImageBitmap(img);
         //  GenerateNewImage(img);
        }
    }

    @Override
    public void GetData(String result) {

        try {
            JSONArray json = new JSONArray(result);
            for (int i = 0; i < json.length(); i++) {
                JSONObject obj = json.getJSONObject(i);
                String path = obj.getString("imagePath");
                Log.d("TAG", path);
                Bitmap photo = ConvertToBitmap(path);
                GenerateNewImage(photo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setResult(String result) {
        Log.d("TAG", result);


    }

    //convert image to base64
    private String ConvertBitmap(Bitmap photo) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bytes = stream.toByteArray();
        String base64 = Base64.encodeToString(bytes, Base64.DEFAULT);
         Log.d("convert to base64", "ConvertBitmap: " + base64);
        return base64;
    }

    //convert base64 to bitmap
    private Bitmap ConvertToBitmap(String base64) {

        try {

            byte[] bytes = Base64.decode(base64, 0);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            return bitmap;

        } catch (Exception e) {
            Log.e("convert to bitmap", "ConvertToBitmap: ",e );
            return null;
        }
    }

    private void GenerateNewImage(Bitmap photo) {

        try {

            layout.findViewById(R.id.background);
            ImageView img = new ImageView(MainActivity.this);
            img.setImageBitmap(photo);


            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(128, 128);
            params.setMargins(10, 10, 10, 10);
            img.setLayoutParams(params);
            layout.addView(img);
        } catch (Exception e) {
            Log.e("Generate image", "GenerateNewImage: " + e);
        }
    }
}