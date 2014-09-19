package com.sybase365.mobiliser.web.btpn.agent.common.panels;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.core.account.facade.api.AccountFacade;
import com.btpnwow.core.account.facade.contract.AccountIdentificationType;
import com.btpnwow.core.account.facade.contract.FindTransactionHistoryRequest;
import com.btpnwow.core.account.facade.contract.FindTransactionHistoryResponse;
import com.btpnwow.core.account.facade.contract.TransactionHistoryFindView;
import com.btpnwow.core.customer.facade.api.CustomerFacade;
import com.btpnwow.core.customer.facade.contract.CustomerIdentificationType;
import com.btpnwow.core.customer.facade.contract.GetCustomerExRequest;
import com.btpnwow.core.customer.facade.contract.GetCustomerExResponse;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.agent.beans.AgentTxnBean;
import com.sybase365.mobiliser.web.btpn.agent.beans.AgentTxnDataBean;
import com.sybase365.mobiliser.web.btpn.agent.common.dataprovider.AgentTxnDataProvider;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.common.behaviours.DateHeaderContributor;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;
import com.sybase365.mobiliser.web.btpn.common.components.BtpnCustomPagingNavigator;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnLocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.btpn.util.BtpnOrderByOrder;
import com.sybase365.mobiliser.web.btpn.util.BtpnUtils;
import com.sybase365.mobiliser.web.btpn.util.FromToDateValidator;
import com.sybase365.mobiliser.web.util.PortalUtils;


public class AgentTransactionDetailsPanel extends Panel {

	private static final Logger log = LoggerFactory.getLogger(AgentTransactionDetailsPanel.class);

	private static final long serialVersionUID = 1L;
	protected BtpnMobiliserBasePage basePage;

	private AgentTxnBean searchData;

	private static final String WICKET_ID_PAGEABLE = "pageable";
	private static final String WICKET_ID_approvalNavigator = "approvalNavigator";
	private static final String WICKET_ID_approvalTotalItems = "approvalHeader";

	private String approvalTotalItemString;
	private int approvalStartIndex = 0;
	private int approvalEndIndex = 0;
	private Label approvalHeader;
	private BtpnCustomPagingNavigator navigator;
	private FeedbackPanel feedBack;

	DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	
	@SpringBean(name = "customerClient")
	private CustomerFacade customerClient;
	
	@SpringBean(name="accountClient")
	private AccountFacade accountClient;
	

	public AgentTransactionDetailsPanel(String id) {
		super(id);
	}

	public AgentTransactionDetailsPanel(String id, BtpnMobiliserBasePage basePage) {
		super(id);
		log.info("Calling AgentTransactionDetailsPanel ");
		this.basePage = basePage;
		this.searchData = new AgentTxnBean();
		// Populate LogedIn Agent Id
		long logedInAgentId = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId();
		searchData.setAgentId(String.valueOf(logedInAgentId));
		addDateHeaderContributor();
		constructPanel();
	}

	protected void addDateHeaderContributor() {
		final String chooseDtTxt = this.getLocalizer().getString("datepicker.chooseDate", basePage);
		final String locale = this.getLocale().getLanguage();
		add(new HeaderContributor(new DateHeaderContributor(locale, BtpnConstants.DATE_FORMAT_PATTERN_PICKER,
			chooseDtTxt)));
	}

	protected void constructPanel() {
		
		final Form<AgentTransactionDetailsPanel> form = new Form<AgentTransactionDetailsPanel>("agentTxnHistoryForm",
			new CompoundPropertyModel<AgentTransactionDetailsPanel>(this));

		// Add feedback panel for Error Messages
		feedBack = new FeedbackPanel("errorMessages");
		feedBack.setOutputMarkupId(true);
		feedBack.setOutputMarkupPlaceholderTag(true);
		form.add(feedBack);

		final DateTextField fromdDate = (DateTextField) DateTextField
			.forDatePattern("searchData.fromDate", BtpnConstants.ID_EXPIRY_DATE_PATTERN).setRequired(true)
			.add(new ErrorIndicator());
		final DateTextField toDate = (DateTextField) DateTextField
			.forDatePattern("searchData.toDate", BtpnConstants.ID_EXPIRY_DATE_PATTERN).setRequired(true)
			.add(new ErrorIndicator());

		form.add(new FromToDateValidator(fromdDate, toDate));

		TextField<String> subAgentMobile = new TextField<String>("searchData.subAgentMobile");

		form.add(fromdDate);
		form.add(toDate);
		form.add(subAgentMobile);

		final IChoiceRenderer<CodeValue> codeValueChoiceRender = new ChoiceRenderer<CodeValue>(
			BtpnConstants.DISPLAY_EXPRESSION, BtpnConstants.ID_EXPRESSION);

		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("searchData.txnType", CodeValue.class,
			BtpnConstants.RESOURCE_BUNDLE_TXN_TYPE, this).setChoiceRenderer(codeValueChoiceRender).add(
			new ErrorIndicator()));

		final WebMarkupContainer txnDataContainer = new WebMarkupContainer("txnDataContainer");
		showSearchTxnDataView(txnDataContainer);
		txnDataContainer.setOutputMarkupId(true);
		txnDataContainer.setOutputMarkupPlaceholderTag(true);
		txnDataContainer.setVisible(false);
		form.add(txnDataContainer);

		form.add(new AjaxButton("searchButton") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				handleSearchSalaryData(target, txnDataContainer);
				target.addComponent(feedBack);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.addComponent(feedBack);
			}
		});
		add(form);
	}

	protected void showSearchTxnDataView(final WebMarkupContainer dataViewContainer) {
		
		final AgentTxnDataProvider agentTxnDataProvider = new AgentTxnDataProvider("date", searchData.getAgentTxnDatalist());

		final DataView<AgentTxnDataBean> dataView = new TxnDataView(WICKET_ID_PAGEABLE, agentTxnDataProvider);
		dataView.setItemsPerPage(20);

		navigator = new BtpnCustomPagingNavigator(WICKET_ID_approvalNavigator, dataView) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return agentTxnDataProvider.size() != 0;
			}
		};
		navigator.setOutputMarkupId(true);
		navigator.setOutputMarkupPlaceholderTag(true);
		dataViewContainer.add(navigator);

		dataViewContainer.add(new Label("no.items", getLocalizer().getString("no.items", this)) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return agentTxnDataProvider.size() == 0;

			}
		}.setRenderBodyOnly(true));

		IModel<String> headerDisplayModel = new AbstractReadOnlyModel<String>() {

			private static final long serialVersionUID = 1L;

			@Override
			public String getObject() {
				final String displayTotalItemsText = AgentTransactionDetailsPanel.this.getLocalizer().getString(
					"approval.totalitems.header", AgentTransactionDetailsPanel.this);
				return String.format(displayTotalItemsText, approvalTotalItemString, approvalStartIndex,
					approvalEndIndex);
			}
		};
		approvalHeader = new Label(WICKET_ID_approvalTotalItems, headerDisplayModel) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return agentTxnDataProvider.size() != 0;
			}
		};
		
		dataViewContainer.add(approvalHeader);
		approvalHeader.setOutputMarkupId(true);
		approvalHeader.setOutputMarkupPlaceholderTag(true);

		// Add the sort providers
		dataViewContainer.add(new BtpnOrderByOrder("orderByDate", "date", agentTxnDataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByTxnId", "txnId", agentTxnDataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByTxnType", "txnType", agentTxnDataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByAmount", "amount", agentTxnDataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByFee", "fee", agentTxnDataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByDesc", "description", agentTxnDataProvider, dataView));
		dataViewContainer.addOrReplace(dataView);
	}

	private class TxnDataView extends DataView<AgentTxnDataBean> {
		private static final long serialVersionUID = 1L;

		protected TxnDataView(String id, IDataProvider<AgentTxnDataBean> dataProvider) {
			super(id, dataProvider);
			setOutputMarkupId(true);
			setOutputMarkupPlaceholderTag(true);

		}

		@Override
		protected void onBeforeRender() {
			final AgentTxnDataProvider dataProvider = (AgentTxnDataProvider) internalGetDataProvider();
			dataProvider.setTxnDataList(searchData.getAgentTxnDatalist());
			refreshTotalItemCount();
			super.onBeforeRender();
		}

		@Override
		protected void populateItem(final Item<AgentTxnDataBean> item) {
			AgentTxnDataBean dataBean = item.getModelObject();
			item.setModel(new CompoundPropertyModel<AgentTxnDataBean>(dataBean));
			DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			item.add(new Label("date", df.format(dataBean.getDate())));
			item.add(new Label("txnId"));
			item.add(new Label("txnType.value"));
			item.add(new AmountLabel("txnAmount"));
			item.add(new Label("fee"));
			item.add(new Label("description"));

			final String cssStyle = item.getIndex() % 2 == 0 ? BtpnConstants.DATA_VIEW_EVEN_ROW_CSS : BtpnConstants.DATA_VIEW_ODD_ROW_CSS;
			item.add(new SimpleAttributeModifier("class", cssStyle));
		}

		@Override
		public boolean isVisible() {
			return ((AgentTxnDataProvider) internalGetDataProvider()).size() != 0;

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


	
	protected void handleSearchSalaryData(AjaxRequestTarget target, final WebMarkupContainer container) {
		
		container.setVisible(true);
		target.addComponent(container);
		
		try {
			
			searchData.getAgentTxnDatalist().clear();
			
			String userName = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getUsername();
			log.info(" ### (AgentTransactionDetailsPanel::handleSearchSalaryData) USER NAME ### " +userName);
			Long customerId = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId();
			log.info(" ### (AgentTransactionDetailsPanel::handleSearchSalaryData) CUST ID ### " +customerId);
			GetCustomerExResponse response = null;
			
			GetCustomerExRequest request = basePage.getNewMobiliserRequest(GetCustomerExRequest.class);
			CustomerIdentificationType cit = new CustomerIdentificationType();
			cit.setType(0);
			cit.setValue(userName);
			
			request.setFlags(0);
			request.setIdentification(cit);
			
			response = customerClient.get(request);
			log.info(" ### (AgentTransactionDetailPane::handleSearchSalaryData) RESPONSE ### " + response.getStatus().getCode());
			Long parentId = response.getInformation().getParentId();
			if (customerId.equals(parentId)){
				final FindTransactionHistoryRequest req = FindTransactionHistoryRequest.class.newInstance(); 
				AccountIdentificationType ait = new AccountIdentificationType();
				ait.setType("MSISDN");
				ait.setFlags(0);
				ait.setValue(userName);
				req.getAccount().add(ait);
				String useCaseId = searchData.getTxnType().getId();
				log.info(" ### (AgentTransactionDetailPane::handleSearchSalaryData) USE CASE ID ### " + useCaseId);
				req.getUseCaseId().add(Integer.parseInt(useCaseId));
				req.setDateBegin(PortalUtils.getSaveXMLGregorianCalendarFromDate(searchData.getFromDate(), null));
				req.setDateEnd(PortalUtils.getSaveXMLGregorianCalendarToDate(searchData.getToDate(), null));
				req.setStart(0);
				req.setLength(Integer.MAX_VALUE);
				req.setOldestToNewest(false);
				
				final FindTransactionHistoryResponse resp = accountClient.findTransactionHistory(req);
				log.info(" ### (AgentTransactionDetailPane::handleSearchSalaryData) RESPONSE 2 ### " +resp.getStatus().getCode());
				if (basePage.evaluateAgentPortalMobiliserResponse(resp)) {
					convertToTransactionDataBean(resp.getTransaction());
				}
			}
			
			log.info(" ### (AgentTransactionDetailPane::handleSearchSalaryData) SUB AGENT ### "+searchData.getSubAgentMobile());
			
			if(searchData.getSubAgentMobile()==null){
				log.info(" ### (AgentTransactionDetailPane::handleSearchSalaryData) WKWKWKWK ### ");
				final FindTransactionHistoryRequest req = FindTransactionHistoryRequest.class.newInstance(); 
				AccountIdentificationType ait = new AccountIdentificationType();
				ait.setType("MSISDN");
				ait.setFlags(0);
				ait.setValue(userName);
				req.getAccount().add(ait);
				if(searchData.getTxnType()!=null){
					String useCaseId = searchData.getTxnType().getId();
					log.info(" ### (AgentTransactionDetailPane::handleSearchSalaryData) USE CASE ID ### " + useCaseId);
					req.getUseCaseId().add(Integer.parseInt(useCaseId));
				}
				req.setDateBegin(PortalUtils.getSaveXMLGregorianCalendarFromDate(searchData.getFromDate(), null));
				req.setDateEnd(PortalUtils.getSaveXMLGregorianCalendarToDate(searchData.getToDate(), null));
				req.setStart(0);
				req.setLength(Integer.MAX_VALUE);
				req.setOldestToNewest(false);
				log.info(" ### (AgentTransactionDetailPane::handleSearchSalaryData) BEFORE SERVICE ### ");
				final FindTransactionHistoryResponse resp = accountClient.findTransactionHistory(req);
				log.info(" ### (AgentTransactionDetailPane::handleSearchSalaryData) RESPONSE WKWKWKWK ### " +resp.getStatus().getCode());
				if (basePage.evaluateAgentPortalMobiliserResponse(resp)) {
					convertToTransactionDataBean(resp.getTransaction());
				}
			}
			
		} catch (Exception ex) {
			log.error("Error while calling agentPortalTransactionHistory from support EndPoint :");
		}
		
	}

	public void convertToTransactionDataBean(
			final List<TransactionHistoryFindView> responseList) {
		
		searchData.getAgentTxnDatalist().clear();
		
		BtpnMobiliserBasePage component = null;
		
		for (TransactionHistoryFindView response : responseList) {
			final AgentTxnDataBean bean = new AgentTxnDataBean();
			log.info("### (ConverterUtils::convertToConsumerTransactionHistoryBeanList) ID ###" +response.getId());
			bean.setTxnId(String.valueOf(response.getId()));
			log.info("### (ConverterUtils::convertToConsumerTransactionHistoryBeanList) DATE TIME ###" +response.getTransactionDateTime());
			bean.setDate(PortalUtils.getSaveDate(response.getTransactionDateTime()));	
			
			String useCaseId = String.valueOf(response.getUseCaseId());
			log.info("### (ConverterUtils::convertToConsumerTransactionHistoryBeanList) USE CASE ID ###" +useCaseId);
			bean.setTxnType(new CodeValue(useCaseId, BtpnUtils.getDropdownValueFromId( 
					basePage.getLookupMapUtility().getLookupNamesMap(BtpnConstants.RESOURCE_BUNDLE_TXN_TYPE, component),
					BtpnConstants.RESOURCE_BUNDLE_TXN_TYPE + "." + useCaseId)));
			
			log.info("### (ConverterUtils::convertToConsumerTransactionHistoryBeanList) AMOUNT ###" +response.getAmount());
			bean.setTxnAmount(response.getAmount());
			log.info("### (ConverterUtils::convertToConsumerTransactionHistoryBeanList) FEE ###" +response.getFee());
			bean.setFee(response.getFee());
			log.info("### (ConverterUtils::convertToConsumerTransactionHistoryBeanList) DESC ###" +response.getDescription());
			bean.setDescription(response.getDescription());
			
			searchData.getAgentTxnDatalist().add(bean);
		}
	}
	
}
