package com.sybase365.mobiliser.web.common.panels.alerts;

import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.AlertType;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerAlert;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerContactPoint;
import com.sybase365.mobiliser.util.tools.wicketutils.security.PrivilegedBehavior;
import com.sybase365.mobiliser.web.application.clients.AlertsClientLogic;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.dataproviders.CustomerAlertsDataProvider;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.common.model.IAlertFilter;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * @author sagraw03
 */
public class MobileAlertsPanel extends Panel {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(MobileAlertsPanel.class);

    private MobiliserBasePage basePage;
    private Class<MobiliserBasePage> addPage;
    private AlertsClientLogic clientLogic;
    private long customerId;
    boolean pageViewedByAgent;
    private PrivilegedBehavior cstPriv;
    private List<AlertType> alertTypes;
    private IAlertFilter alertFilter;
    private boolean forceReload;

    // Private member fields for customer alert subscriptions table
    private CustomerAlertsDataProvider calDataProvider;
    private String calTotalItemString = null;
    private int calStartIndex = 0;
    private int calEndIndex = 0;

    private static final String WICKET_ID_calNavigator = "calNavigator";
    private static final String WICKET_ID_calTotalItems = "calTotalItems";
    private static final String WICKET_ID_calStartIndex = "calStartIndex";
    private static final String WICKET_ID_calEndIndex = "calEndIndex";
    private static final String WICKET_ID_calNoItemsMsg = "calNoItemsMsg";
    private static final String WICKET_ID_calPageable = "calPageable";
    private static final String WICKET_ID_calDescription = "calDescription";
    private static final String WICKET_ID_calContactsPoint = "calContactsPoint";
    private static final String WICKET_ID_calEditAction = "calEditAction";
    private static final String WICKET_ID_calRemoveAction = "calRemoveAction";
    private static final String WICKET_ID_calOrderByNickname = "calOrderByNickname";
    private static final String WICKET_ID_calNickname = "calNickname";

    public MobileAlertsPanel(String id, MobiliserBasePage mobBasePage,
	    AlertsClientLogic clientLogic, long customerId,
	    boolean pageViewedByAgent, IAlertFilter alertFilter,
	    Class<MobiliserBasePage> addPage) {

	super(id);

	this.basePage = mobBasePage;
	this.addPage = addPage;
	this.clientLogic = clientLogic;
	this.customerId = customerId;
	this.pageViewedByAgent = pageViewedByAgent;
	this.alertFilter = alertFilter;

	this.forceReload = true;

	if (!PortalUtils.exists(alertTypes)) {
	    alertTypes = clientLogic.findAlertTypes();
	    alertFilter.filterAlerts(alertTypes, customerId);
	}

	cstPriv = new PrivilegedBehavior(basePage, Constants.PRIV_CST_LOGIN);

	this.constructPanel();
    }

    private void constructPanel() {

	final Form<?> form = new Form<MobileAlertsPanel>("mobileAlertsForm",
		new CompoundPropertyModel<MobileAlertsPanel>(this));

	form.add(new FeedbackPanel("errorMessages"));

	WebMarkupContainer alertsSubscriptionsContainer = new WebMarkupContainer(
		"customerAlertsListContainer");
	form.add(alertsSubscriptionsContainer);

	createAlertsSubscriptionsDataView(alertsSubscriptionsContainer);

	add(form);
    }

    @SuppressWarnings("unchecked")
    private void handleAdd() {
	setResponsePage(addPage);
    }

    private void createAlertsSubscriptionsDataView(WebMarkupContainer parent) {

	calDataProvider = new CustomerAlertsDataProvider(WICKET_ID_calNickname,
		clientLogic, alertTypes);

	Button addAlert = new Button("addAlert") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleAdd();
	    };
	};

	if (isPageViewedByAgent()) {
	    addAlert.add(new PrivilegedBehavior(basePage,
		    Constants.PRIV_CUST_WRITE));
	}

	parent.addOrReplace(addAlert);

	final DataView<CustomerAlert> dataView = new DataView<CustomerAlert>(
		WICKET_ID_calPageable, calDataProvider) {
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onBeforeRender() {

		try {
		    calDataProvider.loadAllAlertEntries(
			    Long.valueOf(customerId), forceReload);
		    refreshTotalItemCount();
		    forceReload = false;
		    if (calDataProvider.size() > 0) {
			setVisible(true);
		    } else {
			setVisible(false);
		    }
		} catch (DataProviderLoadException dple) {
		    LOG.error("# An error occurred while loading alert  list",
			    dple);
		    error(getLocalizer().getString(
			    "mobileAlerts.alertsTable.load.error", this));
		}
		refreshTotalItemCount();
		super.onBeforeRender();
	    }

	    @Override
	    protected void populateItem(final Item<CustomerAlert> item) {
		final CustomerAlert entry = item.getModelObject();
		item.add(new Label(WICKET_ID_calDescription, getLocalizer()
			.getString(
				"manageAlerts.name." + entry.getAlertTypeId()
					+ "", this)));
		List<CustomerContactPoint> customerContactPoints = entry
			.getContactPointList().getContactPoint();

		StringBuilder contacts = new StringBuilder();
		for (CustomerContactPoint customerContactPoint : customerContactPoints) {
		    if (contacts.length() > 0) {
			contacts.append(", ");
		    }
		    if (customerContactPoint.getOtherIdentification() != null) {
			contacts.append(customerContactPoint
				.getOtherIdentification().getNickname());
		    } else if (customerContactPoint.getIdentification() != null) {
			contacts.append(customerContactPoint
				.getIdentification().getIdentification());
		    }
		}
		item.add(new Label(WICKET_ID_calContactsPoint, contacts
			.toString()));

		// Edit Action
		Link<CustomerAlert> editLink = new Link<CustomerAlert>(
			WICKET_ID_calEditAction, item.getModel()) {
		    private static final long serialVersionUID = 1L;

		    @Override
		    public void onClick() {
			forceReload = true;
			setResponseToEditAlertPage(entry);
		    }
		};
		// Remove Action
		Link<CustomerAlert> removeLink = new Link<CustomerAlert>(
			WICKET_ID_calRemoveAction, item.getModel()) {
		    private static final long serialVersionUID = 1L;

		    @SuppressWarnings("unchecked")
		    @Override
		    public void onClick() {
			CustomerAlert entry = (CustomerAlert) getModelObject();
			LOG.debug("Deleting Customer alert with AlertId ",
				entry.getId());
			try {
			    if (clientLogic.deleteCustomerAlert(entry.getId())) {

				getSession()
					.info(getLocalizer()
						.getString(
							"manageAlerts.alertActionDelete.message",
							this));

				calDataProvider.getAllAlertEntries().remove(
					entry);

				setResponsePage(basePage);
			    }
			} catch (Exception e) {
			    LOG.error(
				    "# An error occurred while deleting customer alert",
				    e);
			}
		    }

		};

		removeLink
			.add(new SimpleAttributeModifier(
				"onclick",
				"return confirm('"
					+ getLocalizer()
						.getString(
							"mobileAlerts.alertsTable.remove.confirm",
							this) + "');"));

		if (isPageViewedByAgent()) {
		    editLink.add(new PrivilegedBehavior(basePage,
			    Constants.PRIV_CUST_WRITE));
		    removeLink.add(new PrivilegedBehavior(basePage,
			    Constants.PRIV_CUST_WRITE));
		}
		item.add(editLink);
		item.add(removeLink);
		// set items in even/odd rows to different css style classes
		item.add(new AttributeModifier(Constants.CSS_KEYWARD_CLASS,
			true, new AbstractReadOnlyModel<String>() {
			    private static final long serialVersionUID = 1L;

			    @Override
			    public String getObject() {
				return (item.getIndex() % 2 == 1) ? Constants.CSS_STYLE_ODD
					: Constants.CSS_STYLE_EVEN;
			    }
			}));
	    }

	    private void refreshTotalItemCount() {
		calTotalItemString = new Integer(calDataProvider.size())
			.toString();
		int total = calDataProvider.size();
		if (total > 0) {
		    calStartIndex = getCurrentPage() * getItemsPerPage() + 1;
		    calEndIndex = calStartIndex + getItemsPerPage() - 1;
		    if (calEndIndex > total)
			calEndIndex = total;
		} else {
		    calStartIndex = 0;
		    calEndIndex = 0;
		}
	    }
	};
	dataView.setItemsPerPage(10);
	parent.addOrReplace(dataView);
	parent.addOrReplace(new OrderByBorder(WICKET_ID_calOrderByNickname,
		WICKET_ID_calNickname, calDataProvider) {
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onSortChanged() {
		// For some reasons the dataView can be null when the page is
		// loading
		// and the sort is clicked (clicking the name header), so handle
		// it
		if (dataView != null) {
		    dataView.setCurrentPage(0);
		}
	    }
	});

	parent.addOrReplace(new MultiLineLabel(WICKET_ID_calNoItemsMsg,
		getLocalizer().getString("mobileAlerts.alertsTable.noItemsMsg",
			this)
			+ "\n"
			+ getLocalizer().getString(
				"mobileAlerts.alertsTable.addAlertHelp", this)) {
	    @Override
	    public boolean isVisible() {
		if (dataView.getItemCount() > 0) {
		    return false;
		} else {
		    return super.isVisible();
		}
	    }
	});

	// Navigator example: << < 1 2 > >>
	parent.addOrReplace(new CustomPagingNavigator(WICKET_ID_calNavigator,
		dataView));
	parent.addOrReplace(new Label(WICKET_ID_calTotalItems,
		new PropertyModel<String>(this, "calTotalItemString")));
	parent.addOrReplace(new Label(WICKET_ID_calStartIndex,
		new PropertyModel<Integer>(this, "calStartIndex")));
	parent.addOrReplace(new Label(WICKET_ID_calEndIndex,
		new PropertyModel<Integer>(this, "calEndIndex")));
    }

    @SuppressWarnings("unchecked")
    private void setResponseToEditAlertPage(CustomerAlert customerAlert) {
	try {
	    Class<Page> editAlertPage = basePage.getEditPageClass(
		    basePage.getClass(),
		    findAlertType(customerAlert.getAlertTypeId()).getName());
	    setResponsePage(editAlertPage.getConstructor(CustomerAlert.class)
		    .newInstance(customerAlert));
	} catch (Exception e) {
	    LOG.error(
		    "# An error occurred loading edit alert page for alert id {}",
		    Long.valueOf(customerAlert.getAlertTypeId()), e);
	}
    }

    private AlertType findAlertType(final long alertTypeId)
	    throws IllegalArgumentException {
	for (AlertType alertType : alertTypes) {
	    if (alertType.getId().longValue() == alertTypeId) {
		return alertType;
	    }
	}
	throw new IllegalArgumentException("Alert for id " + alertTypeId
		+ " not available");
    }

    public boolean isPageViewedByAgent() {
	return pageViewedByAgent;
    }

    public void setPageViewedByAgent(boolean pageViewedByAgent) {
	this.pageViewedByAgent = pageViewedByAgent;
    }

}