package com.sybase365.mobiliser.web.cst.pages.usermanager;

import java.util.Iterator;

import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.validation.validator.PatternValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.customer.ContinuePendingCustomerRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.ContinuePendingCustomerResponse;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.common.components.LocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.cst.pages.customercare.FindPendingCustomerPage;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

@AuthorizeInstantiation(Constants.PRIV_UMGR_EDIT_AGENT)
public class EditAgentPage extends UserManagerMenuGroup {
    private static final Logger LOG = LoggerFactory
	    .getLogger(EditAgentPage.class);

    private CustomerBean customer;

    private String subTitle1 = getLocalizer().getString("agent.edit.title",
	    this);

    private String subTitle2 = getLocalizer().getString("agent.edit.title",
	    this);

    private String subTitle2BrCrumbSep = getLocalizer().getString(
	    "application.breadcrumb.separator", this);

    private String subTitle3 = getLocalizer().getString("agent.edit.title",
	    this);

    private boolean isForApproval;

    public EditAgentPage() {
	super();
	this.customer = getCustomer();
	initPageComponents();
    }

    public EditAgentPage(CustomerBean customer) {
	super();
	this.customer = customer;
	this.isForApproval = customer.isPendingApproval();
	initPageComponents();

    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    public EditAgentPage(final PageParameters parameters) {
	super(parameters);
    }

    protected void initPageComponents() {
	Form<?> form = new Form("editAgentForm",
		new CompoundPropertyModel<EditAgentPage>(this));
	form.add(new FeedbackPanel("errorMessages"));

	if (isForApproval) {
	    subTitle1 = getLocalizer().getString("menu.cst.approvals", this);

	    subTitle2 = getLocalizer().getString("menu.cst.approve.custormer",
		    this);

	    subTitle3 = getLocalizer().getString("pending.agents.title", this);

	}

	add(new Label("subTitle1", subTitle1));

	add(new Label("subTitle1BrCrumbSep", subTitle2BrCrumbSep)
		.setVisible(isForApproval));

	add(new Label("subTitle2", subTitle2).setVisible(isForApproval));

	add(new Label("subTitle2BrCrumbSep", subTitle2BrCrumbSep)
		.setVisible(isForApproval));

	add(new Label("subTitle3", subTitle3).setVisible(isForApproval));

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

	form.add(
		new TextField<String>("customer.address.position").add(
			Constants.mediumStringValidator).add(
			Constants.mediumSimpleAttributeModifier)).add(
		new ErrorIndicator());

	form.add(
		new RequiredTextField<String>("customer.address.email")
			.add(new PatternValidator(Constants.REGEX_EMAIL))
			.add(Constants.mediumStringValidator)
			.add(Constants.mediumSimpleAttributeModifier)).add(
		new ErrorIndicator());

	form.add(new LocalizableLookupDropDownChoice<String>(
		"customer.timeZone", String.class, "timezones", this, false,
		true).setNullValid(false).add(new ErrorIndicator()));

	form.add(new LocalizableLookupDropDownChoice<String>(
		"customer.address.kvCountry", String.class, "countries", this,
		false, true).setNullValid(false).setRequired(true)
		.add(new ErrorIndicator()));

	form.add(new LocalizableLookupDropDownChoice<String>(
		"customer.language", String.class, "languages", this, false,
		true).setNullValid(false).setRequired(true)
		.add(new ErrorIndicator()));

	form.add(new Button("save") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		updatePersonalData();
	    }

	    ;
	}.setVisible(!isForApproval));

	form.add(new Button("approve") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		approveCustomer(true);
	    };
	}.setVisible(isForApproval));

	form.add(new Button("reject") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		approveCustomer(false);
	    };
	}.setDefaultFormProcessing(false).setVisible(isForApproval));

	if (PortalUtils.exists(getCustomer().getTaskId())) {
	    Iterator iter = form.iterator();
	    Component component;
	    for (int i = 0; iter.hasNext(); i++) {
		component = (Component) iter.next();

		if (component.getId().equals("approve")
			|| component.getId().equals("reject")
			|| component instanceof FeedbackPanel) {
		    continue;
		} else {

		    component.setEnabled(false);
		    component.add(new SimpleAttributeModifier("readonly",
			    "readonly"));
		    component.add(new SimpleAttributeModifier("style",
			    "background-color: #E6E6E6;"));

		}
	    }
	}

	add(form);
    }

    private void updatePersonalData() {
	try {
	    updateCustomerAddress(customer);
	    customer.setDisplayName(createDisplayName(customer.getAddress()
		    .getFirstName(), customer.getAddress().getLastName()));
	    updateCustomerDetail(customer);
	    getMobiliserWebSession().setCustomer(customer);
	    info(getLocalizer().getString(
		    "MESSAGE.EDIT_AGENT_PERSONALDATA_UPDATED", this));
	    setResponsePage(new EditAgentPage(customer));
	} catch (Exception e) {
	    LOG.error(
		    "# An error occurred whilte saving Personal data, with agent Id {}",
		    customer.getId(), e);
	    error(getLocalizer().getString(
		    "ERROR.EDIT_AGENT_PERSONALDATA_FAILURE", this));
	}
    }

    private void approveCustomer(boolean bApprove) {
	ContinuePendingCustomerResponse response;

	try {
	    ContinuePendingCustomerRequest request = getNewMobiliserRequest(ContinuePendingCustomerRequest.class);
	    request.setTaskId(getCustomer().getTaskId());
	    request.setApprove(bApprove);
	    response = wsCustomerClient.continuePendingCustomer(request);
	    if (!evaluateMobiliserResponse(response)) {
		if (bApprove) {
		    LOG.warn("# An error occurred while approving agent");
		} else {
		    LOG.warn("# An error occurred while rejecting agent");
		}
		return;
	    }
	} catch (Exception e) {
	    if (bApprove) {
		LOG.error("# An error occurred while approving agent");
		error(getLocalizer().getString("agent.approval.error", this));

	    } else {
		LOG.error("# An error occurred while rejecting customer");
		error(getLocalizer().getString("agent.rejection.error", this));

	    }

	}
	if (bApprove) {
	    LOG.info("Agent approved successfully");
	    getMobiliserWebSession().info(
		    getLocalizer()
			    .getString("agent.approved.successfull", this));

	} else {
	    LOG.info("Agent rejected successfully");
	    getMobiliserWebSession().info(
		    getLocalizer()
			    .getString("agent.rejected.successfull", this));

	}

	setResponsePage(FindPendingCustomerPage.class);

    }

    public CustomerBean getCustomer() {
	return getMobiliserWebSession().getCustomer();
    }

    public void setCustomer(CustomerBean customer) {
	this.customer = customer;
    }
}
