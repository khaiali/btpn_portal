package com.sybase365.mobiliser.web.btpn.consumer.pages.portal.airtimetopup;


import id.co.btpn.corp.dev.appmdwdev02.com_btpn_biller_ws_provider.btpnbillerwstopup.CommonParam2;
import id.co.btpn.corp.dev.appmdwdev02.com_btpn_biller_ws_provider.btpnbillerwstopup.TopupBiller;
import id.co.btpn.corp.dev.appmdwdev02.com_btpn_biller_ws_provider.btpnbillerwstopup.TopupBillerResponse;

import java.util.Calendar;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceOperations;
import org.springframework.ws.soap.SoapMessage;

import com.btpnwow.portal.common.util.MobiliserUtils;
import com.sybase365.mobiliser.money.services.api.ISystemEndpoint;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.application.pages.ConsumerPortalApplicationLoginPage;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;
import com.sybase365.mobiliser.web.btpn.consumer.beans.AirtimePerformBean;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PhoneNumber;
import com.sybase365.mobiliser.web.util.PortalUtils;


public class AirtimeTopupConfirmPage extends BtpnBaseConsumerPortalSelfCarePage {

	private static final Logger log = LoggerFactory.getLogger(AirtimeTopupDenominationsPage.class);
	
	private AirtimePerformBean airtimeBean;
	
	@SpringBean(name = "systemAuthSystemClient")
	private ISystemEndpoint systemEndpoint;

	@SpringBean(name="wsTopupTemplate")
	private WebServiceOperations wsTopupTemplate;
	

	public AirtimeTopupConfirmPage(final AirtimePerformBean airtimeBean) {
		super();
		this.airtimeBean = airtimeBean;
		initPageComponents();
	}

	protected void initPageComponents() {
		
		final Form<AirtimeTopupConfirmPage> form = new Form<AirtimeTopupConfirmPage>("airtimeConfirmForm",
			new CompoundPropertyModel<AirtimeTopupConfirmPage>(this));

		form.add(new FeedbackPanel("errorMessages"));
		
		form.add(new Label("airtimeBean.productId", airtimeBean.getBillerDescription()));
		form.add(new Label("airtimeBean.label"));
		form.add(new AmountLabel("airtimeBean.feeAmount"));
		form.add(new Label("airtimeBean.selectedMsisdn.id"));
		form.add(new PasswordTextField("airtimeBean.pin").add(new ErrorIndicator()));
		
		addSubmitButton(form);
		
		add(form);
	}

	/**
	 * This is used to add submit button.
	 */
	private void addSubmitButton(final Form<AirtimeTopupConfirmPage> form) {
		
		final Button btnSubmit = new Button("btnSubmit") {

			private static final long serialVersionUID = 1L;

			public void onSubmit() {
		
				try {
				
					if (checkCredential(airtimeBean.getPin())) {
						handlePerformAirtimeConfirm();
					} else {
						error(getLocalizer().getString("error.invalid.pin", this));
					}
					
				} catch (Exception e) {
					log.error("AirtimeTopupConfirmPage:checkCredential ==> Exception occured while performing ", e);
					error(getLocalizer().getString("error.exception", this));
				}
			};
		};
		
		form.add(btnSubmit);
	}
	
	
	/**
	 * This method is used for performing Airtime Topup.
	 */
	private void handlePerformAirtimeConfirm() {
		
		try {
			
			/* Get Current Calendar */
			Calendar cal = Calendar.getInstance();
			XMLGregorianCalendar transDate = PortalUtils.getSaveXMLGregorianCalendar(cal);
			
			String refNo = MobiliserUtils.getExternalReferenceNo(systemEndpoint);
			
			String userName = this.getMobiliserWebSession().getBtpnLoggedInCustomer().getUsername();
			String topupAmount = airtimeBean.getLabel();
			String productId = airtimeBean.getProductId();
			String billerId = airtimeBean.getBillerId();
			String feeAmount = String.valueOf(airtimeBean.getFeeAmount());
			String destMsisdn = null ;
			if (airtimeBean.getSelectedMsisdn()!=null){
				if (airtimeBean.getSelectedMsisdn().getId()!=null){
					destMsisdn = airtimeBean.getSelectedMsisdn().getId();
				}
			}
			
			TopupBiller req = new TopupBiller();
			CommonParam2 param = new CommonParam2();
			
			param.setPan("1234511234511234");
			param.setProcessingCode("100501");
			param.setChannelId("6018");
			param.setChannelType("PB");
			param.setNode("WOW_CHANNEL");
			param.setTerminalId("WOW");
			param.setTerminalName("WOW");
			param.setCurrencyAmount("IDR");
			param.setAmount(topupAmount);
			param.setTransmissionDateTime(transDate.toXMLFormat());
			param.setRequestId(refNo);
			param.setAcqId("213");
			param.setReferenceNo(refNo);
			param.setOriginal("CONSUMER_PORTAL");
			
			param.setCurrencyfee(airtimeBean.getFeeCurrency());
			param.setFee(feeAmount);
			
			req.setCommonParam(param);
			
			req.setProcessingCodeBiller("570000");
			req.setPinType("0");
			req.setPinValue(airtimeBean.getPin());
			req.setAccountNo(formatedNationalMsisdn(userName));
			req.setBillerCustNo(formatedMsisdn(destMsisdn));
			req.setInstitutionCode(billerId);
			req.setProductID(productId);
			
			req.setAttribute1("Topup Pulsa");
			req.setAttribute2(airtimeBean.getBillerDescription());
			
			TopupBillerResponse response = new TopupBillerResponse();
			
			response = (TopupBillerResponse) wsTopupTemplate.marshalSendAndReceive(req,
							new WebServiceMessageCallback() {
								public void doWithMessage(
										WebServiceMessage message) {
									((SoapMessage) message)
											.setSoapAction("com_btpn_biller_ws_provider_BtpnBillerWsTopup_Binder_topupBiller");
								}
							});
			
			final int statusCode = Integer.valueOf(response.getResponseCode());
			log.info("### (AirtimeTopupConfirmPage::Posting) RESPONSE ### " + statusCode);
			if(evaluateMobResponse(response, ConsumerPortalApplicationLoginPage.class)){
				setResponsePage(new AirtimeTopupStatusPage(airtimeBean));
			} else {
				error(MobiliserUtils.errorMessage(response.getResponseCode(), response.getResponseDesc(), getLocalizer(), this));
			}
			
		} catch (Exception e) {
			this.getWebSession().error(getLocalizer().getString("error.exception", this));
			log.error("AirtimeTopupConfirmPage:handlePerformAirtimeConfirm() ==> Exception occured while performing ",
				e);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public boolean evaluateMobResponse(TopupBillerResponse response,
		Class<? extends Page> loginClass) {

		if (Integer.parseInt(response.getResponseCode()) == 0 && Integer.parseInt(response.getResponseCode()) == 00) {
			return true;
		}

		// check for mobiliser session closed or expired
		if (Integer.parseInt(response.getResponseCode()) == 352 || Integer.parseInt(response.getResponseCode()) == 353) {
			log.debug("# Mobiliser session closed/expired, redirect to sign in page");
			getMobiliserWebSession().invalidate();
			getRequestCycle().setRedirect(true);
			if (null != loginClass) {

				setResponsePage(getComponent(loginClass));
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
	
	private String formatedMsisdn(String msisdn) {
		final PhoneNumber phoneNumber = new PhoneNumber(msisdn, this.getAgentPortalPrefsConfig()
			.getDefaultCountryCode());
		return phoneNumber.getInternationalFormat();
	}
	
	private String formatedNationalMsisdn(String msisdn) {
		final PhoneNumber phoneNumber = new PhoneNumber(msisdn, this.getAgentPortalPrefsConfig()
			.getDefaultCountryCode());
		return phoneNumber.getNationalFormat();
	}
	
}
