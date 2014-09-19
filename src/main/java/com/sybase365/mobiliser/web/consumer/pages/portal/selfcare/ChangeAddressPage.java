package com.sybase365.mobiliser.web.consumer.pages.portal.selfcare;

import java.util.Date;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.PatternValidator;

import com.sybase365.mobiliser.money.contract.v5_0.customer.GetAddressByCustomerRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetAddressByCustomerResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.UpdateAddressRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.UpdateAddressResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Address;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Identification;
import com.sybase365.mobiliser.util.tools.formatutils.FormatUtils;
import com.sybase365.mobiliser.util.tools.wicketutils.BaseWebSession;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.beans.AddressBean;
import com.sybase365.mobiliser.web.common.components.LocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.Converter;

@AuthorizeInstantiation(Constants.PRIV_CHANGE_ADDRESS)
public class ChangeAddressPage extends BaseSelfCarePage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(ChangeAddressPage.class);

    private AddressBean customerAddress;
    private Identification ident;

    @SuppressWarnings("unchecked")
    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();
	getAddress();
	Form<?> form = new Form("changeAddressForm",
		new CompoundPropertyModel<ChangeAddressPage>(this));

	form.add(new Label("customerAddress.firstName"));

	form.add(new Label("customerAddress.lastName"));
	form.add(new Label("ident.identification"));

	form.add(
		new TextField<String>("customerAddress.houseNo").add(
			Constants.mediumStringValidator).add(
			Constants.mediumSimpleAttributeModifier)).add(
		new ErrorIndicator());

	form.add(new TextField<String>("customerAddress.street1")
		.add(new PatternValidator(Constants.REGEX_STREET1))
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	form.add(new TextField<String>("customerAddress.street2")
		.add(new PatternValidator(Constants.REGEX_STREET2))
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	form.add(new TextField<String>("customerAddress.city")
		.add(new PatternValidator(Constants.REGEX_CITY))
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	form.add(new TextField<String>("customerAddress.state")
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	form.add(new TextField<String>("customerAddress.zip")
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	form.add((LocalizableLookupDropDownChoice<String>) new LocalizableLookupDropDownChoice<String>(
		"customerAddress.kvCountry", String.class, "countries", this,
		false, true).setNullValid(false).add(new ErrorIndicator()));

	form.add(new TextField<String>("customerAddress.email")
		.setRequired(true).add(EmailAddressValidator.getInstance())
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	form.add(new Button("add") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleSubmit();
	    };
	});
	form.add(new FeedbackPanel("errorMessages"));
	add(form);

    }

    private void handleSubmit() {

	try {

	    com.sybase365.mobiliser.util.tools.wicketutils.security.Customer loggedInCustomer = ((BaseWebSession) getWebSession())
		    .getLoggedInCustomer();

	    GetAddressByCustomerRequest addrByCustReq = getNewMobiliserRequest(GetAddressByCustomerRequest.class);
	    addrByCustReq.setCustomerId(loggedInCustomer.getCustomerId());
	    GetAddressByCustomerResponse response = wsAddressClient
		    .getAddressByCustomer(addrByCustReq);
	    if (!evaluateMobiliserResponse(response)) {
		LOG.warn("# An error occurred while fetching customer's address");
	    }

	    Address address = response.getAddress();

	    address.setHouseNumber(customerAddress.getHouseNo());
	    address.setStreet1(customerAddress.getStreet1());
	    address.setStreet2(customerAddress.getStreet2());
	    address.setState(customerAddress.getState());
	    address.setCity(customerAddress.getCity());
	    address.setZip(customerAddress.getZip());
	    address.setCountry(customerAddress.getKvCountry());
	    address.setEmail(customerAddress.getEmail());
	    address.setCustomerId(Long.valueOf(loggedInCustomer.getCustomerId()));
	    address.setAddressSince(FormatUtils
		    .getSaveXMLGregorianCalendar(new Date()));

	    UpdateAddressRequest addressRequest = getNewMobiliserRequest(UpdateAddressRequest.class);

	    addressRequest.setAddress(address);
	    UpdateAddressResponse addressResponse = wsAddressClient
		    .updateAddress(addressRequest);
	    if (!evaluateMobiliserResponse(addressResponse)) {
		LOG.warn("# An error occurred while updating customer's address");
	    } else {
		getSession().info(
			getLocalizer().getString(
				"customer.update.address.success", this));

		setResponsePage(ConsumerHomePage.class);
	    }

	} catch (Exception e) {
	    LOG.error("# An error occurred while updating customer's address",
		    e);
	    error(getLocalizer().getString("customer.update.address.error",
		    this));
	}

    }

    public AddressBean getCustomerAddress() {
	return this.customerAddress;
    }

    private void getAddress() {
	try {
	    GetAddressByCustomerRequest req = new GetAddressByCustomerRequest();
	    req.setCustomerId(getMobiliserWebSession().getLoggedInCustomer()
		    .getCustomerId());
	    GetAddressByCustomerResponse res = wsAddressClient
		    .getAddressByCustomer(req);

	    this.customerAddress = Converter.getInstance()
		    .convertFromWSaddressToAddress(res.getAddress());

	    ident = getCustomerIdentification(getMobiliserWebSession()
		    .getLoggedInCustomer().getCustomerId(),
		    Constants.IDENT_TYPE_MSISDN);
	    setIdent(ident);
	} catch (Exception e) {
	    LOG.error("# Error in getting Customer address", e);
	}

    }

    public Identification getIdent() {
	return ident;
    }

    public void setIdent(Identification ident) {
	this.ident = ident;
    }
}
