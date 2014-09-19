package com.sybase365.mobiliser.web.btpn.consumer.common.panels;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.consumer.beans.ManageFavoritesBean;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.AddFavoriteConfirmPage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnLocalizableLookupDropDownChoice;

/**
 * This is the AddFavoritePanel page for consumer portals.
 * 
 * @author Narasa Reddy
 */
public class AddFavoritePanel extends Panel {

	private static final long serialVersionUID = 1L;

	protected BtpnMobiliserBasePage basePage;

	ManageFavoritesBean favoritesBean;

	public AddFavoritePanel(String id, BtpnMobiliserBasePage basePage) {
		super(id);
		this.basePage = basePage;
		constructPanel();
	}

	protected void constructPanel() {
		final Form<AddFavoritePanel> form = new Form<AddFavoritePanel>("addFavoriteForm",
			new CompoundPropertyModel<AddFavoritePanel>(this));
		form.add(new FeedbackPanel("errorMessages"));

		// Add name to form
		form.add(new TextField<String>("favoritesBean.favoriteName").setRequired(true).add(new ErrorIndicator()));

		final IChoiceRenderer<CodeValue> codeValueChoiceRender = new ChoiceRenderer<CodeValue>(
			BtpnConstants.DISPLAY_EXPRESSION, BtpnConstants.ID_EXPRESSION);

		// Add type to form
		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("favoritesBean.favoritesType", CodeValue.class,
			BtpnConstants.RESOURCE_BUBDLE_MANAGE_FAVORITE_TYPES, this, Boolean.FALSE, true).setNullValid(false)
			.setChoiceRenderer(codeValueChoiceRender).setRequired(true).add(new ErrorIndicator()));

		// Add value to form
		form.add(new TextField<String>("favoritesBean.favoriteValue").setRequired(true).add(new ErrorIndicator()));

		// create a Text area field for Description
		form.add(new TextArea<String>("favoritesBean.description").setRequired(true).add(new ErrorIndicator()));

		form.add(new Button("addFavoriteButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(new AddFavoriteConfirmPage(favoritesBean));
			}
		});
		add(form);
	}
}
