package com.sybase365.mobiliser.web.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.mbanking.contract.v5_0.beans.ServicePackage;
import com.sybase365.mobiliser.mbanking.contract.v5_0.beans.SupportedAlert;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.AlertType;
import com.sybase365.mobiliser.web.application.clients.MBankingClientLogic;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.common.model.IAlertFilter;

/**
 * 
 * @author msw
 */
public class ServicePackageAlertFilter extends
	AuthorizeInstantiationAlertFilter implements IAlertFilter,
	java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(ServicePackageAlertFilter.class);

    private Session session;
    private MobiliserBasePage basePage;
    private MBankingClientLogic clientLogic;

    public ServicePackageAlertFilter(Session session,
	    MobiliserBasePage basePage, MBankingClientLogic clientLogic) {

	super(session, basePage);

	this.session = session;
	this.basePage = basePage;
	this.clientLogic = clientLogic;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void filterAlerts(List<AlertType> alertTypeList, long customerId) {

	super.filterAlerts(alertTypeList, customerId);

	LOG.debug("ServicePackageAlertFilter called for customer {}",
		Long.valueOf(customerId));

	if (alertTypeList != null) {

	    ServicePackage custSP = clientLogic
		    .getCustomerServicePackage(customerId);

	    if (custSP != null) {
		List<SupportedAlert> alertListForSP = custSP
			.getSupportedAlertList().getSupportedAlert();

		List<AlertType> alertTypeListCopy = new ArrayList<AlertType>(
			alertTypeList);

		// loop through alerts, and see if the alert name is in the
		// list of supported alerts for the customer service package,
		// if it is not then remove the alert from the list
		for (AlertType alertType : alertTypeListCopy) {
		    if (!contains(alertListForSP, alertType.getName())) {
			LOG.debug(
				"No alert named {} in customer service package, remove from alert type list",
				alertType.getName());
			alertTypeList.remove(alertType);
		    }
		}
	    } //
	    // what to do if service package not found - remove all alerts
	    else {
		alertTypeList
			.removeAll(new ArrayList<AlertType>(alertTypeList));
	    }
	}
    }

    private boolean contains(List<SupportedAlert> saList, String alertName) {
	if (saList != null) {
	    for (SupportedAlert sa : saList) {
		if (sa.getAlertName().equals(alertName)) {
		    return true;
		}
	    }
	}
	return false;
    }
}