package com.sybase365.mobiliser.web.consumer.pages.portal.selfcare.alerts;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.AlertType;
import com.sybase365.mobiliser.web.application.clients.AlertsClientLogic;
import com.sybase365.mobiliser.web.consumer.pages.portal.selfcare.BaseSelfCarePage;

/**
 * Contains shared logic and functions for alert processing
 * 
 * @author sagraw03
 * 
 */
public abstract class BaseAlertPage extends BaseSelfCarePage {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(BaseAlertPage.class);

    private static final String ALERT_TYPE_LABEL_KEY = "manageAlerts.alertOptions.";
    private static final String ALERT_TYPE_LABEL_HELP_KEY = "manageAlerts.alertOptions.addAlertTypeHelp.";

    private static final String WICKET_ID_alertsDiv = "alertsDiv";
    private static final String WICKET_ID_errorMessages = "errorMessages";

    protected AlertType alertType;

    protected static String ACTION_ADD = "addAlertType";
    protected static String ACTION_EDIT = "editAlertType";

    @SpringBean(name = "systemAuthAlertsClientLogic")
    protected AlertsClientLogic clientLogic;

    /**
     * Standard constructor
     * 
     * @param alertTypeToAdd
     *            is alert type to be added
     * @param action
     *            is add or edit alert action
     */

    public BaseAlertPage(long alertTypeToAdd, String action) {
	super();

	this.alertType = clientLogic.getAlertType(alertTypeToAdd);

	WebMarkupContainer addAlertsDiv = new WebMarkupContainer(
		WICKET_ID_alertsDiv) {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public boolean isTransparentResolver() {
		return true;
	    }
	};
	addAlertsDiv.setOutputMarkupId(true);
	addAlertsDiv.add(new Label("alertTypeToAddLabel", getLocalizer()
		.getString(
			ALERT_TYPE_LABEL_KEY + action + "."
				+ String.valueOf(alertTypeToAdd), this)));

	addAlertsDiv.add(new Label("help", getLocalizer().getString(
		ALERT_TYPE_LABEL_HELP_KEY + String.valueOf(alertTypeToAdd),
		this)));
	addAlertsDiv.add(new FeedbackPanel(WICKET_ID_errorMessages));
	add(addAlertsDiv);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Class<MobileAlertsPage> getActiveMenu() {
	return MobileAlertsPage.class;
    }
}
