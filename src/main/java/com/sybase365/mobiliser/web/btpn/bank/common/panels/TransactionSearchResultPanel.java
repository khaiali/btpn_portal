package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.resource.ByteArrayResource;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.io.ByteArrayOutputStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.core.account.facade.api.AccountFacade;
import com.btpnwow.core.account.facade.contract.AccountIdentificationType;
import com.btpnwow.core.account.facade.contract.FindTransactionHistoryRequest;
import com.btpnwow.core.account.facade.contract.FindTransactionHistoryResponse;
import com.btpnwow.core.account.facade.contract.TransactionHistoryFindView;
import com.btpnwow.portal.common.util.MobiliserUtils;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.util.tools.wicketutils.resourceloader.ILookupMapUtility;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.bank.beans.CustomerRegistrationBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.SearchTxnDataBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.TxnDataBean;
import com.sybase365.mobiliser.web.btpn.bank.common.dataproviders.TxnDataProvider;
import com.sybase365.mobiliser.web.btpn.common.behaviours.AJAXDownload;
import com.sybase365.mobiliser.web.btpn.common.behaviours.DateHeaderContributor;
import com.sybase365.mobiliser.web.btpn.common.components.AccountDropDownChoice;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;
import com.sybase365.mobiliser.web.btpn.common.components.BtpnCustomPagingNavigator;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnLocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.btpn.util.BtpnOrderByOrder;
import com.sybase365.mobiliser.web.btpn.util.FromToDateValidator;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the TransactionSearchResultPanel page for bank portal transaction details
 * 
 * @author Narasa Reddy
 */
public class TransactionSearchResultPanel extends Panel {

	private static final Logger LOG = LoggerFactory.getLogger(TransactionSearchResultPanel.class);

	private static final long serialVersionUID = 1L;

	private static final String WICKET_ID_PAGEABLE = "pageable";

	private static final String WICKET_ID_approvalNavigator = "approvalNavigator";

	private static final String WICKET_ID_approvalTotalItems = "approvalHeader";

	@SpringBean(name = "accountClient")
	private AccountFacade accountFacade;
	
	@SpringBean(name = "btpnLookupMapUtilitiesImpl")
	public ILookupMapUtility lookupMapUtility;

	protected BtpnMobiliserBasePage basePage;

	private SearchTxnDataBean searchData;

	private CustomerRegistrationBean searchCustomerBean;

	private String approvalTotalItemString;

	private int approvalStartIndex = 0;

	private int approvalEndIndex = 0;

	private Label approvalHeader;

	private BtpnCustomPagingNavigator navigator;

	private FeedbackPanel feedBack;

	private SimpleDateFormat txnDateFormatter = new SimpleDateFormat("MM/dd/yyyy");

	private TransactionsDownload download;

	private boolean isAmountMask;

	public TransactionSearchResultPanel(String id) {
		super(id);
		
		isAmountMask = true;
	}

	public TransactionSearchResultPanel(String id, BtpnMobiliserBasePage basePage, CustomerRegistrationBean registrationBean) {
		super(id);
		
		this.basePage = basePage;
		
		this.searchCustomerBean = registrationBean;
		this.searchData = new SearchTxnDataBean();
		
		isAmountMask = isAmountMask(searchCustomerBean.getProductCategory());
		
		constructPanel();
		
		addDateHeaderContributor();
	}

	protected void addDateHeaderContributor() {
		final String chooseDtTxt = this.getLocalizer().getString("datepicker.chooseDate", basePage);
		final String locale = this.getLocale().getLanguage();
		add(new HeaderContributor(new DateHeaderContributor(locale, BtpnConstants.DATE_FORMAT_PATTERN_PICKER,
			chooseDtTxt)));
	}

	protected void constructPanel() {
		final Form<TransactionSearchResultPanel> form = new Form<TransactionSearchResultPanel>(
				"txnSearchForm", new CompoundPropertyModel<TransactionSearchResultPanel>(this));

		// Add feedback panel for Error Messages
		feedBack = new FeedbackPanel("errorMessages");
		feedBack.setOutputMarkupId(true);
		feedBack.setOutputMarkupPlaceholderTag(true);
		form.add(feedBack);

		final DateTextField fromDate = (DateTextField) DateTextField.forDatePattern(
				"searchData.fromDate", BtpnConstants.ID_EXPIRY_DATE_PATTERN)
				.setRequired(true)
				.add(new ErrorIndicator());
		
		final DateTextField toDate = (DateTextField) DateTextField.forDatePattern(
				"searchData.toDate", BtpnConstants.ID_EXPIRY_DATE_PATTERN)
				.setRequired(true)
				.add(new ErrorIndicator());

		form.add(fromDate);
		
		form.add(toDate);
		
		form.add(new FromToDateValidator(fromDate, toDate));

		form.add(new AccountDropDownChoice(
				"searchData.paymentInstrumentId", false, true,
				searchCustomerBean.getMobileNumber(),
				0,
				null,
				Collections.singletonList(Integer.valueOf(0)))
				.setNullValid(false)
				.setRequired(true)
				.add(new ErrorIndicator()));
		
		final IChoiceRenderer<CodeValue> codeValueChoiceRender = new ChoiceRenderer<CodeValue>(
			BtpnConstants.DISPLAY_EXPRESSION, BtpnConstants.ID_EXPRESSION);

		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>(
				"searchData.txnType",
				CodeValue.class,
				BtpnConstants.RESOURCE_BUNDLE_TXN_TYPE,
				this)
				.setChoiceRenderer(codeValueChoiceRender)
				.add(new ErrorIndicator()));

		final WebMarkupContainer txnDataContainer = new WebMarkupContainer("txnDataContainer");
		showSearchTxnDataView(txnDataContainer);
		txnDataContainer.setOutputMarkupId(true);
		txnDataContainer.setOutputMarkupPlaceholderTag(true);
		txnDataContainer.setVisible(false);
		form.add(txnDataContainer);

//		searchData.setSvaBalance(basePage.getSvaBalance(searchCustomerBean.getMobileNumber()));
//		form.add(isAmountMask ? new Label("searchData.svaBalance", "IDR xxx") : new AmountLabel("searchData.svaBalance"));

		form.add(new AjaxButton("searchButton") {
			private static final long serialVersionUID = 1L;

			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				handleSearchTransactionData(target, txnDataContainer);
				
				target.addComponent(feedBack);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.addComponent(feedBack);
			}
		});

		// Export Container
		final WebMarkupContainer exportContainer = new WebMarkupContainer("exportContainer") {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return searchData != null && searchData.getTxnDataList().size() != 0;
			}
		};
		
		exportContainer.add(addPDFAjaxLink());
		
		txnDataContainer.add(exportContainer);

		form.add(download = new TransactionsDownload());

		add(form);
	}

	/**
	 * Add the PDF Ajax Link.
	 * 
	 * @return AjaxLink AjaxLink
	 */
	protected AjaxLink<Void> addPDFAjaxLink() {
		return new AjaxLink<Void>("pdfLink") {
			private static final long serialVersionUID = 1L;

			public void onClick(AjaxRequestTarget target) {
				download.initiate(target);
			}
		};
	}

	protected void showSearchTxnDataView(final WebMarkupContainer dataViewContainer) {
		// Create the Salary Data provider
		final TxnDataProvider txnDataProvider = new TxnDataProvider("date", searchData.getTxnDataList());

		final DataView<TxnDataBean> dataView = new TxnDataView(WICKET_ID_PAGEABLE, txnDataProvider);
		dataView.setItemsPerPage(20);

		// Add the navigation
		navigator = new BtpnCustomPagingNavigator(WICKET_ID_approvalNavigator, dataView) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return txnDataProvider.size() != 0;
			}
		};
		navigator.setOutputMarkupId(true);
		navigator.setOutputMarkupPlaceholderTag(true);
		dataViewContainer.add(navigator);

		dataViewContainer.add(new Label("no.items", getLocalizer().getString("no.items", this)) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return txnDataProvider.size() == 0;
			}
		}.setRenderBodyOnly(true));

		// Add the header
		IModel<String> headerDisplayModel = new AbstractReadOnlyModel<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getObject() {
				final String displayTotalItemsText = TransactionSearchResultPanel.this.getLocalizer().getString("approval.totalitems.header", TransactionSearchResultPanel.this);
				
				return String.format(displayTotalItemsText, approvalTotalItemString, Integer.toString(approvalStartIndex), Integer.toString(approvalEndIndex));
			}
		};

		// Add the approval header
		approvalHeader = new Label(WICKET_ID_approvalTotalItems, headerDisplayModel) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return txnDataProvider.size() != 0;
			}
		};
		dataViewContainer.add(approvalHeader);
		approvalHeader.setOutputMarkupId(true);
		approvalHeader.setOutputMarkupPlaceholderTag(true);

		// Add the sort providers
		dataViewContainer.add(new BtpnOrderByOrder("orderByDate", "date", txnDataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderById", "id", txnDataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByType", "type", txnDataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByAmount", "amount", txnDataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByDescription", "description", txnDataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByDebitAmount", "debitAmount", txnDataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByCreditAmount", "creditAmount", txnDataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByRunnigBalance", "runningBalance", txnDataProvider, dataView));

		// Add the Data View
		dataViewContainer.addOrReplace(dataView);
	}

	private class TxnDataView extends DataView<TxnDataBean> {
		private static final long serialVersionUID = 1L;

		protected TxnDataView(String id, IDataProvider<TxnDataBean> dataProvider) {
			super(id, dataProvider);
			setOutputMarkupId(true);
			setOutputMarkupPlaceholderTag(true);
		}

		@Override
		protected void onBeforeRender() {
			final TxnDataProvider dataProvider = (TxnDataProvider) internalGetDataProvider();
			dataProvider.setTxnDataList(searchData.getTxnDataList());
			refreshTotalItemCount();
			super.onBeforeRender();
		}

		@Override
		protected void populateItem(final Item<TxnDataBean> item) {
			TxnDataBean dataBean = item.getModelObject();
			item.setModel(new CompoundPropertyModel<TxnDataBean>(dataBean));
			item.add(new Label("id"));
			item.add(new Label("date", txnDateFormatter.format(dataBean.getDate())));
			item.add(new Label("type"));
			item.add(new Label("description"));
			item.add(isAmountMask ? new Label("amount", "xxx") : new AmountLabel("amount"));
			item.add(isAmountMask ? new Label("debitAmount", "xxx") : new AmountLabel("debitAmount"));
			item.add(isAmountMask ? new Label("creditAmount", "xxx") : new AmountLabel("creditAmount"));
			item.add(isAmountMask ? new Label("runningBalance", "xxx") : new AmountLabel("runningBalance"));
			// Add the UseCase
			final String cssStyle = item.getIndex() % 2 == 0 ? BtpnConstants.DATA_VIEW_EVEN_ROW_CSS : BtpnConstants.DATA_VIEW_ODD_ROW_CSS;
			item.add(new SimpleAttributeModifier("class", cssStyle));
		}

		@Override
		public boolean isVisible() {
			return ((TxnDataProvider) internalGetDataProvider()).size() != 0;

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
	 * This method will return available transaction details
	 */
	protected void handleSearchTransactionData(AjaxRequestTarget target, final WebMarkupContainer container) {
		container.setVisible(true);
		
		target.addComponent(container);
		
		try {
			AccountIdentificationType aid = new AccountIdentificationType();
			aid.setFlags(0);
			aid.setType("WALLET");
			aid.setValue(searchData.getPaymentInstrumentId().getId());
			
			FindTransactionHistoryRequest request = MobiliserUtils.fill(new FindTransactionHistoryRequest(), basePage);
			request.setDateBegin(PortalUtils.getSaveXMLGregorianCalendarFromDate(searchData.getFromDate(), null));
			request.setDateEnd(PortalUtils.getSaveXMLGregorianCalendarToDate(searchData.getToDate(), null));
			request.getAccount().add(aid);
			request.setOldestToNewest(false);
			
			if (searchData.getTxnType() != null) {
				request.getUseCaseId().add(Integer.valueOf(searchData.getTxnType().getId()));
			}
			
			request.setStart(0);
			request.setLength(Integer.MAX_VALUE);
			
			FindTransactionHistoryResponse response = accountFacade.findTransactionHistory(request);
			
			if (MobiliserUtils.success(response)) {
				searchData.getTxnDataList().clear();
				
				List<TransactionHistoryFindView> transactions = response.getTransaction();
				
				if ((transactions != null) && !transactions.isEmpty()) {
					for (TransactionHistoryFindView e : transactions) {
						TxnDataBean ebean = new TxnDataBean();
						
						if (e.isDebitFlags()) {
							ebean.setAmount(-e.getAmount());
							ebean.setDebitAmount(Long.valueOf(e.getAmount()));
							ebean.setCreditAmount(null);
						} else {
							ebean.setAmount(e.getAmount());
							ebean.setDebitAmount(null);
							ebean.setCreditAmount(Long.valueOf(e.getAmount()));
						}
						
						ebean.setFee(e.getAmount());
						
						ebean.setId(e.getId());
						ebean.setDate(PortalUtils.getSaveDate(e.getTransactionDateTime()));
						ebean.setUseCase(MobiliserUtils.getCodeValue("allUseCases", Integer.toString(e.getUseCaseId()), lookupMapUtility, this));
						ebean.setDescription(e.getDescription());
						
						if (ebean.getUseCase().getValue() == null) {
							ebean.setType(Integer.toString(e.getUseCaseId()));
						} else {
							ebean.setType(ebean.getUseCase().getValue());
						}
						
						ebean.setRunningBalance(e.getBalance());
						
						searchData.getTxnDataList().add(ebean);
					}
				}
			} else {
				error(MobiliserUtils.errorMessage(response, this));
			}
		} catch (Throwable ex) {
			LOG.error("An exception was thrown.", ex);
			
			error(getLocalizer().getString("error.exception", this));
		}
	}

	private byte[] createTxnHistoryPDF() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Document document = new Document(PageSize.A4, 50.0F, 50.0F, 50.0F, 50.0F);
		try {
			try {
				PdfWriter.getInstance(document, baos);
			} catch (DocumentException ex) {
				LOG.error("Error while creating PDF");
			}
			document.open();
			Font font = new Font(FontFamily.TIMES_ROMAN, 5, Font.NORMAL, new BaseColor(0, 0, 0));

			PdfPTable txnHistoryTable = new PdfPTable(8);
			txnHistoryTable.setWidthPercentage(110f);
			txnHistoryTable.setSpacingBefore(30.0F);
			txnHistoryTable.setSpacingAfter(30.0F);

			// create Header
			createPdfHeader(font, txnHistoryTable);

			// populate table
			populatePdfTabel(txnHistoryTable, font);

			document.add(txnHistoryTable);

		} catch (DocumentException ex) {
			LOG.error("Error while creating PDF");
		}
		document.close();
		return baos.toByteArray();

	}

	private void createPdfHeader(Font font, PdfPTable txnHistoryTable) {

		PdfPCell cell1 = new PdfPCell(new Phrase("ID", font));
		cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
		cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
		txnHistoryTable.addCell(cell1);

		PdfPCell cell2 = new PdfPCell(new Phrase("Date", font));
		cell2.setBackgroundColor(BaseColor.LIGHT_GRAY);
		txnHistoryTable.addCell(cell2);

		PdfPCell cell3 = new PdfPCell(new Phrase("Type", font));
		cell3.setBackgroundColor(BaseColor.LIGHT_GRAY);
		txnHistoryTable.addCell(cell3);

		PdfPCell cell5 = new PdfPCell(new Phrase("Description", font));
		cell5.setBackgroundColor(BaseColor.LIGHT_GRAY);
		txnHistoryTable.addCell(cell5);

		PdfPCell cell4 = new PdfPCell(new Phrase("Amount", font));
		cell4.setBackgroundColor(BaseColor.LIGHT_GRAY);
		txnHistoryTable.addCell(cell4);

		PdfPCell cell6 = new PdfPCell(new Phrase("Debit Amount", font));
		cell6.setBackgroundColor(BaseColor.LIGHT_GRAY);
		txnHistoryTable.addCell(cell6);

		PdfPCell cell7 = new PdfPCell(new Phrase("Credit Amount", font));
		cell7.setBackgroundColor(BaseColor.LIGHT_GRAY);
		txnHistoryTable.addCell(cell7);

		PdfPCell cell8 = new PdfPCell(new Phrase("Running Balance", font));
		cell8.setBackgroundColor(BaseColor.LIGHT_GRAY);
		txnHistoryTable.addCell(cell8);
	}

	private void populatePdfTabel(PdfPTable txnTable, Font font) {
		List<TxnDataBean> txnHistoryList = searchData.getTxnDataList();
		
		for (TxnDataBean th : txnHistoryList) {
			txnTable.addCell(new Phrase(String.valueOf(th.getId()), font));
			txnTable.addCell(new Phrase(txnDateFormatter.format(th.getDate()), font));
			txnTable.addCell(new Phrase(th.getType(), font));
			txnTable.addCell(new Phrase(th.getDescription(), font));
			txnTable.addCell(new Phrase(isAmountMask ? "xxx" : basePage.displayAmount(Long.valueOf(th.getAmount())), font));
			txnTable.addCell(new Phrase(isAmountMask ? "xxx" : basePage.displayAmount(th.getDebitAmount()), font));
			txnTable.addCell(new Phrase(isAmountMask ? "xxx" : basePage.displayAmount(th.getCreditAmount()), font));
			txnTable.addCell(new Phrase(isAmountMask ? "xxx" : basePage.displayAmount(th.getRunningBalance()), font));
		}
	}

	private class TransactionsDownload extends AJAXDownload {
		private static final long serialVersionUID = 1L;

		@Override
		protected String getFileName() {
			return BtpnConstants.TRANSACTION_PDF_FILE_NAME;
		}

		@Override
		protected IResourceStream getResourceStream() {
			return new ByteArrayResource(BtpnConstants.PDF_CONTENT_TYPE, createTxnHistoryPDF()).getResourceStream();
		}
	}

	private boolean isAmountMask(int productCategory) {
		final Roles roles = basePage.getMobiliserWebSession().getBtpnRoles();
		boolean hasPrivilege = roles.hasRole(BtpnConstants.PRIV_EMPLOYEE_TXN_HISTORY_VIEW);
		if (!hasPrivilege && productCategory == BtpnConstants.PRODUCT_CATEGORY_ZERO) {
			isAmountMask = true;
		}
		return isAmountMask;
	}
}
