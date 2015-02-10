package com.lucasmoellers.metraupn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TrainTripAdapter extends BaseAdapter {
    List<TrainJourney> journeyList;
    int dividerIndex;

    public TrainTripAdapter() {
        journeyList = new ArrayList<>();
    }

    public void setData(List<TrainJourney> journeyList, int dateDivider) {
        this.journeyList = journeyList;
        this.dividerIndex = dateDivider;
    }
    @Override
    public int getCount() {
        return journeyList.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewItem = convertView;

        // divider
        if (position == dividerIndex) {
            viewItem = inflater.inflate(R.layout.date_divider, null);
            return viewItem;
        }
        int adjustPosition = position;
        if (position > dividerIndex) {
            adjustPosition--;
        }

        // regular row
        if (convertView == null || convertView.findViewById(R.id.depart_time) == null) {
            viewItem = inflater.inflate(R.layout.train_trip, null);
        }
        TrainJourney journey = journeyList.get(adjustPosition);
        TextView departTime = (TextView) viewItem.findViewById(R.id.depart_time);
        departTime.setText(journey.fromTime.toString());
        TextView arriveTime = (TextView) viewItem.findViewById(R.id.arrive_time);
        arriveTime.setText(journey.toTime.toString());
        TextView tripDuration = (TextView) viewItem.findViewById(R.id.trip_duration);
        tripDuration.setText(journey.getDuration());
        return viewItem;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}
