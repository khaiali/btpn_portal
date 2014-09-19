package com.sybase365.mobiliser.web.common.reports.panels;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.time.DateUtils;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriUtils;

import com.sybase365.mobiliser.util.contract.v5_0.report.beans.MobiliserReportParameter;
import com.sybase365.mobiliser.util.contract.v5_0.report.beans.ReportRequestParameter;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.util.tools.wicketutils.resourceloader.ILookupMapUtility;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.common.components.KeyValue;
import com.sybase365.mobiliser.web.common.components.KeyValueDropDownChoice;
import com.sybase365.mobiliser.web.common.components.KeyValueDropDownChoice.SortProperties;
import com.sybase365.mobiliser.web.common.reports.ReportProxyServlet;
import com.sybase365.mobiliser.web.common.reports.ReportUtil;
import com.sybase365.mobiliser.web.common.reports.components.DynamicComponent;
import com.sybase365.mobiliser.web.common.reports.components.DynamicDateTextField;
import com.sybase365.mobiliser.web.common.reports.components.DynamicDropDown;
import com.sybase365.mobiliser.web.common.reports.components.DynamicTextField;
import com.sybase365.mobiliser.web.common.reports.custom.IContextReport;
import com.sybase365.mobiliser.web.common.reports.custom.IContextReportParameter;
import com.sybase365.mobiliser.web.common.reports.panels.helpers.ParameterHelperPanel;
import com.sybase365.mobiliser.web.distributor.pages.reports.DynamicReportPage;
import com.sybase365.mobiliser.web.util.Constants;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class DynamicReportPanel extends Panel {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(DynamicReportPanel.class);

    private final MobiliserReportParameter report;

    private MobiliserBasePage basePage;

    private final WebMarkupContainer reportContainer;

    private final WebMarkupContainer reportFormContainer;

    private boolean areResultsVisible = false;

    private IContextReport customReportInfo;

    @SpringBean(name = "lookupMapUtilitiesImpl")
    public ILookupMapUtility lookupMapUtility;

    public DynamicReportPanel(String id, MobiliserBasePage basePage,
	    MobiliserReportParameter report, IContextReport customReportInfo) {

	super(id);
	this.basePage = basePage;
	this.report = report;
	this.customReportInfo = customReportInfo;

	reportContainer = new WebMarkupContainer("dynamicReportContainer");
	reportContainer.setVisible(areResultsVisible);

	reportFormContainer = new WebMarkupContainer(
		"dynamicReportFormContainer");
	reportFormContainer.setVisible(!areResultsVisible);
    }

    public DynamicReportPanel(String id, MobiliserBasePage basePage,
	    MobiliserReportParameter report) {
	this(id, basePage, report, null);
    }

    private void buildPage() {

	LOG.debug("report:" + report + " :" + this);
	if (null == report) {
	    LOG.debug("Nothing to do when report is null");
	    return;
	}

	final Form<?> reportForm = new Form("dynamicReportForm",
		new CompoundPropertyModel<DynamicReportPage>(this));

	List<ParameterEntry> components = new ArrayList<ParameterEntry>();

	String dynamicNameId = "dynName";
	String dynamicValueId = "dynValue";

	List<ReportRequestParameter> parameters = report.getParameters();

	if (null == parameters || parameters.isEmpty()) {

	    components.add(new MessagePanel("component", new Label("label",
		    new Model(getLocalizer().getString("report.no.parameters",
			    this)))));

	} else {

	    for (ReportRequestParameter parameter : parameters) {

		if (parameter.getKey().equals("isBatchReport")) {
		    continue;
		}

		if (parameter.getKey().equals(ReportUtil.PARAM_USER_TIMEZONE)
			|| parameter.getKey().equals(
				ReportUtil.PARAM_RAW_USER_TIMEZONE)) {
		    continue;
		}

		if (parameter.getKey().equals(ReportUtil.PARAM_SERVER_TIMEZONE)) {
		    continue;
		}

		if (parameter.getKey().equals(
			ReportUtil.PARAM_MIDNIGHT_USER_TIMEZONE)) {
		    continue;
		}

		Label name = new Label(dynamicNameId, new Model(
			parameter.getDescription()));

		// check if we have to render text field read only and if we
		// have to pre-fill the parameter's value
		boolean enabled = true;

		IContextReportParameter ctxParameter = getReportContextParameter(parameter
			.getDescription());

		if (ctxParameter != null) {
		    enabled = ctxParameter.allowOverride();

		    if (ctxParameter.getValueDefault() != null) {
			parameter.setValue(String.valueOf(ctxParameter
				.getValueDefault()));
		    }
		}

		// MDV: Hm... if we don't give the customer any choice anyway
		// maybe we should just display the value as a label instead of
		// a text field, drop down etc...wicket seems to safeguard the
		// integrity of
		// fields set to readonly on server side...but this isn't really
		// the nicest way to do it...

		Panel value;
		String paramType = parameter.getType();

		LOG.debug("paramType={}", paramType);

		// check if we have to deal with a pre-filled list of choices
		if (ctxParameter != null
			&& ctxParameter.getType().equals(List.class)) {

		    try {

			// we utilise the KeyValueDropDownChocie implementation
			// to keep track of the actual choices
			value = new DynamicDropDown(
				"dynValueContainer",
				new KeyValueDropDownChoice<String, String>(
					dynamicValueId,
					new PropertyModel<String>(parameter,
						"value"),
					(List<KeyValue<String, String>>) ctxParameter
						.getValueList(this,
							lookupMapUtility),
					new SortProperties(false, true))
					.setRequired(true).setEnabled(enabled)
					.setOutputMarkupId(true)
					.add(new ErrorIndicator()));

		    } catch (Exception e) {
			LOG.warn(e.toString(), e);

			// if creating the drop down choice fails, we at
			// least display a text box which can be filled
			value = new DynamicTextField("dynValueContainer",
				new TextField<String>(dynamicValueId,
					new PropertyModel(parameter, "value"))
					.setRequired(true).setEnabled(enabled)
					.setOutputMarkupId(true)
					.add(new ErrorIndicator()));
		    }

		} else if (Date.class.getName().equals(paramType)) {
		    //
		    // show the time field for all date and datetime fields,
		    // although the time part is ignored
		    // by the reporting backend if the field is date only.
		    //
		    value = new DynamicDateTextField("dynValueContainer",
			    new TextField(dynamicValueId, new PropertyModel(
				    parameter, "value")).setRequired(true)
				    .setEnabled(enabled)
				    .setOutputMarkupId(true)
				    .add(new ErrorIndicator()), true);

		} else if (Double.class.getName().equals(paramType)) {

		    value = new DynamicTextField("dynValueContainer",
			    new TextField<Double>(dynamicValueId,
				    new PropertyModel(parameter, "value"))
				    .setRequired(true).setEnabled(enabled)
				    .setOutputMarkupId(true)
				    .add(new ErrorIndicator()));

		} else {

		    value = new DynamicTextField("dynValueContainer",
			    new TextField<String>(dynamicValueId,
				    new PropertyModel(parameter, "value"))
				    .setRequired(true).setEnabled(enabled)
				    .setOutputMarkupId(true)
				    .add(new ErrorIndicator()));

		}

		ParameterEntryPanel dynamicParameter = new ParameterEntryPanel(
			"component", name, value);

		dynamicParameter.setOutputMarkupId(true);

		Panel helper = null;

		if (ctxParameter != null) {
		    helper = ctxParameter.getHelperPanel(getBasePage(),
			    dynamicParameter, parameter,
			    (DynamicComponent) value);
		}

		if (helper != null) {
		    helper.setOutputMarkupId(true);
		    dynamicParameter.add(helper);
		} else {
		    dynamicParameter.add(new ParameterHelperPanel(
			    "dynValueHelper"));
		}

		components.add(dynamicParameter);
	    }
	}

	reportForm.addOrReplace(new ListView("components", components) {
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void populateItem(ListItem item) {
		item.add((Panel) item.getModelObject());
	    }

	}.setReuseItems(true));

	reportForm.addOrReplace(getButtonForOnlineType());

	final FeedbackPanel feedback = new FeedbackPanel("errorMessages");
	feedback.setOutputMarkupId(true);
	reportForm.addOrReplace(feedback);

	reportForm.addOrReplace(new Label("report.title", new Model(report
		.getDescription())));
	reportFormContainer.addOrReplace(reportForm);
	addOrReplace(reportFormContainer);
	addOrReplace(reportContainer);
	areResultsVisible = false;
    }

    /**
     * @return
     */
    private Button getButtonForOnlineType() {
	return new Button("reportSubmission") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		areResultsVisible = true;
		final String serverUrl = getBasePage().getConfiguration()
			.getReportServerUrl();

		Map<String, String> reportParameters = new HashMap<String, String>();

		// set the report itself
		reportParameters.put("rpt",
			DynamicReportPanel.this.report.getKey());

		Locale locale = this.getRequest().getLocale();
		if (!report.getParameters().isEmpty()) {
		    for (ReportRequestParameter parameter : report
			    .getParameters()) {

			if (ReportUtil.PARAM_MIDNIGHT_USER_TIMEZONE
				.equals(parameter.getKey())) {
			    final Calendar cal = Calendar.getInstance();
			    cal.setTimeZone(getBasePage()
				    .getMobiliserWebSession().getTimeZone());

			    reportParameters.put(
				    parameter.getKey(),
				    String.valueOf(DateUtils
					    .truncate(cal, Calendar.DATE)
					    .getTime().getTime()));
			} else if (Date.class.getName().equals(
				parameter.getType())) {
			    // Try to convert to milliseconds so the backend
			    // service does not
			    // need to deal with date format parsing.
			    // dateformat is locked down by the html
			    try {

				final SimpleDateFormat df = new SimpleDateFormat(
					Constants.DATE_FORMAT_PATTERN_PARSE,
					locale == null ? Locale.getDefault()
						: locale);

				df.setTimeZone(getBasePage()
					.getMobiliserWebSession().getTimeZone());

				final Date date = df
					.parse(parameter.getValue());

				reportParameters.put(parameter.getKey(),
					String.valueOf(date.getTime()));
			    } catch (ParseException pe) {
				LOG.error(pe.toString(), pe);
			    }
			} else if (ReportUtil.PARAM_USER_TIMEZONE
				.equals(parameter.getKey())
				|| ReportUtil.PARAM_RAW_USER_TIMEZONE
					.equals(parameter.getKey())) {

			    reportParameters.put(parameter.getKey(),
				    getBasePage().getMobiliserWebSession()
					    .getTimeZone().getID());

			} else if (ReportUtil.PARAM_SERVER_TIMEZONE
				.equals(parameter.getKey())) {

			    // leave empty will be filled by server
			    reportParameters.put(parameter.getKey(), "");

			} else {

			    reportParameters.put(parameter.getKey(),
				    parameter.getValue());
			}
		    }
		}

		final String requestUrl;
		try {
		    requestUrl = UriUtils.encodeUriComponents(null, null, null,
			    null, null, serverUrl, "newsession", null, "UTF-8");
		} catch (UnsupportedEncodingException e) {
		    // should not happen, UTF-8 is always supported
		    throw new IllegalStateException(e);
		}

		LOG.debug("URL is:" + requestUrl);

		// not nice, but the only way to hide our report request
		// parameters from being exposed in the rendered page's source
		// code (the ReportProxyServlet has to access these via the
		// HttpSession instead of relying on the query string in case of
		// a GET request)
		HttpSession session = ((ServletWebRequest) getRequest())
			.getHttpServletRequest().getSession();
		session.setAttribute(
			ReportProxyServlet.REPORT_SESSION_PARAMETERS,
			reportParameters);

		reportContainer.addOrReplace(new WebMarkupContainer(
			"reportDetailsFrame") {
		    private static final long serialVersionUID = 1L;

		    @Override
		    protected void onComponentTag(ComponentTag tag) {

			super.onComponentTag(tag);
			tag.put("src", requestUrl);
		    }

		});
		reportContainer.addOrReplace(new Label("report.title",
			new Model(report.getDescription())));
		reportContainer.setVisible(areResultsVisible);
		reportFormContainer.setVisible(!areResultsVisible);
	    }
	};
    }

    @Override
    protected void onBeforeRender() {
	super.onBeforeRender();
	buildPage();
    }

    protected IContextReportParameter getReportContextParameter(String name) {

	if (this.customReportInfo == null
		|| this.customReportInfo.getContextParameters() == null) {
	    return null;
	}

	return this.customReportInfo.getContextParameters().get(name);
    }

    protected MobiliserBasePage getBasePage() {
	return this.basePage;
    }

    public void resetPage() {
	areResultsVisible = false;

	//
	// Guard against NPE.
	//
	if (reportContainer != null) {
	    reportContainer.setVisible(areResultsVisible);
	}
	if (reportFormContainer != null) {
	    reportFormContainer.setVisible(!areResultsVisible);
	}
    }

}
