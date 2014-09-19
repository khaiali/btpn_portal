package com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee;

import java.util.Map;

import org.apache.wicket.markup.html.panel.Panel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.fee.FindPendingApprovalFeeDetailRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.fee.FindPendingApprovalFeeDetailResponse;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.limit.FindPendingApprovalLimitDetailRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.limit.FindPendingApprovalLimitDetailResponse;
import com.sybase365.mobiliser.web.btpn.bank.beans.ApproveFeeBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.ApproveFeeConfirmBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.ApproveLimitBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ApproveBillPayOrAirtimeFeePanel;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ApproveFeeDetailsPanel;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ApproveLimitPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnUtils;
import com.sybase365.mobiliser.web.btpn.util.ConverterUtils;

/**
 * This is the Approve Fee page for bank portals.
 * 
 * @author Vikram Gunda
 */
public class ApproveFeeDetailsPage extends BtpnBaseBankPortalSelfCarePage {

	private static final Logger LOG = LoggerFactory.getLogger(ApproveFeeDetailsPage.class);

	/**
	 * Constructor for this page.
	 */
	public ApproveFeeDetailsPage(final ApproveFeeBean feeBean) {
		super();
		constructPage(feeBean);
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
	 */
	private void constructPage(final ApproveFeeBean feeBean) {
		final ApproveFeeConfirmBean confirmBean = new ApproveFeeConfirmBean();
		Panel approveFeePanel = getApproveFeePanel(feeBean, confirmBean);
		add(approveFeePanel);
	}

	/**
	 * Fetch the approval fee panel based on Fee Type.
	 */
	private Panel getApproveFeePanel(final ApproveFeeBean feeBean, final ApproveFeeConfirmBean confirmBean) {
		final String feeType = feeBean.getFeeType();

		if (BtpnUtils.getFeesList().contains(feeType)) {
			fetchApproveFeeBeanDetails(feeBean, confirmBean);
			return new ApproveFeeDetailsPanel("approveFeeDetailsPanel", this, feeBean, confirmBean);
		} else if (BtpnUtils.getBillPayOrAirtimeFeesList().contains(feeType)) {
			fetchApproveFeeBeanDetails(feeBean, confirmBean);
			return new ApproveBillPayOrAirtimeFeePanel("approveFeeDetailsPanel", this, feeBean, confirmBean);
		} else {
			final ApproveLimitBean limitBean = new ApproveLimitBean();
			fetchApproveLimitDetails(feeBean, limitBean);
			return new ApproveLimitPanel("approveFeeDetailsPanel", this, feeBean, limitBean);
		}
	}

	/**
	 * Fetch Approve Fee List from service
	 * 
	 * @return List<ApproveFeeBean> list of Approve Fee Beans
	 */
	private void fetchApproveFeeBeanDetails(final ApproveFeeBean feeBean, final ApproveFeeConfirmBean confirmBean) {
		try {
			// Approve Fee Bean Fees Request
			final FindPendingApprovalFeeDetailRequest request = this
				.getNewMobiliserRequest(FindPendingApprovalFeeDetailRequest.class);
			request.setCheckerId(this.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			request.setTaskId(feeBean.getTaskId());
			final FindPendingApprovalFeeDetailResponse response = this.feeClient.findPendingApprovalFeeDetail(request);
			if (evaluateBankPortalMobiliserResponse(response)) {
				ConverterUtils.convertToApproveFeeBean(response.getFee(), confirmBean);
				final Map<String, String> useCases = lookupMapUtility.getLookupNamesMap(
					BtpnConstants.RESOURCE_BUNDLE_USECASE, this);
				confirmBean.getNewUseCaseName().setValue(
					BtpnUtils.getDropdownValueFromId(useCases, BtpnConstants.RESOURCE_BUNDLE_USECASE + "."
							+ confirmBean.getNewUseCaseName().getId()));
			} else {
				this.getMobiliserWebSession().error(getLocalizer().getString("error.approve.fees", this));
			}
		} catch (Exception e) {
			this.getMobiliserWebSession().error(getLocalizer().getString("error.exception", this));
			LOG.error("Exception occured while fetching Transaction GL Code List  ===> ", e);
		}
	}

	/**
	 * Fetch Approve LimiDetails
	 * 
	 * @return List<ApproveFeeBean> list of Approve Fee Beans
	 */
	private void fetchApproveLimitDetails(final ApproveFeeBean feeBean, final ApproveLimitBean limitBean) {
		try {
			// Fetch Approve Limit Details
			final FindPendingApprovalLimitDetailRequest request = this
				.getNewMobiliserRequest(FindPendingApprovalLimitDetailRequest.class);
			request.setCheckerId(this.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			request.setLimitClassId(feeBean.getLimitClassId());
			request.setTaskId(feeBean.getTaskId());
			final FindPendingApprovalLimitDetailResponse response = this.limitClient
				.findPendingApprovalLimitDetail(request);
			if (evaluateBankPortalMobiliserResponse(response)) {
				ConverterUtils.convertToLimitBean(response.getLimit(), limitBean);
			} else {
				this.getMobiliserWebSession().error(getLocalizer().getString("error.limit.details", this));
			}
		} catch (Exception e) {
			this.getMobiliserWebSession().error(getLocalizer().getString("error.exception", this));
			LOG.error("Exception occured while fetching Transaction GL Code List  ===> ", e);
		}
	}
}
