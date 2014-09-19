package com.sybase365.mobiliser.web.btpn.consumer.pages.portal.transaction;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.ConsumerTransactionBean;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;

/**
 * This class is View Transactions Page for Consumer Portal. The transactions page displays the transaction details.
 * 
 * @author Vikram Gunda
 */
public class TransactionDetailsPopupPage extends BtpnMobiliserBasePage {

	private ConsumerTransactionBean txnDetails;

	public TransactionDetailsPopupPage(ConsumerTransactionBean txnDetails) {
		super();
		this.txnDetails = txnDetails;
		initThisPageComponents();
	}

	protected void initThisPageComponents() {
		final Form<TransactionDetailsPopupPage> form = new Form<TransactionDetailsPopupPage>("detailsForm",
			new CompoundPropertyModel<TransactionDetailsPopupPage>(this));
		Date txnDate = txnDetails.getDate();
		DateFormat date = new SimpleDateFormat("MM/dd/yyyy");
		DateFormat time = new SimpleDateFormat("HH:mm:ss");
		form.add(new Label("txnDetails.date", date.format(txnDate)));
		form.add(new Label("txnDetails.time", time.format(txnDate)));
		form.add(new Label("txnDetails.type"));
		form.add(new Label("txnDetails.errorCode"));
		form.add(new Label("txnDetails.name"));
		form.add(new Label("txnDetails.details"));
		form.add(new AmountLabel("txnDetails.amount"));
		form.add(new AmountLabel("txnDetails.fee"));
		add(form);
	}

	@Override
	protected void initOwnPageComponents() {

	}
}
