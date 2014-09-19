package com.sybase365.mobiliser.web.btpn.common.components;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.util.WildcardListModel;

import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;

/**
 * This class allows us display the roles that logged in user can register. These roles are populated in the dropddown
 * from a mobiliser service based on the logged in user.
 * 
 * @author Vikram Gunda
 */
public class MonthDropdownChoice extends DropDownChoice<CodeValue> {

	private static final long serialVersionUID = 1L;

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MonthDropdownChoice.class);

	private boolean sortKeys;

	private boolean sortAsc;

	private int startMonth;

	private int startYear;

	/**
	 * Constructor for this DropDownChoice.
	 * 
	 * @param id id for the DropDownChoice
	 */
	public MonthDropdownChoice(String id, boolean sortKeys, boolean sortAsc, int startMonth, int startYear) {
		super(id);
		this.sortKeys = sortKeys;
		this.sortAsc = sortAsc;
		this.startMonth = startMonth;
		this.startYear = startYear;
		// Add Default Choice Render
		setChoiceRenderer(new ChoiceRenderer<CodeValue>(BtpnConstants.DISPLAY_EXPRESSION, BtpnConstants.ID_EXPRESSION));

	}

	/**
	 * Override the before render method. This method fethces the values to be populated in this dropdown before
	 * rendering the dropdown and sets the choices list.
	 */
	@Override
	protected void onBeforeRender() {

		// this is the place where we can determine the order of the choices
		getChoices().clear();
		// get the choices for our drop down list
		try {
			setChoices(new WildcardListModel<CodeValue>(getChoiceList()));
		} catch (final Exception e) {
			// not much we can do here but logg the exception
			LOG.warn("MonthDropdownChoice:onBeforeRender() ===> Could not retrieve drop down choices", e);
		}

		super.onBeforeRender();
	}

	/**
	 * This is used to fetch the choices for dropdown
	 */
	protected List<CodeValue> getChoiceList() throws Exception {
		final List<CodeValue> list = new ArrayList<CodeValue>();
		// current date
		final GregorianCalendar systemRefDate = new GregorianCalendar();
		final GregorianCalendar greg = new GregorianCalendar();

		final String currMonth = String.valueOf(greg.get(Calendar.MONTH) + 1);
		final String currYear = String.valueOf(greg.get(Calendar.YEAR));
		final String currYearPropCode = currMonth + "-" + currYear;
		final String currYearPropValue = getLocalizer().getString("calendar.months." + currMonth, this) + "-"
				+ currYear;
		CodeValue sb = new CodeValue(currYearPropCode, currYearPropValue);
		list.add(sb);

		// Previous dates
		while (!(systemRefDate.get(Calendar.MONTH) + 1 == startMonth)
				|| !(systemRefDate.get(Calendar.YEAR) == startYear)) {
			systemRefDate.add(Calendar.MONTH, -1);
			greg.add(Calendar.MONTH, -1);
			final String month = String.valueOf(greg.get(Calendar.MONTH) + 1);
			final String year = String.valueOf(greg.get(Calendar.YEAR));
			final String propCode = month + "-" + year;
			final String propValue = getLocalizer().getString("calendar.months." + month, this) + "-" + year;
			sb = new CodeValue(propCode, propValue);
			list.add(sb);
		}
		return list;
	}
}
