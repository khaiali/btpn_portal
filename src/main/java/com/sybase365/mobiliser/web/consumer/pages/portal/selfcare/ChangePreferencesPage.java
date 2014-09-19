package com.sybase365.mobiliser.web.consumer.pages.portal.selfcare;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.customer.GetCustomerRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetCustomerResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.UpdateCustomerRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.UpdateCustomerResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Customer;
import com.sybase365.mobiliser.web.common.components.KeyValue;
import com.sybase365.mobiliser.web.consumer.pages.portal.selfcare.ConsumerHomePage;
import com.sybase365.mobiliser.web.util.Constants;

@SuppressWarnings("unchecked")
@AuthorizeInstantiation(Constants.PRIV_CHANGE_PREFERENCES)
public class ChangePreferencesPage extends BaseSelfCarePage {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(ChangePreferencesPage.class);

    private boolean preference;
    private List<Integer> preferencesList = new ArrayList<Integer>();
    private Customer customer;

    public ChangePreferencesPage() {
	super();
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    public ChangePreferencesPage(final PageParameters parameters) {
	super(parameters);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();
	Form<?> form = new Form("changePreferencesForm",
		new CompoundPropertyModel<ChangePreferencesPage>(this));
	add(form);
	RadioGroup rg = new RadioGroup("radioGroup", new PropertyModel(this,
		"preference"));
	form.add(rg);
	getCustomer();
	rg.add(new Radio("yes", new Model(true)) {
	    @Override
	    protected void onComponentTag(ComponentTag tag) {
		super.onComponentTag(tag);
		if (customer.getTxnReceiptModeId() > 0)
		    tag.put("checked", "checked");
		else
		    tag.remove("checked");
	    }
	}.add(new SimpleAttributeModifier("onchange", "setNotification();")));
	rg.add(new Radio("no", new Model(false)) {
	    @Override
	    protected void onComponentTag(ComponentTag tag) {
		super.onComponentTag(tag);
		if ((customer.getTxnReceiptModeId() > 0))
		    tag.remove("checked");
		else
		    tag.put("checked", "checked");
	    }
	}.add(new SimpleAttributeModifier("onchange", "setNotification();")));
	CheckGroup preferences = new CheckGroup("chkBoxGroup",
		new PropertyModel(this, "preferencesList"));
	form.add(preferences);
	preferences.add(new Check("sms", new Model(1)) {
	    @Override
	    protected void onComponentTag(ComponentTag arg0) {
		super.onComponentTag(arg0);
		if ((customer.getTxnReceiptModeId() == 1 || customer
			.getTxnReceiptModeId() == 3))
		    arg0.put("checked", "checked");
	    }
	});
	preferences.add(new Check("email", new Model(2)) {
	    @Override
	    protected void onComponentTag(ComponentTag arg0) {
		super.onComponentTag(arg0);
		if ((customer.getTxnReceiptModeId() == 2 || customer
			.getTxnReceiptModeId() == 3))
		    arg0.put("checked", "checked");
	    }
	});
	form.add(new Button("change") {

	    @Override
	    public void onSubmit() {
		try {
		    handleSubmit();
		} catch (Exception e) {
		    LOG.error("# Change preferences failed with exception", e);
		    getMobiliserWebSession().setCustomer(null);
		    error(getLocalizer().getString("ERROR.DATA_NOT_STORED",
			    this));
		}
	    }

	});

    }

    private void handleSubmit() throws Exception {
	com.sybase365.mobiliser.util.tools.wicketutils.security.Customer loggedInCustomer = getMobiliserWebSession()
		.getLoggedInCustomer();
	GetCustomerRequest customerRequest = getNewMobiliserRequest(GetCustomerRequest.class);
	customerRequest.setCustomerId(loggedInCustomer.getCustomerId());
	GetCustomerResponse response = wsCustomerClient
		.getCustomer(customerRequest);
	if (!evaluateMobiliserResponse(response)) {
	    return;
	}
	com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Customer customerWeb = response
		.getCustomer();

	if (!preference) {
	    KeyValue<Integer, String> kv = new KeyValue();
	    kv.setKey(Constants.INFO_MODE_NONE);
	    customerWeb.setTxnReceiptModeId(kv.getKey());
	} else {
	    if (preferencesList.isEmpty()) {
		KeyValue<Integer, String> kv = new KeyValue();
		kv.setKey(Constants.INFO_MODE_NONE);
		customerWeb.setTxnReceiptModeId(kv.getKey());
		preference = false;
	    } else {
		int infoMode = 0;
		for (int prefsType : preferencesList) {
		    infoMode = infoMode + prefsType;
		}
		KeyValue<Integer, String> kv = new KeyValue();
		kv.setKey(infoMode);
		customerWeb.setTxnReceiptModeId(kv.getKey());
	    }
	}
	UpdateCustomerRequest updateCustRequest = getNewMobiliserRequest(UpdateCustomerRequest.class);
	updateCustRequest.setCustomer(customerWeb);
	UpdateCustomerResponse updateResponse = wsCustomerClient
		.updateCustomer(updateCustRequest);
	if (!evaluateMobiliserResponse(updateResponse)) {
	    return;
	}

	LOG.info("# Change preferences was successfully");
	getSession().info(
		getLocalizer()
			.getString("msg.change.preferences.success", this));
	setResponsePage(ConsumerHomePage.class);
    }

    private void getCustomer() {
	GetCustomerRequest getCustReq = new GetCustomerRequest();
	getCustReq.setCustomerId(getMobiliserWebSession().getLoggedInCustomer()
		.getCustomerId());
	GetCustomerResponse getCustRes = wsCustomerClient
		.getCustomer(getCustReq);
	if (evaluateMobiliserResponse(getCustRes)) {
	    this.customer = getCustRes.getCustomer();
	} else {
	    return;
	}

    }
}
