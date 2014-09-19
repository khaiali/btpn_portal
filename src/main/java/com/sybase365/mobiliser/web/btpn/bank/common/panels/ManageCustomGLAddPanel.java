package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.validation.validator.PatternValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageCustomGeneralLedgerBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.generalledger.ManageCustomGLConfirmPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.generalledger.ManageCustomGLPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnLocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * @author Andi Samallangi W
 *
 */
public class ManageCustomGLAddPanel extends Panel {

	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(ManageCustomGLAddPanel.class);
	
	private FeedbackPanel feedBack;
	private WebMarkupContainer glContainer;
	protected BtpnMobiliserBasePage basePage;
	protected ManageCustomGeneralLedgerBean cusLedgerBean;
	
	private Component glCodeComp;
	private Component parentGLCodeComp;
	private Component typeComp;
	private Component glDescComp;
	private Component noteComp;
	
	public ManageCustomGLAddPanel(String id, BtpnBaseBankPortalSelfCarePage basePage) {
		super(id);
		this.basePage = basePage;
		constructPanel();
	}
	
	protected void constructPanel() {
		
		log.info(" ### (ManageCustomGLAddPanel) constructPanel ### ");
		final Form<ManageCustomGLAddPanel> form = new Form<ManageCustomGLAddPanel>("cusGLAddForm",
			new CompoundPropertyModel<ManageCustomGLAddPanel>(this));
		
		// Add feedback panel for Error Messages
		feedBack = new FeedbackPanel("errorMessages");
		feedBack.setOutputMarkupId(true);
		feedBack.setOutputMarkupPlaceholderTag(true);
		form.add(feedBack);
		
		form.add(glCodeComp = new TextField<String>("cusLedgerBean.glCode").setRequired(true)
			.add(new PatternValidator(BtpnConstants.GL_CODE_REGEX)).add(BtpnConstants.GL_MINIMUM_LENGTH)
			.add(new ErrorIndicator()));
		glCodeComp.setOutputMarkupId(true);
		
		final IChoiceRenderer<CodeValue> renderChoice = new ChoiceRenderer<CodeValue>(
				BtpnConstants.DISPLAY_EXPRESSION_ID_VALUE, BtpnConstants.ID_EXPRESSION);
		
		form.add(parentGLCodeComp = new BtpnLocalizableLookupDropDownChoice<CodeValue>(
				"cusLedgerBean.selectedParentGlCode", CodeValue.class, BtpnConstants.RESOURCE_BUNDLE_PARENT_GENERAL_LEDGER_CODES, this)
				.setNullValid(true).setChoiceRenderer(renderChoice).add(new ErrorIndicator()));
		parentGLCodeComp.setOutputMarkupId(true);
		
		form.add(typeComp = new BtpnLocalizableLookupDropDownChoice<CodeValue>("cusLedgerBean.selectedType", CodeValue.class,
				BtpnConstants.RESOURCE_BUNDLE_GENERAL_LEDGER_TYPES, this).setRequired(true).add(new ErrorIndicator()));
		typeComp.setOutputMarkupId(true);
		
		form.add(glDescComp = new TextField<String>("cusLedgerBean.glDescription").setRequired(true).add(new ErrorIndicator()));
		glDescComp.setOutputMarkupId(true);
			
		form.add(noteComp = new TextField<String>("cusLedgerBean.note").add(new ErrorIndicator()));
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
				setResponsePage(new ManageCustomGLConfirmPage(cusLedgerBean, "add"));
				
			}
		});
		
		form.add(new AjaxButton("btnCancel") {
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form) {
				setResponsePage(ManageCustomGLPage.class);
			}
		}.setDefaultFormProcessing(false));
		
		add(form);
	}

}
