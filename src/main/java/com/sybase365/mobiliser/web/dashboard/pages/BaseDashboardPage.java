package com.sybase365.mobiliser.web.dashboard.pages;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;

import org.apache.wicket.Component;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.sybase365.mobiliser.util.tools.wicketutils.menu.IMenuEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.SybaseLeftMenuView;
import com.sybase365.mobiliser.web.application.pages.BaseApplicationPage;
import com.sybase365.mobiliser.web.beans.ServerBean;
import com.sybase365.mobiliser.web.dashboard.pages.trackers.dao.ITrackersDao;
import com.sybase365.mobiliser.web.util.DynamicServiceConfiguration;
import com.sybase365.mobiliser.web.util.PortalUtils;

public abstract class BaseDashboardPage extends BaseApplicationPage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(BaseDashboardPage.class);

    private Component hostnameLabel;
    private Component portLabel;

    private static MetaDataKey SELECTED_SERVER = new MetaDataKey() {
    };

    @SpringBean(name = "trackersDao")
    public ITrackersDao trackersDao;

    public BaseDashboardPage() {
	super();
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    public BaseDashboardPage(final PageParameters parameters) {
	super(parameters);
    }

    @Override
    protected void initOwnPageComponents() {

	super.initOwnPageComponents();

	getMobiliserWebSession().setLeftMenu(buildLeftMenu());

	ServerBean selectedServer = getSelectedServer();

	if (!PortalUtils.exists(selectedServer)) {
	    selectedServer = setDefaultServer();
	}

	hostnameLabel = new Label("selected.context.hostname",
		selectedServer.getHostname()).setVisible(
		isServerContextVisible()).setOutputMarkupId(true);

	portLabel = new Label("selected.context.port",
		String.valueOf(selectedServer.getPort())).setVisible(
		isServerContextVisible()).setOutputMarkupId(true);

	addOrReplace(hostnameLabel);

	addOrReplace(portLabel);

	addOrReplace(new SybaseLeftMenuView("leftMenu",
		new Model<LinkedList<IMenuEntry>>(getMobiliserWebSession()
			.getLeftMenu())));
    }

    public abstract LinkedList<IMenuEntry> buildLeftMenu();

    protected abstract boolean isServerContextVisible();

    protected abstract boolean isTrackerContextVisible();

    /**
     * Allows for subclass to set the leftMenu after component initialization.
     * 
     * @param entries
     */
    public void setLeftMenu(LinkedList<IMenuEntry> entries) {
	getMobiliserWebSession().setLeftMenu(entries);
	addOrReplace(new SybaseLeftMenuView("leftMenu",
		new Model<LinkedList<IMenuEntry>>(entries)));
    };

    @Override
    public String getApplicationName() {
	return getLocalizer().getString("dashboard.page.title", this);
    }

    @Override
    protected Class getActiveMenu() {
	return this.getClass();
    }

    @Override
    protected boolean supportsSvaBalance() {
	return false;
    }

    protected void setSelectedServer(ServerBean bean) {

	LOG.debug("Updating client configuration of management service...");

	DynamicServiceConfiguration clientConfiguration = this
		.getManagementClientConfiguration();

	clientConfiguration.setHostname(bean.getHostname());
	clientConfiguration.setPort(String.valueOf(bean.getPort()));

	this.setManagementClientConfiguration(clientConfiguration);

	this.getSession().setMetaData(SELECTED_SERVER, bean);

	hostnameLabel = new Label("selected.context.hostname",
		bean.getHostname()).setVisible(isServerContextVisible())
		.setOutputMarkupId(true);

	portLabel = new Label("selected.context.port", String.valueOf(bean
		.getPort())).setVisible(isServerContextVisible())
		.setOutputMarkupId(true);

	addOrReplace(hostnameLabel);

	addOrReplace(portLabel);
    }

    protected ServerBean getSelectedServer() {
	return (ServerBean) this.getSession().getMetaData(SELECTED_SERVER);
    }

    protected ServerBean setDefaultServer() {
	try {
	    URL homeServerUrl = new URL(this.getClientConfiguration()
		    .getMobiliserEndpointUrl());

	    setSelectedServer(new ServerBean(homeServerUrl.getHost(),
		    homeServerUrl.getPort()));

	    LOG.debug("Default Server is {}", homeServerUrl);

	    return getSelectedServer();
	} catch (MalformedURLException ex) {
	    LOG.warn("Can not set default server:", ex);
	}
	return null;
    }
}
