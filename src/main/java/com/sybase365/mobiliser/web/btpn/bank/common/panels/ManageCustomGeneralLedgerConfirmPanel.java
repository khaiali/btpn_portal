package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;

import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageCustomGeneralLedgerBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.generalledger.ManageCustomGeneralLedgerPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BankPortalHomePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.behaviours.AttributePrepender;
import com.sybase365.mobiliser.web.btpn.util.BtpnLocalizableLookupDropDownChoice;

/**
 * @author Andi Samallangi W
 *
 */
public class ManageCustomGeneralLedgerConfirmPanel extends Panel {


	private static final long serialVersionUID = -4350259396616681889L;
	
	private static final Logger log = org.slf4j.LoggerFactory
			.getLogger(ManageCustomGeneralLedgerConfirmPanel.class);
	
	private FeedbackPanel feedBack;
	protected BtpnMobiliserBasePage basePage;
	private BtpnLocalizableLookupDropDownChoice<CodeValue> parentGLCode;
	private ManageCustomGeneralLedgerBean ledgerBean;
	
	public ManageCustomGeneralLedgerConfirmPanel(String id, BtpnBaseBankPortalSelfCarePage basePage, ManageCustomGeneralLedgerBean ledgerBean) {
		super(id);
		this.basePage = basePage;
		this.ledgerBean = ledgerBean;
		constructPanel();
	}
	
	
	@SuppressWarnings("unchecked")
	protected void constructPanel() {
		final Form<ManageCustomGeneralLedgerConfirmPanel> form = new Form<ManageCustomGeneralLedgerConfirmPanel>("customGLConfirmForm",
			new CompoundPropertyModel<ManageCustomGeneralLedgerConfirmPanel>(this));
		
		// Add feedback panel for Error Messages
		feedBack = new FeedbackPanel("errorMessages");
		feedBack.setOutputMarkupId(true);
		feedBack.setOutputMarkupPlaceholderTag(true);
		form.add(feedBack);
		
		form.add(new Label("ledgerBean.glCode"));
		form.add(new Label("ledgerBean.selectedParentGlCode.value"));
		form.add(new Label("ledgerBean.selectedType.value"));
		form.add(new Label("ledgerBean.glDescription"));
		form.add(new Label("ledgerBean.note"));
		
		// Add Confirm Button
		Button confirmButton = new Button("submitConfirm"){
			private static final long serialVersionUID = 1L;
			
			public void onSubmit(){
				handleGeneralLedger();
			}
		};
		confirmButton.add(new AttributePrepender("onclick", Model
				.of("loading(submitConfirm)"), ";"));
		form.add(confirmButton);
		
		// Add Back button
		form.add(new Button("submitBack") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(new ManageCustomGeneralLedgerPage());
			};
			
		}.setDefaultFormProcessing(false));
		
		// Add Cancel button
		form.add(new Button("submitCancle") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				basePage.handleCancelButtonRedirectToHomePage(BankPortalHomePage.class);
			};
		}.setDefaultFormProcessing(false));

		add(form);
	}
	
	
	protected void handleGeneralLedger(){
		try {
			
		} catch (Exception e) {
			log.error("");
			error(getLocalizer().getString("bank.gl.failure.exception", this));
		}
	}
	
	private void handleSpecificErrorMessages(final int errorCode) {
		// Specific error message handling
		final String messageKey = "bank.registration.error." + errorCode;
		String message = getLocalizer().getString(messageKey, this);
		// Generice error messages
		if (messageKey.equals(message)) {
			message = getLocalizer().getString("bank.registration.failure.message", this);
		}
		basePage.getWebSession().error(message);
	}

}
