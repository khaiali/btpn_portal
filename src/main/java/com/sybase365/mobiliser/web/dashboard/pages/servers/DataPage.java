package com.sybase365.mobiliser.web.dashboard.pages.servers;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.web.dashboard.pages.servers.beans.EhcacheValues;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_DASHBOARD_SERVERS)
public class DataPage extends ServersMenuGroup {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(DataPage.class);

    @SuppressWarnings("unchecked")
    @Override
    protected void initOwnPageComponents() {

	super.initOwnPageComponents();

	final EhcacheValues ehCacheValues = new EhcacheValues(this);

	final Form<?> form = new Form("dataForm",
		new CompoundPropertyModel<EhcacheValues>(ehCacheValues));

	FeedbackPanel feedbackPanel = new FeedbackPanel("errorMessages");

	form.add(feedbackPanel);

	form.add(new Label("closeStatementCount"));
	form.add(new Label("flushCount"));
	form.add(new Label("hibernateStatisticsSupported"));
	form.add(new Label("maxGetTimeMillis"));
	form.add(new Label("minGetTimeMillis"));
	form.add(new Label("optimisticFailureCount"));
	form.add(new Label("prepareStatementCount"));
	form.add(new Label("queryExecutionCount"));
	form.add(new Label("queryExecutionRate"));
	form.add(new Label("regionCachesEnabled"));
	form.add(new Label("sessionCloseCount"));
	form.add(new Label("sessionOpenCount"));
	form.add(new Label("statisticsEnabled"));
	form.add(new Label("successfulTransactionCount"));
	form.add(new Label("transactionCount"));

	add(form);

    }
}
