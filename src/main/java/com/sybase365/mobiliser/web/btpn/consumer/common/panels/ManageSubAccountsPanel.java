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
import com.sybase365.mobiliser.web.btpn.consumer.common.dataproviders.ManageSubAccountsDataProvider;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.fundtransfer.AddAccountPage;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.fundtransfer.RemoveAccountConfirmPage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;


public class ManageSubAccountsPanel extends Panel {

	private static final long serialVersionUID = 1L;
	
	/** Application Logger */
    private static final Logger log = LoggerFactory
	    .getLogger(ManageSubAccountsPanel.class);

	protected BtpnMobiliserBasePage basePage;

	private static final String WICKET_ID_PAGEABLE = "pageable";

	private static final String WICKET_ID_LINK = "removeLink";

	private static final String WICKET_ID_LINK_NAME = "linkName";

	private static final String WICKET_ID_subAccountsNavigator = "subAccountsNavigator";

	private static final String WICKET_ID_SubAccountsTotalItems = "subAccountsHeader";

	private String subAccountsTotalItemString;

	private int subAccountsStartIndex = 0;

	private int subAccountsEndIndex = 0;

	private Label subAccountsHeader;

	private BtpnCustomPagingNavigator navigator;

	SubAccountsBean subAccountBean;

	private FeedbackPanel feedBack;

	private Label noRecordsLabel;

	public ManageSubAccountsPanel(String id, BtpnMobiliserBasePage basePage) {
		super(id);
		this.basePage = basePage;
		constructPanel();
	}

	protected void constructPanel() {
		
		final Form<ManageSubAccountsPanel> form = new Form<ManageSubAccountsPanel>("manageSubAccountsForm",
			new CompoundPropertyModel<ManageSubAccountsPanel>(this));
		
		feedBack = new FeedbackPanel("errorMessages");
		feedBack.setOutputMarkupId(true);
		form.add(feedBack);

		String message = getLocalizer().getString("label.noAccountsFound", ManageSubAccountsPanel.this);
		noRecordsLabel = new Label("emptyRecordsMessage", message);
		noRecordsLabel.setOutputMarkupId(true);
		noRecordsLabel.setVisible(false);
		form.add(noRecordsLabel);

		// Add the sub Accounts container
		WebMarkupContainer subAccountsContainer = new WebMarkupContainer("subAccountsContainer");
		manageSubAccountsDataView(subAccountsContainer);
		subAccountsContainer.setOutputMarkupId(true);
		form.add(subAccountsContainer);

		// Add Button
		form.add(new Button("submitButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(new AddAccountPage());
			};
		});

		add(form);
	}

	/**
	 * This method adds the subAccountsDataView
	 */
	protected void manageSubAccountsDataView(final WebMarkupContainer dataViewContainer) {
		
		List<SubAccountsBean> subAccountsList = getSubAccountsList();
		if (subAccountsList.isEmpty()) {
			noRecordsLabel.setVisible(true);
		}
		final ManageSubAccountsDataProvider subAccountsDataProvider = new ManageSubAccountsDataProvider("name",
			subAccountsList);
		final DataView<SubAccountsBean> dataView = new SubAccountsDataView(WICKET_ID_PAGEABLE, subAccountsDataProvider);
		dataView.setItemsPerPage(20);

		// Add the navigation
		navigator = new BtpnCustomPagingNavigator(WICKET_ID_subAccountsNavigator, dataView) {
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
				final String displayTotalItemsText = ManageSubAccountsPanel.this.getLocalizer().getString(
					"subAccounts.totalitems.header", ManageSubAccountsPanel.this);
				return String.format(displayTotalItemsText, subAccountsTotalItemString, subAccountsStartIndex,
					subAccountsEndIndex);
			}

		};
		subAccountsHeader = new Label(WICKET_ID_SubAccountsTotalItems, headerDisplayModel) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return subAccountsDataProvider.size() != 0;

			}
		};
		dataViewContainer.add(subAccountsHeader);
		subAccountsHeader.setOutputMarkupId(true);
		subAccountsHeader.setOutputMarkupPlaceholderTag(true);
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

	/**
	 * This is the subAccountsDataView for consumer Portal.
	 * 
	 * @author Narasa Reddy
	 */
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
			// Add the remove Link
			AjaxLink<SubAccountsBean> removeLink = new AjaxLink<SubAccountsBean>(WICKET_ID_LINK, item.getModel()) {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					SubAccountsBean subAccountsBean = (SubAccountsBean) item.getModelObject();
					Long balance = subAccountsBean.getBalance();
					if (balance != BtpnConstants.SUB_ACCOUNT_BALANCE) {
						error(getLocalizer().getString("remove.failedMessage", ManageSubAccountsPanel.this));
						target.addComponent(feedBack);
						return;
					}
					setResponsePage(new RemoveAccountConfirmPage(subAccountsBean));
				}
			};
			removeLink.add(new Label(WICKET_ID_LINK_NAME, "Remove"));
			item.add(removeLink);
			String cssStyle = item.getIndex() % 2 == 0 ? BtpnConstants.DATA_VIEW_EVEN_ROW_CSS : BtpnConstants.DATA_VIEW_ODD_ROW_CSS;
			item.add(new SimpleAttributeModifier("class", cssStyle));
		}

		@Override
		public boolean isVisible() {
			return ((ManageSubAccountsDataProvider) internalGetDataProvider()).size() != 0;

		}

		private void refreshTotalItemCount() {
			final int size = internalGetDataProvider().size();
			subAccountsTotalItemString = new Integer(size).toString();
			if (size > 0) {
				subAccountsStartIndex = getCurrentPage() * getItemsPerPage() + 1;
				subAccountsEndIndex = subAccountsStartIndex + getItemsPerPage() - 1;
				if (subAccountsEndIndex > size)
					subAccountsEndIndex = size;
			} else {
				subAccountsStartIndex = 0;
				subAccountsEndIndex = 0;
			}
		}
	}
	
	
	/**
	 * calling getAllSubAccounts service from fund transfer end point
	 */
	public List<SubAccountsBean> getSubAccountsList() {
		
		List<SubAccountsBean> beanList = new ArrayList<SubAccountsBean>();
		
		try {
			
			log.info(" ### (ManageSubAccountsPanel::getSubAccountsList) ### ");
			
			final String userName = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getUsername();
			
			final FindCustomerAccountRequest request = basePage.getNewMobiliserRequest(FindCustomerAccountRequest.class);
			CustomerIdentificationType obj = new CustomerIdentificationType();
			obj.setType(0);
			obj.setValue(userName);
			
			request.setIdentification(obj);
			request.getPaymentInstrumentType().add(1);
			request.getPaymentInstrumentClass().add(0);
			request.setFlags(1);
			
			final FindCustomerAccountResponse response = basePage.getAccountClient().find(request);
			log.info(" ### (ManageSubAccountsPanel::getSubAccountsList) RESPONSE CODE ### "+response.getStatus().getCode());
			if (basePage.evaluateConsumerPortalMobiliserResponse(response)
					&& response.getStatus().getCode() == BtpnConstants.STATUS_CODE) {
				return beanList = convertToSubAccountsBean(response.getAccount());
			}
			
		} catch (Exception ex) {
			log.error("#An error occurred while calling getAllSubAccounts service.", ex);
		}
		
		return beanList;
	}
	
	
	public static List<SubAccountsBean> convertToSubAccountsBean(
			List<AccountInformationType> accountList) {
		
		final List<SubAccountsBean> beanList = new ArrayList<SubAccountsBean>();
		
		for (AccountInformationType responseBean : accountList) {
			final SubAccountsBean accountBean = new SubAccountsBean();
			log.info(" ### (ManageSubAccountsPanel::getSubAccountsList) ALIAS ### "+responseBean.getAlias());
			
			if (responseBean.getAlias()==null){
				accountBean.setName("");
			}else{
				accountBean.setName(responseBean.getAlias());
			}
			log.info(" ### (ManageSubAccountsPanel::getSubAccountsList) ACCT ID ### "+responseBean.getId());
			accountBean.setAccountId(String.valueOf(responseBean.getId()));
			log.info(" ### (ManageSubAccountsPanel::getSubAccountsList) BALANCE ### "+responseBean.getBalance());
			accountBean.setBalance(responseBean.getBalance());
			accountBean.setDescription(String.valueOf(responseBean.getId()));
			beanList.add(accountBean);
		}
		
		return beanList;
	}
	

}
