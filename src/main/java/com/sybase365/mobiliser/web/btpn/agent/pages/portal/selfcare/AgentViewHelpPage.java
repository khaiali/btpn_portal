package com.sybase365.mobiliser.web.btpn.agent.pages.portal.selfcare;

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
public class AgentViewHelpPage extends BtpnBaseAgentPortalSelfCarePage {

	private String agentPortalText;

	private static final Logger LOG = LoggerFactory.getLogger(AgentViewHelpPage.class);

	/**
	 * Constructor for this page.
	 */
	public AgentViewHelpPage() {
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
		final Form<AgentViewHelpPage> viewHelpForm = new Form<AgentViewHelpPage>("formHelp",
			new CompoundPropertyModel<AgentViewHelpPage>(this));
		viewHelpForm.add(new FeedbackPanel("errorMessages"));
		viewHelpForm.add(new TextArea<String>("agentPortalText"));
		add(viewHelpForm);

	}

	/**
	 * This method fetches the help text
	 */
	protected void fetchAddHelpText() {
		final GetHelpPageContentRequest request = new GetHelpPageContentRequest();
		request.setPortalType(BtpnConstants.PORTAL_AGENT);
		try {
			final GetHelpPageContentResponse response = this.getSupportClient().getHelpPageContent(request);
			if (evaluateBankPortalMobiliserResponse(response)) {
				agentPortalText = response.getHelpPageContent();
			} else {
				error(getLocalizer().getString("fetch.error", this));
			}
		} catch (Exception e) {
			LOG.error("Exception occured while fetching help ==> ", e);
			error(getLocalizer().getString("error.exception", this));
		}
	}
}
