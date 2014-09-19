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

import com.sybase365.mobiliser.web.btpn.bank.beans.ManageInterestTaxApproveBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageinteresttax.ManageInterestTaxApprovePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageinteresttax.ManageInterestTaxConfirmApprovePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the Manage Interest Approve Details Panel.
 * 
 * @author Feny Yanti
 */
public class ManageInterestTaxDetailApprovePanel extends Panel {

	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(ManageInterestTaxDetailApprovePanel.class);

	protected BtpnBaseBankPortalSelfCarePage basePage;
	private ManageInterestTaxApproveBean interestTaxBean;

	
	public ManageInterestTaxDetailApprovePanel(final String id, BtpnBaseBankPortalSelfCarePage basePage,  ManageInterestTaxApproveBean interestTaxBean) {
		super(id);
		this.basePage = basePage;
		this.interestTaxBean = interestTaxBean;
		constructPanel();
	}

	
	private void constructPanel() {
		
		log.info(" ### (ManageInterestDetailApprovePanel) constructPanel() ### ");
		
		final Form<ManageInterestTaxDetailApprovePanel> form = new Form<ManageInterestTaxDetailApprovePanel>("interestTaxDetAppForm",
			new CompoundPropertyModel<ManageInterestTaxDetailApprovePanel>(this));

		// Add feedback panel for Error Messages
		final FeedbackPanel feedBackPanel = new FeedbackPanel("errorMessages");
		form.add(feedBackPanel);
		
		// interest detail fields
				form.add(new Label("interestTaxBean.customerIdentifier"));		
				form.add(new Label("interestTaxBean.customerIdentifierType"));			
				form.add(new Label("customerTypeId",interestTaxBean.getCustomerTypeId()==null? "-": interestTaxBean.getCustomerTypeId().getIdAndValue()));			
				form.add(new Label("interestTaxBean.paymentInstrumentId"));
				form.add(new Label("paymentInstrumentTypeId", interestTaxBean.getPaymentInstrumentTypeId()==null ?"-":interestTaxBean.getPaymentInstrumentTypeId().getIdAndValue()));
				
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
		
		form.add(new AjaxButton("approveBtn"){
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form){
				if (!PortalUtils.exists(interestTaxBean)){
					interestTaxBean = new ManageInterestTaxApproveBean();
				}
				setResponsePage(new ManageInterestTaxConfirmApprovePage(interestTaxBean, BtpnConstants.APPROVED));
			}
		});
		
		form.add(new AjaxButton("rejectBtn"){
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form){
				setResponsePage(new ManageInterestTaxConfirmApprovePage(interestTaxBean, BtpnConstants.REJECT));
			}
		}.setDefaultFormProcessing(false));
		
		form.add(new AjaxButton("backBtn"){
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form){
				setResponsePage(ManageInterestTaxApprovePage.class);
			}
		});
		
		add(form);
	}
	
}
