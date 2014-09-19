package com.sybase365.mobiliser.web.btpn.agent.common.panels;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.validation.validator.PatternValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.support.ChangePinRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.support.ChangePinResponse;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.selfcare.BtpnBaseAgentPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.consumer.beans.ChangePinBean;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;

/**
 * This is the ConsumerPortalChangePinPanel page for agent portals.
 * 
 * @author Narasa Reddy
 */
public class AgentPortalChangePinPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(AgentPortalChangePinPanel.class);

	protected BtpnMobiliserBasePage basePage;

	protected ChangePinBean changePinBean;

	public AgentPortalChangePinPanel(String id, BtpnBaseAgentPortalSelfCarePage basePage) {
		super(id);
		this.basePage = basePage;
		constructPanel();
	}

	protected void constructPanel() {
		final Form<AgentPortalChangePinPanel> form = new Form<AgentPortalChangePinPanel>("changePinForm",
			new CompoundPropertyModel<AgentPortalChangePinPanel>(this));
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
					error(getLocalizer().getString("pin.same", AgentPortalChangePinPanel.this));
					return;
				}
				if (changePinBean.getNewPin().equals(changePinBean.getConfirmNewPin())) {
					if (ischangePinSuccess(changePinBean)) {
						basePage.getWebSession().info(
							getLocalizer().getString("changePinSuccessMessage", AgentPortalChangePinPanel.this));
					}
				} else {
					error(getLocalizer().getString("Pin.notMatch", AgentPortalChangePinPanel.this));
					return;
				}
			};
		});
		add(form);
	}

	private boolean ischangePinSuccess(ChangePinBean changePinBean) {
		boolean changePinFlag = false;
		try {
			final ChangePinRequest changePinRequest = basePage.getNewMobiliserRequest(ChangePinRequest.class);
			changePinRequest.setCustomerId(basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			changePinRequest.setExistingPin(changePinBean.getOldPin());
			changePinRequest.setPin(changePinBean.getNewPin());
			changePinRequest.setReEnterPin(changePinBean.getConfirmNewPin());
			ChangePinResponse changePinResponse = basePage.getSupportClient().changePin(changePinRequest);
			if (basePage.evaluateConsumerPortalMobiliserResponse(changePinResponse)) {
				changePinFlag = true;
			} else {
				error(changePinResponse.getStatus().getValue());
			}
		} catch (Exception ex) {
			LOG.error("#An error occurred while calling changePin service.", ex);
			error(getLocalizer().getString("error.exception", this));
		}
		return changePinFlag;
	}
}
