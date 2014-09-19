package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.core.interest.facade.contract.wrk.FindInterestWrkRequest;
import com.btpnwow.core.interest.facade.contract.wrk.FindInterestWrkResponse;
import com.btpnwow.core.interest.facade.contract.wrk.GetInterestWrkRequest;
import com.btpnwow.core.interest.facade.contract.wrk.GetInterestWrkResponse;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageInterestApproveBean;
import com.sybase365.mobiliser.web.btpn.bank.common.dataproviders.ManageInterestApproveDataProvider;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageinterest.ManageInterestDetailApprovePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.behaviours.DateHeaderContributor;
import com.sybase365.mobiliser.web.btpn.common.components.BtpnCustomPagingNavigator;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnOrderByOrder;
import com.sybase365.mobiliser.web.btpn.util.BtpnUtils;
import com.sybase365.mobiliser.web.btpn.util.ConverterUtils;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the Manage Interest Approve page for bank portals.
 * 
 * @author Feny Yanti
 */
public class ManageInterestApprovePanel extends Panel {

	private static final long serialVersionUID = 1L;
	
	private static final Logger log = LoggerFactory.getLogger(ManageInterestApprovePanel.class);

	private static final String WICKET_ID_PAGEABLE = "pageable";
	private static final String WICKET_ID_LINK = "idLink";
	private static final String WICKET_ID_INTERESTNAVIGATOR = "interestNavigator";
	private static final String WICKET_ID_INTERESTTOTALITEMS = "interestHeader";

	private String interestTotalItemString;
	private int interestStartIndex = 0;
	private int interestEndIndex = 0;
	
	protected BtpnMobiliserBasePage basePage;
	private FeedbackPanel feedBackPanel;
	private ManageInterestApproveBean interestBean;
	WebMarkupContainer interestContainer;
	
	private ManageInterestApproveDataProvider interestDataProvider;

	private Component fromDateComp;
	private Component toDateComp;

	/**
	 * Constructor for this page.
	 */
	public ManageInterestApprovePanel(String id, BtpnBaseBankPortalSelfCarePage basePage) {
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
		
		log.info(" ### (ManageInterestApprovePanel) constructPanel() ### "); 
		
		Form<ManageInterestApprovePanel> form = new Form<ManageInterestApprovePanel>("interestAprForm",
			new CompoundPropertyModel<ManageInterestApprovePanel>(this));
		
		// Add feedback panel for Error Messages
		feedBackPanel = new FeedbackPanel("errorMessages");
		feedBackPanel.setOutputMarkupId(true);
		feedBackPanel.setOutputMarkupPlaceholderTag(true);
		form.add(feedBackPanel);
		
		/*  FROM DATE */
		DateTextField fromDate = (DateTextField) DateTextField
				.forDatePattern("interestBean.dateRangeBegin",
						BtpnConstants.ID_EXPIRY_DATE_PATTERN)
				.setRequired(true).add(new ErrorIndicator());
		fromDateComp = fromDate;
		form.add(fromDateComp);
		
		/*  TO DATE */
		DateTextField toDate = (DateTextField) DateTextField
				.forDatePattern("interestBean.dateRangeEnd",
						BtpnConstants.ID_EXPIRY_DATE_PATTERN)
				.setRequired(true).add(new ErrorIndicator());
		toDateComp = toDate;
		form.add(toDateComp);
				
		
		// Add the Manage Interest container
		interestContainer = new WebMarkupContainer("interestContainer");
		manageInterestDataView(interestContainer);
		interestContainer.setOutputMarkupId(true);
		interestContainer.setOutputMarkupPlaceholderTag(true);
		interestContainer.setVisible(false);
		form.add(interestContainer);
		
		form.add(new AjaxButton("searchBtn"){
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form){
				if (!PortalUtils.exists(interestBean)){
					interestBean = new ManageInterestApproveBean();
				}
				interestBean.setManageInterestList(new ArrayList<ManageInterestApproveBean>());
				handleSearchInterest(target);
			};
			
			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.addComponent(feedBackPanel);
				super.onError(target, form);
			}
			
		});
		// Add add Button
		add(form);

	}
	
	private void handleSearchInterest(AjaxRequestTarget target) {
		
		getInterestWrkList();
		interestContainer.setVisible(true);
		target.addComponent(interestContainer);
		target.addComponent(feedBackPanel);

	}
	

	/**
	 * This method adds the AttachmentsDataView for the file upload and also adds the sorting logic for data view
	 */
	protected void manageInterestDataView(final WebMarkupContainer dataViewContainer) {

		interestDataProvider = new ManageInterestApproveDataProvider("workflowId");

		final DataView<ManageInterestApproveBean> dataView = new DataView<ManageInterestApproveBean>(WICKET_ID_PAGEABLE, interestDataProvider){

			private static final long serialVersionUID = 1L;
			
			protected void onBeforeRender(){
				final ManageInterestApproveDataProvider dataProvider = (ManageInterestApproveDataProvider) internalGetDataProvider();
				dataProvider.setManageInterestList(interestBean.getManageInterestList());
				refreshTotalItemCount();
				super.onBeforeRender();
			}
			
			@Override
			protected void populateItem(final Item<ManageInterestApproveBean> item) {
				final ManageInterestApproveBean entry = item.getModelObject();
				
				final AjaxLink<ManageInterestApproveBean> interestLink = new AjaxLink<ManageInterestApproveBean>(WICKET_ID_LINK, item.getModel()) {
					private static final long serialVersionUID = 1L;
					@Override
					public void onClick(AjaxRequestTarget target) {
						handleInterestDetailClick((ManageInterestApproveBean)item.getModelObject());
					}
				};
				
				interestLink.add(new Label("workflowId", entry.getWorkFlowId()));
				item.add(interestLink);
				item.add(new Label("description", entry.getDescription()));
			
				final String cssStyle = item.getIndex() % 2 == 0 ? BtpnConstants.DATA_VIEW_EVEN_ROW_CSS
						: BtpnConstants.DATA_VIEW_ODD_ROW_CSS;
				item.add(new SimpleAttributeModifier("class", cssStyle));
			}
		
			private void refreshTotalItemCount() {
				final int size = internalGetDataProvider().size();
				interestTotalItemString = new Integer(size).toString();
				if (size > 0) {
					interestStartIndex = getCurrentPage() * getItemsPerPage() + 1;
					interestEndIndex = interestStartIndex + getItemsPerPage() - 1;
					if (interestEndIndex > size) {
						interestEndIndex = size;
					}
				} else {
					interestStartIndex = 0;
					interestEndIndex = 0;
				}
			}
		
			@Override
			public boolean isVisible(){
				final ManageInterestApproveDataProvider dataProvider = (ManageInterestApproveDataProvider) internalGetDataProvider();
				dataProvider.setManageInterestList(interestBean.getManageInterestList());
				return interestBean.getManageInterestList().size() != 0;
			}
		};
	
		// Add The navigation
		final BtpnCustomPagingNavigator navigator = new BtpnCustomPagingNavigator(WICKET_ID_INTERESTNAVIGATOR, dataView){
			private static final long serialVersionUID = 1L;
			@Override
			public boolean isVisible(){
				return interestBean != null && interestBean.getManageInterestList().size() != 0;
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
				final String displayTotalItemsText = ManageInterestApprovePanel.this.getLocalizer().getString(
						"interest.totalitems.header", ManageInterestApprovePanel.this);
				return String.format(displayTotalItemsText, interestTotalItemString, interestStartIndex, interestEndIndex);
			}
		};
	
		final Label interestHeader = new Label(WICKET_ID_INTERESTTOTALITEMS, headerDisplayModel) {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean isVisible() {
				return interestBean != null && interestBean.getManageInterestList().size() != 0;
			}
		};
		interestHeader.setOutputMarkupId(true);
		interestHeader.setOutputMarkupPlaceholderTag(true);
		dataViewContainer.add(interestHeader);
	
		// Add the no items label.
		dataViewContainer.add(new Label("no.items", getLocalizer().getString(
				"interest.emptyRecordsMessage", this)) {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean isVisible() {
				return interestBean != null && interestBean.getManageInterestList().size() == 0;
			}
		}.setRenderBodyOnly(true).setOutputMarkupPlaceholderTag(true));
			
		// Add the sort providers
		dataViewContainer.add(new BtpnOrderByOrder("orderByWorkFlowId", "workflowId", interestDataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByDesc", "description", interestDataProvider, dataView));
		dataViewContainer.addOrReplace(dataView);
	
	}
	

	private void getInterestWrkList(){
		
		List<ManageInterestApproveBean> interestList = new ArrayList<ManageInterestApproveBean>();
		final FindInterestWrkRequest request; 
		
		try {
			
			log.info(" ### (getInterestWrkList) ###" );
			request = basePage.getNewMobiliserRequest(FindInterestWrkRequest.class);
			
			XMLGregorianCalendar fromDate = PortalUtils.getSaveXMLGregorianCalendarFromDate(interestBean.getDateRangeBegin(), TimeZone.getDefault());
			request.setDateRangeBegin(fromDate);
			log.info(" ### (getInterestWrkList) FROM DATE ### " +fromDate);
			
			XMLGregorianCalendar toDate = PortalUtils.getSaveXMLGregorianCalendarToDate(interestBean.getDateRangeEnd(), TimeZone.getDefault());
			request.setDateRangeEnd(toDate);
			log.info(" ### (getInterestWrkList) TO DATE ### " +toDate);
			
			request.setCallerId(this.basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			log.info(" ### (getInterestWrkList) CallerId ###"+ request.getCallerId());
			
			request.setStart(0);
			request.setLength(Integer.MAX_VALUE);
			
			log.info(" ### (getInterestWrkList) BEFORE SERVICE ###" );
			final FindInterestWrkResponse response = this.basePage.getInterestWrkClient().find(request);
			log.info(" ### (getInterestWrkList) RESPONSE CODE ### "+response.getStatus().getCode());
			
			if (this.basePage.evaluateBankPortalMobiliserResponse(response)) {
				log.info(" ### (getInterestWrkList) RESPONSE SIZE ### " +response.getItem().size());
				interestList =  ConverterUtils.convertToManageInterestWrkBean(response.getItem());
			} else {
				handleSpecificErrorMessage(response.getStatus().getCode());
			}		
			
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			log.error("Exception occured while fetching Interest List  ===> ", e);
		}
		
		interestBean.setManageInterestList(interestList);
	}
	
	
	private void handleInterestDetailClick(final ManageInterestApproveBean interestBean){
		
		final GetInterestWrkRequest request;
		
		try {
			
			log.info(" ### (ManageInterestApprovePanel) handleInterestDetailClick ### ");
			request = this.basePage.getNewMobiliserRequest(GetInterestWrkRequest.class);
			log.info(" ### (ManageInterestApprovePanel) WF ID ### "+interestBean.getWorkFlowId());
			request.setWorkflowId(interestBean.getWorkFlowId());
			log.info(" ### (ManageInterestApprovePanel) CALLER ID ### "+basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			request.setCallerId(basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			
			final GetInterestWrkResponse response = this.basePage.getInterestWrkClient().get(request);
			log.info(" ### (ManageInterestApprovePanel) RESPONSE CODE ### "+response.getStatus().getCode()); 
			if (this.basePage.evaluateBankPortalMobiliserResponse(response)){
				final Component component = null;
				ManageInterestApproveBean bean = new ManageInterestApproveBean();
			
				bean.setId(interestBean.getId());
				bean.setWorkFlowId(interestBean.getWorkFlowId());
				bean.setCustomerIdentifier(response.getCustomerIdentifier());
				bean.setCustomerIdentifierType(response.getCustomerIdentifierType());
				
				log.info(" ### (ManageInterestApprovePanel) handleInterestDetailClick CUST TYPE ### " + response.getCustomerTypeId());
				
				// CUST TYPEID
				if (response.getCustomerTypeId() != null) {
						
					bean.setCustomerTypeId(new CodeValue(response.getCustomerTypeId().toString(), 
							BtpnUtils.getDropdownValueFromId(basePage.getLookupMapUtility().getLookupNamesMap(
									BtpnConstants.RESOURCE_USE_CASE_FEE_CUSTOMER_TYPE, component), 
									BtpnConstants.RESOURCE_USE_CASE_FEE_CUSTOMER_TYPE + "." +response.getCustomerTypeId().toString())
							));
				}
			
				// PI Id & PI TypeId 
				log.info(" ### (ManageInterestApprovePanel) handleInterestDetailClick PI ### " + response.getPaymentInstrumentId());
				bean.setPaymentInstrumentId(response.getPaymentInstrumentId());
				
				if(response.getPaymentInstrumentTypeId() != null) {
					bean.setPaymentInstrumentTypeId(new CodeValue(response.getPaymentInstrumentTypeId().toString(), 
							BtpnUtils.getDropdownValueFromId(basePage.getLookupMapUtility().getLookupNamesMap(
									BtpnConstants.RESOURCE_BUNDLE_PI_TYPE, component), 
									BtpnConstants.RESOURCE_BUNDLE_PI_TYPE + "." +response.getPaymentInstrumentTypeId().toString())
							));
				}
				
				//ACC GL CODE & EXP GL CODE
				log.info(" ### (ManageInterestApprovePanel) handleInterestDetailClick GL CODE ### " +(String.valueOf(response.getAccrueGLCode())));
				bean.setAccrueGLCode(new CodeValue(String.valueOf(response.getAccrueGLCode()),
						BtpnUtils.getDropdownValueFromId(basePage.getLookupMapUtility().getLookupNamesMap(
								BtpnConstants.RESOURCE_BILL_PAYMENT_FEE_GL_CODES, component), 
								BtpnConstants.RESOURCE_BILL_PAYMENT_FEE_GL_CODES + "." +String.valueOf(response.getAccrueGLCode()))));
				
				bean.setExpenseGLCode(new CodeValue(String.valueOf(response.getExpenseGLCode()),
						BtpnUtils.getDropdownValueFromId(basePage.getLookupMapUtility().getLookupNamesMap(
								BtpnConstants.RESOURCE_BILL_PAYMENT_FEE_GL_CODES, component), 
								BtpnConstants.RESOURCE_BILL_PAYMENT_FEE_GL_CODES + "." +String.valueOf(response.getExpenseGLCode()))));
			
				
				bean.setValidFrom(PortalUtils.getSaveDate(response.getValidFrom()));
				bean.setFixedFee(response.getFixedFee());
				bean.setMaximumFee(response.getMaximumFee());
				bean.setMinimumFee(response.getMinimumFee());
				bean.setPercentageFee(new BigDecimal(response.getPercentageFee()).movePointLeft(2).setScale(2, RoundingMode.DOWN).toString());
				bean.setThresholdAmount(response.getThresholdAmount());
				bean.setDescription(response.getDescription());
				
				log.info(" ### (ManageInterestApprovePanel) handleInterestDetailClick call ManageInterestDetailApprovePage ### " );
				setResponsePage(new ManageInterestDetailApprovePage(bean));
				
			}else{
				handleSpecificErrorMessage(response.getStatus().getCode());
			}
			
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			log.error("Exception occured while Get Interest Approve Details  ===> ", e);
		}
	}
	
	
	/**
	 * This method handles the specific error message.
	 */
	private String handleSpecificErrorMessage(final int errorCode) {
		// Specific error message handling
		final String messageKey = "error.interest" + errorCode;
		String message = getLocalizer().getString(messageKey, this);
		// Generic error messages
		if (messageKey.equals(message)) {
			message = getLocalizer().getString("interest.error", this);
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
