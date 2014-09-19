package com.sybase365.mobiliser.web.dashboard.pages.home.job;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.validation.validator.StringValidator;

import com.sybase365.mobiliser.money.contract.v5_0.system.CreateJobRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.CreateJobResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.UpdateJobRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.UpdateJobResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.Job;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.common.components.KeyValue;
import com.sybase365.mobiliser.web.common.components.KeyValueDropDownChoice;

public class JobPanel extends Panel {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(JobPanel.class);

    private Job job;

    private WebPage backPage;
    private MobiliserBasePage mobBasePage;

    private int implementationType;
    private String active;

    private Boolean isNew;

    public JobPanel(String component, final Job job, 
	    WebPage backPage, MobiliserBasePage mobBasePage) {

	super(component);

	if (job == null) {
	    this.isNew = Boolean.TRUE;
	    this.job = new Job();
	} else {
	    this.isNew = Boolean.FALSE;
	    this.job = job;
	}

	this.backPage = backPage;
	this.mobBasePage = mobBasePage;

	initPageComponents();
    }

    @SuppressWarnings({"unchecked", "serial"})
    protected void initPageComponents() {

	Form<?> form = new Form("addJobForm",
		new CompoundPropertyModel<JobPanel>(job));

	form.add(
		new RequiredTextField<String>("handlerName")
		.add(StringValidator.lengthBetween(1, 200)))
		.add(new ErrorIndicator());

	List<KeyValue<Integer, String>> implementationTypeOptions = 
		new ArrayList<KeyValue<Integer, String>>();
	implementationTypeOptions.add(new KeyValue<Integer,String>(2,"Class"));
	implementationTypeOptions.add(new KeyValue<Integer,String>(3,"Bean"));

	form.add(new TextField<String>("jobName"));

	form.add(new KeyValueDropDownChoice<Integer, String>( 
		"implementationTypeId", implementationTypeOptions)
		.setRequired(true)
		.add(new ErrorIndicator()));

	form.add(new RequiredTextField<String>("implementationUrl")
		.add(StringValidator.lengthBetween(1, 200)))
		.add(new ErrorIndicator());

	form.add(new RequiredTextField<String>("schedule")
		.add(StringValidator.lengthBetween(1, 80))).add(
		new ErrorIndicator());

	form.add(new TextField<String>("parameters"));

	List<KeyValue<Boolean, String>> activeOptions = 
		new ArrayList<KeyValue<Boolean, String>>();
	activeOptions.add(new KeyValue<Boolean,String>(Boolean.TRUE,"Yes"));
	activeOptions.add(new KeyValue<Boolean,String>(Boolean.FALSE,"No"));

	form.add(new KeyValueDropDownChoice<Boolean, String>( 
		"active", activeOptions)
		.setRequired(true)
		.add(new ErrorIndicator()));

	form.add(new RequiredTextField<String>("maxDelay")
		.add(new ErrorIndicator()));

	form.add(new RequiredTextField<String>("maxDuration")
		.add(new ErrorIndicator()));

	form.add(new Button("save") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		if (isNew) {
		    addJob();
	    	}
		else {
		    editJob();
		}
	    }
	;
	});

	form.add(new Button("cancel") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleBack();
	    }
	;
	}.setDefaultFormProcessing(false));

	form.add(new FeedbackPanel("errorMessages"));

	add(form);
    }

    private void handleBack() {
	setResponsePage(backPage);
    }

    private void editJob() {

	LOG.debug("editJob() -> {}", job);

	try {
	    UpdateJobRequest request = mobBasePage.getNewMobiliserRequest(UpdateJobRequest.class);
	    request.setJob(job);
	    
	    UpdateJobResponse response = mobBasePage.wsJobClient.updateJob(request);

	    if (!mobBasePage.evaluateMobiliserResponse(response)) {
		LOG.warn("# An error occurred while updating the job");
		return;
	    }

	} catch (Exception e) {
	    LOG.error("# An error occurred while updating job [{}]",
		    job.getJobId(), e);

	    error(getLocalizer().getString("addJob.edit.error", this));
	}

	info(getLocalizer().getString("addJob.edit.success", this));

	handleBack();
    }

    private void addJob() {

	LOG.debug("addJob() -> {}", job);

	try {
	    CreateJobRequest request = mobBasePage.getNewMobiliserRequest(CreateJobRequest.class);
	    request.setJob(job);
	    
	    CreateJobResponse response = mobBasePage.wsJobClient.createJob(request);

	    if (!mobBasePage.evaluateMobiliserResponse(response)) {
		LOG.warn("# An error occurred while creating the job");
		return;
	    }

	} catch (Exception e) {
	    LOG.error("# An error occurred while creating job [{}]",
		    job.getJobId(), e);

	    error(getLocalizer().getString("addJob.edit.error", this));
	}

	info(getLocalizer().getString("addJob.add.success", this));

	handleBack();
    }

    public void setJob(Job job) {
	this.job = job;
    }

    public Job getJob() {
	return this.job;

    }

    public void setImplementationType(int value) {
	this.implementationType = value;
    }

    public int getImplementationType() {
	return this.implementationType;
    }

    public void setActive(String value) {
	this.active = value;
    }

    public String getActive() {
	return this.active;
    }

}