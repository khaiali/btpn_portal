package com.sybase365.mobiliser.web.btpn.consumer.common.panels;


import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
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
import org.apache.wicket.validation.validator.PatternValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.common.components.AmountTextField;
import com.sybase365.mobiliser.web.btpn.common.components.FavouriteDropdownChoice;
import com.sybase365.mobiliser.web.btpn.consumer.beans.FundTransferBean;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.standing.ConfirmFundTransferPage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnLocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;



public class StandingInstructionOtherAccountsPanel extends Panel {

	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(StandingInstructionOtherAccountsPanel.class);

	protected BtpnMobiliserBasePage basePage;
	protected FundTransferBean fundTransfer;
	private FeedbackPanel feedBack;
	
	private String filterType = BtpnConstants.FILTERTYPE_MANUAL;
	private int favoriteType = 0;
	
	String ftAccountType;
	String headerMessage = "";
	
	public StandingInstructionOtherAccountsPanel(String id, BtpnBaseConsumerPortalSelfCarePage basePage,
		FundTransferBean fundTransferBean) {
		super(id);
		this.basePage = basePage;
		this.fundTransfer = fundTransferBean;
		this.ftAccountType = fundTransfer.getAccountType().getId();
		constructPanel();
	}

	protected void constructPanel() {

		final Form<StandingInstructionOtherAccountsPanel> form = new Form<StandingInstructionOtherAccountsPanel>(
			"fundTransferBtpnBankForm", new CompoundPropertyModel<StandingInstructionOtherAccountsPanel>(this));

		// Add feedback panel for Error Messages
		feedBack = new FeedbackPanel("errorMessages");
		feedBack.setOutputMarkupId(true);
		feedBack.setOutputMarkupPlaceholderTag(true);
		form.add(feedBack);

		final WebMarkupContainer bankCodeDiv = new WebMarkupContainer("bankCodeDiv");
		bankCodeDiv.setOutputMarkupId(true);
		bankCodeDiv.setOutputMarkupPlaceholderTag(true);

		final IChoiceRenderer<CodeValue> codeValueChoiceRender = new ChoiceRenderer<CodeValue>(
			BtpnConstants.DISPLAY_EXPRESSION, BtpnConstants.ID_EXPRESSION);

		bankCodeDiv.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("fundTransfer.bankCode", CodeValue.class,
				BtpnConstants.RESOURCE_BUBDLE_FT_TO_OTHER_BANK_CODES, this, Boolean.FALSE, Boolean.TRUE).setNullValid(false)
				.setChoiceRenderer(codeValueChoiceRender).setRequired(true).add(new ErrorIndicator()));
		
		form.add(bankCodeDiv);

		if (PortalUtils.exists(ftAccountType)) {
			if (ftAccountType.equals(BtpnConstants.FT_ACCOUNT_TYPE_BTPN_ACCOUNT)) {
				headerMessage = StandingInstructionOtherAccountsPanel.this.getLocalizer().getString(
					"header.fundTransferBtpnBankAccount", StandingInstructionOtherAccountsPanel.this);
				bankCodeDiv.setVisible(Boolean.FALSE);
				favoriteType = BtpnConstants.FAVOURITE_TYPE_BTPN_ACCOUNT;
			} else if (ftAccountType.equals(BtpnConstants.FT_ACCOUNT_TYPE_OTHER_BANK_ACCOUNT)) {
				headerMessage = StandingInstructionOtherAccountsPanel.this.getLocalizer().getString(
					"header.fundTransferOtherBankAccount", StandingInstructionOtherAccountsPanel.this);
				bankCodeDiv.setVisible(Boolean.TRUE);
				favoriteType = BtpnConstants.FAVOURITE_TYPE_OTHER_BANK_ACCOUNT;
			}
		}

		form.add(new Label("fundTransferHeaderMessage", headerMessage));

		RadioGroup<StandingInstructionOtherAccountsPanel> radioGroup = new RadioGroup<StandingInstructionOtherAccountsPanel>(
			"radioGroup", new PropertyModel<StandingInstructionOtherAccountsPanel>(this, "filterType"));

		@SuppressWarnings({ "rawtypes", "unchecked" })
		Radio<StandingInstructionOtherAccountsPanel> enterManually = new Radio<StandingInstructionOtherAccountsPanel>(
			"EnterManually", new Model(BtpnConstants.FILTERTYPE_MANUAL));
		enterManually.setOutputMarkupId(true).setMarkupId("EnterManually");
		radioGroup.add(enterManually);

		@SuppressWarnings({ "rawtypes", "unchecked" })
		Radio<StandingInstructionOtherAccountsPanel> favoriteList = new Radio<StandingInstructionOtherAccountsPanel>(
			"FavoriteList", new Model(BtpnConstants.FILTERTYPE_FAVORITE));
		favoriteList.setOutputMarkupId(true).setMarkupId("FavoriteList");
		radioGroup.add(favoriteList);

		form.add(radioGroup);

		final WebMarkupContainer enterManualllyDiv = new WebMarkupContainer("enterManualllyDiv");
		enterManualllyDiv.setOutputMarkupId(true);
		enterManualllyDiv.setOutputMarkupPlaceholderTag(true);

		enterManualllyDiv.add(new TextField<String>("fundTransfer.accountNo").add(new PatternValidator("^[0-9]*$"))
			.setRequired(true).add(new ErrorIndicator()));
		
		final WebMarkupContainer selectFromFavoriteDiv = new WebMarkupContainer("selectFromFavoriteDiv");
		
		if (PortalUtils.exists(ftAccountType)) {
			if (ftAccountType.equals(BtpnConstants.FT_ACCOUNT_TYPE_BTPN_ACCOUNT)) {
				selectFromFavoriteDiv.setOutputMarkupId(true);
				selectFromFavoriteDiv.setOutputMarkupPlaceholderTag(true);
				selectFromFavoriteDiv.add(new FavouriteDropdownChoice("fundTransfer.favoriteNum", false, true, 
					basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId(), favoriteType).setNullValid(false)
					.setRequired(true).add(new ErrorIndicator()));
				selectFromFavoriteDiv.setVisible(false);
			} else {
				selectFromFavoriteDiv.setOutputMarkupId(true);
				selectFromFavoriteDiv.setOutputMarkupPlaceholderTag(true);
				selectFromFavoriteDiv.add(new FavouriteDropdownChoice("fundTransfer.favoriteNum", false, true, 
					basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId(), favoriteType).setNullValid(false)
					.setRequired(true).add(new ErrorIndicator()));
				selectFromFavoriteDiv.setVisible(false);
			}
		}
		
		form.add(enterManualllyDiv);
		form.add(selectFromFavoriteDiv);

		enterManually.add(new AjaxEventBehavior("onclick") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				log.info("Selecetd Type : " + filterType);
				selectFromFavoriteDiv.setVisible(false);
				target.addComponent(selectFromFavoriteDiv);
				enterManualllyDiv.setVisible(true);
				target.addComponent(enterManualllyDiv);
			}
		});

		favoriteList.add(new AjaxEventBehavior("onclick") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				selectFromFavoriteDiv.setVisible(true);
				target.addComponent(selectFromFavoriteDiv);
				enterManualllyDiv.setVisible(false);
				target.addComponent(enterManualllyDiv);
			}
		});

		form.add(new TextField<String>("fundTransfer.beneficiaryName").add(Constants.mediumStringValidator)
			.add(Constants.mediumSimpleAttributeModifier).add(new ErrorIndicator()));

		form.add(new AmountTextField<Long>("fundTransfer.amount", Long.class, false).setRequired(true).add(
			new ErrorIndicator()));

		form.add(new Button("submitButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				if (ftAccountType.equals(BtpnConstants.FT_ACCOUNT_TYPE_BTPN_ACCOUNT)
						|| ftAccountType.equals(BtpnConstants.FT_ACCOUNT_TYPE_OTHER_BANK_ACCOUNT)) {
					setResponsePage(new ConfirmFundTransferPage(fundTransfer, ftAccountType));
				}
			};
		});

		add(form);
	}

}
