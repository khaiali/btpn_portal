package com.sybase365.mobiliser.web.btpn.bank.beans;

import java.io.Serializable;
import java.util.Date;

public class HolidayListBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String toDate;

	private String fromDate;

	private String description;

	private Date fromDateString;

	private Date toDateString;

	private long holidayId;

	private boolean addHolidaysuccess;

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getFromDateString() {
		return fromDateString;
	}

	public void setFromDateString(Date fromDateString) {
		this.fromDateString = fromDateString;
	}

	public Date getToDateString() {
		return toDateString;
	}

	public void setToDateString(Date toDateString) {
		this.toDateString = toDateString;
	}

	public boolean isAddHolidaysuccess() {
		return addHolidaysuccess;
	}

	public void setAddHolidaysuccess(boolean addHolidaysuccess) {
		this.addHolidaysuccess = addHolidaysuccess;
	}

	public long getHolidayId() {
		return holidayId;
	}

	public void setHolidayId(long holidayId) {
		this.holidayId = holidayId;
	}

}
