package com.sybase365.mobiliser.web.common.reports.panels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.behavior.SimpleAttributeModifier;
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
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.system.DeleteJobAndHistoryRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.DeleteJobAndHistoryResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.Job;
import com.sybase365.mobiliser.util.contract.v5_0.report.beans.MobiliserReportParameter;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.common.dataproviders.ReportJobDataProvider;
import com.sybase365.mobiliser.web.common.reports.ReportUtil;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

public abstract class BatchReportOverviewPanel<TL> extends Panel {

    private static final Logger LOG = LoggerFactory
	    .getLogger(BatchReportOverviewPanel.class);

    // Data Model for table list
    private ReportJobDataProvider dataProvider;
    private final MobiliserReportParameter report;
    List<Job> selectedJob = new ArrayList<Job>();
    List<Job> jobList;

    // Table Items
    private String totalItemString = null;
    private int startIndex = 0;
    private int endIndex = 0;

    private final MobiliserBasePage basePage;

    private static final String WICKET_ID_navigator = "navigator";
    private static final String WICKET_ID_totalItems = "totalItems";
    private static final String WICKET_ID_startIndex = "startIndex";
    private static final String WICKET_ID_endIndex = "endIndex";
    private static final String WICKET_ID_pageable = "pageable";
    private static final String WICKET_ID_isActive = "isActive";
    private static final String WICKET_ID_lastExecution = "lastExecution";
    private static final String WICKET_ID_schedule = "schedule";
    private static final String WICKET_ID_params = "params";
    private static final String WICKET_ID_removeAction = "removeAction";
    private static final String WICKET_ID_noItemsMsg = "noItemsMsg";
    private static final String WICKET_ID_format = "formatValue";
    private static final String WICKET_ID_editAction = "editAction";

    public BatchReportOverviewPanel(String id, MobiliserReportParameter report,
	    MobiliserBasePage basePage) {
	super(id);
	this.report = report;
	this.basePage = basePage;
    }

    abstract protected void editReportJob(Job job,
	    MobiliserReportParameter report, MobiliserBasePage basePage);

    abstract protected void addReportJob(MobiliserReportParameter report,
	    MobiliserBasePage basePage);

    private void createJobListDataView(Form form, String reportName) {

	final Long customerId = basePage.getMobiliserWebSession()
		.getLoggedInCustomer().getCustomerId();

	dataProvider = new ReportJobDataProvider(WICKET_ID_lastExecution,
		basePage, customerId, reportName);

	jobList = new ArrayList<Job>();

	form.addOrReplace(new Button("addJob") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		addReportJob(report, basePage);
	    }

	});

	final DataView<Job> dataView = new DataView<Job>(WICKET_ID_pageable,
		dataProvider) {

	    @Override
	    protected void onBeforeRender() {

		try {
		    dataProvider.loadJobs();
		    refreshTotalItemCount();
		} catch (DataProviderLoadException dple) {
		    LOG.error(
			    "# An error occurred while loading job and family list",
			    dple);
		    error(getLocalizer().getString("report.batch.load.error",
			    this));
		}

		refreshTotalItemCount();

		super.onBeforeRender();
	    }

	    @Override
	    protected void populateItem(final Item<Job> item) {

		ObjectMapper mapper = new ObjectMapper();

		final Job entry = item.getModelObject();

		jobList.add(entry);

		item.add(new Label(WICKET_ID_isActive, entry.isActive() ? "Y"
			: "N"));

		item.add(new Label(WICKET_ID_lastExecution, PortalUtils
			.getFormattedDateTime(entry.getLastExecution(),
				basePage.getMobiliserWebSession().getLocale())));

		try {

		    StringBuffer paramListString = new StringBuffer();

		    Map<String, Object> userData = mapper.readValue(
			    entry.getParameters(), Map.class);
		    ArrayList<Map<String, String>> paramList = (ArrayList<Map<String, String>>) userData
			    .get("reportParameters");

		    item.add(new Label(WICKET_ID_format,
			    userData.get("format") != null ? userData.get(
				    "format").toString() : "-"));

		    for (Map<String, String> cur : paramList) {
			if (!(cur.containsValue(ReportUtil.PARAM_USER_TIMEZONE)
				|| cur.containsValue(ReportUtil.PARAM_SERVER_TIMEZONE)
				|| cur.containsValue(ReportUtil.PARAM_MIDNIGHT_USER_TIMEZONE) || cur
				.containsValue(ReportUtil.PARAM_RAW_USER_TIMEZONE))) {

			    paramListString.append(cur.get("description"));
			    paramListString.append("=");
			    paramListString.append(cur.get("value"));
			    paramListString.append("\n");
			}

		    }
		    item.add(new Label(WICKET_ID_params, paramListString
			    .toString()));

		} catch (JsonParseException e) {
		    LOG.error("Exception while parsing reportparams");
		} catch (JsonMappingException e) {
		    LOG.error("Exception while mapping reportparams");
		} catch (IOException e) {
		    LOG.error("Exception while reading reportparams");
		}

		item.add(new Label(WICKET_ID_schedule, entry.getSchedule()));

		// Edit Action
		Link<Job> editLink = new Link<Job>(WICKET_ID_editAction,
			item.getModel()) {
		    @Override
		    public void onClick() {
			Job entry = (Job) getModelObject();
			editReportJob(entry, report, basePage);
		    }
		};
		item.add(editLink);

		// Remove job
		Link removeLink = new Link<Job>(WICKET_ID_removeAction,
			item.getModel()) {
		    @Override
		    public void onClick() {
			Job entry = (Job) getModelObject();
			removeReportJob(entry, report, basePage);
		    }
		};

		removeLink.add(new SimpleAttributeModifier("onclick",
			"return confirm('"
				+ getLocalizer().getString(
					"report.batch.remove.confirm", this)
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

	    private void refreshTotalItemCount() {
		totalItemString = new Integer(dataProvider.size()).toString();
		int total = getItemCount();
		if (total > 0) {
		    startIndex = getCurrentPage() * getItemsPerPage() + 1;
		    endIndex = startIndex + getItemsPerPage() - 1;
		    if (endIndex > total)
			endIndex = total;
		} else {
		    startIndex = 0;
		    endIndex = 0;
		}
	    }

	};

	dataView.setItemsPerPage(10);
	form.addOrReplace(dataView);

	form.addOrReplace(new MultiLineLabel(WICKET_ID_noItemsMsg,
		getLocalizer().getString("report.batch.noItemsMsg", this)
			+ "\n"
			+ getLocalizer().getString("report.batch.addJobHelp",
				this)) {
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
    }

    @Override
    protected void onBeforeRender() {

	super.onBeforeRender();

	Form<?> form = new Form("batchReportListForm",
		new CompoundPropertyModel<TL>(this));

	addOrReplace(form);

	form.addOrReplace(new FeedbackPanel("errorMessages"));

	createJobListDataView(form, report.getName());
	form.addOrReplace(new Label("report.title", new Model(report
		.getDescription())));

    }

    protected void removeReportJob(Job job, MobiliserReportParameter report,
	    MobiliserBasePage basePage) {
	try {
	    DeleteJobAndHistoryRequest request = basePage
		    .getNewMobiliserRequest(DeleteJobAndHistoryRequest.class);
	    request.setJobId(job.getJobId());

	    DeleteJobAndHistoryResponse response = basePage.wsJobClient
		    .deleteJobAndHistory(request);

	    if (!basePage.evaluateMobiliserResponse(response)) {
		LOG.warn("# An error occurred while deleting the job");
		return;
	    }

	} catch (Exception e) {
	    LOG.error(
		    "# An error occurred while deleting job [{}] from the job list",
		    job.getJobId(), e);

	    error(getLocalizer().getString("report.batch.remove.error", this));
	}
    }
}
