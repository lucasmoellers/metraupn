package com.lucasmoellers.metraupn;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class SettingsActivity extends ActionBarActivity {
    private Spinner destinationOutboundSpinner;
    private Spinner destinationInboundSpinner;
    private Settings settings;
    private List<Station> stations;
    private boolean dataLoaded = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
        destinationOutboundSpinner = (Spinner) findViewById(R.id.destination_outbound_spinner);
        destinationInboundSpinner = (Spinner) findViewById(R.id.destination_inbound_spinner);
        new LoadBackgroundData().execute();
    }

    private class LoadBackgroundData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            SettingsDataStorage dataStorage = new SettingsDataStorage(SettingsActivity.this);
            ObjectMapper mapper = new ObjectMapper();

            try {
                settings = dataStorage.fetch();
                InputStream fis = SettingsActivity.this.getAssets().open("metra.json");
                stations = mapper.readValue(fis, new TypeReference<List<Station>>() {
                });
            } catch (Exception e) {
                Toast.makeText(SettingsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            everythingLoaded();
            dataLoaded = true;
            invalidateOptionsMenu();
        }
    }

    private class SaveData extends  AsyncTask<Settings, Void, Void> {

        @Override
        protected Void doInBackground(Settings... params) {
            SettingsDataStorage dataStorage = new SettingsDataStorage(SettingsActivity.this);
            Settings saveSettings = params[0];
            try {
                dataStorage.save(saveSettings);
            } catch (Exception e) {
                cancel(true);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Intent intent = new Intent();
            intent.putExtra(MainActivity.INTENT_EXTRA_SETTINGS, settings);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private void everythingLoaded() {
        int outboundIndex = 0;
        int inboundIndex = 0;
        List<String> stationNames = new ArrayList<String>();
        int i = 0;
        for (Station s : stations) {
            stationNames.add(s.station_name);
            if (settings != null) {
                if (settings.outboundDestination.station_name.equals(s.station_name)) {
                    outboundIndex = i;
                }

                if (settings.inboundDestination.station_name.equals(s.station_name)) {
                    inboundIndex = i;
                }
            }
            i++;
        }
        // set default to Kenosha to Ogilvie
        if (settings == null) {
            outboundIndex = 0;
            inboundIndex = stations.size() - 1;
        }
        ArrayAdapter<String> outboundAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, stationNames);
        destinationOutboundSpinner.setAdapter(outboundAdapter);
        destinationOutboundSpinner.setSelection(outboundIndex);

        ArrayAdapter<String> inboundAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, stationNames);
        destinationInboundSpinner.setAdapter(inboundAdapter);
        destinationInboundSpinner.setSelection(inboundIndex);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return dataLoaded;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_cancel) {
            finish();
            return true;
        } else if (id == R.id.action_save) {

            String outboundStationName = (String) destinationOutboundSpinner.getAdapter().getItem(destinationOutboundSpinner.getSelectedItemPosition());
            String inboundStationName = (String) destinationInboundSpinner.getAdapter().getItem(destinationInboundSpinner.getSelectedItemPosition());

            Log.d("SettingsActivity", outboundStationName);
            if (settings == null) {
                settings = new Settings();
            }
            boolean wrongStationOrder = false;
            boolean outboundSet = false;
            for (Station s : stations) {
                if (s.station_name.equals(outboundStationName)) {
                    settings.outboundDestination = s;
                    outboundSet = true;
                } else if (s.station_name.equals(inboundStationName)) {
                    settings.inboundDestination = s;
                    if (!outboundSet) {
                        wrongStationOrder = true;
                    }
                }
            }

            // validate settings
            if (wrongStationOrder) {
                Toast.makeText(this, "Stations are in the wrong order", Toast.LENGTH_LONG).show();
            } else if (outboundStationName.equals(inboundStationName)) {
                Toast.makeText(this, "Inbound and Outbound station must be different", Toast.LENGTH_LONG).show();
            } else {
                new SaveData().execute(settings);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
