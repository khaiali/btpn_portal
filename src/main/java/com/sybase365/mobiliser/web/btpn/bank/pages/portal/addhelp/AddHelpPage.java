package com.sybase365.mobiliser.web.btpn.bank.pages.portal.addhelp;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.support.GetHelpPageContentRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.support.GetHelpPageContentResponse;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;

/**
 * This is the Manage Products page for bank portals.
 * 
 * @author Vikram Gunda
 */
public class AddHelpPage extends BtpnBaseBankPortalSelfCarePage {

	private String consumerPortalText;

	private String agentPortalText;

	private static final Logger LOG = LoggerFactory.getLogger(AddHelpPage.class);

	/**
	 * Constructor for this page.
	 */
	public AddHelpPage() {
		super();
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	@Override
	protected void initOwnPageComponents() {
		super.initOwnPageComponents();
		fetchAddHelpText(true);
		fetchAddHelpText(false);
		constructPage();
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	private void constructPage() {
		final Form<AddHelpPage> form = new Form<AddHelpPage>("addHelpForm",
			new CompoundPropertyModel<AddHelpPage>(this));
		form.add(new FeedbackPanel("errorMessages"));
		form.add(new TextArea<String>("consumerPortalText"));
		form.add(new TextArea<String>("agentPortalText"));
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
				setResponsePage(AddHelpStartPage.class);
			}
		};
		return submitButton;
	}

	/**
	 * This method fetches the help text
	 */
	protected void fetchAddHelpText(boolean isConsumerPortal) {
		final GetHelpPageContentRequest request = new GetHelpPageContentRequest();
		request.setPortalType(isConsumerPortal ? BtpnConstants.PORTAL_CONSUMER : BtpnConstants.PORTAL_AGENT);
		try {
			final GetHelpPageContentResponse response = this.getSupportClient().getHelpPageContent(request);
			if (evaluateBankPortalMobiliserResponse(response)) {
				if (isConsumerPortal) {
					consumerPortalText = response.getHelpPageContent();
				} else {
					agentPortalText = response.getHelpPageContent();
				}
			} else {
				error(getLocalizer().getString("fetch.error", this));
			}
		} catch (Exception e) {
			LOG.error("Exception occured while fetching help ==> ", e);
			error(getLocalizer().getString("error.exception", this));
		}
	}
}
