package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.core.limitex.services.contract.v1_0.ContinuePendingLimitExRequest;
import com.btpnwow.core.limitex.services.contract.v1_0.ContinuePendingLimitExResponse;
import com.btpnwow.core.limitex.services.contract.v1_0.CreateLimitExRequest;
import com.btpnwow.core.limitex.services.contract.v1_0.CreateLimitExResponse;
import com.btpnwow.core.limitex.services.contract.v1_0.LimitExType;
import com.btpnwow.portal.common.util.MobiliserUtils;
import com.jhlabs.image.RescaleFilter;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.ElimitBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.limit.ElimitApproveDetailsPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.limit.ElimitApprovePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.limit.ElimitApproveSuccessPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.limit.ElimitPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;
import com.sybase365.mobiliser.web.btpn.util.ConverterUtils;

/**
 * This is the CashoutDetailsPanel page for bank portals.
 * 
 * @author Febrie Subhan
 */
public class ElimitApproveConfirmPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory
			.getLogger(ElimitApproveConfirmPanel.class);

	protected BtpnBaseBankPortalSelfCarePage basePage;
	
	protected BtpnMobiliserBasePage mobBasePage; 

	protected ElimitBean limitBean;

	public ElimitApproveConfirmPanel(String id,
			BtpnBaseBankPortalSelfCarePage basePage, ElimitBean limitBean) {
		super(id);
		this.basePage = basePage;
		this.mobBasePage = basePage;
		this.limitBean = limitBean;
		constructPanel();
	}

	protected void constructPanel() {

		final Form<BankPortalCashinDetailsPanel> form = new Form<BankPortalCashinDetailsPanel>(
				"limitApproveConfirmForm",
				new CompoundPropertyModel<BankPortalCashinDetailsPanel>(this));
		// Add feedback panel for Error Messages
		final FeedbackPanel feedBack = new FeedbackPanel("errorMessages");
		form.add(feedBack);

		form.add(new Label("limitBean.description").setEnabled(false));
		form.add(new Label("limitBean.selectedPiType", limitBean.getSelectedPiType()==null? "-|-" : limitBean.getSelectedPiType().getIdAndValue()).setEnabled(false));
		form.add(new Label("limitBean.pi").setEnabled(false));
		form.add(new Label("limitBean.selectedCustomerType", limitBean.getSelectedCustomerType()==null ?"-|-" : limitBean.getSelectedCustomerType().getIdAndValue()).setEnabled(false));
		form.add(new Label("limitBean.customer").setEnabled(false));
		form.add(new Label("limitBean.selectedUseCases", limitBean.getSelectedUseCases()==null ?"-|-" : limitBean.getSelectedUseCases().getIdAndValue()).setEnabled(false));
		
		form.add(new Label("limitBean.singleDebitMinAmount"));
		form.add(new Label("limitBean.singleDebitMaxAmount"));
		form.add(new Label("limitBean.singleCreditMinAmount"));
		form.add(new Label("limitBean.singleCreditMaxAmount"));
		
		form.add(new Label("limitBean.dailyDebitMaxAmount"));
		form.add(new Label("limitBean.weeklyDebitMaxAmount"));
		form.add(new Label("limitBean.monthlyDebitMaxAmount"));
		form.add(new Label("limitBean.dailyCreditMaxAmount"));
		form.add(new Label("limitBean.weeklyCreditMaxAmount"));
		form.add(new Label("limitBean.monthlyCreditMaxAmount"));
		
		form.add(new Label("limitBean.dailyDebitMaxCount"));
		form.add(new Label("limitBean.weeklyDebitMaxCount"));
		form.add(new Label("limitBean.monthlyDebitMaxCount"));
		form.add(new Label("limitBean.dailyCreditMaxCount"));
		form.add(new Label("limitBean.weeklyCreditMaxCount"));
		form.add(new Label("limitBean.monthlyCreditMaxCount"));
		
		form.add(new Label("limitBean.maximumBalance").setEnabled(false));
		form.add(new Label("limitBean.minimumBalance").setEnabled(false));
		form.add(new Label("limitBean.creationDate").setEnabled(false));
		form.add(new Label("limitBean.creator").setEnabled(false));
		form.add(new Label("limitBean.status", "INITIAL").setEnabled(false));


		Button submitButton = new Button("submitButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				boolean approve = true;
				handleApproveCreateLimitBean(approve);

			};
		};
		form.add(submitButton);

		Button backButton = new Button("backButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				
				setResponsePage(new ElimitApproveDetailsPage(limitBean));

			};
		};

		form.add(backButton);
		
		
		Button cancelButton = new Button("cancelButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(new ElimitApprovePage());

			};
		};

		form.add(cancelButton);

		add(form);
	}
	
	
	/**
	 * This method handles the Creation of Fee Bean.
	 */
	private void handleApproveCreateLimitBean(boolean isApproved) {
		try {
			
			ContinuePendingLimitExRequest request = this.mobBasePage.getNewMobiliserRequest(ContinuePendingLimitExRequest.class);
			System.out.println("task id " + limitBean.getTaskId());
			request.setTaskId(limitBean.getTaskId());
			request.setApprove(isApproved);
			request.setCheckerId(1L);
			ContinuePendingLimitExResponse response = this.mobBasePage.limitExClient.continuePendingLimit(request);
			if (this.mobBasePage.evaluateBankPortalMobiliserResponse(response)) {
				this.mobBasePage.getWebSession().info(getLocalizer().getString("approve.success", this));
				setResponsePage(new ElimitApproveSuccessPage(limitBean));
			} else {
				error(MobiliserUtils.errorMessage(response.getStatus().getCode(), response.getStatus().getValue(), getLocalizer(), this));
				
			}
		} catch (Exception e) {
			this.mobBasePage.getWebSession().error(getLocalizer().getString("error.exception", this));
			LOG.error("Exception occured while creating the fees  ===> ", e);
		}
	}
	
	

	
}
