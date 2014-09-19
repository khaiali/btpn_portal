package com.sybase365.mobiliser.web.util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.AlertNotificationMessage;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.AlertType;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerAlert;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerContactPoint;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.beans.MobileAlertsBean;

/**
 * @author msw
 */
public class AlertsHelper {

    private static final long serialVersionUID = 1L;
    private static final int MILLIS_IN_DAY = 1000 * 60 * 60 * 24;
    private static final Logger LOG = LoggerFactory
	    .getLogger(AlertsHelper.class);

    /**
     * Map alert notification type to a specific notification message id for
     * this alert.
     * 
     * @param alertNotificationTypeId
     *            The id of the type of the notification message
     * 
     * @return long The id of the notification message for this type
     */
    public static long getAlertNotificationMessageId(final AlertType alertType,
	    final long alertNotificationTypeId) {

	if (alertType != null
		&& alertType.getAlertNotificationMsgList() != null) {
	    for (AlertNotificationMessage notifMsg : alertType
		    .getAlertNotificationMsgList().getNotificationMsg()) {
		if (notifMsg.getNotificationMsgTypeId() == alertNotificationTypeId) {
		    LOG
			    .debug(
				    "#AlertsHelpers Alert notif msg id {} for notif msg type {} ",
				    notifMsg.getId(), Long
					    .valueOf(alertNotificationTypeId));
		    return notifMsg.getId().longValue();
		}
	    }
	}
	// this will end up with a entity not found exception, but is all we can
	// do here
	return 0L;
    }

    /**
     * Map alert notification message id to a specific notification type for
     * this alert.
     * 
     * @param alertNotificationMessageId
     *            The notification message id
     * 
     * @return long The type of the notification message for this id
     */
    public static long getAlertNotificationMessageTypeId(
	    final AlertType alertType, final long alertNotificationMessageId) {

	if (alertType != null
		&& alertType.getAlertNotificationMsgList() != null) {
	    for (AlertNotificationMessage notifMsg : alertType
		    .getAlertNotificationMsgList().getNotificationMsg()) {
		if (notifMsg.getId().longValue() == alertNotificationMessageId) {
		    LOG
			    .debug(
				    "#BaseAlertPanel Alert notif type {} for notif msg id {} ",
				    Long.valueOf(notifMsg
					    .getNotificationMsgTypeId()),
				    notifMsg.getId());
		    return notifMsg.getNotificationMsgTypeId();
		}
	    }
	}
	// default if actual notif message not found to text
	return Constants.ALERT_NOTIF_MSG_TYPE_TEXT;
    }

    /**
     * Helper for converting entered amount values into the equivalent of long
     * amount values in the database. We don't actually store long values for
     * alerts, as all data is stored as generalised String based data, however,
     * that String is a representation of a long value and so we have to go
     * through the process of converting it from a String to a long back to a
     * String again, in order to get to a consistente currency amount
     * 
     * @param amountString
     *            The amount as entered
     * 
     * @return String The String representation of the amount as entered after
     *         conversion to a long amount
     */
    public static String convertAmountToStore(final String amountString,
	    final MobiliserBasePage basePage, MobileAlertsBean mobileAlertsBean) {
	LOG.debug("#BaseAlertPanel.convertAmountToStore()");
	String amountConverted = "-1";
	if (mobileAlertsBean.getLogicOperator() != null
		&& mobileAlertsBean.getLogicOperator().equalsIgnoreCase(
			Constants.ANY_OPERATOR)) {
	    amountConverted = "0";
	}
	if (!PortalUtils.exists(amountString)) {
	    return amountConverted;
	} else {
	    try {
		amountConverted = String.valueOf(basePage
			.convertAmountToLong(amountString));

	    } catch (ParseException e) {
		LOG.error("# could not parse amount[" + amountString + "]", e);
	    }
	}
	return amountConverted;
    }

    public static String convertDurationToStore(final String durations) {
	String durationsConverted = "";
	String[] str = durations.split("_");
	durationsConverted = str[1];
	return durationsConverted;
    }

    public static String convertDurationFromStore(
	    final MobiliserBasePage basePage, String key) {
	return basePage.getDisplayValue(key, "convertDurationsToDisplay");
    }

    /**
     * Helper for converting stored amount values into a value in the format of
     * amounts as entered from the UI.
     * 
     * @see#convertAmountToStore
     * 
     * @param amountString
     *            The amount as stored
     * 
     * @return String The String representation of the amount as will be
     *         presented to the UI
     */
    public static String convertAmountFromStore(final String amountString,
	    final MobiliserBasePage basePage) {
	LOG.debug("#BaseAlertPanel.convertAmountFromStore()");
	if (!PortalUtils.exists(amountString)) {
	    return "0";
	} else if (amountString.equals("-1") || amountString.equals("0")) {
	    return "";
	}
	return basePage.convertAmountToString(Long.valueOf(amountString)
		.longValue());
    }

    /**
     * getAlertContactPoints return all the CustomerContactPoint in existing
     * CustomerAlert Object.
     * 
     * @param customerAlert
     * @return
     */
    public static List<CustomerContactPoint> getAlertContactPoints(
	    final CustomerAlert customerAlert) {
	if (customerAlert.getContactPointList() != null) {
	    return customerAlert.getContactPointList().getContactPoint();
	} else {
	    return Collections.emptyList();
	}
    }

    public static boolean checkDatesFromDateRange(
	    List<java.sql.Date> dateSequenceExisting,
	    List<java.sql.Date> dateSequenceChoosen) {
	if (dateSequenceExisting != null && dateSequenceChoosen != null) {
	    if (CollectionUtils.containsAny(dateSequenceExisting,
		    dateSequenceChoosen)) {
		return true;
	    } else {
		return false;
	    }
	} else {
	    return false;
	}

    }

    public static Date startOfDay() {
	Calendar dCal = Calendar.getInstance();
	dCal.set(Calendar.HOUR_OF_DAY, 0);
	dCal.set(Calendar.MINUTE, 0);
	dCal.set(Calendar.SECOND, 0);
	dCal.set(Calendar.MILLISECOND, 0);

	return dCal.getTime();
    }

    public static ArrayList<java.sql.Date> getDateSequenceFromDateRange(
	    Date startDate, Date endDate, ArrayList<java.sql.Date> dateSequence) {
	if (endDate.before(startDate)) {
	    return null;
	}
	if (dateSequence == null) {
	    dateSequence = new ArrayList<java.sql.Date>();
	}

	Date tmpDate = startDate;
	do {
	    dateSequence.add(new java.sql.Date(tmpDate.getTime()));
	    tmpDate = new Date(tmpDate.getTime() + MILLIS_IN_DAY);
	} while (tmpDate.before(endDate) || tmpDate.equals(endDate));

	return dateSequence;

    }

    public static ArrayList<java.sql.Date> updateDateSequenceFromDateRange(
	    Date startDate, Date endDate, ArrayList<java.sql.Date> dateSequence) {

	if (endDate.before(startDate)) {
	    return null;
	}
	if (dateSequence == null) {
	    dateSequence = new ArrayList<java.sql.Date>();
	}

	Date tmpDate = startDate;
	do {
	    dateSequence.remove(new java.sql.Date(tmpDate.getTime()));
	    tmpDate = new Date(tmpDate.getTime() + MILLIS_IN_DAY);
	} while (tmpDate.before(endDate) || tmpDate.equals(endDate));

	return dateSequence;

    }

}