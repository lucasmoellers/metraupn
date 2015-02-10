package com.lucasmoellers.metraupn;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;


public class MainActivity extends ActionBarActivity {

    private List<Station> stations;
    private LocationManager locationManager;
    private Location location;
    private static final int REQUEST_CODE_SETTINGS = 1;
    public static final String INTENT_EXTRA_SETTINGS = "intent_extra_settings";
    public static final String INTENT_EXTRA_FROM_STATION = "intent_extra_from_station";
    public static final String INTENT_EXTRA_TO_STATION = "intent_extra_to_station";
    private Settings settings;
    private Station fromStation;
    private Station toStation;
    private ListView upcomingTripsListView;

    private TextView fromStationTextView;
    private TextView toStationTextView;
    private TrainTripAdapter adapter;

    private boolean dataLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
        fromStationTextView = (TextView) findViewById(R.id.trip_from_station);
        toStationTextView = (TextView) findViewById(R.id.trip_to_station);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        upcomingTripsListView = (ListView) findViewById(R.id.upcoming_trips_list_view);
        new LoadMetraDataTask().execute();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private class LoadMetraDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                InputStream fis = MainActivity.this.getAssets().open("metra.json");
                stations = mapper.readValue(fis, new TypeReference<List<Station>>() { });
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                SettingsDataStorage dataStorage = new SettingsDataStorage(MainActivity.this);
                settings = dataStorage.fetch();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (settings != null) {
                everythingLoaded();
            } else {
                Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivityForResult(settingsIntent, REQUEST_CODE_SETTINGS);
            }
            dataLoaded = true;
            invalidateOptionsMenu();
        }
    }

    public boolean isMetraHoliday(Calendar cal) {
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

        if (month == 1 && day == 1) {
            // new year's day
            return true;
        } else if (month == 5 && day >= 25 && dayOfWeek == Calendar.MONDAY) {
            // memorial day
            return true;
        } else if (month == 7 && day == 4) {
            // independence day
            return true;
        } else if (month == 9 && day <= 7 && dayOfWeek == Calendar.MONDAY) {
            // labor day
            return true;
        } else if (month == 11 && day >= 22 && day <= 28 && dayOfWeek == Calendar.THURSDAY) {
            // thanksgiving
            return true;
        } else if (month == 12 && day == 25) {
            // christmas
            return true;
        }
        return false;
    }

    private void loadTrainTrips() {
        List<TrainTime> departTimes = null;
        List<TrainTime> arriveTimes = null;

        List<TrainTime> departTimesNextDay = null;
        List<TrainTime> arriveTimesNextDay = null;

        Calendar todayCal = Calendar.getInstance();
        todayCal.setTimeZone(TimeZone.getTimeZone("America/Chicago"));
        Calendar tommorowCal = (Calendar) todayCal.clone();
        tommorowCal.add(Calendar.DATE, 1);

        int dayOfWeek = todayCal.get(Calendar.DAY_OF_WEEK);
        int tomorrowDayOfWeek = tommorowCal.get(Calendar.DAY_OF_WEEK);

        if (fromStation == settings.outboundDestination) {
            if (dayOfWeek == Calendar.SUNDAY || isMetraHoliday(todayCal)) {
                departTimes = fromStation.inbound_sunday_holiday_times;
                arriveTimes = toStation.inbound_sunday_holiday_times;
            } else if (dayOfWeek == Calendar.SATURDAY) {
                departTimes = fromStation.inbound_saturday_times;
                arriveTimes = toStation.inbound_saturday_times;
            } else {
                departTimes = fromStation.inbound_weekday_times;
                arriveTimes = toStation.inbound_weekday_times;
            }

            if (tomorrowDayOfWeek == Calendar.SUNDAY || isMetraHoliday(tommorowCal)) {
                departTimesNextDay = fromStation.inbound_sunday_holiday_times;
                arriveTimesNextDay = toStation.inbound_sunday_holiday_times;
            } else if (tomorrowDayOfWeek == Calendar.SATURDAY) {
                departTimesNextDay = fromStation.inbound_saturday_times;
                arriveTimesNextDay = toStation.inbound_saturday_times;
            } else {
                departTimesNextDay = fromStation.inbound_weekday_times;
                arriveTimesNextDay = toStation.inbound_weekday_times;
            }

        } else {

            if (dayOfWeek == Calendar.SUNDAY || isMetraHoliday(todayCal)) {
                departTimes = fromStation.outbound_sunday_holiday_times;
                arriveTimes = toStation.outbound_sunday_holiday_times;
            } else if (dayOfWeek == Calendar.SATURDAY) {
                departTimes = fromStation.outbound_saturday_times;
                arriveTimes = toStation.outbound_saturday_times;
            } else {
                departTimes = fromStation.outbound_weekday_times;
                arriveTimes = toStation.outbound_weekday_times;
            }

            if (tomorrowDayOfWeek == Calendar.SUNDAY || isMetraHoliday(tommorowCal)) {
                departTimesNextDay = fromStation.outbound_sunday_holiday_times;
                arriveTimesNextDay = toStation.outbound_sunday_holiday_times;
            } else if (tomorrowDayOfWeek == Calendar.SATURDAY) {
                departTimesNextDay = fromStation.outbound_saturday_times;
                arriveTimesNextDay = toStation.outbound_saturday_times;
            } else {
                departTimesNextDay = fromStation.outbound_weekday_times;
                arriveTimesNextDay = toStation.outbound_weekday_times;
            }
        }

        // get index of next depart time
        int hour = todayCal.get(Calendar.HOUR_OF_DAY);
        int minute = todayCal.get(Calendar.MINUTE);

        int index = 0;

        for (int i = 0; i < departTimes.size(); i++) {
            TrainTime checkTime = departTimes.get(i);
            if (checkTime != null) {
                if (hour == checkTime.hour) {
                    if (checkTime.minute > minute) {
                        index = i;
                        break;
                    }
                } else if (checkTime.hour > hour) {
                    index = i;
                    break;
                }
            }
        }

        fromStationTextView.setText(fromStation.station_name);
        toStationTextView.setText(toStation.station_name);

        List<TrainJourney> journeys = new ArrayList<>();

        for (int i = index; i < departTimes.size(); i++) {
            TrainTime fromTime = departTimes.get(i);
            TrainTime toTime = arriveTimes.get(i);

            if (fromTime != null && toTime != null) {
                TrainJourney j = new TrainJourney();
                j.fromTime = fromTime;
                j.toTime = toTime;
                journeys.add(j);
            }
        }

        int dateDivider = journeys.size();

        for (int i = 0; i < departTimesNextDay.size(); i++) {
            TrainTime fromTime = departTimesNextDay.get(i);
            TrainTime toTime = arriveTimesNextDay.get(i);

            if (fromTime != null && toTime != null) {
                TrainJourney j = new TrainJourney();
                j.fromTime = fromTime;
                j.toTime = toTime;
                journeys.add(j);
            }
        }
        adapter.setData(journeys, dateDivider);
        adapter.notifyDataSetChanged();

    }

    private void everythingLoaded() {
        Station closestStation = null;
        double minDistance = Double.MAX_VALUE;

        if (location != null) {
            Location outboundLocation = new Location("data");
            outboundLocation.setLongitude(settings.outboundDestination.station_lng);
            outboundLocation.setLatitude(settings.outboundDestination.station_lat);

            Location inboundLocation = new Location("data");
            inboundLocation.setLongitude(settings.inboundDestination.station_lng);
            inboundLocation.setLatitude(settings.inboundDestination.station_lat);

            double outboundStationDistance = location.distanceTo(outboundLocation);
            double inboundStationDistance = location.distanceTo(inboundLocation);

            if (outboundStationDistance < inboundStationDistance) {
                fromStation = settings.outboundDestination;
                toStation = settings.inboundDestination;
            } else {
                fromStation = settings.inboundDestination;
                toStation = settings.outboundDestination;
            }
        } else {
            fromStation = settings.outboundDestination;
            toStation = settings.inboundDestination;
        }

        adapter = new TrainTripAdapter();
        upcomingTripsListView.setAdapter(adapter);
        loadTrainTrips();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_SETTINGS && data != null) {
            settings = (Settings) data.getSerializableExtra(INTENT_EXTRA_SETTINGS);
            everythingLoaded();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return dataLoaded;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivityForResult(settingsIntent, REQUEST_CODE_SETTINGS);
            return true;
        } else if (id == R.id.action_refresh) {
            loadTrainTrips();
            Toast.makeText(this, "List Refreshed", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.action_swap_stations) {
            Station tempStation = fromStation;
            fromStation = toStation;
            toStation = tempStation;
            loadTrainTrips();
        }
        return super.onOptionsItemSelected(item);
    }
}
