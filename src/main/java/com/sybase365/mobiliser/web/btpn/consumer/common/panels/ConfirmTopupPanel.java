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
import com.sybase365.mobiliser.web.btpn.consumer.beans.AirTimeTopupBean;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.standing.StandingInstructionsPage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnLocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.common.panels.AttachmentsPanel;
import com.sybase365.mobiliser.web.util.PortalUtils;


public class ConfirmTopupPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(ConfirmTopupPanel.class);

	protected BtpnMobiliserBasePage basePage;
	private AirTimeTopupBean topupBean;
	private FeedbackPanel feedBack;
	private String filterType;
	
	@SpringBean(name="siClient")
	private SchedTxnFacade siClient;
	

	public ConfirmTopupPanel(String id, BtpnBaseConsumerPortalSelfCarePage basePage, AirTimeTopupBean topupBean) {
		this(id, basePage, null, topupBean);
	}

	public ConfirmTopupPanel(String id, BtpnBaseConsumerPortalSelfCarePage basePage, AttachmentsPanel attachmentsPanel,
		AirTimeTopupBean topupBean) {
		super(id);
		this.basePage = basePage;
		this.topupBean = topupBean;
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
		
		final Form<ConfirmTopupPanel> form = new Form<ConfirmTopupPanel>("confirmTopupForm",
			new CompoundPropertyModel<ConfirmTopupPanel>(this));
		feedBack = new FeedbackPanel("errorMessages");
		feedBack.setOutputMarkupId(true);
		form.add(feedBack);

		final IChoiceRenderer<CodeValue> codeValueChoiceRender = new ChoiceRenderer<CodeValue>(
			BtpnConstants.DISPLAY_EXPRESSION, BtpnConstants.ID_EXPRESSION);

//		form.add(new Label("topupBean.selectedTelco.value"));
//		form.add(new Label("topupBean.denomination.value", BtpnUtils.formatAmount(
//			Long.valueOf(topupBean.getDenomination().getValue()) * 100, new Locale("en", "ID"))));
//		form.add(new Label("topupBean.phonenumber"));

		form.add(new Label("topupBean.productName"));
		form.add(new Label("topupBean.label"));
		
		form.add(new TextField<String>("topupBean.siName").setRequired(true).add(new ErrorIndicator()));

		form.add(DateTextField.forDatePattern("topupBean.startDate", BtpnConstants.ID_EXPIRY_DATE_PATTERN)
			.add(DateValidator.minimum(new Date(), BtpnConstants.ID_EXPIRY_DATE_PATTERN)).setRequired(true)
			.add(new ErrorIndicator()));

		form.add(DateTextField.forDatePattern("topupBean.expiryDate", BtpnConstants.ID_EXPIRY_DATE_PATTERN)
			.add(DateValidator.minimum(new Date(), BtpnConstants.ID_EXPIRY_DATE_PATTERN)).setRequired(true)
			.add(new ErrorIndicator()));

		RadioGroup<ConfirmTopupPanel> frequencyRadioGroup = new RadioGroup<ConfirmTopupPanel>("frequencyRadioGroup",
			new PropertyModel<ConfirmTopupPanel>(this, "topupBean.frequencyType"));
		frequencyRadioGroup.setRequired(true).add(new ErrorIndicator());

		@SuppressWarnings({ "rawtypes", "unchecked" })
		final Radio<ConfirmTopupPanel> frequencyMonth = new Radio<ConfirmTopupPanel>("frequencyMonth", new Model(
			BtpnConstants.FREQUENCY_TYPE_MONTH));
		frequencyMonth.setOutputMarkupId(true);
		frequencyRadioGroup.add(frequencyMonth);

		@SuppressWarnings({ "rawtypes", "unchecked" })
		final Radio<ConfirmTopupPanel> frequencyQuarter = new Radio<ConfirmTopupPanel>("frequencyQuarter", new Model(
			BtpnConstants.FREQUENCY_TYPE_QUARTER));
		frequencyQuarter.setOutputMarkupId(true);
		frequencyRadioGroup.add(frequencyQuarter);

		@SuppressWarnings({ "rawtypes", "unchecked" })
		final Radio<ConfirmTopupPanel> frequencyWeek = new Radio<ConfirmTopupPanel>("frequencyWeek", new Model(
			BtpnConstants.FREQUENCY_TYPE_WEEK));
		frequencyWeek.setOutputMarkupId(true);
		frequencyRadioGroup.add(frequencyWeek);

		@SuppressWarnings({ "rawtypes", "unchecked" })
		final Radio<ConfirmTopupPanel> frequencyDate = new Radio<ConfirmTopupPanel>("frequencyDate", new Model(
			BtpnConstants.FREQUENCY_TYPE_DAILY));
		frequencyDate.setOutputMarkupId(true);
		frequencyRadioGroup.add(frequencyDate);

		form.add(frequencyRadioGroup);

		final WebMarkupContainer selectedDayContainer = new WebMarkupContainer("selectedDayContainer");
		selectedDayContainer.setOutputMarkupId(true);
		selectedDayContainer.setOutputMarkupPlaceholderTag(true);

		selectedDayContainer.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("topupBean.selectedDay",
			CodeValue.class, BtpnConstants.RESOURCE_BUBDLE_SELECTED_DAY, this, true, false).setNullValid(false)
			.setChoiceRenderer(codeValueChoiceRender).setRequired(true).add(new ErrorIndicator()));
		selectedDayContainer.setVisible(true);

		form.add(selectedDayContainer);

		frequencyMonth.add(new AjaxEventBehavior("onclick") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				String selectedType = frequencyMonth.getDefaultModelObjectAsString();
				log.info("Selected Frequency Type : " + selectedType);
				topupBean.setFrequencyType(selectedType);
				selectedDayContainer.setVisible(false);
				target.addComponent(selectedDayContainer);
			}
		});
		
		frequencyQuarter.add(new AjaxEventBehavior("onclick") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				String selectedType = frequencyQuarter.getDefaultModelObjectAsString();
				log.info("Selected Frequency Type : " + selectedType);
				topupBean.setFrequencyType(selectedType);
				selectedDayContainer.setVisible(false);
				target.addComponent(selectedDayContainer);
			}
		});
		
		frequencyWeek.add(new AjaxEventBehavior("onclick") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				String selectedType = frequencyWeek.getDefaultModelObjectAsString();
				log.info("Selected Frequency Type : " + selectedType);
				topupBean.setFrequencyType(selectedType);
				selectedDayContainer.setVisible(true);
				target.addComponent(selectedDayContainer);
			}
		});
		
		frequencyDate.add(new AjaxEventBehavior("onclick") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				String selectedType = frequencyDate.getDefaultModelObjectAsString();
				log.info("Selected Frequency Type : " + selectedType);
				topupBean.setFrequencyType(selectedType);
				selectedDayContainer.setVisible(false);
				target.addComponent(selectedDayContainer);
			}
		});

		form.add(new Button("confirmButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				Date startDate = topupBean.getStartDate();
				Date expiryDate = topupBean.getExpiryDate();
				if (expiryDate.before(startDate)) {
					error(getLocalizer().getString("invalid.expiry.date", this));
					return;
				}
				if (addTopUpStandingInstruction(topupBean)) {
					basePage.getWebSession().info(getLocalizer().getString("si.topup.add.successMessage", this));
					setResponsePage(new StandingInstructionsPage());
				}
			};

		});

		add(form);
	}

	
	private boolean addTopUpStandingInstruction(AirTimeTopupBean topupBean) {
		
		AddSchedTxnResponse response = null;
		boolean isTopupAdded = false;
		
		try {
			
			final AddSchedTxnRequest request = basePage
				.getNewMobiliserRequest(AddSchedTxnRequest.class);
			
			response = createAddTopUpStandingInstructionRequest(topupBean, request);
			if (basePage.evaluateConsumerPortalMobiliserResponse(response)
					&& response.getStatus().getCode() == BtpnConstants.STATUS_CODE) {
				isTopupAdded = true;
			} else {
				error(response.getStatus().getValue());
			}
			
		} catch (Exception ex) {
			log.error("#An error occurred while calling addTopUpStandingInstruction service", ex);
			error(getLocalizer().getString("error.exception", this));
		}
		
		return isTopupAdded;
	}

	
	
	private AddSchedTxnResponse createAddTopUpStandingInstructionRequest(AirTimeTopupBean topupBean,
		final AddSchedTxnRequest request) {
		
		long customerId = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId();
		
		request.setCustomerId(customerId);
		request.setProcessingCode("100501");
		request.setDescription(topupBean.getSiName());
//		request.setBeneficiaryNo(topupBean.getSelectedMsisdn().getValue());
		request.setBeneficiaryNo(PortalUtils.exists(topupBean.getSelectedMsisdn()) ? topupBean.getSelectedMsisdn().getId() : topupBean.getFavoriteNumber().getId()); 
		request.setStartDate(PortalUtils.getSaveXMLGregorianCalendarFromDate(topupBean.getStartDate(), null));
		request.setFrequency(topupBean.getFrequencyType());
		request.setEndDate(PortalUtils.getSaveXMLGregorianCalendarToDate(topupBean.getExpiryDate(), null));
//		request.setCurrencyCode("IDR");
		request.setAmount(Long.valueOf(topupBean.getLabel()));
		
//		MapEntryType amount = new MapEntryType();
//		amount.setKey("amount");
//		amount.setValue(topupBean.getLabel());
		
//		MapEntryType debitIdentifier = new MapEntryType();
//		debitIdentifier.setKey("debitIdentifier");
//		debitIdentifier.setValue("");
		
		MapEntryType amountDenom = new MapEntryType();
		amountDenom.setKey("amountDenom");
		amountDenom.setValue(topupBean.getLabel());
		
//		MapEntryType description = new MapEntryType();
//		description.setKey("description");
//		description.setValue(topupBean.getSiName());
		
		MapEntryType billerId = new MapEntryType();
		billerId.setKey("billerId");
		billerId.setValue(topupBean.getBillerId());
	
		MapEntryType productId = new MapEntryType();
		productId.setKey("productId");
		productId.setValue(topupBean.getProductId());
	
		MapEntryType productName = new MapEntryType();
		productName.setKey("productName");
		productName.setValue(topupBean.getProductName());
		
//		request.getParameter().add(amount);
//		request.getParameter().add(debitIdentifier);
		request.getParameter().add(amountDenom);
//		request.getParameter().add(description);
		request.getParameter().add(billerId);
		request.getParameter().add(productId);
		request.getParameter().add(productName);
		
		request.setFlags(0);
		
		return siClient.add(request);
		
	}

	public String getFilterType() {
		return filterType;
	}

	public void setFilterType(String filterType) {
		this.filterType = filterType;
	}

}
