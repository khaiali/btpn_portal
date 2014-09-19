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

import com.sybase365.mobiliser.web.btpn.bank.beans.ManageInterestTaxBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageinteresttax.ManageInterestTaxConfirmPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageinteresttax.ManageInterestTaxEditPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageinteresttax.ManageInterestTaxPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the Manage Interest Add Details Panel.
 * 
 * @author Feny Yanti
 */
public class ManageInterestTaxDetailPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(ManageInterestTaxDetailPanel.class);

	protected BtpnBaseBankPortalSelfCarePage basePage;
	protected ManageInterestTaxBean interestTaxBean;

	
	public ManageInterestTaxDetailPanel(final String id, BtpnBaseBankPortalSelfCarePage basePage,  ManageInterestTaxBean interestTaxBean) {
		super(id);
		this.basePage = basePage;
		this.interestTaxBean = interestTaxBean;
		constructPanel();
	}

	
	private void constructPanel() {
		
		log.info(" ### (ManageInterestDetailPanel) constructPanel ### ");
		
		final Form<ManageInterestTaxDetailPanel> form = new Form<ManageInterestTaxDetailPanel>("interestTaxDetForm",
			new CompoundPropertyModel<ManageInterestTaxDetailPanel>(this));

		// Add feedback panel for Error Messages
		final FeedbackPanel feedBackPanel = new FeedbackPanel("errorMessages");
		form.add(feedBackPanel);
		
		// interest detail fields
		form.add(new Label("interestTaxBean.customerIdentifier"));
		form.add(new Label("interestTaxBean.customerIdentifierType"));
		form.add(new Label("customerTypeId", interestTaxBean.getCustomerTypeId()==null ? "-" : interestTaxBean.getCustomerTypeId().getIdAndValue()));
		form.add(new Label("interestTaxBean.paymentInstrumentId"));
		form.add(new Label("paymentInstrumentTypeId", interestTaxBean.getPaymentInstrumentTypeId()==null? "-" : interestTaxBean.getPaymentInstrumentTypeId().getIdAndValue()));
	
		form.add(new Label("accrueGLCode", interestTaxBean.getAccrueGLCode().getIdAndValue()));
		form.add(new Label("taxGLCode", interestTaxBean.getTaxGLCode().getIdAndValue()));
		form.add(new Label("interestTaxBean.validFrom"));
		form.add(new Label("interestTaxBean.fixedFee"));
		form.add(new Label("interestTaxBean.percentageFee"));
		form.add(new Label("interestTaxBean.maximumFee"));
		form.add(new Label("interestTaxBean.minimumFee"));
		form.add(new AmountLabel("interestTaxBean.thresholdAmount"));
		form.add(new Label("interestTaxBean.description"));
		form.add(new Label("interestTaxBean.note"));
		log.info(" ### (ManageInterestTaxDetailPanel) SHOW DETAIL PAGE ### ");
		
		
		form.add(new AjaxButton("editBtn"){
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form){
				if (!PortalUtils.exists(interestTaxBean)){
					interestTaxBean = new ManageInterestTaxBean();
				}
				setResponsePage(new ManageInterestTaxEditPage(interestTaxBean));
			}
		});
		
		
		form.add(new AjaxButton("delBtn"){
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form){
				if (!PortalUtils.exists(interestTaxBean)){
					interestTaxBean = new ManageInterestTaxBean();
				}
				setResponsePage(new ManageInterestTaxConfirmPage(interestTaxBean, BtpnConstants.DELETE));
			}
		});
		
		form.add(new AjaxButton("backBtn"){
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form){
				setResponsePage(ManageInterestTaxPage.class);
			}
		});
		
		// Add add Button
		add(form);
	}
	
}
