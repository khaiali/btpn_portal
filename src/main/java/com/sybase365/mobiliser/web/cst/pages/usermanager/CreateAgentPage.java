package com.sybase365.mobiliser.web.cst.pages.usermanager;

import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.validation.validator.PatternValidator;

import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.common.components.LocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class CreateAgentPage extends BaseUserManagerPage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(CreateAgentPage.class);
    private CustomerBean customer;
    private String confirmPassword;

    @SuppressWarnings("unchecked")
    @Override
    protected void initOwnPageComponents() {

	super.initOwnPageComponents();
	Form<?> form = new Form("createAgentForm",
		new CompoundPropertyModel<CreateAgentPage>(this));
	form.add(new FeedbackPanel("errorMessages"));

	form.add(new RequiredTextField<String>("customer.address.firstName")
		.add(new PatternValidator(Constants.REGEX_FIRSTNAME))
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	form.add(new RequiredTextField<String>("customer.address.lastName")
		.add(new PatternValidator(Constants.REGEX_FIRSTNAME))
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	form.add(new TextField<String>("customer.address.company")
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	form.add(new TextField<String>("customer.address.position")
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	form.add(new RequiredTextField<String>("customer.address.email")
		.add(new PatternValidator(Constants.REGEX_EMAIL))
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	form.add(new LocalizableLookupDropDownChoice<String>(
		"customer.address.kvCountry", String.class, "countries", this,
		false, true).setNullValid(false).setRequired(true)
		.add(new ErrorIndicator()));

	form.add(new LocalizableLookupDropDownChoice<String>(
		"customer.language", String.class, "languages", this, false,
		true).setNullValid(false).setRequired(true)
		.add(new ErrorIndicator()));

	form.add(new LocalizableLookupDropDownChoice<String>(
		"customer.timeZone", String.class, "timezones", this, false,
		true).setNullValid(false).add(new ErrorIndicator()));

	form.add(new RequiredTextField<String>("customer.userName")
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	PasswordTextField password = new PasswordTextField("customer.password");
	PasswordTextField confirmPassword = new PasswordTextField(
		"confirmPassword");
	form.add(password.setRequired(true).add(Constants.largeStringValidator)
		.add(new SimpleAttributeModifier("autocomplete", "off"))
		.add(Constants.largeSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	form.add(confirmPassword.setRequired(true)
		.add(Constants.largeStringValidator)
		.add(new SimpleAttributeModifier("autocomplete", "off"))
		.add(Constants.largeSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	form.add(new EqualPasswordInputValidator(password, confirmPassword));

	form.add(new Button("createAgent") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		createAgent();
	    }

	    ;
	});
	add(form);
    }

    private void createAgent() {

	LOG.debug("# CreateAgentPage.createAgent()");
	customer.setBlackListReason(Constants.DEFAULT_MERCHANT_BLACKLSTREASON);
	customer.setRiskCategoryId(getConfiguration()
		.getDefaultRiskCatForNewCustomer());
	customer.setCustomerTypeId(getConfiguration()
		.getDefaultTypeIdForInternalCustomer());
	try {
	    // if (duplicateUsernameCheck(customer)) {
	    // return;
	    // }
	    if (!uniqueIdentificationCheck(customer.getUserName(),
		    Constants.IDENT_TYPE_USERNAME, customer.getId()))
		return;
	    if (!valedatePasswordStrength(customer.getPassword(),
		    Constants.CREDENTIAL_TYPE_PASSWORD)) {
		return;
	    }
	    setCreateStatus(false);
	    customer = createFullCustomer(customer, null);

	    if (PortalUtils.exists(customer) && customer.isPendingApproval()) {
		info(getLocalizer().getString("pendingApproval.msg", this));
		customer = null;
		return;
	    }

	    if (isCreateStatus()) {
		getMobiliserWebSession().setCustomer(customer);
		info(getLocalizer().getString("MESSAGE.CREATE_AGENT_SUCCESS",
			this));
		customer.setDisplayName(createDisplayName(customer.getAddress()
			.getFirstName(), customer.getAddress().getLastName()));
		getMobiliserWebSession().setCustomer(customer);
		setResponsePage(new EditAgentPage(customer));
	    }
	} catch (Exception e) {
	    setCreateStatus(false);
	    error(getLocalizer().getString("ERROR.CREATE_AGENT_FAILURE", this));
	    LOG.error("# An error occurred while creating an agent", e);

	} finally {
	    if (!isCreateStatus()) {
		try {
		    if (PortalUtils.exists(customer)) {
			if (PortalUtils.exists(customer.getId()))
			    deleteCustomer(customer.getId());
			customer.setId(null);
			customer.getAddress().setId(null);
		    }
		} catch (Exception e) {
		    LOG.error("# An error occurred whilte inactivating agent",
			    e);
		}
	    }

	}
    }

    public CustomerBean getCustomer() {
	return customer;
    }

    public void setCustomer(CustomerBean customer) {
	this.customer = customer;
    }

    public String getConfirmPassword() {
	return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
	this.confirmPassword = confirmPassword;
    }
}
