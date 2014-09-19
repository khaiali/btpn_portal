package com.sybase365.mobiliser.web.common.panels.alerts;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.datetime.PatternDateConverter;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;

import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerAlert;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerBlackoutSchedule;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerBlackoutTime;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerBlackoutTimeList;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerBlackoutToAlert;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.application.clients.AlertsClientLogic;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.common.components.KeyValue;
import com.sybase365.mobiliser.web.common.components.KeyValueDropDownChoice;
import com.sybase365.mobiliser.web.common.components.LocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.util.AlertsHelper;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * 
 * @author sagraw03
 */
public class AlertBlackOutSchedulePanel extends Panel {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(AlertBlackOutSchedulePanel.class);

    private static final String WICKET_ID_blackoutScheduleForm = "blackoutScheduleForm";
    private static final String WICKET_ID_botTimePeriodList = "botTimePeriodList";
    private static final String WICKET_ID_botTimeFrom = "botTimeFrom";
    private static final String WICKET_ID_botTimeTo = "botTimeTo";
    private static final String WICKET_ID_botDaysOfWeek = "botDaysOfWeek";
    private static final String WICKET_ID_botRemoveTime = "removeTime";
    private static final String WICKET_ID_addTime = "addTime";
    private static final String WICKET_ID_saveButton = "saveButton";
    private static final String WICKET_ID_cancelButton = "cancelButton";

    private AlertBlackOutPanel parentPanel;
    private MobiliserBasePage basePage;
    private AlertsClientLogic clientLogic;
    private CustomerBlackoutSchedule customerBlackoutSchedule;
    private CustomerAlert customerAlert;
    private ArrayList<java.sql.Date> dateSequenceExisting;
    private ArrayList<java.sql.Date> dateSequenceChoosen;

    private String description;
    private Date validFromDate;
    private Date validToDate;
    private String timeZone;
    private List<CustomerBlackoutTime> blackoutTimeList;
    private Integer startHr;
    private Integer startMin;
    private Integer endHr;
    private Integer endMin;
    private boolean checkMo;
    private boolean checkTu;
    private boolean checkWe;
    private boolean checkTh;
    private boolean checkFr;
    private boolean checkSa;
    private boolean checkSu;

    public String action;
    public static String EDIT_ACTION = "E";
    public static String ADD_ACTION = "A";

    public AlertBlackOutSchedulePanel(final String id,
	    final MobiliserBasePage basePage,
	    final AlertBlackOutPanel parentPanel,
	    final AlertsClientLogic clientLogic,
	    final CustomerBlackoutSchedule customerBlackoutSchedule,
	    final CustomerAlert customerAlert,
	    ArrayList<java.sql.Date> dateSequenceExisting) {

	super(id);

	this.basePage = basePage;
	this.parentPanel = parentPanel;
	this.clientLogic = clientLogic;
	this.customerBlackoutSchedule = customerBlackoutSchedule;
	this.customerAlert = customerAlert;
	this.dateSequenceExisting = dateSequenceExisting;

	LOG.debug("#AlertBlackOutSchedulePanel.init()");

	if (!PortalUtils.exists(this.customerBlackoutSchedule)) {
	    this.customerBlackoutSchedule = new CustomerBlackoutSchedule();
	}

	if (PortalUtils.exists(this.customerBlackoutSchedule.getId())) {
	    this.action = EDIT_ACTION;
	    this.description = this.customerBlackoutSchedule.getDescription();
	    this.validFromDate = (PortalUtils
		    .exists(this.customerBlackoutSchedule.getValidFrom()) ? PortalUtils
		    .getSaveDate(this.customerBlackoutSchedule.getValidFrom())
		    : null);
	    this.validToDate = (PortalUtils
		    .exists(this.customerBlackoutSchedule.getValidTo()) ? PortalUtils
		    .getSaveDate(this.customerBlackoutSchedule.getValidTo())
		    : null);
	    this.timeZone = this.customerBlackoutSchedule.getAltTimezone();
	} else {
	    this.action = ADD_ACTION;
	    this.customerBlackoutSchedule.setCustomerId(customerAlert
		    .getCustomerId());
	    this.customerBlackoutSchedule
		    .setBlackoutTimeList(new CustomerBlackoutTimeList());
	}

	LOG.debug("#AlertBlackOutSchedulePanel action: {}", this.action);

	this.blackoutTimeList = new ArrayList<CustomerBlackoutTime>(
		this.customerBlackoutSchedule.getBlackoutTimeList()
			.getBlackoutTime());

	final String chooseDtTxt = this.basePage.getLocalizer().getString(
		"datepicker.chooseDate", this.basePage);

	add(new HeaderContributor(new IHeaderContributor() {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public void renderHead(IHeaderResponse response) {

		// localize the jquery datepicker based on users locale setting
		// locale specific js includes for datepicker are available at
		// http://jquery-ui.googlecode.com/svn/trunk/ui/i18n/
		String localeLang = getLocale().getLanguage().toLowerCase();

		LOG.debug("Using DatePicker for locale language: {}",
			localeLang);

		if (PortalUtils.exists(localeLang)) {
		    response.renderJavascriptReference("scripts/jquery/i18n/jquery.ui.datepicker-"
			    + localeLang + ".js");
		}

		response.renderJavascript("\n"
			+ "jQuery(document).ready(function($) { \n"
			+ "  $('#validFromDate').datepicker( { \n"
			+ "	'changeMonth' : true, \n" + "	'showOn': 'both', \n"
			+ "	'dateFormat' : '"
			+ Constants.DATE_FORMAT_PATTERN_PICKER + "', \n"
			+ "	'buttonImage': 'images/calendar.gif', \n"
			+ "	'buttonText' : '" + chooseDtTxt + "', \n"
			+ "	'buttonOnlyImage': true} ); \n"

			+ "  $('#validToDate').datepicker( { \n"
			+ "	'changeMonth' : true, \n" + "	'showOn': 'both', \n"
			+ "	'dateFormat' : '"
			+ Constants.DATE_FORMAT_PATTERN_PICKER + "', \n"
			+ "	'buttonImage': 'images/calendar.gif', \n"
			+ "	'buttonText' : '" + chooseDtTxt + "', \n"
			+ "	'buttonOnlyImage': true} ); \n" + "});\n",
			"datePicker");
	    }
	}));

	this.constructPanel();
    }

    private void constructPanel() {

	Form<AlertBlackOutSchedulePanel> form = new Form<AlertBlackOutSchedulePanel>(
		WICKET_ID_blackoutScheduleForm,
		new CompoundPropertyModel<AlertBlackOutSchedulePanel>(this));

	form.add(new RequiredTextField<String>("description").setRequired(true)
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	form.add(new DateTextField("validFromDate", new PropertyModel<Date>(
		this, "validFromDate"), new PatternDateConverter(
		Constants.DATE_FORMAT_PATTERN_PARSE, false)).setRequired(true)
		.add(new ErrorIndicator()));

	form.add(new DateTextField("validToDate", new PropertyModel<Date>(this,
		"validToDate"), new PatternDateConverter(
		Constants.DATE_FORMAT_PATTERN_PARSE, false)).setRequired(true)
		.add(new ErrorIndicator()));

	form.add(new LocalizableLookupDropDownChoice<String>("timeZone",
		String.class, Constants.RESOURCE_BUNDLE_TIMEZONES, this,
		Boolean.FALSE, false).add(new ErrorIndicator()));

	createBlackoutTimeListView(form, getBlackoutTimeList());

	form.add(new Button(WICKET_ID_cancelButton) {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleBack();
	    };
	}.setDefaultFormProcessing(false));

	form.add(new Button(WICKET_ID_saveButton) {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleSave();
	    }
	});

	add(form);
    }

    protected void createBlackoutTimeListView(final WebMarkupContainer parent,
	    final List<CustomerBlackoutTime> botList) {

	LOG.debug(
		"createBlackoutTimeListView() -> {} black out time periods for this schedule",
		Integer.toString(botList.size()));
	parent.setOutputMarkupId(true);
	final ListView<CustomerBlackoutTime> botListView = new ListView<CustomerBlackoutTime>(
		WICKET_ID_botTimePeriodList, botList) {

	    private static final long serialVersionUID = 1L;

	    protected void populateItem(ListItem<CustomerBlackoutTime> item) {

		final CustomerBlackoutTime bot = (CustomerBlackoutTime) item
			.getModelObject();

		StringBuilder timeFrom = new StringBuilder();

		timeFrom.append(addLeadingZero(bot.getStartHr())).append(":")
			.append(addLeadingZero(bot.getStartMin()));

		StringBuilder timeTo = new StringBuilder();

		timeTo.append(addLeadingZero(bot.getEndHr())).append(":")
			.append(addLeadingZero(bot.getEndMin()));

		StringBuilder dow = new StringBuilder();
		String dowToRecur = bot.getWeekdaysToRecur();

		if (bot.isForAllDaysOfWeek().booleanValue()) {
		    dowToRecur = "1,2,3,4,5,6,7";
		}

		String[] dowToRecurArr = dowToRecur.split(",");

		if (PortalUtils.exists(dowToRecurArr)) {
		    for (int i = 0; i < dowToRecurArr.length; i++) {
			if (dowToRecurArr[i].trim().equals("1")) {
			    if (dow.length() > 0)
				dow.append(",");
			    dow.append(getLocalizer()
				    .getString(
					    "alertBlackOut.schedule.selectedDaysOfWeek.monday",
					    this));
			} else if (dowToRecurArr[i].trim().equals("2")) {
			    if (dow.length() > 0)
				dow.append(",");
			    dow.append(getLocalizer()
				    .getString(
					    "alertBlackOut.schedule.selectedDaysOfWeek.tuesday",
					    this));
			} else if (dowToRecurArr[i].trim().equals("3")) {
			    if (dow.length() > 0)
				dow.append(",");
			    dow.append(getLocalizer()
				    .getString(
					    "alertBlackOut.schedule.selectedDaysOfWeek.wednesday",
					    this));
			} else if (dowToRecurArr[i].trim().equals("4")) {
			    if (dow.length() > 0)
				dow.append(",");
			    dow.append(getLocalizer()
				    .getString(
					    "alertBlackOut.schedule.selectedDaysOfWeek.thursday",
					    this));
			} else if (dowToRecurArr[i].trim().equals("5")) {
			    if (dow.length() > 0)
				dow.append(",");
			    dow.append(getLocalizer()
				    .getString(
					    "alertBlackOut.schedule.selectedDaysOfWeek.friday",
					    this));
			} else if (dowToRecurArr[i].trim().equals("6")) {
			    if (dow.length() > 0)
				dow.append(",");
			    dow.append(getLocalizer()
				    .getString(
					    "alertBlackOut.schedule.selectedDaysOfWeek.saturday",
					    this));
			} else if (dowToRecurArr[i].trim().equals("7")) {
			    if (dow.length() > 0)
				dow.append(",");
			    dow.append(getLocalizer()
				    .getString(
					    "alertBlackOut.schedule.selectedDaysOfWeek.sunday",
					    this));
			}
		    }
		}

		if (LOG.isDebugEnabled()) {
		    LOG.debug(
			    "createBlackoutTimeListView() -> {} - {} / {}",
			    new Object[] { timeFrom.toString(),
				    timeTo.toString(), dow.toString() });
		}

		item.add(new Label(WICKET_ID_botTimeFrom, timeFrom.toString()));

		item.add(new Label(WICKET_ID_botTimeTo, timeTo.toString()));

		item.add(new Label(WICKET_ID_botDaysOfWeek, dow.toString()));

		// Remove Action
		Link<CustomerBlackoutTime> removeLink = new Link<CustomerBlackoutTime>(
			WICKET_ID_botRemoveTime, item.getModel()) {

		    private static final long serialVersionUID = 1L;

		    @Override
		    public void onClick() {
			CustomerBlackoutTime entry = (CustomerBlackoutTime) getModelObject();
			LOG.debug("removing time entry #{}", entry.getId());
			getBlackoutTimeList().remove(entry);
		    }
		};

		removeLink.add(new SimpleAttributeModifier("onclick",
			"return confirm('"
				+ getLocalizer().getString(
					"alertBlackOut.schedule.times.confirm",
					this) + "');"));

		item.add(removeLink);
	    }
	};

	parent.add(botListView);

	// default values for a new time period entry
	this.startHr = new Integer(0);
	this.startMin = new Integer(0);
	this.endHr = new Integer(24);
	this.endMin = new Integer(0);

	parent.add(new KeyValueDropDownChoice<Integer, String>("startHr",
		getSelectableHour()));
	parent.add(new KeyValueDropDownChoice<Integer, String>("startMin",
		getSelectableMinute()));
	parent.add(new KeyValueDropDownChoice<Integer, String>("endHr",
		getSelectableHour()));

	parent.add(new KeyValueDropDownChoice<Integer, String>("endMin",
		getSelectableMinute()));

	// default values for new days of week checkbox
	this.checkMo = true;
	this.checkTu = true;
	this.checkWe = true;
	this.checkTh = true;
	this.checkFr = true;
	this.checkSa = true;
	this.checkSu = true;

	parent.add(new CheckBox("checkMo") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onComponentTag(ComponentTag tag) {
		tag.put("checked", "checked");
		super.onComponentTag(tag);
	    }
	});

	parent.add(new CheckBox("checkTu") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onComponentTag(ComponentTag tag) {
		tag.put("checked", "checked");
		super.onComponentTag(tag);
	    }
	});
	parent.add(new CheckBox("checkWe") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onComponentTag(ComponentTag tag) {
		tag.put("checked", "checked");
		super.onComponentTag(tag);
	    }
	});
	parent.add(new CheckBox("checkTh") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onComponentTag(ComponentTag tag) {
		tag.put("checked", "checked");
		super.onComponentTag(tag);
	    }
	});
	parent.add(new CheckBox("checkFr") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onComponentTag(ComponentTag tag) {
		tag.put("checked", "checked");
		super.onComponentTag(tag);
	    }
	});
	parent.add(new CheckBox("checkSa") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onComponentTag(ComponentTag tag) {
		tag.put("checked", "checked");
		super.onComponentTag(tag);
	    }
	});
	parent.add(new CheckBox("checkSu") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onComponentTag(ComponentTag tag) {
		tag.put("checked", "checked");
		super.onComponentTag(tag);
	    }
	});

	// AdAd Time Period Action
	AjaxSubmitLink addLink = new AjaxSubmitLink(WICKET_ID_addTime) {

	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onSubmit(AjaxRequestTarget arg0, Form<?> arg1) {
		CustomerBlackoutTime cbt = new CustomerBlackoutTime();
		cbt.setStartHr(getStartHr());
		cbt.setStartMin(getStartMin());
		cbt.setEndHr(getEndHr());
		cbt.setEndMin(getEndMin());

		StringBuilder dow = new StringBuilder();

		if (isCheckMo() && isCheckTu() && isCheckWe() && isCheckTh()
			&& isCheckFr() && isCheckSa() && isCheckSu()) {
		    cbt.setForAllDaysOfWeek(Boolean.TRUE);
		    dow.append("1,2,3,4,5,6,7");
		} else {
		    cbt.setForAllDaysOfWeek(Boolean.FALSE);
		    if (isCheckMo()) {
			if (dow.length() > 0)
			    dow.append(",");
			dow.append("1");
		    }
		    if (isCheckTu()) {
			if (dow.length() > 0)
			    dow.append(",");
			dow.append("2");
		    }
		    if (isCheckWe()) {
			if (dow.length() > 0)
			    dow.append(",");
			dow.append("3");
		    }
		    if (isCheckTh()) {
			if (dow.length() > 0)
			    dow.append(",");
			dow.append("4");
		    }
		    if (isCheckFr()) {
			if (dow.length() > 0)
			    dow.append(",");
			dow.append("5");
		    }
		    if (isCheckSa()) {
			if (dow.length() > 0)
			    dow.append(",");
			dow.append("6");
		    }
		    if (isCheckSu()) {
			if (dow.length() > 0)
			    dow.append(",");
			dow.append("7");
		    }
		}
		cbt.setWeekdaysToRecur(dow.toString());
		getBlackoutTimeList().add(cbt);
		LOG.debug("added time entry");
		arg0.addComponent(parent);

	    };
	};

	parent.add(addLink);
    }

    public void handleSave() {

	LOG.debug("handleSave()");

	Calendar fromCalendarDate = Calendar.getInstance();
	fromCalendarDate.setTime(this.getValidFromDate());	
	XMLGregorianCalendar fromDateXml = PortalUtils
		.getSaveXMLGregorianCalendar(fromCalendarDate);
	
	Calendar toCalendarDate = Calendar.getInstance();
	toCalendarDate.setTime(this.getValidToDate());		
	XMLGregorianCalendar toDateXml = PortalUtils
		.getSaveXMLGregorianCalendar(toCalendarDate);

	// set the two dates in correct order...
	if (fromDateXml != null && toDateXml != null) {
	    if (fromDateXml.compare(toDateXml) == DatatypeConstants.GREATER) {
		// switcheroony
		XMLGregorianCalendar temp = fromDateXml;
		fromDateXml = toDateXml;
		toDateXml = temp;
	    }

	    dateSequenceChoosen = AlertsHelper.getDateSequenceFromDateRange(
		    fromDateXml.toGregorianCalendar().getTime(), toDateXml
			    .toGregorianCalendar().getTime(),
		    dateSequenceChoosen);

	    if (!validateDate(fromDateXml.toGregorianCalendar().getTime(),
		    toDateXml.toGregorianCalendar().getTime())) {
		return;
	    }
	}
	LOG.debug("From [{}] to [{}]", (fromDateXml == null ? "NOT SET"
		: fromDateXml.toString()), (toDateXml == null ? "NOT SET"
		: toDateXml.toString()));

	this.customerBlackoutSchedule.setDescription(this.getDescription());
	this.customerBlackoutSchedule.setActive(Boolean.TRUE);
	this.customerBlackoutSchedule.setValidFrom(fromDateXml);
	this.customerBlackoutSchedule.setValidTo(toDateXml);
	this.customerBlackoutSchedule.setAltTimezone(this.getTimeZone());

	CustomerBlackoutTimeList cbtList = new CustomerBlackoutTimeList();
	cbtList.getBlackoutTime().addAll(this.blackoutTimeList);
	this.customerBlackoutSchedule.setBlackoutTimeList(cbtList);

	LOG.debug("handleSave() -> time list has {} entries",
		Integer.toString(cbtList.getBlackoutTime().size()));
	if (action.equals(EDIT_ACTION)) {
	    if (clientLogic
		    .updateCustomerBlackoutSchedule(customerBlackoutSchedule) != -1) {
		updateCustomerAlertWithBlackoutSchedule(
			customerBlackoutSchedule, action);
		dateSequenceExisting = AlertsHelper
			.getDateSequenceFromDateRange(
				customerBlackoutSchedule.getValidFrom()
					.toGregorianCalendar().getTime(),
				customerBlackoutSchedule.getValidTo()
					.toGregorianCalendar().getTime(),
				dateSequenceExisting);
		parentPanel.hideBlackoutScheduleEditPanel();
	    }
	} else {
	    if (clientLogic
		    .createCustomerBlackoutSchedule(customerBlackoutSchedule,
			    customerAlert.getId().longValue()) != -1) {
		updateCustomerAlertWithBlackoutSchedule(
			customerBlackoutSchedule, action);
		parentPanel.hideBlackoutScheduleEditPanel();
		parentPanel.addBlackoutSchedule(customerBlackoutSchedule);

	    }

	}
    }

    public void updateCustomerAlertWithBlackoutSchedule(
	    CustomerBlackoutSchedule customerBlackoutSchedule, String action) {
	if (customerAlert != null && customerAlert.getBlackoutList() != null
		&& customerAlert.getBlackoutList().getBlackout() != null) {
	    if (action.equals(EDIT_ACTION)) {
		for (CustomerBlackoutToAlert customerBlackoutToAlert : customerAlert
			.getBlackoutList().getBlackout()) {
		    if (customerBlackoutToAlert.getBlackoutSchedule().getId()
			    .equals(customerBlackoutSchedule.getId())) {
			customerBlackoutToAlert
				.setBlackoutSchedule(customerBlackoutSchedule);
			List<CustomerBlackoutTime> customerBlackoutTimelist = customerBlackoutSchedule
				.getBlackoutTimeList().getBlackoutTime();
			for (CustomerBlackoutTime customerBlackoutTime : customerBlackoutTimelist) {
			    customerBlackoutTime.setId(null);
			}
		    }
		}
	    } else {
		CustomerBlackoutToAlert customerBlackoutToAlert = new CustomerBlackoutToAlert();
		customerBlackoutToAlert
			.setBlackoutSchedule(customerBlackoutSchedule);
		customerAlert.getBlackoutList().getBlackout()
			.add(customerBlackoutToAlert);
	    }
	}
    }

    private boolean validateDate(Date fromDate, Date toDate) {
	boolean validate = true;
	Date todayDate = AlertsHelper.startOfDay();
	if (AlertsHelper.checkDatesFromDateRange(dateSequenceExisting,
		dateSequenceChoosen)) {
	    dateSequenceChoosen.clear();
	    error(getLocalizer().getString(
		    "alertBlackOut.schedule.dateRange.error", this));
	    validate = false;

	} else if (fromDate.before(todayDate) || toDate.before(todayDate)) {
	    validate = false;
	    error(getLocalizer().getString(
		    "alertBlackOut.schedule.datevalidation.error", this));
	}

	return validate;
    }

    public void handleBack() {
	parentPanel.hideBlackoutScheduleEditPanel();
    }

    public void setDescription(String value) {
	this.description = value;
    }

    public String getDescription() {
	return this.description;
    }

    public void setValidFromDate(Date value) {
	this.validFromDate = value;
    }

    public Date getValidFromDate() {
	return this.validFromDate;
    }

    public void setValidToDate(Date value) {
	this.validToDate = value;
    }

    public Date getValidToDate() {
	return this.validToDate;
    }

    public void setTimeZone(String timeZone) {
	this.timeZone = timeZone;
    }

    public String getTimeZone() {
	return timeZone;
    }

    public void setStartHr(Integer startHr) {
	this.startHr = startHr;
    }

    public Integer getStartHr() {
	return this.startHr;
    }

    public void setStartMin(Integer startMin) {
	this.startMin = startMin;
    }

    public Integer getStartMin() {
	return this.startMin;
    }

    public void setEndHr(Integer endHr) {
	this.endHr = endHr;
    }

    public Integer getEndHr() {
	return this.endHr;
    }

    public void setEndMin(Integer endMin) {
	this.endMin = endMin;
    }

    public Integer getEndMin() {
	return this.endMin;
    }

    public void setCheckMo(boolean value) {
	this.checkMo = value;
    }

    public boolean isCheckMo() {
	return this.checkMo;
    }

    public void setCheckTu(boolean value) {
	this.checkTu = value;
    }

    public boolean isCheckTu() {
	return this.checkTu;
    }

    public void setCheckWe(boolean value) {
	this.checkWe = value;
    }

    public boolean isCheckWe() {
	return this.checkWe;
    }

    public void setCheckTh(boolean value) {
	this.checkTh = value;
    }

    public boolean isCheckTh() {
	return this.checkTh;
    }

    public void setCheckFr(boolean value) {
	this.checkFr = value;
    }

    public boolean isCheckFr() {
	return this.checkFr;
    }

    public void setCheckSa(boolean value) {
	this.checkSa = value;
    }

    public boolean isCheckSa() {
	return this.checkSa;
    }

    public void setCheckSu(boolean value) {
	this.checkSu = value;
    }

    public boolean isCheckSu() {
	return this.checkSu;
    }

    private String addLeadingZero(Integer value) {
	if (PortalUtils.exists(value)) {
	    if (value.intValue() < 10) {
		return "0" + String.valueOf(value);
	    }
	    return String.valueOf(value);
	} else {
	    return "NA";
	}
    }

    private List<KeyValue<Integer, String>> getSelectableHour() {

	List<KeyValue<Integer, String>> list = new ArrayList<KeyValue<Integer, String>>();

	for (int i = 0; i <= 24; i++) {
	    list.add(new KeyValue<Integer, String>(Integer.valueOf(i),
		    addLeadingZero(Integer.valueOf(i))));
	}

	return list;
    }

    private List<KeyValue<Integer, String>> getSelectableMinute() {

	List<KeyValue<Integer, String>> list = new ArrayList<KeyValue<Integer, String>>();

	for (int i = 0; i < 60;) {
	    list.add(new KeyValue<Integer, String>(Integer.valueOf(i),
		    addLeadingZero(Integer.valueOf(i))));
	    i += 5;
	}

	return list;
    }

    private List<CustomerBlackoutTime> getBlackoutTimeList() {
	return this.blackoutTimeList;
    }
}