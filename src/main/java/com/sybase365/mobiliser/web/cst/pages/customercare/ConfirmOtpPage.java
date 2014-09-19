package com.sybase365.mobiliser.web.cst.pages.customercare;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.customer.CreateIdentificationRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.CreateIdentificationResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Identification;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.common.components.KeyValue;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PhoneNumber;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class ConfirmOtpPage extends CustomerCareMenuGroup {
    private static final Logger LOG = LoggerFactory
	    .getLogger(ConfirmOtpPage.class);

    private CustomerBean customer;
    private String otp;
    private String msisdn;
    private int otpEnteredCount = 0;

    public ConfirmOtpPage() {
	super();
    }

    public ConfirmOtpPage(CustomerBean customer, String msisdn) {
	super();
	this.customer = customer;
	this.msisdn = msisdn;
	this.initPageComponents();
    }

    protected void initPageComponents() {
	add(new FeedbackPanel("errorMessages"));
	final Form<?> confirmOtpForm = new Form("confirmOtpForm",
		new CompoundPropertyModel<ConfirmOtpPage>(this));
	final RequiredTextField<String> otp = new RequiredTextField<String>(
		"otp");
	otp.add(Constants.mediumStringValidator).add(
		Constants.mediumSimpleAttributeModifier);
	confirmOtpForm.add(otp.setRequired(true)).add(new ErrorIndicator());
	final WebMarkupContainer otpButtons = new WebMarkupContainer(
		"otpButtons");

	otpButtons.add(new Button("confirm") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {

		if (getMobiliserWebSession().isCustomerOtpLimitHit()) {
		    error(getLocalizer().getString("otp.limit.hit.error", this));
		    otpButtons.setVisible(false);
		    return;
		} else if (!getOtp().equals(
			getMobiliserWebSession().getCustomerOtp())) {
		    error(getLocalizer().getString("otp.mismatch.error", this));
		    if (otpEnteredCount++ > Constants.MAX_ALLOWED_OTP_COUNT) {
			getMobiliserWebSession().setCustomerOtpLimitHit(true);
		    }
		    return;
		} else {
		    try {
			Identification existingMsisdn = getIdentificationByCustomer(
				getCustomer().getId(),
				Constants.IDENT_TYPE_MSISDN);
			if (PortalUtils.exists(existingMsisdn)
				&& PortalUtils.exists(existingMsisdn
					.getIdentification())) {
			    LOG.debug("An active identification["
				    + existingMsisdn.getId() + ":"
				    + existingMsisdn.getIdentification()
				    + "] exist.Updating the same");
			    PhoneNumber pn = new PhoneNumber(getMsisdn(),
				    getConfiguration().getCountryCode());

			    if (!updateIdentificationByCustomer(getCustomer()
				    .getId(), Constants.IDENT_TYPE_MSISDN,
				    existingMsisdn.getId(), pn
					    .getInternationalFormat(),
				    getCustomer().getNetworkProvider()))
				return;
			    getCustomer().setMsisdn(
				    new PhoneNumber(getMsisdn())
					    .getInternationalFormat());
			} else {
			    LOG.debug("# No active identification found.Creating new one.");
			    if (!createIdentification())
				return;
			}
			info(getLocalizer().getString("msisdn.changed.success",
				this));
			getCustomer().setMsisdn(msisdn);
			setResponsePage(new StandingDataPage(getCustomer()));
		    } catch (Exception e) {
			LOG.error(
				"#An error occurred while updating the msisdn for customer["
					+ getCustomer().getId(), e);
			getLocalizer().getString("customer.update.error", this);
		    }
		}
	    };
	});

	otpButtons.add(new Button("resend") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		try {
		    if (getMobiliserWebSession().getCustomerOtpCount() > Constants.MAX_ALLOWED_OTP_RESEND_COUNT) {
			getMobiliserWebSession().setCustomerOtpLimitHit(true);
			otpButtons.setVisible(false);
		    }
		    if (getMobiliserWebSession().isCustomerOtpLimitHit()) {
			error(getLocalizer().getString("otp.send.limit.hit",
				this));
			return;
		    } else {
			String generatedOtp = generateOTP();
			if (generatedOtp == null)
			    return;
			List<KeyValue<String, String>> paramsList = new ArrayList<KeyValue<String, String>>();
			paramsList.add(new KeyValue<String, String>("otp",
				generatedOtp));
			if (!sendOTP(getMsisdn(), getConfiguration()
				.getChangeMsisdnTemplate(), "sms",
				getCustomer().getId(), paramsList)) {
			    return;
			} else {
			    info(getLocalizer().getString("otp.resend.success",
				    this));
			    getMobiliserWebSession().setCustomerOtp(
				    generatedOtp);
			    getMobiliserWebSession().setCustomerOtpCount(
				    getMobiliserWebSession()
					    .getCustomerOtpCount() + 1);

			}
		    }
		    if (PortalUtils.exists(getMobiliserWebSession()
			    .getCustomerOtp())) {
			if (!getMobiliserWebSession().isCustomerOtpLimitHit()) {
			    otpButtons.setVisible(true);
			} else {
			    otpButtons.setVisible(false);
			}

		    }
		} catch (Exception e) {
		    LOG.error(
			    "# An error occurred while sending OTP for changing msisdn for customer["
				    + getCustomer().getId() + "[", e);
		    error(getLocalizer().getString("msisdn.change.error", this));
		}
	    };
	}.setDefaultFormProcessing(false));

	confirmOtpForm.add(otpButtons);
	add(confirmOtpForm);
    }

    private boolean createIdentification() {
	try {
	    CreateIdentificationRequest request = getNewMobiliserRequest(CreateIdentificationRequest.class);
	    Identification identification = new Identification();
	    identification.setActive(true);
	    identification.setCustomerId(getCustomer().getId());
	    PhoneNumber pn = new PhoneNumber(getMsisdn(), getConfiguration()
		    .getCountryCode());
	    identification.setIdentification(pn.getInternationalFormat());
	    identification.setType(Constants.IDENT_TYPE_MSISDN);
	    request.setIdentification(identification);
	    CreateIdentificationResponse response = wsIdentClient
		    .createIdentification(request);
	    if (!evaluateMobiliserResponse(response))
		return false;

	} catch (Exception e) {
	    LOG.error("#An error occurred while creating identification["
		    + getMsisdn() + "] for customer[" + getCustomer().getId()
		    + "]");
	    return false;
	}

	return true;
    }

    public int getOtpEnteredCount() {
	return otpEnteredCount;
    }

    public void setOtpEnteredCount(int otpEnteredCount) {
	this.otpEnteredCount = otpEnteredCount;
    }

    public CustomerBean getCustomer() {
	return customer;
    }

    public String getOtp() {
	return otp;
    }

    public String getMsisdn() {
	return msisdn;
    }

    public void setMsisdn(String msisdn) {
	this.msisdn = msisdn;
    }

}
