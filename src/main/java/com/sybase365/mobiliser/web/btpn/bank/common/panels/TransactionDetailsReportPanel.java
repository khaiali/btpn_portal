package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

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
import org.apache.wicket.validation.validator.PatternValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.support.BankTransactionReportRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.support.BankTransactionReportResponse;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.BankTransactionDetailsReportRequestBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.bank.beans.TransactionDetailsReportAgentBean;
import com.sybase365.mobiliser.web.btpn.bank.common.dataproviders.TransactionDetailsReportDataProvider;
import com.sybase365.mobiliser.web.btpn.common.behaviours.DateHeaderContributor;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;
import com.sybase365.mobiliser.web.btpn.common.components.BtpnCustomPagingNavigator;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnLocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.btpn.util.BtpnOrderByOrder;
import com.sybase365.mobiliser.web.btpn.util.ConverterUtils;
import com.sybase365.mobiliser.web.btpn.util.FromToDateValidator;
import com.sybase365.mobiliser.web.util.PhoneNumber;

/**
 * This class is Transaction Details Report Panel for bank Portal for Agents. The transactions page displays the
 * transaction details.
 * 
 * @author Vikram Gunda
 */
public class TransactionDetailsReportPanel extends Panel {

	private static final Logger LOG = LoggerFactory.getLogger(TransactionDetailsReportPanel.class);

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

	private BankTransactionDetailsReportRequestBean searchData;

	private FeedbackPanel feedbackPanel;

	private Form<TransactionDetailsReportPanel> form;

	private String previousMsisdn;

	private BtpnLocalizableLookupDropDownChoice<CodeValue> reportScope;

	/**
	 * Constructor for Transaction details
	 * 
	 * @param id
	 */
	public TransactionDetailsReportPanel(String id, BtpnMobiliserBasePage basePage) {
		super(id);
		this.basePage = basePage;
		this.searchData = new BankTransactionDetailsReportRequestBean();
		addDateHeaderContributor();
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
	@SuppressWarnings("unchecked")
	protected void constructPanel() {

		form = new Form<TransactionDetailsReportPanel>("consumerTxnPanel",
			new CompoundPropertyModel<TransactionDetailsReportPanel>(this));
		form.setOutputMarkupId(true);
		// Error Messages
		form.add(feedbackPanel = new FeedbackPanel("errorMessages"));
		feedbackPanel.setOutputMarkupId(true);

		// Add the Choice Renderer
		final IChoiceRenderer<CodeValue> codeValueChoiceRender = new ChoiceRenderer<CodeValue>(
			BtpnConstants.DISPLAY_EXPRESSION, BtpnConstants.ID_EXPRESSION);
		// From date and To Date
		final DateTextField fromDate;
		final DateTextField toDate;
		form.add(fromDate = (DateTextField) DateTextField
			.forDatePattern("searchData.fromDate", BtpnConstants.ID_EXPIRY_DATE_PATTERN).setRequired(true)
			.setMarkupId("fromDate").add(new ErrorIndicator()));
		form.add(toDate = (DateTextField) DateTextField
			.forDatePattern("searchData.toDate", BtpnConstants.ID_EXPIRY_DATE_PATTERN).setRequired(true)
			.setMarkupId("toDate").add(new ErrorIndicator()));
		form.add(new TextField<String>("searchData.agentMsisdn").setRequired(true)
			.add(new PatternValidator(this.basePage.getBankPortalPrefsConfig().getMobileRegex()))
			.add(new ErrorIndicator()));
		form.add(new TextField<String>("searchData.agentType").add(new ErrorIndicator()).setEnabled(false));
		// Report Scope
		form.add(reportScope = (BtpnLocalizableLookupDropDownChoice<CodeValue>) new BtpnLocalizableLookupDropDownChoice<CodeValue>(
			"searchData.reportScope", CodeValue.class, BtpnConstants.RESOURCE_BUNDLE_TRANSACTION_DETAILS_REPORT_TYPE,
			this, true, false).setChoiceRenderer(codeValueChoiceRender).add(new ErrorIndicator()).setEnabled(false));
		// Transaction Type
		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("searchData.txnType", CodeValue.class,
			BtpnConstants.RESOURCE_BUNDLE_TRANSACTION_DETAILS_TRANSACTION_TYPE, this, true, false).setNullValid(true)
			.setChoiceRenderer(codeValueChoiceRender).add(new ErrorIndicator()));
		// Transaction Search Container
		final WebMarkupContainer transactionDataContainer = new WebMarkupContainer("txnDataContainer");
		showSearchTxnDataView(transactionDataContainer);
		transactionDataContainer.setOutputMarkupId(true);
		transactionDataContainer.setOutputMarkupPlaceholderTag(true);
		transactionDataContainer.setVisible(false);

		form.add(transactionDataContainer);
		form.add(new FromToDateValidator(fromDate, toDate));

		// Add add Button
		form.add(new AjaxButton("searchButton") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				handleChangeAgentMsisdn();
				handleSearchTransactionData(target, transactionDataContainer);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				renderDateJavascipt(target);
				target.addComponent(feedbackPanel);
				target.addComponent(TransactionDetailsReportPanel.this.form);
			}
		});
		add(form);
	}

	/**
	 * Add the dataViewContainer for Transaction details report.
	 * 
	 * @param dataViewContainer dataViewContainer
	 */
	protected void showSearchTxnDataView(final WebMarkupContainer dataViewContainer) {
		// Create the Salary Data provider
		final TransactionDetailsReportDataProvider consumerTxnDataProvider = new TransactionDetailsReportDataProvider(
			"date", searchData.getTxnDataList());

		final DataView<TransactionDetailsReportAgentBean> dataView = new TxnDataView(WICKET_ID_PAGEABLE,
			consumerTxnDataProvider);
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
				final String displayTotalItemsText = TransactionDetailsReportPanel.this.getLocalizer().getString(
					"approval.totalitems.header", TransactionDetailsReportPanel.this);
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
		dataViewContainer.add(new BtpnOrderByOrder("orderByCustomerAccount", "customerAccount",
			consumerTxnDataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByAgentType", "agentType", consumerTxnDataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByAgentId", "agentId", consumerTxnDataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByTransactionType", "transactionType",
			consumerTxnDataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByAmount", "amount", consumerTxnDataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByBiller", "biller", consumerTxnDataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByBeneificary", "beneificary", consumerTxnDataProvider,
			dataView));
		// Add the Data View
		dataViewContainer.addOrReplace(dataView);
	}

	/**
	 * This is the View For Transaction Search Data results
	 * 
	 * @author Vikram Gunda
	 */
	private class TxnDataView extends DataView<TransactionDetailsReportAgentBean> {

		private static final long serialVersionUID = 1L;

		protected TxnDataView(String id, IDataProvider<TransactionDetailsReportAgentBean> dataProvider) {
			super(id, dataProvider);
			setOutputMarkupId(true);
			setOutputMarkupPlaceholderTag(true);

		}

		@Override
		protected void onBeforeRender() {
			final TransactionDetailsReportDataProvider dataProvider = (TransactionDetailsReportDataProvider) internalGetDataProvider();
			dataProvider.setTxnDataList(searchData.getTxnDataList());
			refreshTotalItemCount();
			super.onBeforeRender();
		}

		@Override
		protected void populateItem(final Item<TransactionDetailsReportAgentBean> item) {
			TransactionDetailsReportAgentBean dataBean = item.getModelObject();
			item.setModel(new CompoundPropertyModel<TransactionDetailsReportAgentBean>(dataBean));
			DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			item.add(new Label("date", df.format(dataBean.getDate())));
			item.add(new Label("customerAccount"));
			item.add(new Label("agentType"));
			item.add(new Label("agentId"));
			item.add(new Label("transactionType"));
			item.add(new AmountLabel("amount"));
			item.add(new Label("biller"));
			item.add(new Label("beneificary"));
			// Add the UseCase
			final String cssStyle = item.getIndex() % 2 == 0 ? BtpnConstants.DATA_VIEW_EVEN_ROW_CSS : BtpnConstants.DATA_VIEW_ODD_ROW_CSS;
			item.add(new SimpleAttributeModifier("class", cssStyle));
		}

		@Override
		public boolean isVisible() {
			return searchData != null && searchData.getTxnDataList().size() != 0;
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

	/**
	 * This handles when agent msisdn is changed from old value and new transaction Report is fetched.
	 */
	private void handleChangeAgentMsisdn() {
		final String agentMsisdn = searchData.getAgentMsisdn();
		searchData.setAgentMsisdn(new PhoneNumber(agentMsisdn, this.basePage.getBankPortalPrefsConfig()
			.getDefaultCountryCode()).getInternationalFormat());
		if (previousMsisdn == null || !previousMsisdn.equals(searchData.getAgentMsisdn())) {
			previousMsisdn = searchData.getAgentMsisdn();
			searchData.setReportScope(new CodeValue("0", "Transactions for this Agent only"));
		}
	}

	/**
	 * This method handles the bank transaction data search report.
	 * 
	 * @param target
	 * @param container
	 */
	private void handleSearchTransactionData(final AjaxRequestTarget target, final WebMarkupContainer container) {
		searchData.setTxnDataList(new ArrayList<TransactionDetailsReportAgentBean>());
		try {
			final BankTransactionReportRequest request = this.basePage
				.getNewMobiliserRequest(BankTransactionReportRequest.class);
			final BankTransactionReportResponse response = this.basePage.getSupportClient().bankTransactionReport(
				ConverterUtils.convertToBankTransactionReportRequest(searchData, request));
			if (basePage.evaluateConsumerPortalMobiliserResponse(response)) {
				searchData.setAgentType(BtpnConstants.AGENT_TYPES.get(response.getAgentType()));
				reportScope.setLookupName("report.scope." + searchData.getAgentType());
				reportScope.setEnabled(true);
				searchData.setTxnDataList(ConverterUtils.convertToTransactionDetailsReportAgentBeanList(response
					.getReportResponse(), searchData.getAgentType() + response.getCustomerId()));
			} else {
				handleSpecificErrorMessage(response.getStatus().getCode());
			}
		} catch (Exception e) {
			LOG.error(
				"ConsumerTransactionDetailPanel:handleSearchTransactionData() ==> Error handleSearchTransactionData ==> ",
				e);
			this.basePage.error(getLocalizer().getString("error.exception", this.basePage));
		}
		container.setVisible(true);
		target.addComponent(container);
		renderDateJavascipt(target);
		target.addComponent(form);
		target.addComponent(feedbackPanel);
		target.addComponent(approvalHeader);
		target.addComponent(navigator);
	}

	/**
	 * This method handles the rendering of date java script.
	 * 
	 * @param target
	 */
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

	/**
	 * This method handles the specific error message.
	 */
	private void handleSpecificErrorMessage(final int errorCode) {
		// Specific error message handling
		final String messageKey = "error." + errorCode;
		String message = getLocalizer().getString(messageKey, this);
		// Generice error messages
		if (messageKey.equals(message)) {
			message = getLocalizer().getString("error.transactions", this);
		}
		error(message);
	}
}
