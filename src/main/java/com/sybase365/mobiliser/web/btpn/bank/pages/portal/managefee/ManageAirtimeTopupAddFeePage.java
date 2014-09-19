package com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee;

import java.util.Map;

import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageAirtimeTopupFeeBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageFeeDetailsBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ManageAirtimeTopupFeeAddPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnUtils;

/**
 * This is the Manage Fee page for bank portals.
 * 
 * @author Vikram Gunda
 */
public class ManageAirtimeTopupAddFeePage extends BtpnBaseBankPortalSelfCarePage {

	/**
	 * Constructor for this page.
	 */
	public ManageAirtimeTopupAddFeePage() {
		super();
		initThisPageComponents();
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	@Override
	protected void initOwnPageComponents() {
		super.initOwnPageComponents();
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 * 
	 * @param feeBean feeBean for Manage beanss
	 * @param feeDetailsBean feeDetailsBean for Manage beans
	 */
	private void initThisPageComponents() {
		final ManageAirtimeTopupFeeBean airtimeTopupBean = new ManageAirtimeTopupFeeBean();
		airtimeTopupBean.setFeeType(BtpnConstants.USECASE_AIRTIME_FEE);
		final Map<String, String> useCases = lookupMapUtility.getLookupNamesMap(BtpnConstants.RESOURCE_BUNDLE_USECASE,
			this);
		airtimeTopupBean.setUseCaseName(new CodeValue(String.valueOf(BtpnConstants.USECASE_AIR_TIME_TOPUP), BtpnUtils
			.getDropdownValueFromId(useCases,
				BtpnConstants.RESOURCE_BUNDLE_USECASE + "." + String.valueOf(BtpnConstants.USECASE_AIR_TIME_TOPUP))));
		add(new ManageAirtimeTopupFeeAddPanel("manageAirtimeTopupFeeAddPanel", this, true, airtimeTopupBean,
			new ManageFeeDetailsBean()));
	}
}
