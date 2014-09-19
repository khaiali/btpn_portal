package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.web.btpn.bank.beans.ManageInterestApproveBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageinterest.ManageInterestApprovePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;

/**
 * This is the Manage Interest Success Approve Panel for bank portals. This panel consists of adding fee as fixed, slab and sharing.
 * 
 * @author Feny Yanti
 */
public class ManageInterestSuccessApprovePanel extends Panel {

	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(ManageInterestSuccessApprovePanel.class);
	
	private BtpnBaseBankPortalSelfCarePage basePage;
	private ManageInterestApproveBean interestBean;
	private String flag;

	public ManageInterestSuccessApprovePanel(final String id, final BtpnBaseBankPortalSelfCarePage basePage,
			ManageInterestApproveBean interestBean, String flag) {
		super(id);
		this.basePage = basePage;
		this.interestBean = interestBean;
		this.flag = flag;
		constructPanel();
	}

	
	private void constructPanel() {
		
		log.info(" ### (ManageInterestSuccessPanel) constructPanel ###");
		
		final Form<ManageInterestSuccessApprovePanel> form = new Form<ManageInterestSuccessApprovePanel>("interestSucAprForm",
			new CompoundPropertyModel<ManageInterestSuccessApprovePanel>(this));
		
		// Add feedback panel for Error Messages
		final FeedbackPanel feedBackPanel = new FeedbackPanel("errorMessages");
		form.add(feedBackPanel);
		
		/// Add interest management fields
		form.add(new Label("interestBean.customerIdentifier"));
		form.add(new Label("interestBean.customerIdentifierType"));
		form.add(new Label("customerTypeId", interestBean.getCustomerTypeId()==null ? "-" : interestBean.getCustomerTypeId().getIdAndValue()));
		form.add(new Label("interestBean.paymentInstrumentId"));
		form.add(new Label("paymentInstrumentTypeId", interestBean.getPaymentInstrumentTypeId()==null? "-" : interestBean.getPaymentInstrumentTypeId().getIdAndValue()));
		form.add(new Label("accrueGLCode", interestBean.getAccrueGLCode().getIdAndValue()));
		form.add(new Label("expenseGLCode", interestBean.getExpenseGLCode().getIdAndValue()));
		form.add(new Label("interestBean.validFrom"));
		
		form.add(new Label("interestBean.fixedFee", Long.valueOf(interestBean.getFixedFee()/100).toString())); 
		form.add(new Label("interestBean.percentageFee", new BigDecimal(interestBean.getPercentageFee()).movePointLeft(2).setScale(2, RoundingMode.DOWN).toString()));
		form.add(new AmountLabel("interestBean.thresholdAmount", Long.valueOf(interestBean.getThresholdAmount()/100).toString()));
		form.add(new Label("interestBean.minimumFee"));
		form.add(new Label("interestBean.maximumFee",(interestBean.getMaximumFee()!=null) ? String.valueOf(interestBean.getMaximumFee()) : "" ));
		
		form.add(new Label("interestBean.description"));
		form.add(new Label("interestBean.note"));

		form.add(new AjaxButton("finishButton") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				setResponsePage(ManageInterestApprovePage.class);
			};
		});
		
		// Add add Button
		add(form);
	}

}
