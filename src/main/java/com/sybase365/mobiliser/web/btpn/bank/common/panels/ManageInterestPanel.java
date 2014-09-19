package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
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
import org.springframework.util.StringUtils;

import com.btpnwow.core.interest.facade.contract.FindInterestRequest;
import com.btpnwow.core.interest.facade.contract.FindInterestResponse;
import com.btpnwow.core.interest.facade.contract.GetInterestRequest;
import com.btpnwow.core.interest.facade.contract.GetInterestResponse;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageInterestBean;
import com.sybase365.mobiliser.web.btpn.bank.common.dataproviders.ManageInterestDataProvider;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageinterest.ManageInterestAddPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageinterest.ManageInterestDetailPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.components.BtpnCustomPagingNavigator;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnOrderByOrder;
import com.sybase365.mobiliser.web.btpn.util.BtpnUtils;
import com.sybase365.mobiliser.web.btpn.util.ConverterUtils;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the Manage Interest page for bank portals.
 * 
 * @author Feny Yanti
 */
public class ManageInterestPanel extends Panel { 

	private static final long serialVersionUID = 1L;
	
	private static final Logger log = LoggerFactory.getLogger(ManageInterestPanel.class);

	private static final String WICKET_ID_PAGEABLE = "pageable";
	private static final String WICKET_ID_LINK = "idLink";
	private static final String WICKET_ID_INTERESTNAVIGATOR = "interestNavigator";
	private static final String WICKET_ID_INTERESTTOTALITEMS = "interestHeader";

	
	private String interestTotalItemString;
	private int interestStartIndex = 0;
	private int interestEndIndex = 0;
	
	protected BtpnMobiliserBasePage basePage;
	private FeedbackPanel feedBackPanel;
	private ManageInterestBean interestBean;
	WebMarkupContainer interestContainer;
	
	private ManageInterestDataProvider interestDataProvider;

	private Component descComp;

	/**
	 * Constructor for this page.
	 */
	public ManageInterestPanel(String id, BtpnBaseBankPortalSelfCarePage basePage) {
		super(id);
		this.basePage = basePage;
		constructPanel();
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	private void constructPanel() {
		
		Form<ManageInterestPanel> form = new Form<ManageInterestPanel>("interestForm",
			new CompoundPropertyModel<ManageInterestPanel>(this));
		
		// Add feedback panel for Error Messages
		feedBackPanel = new FeedbackPanel("errorMessages");
		feedBackPanel.setOutputMarkupId(true);
		feedBackPanel.setOutputMarkupPlaceholderTag(true);
		form.add(feedBackPanel);
		
		form.add(descComp = new TextField<String>("interestBean.description").add(new ErrorIndicator()));
		descComp.setOutputMarkupId(true);
		
		// Add the Manage Interest container
		interestContainer = new WebMarkupContainer("interestContainer");
		manageInterestDataView(interestContainer);
		interestContainer.setOutputMarkupId(true);
		interestContainer.setOutputMarkupPlaceholderTag(true);
		interestContainer.setVisible(false);
		form.add(interestContainer);
		
		/**
		 * This method adds the Add button for the Manage Interest
		 */
		Button addButton = new Button("addBtn") {
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit() {
				if (!PortalUtils.exists(interestBean)){
					interestBean = new ManageInterestBean(); 
				}
				setResponsePage(ManageInterestAddPage.class);
			}
		};
		form.add(addButton);
		
		form.add(new AjaxButton("searchBtn"){
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form){
				if (!PortalUtils.exists(interestBean)){
					interestBean = new ManageInterestBean();
				}
				
				interestBean.setManageInterestList(new ArrayList<ManageInterestBean>());
				log.info(" ### KLICK SEARCH INTEREST DESC ### "+interestBean.getDescription());
				handleSearchInterest(target);
			};
			
			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.addComponent(feedBackPanel);
				target.addComponent(descComp);
				target.addComponent(interestContainer);
				super.onError(target, form);
			}
			
		});
		// Add add Button
		add(form);
	}
	
	private void handleSearchInterest(AjaxRequestTarget target) {
		getInterestList();
		interestContainer.setVisible(true);
		target.addComponent(interestContainer);
		target.addComponent(feedBackPanel);
		target.addComponent(descComp);
	}
	

	/**
	 * This method adds the AttachmentsDataView for the file upload and also adds the sorting logic for data view
	 */
	protected void manageInterestDataView(final WebMarkupContainer dataViewContainer) {

		interestDataProvider = new ManageInterestDataProvider("id");

		final DataView<ManageInterestBean> dataView = new DataView<ManageInterestBean>(WICKET_ID_PAGEABLE, interestDataProvider){

			private static final long serialVersionUID = 1L;
			
			
			protected void onBeforeRender(){
				final ManageInterestDataProvider dataProvider = (ManageInterestDataProvider) internalGetDataProvider();
				dataProvider.setManageInterestList(interestBean.getManageInterestList());
				refreshTotalItemCount();
				super.onBeforeRender();
			}
			
			@Override
			protected void populateItem(final Item<ManageInterestBean> item) {
				final ManageInterestBean entry = item.getModelObject();
				
				final AjaxLink<ManageInterestBean> interestLink = new AjaxLink<ManageInterestBean>(WICKET_ID_LINK, item.getModel()) {
					private static final long serialVersionUID = 1L;
					@Override
					public void onClick(AjaxRequestTarget target) {
						handleInterestDetailClick((ManageInterestBean)item.getModelObject());
					}
				};
				
				interestLink.add(new Label("id", entry.getId().toString()));
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
				final ManageInterestDataProvider dataProvider = (ManageInterestDataProvider) internalGetDataProvider();
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
				final String displayTotalItemsText = ManageInterestPanel.this.getLocalizer().getString(
						"interest.totalitems.header", ManageInterestPanel.this);
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
		dataViewContainer.add(new BtpnOrderByOrder("orderById", "id", interestDataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByDesc", "description", interestDataProvider, dataView));
		dataViewContainer.addOrReplace(dataView);
	
	}
	

	private void getInterestList(){
		
		List<ManageInterestBean> interestList = new ArrayList<ManageInterestBean>();
		final FindInterestRequest request; 
		
		try {
			
			request = basePage.getNewMobiliserRequest(FindInterestRequest.class);
			if (StringUtils.hasText(interestBean.getDescription()))
				request.setDescription(interestBean.getDescription());
			else
				request.setDescription(null);
			
			request.setStart(0);
			request.setLength(Integer.MAX_VALUE);
				
			final FindInterestResponse response = this.basePage.getInterestClient().find(request);
			log.info(" ### (getInterestList) RESPONSE CODE ### "+response.getStatus().getCode());
			if (this.basePage.evaluateBankPortalMobiliserResponse(response)) {
				log.info(" ### (getInterestList) RESPONSE SIZE ### " +response.getItem().size());
				interestList =  ConverterUtils.convertToManageInterestBean(response.getItem());
			} else {
				handleSpecificErrorMessage(response.getStatus().getCode());
			}		
			
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			log.error("Exception occured while fetching Interest Details  ===> ", e);
		}
		
		interestBean.setManageInterestList(interestList);
	}
	
	
	private void handleInterestDetailClick(final ManageInterestBean interestBean){
		
		final GetInterestRequest request;
		
		try {
			request = this.basePage.getNewMobiliserRequest(GetInterestRequest.class);
			request.setId(interestBean.getId());
		
			final GetInterestResponse response = this.basePage.getInterestClient().get(request);
			log.info(" ### (ManageInterestPanel) handleInterestDetailClick RESPONSE ### " +response.getStatus().getCode());
			
			if (this.basePage.evaluateBankPortalMobiliserResponse(response)){
				final Component component = null;
				ManageInterestBean bean = new ManageInterestBean();
				log.info(" ### (ManageInterestPanel) handleInterestDetailClick AFTER SERVICE ID ### "+interestBean.getId());
				bean.setId(interestBean.getId());
				
				//CUST IDENTIFIER Type & CUST IDENTIFIERID
				bean.setCustomerIdentifierType(response.getCustomerIdentifierType());
				bean.setCustomerIdentifier(response.getCustomerIdentifier());
				
				//CUST TYPEID
				if (response.getCustomerTypeId() == null) {
					bean.setCustomerTypeId(new CodeValue("-","-"));
				} else {
				
					
					bean.setCustomerTypeId(new CodeValue(response.getCustomerTypeId().toString(), 
							BtpnUtils.getDropdownValueFromId(basePage.getLookupMapUtility().getLookupNamesMap(
									BtpnConstants.RESOURCE_USE_CASE_FEE_CUSTOMER_TYPE, component), 
									BtpnConstants.RESOURCE_USE_CASE_FEE_CUSTOMER_TYPE + "." +response.getCustomerTypeId().toString())
							));
				}
				
				
				// PI */
				bean.setPaymentInstrumentId(response.getPaymentInstrumentId());
				
				if(response.getPaymentInstrumentTypeId() == null) {
					bean.setPaymentInstrumentTypeId(new CodeValue("-","-"));
				}
				else {
					log.info(" ### (ManageInterestApprovePanel) handleInterestDetailClick PI TYPE ### " +(response.getPaymentInstrumentTypeId().toString()));
					bean.setPaymentInstrumentTypeId(new CodeValue(response.getPaymentInstrumentTypeId().toString(), 
							BtpnUtils.getDropdownValueFromId(basePage.getLookupMapUtility().getLookupNamesMap(
									BtpnConstants.RESOURCE_BUNDLE_PI_TYPE, component), 
									BtpnConstants.RESOURCE_BUNDLE_PI_TYPE + "." +response.getPaymentInstrumentTypeId().toString())
							));
				}
				
				//ACC GL CODE & EXP GL CODE*/
				log.info(" ### (ManageInterestPanel) handleInterestDetailClick GL CODE ### " +(String.valueOf(response.getAccrueGLCode())));
				bean.setAccrueGLCode(new CodeValue(String.valueOf(response.getAccrueGLCode()),
						BtpnUtils.getDropdownValueFromId(basePage.getLookupMapUtility().getLookupNamesMap(
								BtpnConstants.RESOURCE_BILL_PAYMENT_FEE_GL_CODES, component), 
								BtpnConstants.RESOURCE_BILL_PAYMENT_FEE_GL_CODES + "." +String.valueOf(response.getAccrueGLCode()))));
				bean.setExpenseGLCode(new CodeValue(String.valueOf(response.getExpenseGLCode()),
						BtpnUtils.getDropdownValueFromId(basePage.getLookupMapUtility().getLookupNamesMap(
								BtpnConstants.RESOURCE_BILL_PAYMENT_FEE_GL_CODES, component), 
								BtpnConstants.RESOURCE_BILL_PAYMENT_FEE_GL_CODES + "." +String.valueOf(response.getExpenseGLCode()))));
			
				bean.setValidFrom(PortalUtils.getSaveDate(response.getValidFrom()));
				bean.setFixedFee(response.getFixedFee()/100);
				bean.setMinimumFee(response.getMinimumFee());
				bean.setPercentageFee(new BigDecimal(response.getPercentageFee()).movePointLeft(2).setScale(2, RoundingMode.DOWN).toString());
				bean.setThresholdAmount(response.getThresholdAmount()/100);
				bean.setDescription(response.getDescription());
				
				setResponsePage(new ManageInterestDetailPage(bean));
				
			}else{
				handleSpecificErrorMessage(response.getStatus().getCode());
			}
			
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			log.error("Exception occured while Get Detail Interest Details  ===> ", e);
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
	
}
