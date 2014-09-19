package com.sybase365.mobiliser.web.common.reports.panels;

import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.time.DateUtils;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.sybase365.mobiliser.money.contract.v5_0.system.CreateJobRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.UpdateJobRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.UpdateJobResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.Job;
import com.sybase365.mobiliser.util.contract.v5_0.report.beans.MobiliserReportParameter;
import com.sybase365.mobiliser.util.contract.v5_0.report.beans.ReportRequestParameter;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.util.tools.wicketutils.resourceloader.ILookupMapUtility;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.common.components.KeyValue;
import com.sybase365.mobiliser.web.common.components.KeyValueDropDownChoice;
import com.sybase365.mobiliser.web.common.components.KeyValueDropDownChoice.SortProperties;
import com.sybase365.mobiliser.web.common.panels.SimpleCronPanel;
import com.sybase365.mobiliser.web.common.reports.ReportUtil;
import com.sybase365.mobiliser.web.common.reports.components.DynamicComponent;
import com.sybase365.mobiliser.web.common.reports.components.DynamicCronTextField;
import com.sybase365.mobiliser.web.common.reports.components.DynamicDateTextField;
import com.sybase365.mobiliser.web.common.reports.components.DynamicDropDown;
import com.sybase365.mobiliser.web.common.reports.components.DynamicTextField;
import com.sybase365.mobiliser.web.common.reports.custom.IContextReport;
import com.sybase365.mobiliser.web.common.reports.custom.IContextReportParameter;
import com.sybase365.mobiliser.web.common.reports.panels.helpers.ParameterHelperPanel;
import com.sybase365.mobiliser.web.util.Constants;

public class BatchReportPanel extends Panel {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(BatchReportPanel.class);

    private Job job;
    private final MobiliserReportParameter report;

    private WebPage backPage;
    private MobiliserBasePage mobBasePage;

    private int implementationType;
    private String active;
    private SimpleCronPanel scP;
    private Boolean isNew;
    private String type;
    private IContextReport customReportInfo;

    @SpringBean(name = "lookupMapUtilitiesImpl")
    public ILookupMapUtility lookupMapUtility;

    public BatchReportPanel(String component, final Job job,
	    final MobiliserReportParameter report, WebPage backPage,
	    MobiliserBasePage mobBasePage, IContextReport customReportInfo) {

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
	this.report = report;
	this.customReportInfo = customReportInfo;

	initPageComponents();
    }

    /**
     * @param feedback
     * @return
     */

    private String parseReportParams(MobiliserReportParameter reportParams)
	    throws JsonGenerationException, JsonMappingException, IOException {

	ObjectMapper mapper = new ObjectMapper();
	StringWriter sw = new StringWriter();
	Map<String, Object> reportParamsParent = new HashMap<String, Object>();

	// Where the "name" parameter much match the report name already
	// provisioned in the system.
	// Also note that the date parameter values are in milliseconds from
	// epoch.

	reportParamsParent.put("owner", mobBasePage.getMobiliserWebSession()
		.getLoggedInCustomer().getCustomerId());
	reportParamsParent.put("name", report.getName());
	reportParamsParent.put("locale", mobBasePage.getMobiliserWebSession()
		.getLocale());
	ArrayList<Map<String, String>> paramL = new ArrayList<Map<String, String>>();

	reportParamsParent.put("format", type);

	Locale locale = this.getRequest().getLocale();

	for (ReportRequestParameter mrp : reportParams.getParameters()) {
	    Map<String, String> parameterStruct = new HashMap<String, String>();

	    // Initialize with original value...
	    String value = mrp.getValue();

	    // and replace with timestamp in case it is a date type
	    if (ReportUtil.PARAM_MIDNIGHT_USER_TIMEZONE.equals(mrp.getKey())) {
		final Calendar cal = Calendar.getInstance();
		cal.setTimeZone(mobBasePage.getMobiliserWebSession()
			.getTimeZone());

		value = Long.toString(DateUtils.truncate(cal, Calendar.DATE)
			.getTime().getTime());
	    } else if (Date.class.getName().equals(mrp.getType())) {
		//
		// Try to convert to milliseconds so the backend
		// service does not
		// need to deal with date format parsing.
		// dateformat is locked down by the html
		try {
		    final SimpleDateFormat df = new SimpleDateFormat(
			    Constants.DATE_FORMAT_PATTERN_PARSE,
			    locale == null ? Locale.getDefault() : locale);
		    df.setTimeZone(mobBasePage.getMobiliserWebSession()
			    .getTimeZone());

		    final Date date = df.parse(mrp.getValue());

		    value = Long.toString(date.getTime());
		} catch (ParseException pe) {
		    LOG.error(pe.toString(), pe);
		}
	    } else if (ReportUtil.PARAM_USER_TIMEZONE.equals(mrp.getKey())
		    || ReportUtil.PARAM_RAW_USER_TIMEZONE.equals(mrp.getKey())) {

		value = mobBasePage.getMobiliserWebSession().getTimeZone()
			.getID();

	    } else if (ReportUtil.PARAM_SERVER_TIMEZONE.equals(mrp.getKey())) {

		// leave empty will be filled by server
		value = "";
	    }

	    parameterStruct.put("value", value);
	    parameterStruct.put("key", mrp.getKey());
	    parameterStruct.put("type", mrp.getType());
	    parameterStruct.put("description", mrp.getDescription());
	    paramL.add(parameterStruct);
	}
	reportParamsParent.put("reportParameters", paramL);
	mapper.writeValue(sw, reportParamsParent);
	return sw.getBuffer().toString();

    }

    protected IContextReportParameter getReportContextParameter(String name) {

	if (this.customReportInfo == null
		|| this.customReportInfo.getContextParameters() == null) {
	    return null;
	}

	return this.customReportInfo.getContextParameters().get(name);
    }

    @SuppressWarnings({ "unchecked", "serial" })
    protected void initPageComponents() {

	ObjectMapper mapper = new ObjectMapper();

	final Form<?> reportForm = new Form("dynamicReportForm",
		new CompoundPropertyModel<BatchReportPanel>(job));

	List<ParameterEntry> components = new ArrayList<ParameterEntry>();

	String dynamicNameId = "dynName";
	String dynamicValueId = "dynValue";

	scP = new SimpleCronPanel("cronpanel");

	ArrayList<KeyValue<String, String>> typeChoices = new ArrayList<KeyValue<String, String>>();
	typeChoices.add(new KeyValue<String, String>("pdf", "pdf"));
	typeChoices.add(new KeyValue<String, String>("csv", "csv"));
	typeChoices.add(new KeyValue<String, String>("rtf", "rtf"));

	DropDownChoice<String> typeDrop = new DropDownChoice<String>("format",
		new PropertyModel<String>(this, "type"),
		Arrays.asList(new String[] { "PDF", "CSV", "RTF" }));
	typeDrop.setRequired(true).setEnabled(true).add(new ErrorIndicator());
	Label typeLabel = new Label("formatLabel", new Model("Format"));

	reportForm.addOrReplace(typeDrop);
	reportForm.addOrReplace(typeLabel);

	// Create Cron Field
	Label cronFieldName = new Label(dynamicNameId,
		new Model("CronSchedule"));
	Panel cronFieldValue;
	cronFieldValue = new DynamicCronTextField("dynValueContainer",
		new TextField<String>(dynamicValueId,
			new PropertyModel<String>(scP, "cronSchedule"))
			.setRequired(true).add(new ErrorIndicator()));
	cronFieldValue.setOutputMarkupId(true);
	ParameterEntryPanel cronField = new ParameterEntryPanel("component",
		cronFieldName, cronFieldValue);
	scP.setCronScheduleField(cronFieldValue);
	// add an empty helper for cron field - helper already provided as
	// a popup window
	cronField.add(new ParameterHelperPanel("dynValueHelper"));
	this.addOrReplace(scP);

	// Create dynamic parameters

	List<ReportRequestParameter> parameters = report.getParameters();

	for (ReportRequestParameter parameter : parameters) {

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

	    if (job != null) {

		scP.setCronSchedule(job.getSchedule());

		if (job.getParameters() != null
			&& job.getParameters().length() > 0) {

		    Map<String, Object> userData;
		    try {
			userData = mapper.readValue(job.getParameters(),
				Map.class);

			type = userData.containsKey("format") ? userData.get(
				"format").toString() : "";

			ArrayList<Map<String, String>> paramList = (ArrayList<Map<String, String>>) userData
				.get("reportParameters");

			for (Map<String, String> cur : paramList) {
			    String curJobParam = cur.get("key");
			    if (curJobParam != null
				    && curJobParam.equals(parameter.getKey())) {
				parameter.setValue(cur.get("value"));
				break;
			    }
			}
		    } catch (JsonParseException e1) {
			LOG.error("Error parsing existing values", e1);
		    } catch (JsonMappingException e1) {
			LOG.error("Error parsing existing values", e1);
		    } catch (IOException e1) {
			LOG.error("Error parsing existing values", e1);
		    }
		}

	    }

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

	    Panel value;
	    String paramType = parameter.getType();

	    LOG.debug("paramType={}", paramType);

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
				.setEnabled(enabled).setOutputMarkupId(true)
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
		helper = ctxParameter.getHelperPanel(mobBasePage,
			dynamicParameter, parameter, (DynamicComponent) value);
	    }

	    if (helper != null) {
		helper.setOutputMarkupId(true);
		dynamicParameter.add(helper);
	    } else {
		dynamicParameter
			.add(new ParameterHelperPanel("dynValueHelper"));
	    }

	    components.add(dynamicParameter);
	}

	components.add(cronField);

	reportForm.addOrReplace(new ListView("components", components) {
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void populateItem(ListItem item) {
		item.add((Panel) item.getModelObject());
	    }

	}.setReuseItems(true));

	final FeedbackPanel feedback = new FeedbackPanel("errorMessages");
	feedback.setOutputMarkupId(true);
	reportForm.addOrReplace(feedback);

	reportForm.addOrReplace(new Label("report.title", new Model(report
		.getDescription())));

	reportForm.add(new Button("save") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		if (isNew) {
		    addJob();
		} else {
		    editJob();
		}
	    };
	});

	reportForm.add(new Button("cancel") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleBack();
	    };
	}.setDefaultFormProcessing(false));

	// reportForm.add(new FeedbackPanel("errorMessages"));

	add(reportForm);
    }

    private void handleBack() {
	setResponsePage(backPage);
    }

    private void editJob() {

	LOG.debug("editJob() -> {}", job);

	try {
	    UpdateJobRequest request = mobBasePage
		    .getNewMobiliserRequest(UpdateJobRequest.class);

	    try {
		job.setParameters(parseReportParams(report));
	    } catch (JsonGenerationException e) {
		LOG.error("Error while generating JSON", e);
	    } catch (JsonMappingException e) {
		LOG.error("Error while generating JSON", e);
	    } catch (IOException e) {
		LOG.error("Error while generating JSON", e);
	    }
	    job.setSchedule(scP.getCronSchedule());

	    request.setJob(job);

	    UpdateJobResponse response = mobBasePage.wsJobClient
		    .updateJob(request);

	    if (!mobBasePage.evaluateMobiliserResponse(response)) {
		LOG.warn("# An error occurred while updating the job");
		return;
	    }

	} catch (Exception e) {
	    LOG.error("# An error occurred while updating job [{}]",
		    job.getJobId(), e);

	    error(getLocalizer().getString("report.batch.save.error", this));
	}

	info(getLocalizer().getString("report.batch.save.success", this));

	handleBack();
    }

    private void addJob() {

	LOG.debug("addJob() -> {}", job);

	try {
	    CreateJobRequest req = new CreateJobRequest();
	    Job job = new Job();
	    job.setActive(true);
	    job.setHandlerName("MOBILISER");
	    job.setImplementationTypeId(2);
	    job.setJobName(report.getName()
		    + " "
		    + mobBasePage.getMobiliserWebSession()
			    .getLoggedInCustomer().getCustomerId());
	    job.setMaxDelay(480);
	    job.setMaxDuration(60);
	    job.setImplementationUrl("com.sybase365.mobiliser.util.report.watcher.ReportJob");
	    try {
		job.setParameters(parseReportParams(report));
	    } catch (JsonGenerationException e) {
		LOG.error("Error while generating JSON", e);
	    } catch (JsonMappingException e) {
		LOG.error("Error while generating JSON", e);
	    } catch (IOException e) {
		LOG.error("Error while generating JSON", e);
	    }
	    job.setSchedule(scP.getCronSchedule());
	    req.setJob(job);
	    req.setOrigin("BatchReportPage");
	    req.setTraceNo(UUID.randomUUID().toString());
	    mobBasePage.wsJobClient.createJob(req);
	} catch (Exception e) {
	    LOG.error("Error while creating Job", e);
	    error("Error while creating new Report Job");
	}

	info("New Reporting Job created successfully");

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

    /**
     * @return the type
     */
    public String getType() {
	return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(String type) {
	this.type = type;
    }

}