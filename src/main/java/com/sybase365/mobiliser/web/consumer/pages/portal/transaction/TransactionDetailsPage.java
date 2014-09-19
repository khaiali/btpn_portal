package com.sybase365.mobiliser.web.consumer.pages.portal.transaction;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.framework.contract.v5_0.base.MoneyAmount;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.SimpleTransaction;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

@AuthorizeInstantiation(Constants.PRIV_CONSUMER_TXN_HISTORY)
public class TransactionDetailsPage extends MobiliserBasePage {

    private static final long serialVersionUID = 1L;

    private SimpleTransaction txnHistoryBean;
    private long customerId;

    public SimpleTransaction getTxnHistoryBean() {
	return txnHistoryBean;
    }

    public TransactionDetailsPage(SimpleTransaction txnHistoryBean,
	    Long customerId) {
	super();
	this.txnHistoryBean = txnHistoryBean;
	if (customerId != null)
	    this.customerId = customerId.longValue();
	initPageComponents();
    }

    protected void initPageComponents() {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	WebMarkupContainer labels = new WebMarkupContainer("dataContainer",
		new CompoundPropertyModel<TransactionDetailsPage>(this));
	labels.add(new Label("txnHistoryBean.creationDate", PortalUtils
		.getFormattedDate(getTxnHistoryBean().getCreationDate(),
			getMobiliserWebSession().getLocale())));
	labels.add(new Label("txnHistoryBean.creationTime", PortalUtils
		.getFormattedTime(getTxnHistoryBean().getCreationDate(),
			getMobiliserWebSession().getLocale())));
	labels.add(new Label("txnHistoryBean.useCase", getDisplayValue(String.valueOf(getTxnHistoryBean().getUseCase()),Constants.RESOURCE_BUNDLE_USE_CASES)));
	labels.add(new Label("txnHistoryBean.errorCode"));
	labels.add(new Label("txnHistoryBean.authCode"));
	long payerId = getTxnHistoryBean().getPayerId();

	String payerName = getTxnHistoryBean().getPayerDisplayName();
	String payeeName = getTxnHistoryBean().getPayeeDisplayName();
	String displayName;

	if (customerId != payerId) {
	    displayName = payerName;
	} else {
	    displayName = payeeName;

	}
	labels.add(new Label("txnHistoryBean.participantName", displayName));
	labels.add(new Label("txnHistoryBean.text"));
	labels.add(new Label("txnHistoryBean.amount", getTransactionAmount(
		getTxnHistoryBean(), customerId)));
	labels.add(new Label("txnHistoryBean.fee",
		getFeeAmount(getTxnHistoryBean())));
	add(labels);

    }

    private String getFeeAmount(SimpleTransaction txn) {

	long amt = 0;
	if (customerId == txn.getPayerId() && customerId != txn.getPayeeId()) {
	    amt = txn.getPayerAmount().getValue() - txn.getAmount().getValue();
	} else if (customerId == txn.getPayeeId()
		&& customerId != txn.getPayerId()) {
	    amt = txn.getAmount().getValue() - txn.getPayeeAmount().getValue();
	} else if (customerId == txn.getPayeeId()
		&& customerId == txn.getPayerId() && txn.getPayerPiType() == 0
		&& txn.getPayeePiType() != 0) {
	    amt = txn.getPayerAmount().getValue() - txn.getAmount().getValue();
	} else if (customerId == txn.getPayeeId()
		&& customerId == txn.getPayerId() && txn.getPayeePiType() == 0
		&& txn.getPayerPiType() != 0) {
	    amt = txn.getAmount().getValue() - txn.getPayeeAmount().getValue();
	} else if (customerId == txn.getPayeeId()
		&& customerId == txn.getPayerId() && txn.getPayeePiType() != 0
		&& txn.getPayerPiType() != 0) {
	    amt = txn.getAmount().getValue() - txn.getPayeeAmount().getValue();
	}

	MoneyAmount feeAmt = new MoneyAmount();
	feeAmt.setCurrency(txn.getAmount().getCurrency());
	feeAmt.setValue(amt);

	return convertAmountToStringWithCurrency(feeAmt);

    }

    @Override
    protected void initOwnPageComponents() {

    }
}
