package com.sybase365.mobiliser.web.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerBlackoutTime;

/**
 * 
 * @author sagraw03
 */

public class AlertBlackOutChooserBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean enableTextMsgAlerts;
    private boolean disableAlertsPeriod;
    private Date disableAlertsPeriodFrom;
    private Date disableAlertsPeriodTo;
    private boolean disableAlertsDay;
    private String disableAlertsDayFrom;
    private String disableAlertsDayTo;
    private boolean disableAlertsWeekends;

    private List<CustomerBlackoutTime> customerBlackoutTimelist;

    public void setEnableTextMsgAlerts(boolean enableTextMsgAlerts) {
	this.enableTextMsgAlerts = enableTextMsgAlerts;
    }

    public boolean isEnableTextMsgAlerts() {
	return enableTextMsgAlerts;
    }

    public void setDisableAlertsPeriod(boolean disableAlertsPeriod) {
	this.disableAlertsPeriod = disableAlertsPeriod;
    }

    public boolean isDisableAlertsPeriod() {
	return disableAlertsPeriod;
    }

    public void setDisableAlertsPeriodFrom(Date disableAlertsPeriodFrom) {
	this.disableAlertsPeriodFrom = disableAlertsPeriodFrom;
    }

    public Date getDisableAlertsPeriodFrom() {
	return disableAlertsPeriodFrom;
    }

    public void setDisableAlertsPeriodTo(Date disableAlertsPeriodTo) {
	this.disableAlertsPeriodTo = disableAlertsPeriodTo;
    }

    public Date getDisableAlertsPeriodTo() {
	return disableAlertsPeriodTo;
    }

    public void setDisableAlertsDay(boolean disableAlertsDay) {
	this.disableAlertsDay = disableAlertsDay;
    }

    public boolean isDisableAlertsDay() {
	return disableAlertsDay;
    }

    public void setDisableAlertsDayFrom(String disableAlertsDayFrom) {
	this.disableAlertsDayFrom = disableAlertsDayFrom;
    }

    public String getDisableAlertsDayFrom() {
	return disableAlertsDayFrom;
    }

    public void setDisableAlertsDayTo(String disableAlertsDayTo) {
	this.disableAlertsDayTo = disableAlertsDayTo;
    }

    public String getDisableAlertsDayTo() {
	return disableAlertsDayTo;
    }

    public void setDisableAlertsWeekends(boolean disableAlertsWeekends) {
	this.disableAlertsWeekends = disableAlertsWeekends;
    }

    public boolean isDisableAlertsWeekends() {
	return disableAlertsWeekends;
    }

    public void setCustomerBlackoutTimelist(
	    List<CustomerBlackoutTime> customerBlackoutTimelist) {
	this.customerBlackoutTimelist = customerBlackoutTimelist;
    }

    public List<CustomerBlackoutTime> getCustomerBlackoutTimelist() {
	return customerBlackoutTimelist;
    }

}
