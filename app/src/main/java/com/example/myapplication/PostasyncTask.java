package com.example.myapplication;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class PostasyncTask extends AsyncTask<String, Void, String> {

    private MainActivity activity;
    private AsyncCallBack callback;


    PostasyncTask setInstance(Context context) {
        this.activity = (MainActivity) context;
        callback = (AsyncCallBack) context;
        return this;
    }

    @Override
    protected String doInBackground(String... strings) {
        String response = "";
        BufferedReader reader = null;

        try {
            URL url = new URL("http://192.168.0.24:3600/api/addImage");
            String data = strings[0];
            Log.d("data", "doInBackground: " + data);
            URLConnection con = url.openConnection();
            con.setDoOutput(true);

            // con.setRequestProperty("Content-Type", "application/json");
            //  con.setRequestProperty("Content-Type", "application/json;charset=UTF-8");





            //write to api
            OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
            writer.write(data);
            writer.flush();

            //receive post response
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;

            //read from reader
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            response = sb.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }
    }


    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        callback.setResult(result);
    }
}
