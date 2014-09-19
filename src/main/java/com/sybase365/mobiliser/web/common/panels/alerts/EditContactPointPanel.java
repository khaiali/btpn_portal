package com.sybase365.mobiliser.web.common.panels.alerts;

import java.util.Date;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.datetime.PatternDateConverter;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;

import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerOtherIdentification;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.consumer.pages.portal.selfcare.alerts.ContactPointsPage;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class EditContactPointPanel extends Panel {

    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(EditContactPointPanel.class);

    private MobiliserBasePage basePage;
    private CustomerOtherIdentification entry;

    private String deviceName;
    private String carrier;
    private String country;
    private String number;
    private boolean browserMsgsEnabled;
    private String authSecretText;
    private boolean receiveLoginToken;
    private boolean enableTextMsgAlerts;
    private boolean disableAlertsPeriod;
    private Date disableAlertsPeriodFrom;
    private Date disableAlertsPeriodTo;
    private boolean disableAlertsDay;
    private String disableAlertsDayFrom;
    private String disableAlertsDayTo;
    private boolean disableAlertsWeekends;
    AjaxCheckBox disableAlertsPeriodCheck = null;
    AjaxCheckBox disableAlertsDayCheck = null;
    CheckBox disableAlertsWeekendsCheck = null;
    AjaxCheckBox enableTextMsgAlertsCheckbox = null;

    public EditContactPointPanel(String id, MobiliserBasePage mobBasePage,
	    CustomerOtherIdentification entry) {
	super(id);
	this.basePage = mobBasePage;

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
			+ "  $('#disableAlertsPeriodFrom').datepicker( { \n"
			+ "	'changeMonth' : true, \n" + "	'showOn': 'both', \n"
			+ "	'dateFormat' : '"
			+ Constants.DATE_FORMAT_PATTERN_PICKER + "', \n"
			+ "	'buttonImage': 'images/calendar.gif', \n"
			+ "	'buttonText' : '" + chooseDtTxt + "', \n"
			+ "	'buttonOnlyImage': true} ); \n"

			+ "  $('#disableAlertsPeriodTo').datepicker( { \n"
			+ "	'changeMonth' : true, \n" + "	'showOn': 'both', \n"
			+ "	'dateFormat' : '"
			+ Constants.DATE_FORMAT_PATTERN_PICKER + "', \n"
			+ "	'buttonImage': 'images/calendar.gif', \n"
			+ "	'buttonText' : '" + chooseDtTxt + "', \n"
			+ "	'buttonOnlyImage': true} ); \n" + "});\n",
			"datePicker");
	    }
	}));

	this.basePage = mobBasePage;
	this.entry = entry;
	this.constructPanel();
    }

    private void constructPanel() {
	editContactPointsDiv();
    }

    private WebMarkupContainer editContactPointsDiv() {

	WebMarkupContainer editContactPointsDiv = new WebMarkupContainer(
		"editContactPointsDiv");
	updateModel();
	final Form<EditContactPointPanel> form = new Form<EditContactPointPanel>(
		"editContactPointForm",
		new CompoundPropertyModel<EditContactPointPanel>(this));

	form.add(new FeedbackPanel("errorMessages"));

	WebMarkupContainer deviceInfoContainer = new WebMarkupContainer(
		"deviceInfoContainer");

	this.createDeviceInfoContainerView(deviceInfoContainer, true);
	form.add(deviceInfoContainer);

	WebMarkupContainer deviceAlertPrefsContainer = new WebMarkupContainer(
		"deviceAlertPrefsContainer");
	this.createDeviceAlertPrefsContainerView(deviceAlertPrefsContainer,
		true);
	form.add(new Button("ecConfirm") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		entry.setNickname(deviceName);
		entry.setIdentification(number);
		basePage.updateCustomerOtherIdentification(entry);
		getSession().info(
			getLocalizer().getString(
				"editContactPoint.update.sucess", this));
		setResponsePage(ContactPointsPage.class);

	    };
	});

	form.add(new Button("ecConfirmback") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		setResponsePage(ContactPointsPage.class);
	    };
	}.setDefaultFormProcessing(false));
	form.add(deviceAlertPrefsContainer);
	editContactPointsDiv.add(form);
	add(editContactPointsDiv);
	return editContactPointsDiv;
    }

    private void updateModel() {
	if (entry != null) {
	    deviceName = entry.getNickname();
	    number = entry.getIdentification();
	}
    }

    private void createDeviceInfoContainerView(WebMarkupContainer parent,
	    boolean enabled) {

	parent.add(new TextField<String>("deviceName").setRequired(enabled)
		.setEnabled(enabled).add(new ErrorIndicator()));
	parent.add(new Label("number"));
	/*
	 * Comment this out until we can figure out where to store it
	 * parent.add(new CheckBox("browserMsgsEnabled")); parent.add(new
	 * TextField<String>("authSecretText").setRequired(enabled)
	 * .setEnabled(enabled).add(new ErrorIndicator())); parent.add(new
	 * CheckBox("receiveLoginToken").setEnabled(enabled));
	 */
    }

    private void createDeviceAlertPrefsContainerView(WebMarkupContainer parent,
	    boolean enabled) {

	final WebMarkupContainer dateRangeContainer = new WebMarkupContainer(
		"dateRangeContainer");

	final WebMarkupContainer timeFrameContainer = new WebMarkupContainer(
		"timeFrameContainer");

	enableTextMsgAlertsCheckbox = new AjaxCheckBox("enableTextMsgAlerts",
		new PropertyModel(this, "enableTextMsgAlerts")) {
	    @Override
	    protected void onUpdate(AjaxRequestTarget target) {
		disableAlertsPeriodCheck.setEnabled(enableTextMsgAlerts);
		disableAlertsDayCheck.setEnabled(enableTextMsgAlerts);
		disableAlertsWeekendsCheck.setEnabled(enableTextMsgAlerts);

		if (!enableTextMsgAlerts) {
		    disableAlertsPeriod = false;
		    disableAlertsDay = false;
		    disableAlertsWeekends = false;
		    dateRangeContainer.setVisible(false);
		    timeFrameContainer.setVisible(false);
		    target.addComponent(dateRangeContainer);
		    target.addComponent(timeFrameContainer);

		}
		target.addComponent(disableAlertsPeriodCheck);
		target.addComponent(disableAlertsDayCheck);
		target.addComponent(disableAlertsWeekendsCheck);
	    }
	};
	parent.add(enableTextMsgAlertsCheckbox);
	disableAlertsPeriodCheck = new AjaxCheckBox("disableAlertsPeriod",
		new PropertyModel(this, "disableAlertsPeriod")) {
	    @Override
	    protected void onUpdate(AjaxRequestTarget target) {
		dateRangeContainer.setVisible(disableAlertsPeriod);
		target.addComponent(dateRangeContainer);
		target.appendJavascript("$j('#disableAlertsPeriodFrom').datepicker( { \n"
			+ "	'changeMonth' : true, \n"
			+ "	'showOn': 'both', \n"
			+ "	'dateFormat' : '"
			+ Constants.DATE_FORMAT_PATTERN_PICKER
			+ "', \n"
			+ "	'buttonImage': 'images/calendar.gif', \n"
			+ "	'buttonOnlyImage': true} ); \n"
			+ "$j('#disableAlertsPeriodTo').datepicker( { \n"
			+ "	'changeMonth' : true,  \n"
			+ "	'showOn': 'both', \n"
			+ "	'dateFormat' : '"
			+ Constants.DATE_FORMAT_PATTERN_PICKER
			+ "', \n"
			+ "	'buttonImage': 'images/calendar.gif', \n"
			+ "	'buttonOnlyImage': true} ); \n");
	    }
	};
	disableAlertsPeriodCheck.setOutputMarkupPlaceholderTag(true)
		.setEnabled(false);
	disableAlertsPeriodCheck.setOutputMarkupId(true);
	parent.add(disableAlertsPeriodCheck);

	DateTextField fromDateTxt = null;
	DateTextField toDateTxt = null;

	fromDateTxt = (DateTextField) new DateTextField(
		"disableAlertsPeriodFrom", new PropertyModel<Date>(this,
			"disableAlertsPeriodFrom"), new PatternDateConverter(
			Constants.DATE_FORMAT_PATTERN_PARSE, false));
	fromDateTxt.setRequired(true);
	fromDateTxt.add(new ErrorIndicator());

	toDateTxt = (DateTextField) new DateTextField("disableAlertsPeriodTo",
		new PropertyModel<Date>(this, "disableAlertsPeriodTo"),
		new PatternDateConverter(Constants.DATE_FORMAT_PATTERN_PARSE,
			false));
	toDateTxt.setRequired(true);
	toDateTxt.add(new ErrorIndicator());

	dateRangeContainer.add(fromDateTxt);
	dateRangeContainer.add(toDateTxt);
	dateRangeContainer.setVisible(false);
	dateRangeContainer.setOutputMarkupPlaceholderTag(true);

	parent.add(dateRangeContainer);
	timeFrameContainer.setOutputMarkupPlaceholderTag(true);
	timeFrameContainer.setVisible(false);

	TextField<String> disableAlertsDayFromTxt = new TextField<String>(
		"disableAlertsDayFrom");
	disableAlertsDayFromTxt.setRequired(true).add(new ErrorIndicator());
	disableAlertsDayFromTxt.setEnabled(enabled);
	timeFrameContainer.add(disableAlertsDayFromTxt);

	TextField disableAlertsDayToTxt = new TextField<String>(
		"disableAlertsDayTo");
	disableAlertsDayFromTxt.setRequired(true).add(new ErrorIndicator());
	timeFrameContainer.add(disableAlertsDayToTxt);

	parent.add(timeFrameContainer);

	disableAlertsDayCheck = new AjaxCheckBox("disableAlertsDay",
		new PropertyModel(this, "disableAlertsDay")) {
	    @Override
	    protected void onUpdate(AjaxRequestTarget target) {
		if (disableAlertsDay) {
		    timeFrameContainer.setVisible(true);
		} else {
		    timeFrameContainer.setVisible(false);
		}
		target.addComponent(timeFrameContainer);

	    }

	};
	disableAlertsDayCheck.setOutputMarkupPlaceholderTag(true).setEnabled(
		false);

	parent.add(disableAlertsDayCheck);

	disableAlertsWeekendsCheck = new CheckBox("disableAlertsWeekends");
	disableAlertsWeekendsCheck.setOutputMarkupPlaceholderTag(true)
		.setEnabled(false);
	parent.add(disableAlertsWeekendsCheck);

    }

    public String getDeviceName() {
	return deviceName;
    }

    public void setDeviceName(String deviceName) {
	this.deviceName = deviceName;
    }

    public String getCarrier() {
	return carrier;
    }

    public void setCarrier(String carrier) {
	this.carrier = carrier;
    }

    public String getCountry() {
	return country;
    }

    public void setCountry(String country) {
	this.country = country;
    }

    public String getNumber() {
	return number;
    }

    public void setNumber(String number) {
	this.number = number;
    }

    public boolean isBrowserMsgsEnabled() {
	return browserMsgsEnabled;
    }

    public void setBrowserMsgsEnabled(boolean browserMsgsEnabled) {
	this.browserMsgsEnabled = browserMsgsEnabled;
    }

    public String getAuthSecretText() {
	return authSecretText;
    }

    public void setAuthSecretText(String authSecretText) {
	this.authSecretText = authSecretText;
    }

    public boolean isReceiveLoginToken() {
	return receiveLoginToken;
    }

    public void setReceiveLoginToken(boolean receiveLoginToken) {
	this.receiveLoginToken = receiveLoginToken;
    }

    public boolean isEnableTextMsgAlerts() {
	return enableTextMsgAlerts;
    }

    public void setEnableTextMsgAlerts(boolean enableTextMsgAlerts) {
	this.enableTextMsgAlerts = enableTextMsgAlerts;
    }

    public boolean isDisableAlertsPeriod() {
	return disableAlertsPeriod;
    }

    public void setDisableAlertsPeriod(boolean disableAlertsPeriod) {
	this.disableAlertsPeriod = disableAlertsPeriod;
    }

    public Date getDisableAlertsPeriodFrom() {
	return disableAlertsPeriodFrom;
    }

    public void setDisableAlertsPeriodFrom(Date disableAlertsPeriodFrom) {
	this.disableAlertsPeriodFrom = disableAlertsPeriodFrom;
    }

    public Date getDisableAlertsPeriodTo() {
	return disableAlertsPeriodTo;
    }

    public void setDisableAlertsPeriodTo(Date disableAlertsPeriodTo) {
	this.disableAlertsPeriodTo = disableAlertsPeriodTo;
    }

    public boolean isDisableAlertsDay() {
	return disableAlertsDay;
    }

    public void setDisableAlertsDay(boolean disableAlertsDay) {
	this.disableAlertsDay = disableAlertsDay;
    }

    public String getDisableAlertsDayFrom() {
	return disableAlertsDayFrom;
    }

    public void setDisableAlertsDayFrom(String disableAlertsDayFrom) {
	this.disableAlertsDayFrom = disableAlertsDayFrom;
    }

    public String getDisableAlertsDayTo() {
	return disableAlertsDayTo;
    }

    public void setDisableAlertsDayTo(String disableAlertsDayTo) {
	this.disableAlertsDayTo = disableAlertsDayTo;
    }

    public boolean isDisableAlertsWeekends() {
	return disableAlertsWeekends;
    }

    public void setDisableAlertsWeekends(boolean disableAlertsWeekends) {
	this.disableAlertsWeekends = disableAlertsWeekends;
    }

}
