package com.sybase365.mobiliser.web.distributor.pages.agentcare;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.money.contract.v5_0.customer.CreateCommissionConfigurationRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.CreateCommissionConfigurationResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetCommissionConfigurationByCustomerRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetCommissionConfigurationByCustomerResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.UpdateCommissionConfigurationRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.UpdateCommissionConfigurationResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.CommissionConfiguration;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.ClearCommissionsRequest;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.ClearCommissionsResponse;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.GetOpenCommissionAmountByConfigurationRequest;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.GetOpenCommissionAmountByConfigurationResponse;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.WalletEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.common.components.KeyValue;
import com.sybase365.mobiliser.web.common.components.KeyValueDropDownChoice;
import com.sybase365.mobiliser.web.common.components.LocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

@AuthorizeInstantiation(Constants.PRIV_VIEW_COMMISSION_MGMT)
public class CommissionManagementPage extends AgentCareMenuGroup {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(CommissionManagementPage.class);

    private CommissionConfiguration commissionConfiguration;
    private String agentSvaBalance;

    private String openCommissionAmount;

    public CommissionManagementPage() {
	super();
	initPageComponents();
    }

    public CommissionManagementPage(String message) {
	super();
	initPageComponents();
	info(message);
    }

    @SuppressWarnings("unchecked")
    protected void initPageComponents() {

	Form<?> commissionMgmtForm = new Form("CommissionMgmtForm",
		new CompoundPropertyModel<AgentCreatePage>(this));
	commissionMgmtForm.add(new RequiredTextField<BigDecimal>(
		"commissionConfiguration.percentage").add(
		Constants.dAmountSimpleAttributeModifier).add(
		new ErrorIndicator()));
	commissionMgmtForm.add(new RequiredTextField<BigDecimal>(
		"commissionConfiguration.percentageSelf").add(
		Constants.dAmountSimpleAttributeModifier).add(
		new ErrorIndicator()));
	/*
	 * commissionMgmtForm.add(new RequiredTextField<Long>(
	 * "commissionConfiguration.paymentInstrumentId") .add(new
	 * ErrorIndicator()));
	 */
	CustomerBean agentToEdit = getMobiliserWebSession().getCustomer();
	List<WalletEntry> walletEntryList = getWalletEntryList(
		agentToEdit.getId(), null, null);
	if (walletEntryList != null && !walletEntryList.isEmpty()) {
	    for (WalletEntry wallet : walletEntryList) {
		if (wallet.getSva() != null) {
		    agentSvaBalance = convertAmountToString(getSVABalanceAmount(wallet
			    .getPaymentInstrumentId()));
		    break;
		}
	    }
	}
	commissionMgmtForm.add(new KeyValueDropDownChoice<Long, String>(
		"commissionConfiguration.piId", getPaymentIdList())
		.setRequired(true).add(new ErrorIndicator()));

	commissionMgmtForm
		.addOrReplace(new LocalizableLookupDropDownChoice<String>(
			"commissionConfiguration.clearingFrequency",
			String.class, "clearingFrequencys", this, false, true) {
		    @Override
		    protected CharSequence getDefaultChoice(Object arg0) {
			return "";
		    }
		}.add(new ErrorIndicator()));

	try {
	    GetCommissionConfigurationByCustomerRequest commissionConfigReq = getNewMobiliserRequest(GetCommissionConfigurationByCustomerRequest.class);
	    if (PortalUtils.exists(getMobiliserWebSession().getCustomer())) {
		commissionConfigReq.setCustomerId(getMobiliserWebSession()
			.getCustomer().getId());
	    }
	    GetCommissionConfigurationByCustomerResponse commissionConfigResp = wsCommissionConfigClient
		    .getCommissionConfigurationByCustomer(commissionConfigReq);
	    if (!evaluateMobiliserResponse(commissionConfigResp))
		return;
	    commissionConfiguration = commissionConfigResp
		    .getCommissionConfiguration();
	} catch (Exception e) {
	    LOG.error("# An error occurred while getting commission management data.");
	}
	boolean settleCommissionAllowed = false;
	if (getMobiliserWebSession().hasPrivilege(
		Constants.PRIV_SETTLE_COMMISSION)
		&& commissionConfiguration != null
		&& commissionConfiguration.getId() != null) {
	    settleCommissionAllowed = true;
	}
	commissionMgmtForm.add(new Button("settlenowCommissionMgmt") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleSettlenow();
	    };
	}.setEnabled(settleCommissionAllowed));
	boolean commissionEditable = false;
	if (getMobiliserWebSession().hasPrivilege(
		Constants.PRIV_EDIT_COMMISSION_MGMT)) {
	    commissionEditable = true;
	}

	commissionMgmtForm.add(new Button("submitCommissionMgmt") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleSubmit();
		boolean settleCommissionAllowed = false;
		if (getMobiliserWebSession().hasPrivilege(
			Constants.PRIV_SETTLE_COMMISSION)
			&& commissionConfiguration != null
			&& commissionConfiguration.getId() != null) {
		    settleCommissionAllowed = true;
		}
		this.getForm().addOrReplace(
			new Button("settlenowCommissionMgmt") {
			    private static final long serialVersionUID = 1L;

			    @Override
			    public void onSubmit() {
				handleSettlenow();
			    };
			}.setEnabled(settleCommissionAllowed));
	    };
	}.setVisible(commissionEditable));
	commissionMgmtForm.add(new Button("cancelCommissionMgmt") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleCancel();
	    };
	}.setDefaultFormProcessing(false));

	commissionMgmtForm.add(new Label("agentSvaBalance"));

	commissionMgmtForm.add(new Label("openCommissionAmount"));

	if (PortalUtils.exists(getCommissionConfiguration())
		&& PortalUtils.exists(getCommissionConfiguration()
			.getLastSettlement())) {
	    commissionMgmtForm.addOrReplace(new Label(
		    "commissionConfiguration.lastSettlement", PortalUtils
			    .getFormattedDate(getCommissionConfiguration()
				    .getLastSettlement(),
				    getMobiliserWebSession().getLocale())));

	} else {
	    commissionMgmtForm.addOrReplace(new Label(
		    "commissionConfiguration.lastSettlement"));

	}
	commissionMgmtForm.add(new FeedbackPanel("errorMessages"));

	add(commissionMgmtForm);
    }

    public void handleSettlenow() {

	try {
	    ClearCommissionsRequest clearCommReq = getNewMobiliserRequest(ClearCommissionsRequest.class);
	    clearCommReq.setCustomerId(getMobiliserWebSession().getCustomer()
		    .getId());
	    TimeZone timeZone = TimeZone.getDefault();
	    if (PortalUtils.exists(getMobiliserWebSession().getCustomer()
		    .getTimeZone())) {
		timeZone = TimeZone.getTimeZone(getMobiliserWebSession()
			.getCustomer().getTimeZone());
	    }
	    Calendar cal = Calendar.getInstance(timeZone);
	    String hour = Integer.toString(cal.get(Calendar.HOUR_OF_DAY));
	    String min = Integer.toString(cal.get(Calendar.MINUTE));
	    if (hour.length() < 2)
		hour = "0" + hour;
	    if (min.length() < 2)
		min = "0" + min;
	    clearCommReq.setCutofftime(hour + min);
	    ClearCommissionsResponse clearCommResp = wsCommissionClearingClient
		    .clearCommission(clearCommReq);
	    if (!evaluateMobiliserResponse(clearCommResp))
		return;
	    refreshSVABalance();
	    setResponsePage(new CommissionManagementPage(getLocalizer()
		    .getString("MESSAGE.COMMISSION_SETTLEMENT_SUCCESS", this)));
	} catch (Exception e) {
	    error(getLocalizer().getString("ERROR.SETTLE_COMMISSION", this));
	    LOG.error("# An error occurred while settle commission.", e);
	}
    }

    public void handleCancel() {

	setResponsePage(new AgentEditPage("edit"));
    }

    public void handleSubmit() {
	LOG.debug("# CommissionManagementActionBean.handleSubmit()");
	try {

	    if (PortalUtils.exists(getMobiliserWebSession().getCustomer())) {
		commissionConfiguration.setCustomerId(getMobiliserWebSession()
			.getCustomer().getId());
	    }
	    // Create mode
	    if (commissionConfiguration.getId() == null) {
		createCommissionConfiguration();
	    }
	    // Update mode
	    else {
		updateCommissionConfiguration();
	    }
	    getMobiliserWebSession().info(
		    getLocalizer().getString(
			    "MESSAGE.COMISSION_MANAGEMENT_DATA_SAVED", this));

	    setResponsePage(CommissionManagementPage.this);

	} catch (Exception e) {
	    LOG.error("# An error occurred while saving commission management data.");
	}
    }

    public List<KeyValue<Long, String>> getPaymentIdList() {
	LOG.debug("# CommissionManagementPage.getPaymentIdList()");
	CustomerBean agentToEdit = getMobiliserWebSession().getCustomer();
	List<WalletEntry> walletEntryList = null;
	Long custId;
	List<KeyValue<Long, String>> walletKeyList = new ArrayList<KeyValue<Long, String>>();
	if (PortalUtils.exists(agentToEdit)) {
	    try {
		Integer typeId = agentToEdit.getCustomerTypeId();
		if (typeId == Constants.CUSTOMER_ROLE_MONEY_MERCHANT_DEALER) {
		    custId = agentToEdit.getParentId();
		} else {
		    custId = agentToEdit.getId();
		}

		walletEntryList = getWalletEntryList(custId, null, null);

	    } catch (Exception e) {
		LOG.error(e.getLocalizedMessage(), e);
	    }
	}
	if (PortalUtils.exists(walletEntryList)) {
	    for (WalletEntry wallet : walletEntryList) {
		String value = "";
		if (PortalUtils.exists(wallet.getPaymentInstrumentId())) {
		    value = String.valueOf(wallet.getPaymentInstrumentId());
		}

		if (PortalUtils.exists(wallet.getAlias())) {
		    value += "(" + wallet.getAlias() + ")";
		}
		walletKeyList.add(new KeyValue<Long, String>(wallet
			.getPaymentInstrumentId(), value));
	    }
	}
	return walletKeyList;
    }

    public CommissionConfiguration getCommissionConfiguration() {
	return commissionConfiguration;
    }

    public void setCommissionConfiguration(
	    CommissionConfiguration commissionConfiguration) {
	this.commissionConfiguration = commissionConfiguration;
    }

    public String getAgentSvaBalance() {
	return agentSvaBalance;
    }

    public void setAgentSvaBalance(String agentSvaBalance) {
	this.agentSvaBalance = agentSvaBalance;
    }

    public String getOpenCommissionAmount() {
	if (commissionConfiguration != null) {
	    openCommissionAmount = getOpenCommission(commissionConfiguration
		    .getId());
	}
	return openCommissionAmount;
    }

    public void setOpenCommissionAmount(String openCommissionAmount) {
	this.openCommissionAmount = openCommissionAmount;
    }

    private String getOpenCommission(Long commConfigID) {
	if (commConfigID != null) {
	    try {
		GetOpenCommissionAmountByConfigurationRequest openCommReq = getNewMobiliserRequest(GetOpenCommissionAmountByConfigurationRequest.class);
		openCommReq.setCommissionConfigurationId(commConfigID);
		GetOpenCommissionAmountByConfigurationResponse openCommResp = wsCommissionClearingClient
			.getOpenCommissionAmountByConfiguration(openCommReq);
		if (!evaluateMobiliserResponse(openCommResp))
		    return null;
		LOG.debug("# Successfully retrived open commission amount");
		if (PortalUtils.exists(openCommResp.getOpenAmount())) {
		    return convertAmountToStringWithCurrency(
			    openCommResp.getOpenAmount(),
			    openCommResp.getCurrency());
		}

		return null;
	    } catch (Exception e) {
		LOG.error("# An error occurred while retriving commission ammount.");
	    }
	}

	return null;
    }

    private void updateCommissionConfiguration() throws Exception {
	LOG.debug("# CommissionManagementPage.UpdateCommissionConfiguration()");
	UpdateCommissionConfigurationRequest updateConfigReq = getNewMobiliserRequest(UpdateCommissionConfigurationRequest.class);
	updateConfigReq.setCommissionConfiguration(commissionConfiguration);
	UpdateCommissionConfigurationResponse updateConfigResp = wsCommissionConfigClient
		.updateCommissionConfiguration(updateConfigReq);
	if (!evaluateMobiliserResponse(updateConfigResp))
	    return;
	LOG.debug("# Successfully updated commission configuration data");
    }

    private void createCommissionConfiguration() throws Exception {
	LOG.debug("# CommissionManagementPage.createCommissionConfiguration()");
	CreateCommissionConfigurationRequest commissionconfigReq = getNewMobiliserRequest(CreateCommissionConfigurationRequest.class);
	commissionconfigReq.setCommissionConfiguration(commissionConfiguration);
	CreateCommissionConfigurationResponse commissionConfigResp = wsCommissionConfigClient
		.createCommissionConfiguration(commissionconfigReq);
	if (!evaluateMobiliserResponse(commissionConfigResp))
	    return;
	commissionConfiguration.setId(commissionConfigResp
		.getCommissionConfigurationId());
	LOG.debug("# Successfully saved commission configuration data");

    }

}
