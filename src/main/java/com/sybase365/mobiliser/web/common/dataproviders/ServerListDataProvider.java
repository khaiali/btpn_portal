package com.sybase365.mobiliser.web.common.dataproviders;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.beans.PreferenceBean;
import com.sybase365.mobiliser.web.beans.ServerBean;
import com.sybase365.mobiliser.web.util.Configuration;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * 
 * @author all
 */
@SuppressWarnings("serial")
public class ServerListDataProvider extends SortableDataProvider<ServerBean> {

    private static final Logger LOG = LoggerFactory
	    .getLogger(ServerListDataProvider.class);

    private transient List<ServerBean> allServers = new ArrayList<ServerBean>();

    private MobiliserBasePage mobBasePage;

    private static String SERVER_LIST_PREF_APP = "presentationlayer";
    private static String SERVER_LIST_PREF_PATH = "/com/sybase365/mobiliser/web/util/Configuration";
    private static String SERVER_LIST_PREF_KEY = "dashboardServerList";

    public ServerListDataProvider(String defaultSortProperty,
	    final MobiliserBasePage mobBasePage) {
	setSort(defaultSortProperty, true);
	this.mobBasePage = mobBasePage;
    }

    private MobiliserBasePage getMobiliserBasePage() {
	return mobBasePage;
    }

    /**
     * Returns ServerBean starting with index <code>first</code> and ending with
     * <code>first+count</code>
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#iterator(int,
     *      int)
     */
    @Override
    public Iterator<ServerBean> iterator(int first, int count) {
	SortParam sp = getSort();
	return find(first, count, sp.getProperty(), sp.isAscending())
		.iterator();
    }

    /**
     * Returns total number of <code>ServerBean</code> in the list
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#size()
     */
    @Override
    public int size() {
	int count = 0;

	if (allServers == null) {
	    return count;
	}

	return allServers.size();
    }

    @Override
    public IModel<ServerBean> model(final ServerBean object) {
	IModel<ServerBean> model = new LoadableDetachableModel<ServerBean>() {
	    @Override
	    protected ServerBean load() {
		ServerBean set = null;
		for (ServerBean obj : allServers) {
		    if ((obj.toString().equals(object.toString()))) {
			set = obj;
			break;
		    }
		}
		return set;
	    }
	};

	return new CompoundPropertyModel<ServerBean>(model);
    }

    public void loadAllServers() throws DataProviderLoadException {

	if (!PortalUtils.exists(allServers)) {

	    allServers = new ArrayList<ServerBean>();

	    try {
		// first add the default (connected-to) server into the list
		URL homeServerUrl = new URL(mobBasePage
			.getClientConfiguration().getMobiliserEndpointUrl());

		LOG.debug("Connected-to server: {}", mobBasePage
			.getClientConfiguration().getMobiliserEndpointUrl());

		allServers.add(new ServerBean(homeServerUrl.getHost(),
			homeServerUrl.getPort()));
	    } catch (MalformedURLException ex) {
		LOG.info(
			"An URL Mailformed Exception has occured and therefore it was not added to the servers list",
			ex);
	    }

	    // now look at the list of other servers maintained by prefs
	    // list is a comma separated host:port,
	    // e.g. 133.23.213.1:8080,robin.batman.com:8080

	    PreferenceBean prefBean = mobBasePage.getPreferenceBean(
		    "presentationlayer", "/" + Configuration.getNodePath(),
		    Configuration.DASHBOARD_SERVER_LIST_KEY);

	    if (PortalUtils.exists(prefBean)) {

		String prefsServerList = prefBean.getValue();

		LOG.debug("Got server list from prefs: {}", prefsServerList);

		if (PortalUtils.exists(prefsServerList)) {

		    List<String> serverList = Arrays.asList(prefsServerList
			    .split(","));

		    for (String server : serverList) {

			ServerBean newServer = null;
			try {
			    newServer = new ServerBean(
				    server.split(":")[0].trim(),
				    Integer.valueOf(server.split(":")[1].trim()));

			    if (!hasServer(newServer.getHostname(),
				    newServer.getPort())) {
				allServers.add(newServer);
			    }
			} catch (Exception e) {
			    LOG.warn(
				    "Badly formed entry in dashboard server list: {} - was expecting host:port but got {}",
				    serverList, server);
			}
		    }
		}
	    }
	}
    }

    private boolean hasServer(String hostName, int port) {
	for (ServerBean existingServer : allServers) {
	    if (existingServer.getHostname().equals(hostName)
		    && existingServer.getPort() == port) {
		return Boolean.TRUE;
	    }
	}
	return Boolean.FALSE;
    }

    protected List<ServerBean> find(int first, int count, String sortProperty,
	    boolean sortAsc) {

	List<ServerBean> sublist = getIndex(allServers, sortProperty, sortAsc)
		.subList(first, first + count);

	return sublist;
    }

    protected List<ServerBean> getIndex(List<ServerBean> allServers,
	    String prop, boolean asc) {

	if (prop.equals("name")) {
	    return sort(allServers, asc);
	} else {
	    return allServers;
	}
    }

    private List<ServerBean> sort(List<ServerBean> entries, boolean asc) {

	if (asc) {

	    Collections.sort(entries);

	}

	return entries;
    }

    public List<ServerBean> getAllServers() {
	return this.allServers;
    }

    public void setAllServers(List<ServerBean> value) {
	this.allServers = value;
    }

    public void removeServer(ServerBean entry) throws Exception {

	StringBuilder serverList = new StringBuilder();

	for (ServerBean serverBean : getAllServers()) {
	    if (serverBean.getHostname().equalsIgnoreCase(entry.getHostname())
		    && serverBean.getPort() == entry.getPort()) {
		continue;
	    }

	    if (serverList.length() > 0) {
		serverList.append(",");
	    }

	    serverList.append(serverBean.getHostname()).append(":")
		    .append(serverBean.getPort());
	}

	mobBasePage.setPreferencesValue("presentationlayer",
		Configuration.getNodePath(),
		Configuration.DASHBOARD_SERVER_LIST_KEY, serverList.toString(),
		Configuration.DASHBOARD_SERVER_LIST_TYPE,
		Configuration.DASHBOARD_SERVER_LIST_DESC);

	allServers.remove(entry);

    }

    public void addServer(String hostname, int port) throws Exception {

	StringBuilder serverList = new StringBuilder(mobBasePage
		.getConfiguration().getDashboardServerList());

	if (serverList.length() > 0) {
	    serverList.append(",");
	}

	serverList.append(hostname).append(":").append(String.valueOf(port));

	mobBasePage.setPreferencesValue("presentationlayer",
		Configuration.getNodePath(),
		Configuration.DASHBOARD_SERVER_LIST_KEY, serverList.toString(),
		Configuration.DASHBOARD_SERVER_LIST_TYPE,
		Configuration.DASHBOARD_SERVER_LIST_DESC);

	allServers.add(new ServerBean(hostname, port));
    }
}