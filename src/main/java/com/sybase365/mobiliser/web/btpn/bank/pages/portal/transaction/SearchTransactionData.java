package com.sybase365.mobiliser.web.btpn.bank.pages.portal.transaction;

import java.util.Date;

import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.validation.validator.DateValidator;

import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.bank.beans.CustomerRegistrationBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.behaviours.DateHeaderContributor;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;

public class SearchTransactionData extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;


	public static CustomerRegistrationBean customerRegistrationBean;
	private Date fromDate;
	private Date toDate;

/*	*//**
	 * Default Constructor for this page.
	 */
	public SearchTransactionData() {
		super();
		customerRegistrationBean = this.getMobiliserWebSession().getCustomerRegistrationBean();
		addDateHeaderContributor();
		initPageComponents();
	}

	/**
	 * Initialize the Components.
	 */
	protected void initPageComponents() {
		final Form<SearchTransactionData> form = new Form<SearchTransactionData>(
				"txnSearchForm",
				new CompoundPropertyModel<SearchTransactionData>(this));
		DateTextField fromdDate = (DateTextField) DateTextField
				.forDatePattern("fromDate",
						BtpnConstants.ID_EXPIRY_DATE_PATTERN)
				.add(DateValidator.minimum(new Date(),
						BtpnConstants.ID_EXPIRY_DATE_PATTERN))
				.setRequired(true).add(new ErrorIndicator());
		DateTextField toDate = (DateTextField) DateTextField
				.forDatePattern("toDate", BtpnConstants.ID_EXPIRY_DATE_PATTERN)
				.add(DateValidator.minimum(new Date(),
						BtpnConstants.ID_EXPIRY_DATE_PATTERN))
				.setRequired(true).add(new ErrorIndicator());
		Button searchBtn=new Button("search"){

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {}
		
			
		};
		form.add(fromdDate);
		form.add(toDate);
		form.add(searchBtn);
		add(form);

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


	/**
	 * @return the fromDate
	 */
	public Date getFromDate() {
		return fromDate;
	}

	/**
	 * @param fromDate the fromDate to set
	 */
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	/**
	 * @return the toDate
	 */
	public Date getToDate() {
		return toDate;
	}

	/**
	 * @param toDate the toDate to set
	 */
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

}
