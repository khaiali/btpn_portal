package com.sybase365.mobiliser.web.dashboard.pages.servers;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.web.dashboard.pages.servers.beans.EnvironmentValues;
import com.sybase365.mobiliser.web.util.Constants;
import org.apache.wicket.markup.html.basic.MultiLineLabel;

@AuthorizeInstantiation(Constants.PRIV_DASHBOARD_SERVERS)
public class InformationPage extends ServersMenuGroup {

	private static final long serialVersionUID = 1L;
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(InformationPage.class);
	private FeedbackPanel feedbackPanel;

	@SuppressWarnings("unchecked")
	@Override
	protected void initOwnPageComponents() {

		super.initOwnPageComponents();

		EnvironmentValues envBean = new EnvironmentValues(this);

		final Form<?> form = new Form("informationForm",
			new CompoundPropertyModel<EnvironmentValues>(envBean));

		feedbackPanel = new FeedbackPanel("errorMessages");

		form.add(feedbackPanel);

		form.add(new Label("os"));
		form.add(new Label("version"));
		form.add(new Label("architecture"));
		form.add(new Label("processors"));
		form.add(new Label("committedVirtualMemorySize"));
		form.add(new Label("totalPhysicalMemorySize"));
		form.add(new Label("freePhysicalMemorySize"));
		form.add(new Label("totalSwapSpaceSize"));
		form.add(new Label("freeSwapSpaceSize"));

		form.add(new Label("name"));
		form.add(new Label("vmName"));
		form.add(new Label("vmVersion"));
		form.add(new Label("vmVendor"));
		form.add(new Label("upTime"));
		form.add(new Label("startTime"));
		form.add(new Label("compiler"));

		form.add(new MultiLineLabel("classPath"));
		form.add(new MultiLineLabel("bootClassPath"));
		form.add(new MultiLineLabel("libraryPath"));

		add(form);
	}
}
