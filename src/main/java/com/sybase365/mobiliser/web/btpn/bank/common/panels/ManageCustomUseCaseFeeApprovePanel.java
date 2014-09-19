package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.core.fee.facade.contract.wrk.FeeEntryWrkType;
import com.btpnwow.core.fee.facade.contract.wrk.FindUseCaseFeeWrkRequest;
import com.btpnwow.core.fee.facade.contract.wrk.FindUseCaseFeeWrkResponse;
import com.btpnwow.core.fee.facade.contract.wrk.GetUseCaseFeeWrkRequest;
import com.btpnwow.core.fee.facade.contract.wrk.GetUseCaseFeeWrkResponse;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageCustomUseCaseFeeBean;
import com.sybase365.mobiliser.web.btpn.bank.common.dataproviders.ManageUseCaseFeeApproveDataProvider;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee.ManageCustomUseCaseFeeDetailApprovePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.behaviours.DateHeaderContributor;
import com.sybase365.mobiliser.web.btpn.common.components.BtpnCustomPagingNavigator;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnOrderByOrder;
import com.sybase365.mobiliser.web.btpn.util.BtpnUtils;
import com.sybase365.mobiliser.web.btpn.util.ConverterUtils;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the Manage Fee page for bank portals.
 * 
 * @author Andi Samallangi W
 */
public class ManageCustomUseCaseFeeApprovePanel extends Panel {

	private static final long serialVersionUID = 1L;
	
	private static final Logger log = LoggerFactory.getLogger(ManageCustomUseCaseFeeApprovePanel.class);

	private static final String WICKET_ID_PAGEABLE = "pageable";
	private static final String WICKET_ID_LINK = "idLink";
	private static final String WICKET_ID_FEENAVIGATOR = "feeNavigator";
	private static final String WICKET_ID_FEETOTALITEMS = "feeHeader";

	private String feeTotalItemString;
	private int feeStartIndex = 0;
	private int feeEndIndex = 0;
	
	protected BtpnMobiliserBasePage basePage;
	private FeedbackPanel feedBackPanel;
	private ManageCustomUseCaseFeeBean ucFeeBean;
	private FeeEntryWrkType feeDetailsBean;
	WebMarkupContainer feeContainer;
	
	private ManageUseCaseFeeApproveDataProvider feeDataProvider;

	private Component fromDateComp;
	private Component toDateComp;
	private Component descComp;

	/**
	 * Constructor for this page.
	 */
	public ManageCustomUseCaseFeeApprovePanel(String id, BtpnBaseBankPortalSelfCarePage basePage) {
		super(id);
		this.basePage = basePage;
		addDateHeaderContributor();
		constructPanel();
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	private void constructPanel() {
		
		log.info(" ### (ManageCustomUseCaseFeeApprovePanel) constructPanel() ### "); 
		
		Form<ManageCustomUseCaseFeeApprovePanel> form = new Form<ManageCustomUseCaseFeeApprovePanel>("ucFeeAprForm",
			new CompoundPropertyModel<ManageCustomUseCaseFeeApprovePanel>(this));
		
		// Add feedback panel for Error Messages
		feedBackPanel = new FeedbackPanel("errorMessages");
		feedBackPanel.setOutputMarkupId(true);
		feedBackPanel.setOutputMarkupPlaceholderTag(true);
		form.add(feedBackPanel);
		
		/*  FROM DATE */
		DateTextField fromDate = (DateTextField) DateTextField
				.forDatePattern("ucFeeBean.dateRangeBegin",
						BtpnConstants.ID_EXPIRY_DATE_PATTERN)
				.setRequired(true).add(new ErrorIndicator());
		fromDateComp = fromDate;
		form.add(fromDateComp);
		
		/*  TO DATE */
		DateTextField toDate = (DateTextField) DateTextField
				.forDatePattern("ucFeeBean.dateRangeEnd",
						BtpnConstants.ID_EXPIRY_DATE_PATTERN)
				.setRequired(true).add(new ErrorIndicator());
		toDateComp = toDate;
		form.add(toDateComp);
		
		/* DESC */
		form.add(descComp = new TextField<String>("ucFeeBean.description").add(new ErrorIndicator()));
		descComp.setOutputMarkupId(true);
		
		// Add the Manage Products container
		feeContainer = new WebMarkupContainer("feeContainer");
		manageFeeDataView(feeContainer);
		feeContainer.setOutputMarkupId(true);
		feeContainer.setOutputMarkupPlaceholderTag(true);
		feeContainer.setVisible(false);
		form.add(feeContainer);
		
		
		form.add(new AjaxButton("searchBtn"){
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form){
				if (!PortalUtils.exists(ucFeeBean)){
					ucFeeBean = new ManageCustomUseCaseFeeBean();
				}
				ucFeeBean.setManageCusUseCaseFeeList(new ArrayList<ManageCustomUseCaseFeeBean>());
				log.info(" ### KLICK SEARCH ID ### "+ucFeeBean.getId());
				log.info(" ### KLICK SEARCH FEE DESC ### "+ucFeeBean.getDescription());
				handleSearchFee(target);
			};
			
			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.addComponent(feedBackPanel);
				target.addComponent(descComp);
				super.onError(target, form);
			}
			
		});
		// Add add Button
		add(form);
	}
	
	private void handleSearchFee(AjaxRequestTarget target) {
		if (!PortalUtils.exists(ucFeeBean.getGlCode())){
			log.info(" ### handleSearchGL UC FEE CODE ### "+ucFeeBean.getGlCode());
		}
		getFeeWrkList();
		feeContainer.setVisible(true);
		target.addComponent(feeContainer);
		target.addComponent(feedBackPanel);
		target.addComponent(descComp);
	}
	

	/**
	 * This method adds the AttachmentsDataView for the file upload and also adds the sorting logic for data view
	 */
	protected void manageFeeDataView(final WebMarkupContainer dataViewContainer) {

		feeDataProvider = new ManageUseCaseFeeApproveDataProvider("workflowId");

		final DataView<ManageCustomUseCaseFeeBean> dataView = new DataView<ManageCustomUseCaseFeeBean>(WICKET_ID_PAGEABLE, feeDataProvider){

			private static final long serialVersionUID = 1L;
			
			protected void onBeforeRender(){
				final ManageUseCaseFeeApproveDataProvider dataProvider = (ManageUseCaseFeeApproveDataProvider) internalGetDataProvider();
				dataProvider.setManageUseCaseFeeList(ucFeeBean.getManageCusUseCaseFeeList());
				refreshTotalItemCount();
				super.onBeforeRender();
			}
			
			@Override
			protected void populateItem(final Item<ManageCustomUseCaseFeeBean> item) {
				final ManageCustomUseCaseFeeBean entry = item.getModelObject();
				
				final AjaxLink<ManageCustomUseCaseFeeBean> feeLink = new AjaxLink<ManageCustomUseCaseFeeBean>(WICKET_ID_LINK, item.getModel()) {
					private static final long serialVersionUID = 1L;
					@Override
					public void onClick(AjaxRequestTarget target) {
						handleFeeDetailClick((ManageCustomUseCaseFeeBean)item.getModelObject());
					}
				};
				
				feeLink.add(new Label("workflowId", entry.getWorkFlowId()));
				item.add(feeLink);
				item.add(new Label("description", entry.getDescription()));
				
				final String cssStyle = item.getIndex() % 2 == 0 ? BtpnConstants.DATA_VIEW_EVEN_ROW_CSS
						: BtpnConstants.DATA_VIEW_ODD_ROW_CSS;
				item.add(new SimpleAttributeModifier("class", cssStyle));
			}
		
			private void refreshTotalItemCount() {
				final int size = internalGetDataProvider().size();
				feeTotalItemString = new Integer(size).toString();
				if (size > 0) {
					feeStartIndex = getCurrentPage() * getItemsPerPage() + 1;
					feeEndIndex = feeStartIndex + getItemsPerPage() - 1;
					if (feeEndIndex > size) {
						feeEndIndex = size;
					}
				} else {
					feeStartIndex = 0;
					feeEndIndex = 0;
				}
			}
		
			@Override
			public boolean isVisible(){
				final ManageUseCaseFeeApproveDataProvider dataProvider = (ManageUseCaseFeeApproveDataProvider) internalGetDataProvider();
				dataProvider.setManageUseCaseFeeList(ucFeeBean.getManageCusUseCaseFeeList());
				return ucFeeBean.getManageCusUseCaseFeeList().size() != 0;
			}
		};
	
		// Add The navigation
		final BtpnCustomPagingNavigator navigator = new BtpnCustomPagingNavigator(WICKET_ID_FEENAVIGATOR, dataView){
			private static final long serialVersionUID = 1L;
			@Override
			public boolean isVisible(){
				return ucFeeBean != null && ucFeeBean.getManageCusUseCaseFeeList().size() != 0;
			}
		};
		navigator.setOutputMarkupId(true);
		navigator.setOutputMarkupPlaceholderTag(true);
		dataViewContainer.add(navigator);
		dataView.setItemsPerPage(20);
	
		// Add the header
		IModel<String> headerDisplayModel = new AbstractReadOnlyModel<String>() {
			private static final long serialVersionUID = 1L;
			@Override
			public String getObject() {
				final String displayTotalItemsText = ManageCustomUseCaseFeeApprovePanel.this.getLocalizer().getString(
						"fee.totalitems.header", ManageCustomUseCaseFeeApprovePanel.this);
				return String.format(displayTotalItemsText, feeTotalItemString, feeStartIndex, feeEndIndex);
			}
		};
	
		final Label feeHeader = new Label(WICKET_ID_FEETOTALITEMS, headerDisplayModel) {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean isVisible() {
				return ucFeeBean != null && ucFeeBean.getManageCusUseCaseFeeList().size() != 0;
			}
		};
		feeHeader.setOutputMarkupId(true);
		feeHeader.setOutputMarkupPlaceholderTag(true);
		dataViewContainer.add(feeHeader);
	
		// Add the no items label.
		dataViewContainer.add(new Label("no.items", getLocalizer().getString(
				"fee.emptyRecordsMessage", this)) {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean isVisible() {
				return ucFeeBean != null && ucFeeBean.getManageCusUseCaseFeeList().size() == 0;
			}
		}.setRenderBodyOnly(true).setOutputMarkupPlaceholderTag(true));
			
		// Add the sort providers
		dataViewContainer.add(new BtpnOrderByOrder("orderByWorkFlowId", "workflowId", feeDataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByDesc", "description", feeDataProvider, dataView));
		dataViewContainer.addOrReplace(dataView);
	
	}
	

	private void getFeeWrkList(){
		
		List<ManageCustomUseCaseFeeBean> ucFeeList = new ArrayList<ManageCustomUseCaseFeeBean>();
		final FindUseCaseFeeWrkRequest request; 
		
		try {
			
			
			request = basePage.getNewMobiliserRequest(FindUseCaseFeeWrkRequest.class);
			XMLGregorianCalendar fromDate = PortalUtils.getSaveXMLGregorianCalendarFromDate(ucFeeBean.getDateRangeBegin(), TimeZone.getDefault());
			request.setDateRangeBegin(fromDate);
			XMLGregorianCalendar toDate = PortalUtils.getSaveXMLGregorianCalendarToDate(ucFeeBean.getDateRangeEnd(), TimeZone.getDefault());
			request.setDateRangeEnd(toDate);
			request.setCallerId(this.basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			request.setStart(0);
			request.setLength(Integer.MAX_VALUE);
		
			final FindUseCaseFeeWrkResponse response = this.basePage.getUcFeeWrkClient().find(request);
			log.info(" ### (getFeeWrkList) RESPONSE CODE ### "+response.getStatus().getCode());
			if (this.basePage.evaluateBankPortalMobiliserResponse(response)) {
				ucFeeList =  ConverterUtils.convertToManageCustomUseCaseFeeWrkBean(response.getItem());
			} else {
				handleSpecificErrorMessage(response.getStatus().getCode());
			}		
			
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			log.error("Exception occured while fetching Use Case Fee Details  ===> ", e);
		}
		
		ucFeeBean.setManageCusUseCaseFeeList(ucFeeList);
	}
	
	
	private void handleFeeDetailClick(final ManageCustomUseCaseFeeBean ucFeeBean){
		
		final GetUseCaseFeeWrkRequest request;
		
		try {
			
			request = this.basePage.getNewMobiliserRequest(GetUseCaseFeeWrkRequest.class);
			request.setWorkflowId(ucFeeBean.getWorkFlowId());
			request.setCallerId(basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			
			final GetUseCaseFeeWrkResponse response = this.basePage.getUcFeeWrkClient().get(request);
			log.info(" ### (ManageCustomUseCaseFeeApprovePanel) RESPONSE CODE ### "+response.getStatus().getCode()); 
			if (this.basePage.evaluateBankPortalMobiliserResponse(response)){
				final Component component = null;
				ManageCustomUseCaseFeeBean bean = new ManageCustomUseCaseFeeBean();
				
				bean.setId(ucFeeBean.getId());
				bean.setWorkFlowId(ucFeeBean.getWorkFlowId());
				
				/* USE CASE */ 
				bean.setUseCase(new CodeValue(response.getUseCase().toString(),
						BtpnUtils.getDropdownValueFromId(basePage.getLookupMapUtility().getLookupNamesMap(
								BtpnConstants.RESOURCE_BUNDLE_USE_CASES, component),
								BtpnConstants.RESOURCE_BUNDLE_USE_CASES + "." +response.getUseCase().toString())));
				
				/* DEBIT ORG UNIT */
				if(response.getDebitOrgUnit() == null) {
					bean.setDebitOrgUnit(new CodeValue("-","-"));
				}
				else {
					bean.setDebitOrgUnit(new CodeValue(response.getDebitOrgUnit().toString(),
							BtpnUtils.getDropdownValueFromId(basePage.getLookupMapUtility().getLookupNamesMap(
									BtpnConstants.RESOURCE_USE_CASE_FEE_ORG_UNIT, component), 
									BtpnConstants.RESOURCE_USE_CASE_FEE_ORG_UNIT + "." +response.getDebitOrgUnit().toString())));
				
				}
					
				/* CREDIT ORG UNIT */
				if(response.getCreditOrgUnit() == null) {
					bean.setCreditOrgUnit(new CodeValue("-","-"));
				}
				else {
					bean.setCreditOrgUnit(new CodeValue(response.getCreditOrgUnit().toString(),
							BtpnUtils.getDropdownValueFromId(basePage.getLookupMapUtility().getLookupNamesMap(
									BtpnConstants.RESOURCE_USE_CASE_FEE_ORG_UNIT, component), 
									BtpnConstants.RESOURCE_USE_CASE_FEE_ORG_UNIT + "." +response.getCreditOrgUnit().toString())));
					
				}
				
				/* GL CODE */
				log.info(" ### (ManageCustomUseCaseFeePanel) handleFeeDetailClick GL CODE ### " +(response.getGlCode().toString()));
				bean.setGlCode(new CodeValue(response.getGlCode().toString(),
						BtpnUtils.getDropdownValueFromId(basePage.getLookupMapUtility().getLookupNamesMap(
								BtpnConstants.RESOURCE_BILL_PAYMENT_FEE_GL_CODES, component), 
								BtpnConstants.RESOURCE_BILL_PAYMENT_FEE_GL_CODES + "." +response.getGlCode().toString())));
				
				/* DEBIT PI TYPE */
				if(response.getDebitPiType() == null) {
					bean.setDebitPiType(new CodeValue("-","-"));
				}
				else {
					bean.setDebitPiType(new CodeValue(response.getDebitPiType().toString(),
							BtpnUtils.getDropdownValueFromId(basePage.getLookupMapUtility().getLookupNamesMap(
									BtpnConstants.RESOURCE_BUNDLE_PI_TYPE, component), 
									BtpnConstants.RESOURCE_BUNDLE_PI_TYPE + "." +response.getDebitPiType().toString())));
				
				}
				
					
				/* CREDIT PI TYPE */
				if(response.getCreditPiType() == null) {
					bean.setCreditPiType(new CodeValue("-","-"));
				}
				else {
					bean.setCreditPiType(new CodeValue(response.getCreditPiType().toString(),
							BtpnUtils.getDropdownValueFromId(basePage.getLookupMapUtility().getLookupNamesMap(
									BtpnConstants.RESOURCE_BUNDLE_PI_TYPE, component),
									BtpnConstants.RESOURCE_BUNDLE_PI_TYPE + "." +response.getCreditPiType().toString())));
					
				}
			
					
				//CUST TYPEID
				if (response.getCustomerType() == null) {
					bean.setCustomerType(new CodeValue("-","-"));
				} else {
	
					bean.setCustomerType(new CodeValue(response.getCustomerType().toString(),
							BtpnUtils.getDropdownValueFromId(basePage.getLookupMapUtility().getLookupNamesMap(
									BtpnConstants.RESOURCE_USE_CASE_FEE_CUSTOMER_TYPE, component), 
									BtpnConstants.RESOURCE_USE_CASE_FEE_CUSTOMER_TYPE + "." +response.getCustomerType().toString())));
				}
				
		
				bean.setValidFrom(PortalUtils.getSaveDate(response.getValidFrom()));
						
				/* CURRENCY CODE */
				bean.setCurrencyCode(new CodeValue(response.getCurrencyCode(),
						BtpnUtils.getDropdownValueFromId(basePage.getLookupMapUtility().getLookupNamesMap(
								BtpnConstants.RESOURCE_USE_CASE_FEE_CURRENCY, component), 
								BtpnConstants.RESOURCE_USE_CASE_FEE_CURRENCY + "." +response.getCurrencyCode())));
				
				bean.setDescription(response.getDescription());
				bean.getManageDetailsWrkList().addAll(response.getEntry());
			
				setResponsePage(new ManageCustomUseCaseFeeDetailApprovePage(bean));
				
			}else{
				handleSpecificErrorMessage(response.getStatus().getCode());
			}
			
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			log.error("Exception occured while Get Detail Use Case Fee Code Details  ===> ", e);
		}
	}
	
	
	/**
	 * This method handles the specific error message.
	 */
	private String handleSpecificErrorMessage(final int errorCode) {
		// Specific error message handling
		final String messageKey = "error.uc.fee" + errorCode;
		String message = getLocalizer().getString(messageKey, this);
		// Generic error messages
		if (messageKey.equals(message)) {
			message = getLocalizer().getString("uc.fee.error", this);
		}
		return message;
	}
	
	/**
	 * Adds the date header contributor
	 */
	protected void addDateHeaderContributor() {
		final String chooseDtTxt = this.getLocalizer().getString(
				"datepicker.chooseDate", basePage);
		final String locale = this.getLocale().getLanguage();
		add(new HeaderContributor(new DateHeaderContributor(locale,
				BtpnConstants.DATE_FORMAT_PATTERN_PICKER, chooseDtTxt)));
	}
	
}
