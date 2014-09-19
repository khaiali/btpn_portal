package com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.support.GetHelpPageContentRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.support.GetHelpPageContentResponse;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;

/**
 * This is the Manage Products page for bank portals.
 * 
 * @author Vikram Gunda
 */
public class ConsumerViewHelpPage extends BtpnBaseConsumerPortalSelfCarePage {

	private String consumerPortalText;

	private static final Logger LOG = LoggerFactory.getLogger(ConsumerViewHelpPage.class);

	/**
	 * Constructor for this page.
	 */
	public ConsumerViewHelpPage() {
		super();
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	@Override
	protected void initOwnPageComponents() {
		super.initOwnPageComponents();
		fetchAddHelpText();
		constructPage();
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	private void constructPage() {
		final Form<ConsumerViewHelpPage> viewHelpForm = new Form<ConsumerViewHelpPage>("formHelp",
			new CompoundPropertyModel<ConsumerViewHelpPage>(this));
		viewHelpForm.add(new FeedbackPanel("errorMessages"));
		viewHelpForm.add(new TextArea<String>("consumerPortalText"));
		add(viewHelpForm);
	}

	/**
	 * This method fetches the help text
	 */
	protected void fetchAddHelpText() {
		final GetHelpPageContentRequest request = new GetHelpPageContentRequest();
		request.setPortalType(BtpnConstants.PORTAL_CONSUMER);
		try {
			final GetHelpPageContentResponse response = this.getSupportClient().getHelpPageContent(request);
			if (evaluateBankPortalMobiliserResponse(response)) {
				consumerPortalText = response.getHelpPageContent();
			} else {
				error(getLocalizer().getString("fetch.error", this));
			}
		} catch (Exception e) {
			LOG.error("Exception occured while fetching help ==> ", e);
			error(getLocalizer().getString("error.exception", this));
		}
	}
}
