/**
 * 
 */
package com.sybase365.mobiliser.web.common.panels.alerts;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.PatternValidator;

import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerOtherIdentification;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.application.clients.AlertsClientLogic;
import com.sybase365.mobiliser.web.common.components.LocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.util.Constants;

/**
 * @author Sushil.agrawala
 * 
 */
public class OtherContactPointPanel extends Panel {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(OtherContactPointPanel.class);

    private Class backPage;
    private AlertsClientLogic clientLogic;
    private Long customerId;
    private String phoneText;
    private String faxText;
    private CustomerOtherIdentification customerOtherIdentification;
    private String action;
    private LocalizableLookupDropDownChoice<Integer> contactTypeList;

    private static final String WICKET_ID_otherContactPointDiv = "otherContactPointDiv";
    private static final String WICKET_ID_otherContactPointForm = "otherContactPointForm";
    private static final String WICKET_ID_otherContactPointCancel = "otherContactPointCancel";
    private static final String WICKET_ID_otherContactPointSave = "otherContactPointSave";
    private static final String WICKET_ID_errorMessages = "errorMessages";

    public OtherContactPointPanel(String id) {
	super(id);
    }

    public OtherContactPointPanel(String id, AlertsClientLogic clientLogic,
	    CustomerOtherIdentification customerOtherIdentification,
	    Long customerId, String action, Class backPage) {

	super(id);

	this.backPage = backPage;
	this.customerOtherIdentification = customerOtherIdentification;
	this.clientLogic = clientLogic;
	this.customerId = customerId;
	this.action = action;

	constructPanel();
    }

    private void constructPanel() {
	OtherContactPointDiv();
    }

    @SuppressWarnings("unchecked")
    private WebMarkupContainer OtherContactPointDiv() {
	WebMarkupContainer addContactDiv = new WebMarkupContainer(
		WICKET_ID_otherContactPointDiv);
	Form<OtherContactPointPanel> form = new Form(
		WICKET_ID_otherContactPointForm,
		new CompoundPropertyModel<OtherContactPointPanel>(this));

	contactTypeList = (LocalizableLookupDropDownChoice) new LocalizableLookupDropDownChoice<String>(
		"customerOtherIdentification.type", String.class, "contTypes",
		this, false, true).setRequired(true).add(new ErrorIndicator());
	form.add(contactTypeList);

	form.add(new TextField<String>("customerOtherIdentification.nickname")
		.setRequired(true).add(new ErrorIndicator()));

	final RequiredTextField<String> emailTextField = new RequiredTextField<String>(
		"customerOtherIdentification.identification");

	emailTextField.setOutputMarkupId(true).setVisible(false);
	emailTextField.setOutputMarkupPlaceholderTag(true);
	emailTextField.setRequired(true)
		.add(EmailAddressValidator.getInstance())
		.add(new ErrorIndicator());
	form.add(emailTextField);

	final TextField<String> phoneTextField = new TextField<String>(
		"phoneText");
	phoneTextField.setOutputMarkupId(true);
	phoneTextField.setOutputMarkupPlaceholderTag(true);
	phoneTextField.setRequired(true)
		.add(new PatternValidator(Constants.REGEX_PHONE_NUMBER))
		.add(new ErrorIndicator());
	form.add(phoneTextField);

	final TextField<String> faxTextField = new TextField<String>("faxText");
	faxTextField.setOutputMarkupId(true).setVisible(false);
	faxTextField.setOutputMarkupPlaceholderTag(true);
	faxTextField.setRequired(true)
		.add(new PatternValidator(Constants.REGEX_PHONE_NUMBER))
		.add(new ErrorIndicator());
	form.add(faxTextField);

	if (customerOtherIdentification != null
		&& customerOtherIdentification.getType() == Constants.IDENT_TYPE_EMAIL) {
	    emailTextField.setVisible(true);
	    faxTextField.setVisible(false);
	    phoneTextField.setVisible(false);
	} else if (customerOtherIdentification != null
		&& customerOtherIdentification.getType() == Constants.IDENT_TYPE_FAX) {
	    setFaxText(customerOtherIdentification.getIdentification());
	    faxTextField.setVisible(true);
	    phoneTextField.setVisible(false);
	    emailTextField.setVisible(false);
	} else if (customerOtherIdentification != null
		&& customerOtherIdentification.getType() == Constants.IDENT_TYPE_MSISDN) {
	    setPhoneText(customerOtherIdentification.getIdentification());
	    phoneTextField.setVisible(true);
	    faxTextField.setVisible(false);
	    emailTextField.setVisible(false);
	}

	contactTypeList.add(new AjaxFormComponentUpdatingBehavior("onchange") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onUpdate(AjaxRequestTarget target) {
		if (contactTypeList.getModelObject().equals(
			Constants.IDENT_TYPE_EMAIL)) {
		    LOG.trace("inside email");
		    emailTextField.setVisible(true);
		    phoneTextField.clearInput();
		    faxTextField.clearInput();
		    setPhoneText(null);
		    setFaxText(null);
		    phoneTextField.setVisible(false);
		    faxTextField.setVisible(false);
		    target.addComponent(emailTextField);
		    target.addComponent(phoneTextField);
		    target.addComponent(faxTextField);
		} else if (contactTypeList.getModelObject().equals(
			Constants.IDENT_TYPE_FAX)) {
		    LOG.trace("inside fax");
		    emailTextField.setVisible(false);
		    phoneTextField.setVisible(false);
		    setPhoneText(null);
		    faxTextField.setVisible(true);
		    emailTextField.clearInput();
		    phoneTextField.clearInput();
		    target.addComponent(emailTextField);
		    target.addComponent(phoneTextField);
		    target.addComponent(faxTextField);
		} else if (contactTypeList.getModelObject().equals(
			Constants.IDENT_TYPE_MSISDN)) {
		    LOG.trace("inside msisdn");
		    emailTextField.setVisible(false);
		    phoneTextField.setVisible(true);
		    emailTextField.clearInput();
		    faxTextField.clearInput();
		    faxTextField.setVisible(false);
		    setFaxText(null);
		    target.addComponent(emailTextField);
		    target.addComponent(phoneTextField);
		    target.addComponent(faxTextField);
		}
	    }
	});

	form.add(new FeedbackPanel(WICKET_ID_errorMessages));

	form.add(new Button(WICKET_ID_otherContactPointCancel) {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		setResponsePage(backPage);

	    };
	}.setDefaultFormProcessing(false));
	form.add(new Button(WICKET_ID_otherContactPointSave) {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		addOrEditOtherContactPointConfirm(getCustomerOtherIdentification());
	    };
	});
	addContactDiv.add(form);
	add(addContactDiv);
	return addContactDiv;

    }

    protected int addOrEditOtherContactPointConfirm(
	    CustomerOtherIdentification customerOtherIdentification) {

	customerOtherIdentification.setCustomerId(customerId);
	if (getPhoneText() != null) {
	    customerOtherIdentification.setIdentification(getPhoneText());
	}
	if (getFaxText() != null) {
	    customerOtherIdentification.setIdentification(getFaxText());
	}
	int status = 0;
	if (Constants.OPERATION_EDIT.equalsIgnoreCase(action)) {
	    status = clientLogic
		    .updateCustomerOtherIdentification(customerOtherIdentification);

	    LOG.debug("Inside edit contact point .Status code :{} ", status);

	    if (status == 0) {
		getSession().info(
			getLocalizer().getString(
				"editOtherContactPoint.success", this));
		setResponsePage(backPage);
	    }
	} else {
	    status = clientLogic
		    .createCustomerOtherIdentification(customerOtherIdentification);
	    LOG.debug("Inside Add contact point .Status code :{} ", status);

	    if (status == 0) {
		getSession().info(
			getLocalizer().getString(
				"addOtherContactPoint.success", this));
		setResponsePage(backPage);
	    }
	}
	if (status == 202) {
	    error(getLocalizer()
		    .getString("addContactPoint.alreadyExist", this));
	}
	return status;
    }

    public void setCustomerOtherIdentification(
	    CustomerOtherIdentification customerOtherIdentification) {
	this.customerOtherIdentification = customerOtherIdentification;
    }

    public CustomerOtherIdentification getCustomerOtherIdentification() {
	return customerOtherIdentification;
    }

    public void setPhoneText(String phoneText) {
	this.phoneText = phoneText;
    }

    public String getPhoneText() {
	return phoneText;
    }

    public void setFaxText(String faxText) {
	this.faxText = faxText;
    }

    public String getFaxText() {
	return faxText;
    }

}
