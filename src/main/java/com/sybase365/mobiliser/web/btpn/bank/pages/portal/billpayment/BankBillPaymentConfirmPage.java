package com.sybase365.mobiliser.web.btpn.bank.pages.portal.billpayment;

import id.co.btpn.corp.dev.appmdwdev02.com_btpn_biller_ws_provider.btpnbillerwsbillpayment.CommonParam2;
import id.co.btpn.corp.dev.appmdwdev02.com_btpn_biller_ws_provider.btpnbillerwsbillpayment.ObjectFactory;
import id.co.btpn.corp.dev.appmdwdev02.com_btpn_biller_ws_provider.btpnbillerwsbillpayment.PaymentBiller;
import id.co.btpn.corp.dev.appmdwdev02.com_btpn_biller_ws_provider.btpnbillerwsbillpayment.PaymentBillerResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
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
import com.sybase365.mobiliser.web.btpn.bank.beans.BankBillPaymentPerformBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;
import com.sybase365.mobiliser.web.util.PhoneNumber;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the page for BillPayment Confirmation.
 * 
 * @author Imam Nur
 */

public class BankBillPaymentConfirmPage extends BtpnBaseBankPortalSelfCarePage {
	
	private static final Logger LOG = LoggerFactory.getLogger(BankBillPaymentConfirmPage.class);

	private static final long serialVersionUID = 1L;

	@SpringBean(name = "wsBillerTemplete")
	private WebServiceOperations webServiceTemplete;
	
	@SpringBean(name = "systemAuthSystemClient")
	private ISystemEndpoint isystemEndpoint;
	
	private BankBillPaymentPerformBean billPayBean;
	
	private List<String> billPayBankNameList;
	
	public BankBillPaymentConfirmPage(final BankBillPaymentPerformBean billPayBean) {
		super();
		this.billPayBean = billPayBean;
		billPayBankNameList = Arrays.asList(this.getBankPortalPrefsConfig().getPlnPrepaidNosForCustomerName()
			.split(","));
		
		initPageComponents();
	}
	
	/*
	 * for get data in additional data 1
	 */
	
	public void parsingAdditionalData() {
		String AdditionalData = billPayBean.getAdditionalData();
		switch (Integer.parseInt(billPayBean.getBillerId())) {
		case 91901:
			billPayBean.setCustomerName(AdditionalData.substring(95,120));
			billPayBean.setMeterNumber(AdditionalData.substring(7,18));
			billPayBean.setBillNumber(AdditionalData.substring(18,30));
			break;
		case 91951:
			billPayBean.setCustomerName(AdditionalData.substring(47,72));
			billPayBean.setBillNumber(AdditionalData.substring(0,12));
			break;
		case 91999:
			billPayBean.setCustomerName(AdditionalData.substring(66,91));
			billPayBean.setRegNumber(AdditionalData.substring(0,13));
			break;
		default:
		}
		return ;
	}
	
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
		
		final Form<BankBillPaymentConfirmPage> form = new Form<BankBillPaymentConfirmPage>("confirmBillPay", 
				new CompoundPropertyModel<BankBillPaymentConfirmPage>(this));
		form.add(new FeedbackPanel("errorMessages"));
		form.add(new Label("billPayBean.billerLabel")); 
		form.add(new Label("billPayBean.productLabel"));
		form.add(new Label("billPayBean.billAmount"));
		form.add(new AmountLabel("billPayBean.feeAmount"));
		final boolean showNameandBillNumber = billPayBankNameList.contains(billPayBean.getBillerId());
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
	private void addSubmitButton(Form<BankBillPaymentConfirmPage> form) {
		// TODO Auto-generated method stub
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
					// TODO: handle exception
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
		// TODO Auto-generated method stub
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
			
			String desc1="Bill Payment";
			String desc2= billPayBean.getBillerId();
			String desc3="";
			
			request.setAdditionalData1(desc1);
			request.setAdditionalData2(desc2);
			request.setAdditionalData3(desc3);
			
			PaymentBillerResponse response = new PaymentBillerResponse();
			
			response = (PaymentBillerResponse) webServiceTemplete.marshalSendAndReceive(request, 
					new WebServiceMessageCallback() {
				
				@Override
				public void doWithMessage(WebServiceMessage message) throws IOException,
						TransformerException {
					// TODO Auto-generated method stub
					((SoapMessage) message)
					.setSoapAction("com_btpn_biller_ws_provider_BtpnBillerWsTopup_Binder_paymentBiller");
				}
			});
			
			if (response.getResponseCode().equals("00")) {
				billPayBean.setStatusMessage(getLocalizer().getString("success.perform.billpayment", this));
				setResponsePage(new BankBillPaymentStatusPage(billPayBean));
			}
			else {
				error(MobiliserUtils.errorMessage(response.getResponseCode(), response.getResponseDesc(), getLocalizer(), this));
				
			}
		} catch (Exception e) {
			// TODO: handle exception
			error(getLocalizer().getString("error.exception", this));
			LOG.error("An exception was thrown", e);
		}
	}
}
