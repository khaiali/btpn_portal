package com.sybase365.mobiliser.web.consumer.pages.portal.selfcare;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.money.contract.v5_0.customer.GetCustomerRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetCustomerResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.UpdateCustomerRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.UpdateCustomerResponse;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.beans.AddressBean;
import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.common.components.LocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_CHANGE_SECQANDA)
public class ChangeSeqQuestionPage extends BaseSelfCarePage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(ChangeSeqQuestionPage.class);

    private CustomerBean customer;

    /*
     * public ChangeSeqQuestionPage() { super();
     * 
     * LOG.info("Created new ChangePasswordPage"); }
     * 
     * public ChangeSeqQuestionPage(final PageParameters parameters) {
     * super(parameters);
     * 
     * LOG.info("Created new ChangePasswordPage"); }
     */

    @SuppressWarnings("unchecked")
    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();
	shoQnA();
	Form<?> form = new Form("changeSeqQuesform",
		new CompoundPropertyModel<ChangeSeqQuestionPage>(this));

	form.add(new Button("add") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleSubmit();

	    };
	});

	form.add(new LocalizableLookupDropDownChoice<String>(
		"customer.address.securityQuestion", String.class,
		Constants.RESOURCE_BUNDLE_SEC_QUESTIONS, this, false, true)
		.setNullValid(false).setRequired(true)
		.add(new ErrorIndicator()));

	form.add(new RequiredTextField<String>("customer.address.SecQuesAns")
		.setRequired(true).add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	form.add(new FeedbackPanel("errorMessages"));
	add(form);
    }

    private void handleSubmit() {

	try {
	    GetCustomerResponse response = loadCustomer();
	    if (!evaluateMobiliserResponse(response)) {
		return;
	    }
	    com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Customer customerWeb = response
		    .getCustomer();
	    customerWeb.setId(Long.valueOf(getMobiliserWebSession()
		    .getLoggedInCustomer().getCustomerId()));
	    customerWeb.setSecurityQuestion(customer.getAddress()
		    .getSecurityQuestion());
	    customerWeb
		    .setSecurityAnswer(customer.getAddress().getSecQuesAns());

	    UpdateCustomerRequest updateCustRequest = getNewMobiliserRequest(UpdateCustomerRequest.class);
	    updateCustRequest.setCustomer(customerWeb);

	    UpdateCustomerResponse customerResponse = wsCustomerClient
		    .updateCustomer(updateCustRequest);
	    if (!evaluateMobiliserResponse(customerResponse))
		return;
	    else {
		getSession().info(
			getLocalizer().getString(
				"msg.changeSecQandA.update.success", this));

		setResponsePage(ConsumerHomePage.class);

	    }

	} catch (Exception e) {
	    LOG.error("# Failed to update customer securtiy question", e);
	    error(getLocalizer().getString("portal.update.secquestion.error",
		    this));
	}

    }

    private GetCustomerResponse loadCustomer() throws Exception {
	com.sybase365.mobiliser.util.tools.wicketutils.security.Customer loggedInCustomer = getMobiliserWebSession()
		.getLoggedInCustomer();
	GetCustomerRequest customerRequest = getNewMobiliserRequest(GetCustomerRequest.class);
	customerRequest.setCustomerId(loggedInCustomer.getCustomerId());
	GetCustomerResponse response = wsCustomerClient
		.getCustomer(customerRequest);
	return response;
    }

    private void shoQnA() {
	try {

	    GetCustomerResponse response = loadCustomer();
	    String secQn = response.getCustomer().getSecurityQuestion();
	    String secQnA = response.getCustomer().getSecurityAnswer();

	    AddressBean address = new AddressBean();
	    address.setSecurityQuestion(secQn);
	    address.setSecQuesAns(secQnA);
	    CustomerBean cust = new CustomerBean();
	    cust.setAddress(address);
	    this.customer = cust;
	} catch (Exception e) {
	    LOG.error("# Failed to fetch customer securtiy question", e);
	    error(getLocalizer().getString("portal.update.secquestion.error",
		    this));
	}
    }

    public com.sybase365.mobiliser.web.beans.CustomerBean getCustomer() {
	return customer;
    }

}
