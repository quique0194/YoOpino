package com.example.kike.smartcomplains;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ComplainsActivity extends AppCompatActivity {
    private HashMap<String, String> enterprise = null;
    private final String TAG = "COMPLAINS";
    private TabHost tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complains);

        setEnterprise();

        ActionBar ab = getSupportActionBar();
        ab.setTitle(enterprise.get("name"));
        ab.setSubtitle(enterprise.get("category"));

        setTabs();
    }

    private void setEnterprise() {
        Intent i = getIntent();
        String json = i.getStringExtra("enterprise_json");
        try {
            JSONObject enterprise_json = new JSONObject(json);
            enterprise = new HashMap<String, String>();
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
        spec.setContent(R.id.tab2);
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
}
