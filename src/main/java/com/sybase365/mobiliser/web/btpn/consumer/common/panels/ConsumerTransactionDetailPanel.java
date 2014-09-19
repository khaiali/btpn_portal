package com.sybase365.mobiliser.web.btpn.consumer.common.panels;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.PopupSettings;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.resource.ByteArrayResource;
import org.apache.wicket.util.resource.IResourceStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.core.account.facade.contract.AccountIdentificationType;
import com.btpnwow.core.account.facade.contract.FindTransactionHistoryRequest;
import com.btpnwow.core.account.facade.contract.FindTransactionHistoryResponse;
import com.btpnwow.core.account.facade.contract.TransactionHistoryFindView;
import com.btpnwow.portal.common.util.MobiliserUtils;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.bank.beans.ConsumerTransactionBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.ConsumerTransactionRequestBean;
import com.sybase365.mobiliser.web.btpn.bank.common.dataproviders.ConsumerTransactionDataProvider;
import com.sybase365.mobiliser.web.btpn.common.behaviours.AJAXDownload;
import com.sybase365.mobiliser.web.btpn.common.behaviours.DateHeaderContributor;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;
import com.sybase365.mobiliser.web.btpn.common.components.BtpnCustomPagingNavigator;
import com.sybase365.mobiliser.web.btpn.common.components.MonthDropdownChoice;
import com.sybase365.mobiliser.web.btpn.common.components.SubAccountDropdownChoice;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.transaction.TransactionDetailsPopupPage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnLocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.btpn.util.BtpnOrderByOrder;
import com.sybase365.mobiliser.web.btpn.util.BtpnUtils;
import com.sybase365.mobiliser.web.btpn.util.FromToDateValidator;
import com.sybase365.mobiliser.web.util.PortalUtils;


public class ConsumerTransactionDetailPanel extends Panel {

	private static final Logger log = LoggerFactory.getLogger(ConsumerTransactionDetailPanel.class);
	private static final long serialVersionUID = 1L;
	
	protected BtpnMobiliserBasePage basePage;

	private static final String WICKET_ID_PAGEABLE = "pageable";
	private static final String WICKET_ID_approvalNavigator = "approvalNavigator";
	private static final String WICKET_ID_approvalTotalItems = "approvalHeader";

	private String approvalTotalItemString;
	private int approvalStartIndex = 0;
	private int approvalEndIndex = 0;
	private Label approvalHeader;

	private BtpnCustomPagingNavigator navigator;
	private ConsumerTransactionRequestBean searchData;

	private FeedbackPanel feedbackPanel;
	private TransactionsDownload download;
	private Component monthComponent;
	private String userName;
	
	public ConsumerTransactionDetailPanel(String id, BtpnMobiliserBasePage basePage) {
		super(id);
		this.basePage = basePage;
		this.searchData = new ConsumerTransactionRequestBean();
		addDateHeaderContributor();
		userName = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getUsername();
		log.info("### ConsumerTransactionDetailPanel ###" +userName);
		constructPanel();
	}

	/**
	 * Add the Date Header Contributor for Dates.
	 */
	protected void addDateHeaderContributor() {
		final String chooseDtTxt = this.getLocalizer().getString("datepicker.chooseDate", basePage);
		final String locale = this.getLocale().getLanguage();
		add(new HeaderContributor(new DateHeaderContributor(locale, BtpnConstants.DATE_FORMAT_PATTERN_PICKER,
			chooseDtTxt)));
	}

	/**
	 * Constructs the panel.
	 */
	protected void constructPanel() {

		final Form<ConsumerTransactionDetailPanel> form = new Form<ConsumerTransactionDetailPanel>("consumerTxnPanel",
			new CompoundPropertyModel<ConsumerTransactionDetailPanel>(this));

		// Error Messages
		form.add(feedbackPanel = new FeedbackPanel("errorMessages"));
		feedbackPanel.setOutputMarkupId(true);

		// Add the Choice Renderer
		final IChoiceRenderer<CodeValue> codeValueChoiceRender = new ChoiceRenderer<CodeValue>(
			BtpnConstants.DISPLAY_EXPRESSION, BtpnConstants.ID_EXPRESSION);

		// Radio buttons for Filter Type
		final RadioGroup<String> rg = new RadioGroup<String>("searchData.filterType");
		rg.add(new Radio<String>("radio.month", Model.of(BtpnConstants.FREQUENCY_TYPE_MONTH)));
		rg.add(new Radio<String>("radio.date", Model.of(BtpnConstants.FREQUENCY_TYPE_DAILY)));
		form.add(rg);
		rg.setDefaultModelObject(BtpnConstants.FREQUENCY_TYPE_MONTH);
		rg.setMarkupId("filterTypeMarkupId");

		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("searchData.txnType", CodeValue.class,
			BtpnConstants.RESOURCE_BUNDLE_USE_CASES, this, false, true).setChoiceRenderer(codeValueChoiceRender).add(
			new ErrorIndicator()));

		// Month Markup Container
		final WebMarkupContainer monthMarkupContainer = new WebMarkupContainer("monthMarkupContainer");
		monthMarkupContainer.add(monthComponent = new MonthDropdownChoice("searchData.month", false, false,
			this.basePage.getCustomerPortalPrefsConfig().getTransactionStartMonth(), this.basePage
				.getCustomerPortalPrefsConfig().getTransactionStartYear()).setRequired(true).add(new ErrorIndicator()));
		monthComponent.setOutputMarkupPlaceholderTag(true);
		monthComponent.setOutputMarkupId(true);
		monthMarkupContainer.setOutputMarkupPlaceholderTag(true);
		monthMarkupContainer.setOutputMarkupId(true);
		monthMarkupContainer.setVisible(true);
		form.add(monthMarkupContainer);

		// Date Markup Container
		final WebMarkupContainer dateMarkupContainer = new WebMarkupContainer("dateMarkupContainer");
		final DateTextField fromdDate = (DateTextField) DateTextField
			.forDatePattern("searchData.fromDate", BtpnConstants.ID_EXPIRY_DATE_PATTERN).setRequired(true)
			.add(new ErrorIndicator());
		final DateTextField toDate = (DateTextField) DateTextField
			.forDatePattern("searchData.toDate", BtpnConstants.ID_EXPIRY_DATE_PATTERN).setRequired(true)
			.add(new ErrorIndicator());
		fromdDate.setMarkupId("fromDate");
		toDate.setMarkupId("toDate");
		dateMarkupContainer.add(fromdDate);
		dateMarkupContainer.add(toDate);
		dateMarkupContainer.setOutputMarkupPlaceholderTag(true);
		dateMarkupContainer.setOutputMarkupId(true);
		dateMarkupContainer.setVisible(false);
		form.add(dateMarkupContainer);
		form.add(new FromToDateValidator(fromdDate, toDate));
		rg.add(new FilterTypeChoiceComponentUpdatingBehavior(monthMarkupContainer, dateMarkupContainer));

		// Dropdown choice for use case name
		log.info("### USER NAME ###" +userName);
		form.add(new SubAccountDropdownChoice("searchData.subAccount", false, true, userName).add(new ErrorIndicator()));
		
		// Transaction Search Container
		final WebMarkupContainer transactionDataContainer = new WebMarkupContainer("txnDataContainer");
		showSearchTxnDataView(transactionDataContainer);
		transactionDataContainer.setOutputMarkupId(true);
		transactionDataContainer.setOutputMarkupPlaceholderTag(true);
		transactionDataContainer.setVisible(false);

		// Export Container
		final WebMarkupContainer exportContainer = new WebMarkupContainer("exportContainer") {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return searchData != null && searchData.getTxnDataList().size() != 0;
			}
		};
		;
		exportContainer.add(addCSVAjaxLink());
		transactionDataContainer.add(exportContainer);

		form.add(transactionDataContainer);

		// Add add Button
		form.add(new AjaxButton("searchButton") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				handleSearchTransactionData(target, transactionDataContainer);
				if (monthMarkupContainer.isVisible()) {
					target.addComponent(monthComponent);
				} else {
					renderDateJavascipt(target);
					target.addComponent(dateMarkupContainer);
				}
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				if (monthMarkupContainer.isVisible()) {
					target.addComponent(monthComponent);
				} else {
					renderDateJavascipt(target);
					target.addComponent(dateMarkupContainer);
				}
				target.addComponent(feedbackPanel);
			}
		});
		download = new TransactionsDownload();
		form.add(download);
		add(form);
	}

	/**
	 * Add the CSV Ajax Link.
	 * 
	 * @return AjaxLink AjaxLink
	 */
	protected AjaxLink<Void> addCSVAjaxLink() {
		AjaxLink<Void> link = new AjaxLink<Void>("csvLink") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				download.initiate(target);
			}
		};
		return link;
	}

	protected void showSearchTxnDataView(final WebMarkupContainer dataViewContainer) {
		// Create the Salary Data provider
		final ConsumerTransactionDataProvider consumerTxnDataProvider = new ConsumerTransactionDataProvider("date",
			searchData.getTxnDataList());

		final DataView<ConsumerTransactionBean> dataView = new TxnDataView(WICKET_ID_PAGEABLE, consumerTxnDataProvider);
		dataView.setItemsPerPage(20);

		// Add the navigation
		navigator = new BtpnCustomPagingNavigator(WICKET_ID_approvalNavigator, dataView) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return searchData != null && searchData.getTxnDataList().size() != 0;
			}
		};
		navigator.setOutputMarkupId(true);
		navigator.setOutputMarkupPlaceholderTag(true);
		dataViewContainer.add(navigator);
		dataViewContainer.add(new Label("no.items", getLocalizer().getString("no.items", this)) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return searchData != null && searchData.getTxnDataList().size() == 0;

			}
		}.setRenderBodyOnly(true));

		// Add the header
		IModel<String> headerDisplayModel = new AbstractReadOnlyModel<String>() {

			private static final long serialVersionUID = 1L;

			@Override
			public String getObject() {
				final String displayTotalItemsText = ConsumerTransactionDetailPanel.this.getLocalizer().getString(
					"approval.totalitems.header", ConsumerTransactionDetailPanel.this);
				return String.format(displayTotalItemsText, approvalTotalItemString, approvalStartIndex,
					approvalEndIndex);
			}
		};

		// Add the approval header
		approvalHeader = new Label(WICKET_ID_approvalTotalItems, headerDisplayModel) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return searchData != null && searchData.getTxnDataList().size() != 0;
			}
		};
		dataViewContainer.add(approvalHeader);
		approvalHeader.setOutputMarkupId(true);
		approvalHeader.setOutputMarkupPlaceholderTag(true);

		// Add the sort providers
		dataViewContainer.add(new BtpnOrderByOrder("orderByDate", "date", consumerTxnDataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByTxnId", "txnId", consumerTxnDataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByType", "type", consumerTxnDataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByDetails", "details", consumerTxnDataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByFee", "fee", consumerTxnDataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByDebitDesc", "debitDesc", consumerTxnDataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByAmount", "amount", consumerTxnDataProvider, dataView));
//		dataViewContainer.add(new BtpnOrderByOrder("orderByErrorCode", "errorCode", consumerTxnDataProvider, dataView));
//		dataViewContainer.add(new BtpnOrderByOrder("orderByName", "name", consumerTxnDataProvider, dataView));
		
		// Add the Data View
		dataViewContainer.addOrReplace(dataView);
	}

	/**
	 * This is the View For Transaction Search Data results
	 * 
	 * @author Vikram Gunda
	 */
	private class TxnDataView extends DataView<ConsumerTransactionBean> {

		private static final long serialVersionUID = 1L;

		private static final String WICKET_ID_LINK_NAME = "detailsLinkName";

		protected TxnDataView(String id, IDataProvider<ConsumerTransactionBean> dataProvider) {
			super(id, dataProvider);
			setOutputMarkupId(true);
			setOutputMarkupPlaceholderTag(true);

		}

		@Override
		protected void onBeforeRender() {
			final ConsumerTransactionDataProvider dataProvider = (ConsumerTransactionDataProvider) internalGetDataProvider();
			dataProvider.setTxnDataList(searchData.getTxnDataList());
			refreshTotalItemCount();
			super.onBeforeRender();
		}

		@Override
		protected void populateItem(final Item<ConsumerTransactionBean> item) {
			ConsumerTransactionBean dataBean = item.getModelObject();
			item.setModel(new CompoundPropertyModel<ConsumerTransactionBean>(dataBean));
			DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			item.add(new Label("date", df.format(dataBean.getDate())));
			item.add(new Label("txnId"));
			item.add(new Label("type"));
			item.add(new Label("details"));
			item.add(new AmountLabel("fee"));
			item.add(new Label("debitDesc"));
			item.add(new AmountLabel("amount"));
			item.add(new Label("status"));

			final PopupSettings popupSettings = new PopupSettings("popuppagemap").setHeight(450).setWidth(350)
				.setLeft(400).setTop(200);
			final Link<ConsumerTransactionBean> link = new Link<ConsumerTransactionBean>("detailsLink", item.getModel()) {
				private static final long serialVersionUID = 1L;

				public void onClick() {
					setResponsePage(new TransactionDetailsPopupPage(item.getModelObject()));
				}
			};
			link.setPopupSettings(popupSettings);
			link.add(new Label(WICKET_ID_LINK_NAME, getLocalizer().getString("label.details", this)));
			item.add(link);

			// Add the UseCase
			final String cssStyle = item.getIndex() % 2 == 0 ? BtpnConstants.DATA_VIEW_EVEN_ROW_CSS : BtpnConstants.DATA_VIEW_ODD_ROW_CSS;
			item.add(new SimpleAttributeModifier("class", cssStyle));
		}

		@Override
		public boolean isVisible() {
			final ConsumerTransactionDataProvider dataProvider = (ConsumerTransactionDataProvider) internalGetDataProvider();
			dataProvider.setTxnDataList(searchData.getTxnDataList());
			return searchData.getTxnDataList().size() != 0;
		}

		private void refreshTotalItemCount() {
			final int size = searchData.getTxnDataList().size();
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

	
	private class FilterTypeChoiceComponentUpdatingBehavior extends AjaxFormChoiceComponentUpdatingBehavior {

		private static final long serialVersionUID = 1L;
		private WebMarkupContainer monthContainer;
		private WebMarkupContainer dateContainer;

		public FilterTypeChoiceComponentUpdatingBehavior(final WebMarkupContainer monthContainer,
			final WebMarkupContainer dateContainer) {
			super();
			this.dateContainer = dateContainer;
			this.monthContainer = monthContainer;
		}

		@Override
		protected void onUpdate(AjaxRequestTarget target) {
			final String filterType = searchData.getFilterType();
			if (PortalUtils.exists(filterType) && filterType.equals(BtpnConstants.FREQUENCY_TYPE_MONTH)) {
				monthContainer.setVisible(true);
				dateContainer.setVisible(false);
			} else {
				renderDateJavascipt(target);
				dateContainer.setVisible(true);
				monthContainer.setVisible(false);
			}
			target.addComponent(monthContainer);
			target.addComponent(dateContainer);
			target.addComponent(feedbackPanel);
		}
	}
	
	private void handleSearchTransactionData(final AjaxRequestTarget target, final WebMarkupContainer container) {
		
		try {
			
			log.info("### ConsumerTransactionDetailPanel::handleSearchTransactionData USER NAME ###" +userName);
			Integer txnType = null;
			if (PortalUtils.exists(searchData.getTxnType())){
				if (PortalUtils.exists(searchData.getTxnType().getId()))
				txnType = Integer.parseInt(searchData.getTxnType().getId());
				log.info("### ConsumerTransactionDetailPanel::handleSearchTransactionData TXN TYPE ###" + 
						txnType);
			}
			
			// Create request from base class
			final FindTransactionHistoryRequest request = FindTransactionHistoryRequest.class.newInstance();
			request.setCallback(null);
			request.setConversationId(UUID.randomUUID().toString());
			request.setOrigin("mobiliser-web");
			request.setRepeat(Boolean.FALSE);
			request.setTraceNo(UUID.randomUUID().toString());
			log.info("### (ConsumerTransactionDetailPanel::handleSearchTransactionData) AFTER BP ###");
			
			AccountIdentificationType obj = new AccountIdentificationType();
			obj.setType("MSISDN");
			obj.setFlags(0);
			obj.setValue(userName);
			request.getAccount().add(obj);
			request.getUseCaseId().add(txnType);
			
			log.info("### (ConsumerTransactionDetailPanel::handleSearchTransactionData) FT ###" +searchData.getFilterType());
			if (searchData.getFilterType().equals(BtpnConstants.FREQUENCY_TYPE_DAILY)) {
				request.setDateBegin(PortalUtils.getSaveXMLGregorianCalendarFromDate(searchData.getFromDate(), null));
				request.setDateEnd(PortalUtils.getSaveXMLGregorianCalendarToDate(searchData.getToDate(), null));
			} else {
				final String[] monthYear = searchData.getMonth().getId().split("-");
				log.info("### MONTH YEAR ###" + monthYear[0] + " " + monthYear[1]);
				request.setDateBegin(PortalUtils.getXmlFromDateOfMonth(null, monthYear[0], monthYear[1]));
				request.setDateEnd(PortalUtils.getXmlToDateOfMonth(null, monthYear[0], monthYear[1]));		
			}
			
			request.setStart(0);
			request.setLength(Integer.MAX_VALUE);
			request.setOldestToNewest(false);
			
			log.info("### (ConsumerTransactionDetailPanel::handleSearchTransactionData) BEFORE SERVICE ###");			
			final FindTransactionHistoryResponse response = basePage.getAccountClient().findTransactionHistory(request);
			log.info("### (ConsumerTransactionDetailPanel::handleSearchTransactionData) AFTER SERVICE ###" +response.getStatus().getCode());
			if (basePage.evaluateConsumerPortalMobiliserResponse(response)) {
				searchData.setTxnDataList(convertToConsumerTrxHistoryBeanList(response.getTransaction()));
			} else {
				error(getLocalizer().getString("error.transactions", ConsumerTransactionDetailPanel.this));
			}
			
		} catch (Exception e) {
			log.error("ConsumerTransactionDetailPanel:handleSearchTransactionData() ==> Error handleSearchTransactionData ==> " ,e);
			this.basePage.error(getLocalizer().getString("error.exception", this.basePage));
		}
		container.setVisible(true);
		target.addComponent(container);
		target.addComponent(feedbackPanel);
		target.addComponent(approvalHeader);
		target.addComponent(navigator);
	}
	
	public List<ConsumerTransactionBean> convertToConsumerTrxHistoryBeanList(
			final List<TransactionHistoryFindView> responseList) {
		
		final List<ConsumerTransactionBean> beanList = new ArrayList<ConsumerTransactionBean>();

		for (TransactionHistoryFindView response : responseList) {
			
			final ConsumerTransactionBean bean = new ConsumerTransactionBean();
			
			log.info("### (ConverterUtils::convertToConsumerTransactionHistoryBeanList) DATE TIME ###" +response.getTransactionDateTime());
			bean.setDate(PortalUtils.getSaveDate(response.getTransactionDateTime()));
			log.info("### (ConverterUtils::convertToConsumerTransactionHistoryBeanList) ID ###" +response.getId());
			bean.setTxnId(String.valueOf(response.getId()));
			log.info("### (ConverterUtils::convertToConsumerTransactionHistoryBeanList) USE CASE ID ###" +response.getUseCaseId());
			bean.setUseCaseId(response.getUseCaseId());
			bean.setType(MobiliserUtils.getValue(BtpnConstants.RESOURCE_BUNDLE_USE_CASES,	
					Integer.toString(response.getUseCaseId()), basePage.getLookupMapUtility(), basePage));
			
			if (response.isDebitFlags()){
				bean.setDebitDesc("Debit");
			}else{
				bean.setDebitDesc("Kredit");
			}
			
			log.info("### (ConverterUtils::convertToConsumerTransactionHistoryBeanList) RESERVED 1 ###" +response.getReserved1());
			bean.setReserved1(response.getReserved1());
			log.info("### (ConverterUtils::convertToConsumerTransactionHistoryBeanList) AMOUNT ###" +response.getAmount());
			bean.setAmount(response.getAmount());
			log.info("### (ConverterUtils::convertToConsumerTransactionHistoryBeanList) FEE ###" +response.getFee());
			bean.setFee(response.getFee());
			log.info("### (ConverterUtils::convertToConsumerTransactionHistoryBeanList) BALANCE ###" +response.getBalance());
			bean.setBalance(response.getBalance());
			log.info("### (ConverterUtils::convertToConsumerTransactionHistoryBeanList) DESC ###" +response.getDescription());
			bean.setDetails(response.getDescription());
			bean.setStatus(getLocalizer().getString("history.status", this));
			beanList.add(bean);
		}
		
		return beanList;
	}
	
	

	/**
	 * This method is used to download the CSV File.
	 * 
	 * @param target AjaxRequestTarget
	 */
	private StringBuilder handleDownloadToCSVFile() {
		final StringBuilder header = new StringBuilder();
		header.append(getLocalizer().getString("label.date", this) + ",");
		header.append(getLocalizer().getString("label.txnId", this) + ",");
		header.append(getLocalizer().getString("label.type", this) + ",");
		header.append(getLocalizer().getString("label.details", this) + ",");
		header.append(getLocalizer().getString("label.fee", this) + ",");
		header.append(getLocalizer().getString("label.debitDesc", this) + ",");
//		header.append(getLocalizer().getString("label.name", this) + ",");
		header.append(getLocalizer().getString("label.amount", this) + ",");
		header.append(getLocalizer().getString("label.status", this) + ",");
		return BtpnUtils.generateCsvFile(header, searchData.getTxnDataList());
	}

	/**
	 * Download to CSV File.
	 * 
	 * @author Vikram Gunda
	 */
	private class TransactionsDownload extends AJAXDownload {

		private static final long serialVersionUID = 1L;

		@Override
		protected String getFileName() {
			return BtpnConstants.TRANSACTION_FILE_NAME;
		}

		@Override
		protected IResourceStream getResourceStream() {
			return new ByteArrayResource(BtpnConstants.CSV_CONTENT_TYPE, handleDownloadToCSVFile().toString()
				.getBytes()).getResourceStream();
		}
	}

	private void renderDateJavascipt(AjaxRequestTarget target) {
		final String chooseDtTxt = this.getLocalizer().getString("datepicker.chooseDate", basePage);
		target.getHeaderResponse().renderOnLoadJavascript(
			"\n" + "jQuery(document).ready(function($) { \n" + "  $('#fromDate').datepicker( { \n"
					+ "	'buttonText' : '" + chooseDtTxt + "', \n" + "	'changeMonth' : true, \n"
					+ "	'changeYear' : true, \n" + "     'yearRange' : '-100:+100', \n" + "	'showOn': 'both', \n"
					+ "	'dateFormat' : '" + BtpnConstants.DATE_FORMAT_PATTERN_PICKER + "', \n"
					+ "	'buttonImage': '../images/calendar.gif', \n" + "	'buttonOnlyImage': true} ); \n" + "});\n");

		target.getHeaderResponse().renderOnLoadJavascript(
			"\n" + "jQuery(document).ready(function($) { \n" + "  $('#toDate').datepicker( { \n" + "	'buttonText' : '"
					+ chooseDtTxt + "', \n" + "	'changeMonth' : true, \n" + "	'changeYear' : true, \n"
					+ "     'yearRange' : '-100:+100', \n" + "	'showOn': 'both', \n" + "	'dateFormat' : '"
					+ BtpnConstants.DATE_FORMAT_PATTERN_PICKER + "', \n"
					+ "	'buttonImage': '../images/calendar.gif', \n" + "	'buttonOnlyImage': true} ); \n" + "});\n");
	}
}
