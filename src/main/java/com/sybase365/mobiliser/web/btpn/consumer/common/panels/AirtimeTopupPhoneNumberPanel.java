package com.sybase365.mobiliser.web.btpn.consumer.common.panels;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
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

import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.common.components.FavouriteDropdownChoice;
import com.sybase365.mobiliser.web.btpn.consumer.beans.AirTimeTopupBean;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.standing.TopupDenominationsPage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnLocalizableLookupDropDownChoice;

/**
 * This is the AirtimeTopupPhoneNumberPanel page for consumer portals.
 * 
 * @author Narasa Reddy
 */
public class AirtimeTopupPhoneNumberPanel extends Panel {

	private static final long serialVersionUID = 1L;

	protected BtpnMobiliserBasePage basePage;

	protected AirTimeTopupBean topupBean;

	String filterType = BtpnConstants.FILTERTYPE_MANUAL;

	public AirtimeTopupPhoneNumberPanel(String id, BtpnBaseConsumerPortalSelfCarePage basePage,
		AirTimeTopupBean topupBean) {
		super(id);
		this.basePage = basePage;
		this.topupBean = topupBean;
		constructPanel();
	}

	protected void constructPanel() {

		final Form<AirtimeTopupPhoneNumberPanel> form = new Form<AirtimeTopupPhoneNumberPanel>("topupPhoneNumberForm",
			new CompoundPropertyModel<AirtimeTopupPhoneNumberPanel>(this));

		// Add feedback panel for Error Messages
		form.add(new FeedbackPanel("errorMessages"));

		RadioGroup<AirtimeTopupPhoneNumberPanel> radioGroup = new RadioGroup<AirtimeTopupPhoneNumberPanel>(
			"radioGroup", new PropertyModel<AirtimeTopupPhoneNumberPanel>(this, "filterType"));

		@SuppressWarnings({ "rawtypes", "unchecked" })
		Radio<AirtimeTopupPhoneNumberPanel> enterManually = new Radio<AirtimeTopupPhoneNumberPanel>("EnterManually",
			new Model(BtpnConstants.FILTERTYPE_MANUAL));
		enterManually.setOutputMarkupId(true).setMarkupId("EnterManually");
		radioGroup.add(enterManually);

		@SuppressWarnings({ "rawtypes", "unchecked" })
		Radio<AirtimeTopupPhoneNumberPanel> favoriteList = new Radio<AirtimeTopupPhoneNumberPanel>("FavoriteList",
			new Model(BtpnConstants.FILTERTYPE_FAVORITE));
		favoriteList.setOutputMarkupId(true).setMarkupId("FavoriteList");
		radioGroup.add(favoriteList);

		form.add(radioGroup);

		final WebMarkupContainer enterManualllyDiv = new WebMarkupContainer("enterManualllyDiv");
		enterManualllyDiv.setOutputMarkupId(true);
		enterManualllyDiv.setOutputMarkupPlaceholderTag(true);

		enterManualllyDiv.add(new TextField<String>("topupBean.phonenumber").setRequired(true)
			.add(new ErrorIndicator()));

		final WebMarkupContainer selectFromFavoriteDiv = new WebMarkupContainer("selectFromFavoriteDiv");
		selectFromFavoriteDiv.setOutputMarkupId(true);
		selectFromFavoriteDiv.setOutputMarkupPlaceholderTag(true);

		// Add the Sub Biller Type Drop down for consumer Portal
//		selectFromFavoriteDiv.add(new FavouriteDropdownChoice("topupBean.favoriteNumber", false, true,
//			BtpnConstants.USECASE_AIR_TIME_TOPUP, this.basePage.getMobiliserWebSession().getBtpnLoggedInCustomer()
//				.getCustomerId()).setNullValid(false).setRequired(true).add(new ErrorIndicator()));
//		selectFromFavoriteDiv.setVisible(false);
		
		selectFromFavoriteDiv.add(new FavouriteDropdownChoice("topupBean.favoriteNumber", false, true,
				this.basePage.getMobiliserWebSession().getBtpnLoggedInCustomer()
					.getCustomerId(), 4).setNullValid(false).setRequired(true).add(new ErrorIndicator()));
			selectFromFavoriteDiv.setVisible(false);

		form.add(enterManualllyDiv);
		form.add(selectFromFavoriteDiv);

		enterManually.add(new AjaxEventBehavior("onclick") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
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

		form.add(new Button("submitButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(new TopupDenominationsPage(topupBean));
			};

		});

		add(form);
	}

}
