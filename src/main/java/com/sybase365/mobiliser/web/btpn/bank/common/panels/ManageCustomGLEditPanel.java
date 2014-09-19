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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.core.gl.facade.contract.UpdateGLRequest;
import com.btpnwow.core.gl.facade.contract.UpdateGLResponse;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageCustomGeneralLedgerBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.generalledger.ManageCustomGLConfirmPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.generalledger.ManageCustomGLPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.generalledger.ManageCustomGLSuccessPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the Edit Page for Manage General Ledger List. This page only updates the description of the General Ledger
 * screen.
 * 
 * @author Andi Samallangi W
 */
public class ManageCustomGLEditPanel extends Panel {     
	
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(ManageCustomGLEditPanel.class);
	
	private FeedbackPanel feedBack;
	private WebMarkupContainer glContainer;
	protected ManageCustomGeneralLedgerBean cusLedgerBean;
	protected BtpnMobiliserBasePage basePage;
	
	private Component glCodeComp;
	private Component parentGLCodeComp;
	private Component typeComp;
	private Component glDescComp;
	private Component noteComp;

	/**
	 * Constructor for this page.
	 */
	public ManageCustomGLEditPanel(String id, BtpnBaseBankPortalSelfCarePage basePage, ManageCustomGeneralLedgerBean cusLedgerBean) {
		super(id);
		this.basePage = basePage;
		this.cusLedgerBean = cusLedgerBean;
		constructPanel();
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	private void constructPanel() {
		
		Form<ManageCustomGLEditPanel> form = new Form<ManageCustomGLEditPanel>("cusGLEditForm",
			new CompoundPropertyModel<ManageCustomGLEditPanel>(this));
		
		// Add feedback panel for Error Messages
		feedBack = new FeedbackPanel("errorMessages");
		feedBack.setOutputMarkupId(true);
		feedBack.setOutputMarkupPlaceholderTag(true);
		form.add(feedBack);
		
		form.add(glCodeComp = new TextField<String>("cusLedgerBean.glCode"));
		glCodeComp.setOutputMarkupId(true);

		form.add(parentGLCodeComp = new TextField<String>("cusLedgerBean.selectedParentGlCode.id"));
		parentGLCodeComp.setOutputMarkupId(true);

		form.add(typeComp = new TextField<String>("cusLedgerBean.selectedType.value"));
		typeComp.setOutputMarkupId(true);

		form.add(glDescComp = new TextField<String>("cusLedgerBean.glDescription"));
		glDescComp.setOutputMarkupId(true);

		form.add(noteComp = new TextField<String>("cusLedgerBean.note"));
		noteComp.setOutputMarkupId(true);

		glContainer = new WebMarkupContainer("glContainer");
		glContainer.setOutputMarkupId(true);
		glContainer.setOutputMarkupPlaceholderTag(true);
		glContainer.setVisible(false);
		form.add(glContainer);
		
		form.add(new AjaxButton("btnSubmit") {
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form) {
				if (!PortalUtils.exists(cusLedgerBean)) {
					cusLedgerBean = new ManageCustomGeneralLedgerBean();
				}
				setResponsePage(new ManageCustomGLConfirmPage(cusLedgerBean, "update"));
			}
		});
		
		form.add(new AjaxButton("btnCancel") {
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form) {
				setResponsePage(ManageCustomGLPage.class);
			}
		}.setDefaultFormProcessing(false));

		// Add add Button
		add(form);

	}

	
	private void handleUpdateGeneralLedger() {

		final UpdateGLRequest request;
		try {
			request = basePage.getNewMobiliserRequest(UpdateGLRequest.class);
			request.setCode(Long.parseLong(cusLedgerBean.getGlCode()));
			request.setDescription(cusLedgerBean.getGlDescription());
			request.setType(cusLedgerBean.getSelectedType().getId());
			final UpdateGLResponse response = this.basePage.getGlClient()
					.update(request);
			if (basePage.evaluateBankPortalMobiliserResponse(response)) {
				getSession().info(
						getLocalizer().getString("update.gl.success", this));
				setResponsePage(new ManageCustomGLSuccessPage(cusLedgerBean));
			} else {
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
