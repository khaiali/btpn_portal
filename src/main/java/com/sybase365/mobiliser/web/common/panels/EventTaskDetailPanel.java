package com.sybase365.mobiliser.web.common.panels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.web.dashboard.pages.servers.ServersMenuGroup;

public abstract class EventTaskDetailPanel extends Panel {
    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(EventTaskHandlerDetailPanel.class);

    Map<String, String> detail;

    public EventTaskDetailPanel(String id, Map<String, String> detail) {
	super(id);
	this.detail = detail;
	constructPanel();
    }

    private void constructPanel() {
	final Form<?> form = new Form("detailForm",
		new CompoundPropertyModel<Map<String, String>>(detail));

	FeedbackPanel feedbackPanel = new FeedbackPanel("errorMessages");

	form.add(feedbackPanel);

	List<String> detailList = new ArrayList<String>(detail.keySet());
	form.add(new ListView("detailListView", detailList) {

	    @Override
	    protected void populateItem(ListItem item) {
		final String key = (String) item.getModelObject();

		item.add(new Label("key", key));
		item.add(new Label("value", detail.get(key)));

	    }

	});

	form.add(new Button("back") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		setResponsePage(getBackResponsePage());
	    }

	});

	add(form);
    }

    protected abstract ServersMenuGroup getBackResponsePage();
}
