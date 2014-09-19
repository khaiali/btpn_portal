package com.sybase365.mobiliser.web.btpn.consumer.pages.portal.billpayment;

import id.co.btpn.corp.dev.appmdwdev02.com_btpn_biller_ws_provider.btpnbillerwsbillpayment.CommonParam2;
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
		initPageComponents();
	}

	/**
	 * This is the init page components for Bill Pay Perform page.
	 */
	public void initPageComponents() {
		
		final Form<BillPaymentConfirmPage> form = new Form<BillPaymentConfirmPage>("confirmBillPay",
			new CompoundPropertyModel<BillPaymentConfirmPage>(this));
		form.add(new FeedbackPanel("errorMessages"));
		form.add(new Label("billPayBean.billerLabel")); 
		form.add(new Label("billPayBean.productLabel"));
		form.add(new Label("billPayBean.billAmount"));
		form.add(new AmountLabel("billPayBean.feeAmount"));
		final boolean showCustomerName = billPayConsumerNameList.contains(billPayBean.getProductId());
		form.add(new Label("label.customer.name", getLocalizer().getString("label.customer.name", this))
			.setVisible(showCustomerName)); 
		form.add(new Label("billPayBean.customerName").setVisible(showCustomerName));
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
			    CommonParam2 commonParam = new CommonParam2();
			    
			    commonParam.setProcessingCode("100601");
				commonParam.setChannelId("6018");
				commonParam.setChannelType("PB");
				commonParam.setNode("WOW_CHANNEL");
				commonParam.setCurrencyAmount("IDR");
				commonParam.setAmount(String.valueOf(billPayBean.getBillAmount()));
				commonParam.setCurrencyfee(billPayBean.getFeeCurrency());
				commonParam.setFee(String.valueOf(billPayBean.getFeeAmount()));
				commonParam.setTransmissionDateTime(PortalUtils.getSaveXMLGregorianCalendar(Calendar.getInstance()).toXMLFormat());
				commonParam.setRequestId(MobiliserUtils.getExternalReferenceNo(isystemEndpoint));
				commonParam.setAcqId("213");
				commonParam.setReferenceNo(String.valueOf(billPayBean.getReferenceNumber()));
				commonParam.setTerminalId("WOW");
				commonParam.setTerminalName("WOW");
				//commonParam.setOriginal("USSD");benico pindah jd additional data
				commonParam.setOriginal(billPayBean.getAdditionalData());
				
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
				String desc3=" ";
				
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
								.setSoapAction("com_btpn_biller_ws_provider_BtpnBillerWsTopup_Binder_topupBiller");
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
}
