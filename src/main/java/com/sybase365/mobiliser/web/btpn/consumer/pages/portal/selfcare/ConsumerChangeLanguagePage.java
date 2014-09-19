package com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare;

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

public class ConsumerChangeLanguagePage extends BtpnBaseConsumerPortalSelfCarePage {

	private static final Logger log = LoggerFactory.getLogger(ConsumerChangeLanguagePage.class);
	
	private CodeValue language;
	
	@SpringBean(name = "customerClient")
	private CustomerFacade customerClient;
	
	/**
	 * Constructor for this page.
	 */
	public ConsumerChangeLanguagePage() {
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
		
		final Form<ConsumerChangeLanguagePage> form = new Form<ConsumerChangeLanguagePage>("changeLanguageForm",
			new CompoundPropertyModel<ConsumerChangeLanguagePage>(this));
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
			
			log.info(" ### (ConsumerChangeLanguagePage::handleUpdateLanguage) ### ");
			String userName = getMobiliserWebSession().getBtpnLoggedInCustomer().getUsername();
			log.info(" ### (ConsumerChangeLanguagePage::handleUpdateLanguage) USER NAME ### "+userName);
			
//			final GetCustomerExRequest request = new GetCustomerExRequest();
//			CustomerIdentificationType obj = new CustomerIdentificationType();
//			obj.setType(0);
//			obj.setValue(userName);
//			
//			request.setFlags(0);
//			request.setIdentification(obj);
//			
//			final GetCustomerExResponse response = customerClient.get(request);
//			log.info(" ### (ViewCustomerProfile::getCustomerDetailsByCustomerId) RESPONSE ### " +response.getStatus().getCode());
//			log.info(" ### (ViewCustomerProfile::getCustomerDetailsByCustomerId) ID ### " +response.getInformation().getId());
//			log.info(" ### (ViewCustomerProfile::getCustomerDetailsByCustomerId) LANGUAGE ### " +response.getInformation().getLanguage());
//			if (response.getInformation().getId()!=null && response.getInformation().getLanguage()!=null) {
				final UpdateCustomerExRequest req = new UpdateCustomerExRequest();
				CustomerInformationType cit = new CustomerInformationType();
				cit.setId(getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
				cit.setLanguage(language.getId());
					
				req.setInformation(cit);
				UpdateCustomerExResponse response2 = customerClient.update(req);
				log.info(" ### (ViewCustomerProfile::getCustomerDetailsByCustomerId) RESPONSE 2 ### " +response2.getStatus().getCode());
				if (evaluateBankPortalMobiliserResponse(response2)) {
					getMobiliserWebSession().getBtpnLoggedInCustomer().setLanguage(language.getId());
					info(getLocalizer().getString("update.success", ConsumerChangeLanguagePage.this));
				} else {
					error(getLocalizer().getString("update.error", ConsumerChangeLanguagePage.this));
				}
//			} else {
//				error(getLocalizer().getString("update.error", ConsumerChangeLanguagePage.this));
//			}
			
		} catch (Exception e) {
			log.error("Error in updating Change Language ==> ", e);
			error(getLocalizer().getString("error.exception", ConsumerChangeLanguagePage.this));
		}
	}
	
}
