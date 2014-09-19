package com.sybase365.mobiliser.web.consumer.pages.portal.selfcare;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.money.contract.v5_0.customer.GetCustomerHistoryRequest;
import com.sybase365.mobiliser.web.common.panels.HistoryPanel;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_SHOW_HISTORY)
public class ShowHistoryPage extends BaseSelfCarePage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(ShowHistoryPage.class);

    @SuppressWarnings("unchecked")
    @Override
    protected void initOwnPageComponents() {

	super.initOwnPageComponents();

	Form<?> form = new Form("historyListForm",
		new CompoundPropertyModel<ShowHistoryPage>(this));

	form.add(new FeedbackPanel("messages"));
	GetCustomerHistoryRequest historyReq = null;
	try {
	    historyReq = getNewMobiliserRequest(GetCustomerHistoryRequest.class);
	    historyReq.setCustomerId(getMobiliserWebSession()
		    .getLoggedInCustomer().getCustomerId());
	} catch (Exception e) {
	    LOG.error(
		    "An error occurred in getting the customer history request",
		    e);
	    error(getLocalizer().getString("history.load.error", this));
	}
	form.add(new HistoryPanel("historyPanel", ShowHistoryPage.this,
		historyReq));
	add(form);

    }
}
