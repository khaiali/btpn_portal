package com.sybase365.mobiliser.web.btpn.consumer.common.panels;


import java.util.Date;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.DateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.core.schedtxn.facade.api.SchedTxnFacade;
import com.btpnwow.core.schedtxn.facade.contract.AddSchedTxnRequest;
import com.btpnwow.core.schedtxn.facade.contract.AddSchedTxnResponse;
import com.btpnwow.core.schedtxn.facade.contract.MapEntryType;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.common.behaviours.DateHeaderContributor;
import com.sybase365.mobiliser.web.btpn.common.components.AmountTextField;
import com.sybase365.mobiliser.web.btpn.consumer.beans.BillPaymentBean;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.standing.StandingInstructionsPage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnLocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.common.panels.AttachmentsPanel;
import com.sybase365.mobiliser.web.util.PortalUtils;


public class BillPaymentConfirmPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(BillPaymentConfirmPanel.class);

	protected BtpnMobiliserBasePage basePage;
	protected BillPaymentBean billPaymentBean;
	private FeedbackPanel feedBack;
	private String filterType;
	private String frequencyType;
	private String selectSubBiller;
	
	@SpringBean(name="siClient")
	private SchedTxnFacade siClient;

	public BillPaymentConfirmPanel(String id, BtpnBaseConsumerPortalSelfCarePage basePage,
		BillPaymentBean billPaymentBean) {
		this(id, basePage, null, billPaymentBean);
	}

	public BillPaymentConfirmPanel(String id, BtpnBaseConsumerPortalSelfCarePage basePage,
		AttachmentsPanel attachmentsPanel, BillPaymentBean billPaymentBean) {
		super(id);
		this.basePage = basePage;
		this.billPaymentBean = billPaymentBean;
		constructPanel();
		addDateHeaderContributor();
	}

	/**
	 * Adds the date header contributor
	 */
	protected void addDateHeaderContributor() {
		final String chooseDtTxt = this.basePage.getLocalizer().getString("datepicker.chooseDate", basePage);
		final String locale = this.basePage.getLocale().getLanguage();
		add(new HeaderContributor(new DateHeaderContributor(locale, BtpnConstants.DATE_FORMAT_PATTERN_PICKER,
			chooseDtTxt)));
	}

	protected void constructPanel() {
		
		final Form<BillPaymentConfirmPanel> form = new Form<BillPaymentConfirmPanel>("billPaymentConfirmForm",
			new CompoundPropertyModel<BillPaymentConfirmPanel>(this));
		feedBack = new FeedbackPanel("errorMessages");
		feedBack.setOutputMarkupId(true);
		form.add(feedBack);

		final IChoiceRenderer<CodeValue> codeValueChoiceRender = new ChoiceRenderer<CodeValue>(
			BtpnConstants.DISPLAY_EXPRESSION, BtpnConstants.ID_EXPRESSION);

//		form.add(new Label("billPaymentBean.billerType.value"));
//		form.add(new Label("billPaymentBean.subBillerType.value"));
		
		form.add(new Label("billPaymentBean.productName"));
		form.add(new Label("billPaymentBean.label"));

		RadioGroup<BillPaymentConfirmPanel> amountRadioGroup = new RadioGroup<BillPaymentConfirmPanel>(
			"amountRadioGroup", new PropertyModel<BillPaymentConfirmPanel>(this, "filterType"));
		amountRadioGroup.setRequired(true).add(new ErrorIndicator());

		@SuppressWarnings({ "rawtypes", "unchecked" })
		final Radio<BillPaymentConfirmPanel> amountCharged = new Radio<BillPaymentConfirmPanel>("amountCharged",
			new Model(BtpnConstants.FILTERTYPE_CHARGED));
		amountCharged.setOutputMarkupId(true);
		amountRadioGroup.add(amountCharged);

		@SuppressWarnings({ "rawtypes", "unchecked" })
		final Radio<BillPaymentConfirmPanel> amountFixed = new Radio<BillPaymentConfirmPanel>("amountFixed", new Model(
			BtpnConstants.FILTERTYPE_FIXED));
		amountFixed.setOutputMarkupId(true);
		amountRadioGroup.add(amountFixed);

		form.add(amountRadioGroup);
		
//		amountRadioGroup.setVisible(!billPaymentBean.getSubBillerType().getId()
//			.equals(this.basePage.getCustomerPortalPrefsConfig().getDefaultPlnPrePaid()));

		final WebMarkupContainer baseAmountContainer = new WebMarkupContainer("baseAmountContainer");
		baseAmountContainer.setOutputMarkupId(true);
		baseAmountContainer.setOutputMarkupPlaceholderTag(true);

		baseAmountContainer.add(new AmountTextField<Long>("billPaymentBean.baseAmount", Long.class, false).setRequired(
			true).add(new ErrorIndicator()));
		baseAmountContainer.setVisible(true);
		form.add(baseAmountContainer);

		form.add(new TextField<String>("billPaymentBean.siName").setRequired(true).add(new ErrorIndicator()));

		form.add(DateTextField.forDatePattern("billPaymentBean.startDate", BtpnConstants.ID_EXPIRY_DATE_PATTERN)
			.add(DateValidator.minimum(new Date(), BtpnConstants.ID_EXPIRY_DATE_PATTERN)).setRequired(true)
			.add(new ErrorIndicator()));

		form.add(DateTextField.forDatePattern("billPaymentBean.expiryDate", BtpnConstants.ID_EXPIRY_DATE_PATTERN)
			.add(DateValidator.minimum(new Date(), BtpnConstants.ID_EXPIRY_DATE_PATTERN)).setRequired(true)
			.add(new ErrorIndicator()));

		RadioGroup<BillPaymentConfirmPanel> frequencyRadioGroup = new RadioGroup<BillPaymentConfirmPanel>(
			"frequencyRadioGroup", new PropertyModel<BillPaymentConfirmPanel>(this, "frequencyType"));
		frequencyRadioGroup.setRequired(true).add(new ErrorIndicator());

		@SuppressWarnings({ "rawtypes", "unchecked" })
		final Radio<BillPaymentConfirmPanel> frequencyMonth = new Radio<BillPaymentConfirmPanel>("frequencyMonth",
			new Model(BtpnConstants.FREQUENCY_TYPE_MONTH));
		frequencyMonth.setOutputMarkupId(true);
		frequencyRadioGroup.add(frequencyMonth);

		@SuppressWarnings({ "rawtypes", "unchecked" })
		final Radio<BillPaymentConfirmPanel> frequencyQuarter = new Radio<BillPaymentConfirmPanel>("frequencyQuarter",
			new Model(BtpnConstants.FREQUENCY_TYPE_QUARTER));
		frequencyQuarter.setOutputMarkupId(true);
		frequencyRadioGroup.add(frequencyQuarter);

		@SuppressWarnings({ "rawtypes", "unchecked" })
		final Radio<BillPaymentConfirmPanel> frequencyWeek = new Radio<BillPaymentConfirmPanel>("frequencyWeek",
			new Model(BtpnConstants.FREQUENCY_TYPE_WEEK));
		frequencyWeek.setOutputMarkupId(true);
		frequencyRadioGroup.add(frequencyWeek);

		@SuppressWarnings({ "rawtypes", "unchecked" })
		final Radio<BillPaymentConfirmPanel> frequencyDate = new Radio<BillPaymentConfirmPanel>("frequencyDate",
			new Model(BtpnConstants.FREQUENCY_TYPE_DAILY));
		frequencyDate.setOutputMarkupId(true);
		frequencyRadioGroup.add(frequencyDate);

		form.add(frequencyRadioGroup);

		final WebMarkupContainer selectedDayContainer = new WebMarkupContainer("selectedDayContainer");
		selectedDayContainer.setOutputMarkupId(true);
		selectedDayContainer.setOutputMarkupPlaceholderTag(true);

		selectedDayContainer.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("billPaymentBean.selectedDay",
			CodeValue.class, BtpnConstants.RESOURCE_BUBDLE_SELECTED_DAY, this, Boolean.FALSE, Boolean.TRUE)
			.setNullValid(false).setChoiceRenderer(codeValueChoiceRender).setRequired(true).add(new ErrorIndicator()));
		selectedDayContainer.setVisible(true);

		form.add(selectedDayContainer);

		amountCharged.add(new AjaxEventBehavior("onclick") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				billPaymentBean.setSelectedChargedAmt(true);
				baseAmountContainer.setVisible(false);
				target.addComponent(baseAmountContainer);

			}
		});

		amountFixed.add(new AjaxEventBehavior("onclick") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				billPaymentBean.setSelectedFixedAmt(true);
				baseAmountContainer.setVisible(true);
				target.addComponent(baseAmountContainer);
			}
		});

		frequencyMonth.add(new AjaxEventBehavior("onclick") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				billPaymentBean.setFrequencyType(frequencyMonth.getDefaultModelObjectAsString());
				selectedDayContainer.setVisible(false);
				target.addComponent(selectedDayContainer);
			}
		});
		
		frequencyQuarter.add(new AjaxEventBehavior("onclick") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				billPaymentBean.setFrequencyType(frequencyQuarter.getDefaultModelObjectAsString());
				selectedDayContainer.setVisible(false);
				target.addComponent(selectedDayContainer);
			}
		});
		
		frequencyWeek.add(new AjaxEventBehavior("onclick") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				billPaymentBean.setFrequencyType(frequencyWeek.getDefaultModelObjectAsString());
				selectedDayContainer.setVisible(true);
				target.addComponent(selectedDayContainer);
			}
		});
		
		frequencyDate.add(new AjaxEventBehavior("onclick") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				billPaymentBean.setFrequencyType(frequencyDate.getDefaultModelObjectAsString());
				selectedDayContainer.setVisible(false);
				target.addComponent(selectedDayContainer);
			}
		});

		form.add(new Button("confirmButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				Date startDate = billPaymentBean.getStartDate();
				Date expiryDate = billPaymentBean.getExpiryDate();
				if (expiryDate.before(startDate)) {
					error(getLocalizer().getString("invalid.expiry.date", this));
					return;
				}
				if (addBillPayStandingInstruction(billPaymentBean)) {
					basePage.getWebSession().info(getLocalizer().getString("si.billpayment.success", this));
					setResponsePage(StandingInstructionsPage.class);
				}
			};
		});

		add(form);
	}

	/**
	 * calling addBillPayStandingInstruction service from Manage Favorite end point
	 */
	private boolean addBillPayStandingInstruction(BillPaymentBean bean) {
		
		AddSchedTxnResponse response = null;
		boolean isBillPayAdd = false;
		
		try {
			
			final AddSchedTxnRequest request = basePage
				.getNewMobiliserRequest(AddSchedTxnRequest.class);
			
			response = createAddBillPayStandingInstructionRequest(bean, request);
			if (basePage.evaluateConsumerPortalMobiliserResponse(response)) {
				isBillPayAdd = true;
			} else {
				error(response.getStatus().getValue());
			}
			
		} catch (Exception ex) {
			log.error("#An error occurred while calling addBillPayStandingInstruction service", ex);
			error(getLocalizer().getString("error.exception", this));
		}
		
		return isBillPayAdd;
	}

	/**
	 * this method will create addBillPayStandingInstruction request and return response
	 */
	private AddSchedTxnResponse createAddBillPayStandingInstructionRequest(BillPaymentBean bean,
		final AddSchedTxnRequest request) {
		
		long customerId = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId();
		request.setCustomerId(customerId);
		request.setProcessingCode("100601");
		request.setDescription(bean.getSiName());
		request.setBeneficiaryNo( bean.getSelectedBillerId().getId());
		
		request.setStartDate(PortalUtils.getSaveXMLGregorianCalendarFromDate(bean.getStartDate(), null));
		request.setFrequency(bean.getFrequencyType());
		request.setEndDate(PortalUtils.getSaveXMLGregorianCalendarToDate(bean.getExpiryDate(), null));
//		request.setCurrencyCode("IDR");
		if (PortalUtils.exists(billPaymentBean.getBaseAmount()))
			request.setAmount(billPaymentBean.getBaseAmount());
		else
			request.setAmount(0L);
		
//		MapEntryType amount = new MapEntryType();
//		amount.setKey("amount");
//		amount.setValue(topupBean.getLabel());
		
//		MapEntryType debitIdentifier = new MapEntryType();
//		debitIdentifier.setKey("debitIdentifier");
//		debitIdentifier.setValue("");
		
//		MapEntryType description = new MapEntryType();
//		description.setKey("description");
//		description.setValue(topupBean.getSiName());
		
		MapEntryType billerId = new MapEntryType();
		billerId.setKey("billerId");
		billerId.setValue(bean.getBillerId());
	
		MapEntryType productId = new MapEntryType();
		productId.setKey("productId");
		productId.setValue(bean.getProductId());
		
//		request.getParameter().add(amount);
//		request.getParameter().add(debitIdentifier);
//		request.getParameter().add(description);
		request.getParameter().add(billerId);
		request.getParameter().add(productId);
		
		request.setFlags(0);
		
		return siClient.add(request);
	}

	public String getSelectSubBiller() {
		return selectSubBiller;
	}

	public void setSelectSubBiller(String selectSubBiller) {
		this.selectSubBiller = selectSubBiller;
	}

	public String getFilterType() {
		return filterType;
	}

	public void setFilterType(String filterType) {
		this.filterType = filterType;
	}

	public String getFrequencyType() {
		return frequencyType;
	}

	public void setFrequencyType(String frequencyType) {
		this.frequencyType = frequencyType;
	}

}
