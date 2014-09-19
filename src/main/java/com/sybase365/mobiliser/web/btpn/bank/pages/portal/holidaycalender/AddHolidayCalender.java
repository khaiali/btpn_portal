package com.sybase365.mobiliser.web.btpn.bank.pages.portal.holidaycalender;

import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.holidaycalendar.HolidayCalendar;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.holidaycalendar.AddHolidayRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.holidaycalendar.AddHolidayResponse;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.bank.beans.HolidayListBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.behaviours.DateHeaderContributor;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.HolidayCalenderDateValidator;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * @author Srinivasulu
 */
public class AddHolidayCalender extends BtpnBaseBankPortalSelfCarePage {
	private static final Logger LOG = LoggerFactory.getLogger(AddHolidayCalender.class);

	HolidayListBean holidayBean;

	public AddHolidayCalender() {
		addDateHeaderContributor();
		initPageComponents();
	}

	public AddHolidayCalender(HolidayListBean holidayBean) {
		this.holidayBean = holidayBean;
		addDateHeaderContributor();
		initPageComponents();
	}

	/**
	 * Adds the date header contributor
	 */
	protected void addDateHeaderContributor() {
		final String chooseDtTxt = this.getLocalizer().getString("datepicker.chooseDate", this);
		final String locale = this.getLocale().getLanguage();
		add(new HeaderContributor(new DateHeaderContributor(locale, BtpnConstants.DATE_FORMAT_PATTERN_PICKER,
			chooseDtTxt)));
	}

	protected void initPageComponents() {
		Form<AddHolidayCalender> form = new Form<AddHolidayCalender>("addHolidayCalenderForm",
			new CompoundPropertyModel<AddHolidayCalender>(this));
		form.add(new FeedbackPanel("errorMessages"));

		final DateTextField fromdDate = (DateTextField) DateTextField
			.forDatePattern("holidayBean.fromDateString", BtpnConstants.ID_EXPIRY_DATE_PATTERN).setRequired(true)
			.add(new ErrorIndicator());
		final DateTextField toDate = (DateTextField) DateTextField
			.forDatePattern("holidayBean.toDateString", BtpnConstants.ID_EXPIRY_DATE_PATTERN).setRequired(true)
			.add(new ErrorIndicator());

		form.add(new HolidayCalenderDateValidator(fromdDate, toDate));

		form.add(fromdDate);
		form.add(toDate);

		form.add(new TextField<String>("holidayBean.description").setRequired(true).add(new ErrorIndicator()));

		form.add(new Button("submitButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				addHolidayCalendar();
			}
		});

		form.add(new Button("cancelButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(HolidayCalender.class);
			}
		}.setDefaultFormProcessing(false));

		add(form);

	}

	/**
	 * calling addHoliday service from holiday calendar end point
	 */
	private void addHolidayCalendar() {
		try {
			final AddHolidayRequest request = getNewMobiliserRequest(AddHolidayRequest.class);
			AddHolidayResponse response = getHolidayCalendarClient().addHoliday(populateAddHolidayRequest(request));
			if (evaluateBankPortalMobiliserResponse(response)) {
				holidayBean.setAddHolidaysuccess(true);
				setResponsePage(new HolidayCalender(holidayBean));
			} else {
				handleSpecificErrorMessage(response.getStatus().getCode());
			}
		} catch (Exception ex) {
			LOG.error("#An error occurred while calling addHoliday service.", ex);
			error(getLocalizer().getString("error.exception", this));
		}
	}

	private AddHolidayRequest populateAddHolidayRequest(AddHolidayRequest request) {
		HolidayCalendar calendar = new HolidayCalendar();
		calendar.setFromDate(PortalUtils.getSaveXMLGregorianCalendarFromDate(holidayBean.getFromDateString(), null));
		calendar.setToDate(PortalUtils.getSaveXMLGregorianCalendarToDate(holidayBean.getToDateString(), null));
		calendar.setDescription(holidayBean.getDescription());
		request.setHoliday(calendar);
		long makerId = getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId();
		request.setMakerId(makerId);
		return request;
	}

	/**
	 * This method handles the specific error message.
	 */
	private void handleSpecificErrorMessage(final int errorCode) {
		final String messageKey = "error." + errorCode;
		String message = getLocalizer().getString(messageKey, this);
		if (messageKey.equals(message)) {
			message = getLocalizer().getString("error.submit.holiday", this);
		}
		error(message);
	}
}
