package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.web.btpn.bank.beans.ManageCustomGeneralLedgerBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.generalledger.ManageCustomGLConfirmApprovePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * @author Andi Samallangi W
 *
 */
public class ManageCustomGLDetailApprovePanel extends Panel {

	private static final long serialVersionUID = 1L;
	
	private static final Logger log = LoggerFactory.getLogger(ManageCustomGLDetailApprovePanel.class);
	
	protected BtpnBaseBankPortalSelfCarePage basePage;
	protected ManageCustomGeneralLedgerBean cusLedgerBean;
	
	
	public ManageCustomGLDetailApprovePanel(String id, BtpnBaseBankPortalSelfCarePage basePage, ManageCustomGeneralLedgerBean cusLedgerBean) {
		super(id);
		this.basePage = basePage;
		this.cusLedgerBean = cusLedgerBean;
		constructPanel();
	}
	
	protected void constructPanel() {
		
		log.info(" ### (ManageCustomGLDetailApprovePanel) constructPanel ### "); 
		final Form<ManageCustomGLDetailApprovePanel> form = new Form<ManageCustomGLDetailApprovePanel>("cusGLDetailAppForm",
			new CompoundPropertyModel<ManageCustomGLDetailApprovePanel>(this));
		
		// Add feedback panel for Error Messages
		final FeedbackPanel feedBackPanel = new FeedbackPanel("errorMessages");
		form.add(feedBackPanel);
		
		form.add(new Label("cusLedgerBean.glCode"));
		form.add(new Label("cusLedgerBean.selectedParentGlCode.id"));
		form.add(new Label("cusLedgerBean.selectedType.value"));
		form.add(new Label("cusLedgerBean.glDescription"));
		form.add(new Label("cusLedgerBean.note"));

		
		form.add(new AjaxButton("approveBtn"){
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form){
				if (!PortalUtils.exists(cusLedgerBean)){
					cusLedgerBean = new ManageCustomGeneralLedgerBean();
				}
				setResponsePage(new ManageCustomGLConfirmApprovePage(cusLedgerBean, "approve"));
			}
		});
		
		form.add(new AjaxButton("rejectBtn"){
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form){
				setResponsePage(new ManageCustomGLConfirmApprovePage(cusLedgerBean, "reject"));
			}
		}.setDefaultFormProcessing(false));

		add(form);
	}

}
