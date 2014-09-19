package com.sybase365.mobiliser.web.btpn.consumer.pages.portal.airtimetopup;

import id.co.btpn.corp.dev.appmdwdev02.com_btpn_biller_ws_provider.btpnbillerwstopup.CommonParam2;
import id.co.btpn.corp.dev.appmdwdev02.com_btpn_biller_ws_provider.btpnbillerwstopup.DebitAccount;
import id.co.btpn.corp.dev.appmdwdev02.com_btpn_biller_ws_provider.btpnbillerwstopup.InqDebitTopup;
import id.co.btpn.corp.dev.appmdwdev02.com_btpn_biller_ws_provider.btpnbillerwstopup.InqDebitTopupResponse;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceOperations;
import org.springframework.ws.soap.SoapMessage;

import com.btpnwow.portal.common.util.BillerProductLookup;
import com.btpnwow.portal.common.util.BillerProductLookup.BillerProduct;
import com.btpnwow.portal.common.util.MobiliserUtils;
import com.sybase365.mobiliser.money.services.api.ISystemEndpoint;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.application.pages.ConsumerPortalApplicationLoginPage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.common.components.FavouriteDropdownChoice;
import com.sybase365.mobiliser.web.btpn.consumer.beans.AirtimePerformBean;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;


public class AirtimeTopupPage extends BtpnBaseConsumerPortalSelfCarePage {

	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(AirtimeTopupPage.class);
	
	private static final int N = 7;
	
	private AirtimePerformBean airtimeBean;
	
	@SpringBean(name="billerProductLookup")
	private BillerProductLookup billerProductLookup;
	
	@SpringBean(name = "systemAuthSystemClient")
	private ISystemEndpoint systemEndpoint;

	private String[] labels;
	private CodeValue[] opts;
	private WebMarkupContainer[] containers;
	private DropDownChoice<CodeValue>[] choices;
	
	@SpringBean(name="wsTopupTemplate")
	private WebServiceOperations wsTopupTemplate;
	
	private WebMarkupContainer favouriteContainer;
	private WebMarkupContainer manualContainer;
	

	/**
	 * Constructor for the Home Page
	 */
	public AirtimeTopupPage() {
		super();
	}
	
	
	protected void initOwnPageComponents(){
		
		super.initOwnPageComponents();
		
		this.labels = new String[N];
		this.opts = new CodeValue[N];
		
		this.containers = new WebMarkupContainer[N];
		this.choices = new DropDownChoice[N];
		
		this.airtimeBean = new AirtimePerformBean();
		
		final Form<AirtimeTopupPage> form = new Form<AirtimeTopupPage>("airtimeTopupForm",
				new CompoundPropertyModel<AirtimeTopupPage>(this));
			
		form.add(new FeedbackPanel("errorMessages"));
		
		final IChoiceRenderer<CodeValue> choiceRenderer = new ChoiceRenderer<CodeValue>(
				BtpnConstants.DISPLAY_EXPRESSION, BtpnConstants.ID_EXPRESSION);
			
		String rootId;
		
		if("IN".equalsIgnoreCase(getLocale().getLanguage())){
			rootId = "in.201";
		} else {
			rootId = "en.251";
		}
		
		for (int i=0; i<N; i++){
			constructOneLevel(form, i, choiceRenderer, rootId);
		}
		
		/* Add the radio button for manual or favourite */
		airtimeBean.setManualOrFavourite(BtpnConstants.BILLPAYMENT_MANUALLY);
		
		form.add(new RadioGroup<String>("airtimeBean.manualOrFavourite")
				.add(new Radio<String>("radio.manually", Model.of(BtpnConstants.BILLPAYMENT_MANUALLY)))
				.add(new Radio<String>("radio.favourite", Model.of(BtpnConstants.BILLPAYMENT_FAVLIST)))
				.add(new FavouriteViewChoiceComponentUpdatingBehavior()));
		
		// Add the favourite container.
//		favouriteContainer = new WebMarkupContainer("favouriteContainer");
//		favouriteContainer.add(new FavouriteDropdownChoice("airtimeBean.selectedMsisdn", false, true,
//				BtpnConstants.USECASE_AIR_TIME_TOPUP, this.getMobiliserWebSession().getBtpnLoggedInCustomer()
//						.getCustomerId()).setNullValid(false).setRequired(true).add(new ErrorIndicator()));
		
		favouriteContainer = new WebMarkupContainer("favouriteContainer");
		favouriteContainer.add(new FavouriteDropdownChoice("airtimeBean.selectedMsisdn", false, true,
				this.getMobiliserWebSession().getBtpnLoggedInCustomer()
						.getCustomerId(), 4).setNullValid(false).setRequired(true).add(new ErrorIndicator()));
		
		favouriteContainer.setOutputMarkupPlaceholderTag(true);
		favouriteContainer.setVisible(false);
		favouriteContainer.setOutputMarkupId(true);
		form.add(favouriteContainer);
		
		// Add the manual container.
		manualContainer = new WebMarkupContainer("manualContainer");
		manualContainer.add(new RequiredTextField<String>("airtimeBean.selectedMsisdn.id").add(new ErrorIndicator()));
		manualContainer.setOutputMarkupPlaceholderTag(true);
		manualContainer.setOutputMarkupId(true);
		form.add(manualContainer);

		// Submit button for form.
		addSubmitButton(form);
		
		add(form);

	}
	
	
	/**
	 * This is used to add submit button.
	 */
	private void addSubmitButton(final Form<AirtimeTopupPage> form) {
		
		form.add(new Button("btnSubmit") {
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit() {
				String rootId = null;
				String finalId = null;
				
				for (int i = 0; i < N; ++i) {
					if (opts[i] != null) {
						List<CodeValue> children = billerProductLookup.getChildrenAsCodeValue(opts[i].getId());
					
						if ((children == null) || children.isEmpty()) {
							finalId = opts[i].getId();
						}
					}
				}
				
				if (finalId == null) {
					error(AirtimeTopupPage.this.getLocalizer().getString("product.required", AirtimeTopupPage.this));
				} 
				else if (!performTopUpValidations()) {
					return;
				}
				else {
					BillerProduct billerProduct = billerProductLookup.get(finalId);
					airtimeBean.setBillerId(billerProduct.getBillerId());
					airtimeBean.setProductId(billerProduct.getProductId());
					airtimeBean.setLabel(billerProduct.getLabel());
					airtimeBean.setBillerDescription(billerProduct.getStrBiller());
					
					handlePerformTopUp();
				}
			}
		}.setDefaultFormProcessing(true));
	}
	
	
	/**
	 * This is class for displaying favourite list or not based on selection.
	 */
	private class FavouriteViewChoiceComponentUpdatingBehavior extends AjaxFormChoiceComponentUpdatingBehavior {

		private static final long serialVersionUID = 1L;

		@Override
		protected void onUpdate(AjaxRequestTarget target) {
			final boolean isManual = airtimeBean.getManualOrFavourite().equals(BtpnConstants.BILLPAYMENT_MANUALLY);
			manualContainer.setVisible(isManual);
			favouriteContainer.setVisible(!isManual);
			target.addComponent(favouriteContainer);
			target.addComponent(manualContainer);
		}
	}
	
	
	/**
	 * This method is used for performing bill payment validations
	 */
	public boolean performTopUpValidations() {
		
//		final String errorKey = isTelePhonaBillPay ? "phonenumber.Required" : "Required";
//		final Component comp = manualContainer.isVisible() ? billNumberField : favouriteField;
//		billPayBean.setBillerLabel(errorKey);
//		if (!PortalUtils.exists(billPayBean.getSelectedBillerId())) {
//			comp.error(getLocalizer().getString("billPayBean.selectedBillerId." + errorKey, this));
//			return false;
//		}
//		if (!PortalUtils.exists(billPayBean.getSelectedBillerId().getId())) {
//			comp.error(getLocalizer().getString("billPayBean.selectedBillerId." + errorKey, this));
//			return false;
//		}
		return true;
	}
	
	
	private void constructOneLevel(Form<?> frm, final int i, IChoiceRenderer<CodeValue> renderer, String rootId) {
		
		String si = Integer.toString(i);
		
		WebMarkupContainer container = new WebMarkupContainer("containers.".concat(si));
		container.setOutputMarkupPlaceholderTag(true);
		container.setVisible(i == 0);
		
		Label label = new Label("labels.".concat(si));
		label.setOutputMarkupPlaceholderTag(true);
		
		DropDownChoice<CodeValue> choice = new DropDownChoice<CodeValue>("opts.".concat(si));
		
		if (i == 0) {
			choice.setChoices(billerProductLookup.getChildrenAsCodeValue(rootId));
			
			labels[i] = billerProductLookup.get(rootId).getPromptLabel();
		} else {
			choice.setChoices(Collections.<CodeValue> emptyList());
			
			labels[i] = ""; 
		}
		choice.setChoiceRenderer(renderer);
		choice.setOutputMarkupPlaceholderTag(true);
		
		choice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			
			private static final long serialVersionUID = 1L;

			protected void onUpdate(AjaxRequestTarget target) {
				if (i + 1 >= N) {
					return;
				}
				
				String rootId;
				
				BillerProduct root;
				List<CodeValue> children;
				
				if (opts[i] == null) {
					rootId = null;
			
					root = null;
					children = null;
				} else {
					rootId = opts[i].getId(); 
					root = billerProductLookup.get(rootId);
					children = billerProductLookup.getChildrenAsCodeValue(rootId);
				}
				
				if ((root == null) || (children == null) || children.isEmpty()) {
					opts[i + 1] = null;
					labels[i + 1] = "";
					
					choices[i + 1].setChoices(Collections.<CodeValue> emptyList());
					containers[i + 1].setVisible(false);
				} else {
					opts[i + 1] = null;
					labels[i + 1] = root.getPromptLabel();
					
					//temp selected Label
//					isTelePhonaBillPay = (root.getLabel().equals("Handphone") || root.getLabel().equals("Telepon"));
					
					choices[i + 1].setChoices(children);
					containers[i + 1].setVisible(true);
				}
				
				target.addComponent(choices[i + 1]);
				target.addComponent(containers[i + 1]);
			
				for (int j = i + 2; j < N; ++j) {
					opts[j] = null;
					labels[j] = "";
					
					choices[j].setChoices(Collections.<CodeValue> emptyList());
					containers[j].setVisible(false);
					
					target.addComponent(choices[j]);
					target.addComponent(containers[j]);
				}
			}
		});
		
		opts[i] = null;
		
		container.add(label);
		container.add(choice);
		
		containers[i] = container;
		choices[i] = choice;
		
		frm.add(container);
	}
	
	private void handlePerformTopUp(){
		
		try {
			
			String userName = getMobiliserWebSession().getBtpnLoggedInCustomer().getUsername();
			log.info("### (AirtimeTopupPage::handlePerformTopUp) USER NAME ### " +userName);
			
			/* Get Current Calendar */
			Calendar cal = Calendar.getInstance();
			XMLGregorianCalendar transDate = PortalUtils.getSaveXMLGregorianCalendar(cal);
			
			String refNo = MobiliserUtils.getExternalReferenceNo(systemEndpoint);
			log.info("### (AirtimeTopupPage::Inquiry) REF NO ### " +refNo);
			
			InqDebitTopup req = new InqDebitTopup();
			CommonParam2 param = new CommonParam2();
			DebitAccount debitAccount = new DebitAccount();
			
			param.setPan("1234511234511234");
			param.setProcessingCode("100501");
			param.setChannelId("6018");
			param.setChannelType("PB");
			param.setNode("WOW_CHANNEL");
			param.setTerminalId("WOW");
			param.setTerminalName("WOW");
			param.setCurrencyAmount("IDR");
			param.setAmount(airtimeBean.getLabel());
			param.setTransmissionDateTime(transDate.toXMLFormat());
			param.setRequestId(refNo);
			param.setAcqId("213");
			param.setReferenceNo(refNo);
//			param.setOriginal("");
			
			debitAccount.setFlags("0");
//			debitAccount.setNumber(airtimeBean.getSelectedMsisdn().getId());
			debitAccount.setNumber(userName);
			debitAccount.setType("MSISDN");
			
			req.setCommonParam(param);
			req.setDebitAccount(debitAccount);
			req.setAmountDenom(airtimeBean.getLabel());
			req.setInstitutionCode(airtimeBean.getBillerId());
			req.setProductID(airtimeBean.getProductId());
			req.setTopUpNumber(airtimeBean.getSelectedMsisdn().getId());
			
			req.setDesc1("Topup Pulsa");
			req.setDesc2(airtimeBean.getBillerDescription());
			
			InqDebitTopupResponse response = new InqDebitTopupResponse();
			
			response = (InqDebitTopupResponse) wsTopupTemplate.marshalSendAndReceive(req,
					new WebServiceMessageCallback() {
				public void doWithMessage(
						WebServiceMessage message) {
					((SoapMessage) message)
							.setSoapAction("com_btpn_biller_ws_provider_BtpnBillerWsTopup_Binder_inqDebitTopup");
				}
			});
			
			final int statusCode = Integer.valueOf(response.getResponseCode());
			log.info("### (AirtimeTopupPage::Inquiry) ### " +statusCode);
			if (evaluateMobResponse(response, ConsumerPortalApplicationLoginPage.class)){
				airtimeBean.setFeeCurrency(response.getFeeCurrency());
				airtimeBean.setFeeAmount(Long.parseLong(response.getFee()));
				setResponsePage(new AirtimeTopupConfirmPage(airtimeBean));
			} else {
				error(MobiliserUtils.errorMessage(response.getResponseCode(), response.getResponseDesc(), getLocalizer(), this));
			}
		
		} catch (Exception e) {
			log.error("#An error occurred while calling createPreFTtoOtherAcctRequest service", e);
			error(getLocalizer().getString("error.exception", this));
		}
		
	}
	
	
	@SuppressWarnings("unchecked")
	public boolean evaluateMobResponse(InqDebitTopupResponse response,
		Class<? extends Page> loginClass) {

		log.debug("# Response returned status: {}-{}", response.getResponseCode());

		if (Integer.parseInt(response.getResponseCode()) == 0 && Integer.parseInt(response.getResponseCode()) == 00) {
			return true;
		}

		// check for mobiliser session closed or expired
		if (Integer.parseInt(response.getResponseCode()) == 352 || Integer.parseInt(response.getResponseCode()) == 353) {
			log.debug("# Mobiliser session closed/expired, redirect to sign in page");
			// if mobiliser session gone, then can't continue with web session
			// so invalidate session and redirect to home, which will go to
			getMobiliserWebSession().invalidate();
			getRequestCycle().setRedirect(true);
			if (null != loginClass) {

				setResponsePage(getComponent(loginClass));

				// Set the mobiliser session expired and redirect it to login
				// page
				String errorMessage = null;

				errorMessage = getDisplayValue(String.valueOf(response.getResponseCode()),
					Constants.RESOURCE_BUNDLE_ERROR_CODES);

				if (PortalUtils.exists(errorMessage)) {
					getMobiliserWebSession().error(errorMessage);
				} else {
					getMobiliserWebSession().error(getLocalizer().getString("portal.genericError", this));
				}
			}
		}

		return false;
	}

}
