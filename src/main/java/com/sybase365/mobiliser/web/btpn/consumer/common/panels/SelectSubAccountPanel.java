package com.sybase365.mobiliser.web.btpn.consumer.common.panels;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
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

import com.btpnwow.core.account.facade.contract.AccountInformationType;
import com.btpnwow.core.account.facade.contract.CustomerIdentificationType;
import com.btpnwow.core.account.facade.contract.FindCustomerAccountRequest;
import com.btpnwow.core.account.facade.contract.FindCustomerAccountResponse;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;
import com.sybase365.mobiliser.web.btpn.common.components.BtpnCustomPagingNavigator;
import com.sybase365.mobiliser.web.btpn.consumer.beans.SubAccountsBean;
import com.sybase365.mobiliser.web.btpn.consumer.common.dataproviders.SubAccountTransferDataProvider;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.fundtransfer.SubAccountTransferConfirmPage;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.fundtransfer.SubAccountTransferPage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;


public class SelectSubAccountPanel extends Panel {

	private static final long serialVersionUID = 1L;
	
	/** Application Logger */
    private static final Logger log = LoggerFactory.getLogger(SelectSubAccountPanel.class);

	protected BtpnMobiliserBasePage basePage;

	private static final String WICKET_ID_PAGEABLE = "pageable";
	private static final String WICKET_ID_LINK = "selectLink";
	private static final String WICKET_ID_LINK_NAME = "linkName";

	private static final String WICKET_ID_selectAccountNavigator = "selectAccountNavigator";
	private static final String WICKET_ID_selectAccountTotalItems = "selectAccountHeader";

	private String selectAccountTotalItemString;
	private int selectAccountStartIndex = 0;
	private int selectAccountEndIndex = 0;
	private Label selectAccountHeader;

	private BtpnCustomPagingNavigator navigator;

	private String message;
	private Label noRecordsLabel;

	SubAccountsBean subAccountBean;
	String selectedTransferType;
	String userName;
	
	Button cancelButton;
	Label txnTypeMessage;

	public SelectSubAccountPanel(String id, BtpnMobiliserBasePage basePage, SubAccountsBean bean) {
		super(id);
		this.basePage = basePage;
		this.subAccountBean = bean;
		this.selectedTransferType = subAccountBean.getTransferType().getId();
		userName = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getUsername();
		constructPanel();
	}

	protected void constructPanel() {
		
		final Form<SelectSubAccountPanel> form = new Form<SelectSubAccountPanel>("selectSubAccountForm",
			new CompoundPropertyModel<SelectSubAccountPanel>(this));
		
		form.add(new FeedbackPanel("errorMessages"));

		// Add transfer Type Message
		if (selectedTransferType.equals(BtpnConstants.RESOURCE_BUBDLE_TRANSFER_TYPE_PTOS)) {
			message = SelectSubAccountPanel.this.getLocalizer().getString("headLine.creditMessage",
				SelectSubAccountPanel.this);
		} else if (selectedTransferType.equals(BtpnConstants.RESOURCE_BUBDLE_TRANSFER_TYPE_STOP)) {
			message = SelectSubAccountPanel.this.getLocalizer().getString("headLine.debitMessage",
				SelectSubAccountPanel.this);
		}

		txnTypeMessage = new Label("txnTypeMessage", message);
		txnTypeMessage.setOutputMarkupId(true);
		txnTypeMessage.setOutputMarkupPlaceholderTag(true);
		txnTypeMessage.setVisible(true);
		form.add(txnTypeMessage);

		String message = getLocalizer().getString("noAccountsExistsMessage", SelectSubAccountPanel.this);
		noRecordsLabel = new Label("emptyRecoredsMessage", message);
		noRecordsLabel.setOutputMarkupId(true);
		noRecordsLabel.setOutputMarkupPlaceholderTag(true);
		noRecordsLabel.setVisible(false);
		form.add(noRecordsLabel);

		cancelButton = new Button("submitButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(new SubAccountTransferPage());
			};
		};
		cancelButton.setOutputMarkupId(true);
		cancelButton.setOutputMarkupPlaceholderTag(true);
		cancelButton.setVisible(true);
		form.add(cancelButton);

		// Add the sub Accounts container
		WebMarkupContainer subAccountsContainer = new WebMarkupContainer("selectAccountContainer");
		selectSubAccountsDataView(subAccountsContainer);
		subAccountsContainer.setOutputMarkupId(true);
		form.add(subAccountsContainer);

		add(form);
	}

	/**
	 * This method adds the subAccountsDataView
	 */
	protected void selectSubAccountsDataView(final WebMarkupContainer dataViewContainer) {
		
		List<SubAccountsBean> subAccountsList = getSubAccountsList();
		
		if (subAccountsList.isEmpty()) {
			noRecordsLabel.setVisible(true);
			cancelButton.setVisible(false);
			txnTypeMessage.setVisible(false);
		}
		
		final SubAccountTransferDataProvider subAccountsDataProvider = new SubAccountTransferDataProvider("name",
			subAccountsList);

		final DataView<SubAccountsBean> dataView = new SubAccountsDataView(WICKET_ID_PAGEABLE, subAccountsDataProvider);
		dataView.setItemsPerPage(20);

		// Add the navigation
		navigator = new BtpnCustomPagingNavigator(WICKET_ID_selectAccountNavigator, dataView) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return subAccountsDataProvider.size() != 0;
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
				final String displayTotalItemsText = SelectSubAccountPanel.this.getLocalizer().getString(
					"subAccounts.totalitems.header", SelectSubAccountPanel.this);
				return String.format(displayTotalItemsText, selectAccountTotalItemString, selectAccountStartIndex,
					selectAccountEndIndex);
			}

		};
		selectAccountHeader = new Label(WICKET_ID_selectAccountTotalItems, headerDisplayModel) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return subAccountsDataProvider.size() != 0;

			}
		};
		dataViewContainer.add(selectAccountHeader);
		selectAccountHeader.setOutputMarkupId(true);
		selectAccountHeader.setOutputMarkupPlaceholderTag(true);

		// Add the sort providers
		dataViewContainer.add(new OrderByBorder("orderByName", "name", subAccountsDataProvider) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSortChanged() {
				dataView.setCurrentPage(0);
			}
		});

		dataViewContainer.addOrReplace(dataView);
	}

	
	private class SubAccountsDataView extends DataView<SubAccountsBean> {

		private static final long serialVersionUID = 1L;

		protected SubAccountsDataView(String id, IDataProvider<SubAccountsBean> dataProvider) {
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
		protected void populateItem(final Item<SubAccountsBean> item) {
			final SubAccountsBean entry = item.getModelObject();
			item.setModel(new CompoundPropertyModel<SubAccountsBean>(entry));
			item.add(new Label("name"));
			item.add(new Label("accountId"));
			item.add(new Label("description"));
			item.add(new AmountLabel("balance"));

			// Add the select Link
			AjaxLink<SubAccountsBean> selectLink = new AjaxLink<SubAccountsBean>(WICKET_ID_LINK, item.getModel()) {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					SubAccountsBean subAccountsBean = (SubAccountsBean) item.getModelObject();
					setResponsePage(new SubAccountTransferConfirmPage(subAccountsBean, selectedTransferType));
				}
			};
			selectLink.add(new Label(WICKET_ID_LINK_NAME, "Select"));
			item.add(selectLink);
			String cssStyle = item.getIndex() % 2 == 0 ? BtpnConstants.DATA_VIEW_EVEN_ROW_CSS : BtpnConstants.DATA_VIEW_ODD_ROW_CSS;
			item.add(new SimpleAttributeModifier("class", cssStyle));
		}

		@Override
		public boolean isVisible() {
			return ((SubAccountTransferDataProvider) internalGetDataProvider()).size() != 0;

		}

		private void refreshTotalItemCount() {
			final int size = internalGetDataProvider().size();
			selectAccountTotalItemString = new Integer(size).toString();
			if (size > 0) {
				selectAccountStartIndex = getCurrentPage() * getItemsPerPage() + 1;
				selectAccountEndIndex = selectAccountStartIndex + getItemsPerPage() - 1;
				if (selectAccountEndIndex > size)
					selectAccountEndIndex = size;
			} else {
				selectAccountStartIndex = 0;
				selectAccountEndIndex = 0;
			}
		}
	}
	
	
	public List<SubAccountsBean> getSubAccountsList() {
		
		List<SubAccountsBean> beanList = new ArrayList<SubAccountsBean>();
		
		try {
			
			log.info(" ### (SelectSubAccountPanel::getSubAccountsList) ### ");
			
			final FindCustomerAccountRequest request = basePage.getNewMobiliserRequest(FindCustomerAccountRequest.class);
			CustomerIdentificationType obj = new CustomerIdentificationType();
			obj.setType(0);
			obj.setValue(userName);
			
			request.setIdentification(obj);
			request.getPaymentInstrumentType().add(1);
			request.getPaymentInstrumentClass().add(0);
			request.setFlags(1);
			
			final FindCustomerAccountResponse response = basePage.getAccountClient().find(request);
			log.info(" ### (SelectSubAccountPanel::getSubAccountsList) RESPONSE CODE ### "+response.getStatus().getCode());
			if (basePage.evaluateConsumerPortalMobiliserResponse(response)
					&& response.getStatus().getCode() == BtpnConstants.STATUS_CODE) {
				return beanList = convertToSubAccountsBean(response.getAccount());
			}
			
		} catch (Exception ex) {
			log.error("#An error occurred while calling (SelectSubAccountPanel::getAllSubAccounts) service.", ex);
		}
		
		return beanList;
	}
	
	
	public static List<SubAccountsBean> convertToSubAccountsBean(
			List<AccountInformationType> accountList) {
		
		final List<SubAccountsBean> beanList = new ArrayList<SubAccountsBean>();
		
		for (AccountInformationType responseBean : accountList) {
			final SubAccountsBean accountBean = new SubAccountsBean();
			log.info(" ### (SelectSubAccountPanel::getSubAccountsList) ALIAS ### "+responseBean.getAlias());
			
			if (responseBean.getAlias()==null){
				accountBean.setName("");
			}else{
				accountBean.setName(responseBean.getAlias());
			}
			
			log.info(" ### (SelectSubAccountPanel::getSubAccountsList) ACCT ID ### "+responseBean.getId());
			accountBean.setAccountId(String.valueOf(responseBean.getId()));
			log.info(" ### (SelectSubAccountPanel::getSubAccountsList) BALANCE ### "+responseBean.getBalance());
			accountBean.setBalance(responseBean.getBalance());
			accountBean.setDescription(String.valueOf(responseBean.getId()));
			beanList.add(accountBean);
		}
		
		return beanList;
	}

}
