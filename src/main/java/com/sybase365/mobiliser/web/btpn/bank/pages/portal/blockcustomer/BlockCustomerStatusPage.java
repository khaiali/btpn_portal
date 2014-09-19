package com.sybase365.mobiliser.web.btpn.bank.pages.portal.blockcustomer;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;

import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.SearchCustomerCareMenu;

/**
 * This is the BlockCustomerPage for bank portals.
 * 
 * @author Febrie Subhan
 */
public class BlockCustomerStatusPage extends SearchCustomerCareMenu {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(BlockCustomerStatusPage.class);

	private String oldStatus;

	private CodeValue newStatus;

	public BlockCustomerStatusPage(String oldStatus, CodeValue newStatus) {
		this.oldStatus = oldStatus;
		this.newStatus = newStatus;
		
		initPageComponents();
	}

	protected void initPageComponents() {
		final Form<BlockCustomerStatusPage> form = new Form<BlockCustomerStatusPage>(
				"blockCustomerStatusForm", new CompoundPropertyModel<BlockCustomerStatusPage>(this));

		form.add(new FeedbackPanel("errorMessages"));
		
		form.add(new Label("oldStatus", oldStatus));
		form.add(new Label("newStatus", newStatus.getIdAndValue()));
		
		add(form);
	}
}
