package com.sybase365.mobiliser.web.consumer.pages.portal.selfcare.alerts;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;

import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerAlert;
import com.sybase365.mobiliser.web.util.Constants;

/**
 * This Page is for editting a Password Changed Alert
 * 
 * @author sushil.agrawala
 */
@AuthorizeInstantiation(Constants.PRIV_SHOW_MBANKING_ALERT)
public class EditBankInquiryResponseAlertPage extends EditGenericAlertPage {

    public EditBankInquiryResponseAlertPage(final CustomerAlert customerAlert) {
	super(customerAlert);
    }

}