package com.sybase365.mobiliser.web.dashboard.pages.home.job;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;

import com.sybase365.mobiliser.money.contract.v5_0.system.beans.Job;
import com.sybase365.mobiliser.web.dashboard.pages.home.HomeMenuGroup;
import com.sybase365.mobiliser.web.util.Constants;
import org.apache.wicket.markup.html.WebPage;

@AuthorizeInstantiation(Constants.PRIV_DASHBOARD_JOBS)
public class JobEditPage extends HomeMenuGroup {

    private static final long serialVersionUID = 1L;

    private WebPage backPage;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(JobEditPage.class);

    private Job job;

    public JobEditPage(final Job job, WebPage backPage) {
	super();
	this.job = job;
	this.backPage = backPage;
	initPageComponents();
    }

    @SuppressWarnings({"unchecked", "serial"})
    protected void initPageComponents() {
	add(new JobPanel("jobPanel", job, backPage, this));
    }

    @Override
    protected Class getActiveMenu() {
	return JobListPage.class;
    }
}
