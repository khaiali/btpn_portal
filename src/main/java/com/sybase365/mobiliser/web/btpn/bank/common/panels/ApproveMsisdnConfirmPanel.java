package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.changemsisdn.ContinuePendingChangeMsisdnRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.changemsisdn.ContinuePendingChangeMsisdnResponse;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.ApproveMsisdnBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.ApproveMsisdnPage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the ApproveMsisdnConfirmPanel for MSISDN in bank portal.
 * 
 * @author Narasa Reddy
 */
public class ApproveMsisdnConfirmPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(ApproveMsisdnConfirmPanel.class);

	private FeedbackPanel feedBack;

	BtpnMobiliserBasePage basePage;

	ApproveMsisdnBean approveBean;

	public ApproveMsisdnConfirmPanel(String id, BtpnMobiliserBasePage basePage, ApproveMsisdnBean bean) {
		super(id);
		this.basePage = basePage;
		this.approveBean = bean;
		constructPanel();
	}

	protected void constructPanel() {

		final Form<ApproveMsisdnConfirmPanel> form = new Form<ApproveMsisdnConfirmPanel>("approveMsisdnConfirmForm",
			new CompoundPropertyModel<ApproveMsisdnConfirmPanel>(this));

		// Add feedback panel for Error Messages
		feedBack = new FeedbackPanel("errorMessages");
		feedBack.setOutputMarkupId(true);
		feedBack.setOutputMarkupPlaceholderTag(true);
		form.add(feedBack);

		String headerMessage = "";
		if (PortalUtils.exists(approveBean)) {
			String selectedLink = approveBean.getSelectedLink();
			if (selectedLink.equals("Approve")) {
				headerMessage = getLocalizer().getString("headLine.confirmApprove", ApproveMsisdnConfirmPanel.this);
			} else if (selectedLink.equals("Reject")) {
				headerMessage = getLocalizer().getString("headLine.confirmReject", ApproveMsisdnConfirmPanel.this);
			}
		}

		form.add(new Label("headerMessage", headerMessage));

		form.add(new Label("approveBean.oldMobile"));
		form.add(new Label("approveBean.newMobile"));

		form.add(addConfirmButton());
		form.add(addCancelButton());

		add(form);
	}

	protected Button addConfirmButton() {
		Button confirmButton = new Button("confirmButton") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				String statusMessage = null;
				if (PortalUtils.exists(approveBean)) {
					String selectedLinkName = approveBean.getSelectedLink();
					if (selectedLinkName.equals("Approve")) {
						approveBean = continuePendingChangeMsisdn(approveBean, true);
						if (approveBean.isApproveSuccess()) {
							statusMessage = getLocalizer().getString("successMessage", ApproveMsisdnConfirmPanel.this);
							approveBean.setStatus(statusMessage);
						}
					} else if (selectedLinkName.equals("Reject")) {
						approveBean = continuePendingChangeMsisdn(approveBean, false);
						if (approveBean.isRejectSuccess()) {
							statusMessage = getLocalizer().getString("rejectMessage", ApproveMsisdnConfirmPanel.this);
							approveBean.setStatus(statusMessage);
						}
					}
					setResponsePage(new ApproveMsisdnPage(approveBean));
				}

			}
		};
		return confirmButton;
	}

	protected Button addCancelButton() {
		Button cancelButton = new Button("cancleButton") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(ApproveMsisdnPage.class);
			};
		};
		cancelButton.setDefaultFormProcessing(false);
		return cancelButton;
	}

	/**
	 * calling continuePendingChangeMsisdn service from change msisdn end point
	 * 
	 * @param bean
	 * @return
	 */
	private ApproveMsisdnBean continuePendingChangeMsisdn(ApproveMsisdnBean approveBean, boolean flag) {
		try {
			final ContinuePendingChangeMsisdnRequest request = basePage
				.getNewMobiliserRequest(ContinuePendingChangeMsisdnRequest.class);
			request.setApprove(flag);
			request.setTaskId(approveBean.getTaskId());
			ContinuePendingChangeMsisdnResponse pendingChangeMsisdnResponse = basePage.getChangeMsisdnClient()
				.continuePendingChangeMsisdn(request);
			if (basePage.evaluateConsumerPortalMobiliserResponse(pendingChangeMsisdnResponse)
					&& pendingChangeMsisdnResponse.getStatus().getCode() == BtpnConstants.STATUS_CODE) {
				approveBean.setNewMobile(pendingChangeMsisdnResponse.getMsisdn());
				if (flag) {
					approveBean.setApproveSuccess(true);
				} else {
					approveBean.setRejectSuccess(true);
				}
			} else {
				error(pendingChangeMsisdnResponse.getStatus().getValue());
			}
		} catch (Exception ex) {
			LOG.error("#An error occurred while calling continuePendingChangeMsisdn service.", ex);
		}
		return approveBean;
	}

}
