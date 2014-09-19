package com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee;

import com.sybase365.mobiliser.web.btpn.bank.beans.ManageBillPaymentFeeBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ManageBillPaymentFeeAddPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the Manage Fee page for bank portals.
 * 
 * @author Vikram Gunda
 */
public class ManageBillPaymentAddFeePage extends BtpnBaseBankPortalSelfCarePage {

	/**
	 * Constructor for this page.
	 */
	public ManageBillPaymentAddFeePage() {
		super();
		initThisPageComponents();
	}


	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 * 
	 * @param feeBean feeBean for Manage beanss
	 * @param feeDetailsBean feeDetailsBean for Manage beans
	 */
	private void initThisPageComponents() {
		final  ManageBillPaymentFeeBean feeBean = new ManageBillPaymentFeeBean();

//		billPayBean.setFeeType(BtpnConstants.USECASE_BILLPAYMENT_FEE);
//		final Map<String, String> useCases = lookupMapUtility.getLookupNamesMap(BtpnConstants.RESOURCE_BUNDLE_USECASE,
//			this);
//		billPayBean.setUseCaseName(new CodeValue(String.valueOf(BtpnConstants.USECASE_BILL_PAYMENT), BtpnUtils
//			.getDropdownValueFromId(useCases,
//				BtpnConstants.RESOURCE_BUNDLE_USECASE + "." + String.valueOf(BtpnConstants.USECASE_BILL_PAYMENT))));

		add(new ManageBillPaymentFeeAddPanel("manageBillPaymentFeeAddPanel", this, feeBean));
	}
	

}
