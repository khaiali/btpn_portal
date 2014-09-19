package com.sybase365.mobiliser.web.btpn.bank.pages.portal.blockcustomer;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;

import com.btpnwow.core.customer.facade.api.CustomerFacade;
import com.btpnwow.core.customer.facade.contract.CustomerInformationType;
import com.btpnwow.core.customer.facade.contract.UpdateCustomerExRequest;
import com.btpnwow.core.customer.facade.contract.UpdateCustomerExResponse;
import com.btpnwow.portal.common.util.MobiliserUtils;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.SearchCustomerCareMenu;

/**
 * This is the BlockCustomerPage for bank portals.
 * 
 * @author Febrie Subhan
 */
public class BlockCustomerConfirmPage extends SearchCustomerCareMenu {

	private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(BlockCustomerConfirmPage.class);
	
	private static final long serialVersionUID = 1L;
	
	@SpringBean(name = "customerFacade")
	protected CustomerFacade customerFacade;

	private String customerId;

	private String oldStatus;

	private CodeValue newStatus;

	public BlockCustomerConfirmPage(String customerId, String oldStatus, CodeValue newStatus) {
		this.oldStatus = oldStatus;
		this.newStatus = newStatus;
		this.customerId = customerId;
		
		initPageComponents();
	}
	
	protected void initPageComponents() {
		final Form<BlockCustomerConfirmPage> form = new Form<BlockCustomerConfirmPage>(
				"blockCustomerConfirmForm", new CompoundPropertyModel<BlockCustomerConfirmPage>(this));

		form.add(new FeedbackPanel("errorMessages"));
		
		form.add(new Label("oldStatus", oldStatus));
		form.add(new Label("newStatus", newStatus.getIdAndValue()));
		
		addSubmitButton(form);
		
		add(form);
	}

	private void addSubmitButton(final Form<BlockCustomerConfirmPage> form) {
		final Button btnSubmit = new Button("submitButton") {

			private static final long serialVersionUID = 1L;

			public void onSubmit() {
				try {
					CustomerInformationType info = new CustomerInformationType();
					info.setId(Long.valueOf(customerId));
					info.setStatus(Integer.valueOf(newStatus.getId()));
					
					UpdateCustomerExRequest request = MobiliserUtils.fill(new UpdateCustomerExRequest(), BlockCustomerConfirmPage.this);
					request.setInformation(info);
					request.setCallerId(Long.valueOf(BlockCustomerConfirmPage.this.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId()));
					request.setFlags(0);
					
					UpdateCustomerExResponse response = customerFacade.update(request);
					
					if (MobiliserUtils.success(response)) {
						setResponsePage(new BlockCustomerStatusPage(oldStatus, newStatus));
					} else {
						error(MobiliserUtils.errorMessage(response, BlockCustomerConfirmPage.this));
					}
				} catch (Exception e) {
					LOG.error("An exception was thrown", e);
					
					error(getLocalizer().getString("error.exception", this));
				}
			}
		};
		
		form.add(btnSubmit);
	}
}
