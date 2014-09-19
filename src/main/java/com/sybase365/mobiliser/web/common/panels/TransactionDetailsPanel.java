package com.sybase365.mobiliser.web.common.panels;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.money.contract.v5_0.transaction.AuthorisationCancel;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.AuthorisationCancelResponse;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.Capture;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.CaptureCancel;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.CaptureCancelResponse;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.CaptureResponse;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.ContinuePendingTransactionRequest;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.ContinuePendingTransactionResponse;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.DetailedSubTransaction;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.DetailedTransaction;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.Transaction;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.VatAmount;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.common.model.ITransactionDetailViewer;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class TransactionDetailsPanel extends Panel {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(TransactionDetailsPanel.class);

    private DetailedTransaction transactionDetailsBean;
    private MobiliserBasePage mobBasePage;
    private WebPage backPage;
    private Form<?> txnDetailsForm;
    private boolean isPendingTxn;
    private String taskId;

    public TransactionDetailsPanel(String id, DetailedTransaction txnBean,
	    MobiliserBasePage mobBasePage, WebPage backPage, String taskId) {

	super(id);

	this.transactionDetailsBean = txnBean;
	this.mobBasePage = mobBasePage;
	this.backPage = backPage;
	if (txnBean.getStatus() == Constants.TXN_STATUS_PENDING_APPROVAL
		&& txnBean.getErrorCode() == 0)
	    isPendingTxn = true;
	this.taskId = taskId;
	LOG.info("Created new TransactionDetailsPanel");

	constructPanel();
    }

    protected void constructPanel() {

	txnDetailsForm = new Form("txnDetailsForm",
		new CompoundPropertyModel<TransactionDetailsPanel>(this));

	txnDetailsForm.add(new Label("transactionDetailsBean.id"));
	txnDetailsForm
		.add(new Label(
			"transactionDetailsBean.amount",
			mobBasePage
				.convertAmountToStringWithCurrency(transactionDetailsBean
					.getAmount())));
	txnDetailsForm.add(new Label("transactionDetailsBean.useCase",
		mobBasePage.getDisplayValue(String
			.valueOf(getTransactionDetailsBean().getUseCase()),
			Constants.RESOURCE_BUNDLE_USE_CASES)));

	txnDetailsForm.add(new Label("transactionDetailsBean.authCode"));

	txnDetailsForm.add(new Label("transactionDetailsBean.text"));
	txnDetailsForm.add(new Label("transactionDetailsBean.creationDate",
		PortalUtils.getFormattedDate(getTransactionDetailsBean()
			.getCreationDate(), mobBasePage
			.getMobiliserWebSession().getLocale())));
	txnDetailsForm
		.add(new Label("transactionDetailsBean.payerDisplayName"));
	txnDetailsForm
		.add(new Label("transactionDetailsBean.payeeDisplayName"));
	txnDetailsForm
		.add(new Label("transactionDetailsBean.status", mobBasePage
			.getDisplayValue(String
				.valueOf(getTransactionDetailsBean()
					.getStatus()),
				Constants.RESOURCE_BUNDLE_TRANSACTION_STATUS)));

	txnDetailsForm.add(new Label("transactionDetailsBean.errorCode",
		getTransactionDetailsBean().getErrorCode() == 0 ? ""
			: (mobBasePage.getDisplayValue(String
				.valueOf(getTransactionDetailsBean()
					.getErrorCode()),
				Constants.RESOURCE_BUNDLE_ERROR_CODES))));

	txnDetailsForm.add(new Label("transactionDetailsBean.cancelled")
		.add(new ErrorIndicator()));

	List<DetailedSubTransaction> subTxnList = getTransactionDetailsBean()
		.getSubTxns();

	ListView<DetailedSubTransaction> lv = new ListView<DetailedSubTransaction>(
		"subTransactionsDiv", subTxnList) {

	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void populateItem(ListItem item) {

		DetailedSubTransaction subTxn = (DetailedSubTransaction) item
			.getModelObject();

		item.add(new Label("subTxnId", getLocalizer().getString(
			"txnDetails.subTxn.txnId", this)));
		item.add(new Label("transactionDetailsBean.subTxns.id", String
			.valueOf(subTxn.getId())));

		item.add(new Label("subTxnType", getLocalizer().getString(
			"txnDetails.subTxn.txnType", this)));

		item.add(new Label("transactionDetailsBean.subTxns.type",
			mobBasePage.getDisplayValue(
				String.valueOf(subTxn.getType()),
				Constants.RESOURCE_BUNDLE_SUB_TXN_TYPES)));

		item.add(new Label("subTxnDate", getLocalizer().getString(
			"txnDetails.subTxn.date", this)));
		item.add(new Label(
			"transactionDetailsBean.subTxns.creationDate",
			PortalUtils.getFormattedDate(subTxn.getCreationDate(),
				mobBasePage.getMobiliserWebSession()
					.getLocale())));

		item.add(new Label("subTxnAmount", getLocalizer().getString(
			"txnDetails.subTxn.amount", this)));
		item.add(new Label("transactionDetailsBean.subTxns.subAmount",
			mobBasePage.convertAmountToStringWithCurrency(subTxn
				.getSubAmount())));

		item.add(new Label("subTxnFeePayer", getLocalizer().getString(
			"txnDetails.subTxn.feePayer", this)));
		item.add(new Label(
			"transactionDetailsBean.subTxns.payerFeeAmount",
			mobBasePage.convertAmountToStringWithCurrency(subTxn
				.getPayerFeeAmount())));

		item.add(new Label("subTxnFeePayee", getLocalizer().getString(
			"txnDetails.subTxn.feePayee", this)));
		item.add(new Label(
			"transactionDetailsBean.subTxns.payeeFeeAmount",
			mobBasePage.convertAmountToStringWithCurrency(subTxn
				.getPayeeFeeAmount())));

		item.add(new Label("subTxnVatPayer", getLocalizer().getString(
			"txnDetails.subTxn.vatPayer", this)));
		item.add(new Label(
			"transactionDetailsBean.subTxns.payerVatAmount",
			mobBasePage.convertAmountToStringWithCurrency(subTxn
				.getPayerVatAmount())));

		item.add(new Label("subTxnVatPayee", getLocalizer().getString(
			"txnDetails.subTxn.vatPayee", this)));
		item.add(new Label(
			"transactionDetailsBean.subTxns.payeeVatAmount",
			mobBasePage.convertAmountToStringWithCurrency(subTxn
				.getPayeeVatAmount())));

		item.add(new Label("subTxnErrorCode", getLocalizer().getString(
			"txnDetails.subTxn.errorCode", this)));

		item.add(new Label("transactionDetailsBean.subTxns.errorCode",
			subTxn.getErrorCode() == 0 ? "" : (mobBasePage
				.getDisplayValue(
					String.valueOf(subTxn.getErrorCode()),
					Constants.RESOURCE_BUNDLE_ERROR_CODES))));

	    }
	};

	txnDetailsForm.add(lv);

	txnDetailsForm.add(new Button("showTransactions") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		setResponsePage(backPage);
	    };
	}.setVisible(!isPendingTxn));

	txnDetailsForm
		.add(new Button("captureCancel") {
		    private static final long serialVersionUID = 2L;

		    @Override
		    public void onSubmit() {
			captureCancelTransaction();
		    };
		}.setVisible(transactionDetailsBean.getStatus() == Constants.TXN_STATUS_CAPTURED
			&& !isPendingTxn));

	txnDetailsForm.add(new Button("approve") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		approve(true);
	    };
	}.setVisible(isPendingTxn));

	txnDetailsForm.add(new Button("reject") {
	    private static final long serialVersionUID = 2L;

	    @Override
	    public void onSubmit() {
		approve(false);
	    };
	}.setVisible(isPendingTxn));

	txnDetailsForm
		.add(new Button("autoriseCancel") {
		    private static final long serialVersionUID = 2L;

		    @Override
		    public void onSubmit() {
			autoriseCancelTransaction();
		    };
		}.setVisible(transactionDetailsBean.getStatus() == Constants.TXN_STATUS_AUTHORISED));

	txnDetailsForm
		.add(new Button("captureTransactions") {
		    private static final long serialVersionUID = 2L;

		    @Override
		    public void onSubmit() {
			captureTransaction();
		    }
		}.setVisible(transactionDetailsBean.getStatus() == Constants.TXN_STATUS_AUTHORISED));

	txnDetailsForm.add(new FeedbackPanel("errorMessages"));

	add(txnDetailsForm);

	// TODO - need to call webservice

    }

    private void reloadTransaction() {
	if (backPage instanceof ITransactionDetailViewer) {
	    setResponsePage(((ITransactionDetailViewer) backPage)
		    .getTransactionDetailViewer(transactionDetailsBean, null,
			    backPage));
	}
    }

    protected void autoriseCancelTransaction() {
	// check if user is allowed to cancel the transaction
	Calendar cal = new GregorianCalendar();
	cal.setTimeInMillis(System.currentTimeMillis()
		- (mobBasePage.getConfiguration().getTxnCancelTimeBuffer() * 1000L));
	if (!(getTransactionDetailsBean().getCreationDate()
		.toGregorianCalendar().after(cal) || mobBasePage
		.getMobiliserWebSession().hasPrivilege(
			Constants.PRIV_TXN_CANCEL))) {
	    error(getLocalizer()
		    .getString("ERROR.CANCEL_TXN_NOT_ALLOWED", this));
	    return;
	}
	try {
	    AuthorisationCancel txnCancelReq = mobBasePage
		    .getNewMobiliserRequest(AuthorisationCancel.class);

	    Transaction transaction = new Transaction();
	    transaction.setSystemId(getTransactionDetailsBean().getId());

	    txnCancelReq.setReferenceTransaction(transaction);

	    AuthorisationCancelResponse txnCancelRes = mobBasePage.wsAuthorisationCancelClient
		    .authorisationCancel(txnCancelReq);

	    if (!mobBasePage.evaluateMobiliserResponse(txnCancelRes)) {
		String responseStr = PortalUtils.exists(txnCancelRes
			.getStatus()) ? (txnCancelRes.getStatus().getCode()
			+ " -> " + txnCancelRes.getStatus().getValue())
			: "No response status returned.";
		LOG.warn(
			"# An error occurred while AuthorisationCancel transaction[{}] :",
			getTransactionDetailsBean().getId(), responseStr);

		return;

	    } else {
		LOG.info(
			"# AuthorisationCancel transaction[{}] successfully finished",
			getTransactionDetailsBean().getId());

		info(getLocalizer().getString("MESSAGE.CANCEL_SUCCESS", this));
		reloadTransaction();
	    }

	} catch (Exception e) {
	    LOG.error("# An error occurred while cancelling transaction", e);
	    error(getLocalizer().getString("cancel.transaction.error", this));
	}
    }

    protected void captureTransaction() {
	try {
	    Capture txnCaptureReq = mobBasePage
		    .getNewMobiliserRequest(Capture.class);

	    VatAmount vatAmnt = new VatAmount();
	    vatAmnt.setCurrency(mobBasePage.getConfiguration().getCurrency());
	    vatAmnt.setValue(getTransactionDetailsBean().getAmount().getValue());

	    txnCaptureReq.setAmount(vatAmnt);

	    Transaction transaction = new Transaction();
	    transaction.setSystemId(getTransactionDetailsBean().getId());
	    txnCaptureReq.setReferenceTransaction(transaction);

	    CaptureResponse txnCaptureRes = mobBasePage.wsCaptureClient
		    .capture(txnCaptureReq);

	    if (!mobBasePage.evaluateMobiliserResponse(txnCaptureRes)) {
		String responseStr = PortalUtils.exists(txnCaptureRes
			.getStatus()) ? (txnCaptureRes.getStatus().getCode()
			+ " -> " + txnCaptureRes.getStatus().getValue())
			: "No response status returned.";
		LOG.warn("# An error occurred while capture transaction[{}] :",
			getTransactionDetailsBean().getId(), responseStr);

		return;

	    } else {
		LOG.info("# capture transaction[{}] successfully finished",
			getTransactionDetailsBean().getId());

		info(getLocalizer().getString("MESSAGE.CAPTURE_SUCCESS", this));
		reloadTransaction();
	    }

	} catch (Exception e) {
	    LOG.error("# An error occurred while cancelling transaction", e);
	    error(getLocalizer().getString("capture.transaction.error", this));
	}
    }

    protected void captureCancelTransaction() {

	// check if user is allowed to cancel the transaction
	Calendar cal = new GregorianCalendar();
	cal.setTimeInMillis(System.currentTimeMillis()
		- (mobBasePage.getConfiguration().getTxnCancelTimeBuffer() * 1000L));
	if (!(getTransactionDetailsBean().getCreationDate()
		.toGregorianCalendar().after(cal) || mobBasePage
		.getMobiliserWebSession().hasPrivilege(
			Constants.PRIV_TXN_CANCEL))) {
	    error(getLocalizer()
		    .getString("ERROR.CANCEL_TXN_NOT_ALLOWED", this));
	    return;
	}
	try {
	    CaptureCancel txnCancelReq = mobBasePage
		    .getNewMobiliserRequest(CaptureCancel.class);

	    VatAmount vatAmnt = new VatAmount();
	    vatAmnt.setCurrency(mobBasePage.getConfiguration().getCurrency());
	    vatAmnt.setValue(getTransactionDetailsBean().getAmount().getValue());

	    txnCancelReq.setAmount(vatAmnt);

	    Transaction transaction = new Transaction();
	    transaction.setSystemId(getTransactionDetailsBean().getId());

	    txnCancelReq.setReferenceTransaction(transaction);

	    CaptureCancelResponse txnCancelRes = mobBasePage.wsCaptureCancelClient
		    .captureCancel(txnCancelReq);

	    if (!mobBasePage.evaluateMobiliserResponse(txnCancelRes)) {
		String responseStr = PortalUtils.exists(txnCancelRes
			.getStatus()) ? (txnCancelRes.getStatus().getCode()
			+ " -> " + txnCancelRes.getStatus().getValue())
			: "No response status returned.";
		LOG.warn(
			"# An error occurred while captureCancel transaction[{}] :",
			getTransactionDetailsBean().getId(), responseStr);

		return;

	    } else {
		LOG.info(
			"# captureCancel transaction[{}] successfully finished",
			getTransactionDetailsBean().getId());

		info(getLocalizer().getString("MESSAGE.CANCEL_SUCCESS", this));
		reloadTransaction();
	    }

	} catch (Exception e) {
	    LOG.error("# An error occurred while cancelling transaction", e);
	    error(getLocalizer().getString("cancel.transaction.error", this));
	}

    }

    private void approve(boolean bApprove) {
	LOG.debug("# TransactionDetailsPanel.approve()");
	ContinuePendingTransactionResponse response;
	String taskId = null;

	try {

	    ContinuePendingTransactionRequest request = mobBasePage
		    .getNewMobiliserRequest(ContinuePendingTransactionRequest.class);
	    request.setApprove(bApprove);
	    request.setTxnId(this.transactionDetailsBean.getId());
	    taskId = this.taskId;

	    if (!PortalUtils.exists(taskId)) {
		return;
	    }
	    request.setTaskId(taskId);

	    response = mobBasePage.wsTransactionsClient
		    .continuePendingAuthorisation(request);

	    if (!mobBasePage.evaluateMobiliserResponse(response)) {
		LOG.warn(
			"# An error occurred while approving/rejecting transaction [{}] :",
			taskId);
		return;
	    } else {
		if (bApprove) {
		    mobBasePage.getMobiliserWebSession().info(
			    getLocalizer().getString(
				    "txn.approved.successfull", this));
		} else {
		    mobBasePage.getMobiliserWebSession().info(
			    getLocalizer().getString(
				    "txn.rejected.successfull", this));
		}

	    }

	} catch (Exception e) {
	    LOG.error(
		    "# An error occurred while approving/rejecting transaction [{}] :",
		    taskId);

	    if (bApprove)
		error(getLocalizer().getString("txn.approve.error", this));
	    else
		error(getLocalizer().getString("txn.reject.error", this));

	}

	setResponsePage(backPage.getClass());
    }

    public void setTransactionDetailsBean(
	    DetailedTransaction transactionDetailsBean) {
	this.transactionDetailsBean = transactionDetailsBean;
    }

    public DetailedTransaction getTransactionDetailsBean() {
	return transactionDetailsBean;
    }

}
