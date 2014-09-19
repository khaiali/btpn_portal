package com.sybase365.mobiliser.web.dashboard.pages.servers;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.common.dataproviders.ServerListDataProvider;

import com.sybase365.mobiliser.web.dashboard.pages.servers.model.IServerListViewer;
import com.sybase365.mobiliser.web.util.Constants;
import org.apache.wicket.markup.html.WebPage;

@AuthorizeInstantiation(Constants.PRIV_DASHBOARD_SERVERS)
public class AddServerPage extends ServersMenuGroup {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(AddServerPage.class);

    private IServerListViewer backPage;
    private ServerListDataProvider dataProvider;

    private String hostname;
    private int port;

    public AddServerPage(IServerListViewer backPage, 
	    ServerListDataProvider dataProvider) {
        super();
        this.backPage = backPage;
        this.dataProvider = dataProvider;
        initPageComponents();
    }

    @SuppressWarnings("unchecked")
    protected void initPageComponents() {

	Form<?> form = new Form("addServerForm",
		new CompoundPropertyModel<AddServerPage>(this));

        form.add(new FeedbackPanel("errorMessages"));

       	form.add(new RequiredTextField<String>("hostname")
                        .add(new ErrorIndicator()));

       	form.add(new RequiredTextField<String>("port")
                        .add(new ErrorIndicator()));

	form.add(new Button("cancelButton") {

            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
                setResponsePage((WebPage)backPage);
            };

        }.setDefaultFormProcessing(false));

        form.add(new Button("saveButton") {

            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
		handleSaveAction();
            };
        });

	add(form);
    }

    private void handleSaveAction() {

	try {
	    dataProvider.addServer(hostname, port);

	    info(getLocalizer().getString("addServer.success",
		    this));

	    backPage.refreshServerList();

            setResponsePage((WebPage)backPage);
	}
	catch (Exception e) {
	    error(getLocalizer().getString("addServer.error",
		    this));
	}
    }

    @Override
    protected Class getActiveMenu() {
	return ServerListPage.class;
    }

    public void setHostname(String value) {
	this.hostname = value;
    }

    public String getHostname() {
	return this.hostname;
    }

    public void setPort(int value) {
	this.port = value;
    }

    public int getPort() {
	return this.port;
    }
}
