package com.sybase365.mobiliser.web.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.AlertType;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.common.model.IAlertFilter;

/**
 * 
 * @author msw
 */
public class AuthorizeInstantiationAlertFilter implements IAlertFilter,
	java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(AuthorizeInstantiationAlertFilter.class);

    private Session session;
    private MobiliserBasePage basePage;

    public AuthorizeInstantiationAlertFilter(Session session,
	    MobiliserBasePage basePage) {
	this.session = session;
	this.basePage = basePage;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void filterAlerts(List<AlertType> alertTypeList, long customerId) {

	LOG.debug("AuthorizeInstantiationAlertFilter called for customer {}",
		Long.valueOf(customerId));

	if (alertTypeList != null) {
	    List<AlertType> alertTypeListCopy = new ArrayList<AlertType>(
		    alertTypeList);

	    // loop through alerts, get the add page class for the alert
	    // dynamically based on its name and using this parent class of this
	    // class as the package name, and check the authorisation for
	    // instantiation of that class, remove if no authorisation
	    for (AlertType alertType : alertTypeListCopy) {
		try {
		    Class pageClass = basePage.getAddPageClass(
			    basePage.getClass(), alertType.getName());
		    if (!session.getAuthorizationStrategy()
			    .isInstantiationAuthorized(pageClass)) {
			LOG.debug(
				"No authorization on add page class for alert {} found, remove from alert type list",
				alertType.getName());
			alertTypeList.remove(alertType);
		    }
		} catch (Exception e) {
		    LOG.debug(
			    "No add page class for alert {} found, remove from alert type list",
			    alertType.getName());
		    alertTypeList.remove(alertType);
		}
	    }
	}
    }

}