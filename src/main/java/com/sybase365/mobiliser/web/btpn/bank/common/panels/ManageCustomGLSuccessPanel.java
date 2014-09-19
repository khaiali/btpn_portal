package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageCustomGeneralLedgerBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.generalledger.ManageCustomGLPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * @author Andi Samallangi W
 *
 */
public class ManageCustomGLSuccessPanel extends Panel {
	
	private static final long serialVersionUID = 1L;

	private FeedbackPanel feedBack;
	protected BtpnMobiliserBasePage basePage;
	protected ManageCustomGeneralLedgerBean cusLedgerBean;
	
	public ManageCustomGLSuccessPanel(String id, BtpnBaseBankPortalSelfCarePage basePage, ManageCustomGeneralLedgerBean cusLedgerBean) {
		super(id);
		this.basePage = basePage;
		this.cusLedgerBean = cusLedgerBean;
		constructPanel();
	}
	
	
	protected void constructPanel() {
		final Form<ManageCustomGLSuccessPanel> form = new Form<ManageCustomGLSuccessPanel>("cusGLSuccessForm",
			new CompoundPropertyModel<ManageCustomGLSuccessPanel>(this));
		
		// Add feedback panel for Error Messages
		feedBack = new FeedbackPanel("errorMessages");
		feedBack.setOutputMarkupId(true);
		feedBack.setOutputMarkupPlaceholderTag(true);
		form.add(feedBack);
		
		form.add(new Label("cusLedgerBean.glCode"));
		form.add(new Label("cusLedgerBean.selectedParentGlCode.id"));
		form.add(new Label("cusLedgerBean.selectedType.value"));
		form.add(new Label("cusLedgerBean.glDescription"));
		form.add(new Label("cusLedgerBean.note"));
		
		form.add(new AjaxButton("finishButton") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				setResponsePage(ManageCustomGLPage.class);
			};
		});
		
		add(form);
	}
}
