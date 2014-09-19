package com.sybase365.mobiliser.web.cst.pages.customercare;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.validation.validator.PatternValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.common.components.KeyValue;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PhoneNumber;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class ChangeMsisdnPage extends CustomerCareMenuGroup {
    private static final Logger LOG = LoggerFactory
	    .getLogger(ChangeMsisdnPage.class);

    private CustomerBean customer;
    private String msisdn;

    public ChangeMsisdnPage() {
	super();
    }

    public ChangeMsisdnPage(CustomerBean customer) {
	super();
	this.customer = customer;
	this.msisdn = getCustomer().getMsisdn();
	initPageComponents();
    }

    protected void initPageComponents() {
	add(new FeedbackPanel("errorMessages"));
	final Form<?> changeMsisdnForm = new Form("changeMsisdnForm",
		new CompoundPropertyModel<ChangeMsisdnPage>(this));
	final RequiredTextField<String> msisdn = new RequiredTextField<String>(
		"msisdn");
	msisdn.add(Constants.mediumStringValidator).add(
		Constants.mediumSimpleAttributeModifier);
	changeMsisdnForm.add(
		msisdn.setRequired(true).add(
			new PatternValidator(Constants.REGEX_MSISDN))).add(
		new ErrorIndicator());

	final WebMarkupContainer changeMsisdnButtonDiv = new WebMarkupContainer(
		"changeMsisdnButtonDiv");
	changeMsisdnButtonDiv.add(new Button("sendMsisdnOtp") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		try {
		    PhoneNumber pn = new PhoneNumber(getMsisdn(),
			    getConfiguration().getCountryCode());
		    if ((pn.getInternationalFormat()).equals(getCustomer()
			    .getMsisdn())) {
			error(getLocalizer().getString(
				"current.new.msidn.identical", this));
			return;
		    }
		    if (!uniqueIdentificationCheck(getMsisdn(),
			    Constants.IDENT_TYPE_MSISDN, getCustomer().getId()))
			return;
		    String generatedOtp = generateOTP();
		    if (generatedOtp == null)
			return;
		    List<KeyValue<String, String>> paramsList = new ArrayList<KeyValue<String, String>>();
		    paramsList.add(new KeyValue<String, String>("otp",
			    generatedOtp));
		    // String sentOtp = sendOTP(getMsisdn());
		    if (!sendOTP(getMsisdn(), getConfiguration()
			    .getChangeMsisdnTemplate(), "sms", getCustomer()
			    .getId(), paramsList)) {
			return;
		    } else {
			info(getLocalizer().getString("otp.send.success", this));
			getMobiliserWebSession().setCustomerOtp(generatedOtp);
			getMobiliserWebSession().setCustomerOtpCount(1);
			setResponsePage(new ConfirmOtpPage(getCustomer(),
				getMsisdn()));
		    }

		} catch (Exception e) {
		    LOG.error("#An error occurred while changing the msisdn", e);
		    error(getLocalizer().getString("change.msisdn.error", this));
		    return;
		}

	    };
	});
	if (PortalUtils.exists(getMobiliserWebSession().getCustomerOtp())) {
	    msisdn.add(new SimpleAttributeModifier("readonly", "readonly"));
	    changeMsisdnButtonDiv.setVisible(false);

	}

	changeMsisdnForm.add(changeMsisdnButtonDiv);
	add(changeMsisdnForm);

    }

    public String getMsisdn() {
	return msisdn;
    }

    public CustomerBean getCustomer() {
	return customer;
    }

    public void setMsisdn(String msisdn) {
	this.msisdn = msisdn;
    }

}
