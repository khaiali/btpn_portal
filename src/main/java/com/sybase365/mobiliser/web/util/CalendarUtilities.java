package com.sybase365.mobiliser.web.util;

import java.util.Calendar;

public class CalendarUtilities {

    /**
     * Sets the time of the given calendar to the first millisecond of the day
     * that is already set.
     * 
     * @param c
     *            The calendar whose time should be set to the first millisecond
     *            of the given day.
     */
    public static void setToDaysFirstMillisecond(Calendar c) {
	// The following lines are necessary to retrieve the "first" midnight of
	// the day. Because of the daylight savings, a day can have two
	// midnights (in some time zones).
	c.add(Calendar.DAY_OF_YEAR, -1);
	c.set(Calendar.HOUR_OF_DAY, 23);
	c.set(Calendar.MINUTE, 59);
	c.set(Calendar.SECOND, 59);
	c.set(Calendar.MILLISECOND, 999);
	c.add(Calendar.MILLISECOND, 1);
    }
}
