package com.sybase365.mobiliser.web.common.reports.panels.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.customer.FindCustomerRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.FindHierarchicalCustomerRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.AddressFindBean;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.IdentificationFindBean;
import com.sybase365.mobiliser.util.contract.v5_0.report.beans.ReportRequestParameter;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.common.components.KeyValue;
import com.sybase365.mobiliser.web.common.components.KeyValueDropDownChoice;
import com.sybase365.mobiliser.web.common.components.KeyValueDropDownChoice.SortProperties;
import com.sybase365.mobiliser.web.common.dataproviders.CustomerBeanDataProvider;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.common.reports.components.DynamicComponent;
import com.sybase365.mobiliser.web.common.reports.components.DynamicDropDown;
import com.sybase365.mobiliser.web.common.reports.panels.ParameterEntryPanel;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PhoneNumber;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * 
 * <p>
 * &copy; 2012, Sybase Inc.
 * </p>
 * 
 * @author Mark White <msw@sybase.com>
 */
public abstract class AbstractFindCustomerHelperPanel extends Panel {

    private static final Logger LOG = LoggerFactory
	    .getLogger(AbstractFindCustomerHelperPanel.class);

    private MobiliserBasePage basePage;
    private ParameterEntryPanel parameterEntryPanel;
    private ReportRequestParameter parameter;
    private DynamicComponent dynamicComp;

    private String firstName;
    private String lastName;
    private String msisdn;

    private List<CustomerBean> customerList;
    private CustomerBeanDataProvider dataProvider;
    private final List<KeyValue<String, String>> dispCustList = new ArrayList<KeyValue<String, String>>();

    public AbstractFindCustomerHelperPanel(final String id,
	    final MobiliserBasePage basePage,
	    final ParameterEntryPanel parameterEntryPanel,
	    final ReportRequestParameter parameter,
	    final DynamicComponent dynamicComp) {

	super(id);

	this.basePage = basePage;
	this.parameterEntryPanel = parameterEntryPanel;
	this.parameter = parameter;
	this.dynamicComp = dynamicComp;

	@SuppressWarnings("rawtypes")
	final Form<?> form = new Form(
		"findCustomerForm",
		new CompoundPropertyModel<AbstractFindCustomerHelperPanel>(this));

	dataProvider = new CustomerBeanDataProvider("", basePage);

	final Component feedbackPanel = new FeedbackPanel("searchMessages")
		.setOutputMarkupId(true);

	form.add(feedbackPanel);

	addFormComponents(form);

	form.add(new AjaxButton("findCustomer") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {

		if (PortalUtils.exists(getMsisdn())
			|| PortalUtils.exists(getFirstName())
			|| PortalUtils.exists(getLastName())) {

		    customerList = Collections.EMPTY_LIST;

		    findCustomer();

		    if (PortalUtils.exists(customerList)) {
			try {
			    dispCustList.clear();

			    for (CustomerBean customer : customerList) {
				dispCustList.add(new KeyValue<String, String>(
					String.valueOf(customer.getId()),
					new StringBuilder()
						.append(customer
							.getDisplayName())
						.append(" (")
						.append(String.valueOf(customer
							.getId())).append(")")
						.toString()));
			    }

			    DynamicComponent newValue = new DynamicDropDown(
				    "dynValueContainer",
				    new KeyValueDropDownChoice<String, String>(
					    dynamicComp.getComponent().getId(),
					    new PropertyModel<String>(
						    parameter, "value"),
					    dispCustList, new SortProperties(
						    false, true))
					    .setRequired(true).setEnabled(true)
					    .setOutputMarkupId(true)
					    .add(new ErrorIndicator()));

			    parameterEntryPanel.updateValue((Panel) newValue);

			    target.addComponent((Panel) newValue);

			    info(getLocalizer().getString(
				    "report.search.customer.found", this));
			    target.addComponent(feedbackPanel);

			} catch (Exception e) {
			    LOG.warn(e.toString(), e);
			    error(getLocalizer().getString(
				    "report.search.customer.error", this));
			    target.addComponent(feedbackPanel);
			}

		    } else {
			info(getLocalizer().getString(
				"report.search.customer.none", this));
			target.addComponent(feedbackPanel);
		    }

		} else {
		    error(getLocalizer().getString(
			    "report.search.customer.entry.required", this));
		    target.addComponent(feedbackPanel);
		}
	    };
	});

	add(form);
    }

    protected abstract void addFormComponents(Form form);

    protected abstract boolean isHierarchical();

    protected abstract String getCustomerSearchType();

    private void findCustomer() {

	IdentificationFindBean id = null;

	if (PortalUtils.exists(getMsisdn())) {
	    PhoneNumber pn = new PhoneNumber(getMsisdn(), basePage
		    .getConfiguration().getCountryCode());
	    setMsisdn(pn.getInternationalFormat());
	    id = new IdentificationFindBean();
	    id.setType(Constants.IDENT_TYPE_MSISDN);
	    id.setIdentification(getMsisdn());
	    // TODO: Add drop down selector for the org unit of the
	    // identification one option would be find for any org unit
	    id.setAnyOrgUnit(Boolean.TRUE);
	}

	AddressFindBean address = null;

	if (PortalUtils.exists(getFirstName())
		|| PortalUtils.exists(getLastName())) {
	    address = new AddressFindBean();
	    if (PortalUtils.exists(getFirstName())) {
		address.setFirstName(getFirstName().replaceAll("\\*", "%"));
	    }
	    if (PortalUtils.exists(getLastName())) {
		address.setLastName(getLastName().replaceAll("\\*", "%"));
	    }
	}

	try {
	    if (isHierarchical()) {
		final FindHierarchicalCustomerRequest request = createFindHierarchicalAgentRequest(
			id, address);

		customerList = dataProvider.findCustomer(request, true);
	    } else {
		final FindCustomerRequest request = createFindCustomerRequest(
			id, address);

		customerList = dataProvider.findCustomer(request, true,
			getCustomerSearchType());
	    }
	} catch (DataProviderLoadException ex) {
	    LOG.error("# An error occurred in searching for customers.", ex);
	    error(getLocalizer().getString("agent.find.error", this));
	}
    }

    protected FindHierarchicalCustomerRequest createFindHierarchicalAgentRequest(
	    final IdentificationFindBean id, final AddressFindBean address) {
	FindHierarchicalCustomerRequest request = null;
	try {
	    request = basePage
		    .getNewMobiliserRequest(FindHierarchicalCustomerRequest.class);
	    request.setIdentification(id);
	    request.setAddress(address);
	} catch (Exception e) {
	    LOG.error("# An error occurred in find agent.", e);
	    error(getLocalizer().getString("agent.find.error", this));
	    return null;
	}
	return request;
    }

    protected FindCustomerRequest createFindCustomerRequest(
	    final IdentificationFindBean id, final AddressFindBean address) {

	FindCustomerRequest request = null;

	try {
	    request = basePage
		    .getNewMobiliserRequest(FindCustomerRequest.class);
	    request.setIdentification(id);
	    request.setAddress(address);
	} catch (Exception e) {
	    LOG.warn("An error occurred in find customer.", e);
	    error(getLocalizer().getString("customer.find.error", this));
	    return null;
	}
	return request;
    }

    public String getMsisdn() {
	return msisdn;
    }

    public void setMsisdn(String msisdn) {
	this.msisdn = msisdn;
    }

    public String getLastName() {
	return lastName;
    }

    public void setLastName(String lastName) {
	this.lastName = lastName;
    }

    public String getFirstName() {
	return firstName;
    }

    public void setFirstName(String firstName) {
	this.firstName = firstName;
    }

    public List<KeyValue<String, String>> getCustomerList() {
	return this.dispCustList;
    }
}