package com.sybase365.mobiliser.web.btpn.bank.pages.portal.billpayment;

import id.co.btpn.corp.dev.appmdwdev02.com_btpn_biller_ws_provider.btpnbillerwsbillpayment.CommonParam2;
import id.co.btpn.corp.dev.appmdwdev02.com_btpn_biller_ws_provider.btpnbillerwsbillpayment.InqDebitBillPayment;
import id.co.btpn.corp.dev.appmdwdev02.com_btpn_biller_ws_provider.btpnbillerwsbillpayment.InqDebitBillPaymentResponse;
import id.co.btpn.corp.dev.appmdwdev02.com_btpn_biller_ws_provider.btpnbillerwsbillpayment.ObjectFactory;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
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
import com.sybase365.mobiliser.web.btpn.bank.beans.BankBillPaymentPerformBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.util.PhoneNumber;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class BankBillPaymentPage extends BtpnBaseBankPortalSelfCarePage{
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(BankBillPaymentPage.class);
	private static final int N = 7;
	
	@SpringBean(name = "wsBillerTemplate")
	private WebServiceOperations webServiceTemplete;
	
	@SpringBean(name = "billerProductLookup")
	private BillerProductLookup billerProductLookup;
	
	@SpringBean(name = "systemAuthSystemClient")
	private ISystemEndpoint isystemEndpoint;
	
	private String[] labels;
	private CodeValue[] opts;
	private WebMarkupContainer[] containers;
	private DropDownChoice<CodeValue>[] choices;

	private BankBillPaymentPerformBean billPayBean;

	private boolean isTelePhonaBillPay;
	
	private TextField<String> billerIdField;
	
	/**
	 * Constructor for the BillPayment Page.
	 */
	public BankBillPaymentPage() {
		super();

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
			labels[i] = billerProductLookup.get(rootId).getLabel();
		}
		else {
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
				
				//temp selected Label
				if(i==0)
				{	
					isTelePhonaBillPay = (root.getLabel().equals("Handphone") || root.getLabel().equals("Telepon"));
				}	
			}
		});

		opts[i] = null;
		
		container.add(label);
		container.add(choices);
		
		containers[i] = container;
		choices[i] = choice;
		
		frm.add(container);
	}
	
	@Override
	protected void initOwnPageComponents() {
		super.initOwnPageComponents();
		
		this.labels = new String[N];
		this.opts = new CodeValue[N];
		
		this.containers = new WebMarkupContainer[N];
		this.choices = new DropDownChoice[N];
		
		this.billPayBean = new BankBillPaymentPerformBean();
		
		final Form<BankBillPaymentPage> form = new Form<BankBillPaymentPage>(
				"billPaymentForm", new CompoundPropertyModel<BankBillPaymentPage>(this));
		
		form.add(new FeedbackPanel("errorMessages"));
		
		final IChoiceRenderer<CodeValue> choiceRenderer = new ChoiceRenderer<CodeValue>(
				BtpnConstants.DISPLAY_EXPRESSION, BtpnConstants.ID_EXPRESSION);
		
		String rootId;
		
		if ("IN".equalsIgnoreCase(getLocale().getLanguage())) {
			rootId = "in.100";
		} else {
			rootId = "en.150";
		}
		
		for (int i=0;i < N; ++i) {
			constructOneLevel(form, i, choiceRenderer, rootId);
		}
		
		billerIdField = (TextField<String>) new TextField<String>("billPayBean.fillBillerId").add(new ErrorIndicator());
		
		// Add the textfield.
		form.add(billerIdField);
		
		// Submit button for form.
		addSubmitButton(form);
		
		add(form);
		
	}
	
	/**
	 * This method is used for performing bill payment validations
	 */
	public boolean performBillPayValidations() {
		final String errorKey = isTelePhonaBillPay ? "phonenumber.Required" : "Required";
		
		if (!PortalUtils.exists(billPayBean.getSelectedBillerId())) {
			billerIdField.error(getLocalizer().getString("billPayBean.selectedBillerId." + errorKey, this));
			
		}
		if (!PortalUtils.exists(billPayBean.getSelectedBillerId().getId())) {
			billerIdField.error(getLocalizer().getString("billPayBean.selectedBillerId." + errorKey, this));
			
		}
		return true;
	}

	/**
	 * This is used to add submit button.
	 */
	private void addSubmitButton(Form<BankBillPaymentPage> form) {
		// TODO Auto-generated method stub
		form.add(new Button("btnSubmit") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void onSubmit() {
				String rootId = null;
				String finalId = null;
				
				for (int i = 0; i < N; ++i) {
					if (opts[i] != null) {
						rootId = opts[0].getId();
						List<CodeValue> children = billerProductLookup.getChildrenAsCodeValue(opts[i].getId());
						
						if ((finalId == null) || children.isEmpty()) {
							finalId = opts[i].getId();
							
						}
						
					}
				}
				
				if (finalId == null) {
					error(BankBillPaymentPage.this.getLocalizer().getString("product.required", BankBillPaymentPage.this));
				} else if (!performBillPayValidations()) {
					return;
				} else {
					BillerProduct billerRoot =  billerProductLookup.get(rootId);
					billPayBean.setBillerLabel(billerRoot.getLabel());
					
					BillerProduct billerProduct = billerProductLookup.get(rootId);
					billPayBean.setBillerId(billerProduct.getBillerId());
					billPayBean.setProductId(billerProduct.getProductId());
					billPayBean.setProductLabel(billerProduct.getLabel());
					
					handlePerformBillPayment();
				}
			}
			
		}.setDefaultFormProcessing(true));
	}
	
	/**
	 * This method is used for performing bill payment.
	 */
	private void handlePerformBillPayment() {
		// TODO Auto-generated method stub
		try {
			//call middleware
			InqDebitBillPayment request = new InqDebitBillPayment();
			
			ObjectFactory f = new ObjectFactory();
			CommonParam2 commonParam = f.createCommonParam2();
			billPayBean.setReferenceNumber(MobiliserUtils.getExternalReferenceNo(isystemEndpoint));
			billPayBean.setPayDate(PortalUtils.getSaveXMLGregorianCalendar(Calendar.getInstance()).toXMLFormat());
			
			//TODO  debit Account  => debitIdentifierType, debitOrgUnitId
			
			switch (Integer.parseInt(billPayBean.getBillerId())) {
			case 91901:
				commonParam.setPan(f.createCommonParam2Pan("053502"));
				break;
			case 91951:
				commonParam.setPan(f.createCommonParam2Pan("053501"));
				break;
			case 91999:
				commonParam.setPan(f.createCommonParam2Pan("053504"));
			default :
				commonParam.setPan(f.createCommonParam2Pan("1234511234511234"));
				break;
			}
			commonParam.setProcessingCode(f.createCommonParam2ProcessingCode("100601"));
			commonParam.setChannelId(f.createCommonParam2ChannelId("6017"));
			commonParam.setChannelType(f.createCommonParam2ChannelType("PB"));
			commonParam.setNode(f.createCommonParam2Node("WOW_CHANNEL"));
			commonParam.setCurrencyAmount(f.createCommonParam2CurrencyAmount("IDR"));
			commonParam.setAmount(f.createCommonParam2Amount(String.valueOf(0)));
			commonParam.setTransmissionDateTime(f.createCommonParam2TransmissionDateTime(billPayBean.getPayDate()));
			commonParam.setRequestId(f.createCommonParam2RequestId(billPayBean.getReferenceNumber()));
			commonParam.setAcqId(f.createCommonParam2AcqId("213"));
			commonParam.setReferenceNo(f.createCommonParam2ReferenceNo(billPayBean.getReferenceNumber()));
			commonParam.setTerminalId(f.createCommonParam2TerminalId("WOW"));
			commonParam.setTerminalName(f.createCommonParam2TerminalName("WOW"));
			commonParam.setOriginal(f.createCommonParam2Original("USSD"));
		
			request.setCommonParam(commonParam);
			PhoneNumber phone = new PhoneNumber(this.getMobiliserWebSession().getBtpnLoggedInCustomer().getUsername());
			request.setAccountNo( phone.getNationalFormat()); //msisdn
			request.setProductID( billPayBean.getProductId());
			//request.setInstitutionCode(billPayBean.getBillerId());
			//request.setBillerCustNo( billPayBean.getSelectedBillerId().getId() );
			request.setDebitType("MSISDN");
			request.setUnitId("");
			request.setProcessingCodeBiller("380000");
			request.setAdditionalData1("534611446505" /*billPayBean.getSelectedBillerId().getId()*/);
			//request.setAdditionalData2(billPayBean.getBillerId());
			//request.setAdditionalData3(" ");
			
			InqDebitBillPaymentResponse response = new InqDebitBillPaymentResponse();
			InqDebitBillPaymentResponse response2 = new InqDebitBillPaymentResponse();
			
			response = (InqDebitBillPaymentResponse) webServiceTemplete.marshalSendAndReceive(request, new WebServiceMessageCallback() {
				
				
				@Override
				public void doWithMessage(WebServiceMessage message) throws IOException,
						TransformerException {
					// TODO Auto-generated method stub
					((SoapMessage) message).setSoapAction("com_btpn_biller_ws_provider_BtpnBillerWsBillPayment_Binder_inqDebitBillPayment");
					
				}
			});
			
			LOG.info("Response 1 : "+response.getResponseCode()+"##"+response.getResponseDesc());
			if (response.getResponseCode().equals("00")) {
				String billerAmount = "0";
				billerAmount = response.getTransactionAmount();
				if (response.getClass().getTypeParameters()==null) {
					billerAmount = "0";
					
				}
				
				switch (Integer.parseInt(billPayBean.getBillerId())) {
				case 91901:
					billPayBean.setAdditionalData(response.getAdditionalData());
					billPayBean.setAdditionalData2(response.getAdditionalData2());
					break;
				default:
					billPayBean.setAdditionalData(response.getAdditionalData());
					break;
				}
				billPayBean.setReferenceNumber(MobiliserUtils.getExternalReferenceNo(isystemEndpoint));
				commonParam.setAmount(f.createCommonParam2Amount(String.valueOf(billerAmount)));
				commonParam.setReferenceNo(f.createCommonParam2ReferenceNo(billPayBean.getReferenceNumber()));
				
				response2 = (InqDebitBillPaymentResponse) webServiceTemplete.marshalSendAndReceive(request, 
						new WebServiceMessageCallback() {
					public void doWithMessage(
							WebServiceMessage message) {
						((SoapMessage) message)
						.setSoapAction("com_btpn_biller_ws_provider_BtpnBillerWsBillPayment_Binder_inqDebitBillPayment");
						
					}
					
				});
				
				LOG.info("Response 2 : "+response2.getResponseCode()+"##"+response2.getResponseDesc());
				if (response2.getResponseCode().equals("00")) {
					// -- Output Transaction Id --
					billPayBean.setBillAmount(Long.valueOf(response2.getTransactionAmount()));
					billPayBean.setFeeAmount(Long.valueOf(response2.getFee()));
					billPayBean.setTxnId("1");
					billPayBean.setFeeCurrency(response2.getFeeCurrency());
					
					switchPanel(billPayBean);
				}
				else {
					error(MobiliserUtils.errorMessage(response2.getResponseCode(), response2.getResponseDesc(), getLocalizer(), this));
				}
			}
			else {
				error(MobiliserUtils.errorMessage(response2.getResponseCode(), response2.getResponseDesc(), getLocalizer(), this));
				
			}
			
		} catch (Exception e) {
			LOG.error("Exception occured while handlePerformBillPayment  ===> ", e);
			error(getLocalizer().getString("error.exception", this));
		}		
	}
	
	public void switchPanel (BankBillPaymentPerformBean billPayBean) {
		switch (Integer.parseInt(billPayBean.getBillerId())) {
		case 91901:
			setResponsePage(new BankBillPaymentPlnPrePaidAmountPage(billPayBean));
			break;
		default :
			setResponsePage(new BankBillPaymentConfirmPage(billPayBean));
			break;
		}
	}
	
}