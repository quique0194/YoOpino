package com.example.kike.smartcomplains;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ChooseEnterprise extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_enterprise);
        GetEnterprisesTask task = new GetEnterprisesTask();
        task.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choose_enterprise, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class GetEnterprisesTask extends AsyncTask<Float, Void, String> {
        String the_url = "http://10.0.2.2:5000/enterprises/?lat=10&lon=20&radius=11";
        private static final String TAG = "HttpGET";
        URL url = null;
        HttpURLConnection urlConnection = null;
        LinkedList<HashMap<String, String>> data = new LinkedList<HashMap<String, String>>();

        @Override
        protected String doInBackground(Float... params) {
            try {
                url = new URL(the_url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            try {
                urlConnection = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                return readStream(urlConnection.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }

        private String readStream(InputStream in) {
            BufferedReader reader = null;
            StringBuffer data = new StringBuffer();
            try {
                reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    data.append(line);
                }
            } catch (IOException e) {
                Log.e(TAG, "IOException");
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(TAG, "IOException");
                    }
                }
            }
            return data.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            Log.d(TAG, s);
            try {
                JSONArray json = new JSONArray(s);
                for (int i = 0; i < json.length(); ++i) {
                    JSONObject item = json.getJSONObject(i);
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("name", item.getString("name"));
                    map.put("category", item.getString("category"));
                    data.add(map);
                }
                SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(),
                        data,
                        android.R.layout.two_line_list_item,
                        new String[] {"name", "category"},
                        new int[] {android.R.id.text1, android.R.id.text2});
                ListView lv = (ListView) findViewById(R.id.enterprisesList);
                lv.setAdapter(adapter);
            } catch (JSONException e) {
                Log.e("ERROR", e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
