package com.sybase365.mobiliser.web.btpn.agent.pages.portal.selfcare;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.core.customer.facade.api.CustomerFacade;
import com.btpnwow.core.customer.facade.contract.CustomerInformationType;
import com.btpnwow.core.customer.facade.contract.UpdateCustomerExRequest;
import com.btpnwow.core.customer.facade.contract.UpdateCustomerExResponse;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnLocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.btpn.util.BtpnUtils;


public class AgentChangeLanguagePage extends BtpnBaseAgentPortalSelfCarePage {

	private static final Logger log = LoggerFactory.getLogger(AgentChangeLanguagePage.class);

	private CodeValue language;
	
	@SpringBean(name = "customerClient")
	private CustomerFacade customerClient;
	
	/**
	 * Constructor for this page.
	 */
	public AgentChangeLanguagePage() {
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
		
		final Form<AgentChangeLanguagePage> form = new Form<AgentChangeLanguagePage>("changeLanguageForm",
			new CompoundPropertyModel<AgentChangeLanguagePage>(this));
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
	
	
	protected void handleUpdateLanguage() {

		try {

			log.info(" ### (ConsumerChangeLanguagePage::handleUpdateLanguage) ### ");
			
			final UpdateCustomerExRequest req = new UpdateCustomerExRequest();
			CustomerInformationType cit = new CustomerInformationType();
			cit.setId(getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			cit.setLanguage(language.getId());

			req.setInformation(cit);
			UpdateCustomerExResponse response = customerClient.update(req);
			log.info(" ### (ViewCustomerProfile::getCustomerDetailsByCustomerId) RESPONSE ### " + response.getStatus().getCode());
			if (evaluateBankPortalMobiliserResponse(response)) {
				getMobiliserWebSession().getBtpnLoggedInCustomer().setLanguage(language.getId());
				info(getLocalizer().getString("update.success", AgentChangeLanguagePage.this));
			} else {
				error(getLocalizer().getString("update.error", AgentChangeLanguagePage.this));
			}

		} catch (Exception e) {
			log.error("Error in updating Change Language ==> ", e);
			error(getLocalizer().getString("error.exception",
					AgentChangeLanguagePage.this));
		}
	}
	
}
