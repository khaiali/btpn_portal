package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.validation.validator.PatternValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.core.gl.facade.contract.AddGLRequest;
import com.btpnwow.core.gl.facade.contract.AddGLResponse;
import com.btpnwow.core.gl.facade.contract.UpdateGLRequest;
import com.btpnwow.core.gl.facade.contract.UpdateGLResponse;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageCustomGeneralLedgerBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.generalledger.ManageCustomGeneralLedgerPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.generalledger.ManageCustomGeneralLedgerSuccessPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnLocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * @author Andi Samallangi W
 *
 */
public class ManageCustomGeneralLedgerAddPanel extends Panel {

	private static final long serialVersionUID = -4350259396616681889L;
	private static final Logger log = LoggerFactory.getLogger(ManageCustomGeneralLedgerAddPanel.class);
	private FeedbackPanel feedBack;
	protected BtpnMobiliserBasePage basePage;
	private WebMarkupContainer glContainer;
	private ManageCustomGeneralLedgerBean ledgerBean;
	
	private Component glCodeComp;
	private Component parentGLCodeComp;
	private Component typeComp;
	private Component glDescComp;
	private Component noteComp;
	
	public ManageCustomGeneralLedgerAddPanel(String id, BtpnBaseBankPortalSelfCarePage basePage) {
		super(id);
		this.basePage = basePage;
		constructPanel();
	}
	
	public ManageCustomGeneralLedgerAddPanel(String id, BtpnBaseBankPortalSelfCarePage basePage, ManageCustomGeneralLedgerBean ledgerBean) {
		super(id);
		this.basePage = basePage;
		this.ledgerBean = ledgerBean;
		constructPanel();
	}
	
	protected void constructPanel() {
		final Form<ManageCustomGeneralLedgerAddPanel> form = new Form<ManageCustomGeneralLedgerAddPanel>("customGLForm",
			new CompoundPropertyModel<ManageCustomGeneralLedgerAddPanel>(this));
		
		// Add feedback panel for Error Messages
		feedBack = new FeedbackPanel("errorMessages");
		feedBack.setOutputMarkupId(true);
		feedBack.setOutputMarkupPlaceholderTag(true);
		
		form.add(glCodeComp = new TextField<String>("ledgerBean.glCode").setRequired(true)
			.add(new PatternValidator(BtpnConstants.GL_CODE_REGEX)).add(BtpnConstants.GL_MINIMUM_LENGTH)
			.add(new ErrorIndicator()));
		glCodeComp.setOutputMarkupId(true);
		
		form.add(parentGLCodeComp = new BtpnLocalizableLookupDropDownChoice<CodeValue>(
				"ledgerBean.selectedParentGlCode", CodeValue.class, BtpnConstants.RESOURCE_BUNDLE_PARENT_GENERAL_LEDGER_CODES, this)
				.setNullValid(true).add(new ErrorIndicator()));
		parentGLCodeComp.setOutputMarkupId(true);
		
		form.add(typeComp = new BtpnLocalizableLookupDropDownChoice<CodeValue>("ledgerBean.selectedType", CodeValue.class,
				BtpnConstants.RESOURCE_BUNDLE_GENERAL_LEDGER_TYPES, this).setRequired(true).add(new ErrorIndicator()));
		typeComp.setOutputMarkupId(true);
		
		form.add(glDescComp = new TextField<String>("ledgerBean.glDescription").setRequired(true).add(new ErrorIndicator()));
		glDescComp.setOutputMarkupId(true);
			
		form.add(noteComp = new TextField<String>("ledgerBean.note").add(new ErrorIndicator()));
		noteComp.setOutputMarkupId(true);
		
		glContainer = new WebMarkupContainer("glContainer");
		glContainer.setOutputMarkupId(true);
		glContainer.setOutputMarkupPlaceholderTag(true);
		glContainer.setVisible(false);
		glContainer.add(feedBack);
		glContainer.add(glCodeComp);
		glContainer.add(parentGLCodeComp);
		glContainer.add(typeComp);
		glContainer.add(glDescComp);
		glContainer.add(noteComp);
		glContainer.add(submitButton);
		glContainer.add(cancelButton);
		form.add(glContainer);
		
		add(form);
	}
	
	
	Button submitButton = new Button("btnSubmit") {
		private static final long serialVersionUID = 1L;
		@Override
		public void onSubmit() {
			if (!PortalUtils.exists(ledgerBean)) {
				ledgerBean = new ManageCustomGeneralLedgerBean();
			}
//			if (!StringUtils.isEmpty(ledgerBean.getFlag()) && ledgerBean.getFlag().equalsIgnoreCase("add"))
//				handleAddGeneralLedger();
//			else
//				handleUpdateGeneralLedger();
		}
	};
	
	Button cancelButton = new Button("btnCancel") {
		private static final long serialVersionUID = 1L;
		@Override
		public void onSubmit() {
			setResponsePage(ManageCustomGeneralLedgerPage.class);
		}
	}.setDefaultFormProcessing(false);
	
	

	private void handleAddGeneralLedger() {
		
		AddGLRequest request;
		try {
			request = basePage.getNewMobiliserRequest(AddGLRequest.class);
			request.setCode(Long.parseLong(ledgerBean.getGlCode()));
			request.setParent(Long.parseLong(ledgerBean.getSelectedParentGlCode().getId()));
			request.setDescription(ledgerBean.getGlDescription());
			request.setType(ledgerBean.getSelectedType().getId());
			
			final AddGLResponse response = this.basePage.getGlClient().add(request);
			if (basePage.evaluateBankPortalMobiliserResponse(response)){
				setResponsePage(new ManageCustomGeneralLedgerSuccessPage(ledgerBean));
			}else{
				handleSpecificErrorMessage(response.getStatus().getCode());
			}
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			log.error("Exception occured while Add GL ===> ", e);
		}
	}
	
	
	private void handleUpdateGeneralLedger(){
		
		UpdateGLRequest request;
		try {
			request = basePage.getNewMobiliserRequest(UpdateGLRequest.class);
			request.setCode(Long.parseLong(ledgerBean.getGlCode()));
			request.setDescription(ledgerBean.getGlDescription());
			final UpdateGLResponse response = this.basePage.getGlClient().update(request);
			if (basePage.evaluateBankPortalMobiliserResponse(response)){
				setResponsePage(new ManageCustomGeneralLedgerSuccessPage(ledgerBean));
			}else{
				handleSpecificErrorMessage(response.getStatus().getCode());
			}
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			log.error("Exception occured while Update GL ===> ", e);
		}
	}
	
	/**
	 * This method handles the specific error message.
	 */
	private String handleSpecificErrorMessage(final int errorCode) {
		// Specific error message handling
		final String messageKey = "error.gl." + errorCode;
		String message = getLocalizer().getString(messageKey, this);
		// Generice error messages
		if (messageKey.equals(message)) {
			message = getLocalizer().getString("gl.error", this);
		}
		return message;
	}

}
