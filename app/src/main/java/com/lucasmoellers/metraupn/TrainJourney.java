package com.lucasmoellers.metraupn;

public class TrainJourney {
    public TrainTime fromTime;
    public TrainTime toTime;

    public String getDuration() {
        int fromTimeHour = fromTime.hour;
        int toTimeHour = toTime.hour;
        if (fromTimeHour > toTimeHour) {
            toTimeHour += 24;
        }
        int minuteDifference = (toTimeHour - fromTimeHour) * 60 + (toTime.minute - fromTime.minute);
        int hourDifference = minuteDifference / 60;
        minuteDifference = minuteDifference % 60;
        StringBuilder durationBuilder = new StringBuilder();
        if (hourDifference > 0) {
            durationBuilder.append(hourDifference).append(" hour");
            if (hourDifference > 1) {
                durationBuilder.append('s');
            }
        }
        if (minuteDifference > 0) {
            if (hourDifference > 0) {
                durationBuilder.append(' ');
            }
            durationBuilder.append(minuteDifference).append(" minute");
            if (minuteDifference > 1) {
                durationBuilder.append('s');
            }
        }
        return durationBuilder.toString();
    }
}
