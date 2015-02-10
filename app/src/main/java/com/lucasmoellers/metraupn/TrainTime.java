package com.lucasmoellers.metraupn;

import java.io.Serializable;

public class TrainTime implements Serializable {
	public int hour;
	public int minute;
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		int modifiedHour = hour;
		boolean pm = modifiedHour > 11;
		if (modifiedHour > 12) {
			modifiedHour -= 12;
		}
		if (hour == 0) {
			modifiedHour = 12;
		}
		
		sb.append(String.valueOf(modifiedHour)).append(":");
		
		if (minute < 10) {
			sb.append("0");
		}
		sb.append(String.valueOf(minute));
		
		if (pm) {
			sb.append(" PM");
		} else {
			sb.append(" AM");
		}
		return sb.toString();
	}
	
	
}
