package com.sybase365.mobiliser.web.common.panels.alerts;

import org.apache.wicket.AttributeModifier;
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
import com.sybase365.mobiliser.util.tools.wicketutils.security.PrivilegedBehavior;
import com.sybase365.mobiliser.web.application.clients.AlertsClientLogic;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.dataproviders.AlertsDataProvider;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.common.model.IAlertFilter;
import com.sybase365.mobiliser.web.util.Constants;

/**
 * 
 * 
 * @author msw
 */
public class MobileAlertsOptionsPanel extends Panel {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(MobileAlertsOptionsPanel.class);

    private MobiliserBasePage basePage;
    private Class<MobiliserBasePage> backPage;
    private AlertsClientLogic clientLogic;
    private long customerId;
    boolean pageViewedByAgent;
    private PrivilegedBehavior cstPriv;
    private IAlertFilter alertFilter;

    // Private member fields for alert options table
    private AlertsDataProvider alDataProvider;
    private String alTotalItemString = null;
    private int alStartIndex = 0;
    private int alEndIndex = 0;

    private static final String WICKET_ID_alNavigator = "alNavigator";
    private static final String WICKET_ID_alTotalItems = "alTotalItems";
    private static final String WICKET_ID_alStartIndex = "alStartIndex";
    private static final String WICKET_ID_alEndIndex = "alEndIndex";
    private static final String WICKET_ID_alNoItemsMsg = "alNoItemsMsg";
    private static final String WICKET_ID_alPageable = "alPageable";
    private static final String WICKET_ID_alDescription = "alDescription";
    private static final String WICKET_ID_alName = "alName";
    private static final String WICKET_ID_alOrderByName = "alOrderByName";
    private static final String WICKET_ID_alAddAction = "alAddAction";

    public MobileAlertsOptionsPanel(String id, MobiliserBasePage mobBasePage,
	    AlertsClientLogic clientLogic, long customerId,
	    boolean pageViewedByAgent, IAlertFilter alertFilter,
	    Class<MobiliserBasePage> backPage) {

	super(id);

	this.basePage = mobBasePage;
	this.backPage = backPage;
	this.clientLogic = clientLogic;
	this.customerId = customerId;
	this.pageViewedByAgent = pageViewedByAgent;
	this.alertFilter = alertFilter;

	cstPriv = new PrivilegedBehavior(basePage, Constants.PRIV_CST_LOGIN);

	this.constructPanel();
    }

    private void constructPanel() {

	final Form<?> form = new Form<MobileAlertsOptionsPanel>(
		"mobileAlertsOptionsForm",
		new CompoundPropertyModel<MobileAlertsOptionsPanel>(this));

	form.add(new FeedbackPanel("errorMessages"));

	WebMarkupContainer alertsOptionsContainer = new WebMarkupContainer(
		"alertsListContainer");

	form.add(alertsOptionsContainer);

	createAlertsOptionsDataView(alertsOptionsContainer);

	Button backButton = new Button("back") {
	    private static final long serialVersionUID = 1L;

	    @SuppressWarnings("unchecked")
	    @Override
	    public void onSubmit() {
		setResponsePage(backPage);
	    };
	};

	form.add(backButton);

	add(form);
    }

    private void createAlertsOptionsDataView(WebMarkupContainer parent) {

	alDataProvider = new AlertsDataProvider(WICKET_ID_alName, clientLogic,
		alertFilter);

	final DataView<AlertType> dataView = new DataView<AlertType>(
		WICKET_ID_alPageable, alDataProvider) {
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onBeforeRender() {

		try {
		    alDataProvider
			    .loadAllAlertEntries(Long.valueOf(customerId));
		    refreshTotalItemCount();
		    if (alDataProvider.size() > 0) {
			setVisible(true);
		    } else {
			setVisible(false);
		    }
		} catch (DataProviderLoadException dple) {
		    LOG.error(
			    "# An error occurred while loading customer alert list",
			    dple);
		    error(getLocalizer().getString(
			    "mobileAlerts.alertsTable.load.error", this));
		}
		refreshTotalItemCount();
		super.onBeforeRender();
	    }

	    @Override
	    protected void populateItem(final Item<AlertType> item) {

		final AlertType entry = item.getModelObject();

		item.add(new Label(WICKET_ID_alName, getLocalizer().getString(
			"manageAlerts.name." + entry.getId(), this)));

		item.add(new Label(WICKET_ID_alDescription, getLocalizer()
			.getString("manageAlerts.desc." + entry.getId(), this)));

		// Add Action
		Link<AlertType> addLink = new Link<AlertType>(
			WICKET_ID_alAddAction, item.getModel()) {
		    private static final long serialVersionUID = 1L;

		    @Override
		    public void onClick() {
			setResponseToAddAlertPage(entry);
		    }
		};

		item.add(addLink);

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
		alTotalItemString = new Integer(alDataProvider.size())
			.toString();
		int total = alDataProvider.size();
		if (total > 0) {
		    alStartIndex = getCurrentPage() * getItemsPerPage() + 1;
		    alEndIndex = alStartIndex + getItemsPerPage() - 1;
		    if (alEndIndex > total)
			alEndIndex = total;
		} else {
		    alStartIndex = 0;
		    alEndIndex = 0;
		}
	    }
	};

	dataView.setItemsPerPage(10);

	parent.add(dataView);

	parent.add(new OrderByBorder(WICKET_ID_alOrderByName, WICKET_ID_alName,
		alDataProvider) {
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

	parent.add(new MultiLineLabel(WICKET_ID_alNoItemsMsg, getLocalizer()
		.getString("mobileAlerts.alertsTable.noItemsMsg", this)) {
	    private static final long serialVersionUID = 1L;

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
	parent.add(new CustomPagingNavigator(WICKET_ID_alNavigator, dataView));
	parent.add(new Label(WICKET_ID_alTotalItems, new PropertyModel<String>(
		this, "alTotalItemString")));
	parent.add(new Label(WICKET_ID_alStartIndex,
		new PropertyModel<Integer>(this, "alStartIndex")));
	parent.add(new Label(WICKET_ID_alEndIndex, new PropertyModel<Integer>(
		this, "alEndIndex")));
    }

    private void setResponseToAddAlertPage(AlertType alertType) {
	try {
	    setResponsePage(basePage.getAddPageClass(basePage.getClass(),
		    alertType.getName()));
	} catch (ClassNotFoundException cnfe) {
	    LOG.error("# An error occurred loading add alert page for {}",
		    alertType.getName(), cnfe);
	}
    }

    public boolean isPageViewedByAgent() {
	return pageViewedByAgent;
    }

    public void setPageViewedByAgent(boolean pageViewedByAgent) {
	this.pageViewedByAgent = pageViewedByAgent;
    }

}