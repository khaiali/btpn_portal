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
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.generalledger.ManageCustomGeneralLedgerPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * @author Andi Samallangi W
 *
 */
public class ManageCustomGeneralLedgerSuccessPanel extends Panel {
	
	private static final long serialVersionUID = 1284055752006670606L;

	private FeedbackPanel feedBack;
	BtpnMobiliserBasePage basePage;
	ManageCustomGeneralLedgerBean ledgerBean;
	
	public ManageCustomGeneralLedgerSuccessPanel(String id, BtpnBaseBankPortalSelfCarePage basePage, ManageCustomGeneralLedgerBean ledgerBean) {
		super(id);
		this.basePage = basePage;
		this.ledgerBean = ledgerBean;
		constructPanel();
	}
	
	
	protected void constructPanel() {
		final Form<ManageCustomGeneralLedgerSuccessPanel> form = new Form<ManageCustomGeneralLedgerSuccessPanel>("customGLSuccessForm",
			new CompoundPropertyModel<ManageCustomGeneralLedgerSuccessPanel>(this));
		
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
		
		form.add(new AjaxButton("finishButton") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				setResponsePage(ManageCustomGeneralLedgerPage.class);
			};
		});
		
		add(form);
	}
}
