package com.sybase365.mobiliser.web.btpn.consumer.pages.portal.billpayment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.web.btpn.consumer.beans.BillPaymentPerformBean;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.ConsumerPortalHomePage;

/**
 * This is the page for BillPayment Confirmation.
 * 
 * @author Vikram Gunda
 */
public class BillPaymentStatusPage extends BtpnBaseConsumerPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	private BillPaymentPerformBean billPayBean;

	public BillPaymentStatusPage(final BillPaymentPerformBean billPayBean) {
		super();
		this.billPayBean = billPayBean;
		initPageComponents();
	}
	
	/**
	 * This is the init page components for Bill Pay Perform page.
	 */
	public void initPageComponents() {
		
		boolean showCustNameandBillAmount = false;
		boolean showMeterNumber = false;
		boolean showBillNumber = false;
		boolean showTarifDaya = false;
		boolean showMaterai = false;
		boolean showPPN = false;
		boolean showPPJ = false;
		boolean showAngsuran = false;
		boolean showTokenAmount = false;
		boolean showKwh = false;
		boolean showToken = false;
		
		boolean showMonth = false;
		boolean showStdMeter = false;
		boolean showFee = false;
		boolean showTotalAmount = false;
		
		boolean showRegNumber = false;
		boolean showDateReg = false;
		
		
		if(billPayBean.getBillerId().equals("91901")){
			showCustNameandBillAmount = true;
			showMeterNumber = true;
			showBillNumber = true;
			showTarifDaya = true;
			showMaterai = true;
			showPPN = true;
			showPPJ = true;
			showAngsuran = true;
			showTokenAmount = true;
			showKwh = true;
			showToken = true;
		}else if(billPayBean.getBillerId().equals("91951")){
			showCustNameandBillAmount = true;
			showBillNumber = true;
			showTarifDaya = true;
			showMonth = true;
			showStdMeter = true;
			showFee = true;
			showTotalAmount = true;
		}else if(billPayBean.getBillerId().equals("91999")){
			showCustNameandBillAmount = true;
			showRegNumber = true;
			showDateReg = true;
			showBillNumber = true;
			showFee = true;
			showTotalAmount = true;
		}
		
		final Form<BillPaymentStatusPage> form = new Form<BillPaymentStatusPage>("confirmBillPay",
			new CompoundPropertyModel<BillPaymentStatusPage>(this));
		form.add(new Label("billPayBean.statusMessage").setRenderBodyOnly(true));
		
		parsingAdditionalData();

		
		form.add(new Label("label.reg.number", getLocalizer().getString("label.reg.number", this))).setVisible(showRegNumber);
		form.add(new Label("billPayBean.regNumber").setVisible(showRegNumber));
		
		form.add(new Label("label.date.number", getLocalizer().getString("label.date.number", this))).setVisible(showDateReg);
		form.add(new Label("billPayBean.dateReg").setVisible(showDateReg));
		
		form.add(new Label("label.meter.number", getLocalizer().getString("label.meter.number", this))).setVisible(showMeterNumber);
		form.add(new Label("billPayBean.meterNumber").setVisible(showMeterNumber));
		
		form.add(new Label("label.bill.number", getLocalizer().getString("label.bill.number", this))).setVisible(showBillNumber);
		form.add(new Label("billPayBean.billNumber").setVisible(showBillNumber));
		
		form.add(new Label("label.customer.name", getLocalizer().getString("label.customer.name", this))).setVisible(showCustNameandBillAmount);
		form.add(new Label("billPayBean.customerName").setVisible(showCustNameandBillAmount));
		
		form.add(new Label("label.tarifDaya", getLocalizer().getString("label.tarifDaya", this))).setVisible(showTarifDaya);
		form.add(new Label("billPayBean.tarif".concat("/").concat("billPayBean.daya")).setVisible(showTarifDaya));
		
		form.add(new Label("label.monthYear", getLocalizer().getString("label.monthYear", this))).setVisible(showMonth);
		form.add(new Label("billPayBean.monthYear").setVisible(showMonth));
		
		form.add(new Label("label.std.meter", getLocalizer().getString("label.std.meter", this))).setVisible(showStdMeter);
		form.add(new Label("billPayBean.stdMeter").setVisible(showStdMeter));
		
		form.add(new Label("label.bill.amount", getLocalizer().getString("label.bill.amount", this))).setVisible(showCustNameandBillAmount);
		form.add(new Label("RP ".concat("billPayBean.billAmount").concat(",00")).setVisible(showCustNameandBillAmount));

		form.add(new Label("label.fee.amount", getLocalizer().getString("label.fee.amount", this))).setVisible(showFee);
		form.add(new Label("RP ".concat("billPayBean.feeAmount").concat(",00")).setVisible(showFee));
		
		form.add(new Label("label.total.amount", getLocalizer().getString("label.total.amount", this))).setVisible(showTotalAmount);
		form.add(new Label("RP ".concat("billPayBean.totalAmount").concat(",00")).setVisible(showTotalAmount));
		
		form.add(new Label("label.materai", getLocalizer().getString("label.materai", this))).setVisible(showMaterai);
		form.add(new Label("RP ".concat("billPayBean.materai").concat(",00")).setVisible(showMaterai));
		
		form.add(new Label("label.ppn", getLocalizer().getString("label.ppn", this))).setVisible(showPPN);
		form.add(new Label("RP ".concat("billPayBean.ppn").concat(",00")).setVisible(showPPN));
		
		form.add(new Label("label.ppj", getLocalizer().getString("label.ppj", this))).setVisible(showPPJ);
		form.add(new Label("RP ".concat("billPayBean.ppj").concat(",00")).setVisible(showPPJ));
		
		form.add(new Label("label.angsuran", getLocalizer().getString("label.angsuran", this))).setVisible(showAngsuran);
		form.add(new Label("RP ".concat("billPayBean.angsuran").concat(",00")).setVisible(showAngsuran));
		
		form.add(new Label("label.token.amount", getLocalizer().getString("label.token.amount", this))).setVisible(showTokenAmount);
		form.add(new Label("RP ".concat("billPayBean.tokenAmount").concat(",00")).setVisible(showTokenAmount));
		
		form.add(new Label("label.kwh", getLocalizer().getString("label.kwh", this))).setVisible(showKwh);
		form.add(new Label("billPayBean.kwh").setVisible(showKwh));
		
		form.add(new Label("label.token", getLocalizer().getString("label.token", this))).setVisible(showToken);
		form.add(new Label("billPayBean.token").setVisible(showToken));
		
		addSubmitButton(form);
		add(form);
	}

	/**
	 * This adds the submit button.
	 */
	private void addSubmitButton(final Form<BillPaymentStatusPage> form) {

		Button confirmBtn = new Button("btnOk") {

			private static final long serialVersionUID = 1L;

			public void onSubmit() {
				BillPaymentStatusPage.this.handleCancelButtonRedirectToHomePage(ConsumerPortalHomePage.class);
			};
		};
		form.add(confirmBtn);
	}
	
	/*
	 * for get data in additional data 1
	 */
	
	public void parsingAdditionalData() {
		String additionalData = billPayBean.getAdditionalData();
		Long totalAmount = billPayBean.getBillAmount() + billPayBean.getFeeAmount();
		switch (Integer.parseInt(billPayBean.getBillerId())) {
		case 91901: //PLN PrePaid
			billPayBean.setMeterNumber(additionalData.substring(7,18));
			billPayBean.setBillNumber(additionalData.substring(18,30));
			billPayBean.setCustomerName(additionalData.substring(95,120));
			billPayBean.setTarif(additionalData.substring(128,132));
			billPayBean.setDaya(additionalData.substring(132,141));
			billPayBean.setMaterai(additionalData.substring(154,164));
			billPayBean.setPpn(additionalData.substring(165,175));
			billPayBean.setPpj(additionalData.substring(176,186));
			billPayBean.setAngsuran(additionalData.substring(187,197));
			billPayBean.setTokenAmount(additionalData.substring(198,210));
			billPayBean.setKwh(additionalData.substring(211,221));
			billPayBean.setToken(additionalData.substring(221,241));
			break;
		case 91951: //PLN PostPaid
			billPayBean.setBillNumber(additionalData.substring(0,12));
			billPayBean.setCustomerName(additionalData.substring(47,72));
			billPayBean.setTarif(additionalData.substring(125,129));
			billPayBean.setDaya(additionalData.substring(129,138));
			billPayBean.setMonthYear(additionalData.substring(147,153));
			billPayBean.setStdMeter(additionalData.substring(210,218).concat("-").concat(additionalData.substring(218,226)));
			billPayBean.setTotalAmount(totalAmount);
			break;
		case 91999://PLN NonTagLis
			billPayBean.setRegNumber(additionalData.substring(0,13));
			billPayBean.setDateReg(dateFormat(additionalData.substring(38,46)));
			billPayBean.setCustomerName(additionalData.substring(66,91));
			billPayBean.setBillNumber(additionalData.substring(7,18));
			break;
		default: 
			break;
		}
		return ;
	}
	
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
}
