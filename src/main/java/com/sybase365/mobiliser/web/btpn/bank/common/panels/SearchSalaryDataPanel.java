package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
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
import org.apache.wicket.markup.html.form.TextField;
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
import org.apache.wicket.util.resource.IResourceStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.core.bulk.facade.contract.ApproveBulkFileWrkRequest;
import com.btpnwow.core.bulk.facade.contract.ApproveBulkFileWrkResponse;
import com.btpnwow.core.bulk.facade.contract.BulkFileType;
import com.btpnwow.core.bulk.facade.contract.BulkFileWrkType;
import com.btpnwow.core.bulk.facade.contract.FindBulkFileRequest;
import com.btpnwow.core.bulk.facade.contract.FindBulkFileResponse;
import com.btpnwow.core.bulk.facade.contract.FindBulkFileWrkRequest;
import com.btpnwow.core.bulk.facade.contract.FindBulkFileWrkResponse;
import com.btpnwow.core.bulk.facade.contract.GetBulkFileRequest;
import com.btpnwow.core.bulk.facade.contract.GetBulkFileResponse;
import com.btpnwow.core.bulk.facade.contract.GetBulkFileWrkRequest;
import com.btpnwow.core.bulk.facade.contract.GetBulkFileWrkResponse;
import com.btpnwow.core.bulk.facade.contract.RejectBulkFileWrkRequest;
import com.btpnwow.core.bulk.facade.contract.RejectBulkFileWrkResponse;
import com.btpnwow.portal.common.util.MobiliserUtils;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.util.tools.wicketutils.resourceloader.ILookupMapUtility;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.bank.beans.SalaryDataBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.SearchSalaryDataBean;
import com.sybase365.mobiliser.web.btpn.bank.common.dataproviders.SalaryDataProvider;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.salaryupload.SearchSalaryDataPage;
import com.sybase365.mobiliser.web.btpn.common.behaviours.AJAXDownload;
import com.sybase365.mobiliser.web.btpn.common.behaviours.DateHeaderContributor;
import com.sybase365.mobiliser.web.btpn.common.components.BtpnCustomPagingNavigator;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnLocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.btpn.util.BtpnOrderByOrder;
import com.sybase365.mobiliser.web.btpn.util.ConverterUtils;
import com.sybase365.mobiliser.web.btpn.util.FromToDateValidator;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the Salary upload panel.
 * 
 * @author Vikram Gunda
 */
public class SearchSalaryDataPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(SearchSalaryDataPanel.class);

	protected BtpnMobiliserBasePage basePage;

	private static final String WICKET_ID_PAGEABLE = "pageable";

	private static final String WICKET_ID_VIEW_LINK = "viewLink";

	private static final String WICKET_ID_APPROVE_LINK = "approveLink";

	private static final String WICKET_ID_REJECT_LINK = "rejectLink";

	private static final String WICKET_ID_ERROR_LINK = "errorLink";

	private static final String WICKET_ID_approvalNavigator = "approvalNavigator";

	private static final String WICKET_ID_approvalTotalItems = "approvalHeader";

	@SpringBean(name = "btpnLookupMapUtilitiesImpl")
	public ILookupMapUtility lookupMapUtility;

	private String approvalTotalItemString;

	private int approvalStartIndex = 0;

	private int approvalEndIndex = 0;

	private Label approvalHeader;

	private BtpnCustomPagingNavigator navigator;

	private SearchSalaryDataBean searchData;
	
	private byte[] fileContent;
	
	private String fileName;

	private Component dateFromComp;
	
	private Component dateToComp;

	private FeedbackPanel feedbackPanel;

	private SalaryDataDownload download;

	/**
	 * Constructor of this page.
	 * 
	 * @param id id of the panel
	 * @param basePage basePage of the mobiliser
	 */
	public SearchSalaryDataPanel(final String id, final BtpnMobiliserBasePage basePage, int fileTypeId) {
		super(id);
		
		this.basePage = basePage;
		
		addDateHeaderContributor();
		
		searchData = new SearchSalaryDataBean();
		
		constructPanel();
	}

	/**
	 * Adds the date header contributor
	 */
	protected void addDateHeaderContributor() {
		final String chooseDtTxt = basePage.getLocalizer().getString("datepicker.chooseDate", basePage);
		final String locale = basePage.getLocale().getLanguage();
		add(new HeaderContributor(new DateHeaderContributor(locale, BtpnConstants.DATE_FORMAT_PATTERN_PICKER,
			chooseDtTxt)));
	}

	/**
	 * Constructs the file
	 */
	protected void constructPanel() {
		final Form<SearchSalaryDataPanel> form = new Form<SearchSalaryDataPanel>("searchSalaryDataForm",
			new CompoundPropertyModel<SearchSalaryDataPanel>(this));
		form.setOutputMarkupId(true);
		// Error Messages
		form.add(feedbackPanel = new FeedbackPanel("errorMessages"));
		feedbackPanel.setOutputMarkupId(true);
		feedbackPanel.setOutputMarkupPlaceholderTag(true);
		form.add(feedbackPanel);
		
		final IChoiceRenderer<CodeValue> codeValueChoiceRender = new ChoiceRenderer<CodeValue>(
				BtpnConstants.DISPLAY_EXPRESSION_ID_VALUE, BtpnConstants.ID_EXPRESSION);
		
		// Status
		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("searchData.status", CodeValue.class,
			BtpnConstants.RESOURCE_BUNDLE_BULK_FILE_PROCESSING_STATUS, this, Boolean.FALSE, true).setNullValid(true)
			.setChoiceRenderer(codeValueChoiceRender).setOutputMarkupId(true).add(new ErrorIndicator()));
			
		// From date
		final DateTextField fromDate = (DateTextField) DateTextField
				.forDatePattern("searchData.dateRangeBegin",
						BtpnConstants.SALARY_SEARCH_FROM_DATE_PATTERN).setRequired(true)
				.add(new ErrorIndicator());
		dateFromComp = fromDate;
		form.add(dateFromComp);
		

		// To date
		final DateTextField toDate = (DateTextField) DateTextField
				.forDatePattern("searchData.dateRangeEnd",
						BtpnConstants.SALARY_SEARCH_TO_DATE_PATTERN).setRequired(true)
				.add(new ErrorIndicator());
		dateToComp = toDate;
		form.add(dateToComp);
		
		// File Name
		form.add(new TextField<String>("searchData.name").add(new ErrorIndicator()));
		
		// Type
		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("searchData.type", CodeValue.class,
			BtpnConstants.RESOURCE_BUNDLE_BULK_FILE_PROCESSING_TYPE, this, Boolean.TRUE, false)
			.setChoiceRenderer(codeValueChoiceRender).setRequired(true).add(new ErrorIndicator()));
		
		// Add the search container
		final WebMarkupContainer salaryDataContainer = new WebMarkupContainer("salaryDataContainer");
		showSearchSalaryDataView(salaryDataContainer);
		salaryDataContainer.setOutputMarkupId(true);
		salaryDataContainer.setOutputMarkupPlaceholderTag(true);
		salaryDataContainer.setVisible(false);
		form.add(salaryDataContainer);
		
		
		form.add(new AjaxButton("searchButton") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				searchData.setSalaryDataList(null);
				renderDateJavascipt(target);
				target.addComponent(form);
				handleSearchSalaryData(target, salaryDataContainer);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				renderDateJavascipt(target);
				target.addComponent(form);
				target.addComponent(feedbackPanel);
			}
		});
		download = new SalaryDataDownload();
		form.add(new FromToDateValidator(fromDate, toDate));
		form.add(download);
		add(form);
		add(form);
	}

	/**
	 * This method adds the approveTxnReversalDataView for the transaction reversal, and also adds the sorting logic for
	 * data view
	 */
	protected void showSearchSalaryDataView(final WebMarkupContainer dataViewContainer) {
		// Create the Salary Data provider
		final SalaryDataProvider salaryDataProvider = new SalaryDataProvider("fileName", searchData.getSalaryDataList());
		final DataView<SalaryDataBean> dataView = new SalaryDataView(WICKET_ID_PAGEABLE, salaryDataProvider);
		dataView.setItemsPerPage(20);
		
	
		// Add the navigation
		navigator = new BtpnCustomPagingNavigator(WICKET_ID_approvalNavigator, dataView) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return searchData != null && searchData.getSalaryDataList().size() != 0;
			}
		};
		navigator.setOutputMarkupId(true);
		navigator.setOutputMarkupPlaceholderTag(true);
		dataViewContainer.add(navigator);
		dataViewContainer.add(new Label("no.items", getLocalizer().getString("no.items", this)) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return searchData != null && searchData.getSalaryDataList().size() == 0;

			}
		}.setRenderBodyOnly(true));
		// Add the header
		IModel<String> headerDisplayModel = new AbstractReadOnlyModel<String>() {

			private static final long serialVersionUID = 1L;

			@Override
			public String getObject() {
				final String displayTotalItemsText = SearchSalaryDataPanel.this.getLocalizer().getString(
					"approval.totalitems.header", SearchSalaryDataPanel.this);
				return String.format(displayTotalItemsText, approvalTotalItemString, Integer.valueOf(approvalStartIndex),
					Integer.valueOf(approvalEndIndex));
			}
		};
		// Add the approval header
		approvalHeader = new Label(WICKET_ID_approvalTotalItems, headerDisplayModel) {

			private static final long serialVersionUID = 1L;

			
			@Override
			public boolean isVisible() {
				return searchData != null && searchData.getSalaryDataList().size() != 0;
			}
		};
		dataViewContainer.add(approvalHeader);
		approvalHeader.setOutputMarkupId(true);
		approvalHeader.setOutputMarkupPlaceholderTag(true);
		

		// Add the sort providers
		dataViewContainer.add(new BtpnOrderByOrder("orderByFileName", "fileName", salaryDataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByStatus", "status", salaryDataProvider, dataView));
		
		dataViewContainer.add(new Label("label.action", getLocalizer().getString("label.action", this))
			  .setVisible(true));
		// Add the Data View
		dataViewContainer.addOrReplace(dataView);
	}

	/**
	 * This is the Data View for Salary Data Bean.
	 * 
	 * @author Vikram Gunda
	 */
	private class SalaryDataView extends DataView<SalaryDataBean> {

		private static final long serialVersionUID = 1L;

		protected SalaryDataView(String id,  IDataProvider<SalaryDataBean> dataProvider) {
			super(id, dataProvider);
			setOutputMarkupId(true);
			setOutputMarkupPlaceholderTag(true);

		}

		@Override
		protected void onBeforeRender() {
			final SalaryDataProvider dataProvider = (SalaryDataProvider) internalGetDataProvider();
			dataProvider.setSalaryDataList(searchData.getSalaryDataList());
			refreshTotalItemCount();
			super.onBeforeRender();
		}

		@Override
		protected void populateItem(final Item<SalaryDataBean> item) {
			final SalaryDataBean searchBean = item.getModelObject();
			item.add(new Label("fileName", searchBean.getName()));
			item.add(new Label("status", searchBean.getStatus().getValue()));

			// Add the View Link
			final AjaxLink<SalaryDataBean> viewLink = new AjaxLink<SalaryDataBean>(WICKET_ID_VIEW_LINK, item.getModel()) {

				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					handleViewData(target, item.getModelObject(),false);
				}
			};
			
			// Add the Approve Link
			final AjaxLink<SalaryDataBean> approveLink = new AjaxLink<SalaryDataBean>(WICKET_ID_APPROVE_LINK,
				item.getModel()) {

				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					handleApprove(target,  item.getModelObject());
				}
			};
			// Add the Reject Link
			final AjaxLink<SalaryDataBean> rejectLink = new AjaxLink<SalaryDataBean>(WICKET_ID_REJECT_LINK,
				item.getModel()) {

				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					handleReject(target, item.getModelObject());
				}
			};
			
			
			// Add the Error Link
			final AjaxLink<SalaryDataBean> errorReportLink = new AjaxLink<SalaryDataBean>(WICKET_ID_ERROR_LINK,
				item.getModel()) {

				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					handleViewData(target, item.getModelObject(), true);
					
				}
			};
			
			
			item.add(viewLink);
			
			// Show Approve/Reject or Error Report links.
			final WebMarkupContainer approveRejectContainer = new WebMarkupContainer("approveRejectContainer");
			approveRejectContainer.setVisible(true);
			
			final boolean showApproveLink = Integer.parseInt(searchBean.getStatus().getId()) == 2 && PortalUtils.exists(searchBean.getId());
			approveRejectContainer.add(approveLink.setVisible(showApproveLink));
			approveRejectContainer.add(new Label("line.separator", "/").setRenderBodyOnly(true).setVisible(showApproveLink));
			
			approveRejectContainer.add(rejectLink.setVisible(showApproveLink));
			
			int status = Integer.parseInt(searchBean.getStatus().getId());
			
			approveRejectContainer.add(errorReportLink.setVisible((status == 4) || (status == 12)));
			
			item.add(approveRejectContainer);
			
			// Add the UseCase
			final String cssStyle = item.getIndex() % 2 == 0 ? BtpnConstants.DATA_VIEW_EVEN_ROW_CSS : BtpnConstants.DATA_VIEW_ODD_ROW_CSS;
			item.add(new SimpleAttributeModifier("class", cssStyle));
		}

		@Override
		public boolean isVisible() {
			return searchData != null && searchData.getSalaryDataList().size() != 0;

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
	 * This is the method for handling Search Data.
	 * 
	 * @param target
	 * @param isApprove
	 */
	protected void handleSearchSalaryData(AjaxRequestTarget target, final WebMarkupContainer container) {
		List<SalaryDataBean> salaryDataList = new ArrayList<SalaryDataBean>();
		
		int type = Integer.parseInt(searchData.getType().getId());
		
		try {
			// Find Request
			if ((searchData.getStatus() == null) || "2".equals(searchData.getStatus().getId())) {
				FindBulkFileWrkRequest request = basePage.getNewMobiliserRequest(FindBulkFileWrkRequest.class);
				
				request.setDateRangeBegin(PortalUtils.getSaveXMLGregorianCalendarFromDate(searchData.getDateRangeBegin(), null));
				request.setDateRangeEnd(PortalUtils.getSaveXMLGregorianCalendarToDate(searchData.getDateRangeEnd(), null));
				request.setCallerId(Long.valueOf(basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId()));
				
				FindBulkFileWrkResponse response = basePage.getBulkFileWrkClient().findWrk(request);
				if (MobiliserUtils.success(response)) {
					List<BulkFileWrkType> flist = new ArrayList<BulkFileWrkType>();
						
					if ((response.getItem() != null) && !response.getItem().isEmpty()) {
						for (BulkFileWrkType e : response.getItem()) {
							if (e.getType() == type) {
								flist.add(e);
							}
						}
					}
					
					salaryDataList.addAll(ConverterUtils.convertToSalaryWrkDataBean(flist, this.basePage.getLookupMapUtility(), this));
				} else if (MobiliserUtils.errorCode(response) != 205) {
					error(MobiliserUtils.errorMessage(response, basePage));
					// error(getLocalizer().getString("file.search.error", this));
				}
			}
			
			if ((searchData.getStatus() == null) || !"2".equals(searchData.getStatus().getId())) {
				FindBulkFileRequest request = basePage.getNewMobiliserRequest(FindBulkFileRequest.class);
				request.setDateRangeBegin(PortalUtils.getSaveXMLGregorianCalendarFromDate(searchData.getDateRangeBegin(), null));
				request.setDateRangeEnd(PortalUtils.getSaveXMLGregorianCalendarToDate(searchData.getDateRangeEnd(), null));
				request.setName(searchData.getName());
				request.setType(type);
				request.setCallerId(Long.valueOf(basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId()));
				
				if (searchData.getStatus() == null) {
					for (String id :
						lookupMapUtility.getLookupEntriesMap(BtpnConstants.RESOURCE_BUNDLE_BULK_FILE_PROCESSING_STATUS, basePage.getLocalizer(), basePage).keySet()) {
						
						if (!"2".equals(id)) {
							request.getStatus().add(Integer.valueOf(id));
						}
					}
				} else {
					request.getStatus().add(Integer.valueOf(searchData.getStatus().getId()));
				}
					
				// Fetch search data.
				FindBulkFileResponse response = this.basePage.getBulkFileClient().find(request);
				
				if (MobiliserUtils.success(response)) {
					salaryDataList.addAll(ConverterUtils.convertToSalaryDataBean(response.getItem(), this.basePage.getLookupMapUtility(), this.basePage, this));
				} else if (MobiliserUtils.errorCode(response) != 205) {
					error(MobiliserUtils.errorMessage(response, basePage));
					// error(getLocalizer().getString("file.search.error", this));
				}
			}	
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			LOG.error("Exception occured while handleSearchSalaryData/RegData ===> ", e);
		}
	
		searchData.setSalaryDataList(salaryDataList);
		
		container.setVisible(true);
		target.addComponent(container);
		target.addComponent(feedbackPanel);
		target.addComponent(approvalHeader);
		target.addComponent(navigator);
	}

	/**
	 * This is the method for handling approve
	 * @param target
	 */
	protected void handleApprove(AjaxRequestTarget target, final SalaryDataBean salaryData) {
		try {
				final ApproveBulkFileWrkRequest request = this.basePage.getNewMobiliserRequest(ApproveBulkFileWrkRequest.class);
				request.setWorkflowId(salaryData.getWorkFlowId());
				request.setCallerId(Long.valueOf(basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId()));
				
				final ApproveBulkFileWrkResponse response = this.basePage.getBulkFileWrkClient().approveWrk(request);
			
			if (this.basePage.evaluateBankPortalMobiliserResponse(response)) {
				final String msg =  "approve.success";
				this.basePage.getMobiliserWebSession().info(getLocalizer().getString(msg, this));
				setResponsePage(SearchSalaryDataPage.class );
			} else {
				final String msg =  "approve.fail";
				this.basePage.getMobiliserWebSession().error(getLocalizer().getString(msg, this));
				target.addComponent(feedbackPanel);
			}

		} catch (Exception e) {
			LOG.error("SearchSalaryDataPanel:handleApprove() ==> Error Approving ==> ", e);
			error(getLocalizer().getString("error.exception", this));
			target.addComponent(feedbackPanel);
		}

	}
	
	/**
	 * This is the method for handling reject.
	 * @param target
	 */
	protected void handleReject(AjaxRequestTarget target, final SalaryDataBean salaryData) {
		try {
			final RejectBulkFileWrkRequest request = this.basePage.getNewMobiliserRequest(RejectBulkFileWrkRequest.class);
			request.setWorkflowId(salaryData.getWorkFlowId());
			request.setCallerId(Long.valueOf(basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId()));
			
			final RejectBulkFileWrkResponse response = this.basePage.getBulkFileWrkClient().rejectWrk(request);
	
			if (this.basePage.evaluateBankPortalMobiliserResponse(response)) {
				final String msg = "reject.success";
				this.basePage.getMobiliserWebSession().info(getLocalizer().getString(msg, this));
				setResponsePage(SearchSalaryDataPage.class );
			} else {
				final String msg = "reject.fail";
				this.basePage.getMobiliserWebSession().error(getLocalizer().getString(msg, this));
				target.addComponent(feedbackPanel);
			}

		} catch (Exception e) {
			LOG.error("SearchSalaryDataPanel:handleReject() ==> Error Rejecting ==> ", e);
			error(getLocalizer().getString("error.exception", this));
			target.addComponent(feedbackPanel);
		}

	}

	/**
	 * This is the method for handle error report
	 * 
	 * @param salaryData
	 */
	/*
	protected void handleErrorReport(final SalaryDataBean salaryData) {
		try {
			final GetFileErrorDataRequest dataRequest = this.basePage
				.getNewMobiliserRequest(GetFileErrorDataRequest.class);
			dataRequest.setFileId(salaryData.getFileId());
			dataRequest.setFileName(salaryData.getFileName());
			dataRequest.setCustomerId(this.basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			final GetFileErrorDataResponse fileResponse = this.basePage.getSupportClient()
				.getFileErrorData(dataRequest);
			if (this.basePage.evaluateBankPortalMobiliserResponse(fileResponse)) {
				setResponsePage(new SalaryDataErrorPage(fileTypeId, salaryData.getFileName(),
					ConverterUtils.convertToSalaryDataErrorBeanList(fileResponse.getFileErrorObjects())));
			} else {
				error(getLocalizer().getString("error.fetch.data", this));
			}

		} catch (Exception e) {
			error(this.basePage.getLocalizer().getString("error.exception", this.basePage));
			LOG.error("Exception occured while fetching File the Error Report ==> ", e);
		}
	}
	

	/**
	 * This is the method for handle view data
	 * 
	 * @param salaryData
	 */
	protected void handleViewData(final AjaxRequestTarget target, final SalaryDataBean data, boolean isError) {
		try {
			// View Request
			if(Integer.parseInt( data.getStatus().getId() )  == 2) {
				
				final GetBulkFileWrkRequest request = this.basePage.getNewMobiliserRequest(GetBulkFileWrkRequest.class);
				// set workflow to fetch.
				request.setWorkflowId(data.getWorkFlowId());
				request.setRequest(true);
				request.setResponse(false);
				request.setCallerId(Long.valueOf(basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId()));
				
				LOG.info("GetBulkFileWrkRequest=>id:"+data.getWorkFlowId());
				
				final GetBulkFileWrkResponse response = this.basePage.getBulkFileWrkClient().getWrk(request);
				
				if (this.basePage.evaluateBankPortalMobiliserResponse(response)) {
					BulkFileWrkType bulkData = response.getFile();
					
					fileContent = bulkData.getRequest();
					
					if(fileContent != null){
						fileName = bulkData.getName();
						
						convertToCsvFile(target);
					}else{
						error(getLocalizer().getString("file.download.no.content", this));
					}
				} else {
					error(getLocalizer().getString("file.download.error", this));
				}
			}
			else
			{
				final GetBulkFileRequest request = this.basePage.getNewMobiliserRequest(GetBulkFileRequest.class);
				// set workflow to fetch.
				request.setId(data.getId().longValue());
				
				if (isError) {
					request.setRequest(false);
					request.setResponse(true);
				} else {
					request.setRequest(true);
					request.setResponse(false);
				}
				
				request.setCallerId(Long.valueOf(basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId()));
				
				LOG.info("GetBulkFileRequest=>id:"+data.getId());
				
				final GetBulkFileResponse response = this.basePage.getBulkFileClient().get(request);
				
				if (this.basePage.evaluateBankPortalMobiliserResponse(response)) {
					BulkFileType bulkData = response.getFile();
					
					fileContent =  isError ? bulkData.getResponse() : bulkData.getRequest();
						
					fileName = isError ? bulkData.getName() + ".error.txt" : bulkData.getName();
						
					convertToCsvFile(target);
				} else {
					error(getLocalizer().getString("file.download.error", this));
				}
			
			}
			
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			LOG.error("Exception occured while fetching Transaction GL Code List  ===> ", e);
		}
		target.addComponent(feedbackPanel);
	}

	/**
	 * This method converts to CSV File.
	 * 
	 * @param salaryData
	 */
	protected void convertToCsvFile(final AjaxRequestTarget target) {
		download.initiate(target);
	}

	/**
	 * Download to CSV File.
	 * 
	 * @author Vikram Gunda
	 */
	private class SalaryDataDownload extends AJAXDownload {

		private static final long serialVersionUID = 1L;

		@Override
		protected String getFileName() {
			return fileName;
		}

		@Override
		protected IResourceStream getResourceStream() {
			return new ByteArrayResource(fileName.toLowerCase().endsWith(".txt") ? "text/plain" : BtpnConstants.CSV_CONTENT_TYPE, fileContent).getResourceStream();
		}
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
}
