package com.sybase365.mobiliser.web.common.reports.panels;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.IRequestTarget;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.util.io.Streams;

import com.sybase365.mobiliser.util.contract.v5_0.report.GeneratedReportBeanRequest;
import com.sybase365.mobiliser.util.contract.v5_0.report.ReportBeanResponse;
import com.sybase365.mobiliser.util.contract.v5_0.report.beans.GeneratedReportRequestBean;
import com.sybase365.mobiliser.util.contract.v5_0.report.beans.ReportResponseBean;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.common.dataproviders.StoredReportDataProvider;
import com.sybase365.mobiliser.web.util.Constants;

public class DownloadReportPanel extends Panel {

    private int startIndex = 0;
    private int endIndex = 0;

    private static final String WICKET_ID_navigator = "navigator";
    private static final String WICKET_ID_totalItems = "totalItems";
    private static final String WICKET_ID_startIndex = "startIndex";
    private static final String WICKET_ID_endIndex = "endIndex";
    private static final String WICKET_ID_pageable = "pageable";
    private static final String WICKET_ID_reportName = "reportName";
    private static final String WICKET_ID_downLoadAction = "download";
    private static final String WICKET_ID_noItemsMsg = "noReports";

    java.util.List<String> reportList;

    String totalItemString;

    private StoredReportDataProvider dataProvider;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(DownloadReportPanel.class);

    public DownloadReportPanel(String id, MobiliserBasePage basePage) {
	super(id);
	LOG.debug("Inside DownloadReportPage");
	createDownLoadReportDataView(basePage);

    }

    private void createDownLoadReportDataView(final MobiliserBasePage parent) {

	final Long customerId = parent.getMobiliserWebSession()
		.getLoggedInCustomer().getCustomerId();

	dataProvider = new StoredReportDataProvider(WICKET_ID_reportName,
		parent, customerId);

	reportList = new ArrayList<String>();

	final DataView<String> dataView = new DataView<String>(
		WICKET_ID_pageable, dataProvider) {

	    @Override
	    protected void onBeforeRender() {

		try {
		    dataProvider.loadStoredReports();
		    refreshTotalItemCount();
		} catch (DataProviderLoadException dple) {
		    LOG.error(
			    "# An error occurred while loading stored report list",
			    dple);
		    error(getLocalizer().getString(
			    "report.download.load.error", parent));
		}

		refreshTotalItemCount();

		super.onBeforeRender();
	    }

	    @Override
	    protected void populateItem(final Item<String> item) {

		final String entry = item.getModelObject();

		reportList.add(entry);

		item.add(new Label(WICKET_ID_reportName, entry));

		Link removeLink = new Link<String>(WICKET_ID_downLoadAction,
			item.getModel()) {
		    @Override
		    public void onClick() {

			GeneratedReportBeanRequest request = new GeneratedReportBeanRequest();
			GeneratedReportRequestBean requestBean = new GeneratedReportRequestBean();
			requestBean.setKey(entry);
			requestBean.setOwner(customerId);
			request.setReportElement(requestBean);
			ReportBeanResponse response = parent.wsReportClient
				.getGeneratedReport(request);

			StringTokenizer st = new StringTokenizer(entry, ".");
			String contentType = "";
			if (st.countTokens() == 2) {
			    st.nextToken();
			    contentType = "application/" + st.nextToken();
			}
			LOG.debug("Contenttype of element is " + contentType);

			downloadReport(response.getReportElement(), entry,
				contentType, parent);

		    }
		};

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
	this.addOrReplace(dataView);

	this.addOrReplace(new MultiLineLabel(WICKET_ID_noItemsMsg,
		getLocalizer().getString("report.batch.noItemsMsg", parent)) {
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
	this.addOrReplace(new CustomPagingNavigator(WICKET_ID_navigator,
		dataView));

	this.addOrReplace(new Label(WICKET_ID_totalItems,
		new PropertyModel<String>(this, "totalItemString")));

	this.addOrReplace(new Label(WICKET_ID_startIndex, new PropertyModel(
		this, "startIndex")));

	this.addOrReplace(new Label(WICKET_ID_endIndex, new PropertyModel(this,
		"endIndex")));
    }

    private void downloadReport(final ReportResponseBean reportBean,
	    final String name, final String contentType, 
	    final MobiliserBasePage parent) {
	try {
	    getRequestCycle().setRequestTarget(new IRequestTarget() {

		@Override
		public void respond(RequestCycle requestCycle) {
		    try {
			InputStream attStream = new ByteArrayInputStream(
				reportBean.getResult());

			WebResponse webResponse = (WebResponse) getResponse();
			webResponse.setAttachmentHeader(name);
			webResponse.setContentType(contentType);

			Streams.copy(attStream, webResponse.getOutputStream());
		    } catch (Exception e) {
			LOG.error("Error in download report ", e);
		    }
		}

		@Override
		public void detach(RequestCycle arg0) {
		    // nothing to do here
		}
	    });
	} catch (Exception e) {
	    LOG.error("Error in download report ", e);
	    error(getLocalizer().getString("report.download.error", parent));
	}
    }

    /**
     * @return the totalItemString
     */
    public String getTotalItemString() {
	return totalItemString;
    }

    /**
     * @param totalItemString
     *            the totalItemString to set
     */
    public void setTotalItemString(String totalItemString) {
	this.totalItemString = totalItemString;
    }

}
