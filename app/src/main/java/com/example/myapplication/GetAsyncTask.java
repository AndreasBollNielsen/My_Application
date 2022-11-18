package com.example.myapplication;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpResponse;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.ClientProtocolException;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.HttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpGet;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.DecompressingHttpClient;

public class GetAsyncTask extends AsyncTask<Void,Void,String> {

    private MainActivity activity;
    private AsyncCallBack callback;

    GetAsyncTask setInstance(Context context)
    {
        this.activity = (MainActivity) context;
        callback = (AsyncCallBack) context;
        return this;
    }

    @Override
    protected String doInBackground(Void... voids) {

        HttpGet httpGet = new HttpGet("http://10.108.169.27:3600/api/getImages");
        HttpClient client = new DecompressingHttpClient();
        String result = "";

        try {
            HttpResponse response = client.execute(httpGet);
            int statuscode = response.getStatusLine().getStatusCode();
            Log.d("Status code", toString().valueOf(statuscode));
            if(statuscode == 200)
            {
                InputStream stream = response.getEntity().getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String line;
                while ((line = reader.readLine()) != null)
                {
                    result += line;
                }

              //  Log.d("input stream", String.valueOf(result));
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        //callback.setResult(result);
        callback.GetData(result);
    }
}
