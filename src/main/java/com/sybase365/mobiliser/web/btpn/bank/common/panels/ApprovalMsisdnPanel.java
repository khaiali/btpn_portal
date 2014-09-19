package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.changemsisdn.FindPendingChangeMsisdnRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.changemsisdn.FindPendingChangeMsisdnResponse;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.ApproveMsisdnBean;
import com.sybase365.mobiliser.web.btpn.bank.common.dataproviders.ApprovalMsisdnDataProvider;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.ApproveMsisdnConfirmPage;
import com.sybase365.mobiliser.web.btpn.common.components.BtpnCustomPagingNavigator;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.ConverterUtils;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the ConsumerApprovalPanel page for bank portals.
 * 
 * @author Narasa Reddy
 */
public class ApprovalMsisdnPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(ApprovalMsisdnPanel.class);

	protected BtpnMobiliserBasePage basePage;

	ApproveMsisdnBean approveBean;

	private FeedbackPanel feedBack;

	private static final String WICKET_ID_PAGEABLE = "pageable";

	private static final String WICKET_ID_APPROVE_LINK = "approveLink";

	private static final String WICKET_ID_REJECT_LINK = "rejectLink";

	private static final String WICKET_ID_APPROVE_LINK_NAME = "approveLinkName";

	private static final String WICKET_ID_REJECT_LINK_NAME = "rejectLinkName";

	private static final String WICKET_ID_approvalNavigator = "approvalNavigator";

	private static final String WICKET_ID_approvalTotalItems = "approvalHeader";

	private String approvalTotalItemString;

	private int approvalStartIndex = 0;

	private int approvalEndIndex = 0;

	private Label approvalHeader;

	private Label noRecordsLabel;

	private BtpnCustomPagingNavigator navigator;

	// private String consumerType;

	public ApprovalMsisdnPanel(String id, BtpnMobiliserBasePage basePage, ApproveMsisdnBean bean) {
		super(id);
		this.basePage = basePage;
		this.approveBean = bean;
		LOG.debug("ApprovalMsisdnPanel Started.");
		constructPanel();
	}

	protected void constructPanel() {
		final Form<ApprovalMsisdnPanel> form = new Form<ApprovalMsisdnPanel>("approvalMsisdnForm",
			new CompoundPropertyModel<ApprovalMsisdnPanel>(this));

		// Add feedback panel for Error Messages
		feedBack = new FeedbackPanel("errorMessages");
		feedBack.setOutputMarkupId(true);
		feedBack.setOutputMarkupPlaceholderTag(true);
		form.add(feedBack);

		String message = getLocalizer().getString("label.noDataFound", ApprovalMsisdnPanel.this);
		noRecordsLabel = new Label("emptyRecordsMessage", message);
		noRecordsLabel.setOutputMarkupId(true);
		noRecordsLabel.setOutputMarkupPlaceholderTag(true);
		noRecordsLabel.setVisible(false);
		form.add(noRecordsLabel);

		List<ApproveMsisdnBean> approvalList = fetchPendingMsisdnApprovalList();
		if (!PortalUtils.exists(approvalList)) {
			noRecordsLabel.setVisible(true);
		} else {
			noRecordsLabel.setVisible(false);
		}

		if (PortalUtils.exists(approveBean)) {
			if (approveBean.isApproveSuccess() || approveBean.isRejectSuccess()) {
				basePage.getWebSession().info(approveBean.getStatus());
			}
		}

		// Add the approval container
		final WebMarkupContainer approvalContainer = new WebMarkupContainer("pendingApprovalContainer");
		approvalsMsisdnDataView(approvalContainer);
		approvalContainer.setOutputMarkupId(true);
		form.add(approvalContainer);
		add(form);
	}

	/**
	 * This method adds the approvalsDataView for the MSISDN and also adds the sorting logic for data view
	 */
	protected void approvalsMsisdnDataView(final WebMarkupContainer dataViewContainer) {
		// Create the approval View
		final ApprovalMsisdnDataProvider approvalDataProvider = new ApprovalMsisdnDataProvider("createdBy",
			fetchPendingMsisdnApprovalList());

		final DataView<ApproveMsisdnBean> dataView = new ApprovalDataView(WICKET_ID_PAGEABLE, approvalDataProvider);
		dataView.setItemsPerPage(20);

		// Add the navigation
		navigator = new BtpnCustomPagingNavigator(WICKET_ID_approvalNavigator, dataView) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return approvalDataProvider.size() != 0;

			}

		};
		navigator.setOutputMarkupId(true);
		navigator.setOutputMarkupPlaceholderTag(true);
		dataViewContainer.add(navigator);

		// Add the header
		IModel<String> headerDisplayModel = new AbstractReadOnlyModel<String>() {

			private static final long serialVersionUID = 1L;

			@Override
			public String getObject() {
				final String displayTotalItemsText = ApprovalMsisdnPanel.this.getLocalizer().getString(
					"approval.totalitems.header", ApprovalMsisdnPanel.this);
				return String.format(displayTotalItemsText, approvalTotalItemString, approvalStartIndex,
					approvalEndIndex);
			}

		};
		approvalHeader = new Label(WICKET_ID_approvalTotalItems, headerDisplayModel) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return approvalDataProvider.size() != 0;

			}
		};
		dataViewContainer.add(approvalHeader);
		approvalHeader.setOutputMarkupId(true);
		approvalHeader.setOutputMarkupPlaceholderTag(true);

		// Add the sort providers
		dataViewContainer.add(new OrderByBorder("orderCreatedBy", "createdBy", approvalDataProvider) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSortChanged() {
				dataView.setCurrentPage(0);
			}
		});
		dataViewContainer.add(new OrderByBorder("orderByCustomer", "mobileNumber", approvalDataProvider) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSortChanged() {
				dataView.setCurrentPage(0);
			}
		});

		dataViewContainer.add(new OrderByBorder("orderByOldMobile", "oldMobile", approvalDataProvider) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSortChanged() {
				dataView.setCurrentPage(0);
			}
		});

		dataViewContainer.add(new OrderByBorder("orderByNewMobile", "newMobile", approvalDataProvider) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSortChanged() {
				dataView.setCurrentPage(0);
			}
		});

		dataViewContainer.add(new OrderByBorder("orderByStatus", "status", approvalDataProvider) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSortChanged() {
				dataView.setCurrentPage(0);
			}
		});

		dataViewContainer.addOrReplace(dataView);
	}

	/**
	 * This is the approvalsDataView for approval MSISDN.
	 * 
	 * @author Narasa Reddy
	 */
	private class ApprovalDataView extends DataView<ApproveMsisdnBean> {

		private static final long serialVersionUID = 1L;

		protected ApprovalDataView(String id, IDataProvider<ApproveMsisdnBean> dataProvider) {
			super(id, dataProvider);
			setOutputMarkupId(true);
			setOutputMarkupPlaceholderTag(true);

		}

		@Override
		protected void onBeforeRender() {
			refreshTotalItemCount();
			super.onBeforeRender();
		}

		@Override
		protected void populateItem(final Item<ApproveMsisdnBean> item) {
			final ApproveMsisdnBean entry = item.getModelObject();

			// Add the createdBy
			item.add(new Label("createdBy", entry.getCreatedBy()));
			// Add the Mobile Number
			item.add(new Label("mobileNumber", entry.getMobileNumber()));
			// Add the Change Request
			item.add(new Label("changeRequest", entry.getChangeRequest()));
			// Add the Old Mobile
			item.add(new Label("oldMobile", entry.getOldMobile()));
			// Add the New Mobile
			item.add(new Label("newMobile", entry.getNewMobile()));
			// Add the Status
			item.add(new Label("status", entry.getStatus()));

			// Add the Approve Link
			final AjaxLink<ApproveMsisdnBean> approveLink = new AjaxLink<ApproveMsisdnBean>(WICKET_ID_APPROVE_LINK,
				item.getModel()) {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					final ApproveMsisdnBean approveBean = (ApproveMsisdnBean) item.getModelObject();
					LOG.debug("ApproveMsisdnBean :" + approveBean);
					approveBean.setSelectedLink("Approve");
					setResponsePage(new ApproveMsisdnConfirmPage(approveBean));
				}
			};
			approveLink.add(new Label(WICKET_ID_APPROVE_LINK_NAME, "Approve"));
			item.add(approveLink);

			// Add the Reject Link
			final AjaxLink<ApproveMsisdnBean> rejectLink = new AjaxLink<ApproveMsisdnBean>(WICKET_ID_REJECT_LINK,
				item.getModel()) {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					final ApproveMsisdnBean rejectBean = (ApproveMsisdnBean) item.getModelObject();
					LOG.debug("ApproveMsisdnBean :" + rejectBean);
					rejectBean.setSelectedLink("Reject");
					setResponsePage(new ApproveMsisdnConfirmPage(rejectBean));

				}
			};
			rejectLink.add(new Label(WICKET_ID_REJECT_LINK_NAME, "Reject"));
			item.add(rejectLink);

			final String cssStyle = item.getIndex() % 2 == 0 ? BtpnConstants.DATA_VIEW_EVEN_ROW_CSS : BtpnConstants.DATA_VIEW_ODD_ROW_CSS;
			item.add(new SimpleAttributeModifier("class", cssStyle));
		}

		@Override
		public boolean isVisible() {
			return ((ApprovalMsisdnDataProvider) internalGetDataProvider()).size() != 0;

		}

		private void refreshTotalItemCount() {
			final int size = internalGetDataProvider().size();
			approvalTotalItemString = new Integer(size).toString();
			if (size > 0) {
				approvalStartIndex = getCurrentPage() * getItemsPerPage() + 1;
				approvalEndIndex = approvalStartIndex + getItemsPerPage() - 1;
				if (approvalEndIndex > size) {
					approvalEndIndex = size;
				}
			} else {
				approvalStartIndex = 0;
				approvalEndIndex = 0;
			}
		}
	}

	/**
	 * This fetches the pending approval details
	 */
	public List<ApproveMsisdnBean> fetchPendingMsisdnApprovalList() {
		List<ApproveMsisdnBean> approvalList = new ArrayList<ApproveMsisdnBean>();
		try {
			final FindPendingChangeMsisdnRequest request = basePage
				.getNewMobiliserRequest(FindPendingChangeMsisdnRequest.class);
			FindPendingChangeMsisdnResponse response = basePage.getChangeMsisdnClient()
				.findPendingChangeMsisdn(request);
			if (basePage.evaluateBankPortalMobiliserResponse(response)
					&& response.getStatus().getCode() == BtpnConstants.STATUS_CODE) {
				return ConverterUtils.convertToApproveMsisdnBean(response.getCustomers());
			}
		} catch (Exception e) {
			LOG.error("ApprovalMsisdnPanel:fetchPendingMsisdnApprovalList ==> ", e);
			error(getLocalizer().getString("pending.failure.exception", this));
		}
		return approvalList;
	}

}
