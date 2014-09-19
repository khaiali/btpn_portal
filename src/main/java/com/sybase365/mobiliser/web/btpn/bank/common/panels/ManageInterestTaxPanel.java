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

import com.btpnwow.core.interest.facade.contract.FindInterestTaxRequest;
import com.btpnwow.core.interest.facade.contract.FindInterestTaxResponse;
import com.btpnwow.core.interest.facade.contract.GetInterestTaxRequest;
import com.btpnwow.core.interest.facade.contract.GetInterestTaxResponse;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageInterestTaxBean;
import com.sybase365.mobiliser.web.btpn.bank.common.dataproviders.ManageInterestTaxDataProvider;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageinteresttax.ManageInterestTaxAddPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageinteresttax.ManageInterestTaxDetailPage;
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
public class ManageInterestTaxPanel extends Panel { 

	private static final long serialVersionUID = 1L;
	
	private static final Logger log = LoggerFactory.getLogger(ManageInterestTaxPanel.class);

	private static final String WICKET_ID_PAGEABLE = "pageable";
	private static final String WICKET_ID_LINK = "idLink";
	private static final String WICKET_ID_INTERESTNAVIGATOR = "interestNavigator";
	private static final String WICKET_ID_INTERESTTOTALITEMS = "interestHeader";


	private String interestTotalItemString;
	private int interestStartIndex = 0;
	private int interestEndIndex = 0;
	
	protected BtpnMobiliserBasePage basePage;
	private FeedbackPanel feedBackPanel;
	private ManageInterestTaxBean interestTaxBean;
	WebMarkupContainer interestTaxContainer;
	
	private ManageInterestTaxDataProvider interestTaxDataProvider;

	private Component descComp;

	/**
	 * Constructor for this page.
	 */
	public ManageInterestTaxPanel(String id, BtpnBaseBankPortalSelfCarePage basePage) {
		super(id);
		this.basePage = basePage;
		constructPanel();
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	private void constructPanel() {
		
		Form<ManageInterestTaxPanel> form = new Form<ManageInterestTaxPanel>("interestTaxForm",
			new CompoundPropertyModel<ManageInterestTaxPanel>(this));
		
		// Add feedback panel for Error Messages
		feedBackPanel = new FeedbackPanel("errorMessages");
		feedBackPanel.setOutputMarkupId(true);
		feedBackPanel.setOutputMarkupPlaceholderTag(true);
		form.add(feedBackPanel);
		
		form.add(descComp = new TextField<String>("interestTaxBean.description").add(new ErrorIndicator()));
		descComp.setOutputMarkupId(true);
		
		// Add the Manage Interest container
		interestTaxContainer = new WebMarkupContainer("interestTaxContainer");
		manageInterestDataView(interestTaxContainer);
		interestTaxContainer.setOutputMarkupId(true);
		interestTaxContainer.setOutputMarkupPlaceholderTag(true);
		interestTaxContainer.setVisible(false);
		form.add(interestTaxContainer);

		/**
		 * This method adds the Add button for the Manage Interest
		 */
		Button addButton = new Button("addBtn") {
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit() {
				if (!PortalUtils.exists(interestTaxBean)){
					interestTaxBean = new ManageInterestTaxBean(); 
				}
				setResponsePage(ManageInterestTaxAddPage.class);
			}
		};
		form.add(addButton);
		
		form.add(new AjaxButton("searchBtn"){
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form){
				if (!PortalUtils.exists(interestTaxBean)){
					interestTaxBean = new ManageInterestTaxBean();
				}
				interestTaxBean.setManageInterestTaxList(new ArrayList<ManageInterestTaxBean>());
				log.info(" ### KLICK SEARCH ID ### "+interestTaxBean.getId());
				log.info(" ### KLICK SEARCH INTEREST DESC ### "+interestTaxBean.getDescription());
				handleSearchInterest(target);
			};
			
			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.addComponent(feedBackPanel);
				target.addComponent(descComp);
				target.addComponent(interestTaxContainer);
				super.onError(target, form);
			}
			
		});
		// Add add Button
		add(form);
	}
	
	private void handleSearchInterest(AjaxRequestTarget target) {
		getInterestList();
		interestTaxContainer.setVisible(true);
		target.addComponent(interestTaxContainer);
		target.addComponent(feedBackPanel);
		target.addComponent(descComp);
	}
	

	/**
	 * This method adds the AttachmentsDataView for the file upload and also adds the sorting logic for data view
	 */
	protected void manageInterestDataView(final WebMarkupContainer dataViewContainer) {

		interestTaxDataProvider = new ManageInterestTaxDataProvider("id");

		final DataView<ManageInterestTaxBean> dataView = new DataView<ManageInterestTaxBean>(WICKET_ID_PAGEABLE, interestTaxDataProvider){

			private static final long serialVersionUID = 1L;
			
			
			protected void onBeforeRender(){
				final ManageInterestTaxDataProvider dataProvider = (ManageInterestTaxDataProvider) internalGetDataProvider();
				dataProvider.setManageInterestList(interestTaxBean.getManageInterestTaxList());
				refreshTotalItemCount();
				super.onBeforeRender();
			}
			
			@Override
			protected void populateItem(final Item<ManageInterestTaxBean> item) {
				final ManageInterestTaxBean entry = item.getModelObject();
				
				final AjaxLink<ManageInterestTaxBean> feeLink = new AjaxLink<ManageInterestTaxBean>(WICKET_ID_LINK, item.getModel()) {
					private static final long serialVersionUID = 1L;
					@Override
					public void onClick(AjaxRequestTarget target) {
						handleInterestDetailClick((ManageInterestTaxBean)item.getModelObject());
					}
				};
				
				feeLink.add(new Label("id", entry.getId().toString()));
				item.add(feeLink);
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
				final ManageInterestTaxDataProvider dataProvider = (ManageInterestTaxDataProvider) internalGetDataProvider();
				dataProvider.setManageInterestList(interestTaxBean.getManageInterestTaxList());
				return interestTaxBean.getManageInterestTaxList().size() != 0;
			}
		};
	
		// Add The navigation
		final BtpnCustomPagingNavigator navigator = new BtpnCustomPagingNavigator(WICKET_ID_INTERESTNAVIGATOR, dataView){
			private static final long serialVersionUID = 1L;
			@Override
			public boolean isVisible(){
				return interestTaxBean != null && interestTaxBean.getManageInterestTaxList().size() != 0;
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
				final String displayTotalItemsText = ManageInterestTaxPanel.this.getLocalizer().getString(
						"interest.totalitems.header", ManageInterestTaxPanel.this);
				return String.format(displayTotalItemsText, interestTotalItemString, interestStartIndex, interestEndIndex);
			}
		};
	
		final Label interestHeader = new Label(WICKET_ID_INTERESTTOTALITEMS, headerDisplayModel) {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean isVisible() {
				return interestTaxBean != null && interestTaxBean.getManageInterestTaxList().size() != 0;
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
				return interestTaxBean != null && interestTaxBean.getManageInterestTaxList().size() == 0;
			}
		}.setRenderBodyOnly(true).setOutputMarkupPlaceholderTag(true));
			
		// Add the sort providers
		dataViewContainer.add(new BtpnOrderByOrder("orderById", "id", interestTaxDataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByDesc", "description", interestTaxDataProvider, dataView));
		dataViewContainer.addOrReplace(dataView);
	
	}
	

	private void getInterestList(){
		
		List<ManageInterestTaxBean> interestTaxList = new ArrayList<ManageInterestTaxBean>();
		final FindInterestTaxRequest request; 
		
		try {
			log.info(" ### (getInterestTaxList) start ### ");
			request = basePage.getNewMobiliserRequest(FindInterestTaxRequest.class);
			if (StringUtils.hasText(interestTaxBean.getDescription()))
				request.setDescription(interestTaxBean.getDescription());
			else
				request.setDescription(null);
			
			request.setStart(0);
			request.setLength(Integer.MAX_VALUE);
			log.info(" ### (getInterestTaxList) description ### "+request.getDescription());
			
			final FindInterestTaxResponse response = this.basePage.getInterestTaxClient().find(request);
			log.info(" ### (getInterestList) RESPONSE CODE ### "+response.getStatus().getCode());
			if (this.basePage.evaluateBankPortalMobiliserResponse(response)) {
				log.info(" ### (getInterestList) RESPONSE SIZE ### " +response.getItem().size());
				interestTaxList =  ConverterUtils.convertToManageInterestTaxBean(response.getItem());
			} else {
				handleSpecificErrorMessage(response.getStatus().getCode());
			}		
			
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			log.error("Exception occured while fetching Interest Tax Details  ===> ", e);
		}
		
		interestTaxBean.setManageInterestTaxList(interestTaxList);
	}
	
	

	private void handleInterestDetailClick(final ManageInterestTaxBean interestTaxBean){
		
		final GetInterestTaxRequest request;
		
		try {
			
			log.info(" ### (ManageInterestTaxPanel) handleInterestTaxDetailClick ### "); 
			request = this.basePage.getNewMobiliserRequest(GetInterestTaxRequest.class);
			log.info(" ### (ManageInterestTaxPanel) handleInterestTaxDetailClick ID ### " +interestTaxBean.getId());
			request.setId(interestTaxBean.getId());
			final GetInterestTaxResponse response = this.basePage.getInterestTaxClient().get(request);
			log.info(" ### (ManageInterestTaxPanel) handleInterestTaxDetailClick RESPONSE ### " +response.getStatus().getCode());
			
			if (this.basePage.evaluateBankPortalMobiliserResponse(response)){
				final Component component = null;
				ManageInterestTaxBean bean = new ManageInterestTaxBean();
				log.info(" ### (ManageInterestTaxPanel) handleInterestTaxDetailClick AFTER SERVICE ID ### "+interestTaxBean.getId());
				bean.setId(interestTaxBean.getId());
				
				//CUST IDENTIFIER Type & CUST IDENTIFIERID
				bean.setCustomerIdentifierType(response.getCustomerIdentifierType());
				bean.setCustomerIdentifier(response.getCustomerIdentifier());
				
				//CUST TYPEID
				log.info(" ### (ManageInterestTaxPanel) handleInterestTaxDetailClick CUST TYPE ### " + response.getCustomerTypeId());
				if (response.getCustomerTypeId() != null) {
					bean.setCustomerTypeId(new CodeValue(response.getCustomerTypeId().toString(), 
							BtpnUtils.getDropdownValueFromId(basePage.getLookupMapUtility().getLookupNamesMap(
									BtpnConstants.RESOURCE_USE_CASE_FEE_CUSTOMER_TYPE, component), 
									BtpnConstants.RESOURCE_USE_CASE_FEE_CUSTOMER_TYPE + "." +response.getCustomerTypeId().toString())
							));
				}
			
				// PI */
				bean.setPaymentInstrumentId(response.getPaymentInstrumentId());
				
				if(response.getPaymentInstrumentTypeId() != null) {
					bean.setPaymentInstrumentTypeId(new CodeValue(response.getPaymentInstrumentTypeId().toString(), 
							BtpnUtils.getDropdownValueFromId(basePage.getLookupMapUtility().getLookupNamesMap(
									BtpnConstants.RESOURCE_BUNDLE_PI_TYPE, component), 
									BtpnConstants.RESOURCE_BUNDLE_PI_TYPE + "." +response.getPaymentInstrumentTypeId().toString())
							));
				}
				
				
				//ACC GL CODE & EXP GL CODE*/
				log.info(" ### (ManageInterestTaxPanel) handleInterestTaxDetailClick GL CODE ### " +(String.valueOf(response.getAccrueGLCode())));
				bean.setAccrueGLCode(new CodeValue(String.valueOf(response.getAccrueGLCode()),
						BtpnUtils.getDropdownValueFromId(basePage.getLookupMapUtility().getLookupNamesMap(
								BtpnConstants.RESOURCE_BILL_PAYMENT_FEE_GL_CODES, component), 
								BtpnConstants.RESOURCE_BILL_PAYMENT_FEE_GL_CODES + "." +String.valueOf(response.getAccrueGLCode()))));
				bean.setTaxGLCode(new CodeValue(String.valueOf(response.getTaxGLCode()),
						BtpnUtils.getDropdownValueFromId(basePage.getLookupMapUtility().getLookupNamesMap(
								BtpnConstants.RESOURCE_BILL_PAYMENT_FEE_GL_CODES, component), 
								BtpnConstants.RESOURCE_BILL_PAYMENT_FEE_GL_CODES + "." +String.valueOf(response.getTaxGLCode()))));
			
				bean.setValidFrom(PortalUtils.getSaveDate(response.getValidFrom()));
				bean.setFixedFee(response.getFixedFee()/100);
				bean.setMinimumFee(response.getMinimumFee());
				bean.setPercentageFee(new BigDecimal(response.getPercentageFee()).movePointLeft(2).setScale(2, RoundingMode.DOWN).toString());
				bean.setThresholdAmount(response.getThresholdAmount()/100);
				bean.setDescription(response.getDescription());
				
				setResponsePage(new ManageInterestTaxDetailPage(bean));
				
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
