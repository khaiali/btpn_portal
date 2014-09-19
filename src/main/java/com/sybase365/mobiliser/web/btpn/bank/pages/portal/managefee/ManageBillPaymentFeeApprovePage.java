package com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee;

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
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.core.fee.facade.contract.wrk.BillerFeeWrkFindViewType;
import com.btpnwow.core.fee.facade.contract.wrk.FindBillerFeeWrkRequest;
import com.btpnwow.core.fee.facade.contract.wrk.FindBillerFeeWrkResponse;
import com.btpnwow.core.fee.facade.contract.wrk.GetBillerFeeWrkRequest;
import com.btpnwow.core.fee.facade.contract.wrk.GetBillerFeeWrkResponse;
import com.btpnwow.portal.common.util.BillerProductLookup.BillerProduct;
import com.btpnwow.portal.common.util.BillerProductLookup;
import com.btpnwow.portal.common.util.MobiliserUtils;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageBillPaymentFeeBean;
import com.sybase365.mobiliser.web.btpn.bank.common.dataproviders.ManageBillPaymentFeeDataProvider;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.behaviours.DateHeaderContributor;
import com.sybase365.mobiliser.web.btpn.common.components.BtpnCustomPagingNavigator;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnOrderByOrder;
import com.sybase365.mobiliser.web.btpn.util.BtpnUtils;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the Manage InterestApprove page for bank portals.
 * 
 * @author Feny Yanti
 */
public class ManageBillPaymentFeeApprovePage extends BtpnBaseBankPortalSelfCarePage {
	
	private static final String WICKET_ID_PAGEABLE = "pageable";
	private static final String WICKET_ID_LINK = "idLink";
	private static final String WICKET_ID_FEENAVIGATOR = "feeNavigator";
	private static final String WICKET_ID_FEETOTALITEMS = "feeHeader";

	private String feeTotalItemString;
	private int feeStartIndex = 0;
	private int feeEndIndex = 0;

	private ManageBillPaymentFeeBean feeBean;
	private FeedbackPanel feedbackPanel;
	WebMarkupContainer feeContainer;
	
	private ManageBillPaymentFeeDataProvider feeDataProvider;
	private Component fromDateComp;
	private Component toDateComp;
	
	@SpringBean(name = "billerProductLookup")
	private BillerProductLookup billerProductLookup;
	
	private static final Logger log = LoggerFactory.getLogger(ManageBillPaymentFeeApprovePage.class);

	
	public ManageBillPaymentFeeApprovePage(){
		super();
	}
	
	@Override
	protected void initOwnPageComponents() {
		super.initOwnPageComponents();
		addDateHeaderContributor();
		constructPage();
	}
	
	private void constructPage() {
		Form<ManageBillPaymentFeePage> form = new Form<ManageBillPaymentFeePage>("feeForm",
			new CompoundPropertyModel<ManageBillPaymentFeePage>(this));
		form.add(feedbackPanel = new FeedbackPanel("errorMessages"));
		feedbackPanel.setOutputMarkupId(true);
		feedbackPanel.setOutputMarkupPlaceholderTag(true);
		form.add(feedbackPanel);
		
		/*  FROM DATE */
		DateTextField fromDate = (DateTextField) DateTextField
				.forDatePattern("feeBean.dateRangeBegin",
						BtpnConstants.ID_EXPIRY_DATE_PATTERN)
				.setRequired(true).add(new ErrorIndicator());
		fromDateComp = fromDate;
		form.add(fromDateComp);
		
		/*  TO DATE */
		DateTextField toDate = (DateTextField) DateTextField
				.forDatePattern("feeBean.dateRangeEnd",
						BtpnConstants.ID_EXPIRY_DATE_PATTERN)
				.setRequired(true).add(new ErrorIndicator());
		toDateComp = toDate;
		form.add(toDateComp);
		
		// Add the Manage Products container
		feeContainer = new WebMarkupContainer("feeContainer");
		manageLimitDataView(feeContainer);
		feeContainer.setOutputMarkupId(true);
		feeContainer.setOutputMarkupPlaceholderTag(true);
		feeContainer.setVisible(false);
		form.add(feeContainer);
		
		Button addButton = new Button("btnAdd") {
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit() {
				setResponsePage(ManageBillPaymentFeeAddPage.class);
			}
		};
		form.add(addButton);
	
		form.add(new AjaxButton("searchBtn") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				feeBean.setManageBillerList(null);
				handleSearchFee(target);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form){
				target.addComponent(feedbackPanel);
				super.onError(target, form);
			}
		});
	
		form.add(feeContainer);
		add(form);

	}

	private void handleSearchFee(AjaxRequestTarget target) {
		if (!PortalUtils.exists(feeBean.getGlCode())){
			log.info(" ### handleSearchGL BILLER FEE GL CODE ### "+feeBean.getGlCode());
		}
		getBillerWrkList();
		feeContainer.setVisible(true);
		target.addComponent(feeContainer);
		target.addComponent(feedbackPanel);
	}
	
	private void getBillerWrkList(){
		
		List<ManageBillPaymentFeeBean> billerList = new ArrayList<ManageBillPaymentFeeBean>();
		final FindBillerFeeWrkRequest request; 
		
		try {
			
			request = this.getNewMobiliserRequest(FindBillerFeeWrkRequest.class);
			
			request.setDateRangeBegin(PortalUtils.getSaveXMLGregorianCalendarFromDate(feeBean.getDateRangeBegin(), null));
			request.setDateRangeEnd(PortalUtils.getSaveXMLGregorianCalendarToDate(feeBean.getDateRangeEnd(), null));
			request.setStart(0);
			request.setLength(Integer.MAX_VALUE);
			request.setCallerId(this.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			
			final FindBillerFeeWrkResponse response = this.getBillerFeeWrkClient().find(request);
			log.info(" ### (getBillerList) RESPONSE CODE ### "+response.getStatus().getCode());
			if (MobiliserUtils.success(response)) {
				log.info(" ### (getBillerList) RESPONSE SIZE ### " +response.getItem().size());
				billerList =  billPaymentWrkList(response.getItem());
			} else {
				error(MobiliserUtils.errorMessage(response.getStatus().getCode(), response.getStatus().getValue(), getLocalizer(), this));
			}		
			
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			log.error("Exception occured while fetching getBillerList  ===> ", e);
		}
		
		feeBean.setManageBillerList(billerList);
	}
	
	public static List<ManageBillPaymentFeeBean> billPaymentWrkList(
			final List<BillerFeeWrkFindViewType> txnList) {
		
		final List<ManageBillPaymentFeeBean> billerList = new ArrayList<ManageBillPaymentFeeBean>();
		for (final BillerFeeWrkFindViewType uc : txnList) {
			final ManageBillPaymentFeeBean billerListBean = new ManageBillPaymentFeeBean();
			billerListBean.setWorkflowId(uc.getWorkflowId());
			billerListBean.setDescription(uc.getDescription());
			billerListBean.setLastModifiedByName(uc.getLastModifiedByName());
			billerListBean.setLastModifiedByDate(uc.getLastModifiedDate().toString());
			billerList.add(billerListBean);
		}
		return billerList;
	}
	
	
	/**
	 * This method adds the AttachmentsDataView for the file upload and also adds the sorting logic for data view
	 */
	protected void manageLimitDataView(final WebMarkupContainer dataViewContainer) {

		feeDataProvider = new ManageBillPaymentFeeDataProvider("wrkId");

		final DataView<ManageBillPaymentFeeBean> dataView = new DataView<ManageBillPaymentFeeBean>(WICKET_ID_PAGEABLE, feeDataProvider){

			private static final long serialVersionUID = 1L;
			
			protected void onBeforeRender(){
				final ManageBillPaymentFeeDataProvider dataProvider = (ManageBillPaymentFeeDataProvider) internalGetDataProvider();
				dataProvider.setManageBillerFeeList(feeBean.getManageBillerList());
				refreshTotalItemCount();
				super.onBeforeRender();
			}
			
			@Override
			protected void populateItem(final Item<ManageBillPaymentFeeBean> item) {
				final ManageBillPaymentFeeBean entry = item.getModelObject();
				
				final AjaxLink<ManageBillPaymentFeeBean> billerLink = new AjaxLink<ManageBillPaymentFeeBean>(WICKET_ID_LINK, item.getModel()) {
					private static final long serialVersionUID = 1L;
					@Override
					public void onClick(AjaxRequestTarget target) {
						handleBillerFeeDetailClick((ManageBillPaymentFeeBean)item.getModelObject());
					}
				};
				
				billerLink.add(new Label("workflowId", entry.getWorkflowId().toString()));
				item.add(billerLink);
				item.add(new Label("description", entry.getDescription()));
				item.add(new Label("lastModifiedName", entry.getLastModifiedByName()));
				item.add(new Label("lastModifiedDate", entry.getLastModifiedByDate()));
				
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
				final ManageBillPaymentFeeDataProvider dataProvider = (ManageBillPaymentFeeDataProvider) internalGetDataProvider();
				dataProvider.setManageBillerFeeList(feeBean.getManageBillerList());
				return feeBean.getManageBillerList().size() != 0;
			}
		};
	
		// Add The navigation
		final BtpnCustomPagingNavigator navigator = new BtpnCustomPagingNavigator(WICKET_ID_FEENAVIGATOR, dataView){
			private static final long serialVersionUID = 1L;
			@Override
			public boolean isVisible(){
				return feeBean != null && feeBean.getManageBillerList().size() != 0;
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
				final String displayTotalItemsText = ManageBillPaymentFeeApprovePage.this.getLocalizer().getString(
						"fee.totalitems.header", ManageBillPaymentFeeApprovePage.this);
				return String.format(displayTotalItemsText, feeTotalItemString, feeStartIndex, feeEndIndex);
			}
		};
	
		final Label feeHeader = new Label(WICKET_ID_FEETOTALITEMS, headerDisplayModel) {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean isVisible() {
				return feeBean != null && feeBean.getManageBillerList().size() != 0;
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
				return feeBean != null && feeBean.getManageBillerList().size() == 0;
			}
		}.setRenderBodyOnly(true).setOutputMarkupPlaceholderTag(true));
			
		// Add the sort providers
		dataViewContainer.add(new BtpnOrderByOrder("orderByWorkFlowId", "workflowId", feeDataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByDesc", "description", feeDataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByName", "lastModifiedName", feeDataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByDate", "lastModifiedDate", feeDataProvider, dataView));
		dataViewContainer.addOrReplace(dataView);
	}
	
	private void handleBillerFeeDetailClick(final ManageBillPaymentFeeBean feeBean){
		
		final GetBillerFeeWrkRequest request;
		
		try {
			
			log.info(" ###  handleBillerFeeDetailClick ### ");
			request = this.getNewMobiliserRequest(GetBillerFeeWrkRequest.class);
			request.setWorkflowId(feeBean.getWorkflowId());
			request.setCallerId(this.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			
			final GetBillerFeeWrkResponse response = this.getBillerFeeWrkClient().get(request);
			
			if (this.evaluateBankPortalMobiliserResponse(response)){
				ManageBillPaymentFeeBean bean = new ManageBillPaymentFeeBean();
				final Component component = null;
				
				bean.setWorkflowId(request.getWorkflowId());
				bean.setDescription(response.getDescription());
				bean.setUseCase(new CodeValue(String.valueOf(response.getUseCase()),
						BtpnUtils.getDropdownValueFromId(this.getLookupMapUtility().getLookupNamesMap(
								BtpnConstants.RESOURCE_BUNDLE_USE_CASES, component), 
								BtpnConstants.RESOURCE_BUNDLE_USE_CASES + "." +String.valueOf(response.getUseCase()))) );
			
				bean.setBillerId(response.getBillerId());
				bean.setProductId(response.getProductId());
				String lang = this.getMobiliserWebSession().getBtpnLoggedInCustomer().getLanguage().toLowerCase();
				BillerProduct bill = billerProductLookup.get(lang, bean.getBillerId(), bean.getProductId());
				if(bill!=null)
				{
					bean.setProductLabel(bill.getDescription() == null ? bill.getLabel() : bill.getDescription());
				}
			
				//cust type
				if (response.getDebitCustomerType() != null) {
					bean.setCustomerType(new CodeValue(response.getDebitCustomerType().toString(), 
							BtpnUtils.getDropdownValueFromId(this.getLookupMapUtility().getLookupNamesMap(
									BtpnConstants.RESOURCE_USE_CASE_FEE_CUSTOMER_TYPE, component), 
									BtpnConstants.RESOURCE_USE_CASE_FEE_CUSTOMER_TYPE + "." +response.getDebitCustomerType().toString())
							));
				}
				
				//org unit
				if (response.getDebitOrgUnit() != null) {
					bean.setOrgUnit(new CodeValue(response.getDebitOrgUnit().toString(), 
							BtpnUtils.getDropdownValueFromId(this.getLookupMapUtility().getLookupNamesMap(
									BtpnConstants.RESOURCE_USE_CASE_FEE_ORG_UNIT, component), 
									BtpnConstants.RESOURCE_USE_CASE_FEE_ORG_UNIT + "." +response.getDebitOrgUnit().toString())
							));
				}
				
				//pitype	
				if(response.getDebitPiType() != null) {
					bean.setPiType(new CodeValue(response.getDebitPiType().toString(), 
							BtpnUtils.getDropdownValueFromId(this.getLookupMapUtility().getLookupNamesMap(
									BtpnConstants.RESOURCE_BUNDLE_PI_TYPE, component), 
									BtpnConstants.RESOURCE_BUNDLE_PI_TYPE + "." +response.getDebitPiType().toString())
							));
				}
				
				//currency
				bean.setCurrency(new CodeValue(String.valueOf(response.getCurrencyCode()),
						BtpnUtils.getDropdownValueFromId(this.getLookupMapUtility().getLookupNamesMap(
								BtpnConstants.RESOURCE_USE_CASE_FEE_CURRENCY, component), 
								BtpnConstants.RESOURCE_USE_CASE_FEE_CURRENCY + "." +String.valueOf(response.getCurrencyCode()))));
			
				//glcode
				bean.setGlCode(new CodeValue(String.valueOf(response.getGlCode()),
						BtpnUtils.getDropdownValueFromId(this.getLookupMapUtility().getLookupNamesMap(
								BtpnConstants.RESOURCE_BILL_PAYMENT_FEE_GL_CODES, component), 
								BtpnConstants.RESOURCE_BILL_PAYMENT_FEE_GL_CODES + "." +String.valueOf(response.getGlCode()))));
			
				
				bean.setValidFrom(PortalUtils.getSaveDate(response.getValidFrom()));
				bean.setApplyToPayee(response.getPayeeFee());
				bean.getManageDetailsWrkList().addAll(response.getEntry());//FeeEntryWrkType
				
				setResponsePage(new ManageBillPaymentFeeDetailsApprovePage(bean));
				
			}else{
				handleSpecificErrorMessage(response.getStatus().getCode());
			}
			
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			log.error("Exception occured while Get Detail Use Case Fee Code Details  ===> ", e);
		}
	}
	
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
	
	protected void addDateHeaderContributor() {
		final String chooseDtTxt = this.getLocalizer().getString(
				"datepicker.chooseDate", this);
		final String locale = this.getLocale().getLanguage();
		add(new HeaderContributor(new DateHeaderContributor(locale,
				BtpnConstants.DATE_FORMAT_PATTERN_PICKER, chooseDtTxt)));
	}
	
	
}



