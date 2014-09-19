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
import com.sybase365.mobiliser.web.btpn.common.components.BillPayFavouriteDropdownChoice;
import com.sybase365.mobiliser.web.btpn.common.components.SubBillerCodeDropdownChoice;
import com.sybase365.mobiliser.web.btpn.consumer.beans.BillPaymentBean;
import com.sybase365.mobiliser.web.btpn.consumer.beans.BillPaymentPerformBean;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.standing.ConfirmBillPaymentPage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;


public class BillPaymentDetailsPanel extends Panel {

	private static final long serialVersionUID = 1L;

	protected BtpnMobiliserBasePage basePage;

	protected BillPaymentPerformBean billPaymentBean;

	String filterType = BtpnConstants.FILTERTYPE_MANUAL;

	private String selectSubBiller;

	public BillPaymentDetailsPanel(String id, BtpnBaseConsumerPortalSelfCarePage basePage,
		BillPaymentPerformBean billPaymentBean) {
		super(id);
		this.basePage = basePage;
		this.billPaymentBean = billPaymentBean;
		constructPanel();
	}

	protected void constructPanel() {

		final Form<BillPaymentDetailsPanel> form = new Form<BillPaymentDetailsPanel>("billPaymentDetailsForm",
			new CompoundPropertyModel<BillPaymentDetailsPanel>(this));

		// Add feedback panel for Error Messages
		form.add(new FeedbackPanel("errorMessages"));

		final IChoiceRenderer<CodeValue> codeValueChoiceRender = new ChoiceRenderer<CodeValue>(
			BtpnConstants.DISPLAY_EXPRESSION, BtpnConstants.ID_EXPRESSION);

		// Add the Sub Biller Type Drop down for consumer Portal
//		form.add(new SubBillerCodeDropdownChoice("billPaymentBean.subBillerType", false, false, billPaymentBean
//			.getBillerType().getId()).setRequired(true).add(new ErrorIndicator()));

		RadioGroup<BillPaymentDetailsPanel> radioGroup = new RadioGroup<BillPaymentDetailsPanel>("radioGroup",
			new PropertyModel<BillPaymentDetailsPanel>(this, "filterType"));

		@SuppressWarnings({ "rawtypes", "unchecked" })
		Radio<BillPaymentDetailsPanel> enterManually = new Radio<BillPaymentDetailsPanel>("EnterManually", new Model(
			BtpnConstants.FILTERTYPE_MANUAL));
		enterManually.setOutputMarkupId(true).setMarkupId("EnterManually");
		radioGroup.add(enterManually);

		@SuppressWarnings({ "rawtypes", "unchecked" })
		Radio<BillPaymentDetailsPanel> favoriteList = new Radio<BillPaymentDetailsPanel>("FavoriteList", new Model(
			BtpnConstants.FILTERTYPE_FAVORITE));
		favoriteList.setOutputMarkupId(true).setMarkupId("FavoriteList");
		radioGroup.add(favoriteList);

		form.add(radioGroup);

		final WebMarkupContainer phoneNumberDiv = new WebMarkupContainer("phoneNumberDiv");
		phoneNumberDiv.setOutputMarkupId(true);
		phoneNumberDiv.setOutputMarkupPlaceholderTag(true);
		phoneNumberDiv.setVisible(false);

		final WebMarkupContainer billNumberDiv = new WebMarkupContainer("billNumberDiv");
		billNumberDiv.setOutputMarkupId(true);
		billNumberDiv.setOutputMarkupPlaceholderTag(true);
		billNumberDiv.setVisible(true);

		phoneNumberDiv.add(new TextField<String>("billPaymentBean.phonenumber").setRequired(true).add(
			new ErrorIndicator()));

		billNumberDiv.add(new TextField<String>("billPaymentBean.billNumber").setRequired(true).add(
			new ErrorIndicator()));

		if (billPaymentBean.getBillerType().getId().equals(BtpnConstants.SI_BILLER_TYPE_TELCO)) {
			phoneNumberDiv.setVisible(true);
			billNumberDiv.setVisible(false);
		} else {
			phoneNumberDiv.setVisible(false);
			billNumberDiv.setVisible(true);
		}

		final WebMarkupContainer selectFromFavoriteDiv = new WebMarkupContainer("selectFromFavoriteDiv");
		selectFromFavoriteDiv.setOutputMarkupId(true);
		selectFromFavoriteDiv.setOutputMarkupPlaceholderTag(true);

		// Add the Sub Biller Type Drop down for consumer Portal
		selectFromFavoriteDiv.add(new BillPayFavouriteDropdownChoice("billPaymentBean.favoriteNumber", false, true,
			this.basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId()).setNullValid(false).add(
			new ErrorIndicator()));
		selectFromFavoriteDiv.setVisible(false);

		form.add(phoneNumberDiv);
		form.add(billNumberDiv);
		form.add(selectFromFavoriteDiv);

		enterManually.add(new AjaxEventBehavior("onclick") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				selectFromFavoriteDiv.setVisible(false);
				target.addComponent(selectFromFavoriteDiv);
				if (billPaymentBean.getBillerType().getId().equals(BtpnConstants.SI_BILLER_TYPE_TELCO)) {
					phoneNumberDiv.setVisible(true);
					billNumberDiv.setVisible(false);
				} else {
					phoneNumberDiv.setVisible(false);
					billNumberDiv.setVisible(true);
				}
				target.addComponent(billNumberDiv);
				target.addComponent(phoneNumberDiv);
			}
		});

		favoriteList.add(new AjaxEventBehavior("onclick") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				selectFromFavoriteDiv.setVisible(true);
				target.addComponent(selectFromFavoriteDiv);
				phoneNumberDiv.setVisible(false);
				billNumberDiv.setVisible(false);
				target.addComponent(billNumberDiv);
				target.addComponent(phoneNumberDiv);
			}
		});

		form.add(new Button("submitButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(new ConfirmBillPaymentPage(billPaymentBean));
			};

		});

		add(form);
	}

	public String getSelectSubBiller() {
		return selectSubBiller;
	}

	public void setSelectSubBiller(String selectSubBiller) {
		this.selectSubBiller = selectSubBiller;
	}

}
