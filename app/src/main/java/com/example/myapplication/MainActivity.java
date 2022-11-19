package com.example.myapplication;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.graphics.BitmapFactory;
import android.widget.RelativeLayout;
import android.view.View.OnTouchListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;


public class MainActivity extends AppCompatActivity implements AsyncCallBack {

    int numImageX = 0;
    int numImageY = 0;
    int maxRow = 8;

    int xDelta;
    int yDelta;

    RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layout = findViewById(R.id.pictures);

        //get reference objects from layout
        ImageButton openCamera = (ImageButton) findViewById(R.id.imageButton);
        Button GetImageBtn = (Button) findViewById(R.id.Button);


        //request camera permission
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 100);
        }

        // click button to open camera app
        openCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 100);
            }
        });

        //click button to get images from Api
        GetImageBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                GetImages();
            }
        });


    }

    /**
     * Execute Task for getting images from Api
     */
    void GetImages() {

        new GetAsyncTask().setInstance(MainActivity.this).execute();
    }

    /**
     * Execute Task for posting image to Api
     *
     * @param photo
     */
    void PostImages(String photo) {
        new PostasyncTask().setInstance(MainActivity.this).execute(photo);
    }

    /**
     * Take picture using the camera app & send it to Api
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");

            String base64 = ConvertBitmap(photo);
            PostImages(base64);
            GenerateNewImage(photo);

            //save image in local storage
          boolean success =  SaveImageToStorage(UUID.randomUUID().toString(),photo);
          if(success)
          {
              Log.d("save image", "onActivityResult: image save successfully");
          }
        }
    }

    /**
     * gets the images from Api and generates the ImageView
     *
     * @param result
     */
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

    /**
     * callback method logging response from Api
     *
     * @param result
     */
    @Override
    public void setResult(String result) {
        Log.d("TAG", result);


    }

    /**
     * converts Bitmap into base64 string
     *
     * @param photo
     * @return
     */
    private String ConvertBitmap(Bitmap photo) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bytes = stream.toByteArray();
        String base64 = Base64.encodeToString(bytes, Base64.NO_WRAP);

        //  Log.d("convert to base64", "ConvertBitmap: " + base64);
        return base64;
    }

    /**
     * converts base64 string to Bitmap
     *
     * @param base64
     * @return
     */
    private Bitmap ConvertToBitmap(String base64) {

        try {

            byte[] bytes = Base64.decode(base64.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);

            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            return bitmap;

        } catch (Exception e) {
            Log.e("convert to bitmap", "ConvertToBitmap: ", e);
            return null;
        }
    }

    /**
     * Generates new ImageView with new Bitmap
     *
     * @param photo
     */
    private void GenerateNewImage(Bitmap photo) {

        int size = 256;
        float x = numImageX * size;
        float y = numImageY * size;
        // RelativeLayout layout = findViewById(R.id.pictures);
        try {

            //create image view
            ImageView img = new ImageView(MainActivity.this);
            img.setImageBitmap(Bitmap.createScaledBitmap(photo,size,size,false));
            img.setBackground(getResources().getDrawable(R.drawable.boxshadow));

            //set on touch listener
            img.setOnTouchListener(onTouchListener());

            //set size & margins
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(size, size);
            params.setMargins(10, 10, 10, 10);

            img.setLayoutParams(params);
            layout.addView(img);

            //position images in rows & columns
            layout.getChildAt(numImageX).setX(x);
            layout.getChildAt(numImageX).setY(y);
           // layout.getChildAt(numImageX).setElevation(10);

            Log.d("childCount", "GenerateNewImage: " + layout.getChildCount());

            numImageX++;

            //calculate next row
            if (numImageX >= maxRow) {
                numImageY++;
                numImageX = 0;
            }

        } catch (Exception e) {
            Log.e("Generate image", "GenerateNewImage: " + e);
        }
    }

    /**
     * on touch event for moving image around on screen
     * @return
     */
    private OnTouchListener onTouchListener() {
        return new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                final int x = (int) motionEvent.getRawX();
                final int y = (int) motionEvent.getRawY();

                switch (motionEvent.getAction() & motionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
                        xDelta = x - params.leftMargin;
                        yDelta = y - params.topMargin;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        RelativeLayout.LayoutParams newParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                        newParams.leftMargin = x - xDelta;
                        newParams.topMargin = y - yDelta;
                        newParams.rightMargin = 0;
                        newParams.bottomMargin = 0;
                        view.setLayoutParams(newParams);
                        break;
                }
                layout.invalidate();

                return true;
            }
        };
    }


    private boolean SaveImageToStorage(String name,Bitmap photo)
    {
        Uri ImageCollection;
        ContentResolver resolver = getContentResolver();

        ImageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME,name + "jpg");
        values.put(MediaStore.Images.Media.MIME_TYPE,"image/jpg");
        Uri imageUri = resolver.insert(ImageCollection,values);

        try {
            OutputStream stream = resolver.openOutputStream(Objects.requireNonNull(imageUri));
            photo.compress(Bitmap.CompressFormat.JPEG,100,stream);
            Objects.requireNonNull(stream);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}