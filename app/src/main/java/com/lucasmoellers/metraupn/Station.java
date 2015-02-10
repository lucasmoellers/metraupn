package com.lucasmoellers.metraupn;

import java.io.Serializable;
import java.util.List;

public class Station implements Serializable {
	public String station_name;
	public double station_lat;
	public double station_lng;
	public List<String> outbound_weekday;
	public List<String> outbound_saturday;
	public List<String> outbound_sunday_holiday;
	public List<String> inbound_weekday;
	public List<String> inbound_saturday;
	public List<String> inbound_sunday_holiday;
	
	public List<TrainTime> outbound_weekday_times;
	public List<TrainTime> outbound_saturday_times;
	public List<TrainTime> outbound_sunday_holiday_times;
	
	public List<TrainTime> inbound_weekday_times;
	public List<TrainTime> inbound_saturday_times;
	public List<TrainTime> inbound_sunday_holiday_times;
}
