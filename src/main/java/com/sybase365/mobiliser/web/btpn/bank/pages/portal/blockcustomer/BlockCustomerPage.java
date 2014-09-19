package com.sybase365.mobiliser.web.btpn.bank.pages.portal.blockcustomer;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;

import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.SearchCustomerCareMenu;
import com.sybase365.mobiliser.web.btpn.util.BtpnLocalizableLookupDropDownChoice;

/**
 * This is the BlockCustomerPage for bank portals.
 * 
 * @author Febrie Subhan
 */
public class BlockCustomerPage extends SearchCustomerCareMenu {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(BlockCustomerPage.class);

	private String oldStatus;

	private CodeValue newStatus;

	public BlockCustomerPage() {
		oldStatus = getMobiliserWebSession().getCustomerRegistrationBean().getBlackListReason().getIdAndValue();
		
		constructPanel();
	}

	protected void constructPanel() {
		final Form<BlockCustomerPage> form = new Form<BlockCustomerPage>(
				"blockCustomerForm", new CompoundPropertyModel<BlockCustomerPage>(this));
		
		form.add(new FeedbackPanel("errorMessages"));

		form.add(new Label("oldStatus", oldStatus));
		
		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("newStatus", CodeValue.class, "blacklistReasonsForBlocking", this, Boolean.TRUE, true)
				.setNullValid(false)
				.setRequired(true)
				.add(new ErrorIndicator()));
		
		form.add(new Button("submitButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				goToConfirmBlockCustomer();
			};
		});
		add(form);
	}

	private void goToConfirmBlockCustomer() {
		try {
			setResponsePage(new BlockCustomerConfirmPage(getMobiliserWebSession().getCustomerRegistrationBean().getCustomerId(), oldStatus, newStatus));
		} catch (Exception ex) {
			LOG.error("#An error occurred while calling service", ex);
			
			error(getLocalizer().getString("error.exception", this));
		}
	}
}
