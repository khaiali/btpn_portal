package com.sybase365.mobiliser.web.dashboard.pages.home.job;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.system.DeleteJobAndHistoryRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.DeleteJobAndHistoryResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.Job;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.common.dataproviders.JobDataProvider;
import com.sybase365.mobiliser.web.dashboard.pages.home.HomeMenuGroup;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_DASHBOARD_JOBS)
public class JobListPage extends HomeMenuGroup {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(JobListPage.class);

    private final WebPage thisPage = this;

    // Data Model for table list
    private JobDataProvider dataProvider;

    List<Job> selectedJob = new ArrayList<Job>();
    List<Job> jobList;

    // Table Items
    private String totalItemString = null;
    private int startIndex = 0;
    private int endIndex = 0;

    private static final String WICKET_ID_navigator = "navigator";
    private static final String WICKET_ID_totalItems = "totalItems";
    private static final String WICKET_ID_startIndex = "startIndex";
    private static final String WICKET_ID_endIndex = "endIndex";
    private static final String WICKET_ID_pageable = "pageable";
    private static final String WICKET_ID_handlerName = "handlerName";
    private static final String WICKET_ID_schedule = "schedule";
    private static final String WICKET_ID_implementationUrl = "implementationUrl";
    private static final String WICKET_ID_active = "active";
    private static final String WICKET_ID_editAction = "editAction";
    private static final String WICKET_ID_removeAction = "removeAction";
    private static final String WICKET_ID_noItemsMsg = "noItemsMsg";

    public JobListPage() {
	super();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();

	Form<?> form = new Form("jobListForm",
		new CompoundPropertyModel<JobListPage>(this));

	add(form);

	form.add(new FeedbackPanel("errorMessages"));

	createJobListDataView(form);
    }

    @Override
    protected Class getActiveMenu() {
	return this.getClass();
    }

    protected void editJob(Job job) {
	setResponsePage(new JobEditPage(job, thisPage));
    }

    private void removeJob(Job job) {
	try {
	    DeleteJobAndHistoryRequest request = getNewMobiliserRequest(DeleteJobAndHistoryRequest.class);
	    request.setJobId(job.getJobId());

	    DeleteJobAndHistoryResponse response = wsJobClient
		    .deleteJobAndHistory(request);

	    if (!evaluateMobiliserResponse(response)) {
		LOG.warn("# An error occurred while deleting the job");
		return;
	    }

	} catch (Exception e) {
	    LOG.error(
		    "# An error occurred while deleting job [{}] from the job list",
		    job.getJobId(), e);

	    error(getLocalizer().getString("jobList.remove.error", this));
	}
    };

    private void createJobListDataView(Form form) {

	dataProvider = new JobDataProvider(WICKET_ID_handlerName, this);

	jobList = new ArrayList<Job>();

	final Long customerId = getMobiliserWebSession().getLoggedInCustomer()
		.getCustomerId();

	form.add(new Button("addJob") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		setResponsePage(new JobAddPage(null, thisPage));
	    };
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
		    error(getLocalizer().getString("jobList.load.error", this));
		}

		refreshTotalItemCount();

		super.onBeforeRender();
	    }

	    @Override
	    protected void populateItem(final Item<Job> item) {

		final Job entry = item.getModelObject();

		jobList.add(entry);

		item.add(new Label(WICKET_ID_handlerName, entry
			.getHandlerName()));

		item.add(new Label(WICKET_ID_implementationUrl, entry
			.getImplementationUrl()));

		item.add(new Label(WICKET_ID_schedule, entry.getSchedule()));

		// Edit Action
		Link<Job> editLink = new Link<Job>(WICKET_ID_editAction,
			item.getModel()) {
		    @Override
		    public void onClick() {
			Job entry = (Job) getModelObject();
			editJob(entry);
		    }
		};
		item.add(editLink);

		// Send Money Action
		Link removeLink = new Link<Job>(WICKET_ID_removeAction,
			item.getModel()) {
		    @Override
		    public void onClick() {
			Job entry = (Job) getModelObject();
			removeJob(entry);
		    }
		};

		removeLink
			.add(new SimpleAttributeModifier("onclick",
				"return confirm('"
					+ getLocalizer().getString(
						"jobList.remove.confirm", this)
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
	form.add(dataView);

	form.add(new MultiLineLabel(WICKET_ID_noItemsMsg, getLocalizer()
		.getString("jobList.noItemsMsg", this)
		+ "\n"
		+ getLocalizer().getString("jobList.addJobHelp", this)) {
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
	form.add(new CustomPagingNavigator(WICKET_ID_navigator, dataView));

	form.add(new Label(WICKET_ID_totalItems, new PropertyModel<String>(
		this, "totalItemString")));

	form.add(new Label(WICKET_ID_startIndex, new PropertyModel(this,
		"startIndex")));

	form.add(new Label(WICKET_ID_endIndex, new PropertyModel(this,
		"endIndex")));
    }
}