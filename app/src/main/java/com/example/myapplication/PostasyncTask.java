package com.example.myapplication;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpResponse;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.HttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.entity.UrlEncodedFormEntity;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpPost;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.DefaultHttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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
        String outputResponse = "";
        BufferedReader reader = null;

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("http://192.168.0.24:3600/api/addImage");

        try {
          //  URL url = new URL("http://192.168.0.24:3600/api/addImage");
            String data = strings[0];
           // Log.d("data", "doInBackground: " + data);
          //  URLConnection con = url.openConnection();
          // con.setDoOutput(true);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("path", data));
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            post.setHeader("content.type","application/json");

            //http response
            HttpResponse response = client.execute(post);
            reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = "";
            while ((line = reader.readLine()) != null)
            {
                outputResponse = line;
            }




           /* //write to api
            OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
            writer.write(json.toString());
            writer.flush();*/

            //receive post response
            /*reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;

            //read from reader
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            response = sb.toString();*/

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
            return outputResponse;
        }
    }


    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        callback.setResult(result);
    }
}
