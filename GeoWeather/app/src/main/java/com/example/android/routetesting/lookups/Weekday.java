package com.example.android.routetesting.lookups;

/**
 * Created by AaronEnglerPXL on 17/10/2017.
 */

public enum Weekday {

    //Calendar.WEEK_OF_DAY has Sunday as first day of the week
    SUNDAY("Sunday"),
    MONDAY("Monday"),
    TUESDAY("Tuesday"),
    WEDNESDAY("Wednesday"),
    THURSDAY("Thursday"),
    FRIDAY("Friday"),
    SATURDAY("Saturday");


    private String value;

    Weekday(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }


}
