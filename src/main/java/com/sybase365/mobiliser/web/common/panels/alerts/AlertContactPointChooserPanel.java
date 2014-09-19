package com.sybase365.mobiliser.web.common.panels.alerts;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerContactPoint;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerContactPointList;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.beans.AlertContactPointBean;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * AlertContactPointPanel should be added wherever contactPoints group checkbox
 * is required.
 * 
 * @author sagraw03
 */
public class AlertContactPointChooserPanel extends Panel {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(AlertContactPointChooserPanel.class);

    private MobiliserBasePage basePage;
    private List<CustomerContactPoint> previousContactPointList;
    private List<AlertContactPointBean> newContactPointList;
    private List<AlertContactPointBean> allContactPointList;
    private long customerId;

    private static final String WICKET_ID_contactNumbers = "contactNumbers";
    private static final String WICKET_ID_contactNumber = "contactNumber";
    private static final String WICKET_ID_check = "check";

    public AlertContactPointChooserPanel(final String id,
	    final long customerId, final MobiliserBasePage basePage,
	    final List<CustomerContactPoint> previousContactPointList) {

	super(id);

	this.customerId = customerId;
	this.basePage = basePage;
	this.previousContactPointList = previousContactPointList;

	if (LOG.isDebugEnabled()) {
	    LOG.debug("Current contact points for alert:");
	    for (CustomerContactPoint previousContactPoint : previousContactPointList) {
		LOG
			.debug(
				"{}",
				(previousContactPoint.getIdentification() != null ? previousContactPoint
					.getIdentification()
					.getIdentification()
					: previousContactPoint
						.getOtherIdentification()
						.getIdentification()));
	    }
	}

	allContactPointList = basePage.getAllContactPointsList(customerId);
	newContactPointList = new ArrayList<AlertContactPointBean>();
	CheckGroup<AlertContactPointBean> contactPointCheckGroup = new CheckGroup<AlertContactPointBean>(
		"checkgroup", newContactPointList);
	contactPointCheckGroup.setRequired(true);

	final ListView<AlertContactPointBean> allContactPointListView = new ListView<AlertContactPointBean>(
		WICKET_ID_contactNumbers, allContactPointList) {

	    private static final long serialVersionUID = 1L;

	    @SuppressWarnings("unchecked")
	    protected void populateItem(ListItem<AlertContactPointBean> item) {

		final AlertContactPointBean thisContactPoint = (AlertContactPointBean) item
			.getModelObject();

		Check<AlertContactPointBean> checkBox = new Check<AlertContactPointBean>(
			WICKET_ID_check, item.getModel()) {
		    private static final long serialVersionUID = 1L;

		    @Override
		    protected void onComponentTag(ComponentTag tag) {
			if (isSelected(thisContactPoint)) {
			    tag.put("checked", "checked");
			}
			super.onComponentTag(tag);
		    }
		};

		item.add(checkBox);

		item.add(new Label(WICKET_ID_contactNumber,
			formatContactPoint(thisContactPoint)));
	    }
	};
	contactPointCheckGroup.add(allContactPointListView);
	add(contactPointCheckGroup);
    }

    protected boolean isSelected(AlertContactPointBean contactPoint) {
	for (CustomerContactPoint cp : this.previousContactPointList) {
	    if (cp.getIdentification() != null
		    && contactPoint.getPrimaryIdentification() != null) {
		if (cp.getIdentification().getIdentification().equals(
			contactPoint.getIdentification())) {
		    return Boolean.TRUE;
		}
	    } else if (cp.getOtherIdentification() != null
		    && contactPoint.getOtherIdentification() != null) {
		if (cp.getOtherIdentification().getIdentification().equals(
			contactPoint.getIdentification())) {
		    return Boolean.TRUE;
		}
	    }
	}
	for (AlertContactPointBean alertContactPointBean : newContactPointList) {
	    if (alertContactPointBean.getIdentification() != null
		    && contactPoint.getPrimaryIdentification() != null) {
		if (alertContactPointBean.getIdentification().equals(
			contactPoint.getIdentification())) {
		    return Boolean.TRUE;
		}
	    } else if (alertContactPointBean.getOtherIdentification() != null
		    && contactPoint.getOtherIdentification() != null) {
		if (alertContactPointBean.getOtherIdentification()
			.getIdentification().equals(
				contactPoint.getIdentification())) {
		    return Boolean.TRUE;
		}
	    }
	}

	return Boolean.FALSE;
    }

    /**
     * Return all selected contact points as a list of CustomerContactPoint
     * 
     * @param customerAlertID
     *            Id of a customer alert
     * 
     * @return List<CustomerContactPoint>
     */
    public CustomerContactPointList getContactPointList(long customerAlertId) {

	CustomerContactPointList ccpl = new CustomerContactPointList();
	List<CustomerContactPoint> contactPointList = ccpl.getContactPoint();

	for (AlertContactPointBean contactPoint : newContactPointList) {
	    CustomerContactPoint newContactPoint = new CustomerContactPoint();
	    newContactPoint.setCustomerAlertId(customerAlertId);
	    newContactPoint.setIdentification(contactPoint
		    .getPrimaryIdentification());
	    newContactPoint.setOtherIdentification(contactPoint
		    .getOtherIdentification());
	    contactPointList.add(newContactPoint);
	}

	return ccpl;
    }

    /**
     * formatContactPoint format a contact point to display
     * 
     * @param contactPoint
     *            a contact point
     * 
     * @return String formatedContact Number
     */
    public String formatContactPoint(final AlertContactPointBean contactPoint) {

	StringBuilder result = new StringBuilder();

	if (PortalUtils.exists(contactPoint.getNickname())) {
	    result.append(contactPoint.getNickname());
	    result.append(" (");
	    result.append(contactPoint.getIdentification());
	    result.append(")");
	} else {
	    result.append(contactPoint.getIdentification());
	}

	return result.toString();
    }
}