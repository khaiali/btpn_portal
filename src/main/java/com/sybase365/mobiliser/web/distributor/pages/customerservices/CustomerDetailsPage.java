package com.sybase365.mobiliser.web.distributor.pages.customerservices;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.common.panels.AttachmentsPanel;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_WALLET_SERVICES)
public class CustomerDetailsPage extends CustomerServicesMenuGroup {

    private static final long serialVersionUID = 1L;
    private FeedbackPanel detailsFeedBackPanel;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(CustomerDetailsPage.class);

    public CustomerDetailsPage() {
	super();
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    public CustomerDetailsPage(final PageParameters parameters) {
	super(parameters);
    }

    @Override
    protected void initOwnPageComponents() {

	super.initOwnPageComponents();

	final CustomerBean customer = getCustomer();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	final Form form = new Form("customerDetailsForm",
		new CompoundPropertyModel<CustomerDetailsPage>(this));
	detailsFeedBackPanel = new FeedbackPanel("errorMessages");
	form.add(detailsFeedBackPanel);
	form.add(new Label("customer.address.firstName"));
	form.add(new Label("customer.msisdn"));
	form.add(new Label("customer.kvIdentityType", getDisplayValue(
		String.valueOf(getCustomer().getKvIdentityType()),
		Constants.RESOURCE_BUNDLE_IDENTITYTYPES)));

	form.add(new Label("customer.timeZone", getDisplayValue(getCustomer()
		.getTimeZone(), Constants.RESOURCE_BUNDLE_TIMEZONES)));
	form.add(new Label("customer.address.lastName"));

	form.add(new Label("customer.kvCountry", getDisplayValue(
		String.valueOf(getCustomer().getKvCountry()),
		Constants.RESOURCE_BUNDLE_COUNTIRES)));

	form.add(new Label("customer.identityValue"));
	form.add(new Label("customer.address.street1"));
	form.add(new Label("customer.address.street2"));
	form.add(new Label("customer.address.houseNo"));
	form.add(new Label("customer.address.city"));
	form.add(new Label("customer.address.state"));
	form.add(new Label("customer.address.zip"));

	form.add(new Label("customer.address.kvCountry", getDisplayValue(
		String.valueOf(getCustomer().getAddress().getKvCountry()),
		Constants.RESOURCE_BUNDLE_COUNTIRES)));

	add(form);

	add(new AttachmentsPanel("attachments.panel", customer.getId(), this,
		detailsFeedBackPanel).setOutputMarkupPlaceholderTag(true));

    }

    public void setCustomer(CustomerBean customer) {
	getMobiliserWebSession().setCustomer(customer);
    }

    public CustomerBean getCustomer() {
	return getMobiliserWebSession().getCustomer();
    }

}
