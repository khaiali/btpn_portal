package com.sybase365.mobiliser.web.btpn.bank.common.panels;


import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.validation.validator.PatternValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.BankCustomCashOutBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.cashout.BankPortalCustomCashOutConfirmPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.util.PortalUtils;


public class BankPortalCustomCashOutPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(BankPortalCustomCashOutPanel.class);

	private FeedbackPanel feedBack;

	protected BtpnMobiliserBasePage basePage;

	protected BankCustomCashOutBean cashOutBean;

	private WebMarkupContainer cashOutContainer;

	private Component msisdnComponent;

	private Component amountComponent;

	public BankPortalCustomCashOutPanel(String id, BtpnBaseBankPortalSelfCarePage basePage, BankCustomCashOutBean cashOutBean) {
		super(id);
		this.basePage = basePage;
		this.cashOutBean = cashOutBean;
		// Adds the panel
		constructPanel();
	}

	protected void constructPanel() {

		final Form<BankPortalCustomCashOutPanel> form = new Form<BankPortalCustomCashOutPanel>("customCashOutForm",
			new CompoundPropertyModel<BankPortalCustomCashOutPanel>(this));

		// Add feedback panel for Error Messages
		feedBack = new FeedbackPanel("errorMessages");
		feedBack.setOutputMarkupId(true);
		feedBack.setOutputMarkupPlaceholderTag(true);
		form.add(feedBack);

		form.add(msisdnComponent = new TextField<String>("cashOutBean.mobileNumber")
			.add(new PatternValidator(this.basePage.getBankPortalPrefsConfig().getMobileRegex()))
			.add(BtpnConstants.PHONE_NUMBER_VALIDATOR).add(BtpnConstants.PHONE_NUMBER_MAX_LENGTH)
			.add(new ErrorIndicator()));
		msisdnComponent.setOutputMarkupId(true);
		
//		form.add(amountComponent = new TextField<String>("cashInBean.cashinAmount")
//				.add(new PatternValidator(this.basePage.getBankPortalPrefsConfig().getAmountRegex())))
//				.add(new ErrorIndicator());
//		amountComponent.setOutputMarkupId(true);

		form.add(amountComponent = new TextField<String>("cashOutBean.cashOutAmount")
				.add(new ErrorIndicator()));
		amountComponent.setOutputMarkupId(true);

		cashOutContainer = new WebMarkupContainer("cashOutContainer");
		cashOutContainer.setOutputMarkupId(true);
		cashOutContainer.setOutputMarkupPlaceholderTag(true);
		cashOutContainer.setVisible(false);
		form.add(cashOutContainer);

		form.add(new AjaxButton("submitButton") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				if (!PortalUtils.exists(cashOutBean)) {
					cashOutBean = new BankCustomCashOutBean();
				}
				setResponsePage(new BankPortalCustomCashOutConfirmPage(cashOutBean));
			};

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.addComponent(feedBack);
				target.addComponent(msisdnComponent);
				target.addComponent(amountComponent);
				target.addComponent(cashOutContainer);
				super.onError(target, form);
			}
		});

		add(form);
	}
	
}
