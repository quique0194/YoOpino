package com.example.kike.smartcomplains;

import android.animation.TimeInterpolator;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Formatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class ComplainsActivity extends AppCompatActivity {
    private HashMap<String, String> enterprise = null;
    private final String TAG = "COMPLAINS";
    private TabHost tabs;
    private ListView complainsList = null;
    private boolean activityIsVisible = true;

    @Override
    protected void onResume() {
        super.onResume();
        activityIsVisible = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        activityIsVisible = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complains);

        setEnterprise();

        ActionBar ab = getSupportActionBar();
        ab.setTitle(enterprise.get("name"));
        ab.setSubtitle(enterprise.get("category"));

        setTabs();
        complainsList = (ListView) findViewById(R.id.complains_list);
        GetComplainsTask task = new GetComplainsTask();
        task.execute(enterprise.get("id"));
    }

    private void setEnterprise() {
        Intent i = getIntent();
        String json = i.getStringExtra("enterprise_json");
        try {
            JSONObject enterprise_json = new JSONObject(json);
            enterprise = new HashMap<String, String>();
            enterprise.put("id", enterprise_json.getString("id"));
            enterprise.put("name", enterprise_json.getString("name"));
            enterprise.put("category", enterprise_json.getString("category"));
        } catch (JSONException e) {
            Log.e(TAG, "Unable to get enterprise information");
            e.printStackTrace();
        }
    }

    private void setTabs() {
        tabs = (TabHost) findViewById(R.id.tabHost);
        tabs.setup();

        TabHost.TabSpec spec = tabs.newTabSpec("tab1");
        spec.setContent(R.id.tab1);
        spec.setIndicator(getString(R.string.reputation));
        tabs.addTab(spec);

        TabHost.TabSpec spec2 = tabs.newTabSpec("tab2");
        spec2.setContent(R.id.tab2);
        spec2.setIndicator(getString(R.string.complain));
        tabs.addTab(spec2);

        tabs.setCurrentTab(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_complains, menu);
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

    public void submitComplain(View v) {
        EditText title = (EditText) findViewById(R.id.complain_title);
        EditText detail = (EditText) findViewById(R.id.complain_detail);

        SubmitComplainTask task = new SubmitComplainTask();
        task.execute(title.getText().toString().replaceAll(" ", "%20"),
                     detail.getText().toString().replaceAll(" ", "%20"),
                     enterprise.get("id"));
    }

    private class SubmitComplainTask extends AsyncTask<String, Void, String> {
        String the_url = String.format("http://%s", Constants.HOST) + "/submit_complain/?title=%s&detail=%s&enterprise_id=%s";
        private static final String TAG = "HttpGET";
        URL url = null;
        HttpURLConnection urlConnection = null;

        @Override
        protected String doInBackground(String... params) {
            the_url = String.format(the_url, params[0], params[1], params[2]);

            try {
                url = new URL(the_url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            EditText title = (EditText) findViewById(R.id.complain_title);
            EditText detail = (EditText) findViewById(R.id.complain_detail);
            title.setText("");
            detail.setText("");
        }
    }

    private class GetComplainsTask extends AsyncTask<String, String, String> {
        String the_url = String.format("http://%s", Constants.HOST) + "/complains/?id=%s";
        private static final String TAG = "HttpGET";
        URL url = null;
        HttpURLConnection urlConnection = null;
        LinkedList<HashMap<String, String>> data = new LinkedList<HashMap<String, String>>();

        @Override
        protected String doInBackground(final String... params) {
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    String response = doRequest(params[0]);
                    if (response != null) {
                        publishProgress(response);
                    }
                }
            };

            Timer timer = new Timer();
            timer.scheduleAtFixedRate(task, 0, 2000);

            return null;
        }

        private String doRequest(String enterprise_id) {
            if (!activityIsVisible) {
                return null;
            }
            the_url = String.format(the_url, enterprise_id);

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
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            String s = values[0];
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            Log.d(TAG, s);
            try {
                JSONArray json = new JSONArray(s);
                data = new LinkedList<HashMap<String, String>>();
                for (int i = 0; i < json.length(); ++i) {
                    JSONObject item = json.getJSONObject(i);
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("title", item.getString("title"));
                    map.put("detail", item.getString("detail"));
                    map.put("json", item.toString());
                    data.add(map);
                }
                SimpleAdapter adapter = new SimpleAdapter(ComplainsActivity.this,
                        data,
                        android.R.layout.two_line_list_item,
                        new String[] {"title", "detail"},
                        new int[] {android.R.id.text1, android.R.id.text2});
                complainsList.setAdapter(adapter);
            } catch (JSONException e) {
                Log.e("ERROR", e.getMessage());
                e.printStackTrace();
            }
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

    }
}
