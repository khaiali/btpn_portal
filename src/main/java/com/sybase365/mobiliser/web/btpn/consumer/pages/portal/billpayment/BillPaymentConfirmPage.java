package com.sybase365.mobiliser.web.btpn.consumer.pages.portal.billpayment;

import id.co.btpn.corp.dev.appmdwdev02.com_btpn_biller_ws_provider.btpnbillerwsbillpayment.CommonParam2;
import id.co.btpn.corp.dev.appmdwdev02.com_btpn_biller_ws_provider.btpnbillerwsbillpayment.ObjectFactory;
import id.co.btpn.corp.dev.appmdwdev02.com_btpn_biller_ws_provider.btpnbillerwsbillpayment.PaymentBiller;
import id.co.btpn.corp.dev.appmdwdev02.com_btpn_biller_ws_provider.btpnbillerwsbillpayment.PaymentBillerResponse;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.transform.TransformerException;

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
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;
import com.sybase365.mobiliser.web.btpn.consumer.beans.BillPaymentPerformBean;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;
import com.sybase365.mobiliser.web.util.PhoneNumber;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the page for BillPayment Confirmation.
 * 
 * @author Vikram Gunda
 */
public class BillPaymentConfirmPage extends BtpnBaseConsumerPortalSelfCarePage {
	
	private static final Logger LOG = LoggerFactory.getLogger(BillPaymentConfirmPage.class);

	private static final long serialVersionUID = 1L;

	@SpringBean(name = "wsBillerTemplate")
	private WebServiceOperations webServiceTemplate;
	
	@SpringBean(name = "systemAuthSystemClient")
	private ISystemEndpoint isystemEndpoint;
	
	private BillPaymentPerformBean billPayBean;

	private List<String> billPayConsumerNameList;

	public BillPaymentConfirmPage(final BillPaymentPerformBean billPayBean) {
		super();
		this.billPayBean = billPayBean;
		billPayConsumerNameList = Arrays.asList(this.getCustomerPortalPrefsConfig().getPlnPrePaidNosForCustomerName()
			.split(","));
		parsingAdditionalData();
		initPageComponents();
	}

	/*
	 * for get data in additional data 1
	 */
	
	public void parsingAdditionalData() {
<<<<<<< HEAD
		String AdditionalData = billPayBean.getAdditionalData();
		switch (Integer.parseInt(billPayBean.getBillerId())) {
		case 91901:
			billPayBean.setCustomerName(AdditionalData.substring(95,120).trim());
			billPayBean.setMeterNumber(removePadding(AdditionalData.substring(7,18), "right", "0"));
			billPayBean.setBillNumber(removePadding(AdditionalData.substring(18,30), "right", "0"));
			break;
		case 91951:
			billPayBean.setCustomerName(AdditionalData.substring(47,72).trim());
			billPayBean.setBillNumber(removePadding(AdditionalData.substring(0,12), "right", "0"));
			break;
		case 91999:
			billPayBean.setCustomerName(AdditionalData.substring(66,91).trim());
			billPayBean.setRegNumber(AdditionalData.substring(0,13).trim());
=======
		String additionalData = billPayBean.getAdditionalData();
		Long totalAmount = billPayBean.getBillAmount() + billPayBean.getFeeAmount();
		switch (Integer.parseInt(billPayBean.getBillerId())) {
		case 91901:
			billPayBean.setCustomerName(additionalData.substring(95,120));
			billPayBean.setMeterNumber(additionalData.substring(7,18));
			billPayBean.setBillNumber(additionalData.substring(18,30));
			billPayBean.setTarif(additionalData.substring(120,124).concat("/").concat(additionalData.substring(132,141)));
			//billPayBean.setDaya(additionalData.substring(132,141));
			/*
			 * Data yang dibutuhkan tidak ada di inquiry response
			billPayBean.setMaterai(additionalData.substring(154,164));
			billPayBean.setPpn(additionalData.substring(165,175));
			billPayBean.setPpj(additionalData.substring(176,186));
			billPayBean.setAngsuran(additionalData.substring(187,197));
			billPayBean.setTokenAmount(additionalData.substring(198,210));
			billPayBean.setKwh(additionalData.substring(211,221));
			billPayBean.setToken(additionalData.substring(221,241));
			 */
			break;
		case 91951:
			billPayBean.setCustomerName(additionalData.substring(47,72));
			billPayBean.setBillNumber(additionalData.substring(0,12));
			billPayBean.setTarif(additionalData.substring(92,96));
			billPayBean.setDaya(additionalData.substring(96,105));
			billPayBean.setMonthYear(additionalData.substring(114,120));
			billPayBean.setTotalAmount(totalAmount);
			/*
			 * Data yang dibutuhkan tidak ada di inquiry reponse
			 * billPayBean.setStdMeter(additionalData.substring(210,218).concat("-").concat(additionalData.substring(218,226)));
			 */
			break;
		case 91999:
			billPayBean.setCustomerName(additionalData.substring(66,91));
			billPayBean.setRegNumber(additionalData.substring(0,13));
			billPayBean.setDateReg(dateFormat(additionalData.substring(28,36)));
			billPayBean.setBillNumber(additionalData.substring(44,56));
>>>>>>> origin/master
			break;
		default:
		}
		return ;
	}
	
<<<<<<< HEAD
=======
	/*
	 * This is for convert the date format
	 */
	
	public String dateFormat(String date){
		String newDate = null;
		try {
			SimpleDateFormat inputFormat  = new SimpleDateFormat("YYYYMMDD");
	        SimpleDateFormat outputFormat = new SimpleDateFormat("ddMMMYY");

	        Date parsedDate = inputFormat.parse(date);
	        System.out.println(outputFormat.format(parsedDate));
		} catch (Exception e) {
			// TODO: handle exception
		}
		return newDate;
	}
	
>>>>>>> origin/master
	/**
	 * This is the init page components for Bill Pay Perform page.
	 */
	public void initPageComponents() {
		
		boolean showMeterID 	= false;
		boolean showBillerID 	= false;
		boolean showRegID		= false;
		boolean fillMeterID		= false;
		boolean fillBillerID 	= false;
		boolean fillRegID		= false;
		if(billPayBean.getBillerId().equals("91901")) { //prepaid
			showMeterID 	= true;
			showBillerID 	= true;
			fillMeterID		= true;
			fillBillerID	= true;
		} else if(billPayBean.getBillerId().equals("91951")) { //postpaid
			showBillerID 	= true;
			fillBillerID	= true;
		} else if(billPayBean.getBillerId().equals("91999")) { //nontaglis
			showRegID		= true;
			fillRegID		= true;
		}
		
		final Form<BillPaymentConfirmPage> form = new Form<BillPaymentConfirmPage>("confirmBillPay",
			new CompoundPropertyModel<BillPaymentConfirmPage>(this));
		form.add(new FeedbackPanel("errorMessages"));
		form.add(new Label("billPayBean.billerLabel")); 
		form.add(new Label("billPayBean.productLabel"));
		form.add(new Label("billPayBean.billAmount"));
		form.add(new AmountLabel("billPayBean.feeAmount"));
		final boolean showNameandBillNumber = billPayConsumerNameList.contains(billPayBean.getBillerId());
		form.add(new Label("label.customer.name", getLocalizer().getString("label.customer.name", this)).setVisible(showNameandBillNumber)); 
		form.add(new Label("billPayBean.customerName").setVisible(showNameandBillNumber));
		form.add(new Label("label.meter.number", getLocalizer().getString("label.meter.number", this)).setVisible(showMeterID));
		form.add(new Label("billPayBean.meterNumber").setVisible(fillMeterID));
		form.add(new Label("label.bill.number", getLocalizer().getString("label.bill.number", this)).setVisible(showBillerID));
		form.add(new Label("billPayBean.billNumber").setVisible(fillBillerID));
		form.add(new Label("label.reg.number", getLocalizer().getString("label.reg.number", this)).setVisible(showRegID));
		form.add(new Label("billPayBean.regNumber").setVisible(fillRegID));
		
		form.add(new PasswordTextField("billPayBean.pin").add(new ErrorIndicator()));
		
		addSubmitButton(form);
		
		add(form);
		
	}

	/**
	 * This adds the submit button.
	 */
	private void addSubmitButton(final Form<BillPaymentConfirmPage> form) {

		Button confirmBtn = new Button("btnConfirm") {

			private static final long serialVersionUID = 1L;

			public void onSubmit() {
				try {
					if (checkCredential(billPayBean.getPin())) {
						handleConfirmBillPayment();
					} else {
						error(getLocalizer().getString("error.invalid.pin", this));
					}
				} catch (Exception e) {
					error(getLocalizer().getString("error.exception", this));
				}
			};
		};
		form.add(confirmBtn);
	}

	/**
	 * This method is used for performing bill payment.
	 */
	private void handleConfirmBillPayment() {
		try {
			 	PaymentBiller request = new PaymentBiller();
			 	ObjectFactory f = new ObjectFactory();
				CommonParam2 commonParam = f.createCommonParam2();
			    
			    commonParam.setProcessingCode(f.createCommonParam2ProcessingCode("100601"));
				commonParam.setChannelId(f.createCommonParam2ChannelId("6018"));
				commonParam.setChannelType(f.createCommonParam2ChannelType("PB"));
				commonParam.setNode(f.createCommonParam2Node("WOW_CHANNEL"));
				commonParam.setCurrencyAmount(f.createCommonParam2CurrencyAmount("IDR"));
				commonParam.setAmount(f.createCommonParam2Amount(String.valueOf(billPayBean.getBillAmount())));
				commonParam.setCurrencyfee(f.createCommonParam2Currencyfee(billPayBean.getFeeCurrency()));
				commonParam.setFee(f.createCommonParam2Fee(String.valueOf(billPayBean.getFeeAmount())));
				commonParam.setTransmissionDateTime(f.createCommonParam2TransmissionDateTime(PortalUtils.getSaveXMLGregorianCalendar(Calendar.getInstance()).toXMLFormat()));
				commonParam.setRequestId(f.createCommonParam2RequestId(MobiliserUtils.getExternalReferenceNo(isystemEndpoint)));
				commonParam.setAcqId(f.createCommonParam2AcqId("213"));
				commonParam.setReferenceNo(f.createCommonParam2ReferenceNo(String.valueOf(billPayBean.getReferenceNumber())));
				commonParam.setTerminalId(f.createCommonParam2TerminalId("WOW"));
				commonParam.setTerminalName(f.createCommonParam2TerminalName("WOW"));
				//commonParam.setOriginal("USSD");benico pindah jd additional data
				commonParam.setOriginal(f.createCommonParam2Original(billPayBean.getAdditionalData()));
				
				request.setCommonParam(commonParam);
				PhoneNumber phone = new PhoneNumber(this.getMobiliserWebSession().getBtpnLoggedInCustomer().getUsername());
				request.setAccountNo(phone.getNationalFormat());
				request.setBillerCustNo( billPayBean.getSelectedBillerId().getId());  
				request.setDebitType("0");
				request.setInstitutionCode(billPayBean.getBillerId());
				request.setProductID(billPayBean.getProductId());
				request.setUnitId("0901");
				request.setProcessingCodeBiller("501000");
				
				String desc1="Bill Payment ";
				String desc2= billPayBean.getBillerId();
				String desc3="";
				
				request.setAdditionalData1(desc1);
				request.setAdditionalData2(desc2);
				request.setAdditionalData3(desc3);
			
				PaymentBillerResponse response = new PaymentBillerResponse();
				
				response = (PaymentBillerResponse) webServiceTemplate.marshalSendAndReceive(request,
						new WebServiceMessageCallback() {
					
							@Override
							public void doWithMessage(WebServiceMessage message) throws IOException,
									TransformerException {
								((SoapMessage) message)
								.setSoapAction("com_btpn_biller_ws_provider_BtpnBillerWsTopup_Binder_paymentBiller");
							}
				});
			    
				if (response.getResponseCode().equals("00")) {
					billPayBean.setStatusMessage(getLocalizer().getString("success.perform.billpayment", this));
					setResponsePage(new BillPaymentStatusPage(billPayBean));
				}
				else
				{
					error(MobiliserUtils.errorMessage(response.getResponseCode(), response.getResponseDesc(), getLocalizer(), this));
				}
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			
			LOG.error("An exception was thrown", e);
		}
		
	}
<<<<<<< HEAD
	
	
	public static String removePadding(String text, String allign, String type) {
		String result = "";
		char[] textChar = text.toCharArray();
		if(allign.equalsIgnoreCase("right")) {
			for(int i=0; i<textChar.length; i++) {
				if(!String.valueOf(textChar[i]).equals(type)) {
					result = text.substring(i);
					break;
				}
			}
		}
		else if(allign.equalsIgnoreCase("left")) {
			for(int i=textChar.length-1; i>0; i--) {
				if(!String.valueOf(textChar[i]).equals(type)) {
					result = text.substring(0,i+1);
					break;
				}
			}
		}
		return result;
	}
=======

	public void printStrukPrepaid(){
		
		try {
			
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
>>>>>>> origin/master
}
