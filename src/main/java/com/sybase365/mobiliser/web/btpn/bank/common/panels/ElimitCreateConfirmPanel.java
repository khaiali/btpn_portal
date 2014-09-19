package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.core.limitex.services.contract.v1_0.CreateLimitExRequest;
import com.btpnwow.core.limitex.services.contract.v1_0.CreateLimitExResponse;
import com.btpnwow.core.limitex.services.contract.v1_0.IsLimitAlreadyDefinedRequest;
import com.btpnwow.core.limitex.services.contract.v1_0.IsLimitAlreadyDefinedResponse;
import com.btpnwow.core.limitex.services.contract.v1_0.LimitExType;
import com.btpnwow.portal.common.util.MobiliserUtils;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.ElimitBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.limit.ElimitCreatePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.limit.ElimitCreateSuccessPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.limit.ElimitPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.util.ConverterUtils;

/**
 * This is the CashoutDetailsPanel page for bank portals.
 * 
 * @author Febrie Subhan
 */
public class ElimitCreateConfirmPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory
			.getLogger(ElimitCreateConfirmPanel.class);

	protected BtpnBaseBankPortalSelfCarePage basePage;

	protected ElimitBean limitBean;
	
	protected BtpnMobiliserBasePage mobBasePage;

	public ElimitCreateConfirmPanel(String id,
			BtpnBaseBankPortalSelfCarePage basePage, ElimitBean limitBean) {
		super(id);
		this.mobBasePage = basePage;	
		this.basePage = basePage;
		this.limitBean = limitBean;
		constructPanel();
	}

	protected void constructPanel() {

		final Form<ElimitCreateConfirmPanel> form = new Form<ElimitCreateConfirmPanel>(
				"limitCreateConfirmForm",
				new CompoundPropertyModel<ElimitCreateConfirmPanel>(this));
		// Add feedback panel for Error Messages
		final FeedbackPanel feedBack = new FeedbackPanel("errorMessages");
		form.add(feedBack);

		form.add(new Label("limitBean.description").setEnabled(false));
		form.add(new Label("limitBean.selectedPiType", limitBean.getSelectedPiType()==null ? "-|-" : limitBean.getSelectedPiType().getIdAndValue()).setEnabled(false));
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
		form.add(new Label("limitBean.status", "INITIAL").setEnabled(false));
		
		
		Button submitButton = new Button("submitButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				handleCreateLimitBean();
			};
		};
		form.add(submitButton);

		Button backButton = new Button("backButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(new ElimitCreatePage(limitBean));
			};
		};

		form.add(backButton);

		Button cancelButton = new Button("cancelButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(new ElimitPage());
			};
		};

		form.add(cancelButton);

		add(form);
	}

	
	
	/**
	 * This method handles the Creation of Fee Bean.
	 */
	private void handleCreateLimitBean() {
			
		LimitExType limit = ConverterUtils.convertToLimitExType(limitBean);
		
		try {
			limit.setCreator(basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			
			IsLimitAlreadyDefinedRequest req = this.mobBasePage.getNewMobiliserRequest(IsLimitAlreadyDefinedRequest.class);
			
			req.setPi(limit.getPiId());
			req.setPiType(limit.getPiType());
			req.setCustomerType(limit.getCustomerType());
			req.setCustomer(limit.getCustomerId());
			req.setUseCaseId(limit.getUseCase());
			
			IsLimitAlreadyDefinedResponse resp = this.mobBasePage.limitExClient.isLimitExAlreadyDefined(req);
			
			if(resp.getStatus().getValue().equals("FALSE")){
				CreateLimitExRequest request = this.mobBasePage.getNewMobiliserRequest(CreateLimitExRequest.class);
				request.setData(limit);
				CreateLimitExResponse response = this.mobBasePage.limitExClient.create(request);
				if (this.mobBasePage.evaluateBankPortalMobiliserResponse(response)) {
					this.mobBasePage.getWebSession().info(getLocalizer().getString("create.success.waiting.approval", this));
					setResponsePage(new ElimitCreateSuccessPage(limitBean));
				} else {
					error(MobiliserUtils.errorMessage(response.getStatus().getCode(), response.getStatus().getValue(), getLocalizer(), this));
				}
				
			}else{
				this.mobBasePage.getWebSession().error(getLocalizer().getString("Limit is already defined", this));
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			this.mobBasePage.getWebSession().error(getLocalizer().getString("error.exception", this));
			LOG.error("Exception occured while creating the fees  ===> ", e);
		}
		
	}
	
	/**
	 * This method handles the specific error message.
	 */
	private void handleSpecificErrorMessage(final int errorCode) {
		// Specific error message handling
		final String messageKey = "error." + errorCode;
		String message = getLocalizer().getString(messageKey, this);
		// Generice error messages
		if (messageKey.equals(message)) {
			message = getLocalizer().getString("error.create.Limit", this);
		}
		this.mobBasePage.getWebSession().error(message);
	}
	
	
	public boolean isLimitExAlreadyDefine(LimitExType req, IsLimitAlreadyDefinedResponse resp){
		
		if(resp.getStatus().getValue().equals("FALSE") && req.getUseCase()!=null){
			
		}
		
		
		return false;
	}
	
	

}
