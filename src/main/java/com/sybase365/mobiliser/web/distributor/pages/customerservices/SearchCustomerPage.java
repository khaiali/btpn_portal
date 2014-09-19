package com.sybase365.mobiliser.web.distributor.pages.customerservices;

import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.validation.validator.PatternValidator;

import com.sybase365.mobiliser.money.contract.v5_0.customer.GetAddressByCustomerRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetAddressByCustomerResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetCustomerByIdentificationRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetCustomerByIdentificationResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetIdentitiesRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetIdentitiesResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Address;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Identity;
import com.sybase365.mobiliser.util.tools.formatutils.FormatUtils;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.beans.AddressBean;
import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.Converter;
import com.sybase365.mobiliser.web.util.PhoneNumber;
import com.sybase365.mobiliser.web.util.PortalUtils;

@AuthorizeInstantiation(Constants.PRIV_WALLET_SERVICES)
public class SearchCustomerPage extends BaseCustomerServicesPage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(SearchCustomerPage.class);
    private String msisdn;

    public SearchCustomerPage() {
	super();
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    public SearchCustomerPage(final PageParameters parameters) {
	super(parameters);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected void initOwnPageComponents() {

	super.initOwnPageComponents();

	final Form<?> form = new Form("searchCustomerForm",
		new CompoundPropertyModel<SearchCustomerPage>(this));
	form.add(new RequiredTextField<String>("msisdn").setRequired(true)
		.add(Constants.mediumStringValidator)
		.add(new PatternValidator(Constants.REGEX_MSISDN))
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	form.add(new FeedbackPanel("errorMessages"));
	form.add(new Button("searchCustomer") {
	    private static final long serialVersionUID = 1L;

	    public void onSubmit() {
		// make sure the MSISDN is in international format before
		// proceeding
		try {
		    PhoneNumber pn = new PhoneNumber(getMsisdn(),
			    getConfiguration().getCountryCode());
		    setMsisdn(pn.getInternationalFormat());
		    // check if the MSISDN is already registered
		    GetCustomerByIdentificationRequest req = getNewMobiliserRequest(GetCustomerByIdentificationRequest.class);
		    req.setIdentificationType(Constants.IDENT_TYPE_MSISDN);
		    req.setIdentification(getMsisdn());
		    GetCustomerByIdentificationResponse resp = wsCustomerClient
			    .getCustomerByIdentification(req);
		    if (!evaluateMobiliserResponse(resp))
			return;
		    if (resp.getCustomer() != null
			    && resp.getCustomer().getId() != null) {
			CustomerBean customer = Converter
				.getInstance()
				.getCustomerBeanFromCustomer(resp.getCustomer());
			customer.setMsisdn(getMsisdn());
			GetAddressByCustomerRequest addressReq = getNewMobiliserRequest(GetAddressByCustomerRequest.class);
			addressReq.setCustomerId(customer.getId());
			addressReq
				.setAddressType(Constants.ADDRESS_TYPE_POSTAL_DELIVERY);
			GetAddressByCustomerResponse addressResp = wsAddressClient
				.getAddressByCustomer(addressReq);
			if (evaluateMobiliserResponse(addressResp)) {
			    Address custAddress = addressResp.getAddress();
			    AddressBean address = Converter.getInstance()
				    .getAddressBeanFromAddress(custAddress);
			    customer.setAddress(address);
			}

			GetIdentitiesRequest custIdentityReq = getNewMobiliserRequest(GetIdentitiesRequest.class);
			custIdentityReq.setCustomerId(customer.getId());
			GetIdentitiesResponse custIdentityRes = wsIdentityClient
				.getIdentitiesByCustomer(custIdentityReq);
			if (evaluateMobiliserResponse(custIdentityRes)) {
			    List<Identity> identityList = custIdentityRes
				    .getIdentities();
			    if (PortalUtils.exists(identityList)) {
				Identity identity = identityList.get(0);
				customer.setKvIdentityType(identity
					.getIdentityType());
				customer.setIssuer(identity.getIssuer());
				customer.setIdentityId(String.valueOf(identity
					.getId()));
				customer.setIdentityValue(identity
					.getIdentity());
				if (identity.getDateExpires() != null)
				    customer.setExpirationDate(FormatUtils
					    .getSaveDate(identity
						    .getDateExpires()));
			    }
			}

			getMobiliserWebSession().setCustomer(customer);
			setResponsePage(CustomerDetailsPage.class);
			// go to customer details page
		    } else {
			error(getLocalizer().getString("customer.not.found",
				SearchCustomerPage.this));
		    }
		} catch (Exception e) {
		    LOG.error("# Error querying MSISDN status", e);
		    error(getLocalizer()
			    .getString("searchCustomer.error", this));
		}
	    }
	});
	add(form);
    }

    public void setMsisdn(String msisdn) {
	this.msisdn = msisdn;
    }

    public String getMsisdn() {
	return msisdn;
    }

}
