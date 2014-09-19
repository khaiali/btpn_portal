package com.sybase365.mobiliser.web.dashboard.pages.servers;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;

import com.sybase365.mobiliser.web.beans.ServerBean;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.common.dataproviders.ServerListDataProvider;
import com.sybase365.mobiliser.web.dashboard.pages.servers.model.IServerListViewer;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_DASHBOARD_SERVERS)
public class ServerListPage extends ServersMenuGroup implements
	IServerListViewer {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(ServerListPage.class);

    // Data Model for table list
    private ServerListDataProvider dataProvider;

    // Table Items
    private String totalItemString = null;
    private int startIndex = 0;
    private int endIndex = 0;

    private boolean forceReload = true;

    private static final String WICKET_ID_navigator = "navigator";
    private static final String WICKET_ID_totalItems = "totalItems";
    private static final String WICKET_ID_startIndex = "startIndex";
    private static final String WICKET_ID_endIndex = "endIndex";
    private static final String WICKET_ID_pageable = "pageable";
    private static final String WICKET_ID_hostname = "hostname";
    private static final String WICKET_ID_port = "port";
    private static final String WICKET_ID_pingAction = "pingAction";
    private static final String WICKET_ID_selectAction = "selectAction";
    private static final String WICKET_ID_removeAction = "removeAction";
    private static final String WICKET_ID_noItemsMsg = "noItemsMsg";

    private Form<?> form;

    @SuppressWarnings("unchecked")
    @Override
    protected void initOwnPageComponents() {

	super.initOwnPageComponents();

	form = new Form("serverListForm",
		new CompoundPropertyModel<ServerListPage>(this));

	add(form);

	form.add(new FeedbackPanel("errorMessages"));

	form.add(new Button("addServer") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleAddServerAction();
	    };
	});

	refreshServerList();
    }

    private void handleAddServerAction() {
	setResponsePage(new AddServerPage(this, dataProvider) {
	    // D055066: Method is not called locally, therefore we don't need it
	    // public void refresh() {
	    //
	    // }
	});
    }

    private void handlePingAction(ServerBean entry) {
	entry.setPingOk(pingServer(entry.getHostname(), entry.getPort()));
    }

    private void handleSelectAction(ServerBean entry) {
	setSelectedServer(entry);
    }

    private void handleRemoveAction(ServerBean entry) {
	try {
	    dataProvider.removeServer(entry);

	    refreshServerList();

	    info(getLocalizer().getString("serverList.remove.success", this));
	} catch (Exception e) {
	    error(getLocalizer().getString("serverList.remove.error", this));
	}
    }

    private void createServerListDataView() {

	dataProvider = new ServerListDataProvider(WICKET_ID_hostname, this);

	final DataView<ServerBean> dataView = new DataView<ServerBean>(
		WICKET_ID_pageable, dataProvider) {

	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onBeforeRender() {

		try {
		    dataProvider.loadAllServers();

		    refreshTotalItemCount();
		} catch (DataProviderLoadException dple) {
		    LOG.error(
			    "An error occured while loading customer serverList list",
			    dple);
		    error(getLocalizer().getString("serverList.load.error",
			    this));
		}

		refreshTotalItemCount();

		super.onBeforeRender();
	    }

	    @Override
	    protected void populateItem(final Item<ServerBean> item) {

		final ServerBean entry = item.getModelObject();

		// run ping to set server status
		handlePingAction(entry);

		Image serverIcon = new Image("serverIcon");
		serverIcon.add(new AttributeModifier("src", true,
			new AbstractReadOnlyModel() {
			    private static final long serialVersionUID = 1L;

			    @Override
			    public final Object getObject() {
				if (isSelected(entry)) {
				    return "images/icons/server_original_color_112x92.png";
				} else {
				    return "images/icons/server_original_greyscale_112x92.png";
				}
			    }
			}));
		serverIcon.setOutputMarkupId(true);

		item.add(serverIcon);

		Image statusIcon = new Image("statusIcon");
		statusIcon.add(new AttributeModifier("src", true,
			new AbstractReadOnlyModel() {
			    private static final long serialVersionUID = 1L;

			    @Override
			    public final Object getObject() {
				if (entry.pingOk()) {
				    return "images/icons/status-up-48px.png";
				} else {
				    return "images/icons/status-important-48px.png";
				}
			    }
			}));
		statusIcon.setOutputMarkupId(true);

		item.add(statusIcon);

		item.add(new Label(WICKET_ID_hostname, entry.getHostname()));
		item.add(new Label(WICKET_ID_port, String.valueOf(entry
			.getPort())));

		// Ping Action
		Link<ServerBean> pingLink = new Link<ServerBean>(
			WICKET_ID_pingAction, item.getModel()) {

		    @Override
		    public void onClick() {
			ServerBean entry = (ServerBean) getModelObject();
			handlePingAction(entry);
			if (entry.pingOk()) {
			    setInfo(getLocalizer().getString(
				    "serverList.ping.success", this)
				    + entry.getHostname()
				    + ":"
				    + entry.getPort());
			} else {
			    setError(getLocalizer().getString(
				    "serverList.ping.error", this)
				    + entry.getHostname()
				    + ":"
				    + entry.getPort());
			}
		    }
		};
		item.add(pingLink);

		// Select Action
		Link selectLink = new Link<ServerBean>(WICKET_ID_selectAction,
			item.getModel()) {
		    @Override
		    public void onClick() {
			ServerBean entry = (ServerBean) getModelObject();
			handlePingAction(entry);
			if (!entry.pingOk()) {
			    setError(getLocalizer().getString(
				    "serverList.select.error.notonline", this));
			} else {
			    handleSelectAction(entry);
			}
		    }
		};
		item.add(selectLink);

		// Remove Action
		Link removeLink = new Link<ServerBean>(WICKET_ID_removeAction,
			item.getModel()) {
		    @Override
		    public void onClick() {
			ServerBean entry = (ServerBean) getModelObject();
			handleRemoveAction(entry);
		    }
		};

		removeLink.add(new SimpleAttributeModifier("onclick",
			"return confirm('"
				+ getLocalizer().getString(
					"serverList.remove.confirm", this)
				+ "');"));

		item.add(removeLink);

		// set items in even/odd rows to different css style classes
		item.add(new AttributeModifier(Constants.CSS_KEYWARD_CLASS,
			true, new AbstractReadOnlyModel<String>() {

			    @Override
			    public String getObject() {
				return (item.getIndex() % 2 == 1) ? Constants.CSS_STYLE_ODD
					: Constants.CSS_STYLE_EVEN;
			    }
			}));
	    }

	    @Override
	    public boolean isVisible() {
		if (getItemCount() > 0) {
		    return true;
		} else {
		    return super.isVisible();
		}
	    }

	    private void refreshTotalItemCount() {
		totalItemString = new Integer(dataProvider.size()).toString();
		int total = dataProvider.size();
		if (total > 0) {
		    startIndex = getCurrentPage() * getItemsPerPage() + 1;
		    endIndex = startIndex + getItemsPerPage() - 1;
		    if (endIndex > total) {
			endIndex = total;
		    }
		} else {
		    startIndex = 0;
		    endIndex = 0;
		}
	    }
	};

	dataView.setOutputMarkupPlaceholderTag(true);

	dataView.setItemsPerPage(10);

	form.addOrReplace(dataView);

	form.addOrReplace(new MultiLineLabel(WICKET_ID_noItemsMsg,
		getLocalizer().getString("serverList.table.noItemsMsg", this)
			+ "\n"
			+ getLocalizer().getString(
				"serverList.table.addServerBeanHelp", this)) {

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
	form.addOrReplace(new CustomPagingNavigator(WICKET_ID_navigator,
		dataView));

	form.addOrReplace(new Label(WICKET_ID_totalItems,
		new PropertyModel<String>(this, "totalItemString")));

	form.addOrReplace(new Label(WICKET_ID_startIndex, new PropertyModel(
		this, "startIndex")));

	form.addOrReplace(new Label(WICKET_ID_endIndex, new PropertyModel(this,
		"endIndex")));

	return;
    }

    private boolean isSelected(ServerBean entry) {
	if (getSelectedServer().getHostname().equals(entry.getHostname())
		&& getSelectedServer().getPort() == entry.getPort()) {
	    return Boolean.TRUE;
	}
	return Boolean.FALSE;
    }

    private void setInfo(String message) {
	info(message);
    }

    private void setError(String message) {
	error(message);
    }

    @Override
    public void refreshServerList() {

	LOG.debug("refreshServerList()...");

	this.refreshPreferencesClientSource();

	createServerListDataView();
    }
}