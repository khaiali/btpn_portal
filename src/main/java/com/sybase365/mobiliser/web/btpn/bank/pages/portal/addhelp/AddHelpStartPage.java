package com.sybase365.mobiliser.web.btpn.bank.pages.portal.addhelp;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnLocalizableLookupDropDownChoice;

/**
 * This is the Manage Products page for bank portals.
 * 
 * @author Vikram Gunda
 */
public class AddHelpStartPage extends BtpnBaseBankPortalSelfCarePage {

	
	private CodeValue portalType;
	
	private String helpText;
	
	/**
	 * Constructor for this page.
	 */
	public AddHelpStartPage() {
		super();
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	@Override
	protected void initOwnPageComponents() {
		super.initOwnPageComponents();
		constructPage();
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	private void constructPage() {
		final Form<AddHelpStartPage> form = new Form<AddHelpStartPage>("addHelpForm",
			new CompoundPropertyModel<AddHelpStartPage>(this));
		form.add(new FeedbackPanel("errorMessages"));
		final IChoiceRenderer<CodeValue> codeValueChoiceRender = new ChoiceRenderer<CodeValue>(
				BtpnConstants.DISPLAY_EXPRESSION, BtpnConstants.ID_EXPRESSION);
		// Add the Portal Add Help dropdown
		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("portalType", CodeValue.class,
			BtpnConstants.RESOURCE_BUNDLE_PORTALS_ADD_HELP, this)
			.setChoiceRenderer(codeValueChoiceRender).setRequired(true).add(new ErrorIndicator()));
		form.add(new TextArea<String>("helpText").setRequired(true).add(new ErrorIndicator()));
		form.add(addUpdateButton());
		add(form);
	}

	/**
	 * This method adds the Add button for the Manage Profile Page
	 */
	protected Button addUpdateButton() {
		Button submitButton = new Button("btnAdd") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(new AddHelpConfirmPage(portalType, helpText));
			}
		};
		return submitButton;
	}
}
