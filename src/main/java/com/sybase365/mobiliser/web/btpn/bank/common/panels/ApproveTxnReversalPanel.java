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

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.transactionreversal.FindPendingApprovalTransactionReversalRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.transactionreversal.FindPendingApprovalTransactionReversalResponse;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.TransactionReversalBean;
import com.sybase365.mobiliser.web.btpn.bank.common.dataproviders.ApprovalTxnReversalDataProvider;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.transaction.ApproveTxnReversalConfirmPage;
import com.sybase365.mobiliser.web.btpn.common.components.BtpnCustomPagingNavigator;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.ConverterUtils;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the ApproveTxnReversalPanel page for bank portals.
 * 
 * @author Narasa Reddy
 */
public class ApproveTxnReversalPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(ApproveTxnReversalPanel.class);

	protected BtpnMobiliserBasePage basePage;

	TransactionReversalBean txnReversalBean;

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

	public ApproveTxnReversalPanel(String id, BtpnMobiliserBasePage basePage, TransactionReversalBean bean) {
		super(id);
		this.basePage = basePage;
		this.txnReversalBean = bean;
		LOG.debug("ApproveTxnReversalPanel Started.");
		constructPanel();
	}

	protected void constructPanel() {
		final Form<ApproveTxnReversalPanel> form = new Form<ApproveTxnReversalPanel>("approveTxnReversalForm",
			new CompoundPropertyModel<ApproveTxnReversalPanel>(this));

		// Add feedback panel for Error Messages
		feedBack = new FeedbackPanel("errorMessages");
		feedBack.setOutputMarkupId(true);
		feedBack.setOutputMarkupPlaceholderTag(true);
		form.add(feedBack);

		String message = getLocalizer().getString("label.noDataFound", ApproveTxnReversalPanel.this);
		noRecordsLabel = new Label("emptyRecordsMessage", message);
		noRecordsLabel.setOutputMarkupId(true);
		noRecordsLabel.setOutputMarkupPlaceholderTag(true);
		noRecordsLabel.setVisible(false);
		form.add(noRecordsLabel);

		List<TransactionReversalBean> txnReversalList = fetchPendingTxnReversalList();
		if (!PortalUtils.exists(txnReversalList)) {
			noRecordsLabel.setVisible(true);
		} else {
			noRecordsLabel.setVisible(false);
		}

		// Add the approval container
		final WebMarkupContainer txnReversalContainer = new WebMarkupContainer("txnReversalContainer");
		approveTxnReversalDataView(txnReversalContainer);
		txnReversalContainer.setOutputMarkupId(true);
		form.add(txnReversalContainer);
		add(form);
	}

	/**
	 * This method adds the approveTxnReversalDataView for the transaction reversal, and also adds the sorting logic for
	 * data view
	 */
	protected void approveTxnReversalDataView(final WebMarkupContainer dataViewContainer) {
		// Create the approval View
		final ApprovalTxnReversalDataProvider approvalDataProvider = new ApprovalTxnReversalDataProvider("maker",
			fetchPendingTxnReversalList());

		final DataView<TransactionReversalBean> dataView = new ApprovalDataView(WICKET_ID_PAGEABLE,
			approvalDataProvider);
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
				final String displayTotalItemsText = ApproveTxnReversalPanel.this.getLocalizer().getString(
					"approval.totalitems.header", ApproveTxnReversalPanel.this);
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
		dataViewContainer.add(new OrderByBorder("orderByMaker", "maker", approvalDataProvider) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSortChanged() {
				dataView.setCurrentPage(0);
			}
		});
		dataViewContainer.add(new OrderByBorder("orderByTxnId", "txnId", approvalDataProvider) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSortChanged() {
				dataView.setCurrentPage(0);
			}
		});

		dataViewContainer.add(new OrderByBorder("orderByTxnName", "name", approvalDataProvider) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSortChanged() {
				dataView.setCurrentPage(0);
			}
		});

		dataViewContainer.add(new OrderByBorder("orderByUseCase", "useCaseId", approvalDataProvider) {
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
	private class ApprovalDataView extends DataView<TransactionReversalBean> {

		private static final long serialVersionUID = 1L;

		protected ApprovalDataView(String id, IDataProvider<TransactionReversalBean> dataProvider) {
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
		protected void populateItem(final Item<TransactionReversalBean> item) {
			final TransactionReversalBean entry = item.getModelObject();

			// Add the Maker
			item.add(new Label("maker", entry.getMaker()));
			// Add the Transaction Id
			item.add(new Label("transactionId", entry.getNewValue().getTransactionID()));
			// Add the Transaction Name
			item.add(new Label("transactionName", entry.getNewValue().getTransactionName()));
			// Add the UseCase
			item.add(new Label("useCase", entry.getNewValue().getUseCase()));
			// Add the Status
			item.add(new Label("status", entry.getNewValue().getStatus()));

			// Add the Approve Link
			final AjaxLink<TransactionReversalBean> approveLink = new AjaxLink<TransactionReversalBean>(
				WICKET_ID_APPROVE_LINK, item.getModel()) {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					final TransactionReversalBean approveBean = (TransactionReversalBean) item.getModelObject();
					approveBean.setSelectedLink("Approve");
					LOG.debug("TransactionReversalBean :" + approveBean);
					setResponsePage(new ApproveTxnReversalConfirmPage(approveBean));

				}
			};
			approveLink.add(new Label(WICKET_ID_APPROVE_LINK_NAME, "Approve"));
			item.add(approveLink);

			// Add the Reject Link
			final AjaxLink<TransactionReversalBean> rejectLink = new AjaxLink<TransactionReversalBean>(
				WICKET_ID_REJECT_LINK, item.getModel()) {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					final TransactionReversalBean rejectBean = (TransactionReversalBean) item.getModelObject();
					LOG.debug("TransactionReversalBean :" + rejectBean);
					rejectBean.setSelectedLink("Reject");
					setResponsePage(new ApproveTxnReversalConfirmPage(rejectBean));
				}
			};
			rejectLink.add(new Label(WICKET_ID_REJECT_LINK_NAME, "Reject"));
			item.add(rejectLink);

			final String cssStyle = item.getIndex() % 2 == 0 ? BtpnConstants.DATA_VIEW_EVEN_ROW_CSS : BtpnConstants.DATA_VIEW_ODD_ROW_CSS;
			item.add(new SimpleAttributeModifier("class", cssStyle));
		}

		@Override
		public boolean isVisible() {
			return ((ApprovalTxnReversalDataProvider) internalGetDataProvider()).size() != 0;

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
	 * calling findPendingApprovalTransactionReversal service from transaction reversal end point
	 * 
	 * @return
	 */
	public List<TransactionReversalBean> fetchPendingTxnReversalList() {
		List<TransactionReversalBean> approvalList = new ArrayList<TransactionReversalBean>();
		FindPendingApprovalTransactionReversalResponse response = null;
		try {
			final FindPendingApprovalTransactionReversalRequest request = basePage
				.getNewMobiliserRequest(FindPendingApprovalTransactionReversalRequest.class);
			long checkerId = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId();
			request.setCheckerId(checkerId);
			response = basePage.getTransactionReversalClient().findPendingApprovalTransactionReversal(request);
			if (basePage.evaluateBankPortalMobiliserResponse(response)
					&& response.getStatus().getCode() == BtpnConstants.STATUS_CODE) {
				return ConverterUtils.convertToTransactionReversalBean(response.getReversals());
			} else {
				error(response.getStatus().getValue());
			}
		} catch (Exception ex) {
			LOG.error("#An error occurred while calling findPendingApprovalTransactionReversal service", ex);
		}
		return approvalList;
	}

}
