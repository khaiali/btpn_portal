package com.sybase365.mobiliser.web.common.panels.alerts;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.panel.Panel;

import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.WalletEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.beans.AlertAccountChooserBean;
import com.sybase365.mobiliser.web.common.components.KeyValue;
import com.sybase365.mobiliser.web.common.components.KeyValueDropDownChoice;
import com.sybase365.mobiliser.web.util.Constants;

/**
 * @author sagraw03
 * 
 *         AlertAccountChooserPanel need to be added where we need to show
 *         account dropdownChoice.
 */
public class AlertAccountChooserPanel extends Panel {
    /**
     * 
     */
    private static final long serialVersionUID = 9084969205565399137L;
    private KeyValueDropDownChoice<String, String> bankAccList;
    private MobiliserBasePage basePage;
    private AlertAccountChooserBean alertAccountChooserBean;

    public AlertAccountChooserPanel(String id, long customerId,
	    MobiliserBasePage basePage,
	    final AlertAccountChooserBean alertAccountChooserBean) {

	super(id);
	this.alertAccountChooserBean = alertAccountChooserBean;
	this.setBasePage(basePage);
	bankAccList = (KeyValueDropDownChoice<String, String>) new KeyValueDropDownChoice<String, String>(
		"alertAccountChooserBean.paymentInstrumentId",
		getBankAccList(customerId));

	bankAccList.setRequired(true).add(new ErrorIndicator());
	add(bankAccList);
    }

    public void setBasePage(MobiliserBasePage basePage) {
	this.basePage = basePage;
    }

    public MobiliserBasePage getBasePage() {
	return basePage;
    }

    public void setAlertAccountChooserBean(
	    AlertAccountChooserBean alertAccountChooserBean) {
	this.alertAccountChooserBean = alertAccountChooserBean;
    }

    public AlertAccountChooserBean getAlertAccountChooserBean() {
	return alertAccountChooserBean;
    }

    /**
     * getBankAccList return all bank account for a customer Id.If bank account
     * is more than one this method add AllAcount in BankAccount List.
     * 
     * @param customerId
     * @return
     */

    public List<KeyValue<String, String>> getBankAccList(long customerId) {
	List<WalletEntry> waleetEntries = basePage.getWalletEntryList(
		Long.valueOf(customerId),
		Integer.valueOf(Constants.PIS_CLASS_FILTER_BANK_ACCOUNT), null);
	List<KeyValue<String, String>> bankAccList = new ArrayList<KeyValue<String, String>>();
	if (waleetEntries != null) {
	    if (waleetEntries.size() > 0) {
		String allAccount = getLocalizer().getString(
			"accountChooser.allAccount", this);
		bankAccList.add(new KeyValue<String, String>(
			Constants.ALL_ACCOUNT, allAccount));
	    }
	    for (WalletEntry wallet : waleetEntries) {
		Long key = wallet.getPaymentInstrumentId();
		String value = wallet.getAlias();
		bankAccList.add(new KeyValue<String, String>(key.toString(),
			value));

	    }
	}
	return bankAccList;
    }
}
