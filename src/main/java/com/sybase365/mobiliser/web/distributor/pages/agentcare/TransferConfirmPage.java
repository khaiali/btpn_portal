package com.sybase365.mobiliser.web.distributor.pages.agentcare;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.web.beans.TransactionBean;
import com.sybase365.mobiliser.web.common.panels.AddFundsPanel;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

@AuthorizeInstantiation(Constants.PRIV_MANAGE_AGENTS)
public class TransferConfirmPage extends AgentCareMenuGroup {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(TransferConfirmPage.class);

    private WebMarkupContainer transformConfirmDiv;
    private WebMarkupContainer transformFinishDiv;

    public TransferConfirmPage(TransactionBean txnBean) {
	super();
	this.txnBean = txnBean;
	initPageComponents();
    }

    private Long recipient;
    private String orderId;

    private TransactionBean txnBean;

    protected void initPageComponents() {

	transformConfirmDiv = addTransformConfirmDiv();
	transformFinishDiv = addTransformFinishDiv();
	setContainerVisibilities(true, false);

    }

    @SuppressWarnings("unchecked")
    private WebMarkupContainer addTransformConfirmDiv() {
	transformConfirmDiv = new WebMarkupContainer("transformConfirmDiv");
	Form<?> transferConfirmForm = new Form("transferConfirmForm",
		new CompoundPropertyModel<TransferConfirmPage>(this));
	transferConfirmForm.add(new Label("creditAmount"));
	transferConfirmForm.add(new Label("feeAmount"));
	transferConfirmForm.add(new Label("debitAmount"));
	transferConfirmForm.add(new Label("recipient"));
	transferConfirmForm.add(new Label("txnBean.orderId"));
	transferConfirmForm.add(new Button("transferBack") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		setResponsePage(AgentTransferMoneyPage.class);
	    };
	}.setDefaultFormProcessing(false));
	transferConfirmForm.add(new Button("transferConfirm") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		tranferConfirm();
	    };
	});
	transferConfirmForm.add(new FeedbackPanel("errorMessages"));
	transformConfirmDiv.add(transferConfirmForm);

	add(transformConfirmDiv);
	return transformConfirmDiv;
    }

    @SuppressWarnings("unchecked")
    private WebMarkupContainer addTransformFinishDiv() {

	transformFinishDiv = new WebMarkupContainer("transformFinishDiv");
	transformFinishDiv.setOutputMarkupId(true);
	transformFinishDiv.setOutputMarkupPlaceholderTag(true);

	Form<?> transferFinishForm = new Form("transferFinishForm",
		new CompoundPropertyModel<TransferConfirmPage>(this));

	WebMarkupContainer labels = new WebMarkupContainer("dataContainer",
		new CompoundPropertyModel<AddFundsPanel>(this));

	labels.add(new Label("txnBean.authCode"));
	labels.add(new Label("debitAmount"));
	labels.add(new Label("recipient"));
	labels.add(new Label("txnBean.txnId"));
	transferFinishForm.add(labels);

	Label successLbl = new Label("successMsg", getLocalizer().getString(
		"transferFinish.success", this));

	transferFinishForm.add(successLbl);

	Label pendingLbl = new Label("pendingApprovalMsg", getLocalizer()
		.getString("pendingApproval.msg", this));
	transferFinishForm.add(pendingLbl);

	if (PortalUtils.exists(getTxnBean())
		&& getTxnBean().getStatusCode() == Constants.TXN_STATUS_PENDING_APPROVAL) {
	    labels.setVisible(false);
	    successLbl.setVisible(false);
	} else
	    pendingLbl.setVisible(false);

	transferFinishForm.add(new Button("transferFinished") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {

		setResponsePage(new AgentEditPage("edit"));
	    };
	});
	transferFinishForm.add(new FeedbackPanel("errorMessages"));
	transformFinishDiv.add(transferFinishForm);

	addOrReplace(transformFinishDiv);
	return transformFinishDiv;
    }

    protected void tranferConfirm() {

	try {
	    // AuthorisationResponse aurhResp = handleAuthorisation();

	    if (handleTransaction(getTxnBean())) {
		setContainerVisibilities(false, true);

	    } else {
		if (getTxnBean().getStatusCode() == Constants.TXN_STATUS_PENDING_APPROVAL) {
		    addTransformFinishDiv();
		    setContainerVisibilities(false, true);
		}
	    }

	} catch (Exception e) {
	    LOG.error("Error in cashIn Authorisation", e);
	    error(getLocalizer().getString("preauthorization.continue.error",
		    this));
	}
    }

    private void setContainerVisibilities(boolean viewTransformConfirmDiv,
	    boolean viewTransformFinishDiv) {
	this.transformConfirmDiv.setVisible(viewTransformConfirmDiv);
	if (viewTransformFinishDiv) {
	    refreshSVABalance();
	}
	this.transformFinishDiv.setVisible(viewTransformFinishDiv);
    }

    public Long getRecipient() {
	this.recipient = getMobiliserWebSession().getCustomer().getId();
	return recipient;
    }

    public void setRecipient(Long recipient) {
	this.recipient = recipient;
    }

    public String getOrderId() {
	return orderId;
    }

    public void setOrderId(String orderId) {
	this.orderId = orderId;
    }

    public TransactionBean getTxnBean() {
	return txnBean;
    }

    public final void setTxnBean(TransactionBean txnBean) {
	this.txnBean = txnBean;
    }

    public String getFeeAmount() {
	return convertAmountToStringWithCurrency(getTxnBean() != null ? getTxnBean()
		.getFeeAmount() : 0L);
    }

    public String getDebitAmount() {
	return convertAmountToStringWithCurrency(getTxnBean() != null ? getTxnBean()
		.getDebitAmount() : 0L);
    }

    public String getCreditAmount() {

	return convertAmountToStringWithCurrency(getTxnBean() != null ? getTxnBean()
		.getCreditAmount() : 0L);
    }

}
