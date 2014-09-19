package com.sybase365.mobiliser.web.demomerchant.pages;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.money.contract.v5_0.transaction.GetWebTxnDetailsRequest;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.GetWebTxnDetailsResponse;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.DetailedWebTransaction;
import com.sybase365.mobiliser.web.checkout.exceptions.ServicesException;

@SuppressWarnings("all")
public class TransactionStatusPage extends BaseDemoMerchantPage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(TransactionStatusPage.class);

    private boolean autoCapture;
    private long systemId;

    public TransactionStatusPage() {
	super();
	LOG.info("Created new TransactionStatusPage");
	initPageComponents();
    }

    public TransactionStatusPage(PageParameters params) {
	super();
	this.systemId = Long.valueOf(params.getString("txnId"));
	LOG.info("Created new TransactionStatusPage");
	initPageComponents();
    }

    protected void initPageComponents() {

	this.autoCapture = getMobiliserWebSession().isAutoCapture();
	DetailedWebTransaction txn = null;
	try {
	    txn = getTransactionDetail();

	} catch (Exception e) {
	    LOG.error("An error occurred while getting the transaction status",
		    e);
	    error(getLocalizer().getString("get.txn.status.error", this));
	    return;
	}

	Label inCompleteStatusMsg = new Label("inCompleteStatusMsg",
		getLocalizer().getString("inCompleteStatus_msg", this));
	Label status0 = new Label("status0", getLocalizer().getString(
		"status0_msg", this));

	Label status2541 = new Label("status2541", getLocalizer().getString(
		"status2541_msg", this));

	Label status2699 = new Label("status2699", getLocalizer().getString(
		"status2699_msg", this));

	Label status2853 = new Label("status2853", getLocalizer().getString(
		"status2853_msg", this));

	Label statusOther = new Label("statusOther", getLocalizer().getString(
		"statusOther_msg", this));

	Label amount = new Label("amount", formateAmount(txn.getAmount()
		.getValue() / 100));

	final Label txnSystemId = new Label("txnSystemId", String.valueOf(txn
		.getId()));

	final Label txnStatus = new Label("txnStatus", String.valueOf(txn
		.getStatus()));

	final Label txnErrorCode = new Label("txnErrorCode", String.valueOf(txn
		.getErrorCode()));

	final Label txnText = new Label("txnText", txn.getText());

	Form doCaptureForm = new Form("doCaptureForm",
		new CompoundPropertyModel<TransactionStatusPage>(this));
	Button doCaptureButton = new Button("doCapture") {

	    @Override
	    public void onSubmit() {

	    }

	};
	doCaptureButton.setVisible(!this.autoCapture);
	doCaptureForm.add(doCaptureButton);
	add(doCaptureForm);

	final WebMarkupContainer moreInfo = new WebMarkupContainer("moreInfo");
	moreInfo.setVisible(false);
	moreInfo.add(txnSystemId);
	moreInfo.add(txnStatus);
	moreInfo.add(txnErrorCode);
	moreInfo.add(txnText);

	add(new Link("showDetails") {
	    @Override
	    public void onClick() {
		moreInfo.setVisible(true);

	    }
	}.setVisible(txn.getErrorCode() != 0));

	add(inCompleteStatusMsg.setVisible(false));
	add(status0.setVisible(false));
	add(status2541.setVisible(false));
	add(status2699.setVisible(false));
	add(status2853.setVisible(false));
	add(statusOther.setVisible(false));
	add(amount);
	add(moreInfo);
	LOG.debug("--- Getting Status ---");

	// toggling visibility

	if ((txn.getStatus() == 20 || txn.getStatus() != 30)
		&& txn.getErrorCode() == 0) {
	    inCompleteStatusMsg.setVisible(true);
	} else {
	    if (txn.getErrorCode() == 0)
		status0.setVisible(true);
	    else if (txn.getErrorCode() == 2541)
		status2541.setVisible(true);
	    else if (txn.getErrorCode() == 2699)
		status2699.setVisible(true);
	    else if (txn.getErrorCode() == 2853)
		status2853.setVisible(true);
	    else
		statusOther.setVisible(true);

	}

    }

    private DetailedWebTransaction getTransactionDetail()
	    throws ServicesException, Exception {

	GetWebTxnDetailsRequest request = getNewMobiliserRequest(GetWebTxnDetailsRequest.class);
	request.setSystemId(this.systemId);
	GetWebTxnDetailsResponse response = wsTransactionsClient
		.getWebTxnDetails(request);
	if (!evaluateMobiliserResponse(response)) {
	    throw new ServicesException("Service returned an error:"
		    + response.getStatus().getValue(), response.getStatus());
	}
	// appending the transaction id in the uRL
	response.getTransaction().setReturnUrl(
		response.getTransaction().getReturnUrl() + "&txnId="
			+ this.systemId);
	return response.getTransaction();

    }

    @Override
    protected void initOwnPageComponents() {

    }

    private String formateAmount(long amount) {

	return amount + " " + "\u20AC";

    }

}
