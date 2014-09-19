package com.sybase365.mobiliser.web.btpn.bank.common.panels;

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

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.transactionreversal.TransactionReversalDetail;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.transactionreversal.GetTransactionDetailRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.transactionreversal.GetTransactionDetailResponse;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.transactionreversal.PerformTransactionReversalRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.transactionreversal.PerformTransactionReversalResponse;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.TransactionReversalBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.common.panels.AttachmentsPanel;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the TransactionReversalPanel page for bank portals.
 * 
 * @author Narasa Reddy
 */
public class TransactionReversalPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(TransactionReversalPanel.class);

	private FeedbackPanel feedBack;

	protected BtpnMobiliserBasePage basePage;

	protected TransactionReversalBean txnBean;

	public TransactionReversalPanel(String id, BtpnBaseBankPortalSelfCarePage basePage, TransactionReversalBean txnBean) {
		this(id, basePage, null, txnBean);
	}

	public TransactionReversalPanel(String id, BtpnBaseBankPortalSelfCarePage basePage,
		AttachmentsPanel attachmentsPanel, TransactionReversalBean txnBean) {
		super(id);
		this.basePage = basePage;
		this.txnBean = txnBean;
		constructPanel();
	}

	protected void constructPanel() {
		final Form<TransactionReversalPanel> form = new Form<TransactionReversalPanel>("txnReversalForm",
			new CompoundPropertyModel<TransactionReversalPanel>(this));
		feedBack = new FeedbackPanel("errorMessages");
		feedBack.setOutputMarkupId(true);
		feedBack.setOutputMarkupPlaceholderTag(true);
		form.add(feedBack);

		final WebMarkupContainer txnContainer = new WebMarkupContainer("txnContainer");
		txnContainer.setOutputMarkupId(true);
		txnContainer.setOutputMarkupPlaceholderTag(true);
		txnContainer.setVisible(false);

		form.add(new TextField<String>("txnBean.transactionID").setRequired(true).add(new ErrorIndicator()));
		txnContainer.add(new TextField<String>("txnBean.transactionName").setRequired(true).add(new ErrorIndicator()));
		txnContainer.add(new TextField<String>("txnBean.transactionDate").setRequired(true).add(new ErrorIndicator()));
		txnContainer.add(new TextField<String>("txnBean.mobileNumber").add(new ErrorIndicator()));
		txnContainer
			.add(new TextField<String>("txnBean.transactionAmount").setRequired(true).add(new ErrorIndicator()));

		form.add(txnContainer);

		form.add(new AjaxButton("submitButton") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				try {
					if (!txnBean.isMsisdn()) {
						txnBean = getTransactionDetails(Long.valueOf(txnBean.getTransactionID()));
						if (txnBean.isSuccess()) {
							txnContainer.setVisible(true);
							target.addComponent(txnContainer);
							txnBean.setMsisdn(true);
						}
						target.addComponent(feedBack);
					} else if (txnBean.isMsisdn()) {
						if (performTransactionReversal(txnBean)) {
							info(getLocalizer().getString("txnreversal.add.success", TransactionReversalPanel.this));
							txnContainer.setVisible(false);
							target.addComponent(txnContainer);
							target.addComponent(feedBack);
							txnBean.setMsisdn(false);
							target.addComponent(form);
						}
						target.addComponent(feedBack);
					}
				} catch (Exception e) {
					LOG.error("#An error occurred while calling transaction reversal", e);
					target.addComponent(feedBack);
				}
			};

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.addComponent(feedBack);
				super.onError(target, form);
			}
		});

		add(form);
	}

	/**
	 * calling getTransactionDetail service from transaction reversal end point
	 */
	private TransactionReversalBean getTransactionDetails(Long txnId) {
		GetTransactionDetailResponse response = null;
		TransactionReversalBean bean = new TransactionReversalBean();
		try {
			final GetTransactionDetailRequest request = basePage
				.getNewMobiliserRequest(GetTransactionDetailRequest.class);
			request.setTransactionId(txnId);
			response = basePage.getTransactionReversalClient().getTransactionDetail(request);
			if (basePage.evaluateBankPortalMobiliserResponse(response)) {
				TransactionReversalDetail txnReversalBean = response.getTransactionreversal();
				bean.setMobileNumber(txnReversalBean.getMsisdn());
				bean.setTransactionAmount(String.valueOf(txnReversalBean.getTransactionAmount()));
				bean.setTransactionDate(PortalUtils.getFormattedDateTime(txnReversalBean.getTransactionDate(), basePage
					.getMobiliserWebSession().getLocale()));
				bean.setTransactionID(String.valueOf(txnReversalBean.getTransactionId()));
				bean.setTransactionName(txnReversalBean.getTransactionName());
				bean.setUseCase(String.valueOf(txnReversalBean.getUseCaseId()));
				bean.setTxnDate(txnReversalBean.getTransactionDate());
				bean.setSuccess(true);
			} else {
				error(response.getStatus().getValue());
				bean.setSuccess(false);
			}
		} catch (Exception ex) {
			LOG.error("#An error occurred while calling getTransactionDetail service", ex);
			this.basePage.error(getLocalizer().getString("error.exception", this.basePage));
		}
		return bean;
	}

	/**
	 * calling performTransactionReversal service from transaction reversal end point
	 */
	private boolean performTransactionReversal(TransactionReversalBean txnBean) {
		PerformTransactionReversalResponse performTxnReversalResponse = null;
		boolean txnReversalStatus = false;
		try {
			final PerformTransactionReversalRequest request = basePage
				.getNewMobiliserRequest(PerformTransactionReversalRequest.class);
			long customerId = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId();
			request.setMakerId(customerId);
			TransactionReversalDetail txnReversalBean = new TransactionReversalDetail();
			txnReversalBean.setMsisdn(txnBean.getMobileNumber());
			txnReversalBean.setTransactionAmount(Long.valueOf(txnBean.getTransactionAmount()));
			txnReversalBean.setTransactionDate(txnBean.getTxnDate());
			txnReversalBean.setTransactionId(Long.valueOf(txnBean.getTransactionID()));
			txnReversalBean.setTransactionName(txnBean.getTransactionName());
			txnReversalBean.setUseCaseId(Integer.valueOf(txnBean.getUseCase()));
			request.setTransactionReversal(txnReversalBean);
			performTxnReversalResponse = basePage.getTransactionReversalClient().performTransactionReversal(request);
			if (basePage.evaluateBankPortalMobiliserResponse(performTxnReversalResponse)
					&& performTxnReversalResponse.getStatus().getCode() == BtpnConstants.STATUS_CODE) {
				txnReversalStatus = true;
			} else {
				handleSpecificErrorMessage(performTxnReversalResponse.getStatus().getCode());
			}
		} catch (Exception ex) {
			LOG.error("#An error occurred while calling performTransactionReversal service", ex);
			this.basePage.error(getLocalizer().getString("error.exception", this.basePage));
		}
		return txnReversalStatus;
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
			message = getLocalizer().getString("error.perform.reversal", this);
		}
		error(message);
	}
	public TransactionReversalBean getTxnBean() {
		return txnBean;
	}

	public void setTxnBean(TransactionReversalBean txnBean) {
		this.txnBean = txnBean;
	}
}
