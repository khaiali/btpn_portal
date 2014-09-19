package com.sybase365.mobiliser.web.btpn.consumer.common.panels;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.validation.validator.PatternValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.core.security.facade.contract.ChangeCredentialRequest;
import com.btpnwow.core.security.facade.contract.ChangeCredentialResponse;
import com.btpnwow.core.security.facade.contract.CustomerCredentialType;
import com.btpnwow.core.security.facade.contract.CustomerIdentificationType;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.consumer.beans.ChangePinBean;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.ConsumerPortalHomePage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;


public class ConsumerPortalChangePinPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(ConsumerPortalChangePinPanel.class);

	BtpnMobiliserBasePage basePage;

	ChangePinBean changePinBean;

	public ConsumerPortalChangePinPanel(String id, BtpnBaseConsumerPortalSelfCarePage basePage) {
		super(id);
		this.basePage = basePage;
		constructPanel();
	}

	protected void constructPanel() {
		
		final Form<ConsumerPortalChangePinPanel> form = new Form<ConsumerPortalChangePinPanel>("changePinForm",
			new CompoundPropertyModel<ConsumerPortalChangePinPanel>(this));
		form.add(new FeedbackPanel("errorMessages"));

		form.add(new PasswordTextField("changePinBean.oldPin").setRequired(true).add(new ErrorIndicator()));
		form.add(new PasswordTextField("changePinBean.newPin").setRequired(true).add(new ErrorIndicator()));
		form.add(new PasswordTextField("changePinBean.confirmNewPin").setRequired(true)
			.add(new PatternValidator(BtpnConstants.PIN_REGEX)).add(new ErrorIndicator()));

		// Add Submit button
		form.add(new Button("submitButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				if (changePinBean.getOldPin().equals(changePinBean.getNewPin())) {
					error(getLocalizer().getString("pin.same", ConsumerPortalChangePinPanel.this));
					return;
				}
				if (changePinBean.getNewPin().equals(changePinBean.getConfirmNewPin())) {
					if (ischangePinSuccess(changePinBean)) {
						basePage.getWebSession().info(
							getLocalizer().getString("changePinSuccessMessage", ConsumerPortalChangePinPanel.this));
						setResponsePage(new ConsumerPortalHomePage());
					}
				} else {
					error(getLocalizer().getString("Pin.notMatch", ConsumerPortalChangePinPanel.this));
					return;
				}
			};
		});
		add(form);
	}
	
	
	private boolean ischangePinSuccess(ChangePinBean changePinBean) {
		
		boolean changePinFlag = false;
		
		try {
			
			log.info(" ### (ConsumerPortalChangePinPanel::ischangePinSuccess) ### ");
			
			final ChangeCredentialRequest req = basePage.getNewMobiliserRequest(ChangeCredentialRequest.class);
			Long customerId = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId();
			log.info(" ### (ConsumerPortalChangePinPanel::ischangePinSuccess) CUST ID ### " +customerId);
			CustomerIdentificationType cit = new CustomerIdentificationType();
			cit.setType(1);
			cit.setValue(String.valueOf(customerId));
			
			CustomerCredentialType cct = new CustomerCredentialType();
			cct.setType(0);
			cct.setValue(changePinBean.getOldPin());
			
			req.setIdentification(cit);
			req.setCredential(cct);
			req.setNewCredentialValue(changePinBean.getNewPin());
			
			ChangeCredentialResponse response = basePage.getSecuritysClient().changeCredential(req);
			log.info(" ### (ConsumerPortalChangePinPanel::ischangePinSuccess) RESPONSE ### " +response.getStatus().getCode());
			if (basePage.evaluateConsumerPortalMobiliserResponse(response)) {
				changePinFlag = true;
			} else {
				error(response.getStatus().getValue());
			}
			
		} catch (Exception ex) {
			log.error("#An error occurred while calling changePin service.", ex);
			error(getLocalizer().getString("error.exception", this));
		}
		
		return changePinFlag;
	}
}
