package com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.support.SetLanguageRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.support.SetLanguageResponse;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnLocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.btpn.util.BtpnUtils;

/**
 * This is the Manage Products page for bank portals.
 * 
 * @author Vikram Gunda
 */
public class ChangeLanguagePage extends BtpnBaseBankPortalSelfCarePage {

	private CodeValue language;

	private static final Logger LOG = LoggerFactory.getLogger(ChangeLanguagePage.class);

	/**
	 * Constructor for this page.
	 */
	public ChangeLanguagePage() {
		super();
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	@Override
	protected void initOwnPageComponents() {
		super.initOwnPageComponents();
		final String id = this.getMobiliserWebSession().getBtpnLoggedInCustomer().getLanguage();
		language = new CodeValue(id, BtpnUtils.getDropdownValueFromId(
			lookupMapUtility.getLookupNamesMap(BtpnConstants.RESOURCE_BUNDLE_BTPN_REG_LANG, this),
			BtpnConstants.RESOURCE_BUNDLE_BTPN_REG_LANG + "." + id));
		constructPage();

	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	private void constructPage() {
		final Form<ChangeLanguagePage> form = new Form<ChangeLanguagePage>("changeLanguageForm",
			new CompoundPropertyModel<ChangeLanguagePage>(this));
		form.add(new FeedbackPanel("errorMessages"));
		final IChoiceRenderer<CodeValue> codeValueChoiceRender = new ChoiceRenderer<CodeValue>(
			BtpnConstants.DISPLAY_EXPRESSION, BtpnConstants.ID_EXPRESSION);
		form.add(new Label("language.value"));
		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("language", CodeValue.class,
			BtpnConstants.RESOURCE_BUNDLE_BTPN_REG_LANG, this, Boolean.FALSE, true).setNullValid(false)
			.setChoiceRenderer(codeValueChoiceRender).setRequired(true).add(new ErrorIndicator()));
		form.add(addUpdateButton());
		// Add add Button
		add(form);
	}

	/**
	 * This method adds the Add button for the Manage Profile Page
	 */
	protected Button addUpdateButton() {
		Button submitButton = new Button("btnUpdate") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				handleUpdateLanguage();
			}
		};
		return submitButton;
	}

	/**
	 * This method handles the update Language.
	 */
	protected void handleUpdateLanguage() {
		try {
			final SetLanguageRequest request = new SetLanguageRequest();
			request.setCustomerId(this.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			request.setLanguage(language.getId());
			final SetLanguageResponse response = this.getSupportClient().setLanguage(request);
			if (evaluateBankPortalMobiliserResponse(response)) {
				this.getMobiliserWebSession().getBtpnLoggedInCustomer().setLanguage(language.getId());
				info(getLocalizer().getString("update.success", ChangeLanguagePage.this));
			} else {
				error(getLocalizer().getString("update.error", ChangeLanguagePage.this));
			}
		} catch (Exception e) {
			LOG.error("Error in updating Change Language ==> ", e);
			error(getLocalizer().getString("error.exception", ChangeLanguagePage.this));
		}
	}
}
