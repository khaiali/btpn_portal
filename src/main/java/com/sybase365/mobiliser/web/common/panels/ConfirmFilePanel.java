package com.sybase365.mobiliser.web.common.panels;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.datetime.PatternDateConverter;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import com.sybase365.mobiliser.money.contract.v5_0.system.FindPendingBulkProcessingFilesRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.GetBulkFileTypeRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.GetBulkFileTypeResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.BulkFile;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.BulkFileType;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.components.KeyValue;
import com.sybase365.mobiliser.web.common.components.KeyValueDropDownChoice;
import com.sybase365.mobiliser.web.common.dataproviders.ConfirmFileDataProvider;
import com.sybase365.mobiliser.web.cst.pages.bulkprocessing.ConfirmBulkFileDetailsPage;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class ConfirmFilePanel extends Panel {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(ConfirmFilePanel.class);

    /* Reference to logged in customer for locale settings */
    private MobiliserBasePage mobBasePage;
    private String filterType = Constants.TXN_FILTERTYPE_MONTH;
    private String fromMonth;
    private Date fromDate;
    private Date toDate;
    private ConfirmFileDataProvider dataProvider;
    private List<BulkFile> fileList;
    private String totalItemString = null;
    private int startIndex = 0;
    private int endIndex = 0;
    private int rowIndex = 1;
    private boolean forceReload = true;
    private Integer bulkFileType;
    private String fileName;
    private XMLGregorianCalendar fromDateXml;
    private XMLGregorianCalendar toDateXml;

    private static final String WICKET_ID_navigator = "navigator";
    private static final String WICKET_ID_totalItems = "totalItems";
    private static final String WICKET_ID_startIndex = "startIndex";
    private static final String WICKET_ID_endIndex = "endIndex";
    private static final String WICKET_ID_orderById = "orderById";
    private static final String WICKET_ID_LID = "Lid";
    private static final String WICKET_ID_pageable = "pageable";
    private static final String WICKET_ID_noItemsMsg = "noItemsMsg";

    public ConfirmFilePanel(String id, MobiliserBasePage mobBasePage) {

	super(id);

	this.mobBasePage = mobBasePage;

	final String chooseDtTxt = this.mobBasePage.getLocalizer().getString(
		"datepicker.chooseDate", this.mobBasePage);

	add(new HeaderContributor(new IHeaderContributor() {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public void renderHead(IHeaderResponse response) {

		// localize the jquery datepicker based on users locale setting
		// locale specific js includes for datepicker are available at
		// http://jquery-ui.googlecode.com/svn/trunk/ui/i18n/
		String localeLang = getLocale().getLanguage().toLowerCase();

		LOG.debug("Using DatePicker for locale language: {}",
			localeLang);

		if (PortalUtils.exists(localeLang)) {
		    response.renderJavascriptReference("scripts/jquery/i18n/jquery.ui.datepicker-"
			    + localeLang + ".js");
		}

		response.renderJavascript("\n"
			+ "jQuery(document).ready(function($) { \n"
			+ "  $('#fromDate').datepicker( { \n"
			+ "	'changeMonth' : true, \n" + "	'showOn': 'both', \n"
			+ "	'dateFormat' : '"
			+ Constants.DATE_FORMAT_PATTERN_PICKER + "', \n"
			+ "	'buttonImage': 'images/calendar.gif', \n"
			+ "	'buttonText' : '" + chooseDtTxt + "', \n"
			+ "	'buttonOnlyImage': true} ); \n"

			+ "  $('#toDate').datepicker( { \n"
			+ "	'changeMonth' : true, \n" + "	'showOn': 'both', \n"
			+ "	'dateFormat' : '"
			+ Constants.DATE_FORMAT_PATTERN_PICKER + "', \n"
			+ "	'buttonImage': 'images/calendar.gif', \n"
			+ "	'buttonText' : '" + chooseDtTxt + "', \n"
			+ "	'buttonOnlyImage': true} ); \n" + "});\n",
			"datePicker");
	    }
	}));

	constructPanel();
    }

    @SuppressWarnings("unchecked")
    private void constructPanel() {

	Form<?> form = new Form("confirmfileform",
		new CompoundPropertyModel<ConfirmFilePanel>(this));

	form.add(new FeedbackPanel("errorMessages"));

	RadioGroup rg = new RadioGroup("radioGroup", new PropertyModel(this,
		"filterType"));

	Radio filteTypeMonth = new Radio("Month", new Model(
		Constants.TXN_FILTERTYPE_MONTH));
	filteTypeMonth.setOutputMarkupId(true).setMarkupId("Month");
	rg.add(filteTypeMonth);

	Radio filteTypeDate = new Radio("Date", new Model(
		Constants.TXN_FILTERTYPE_TIMEFRAME));
	filteTypeDate.setOutputMarkupId(true).setMarkupId("Date");
	rg.add(filteTypeDate);

	final WebMarkupContainer monthLbl = new WebMarkupContainer(
		"monthLabel", new Model());
	monthLbl.setOutputMarkupPlaceholderTag(true);
	monthLbl.setOutputMarkupId(true);

	final KeyValueDropDownChoice<String, String> monthDropDown = new KeyValueDropDownChoice<String, String>(
		"fromMonth", mobBasePage.getSelectableMonth());
	monthDropDown.setOutputMarkupPlaceholderTag(true);
	monthDropDown.setEnabled(true);

	monthLbl.add(monthDropDown);

	final WebMarkupContainer fromDateLabel = new WebMarkupContainer(
		"fromDateLabel", new Model());
	fromDateLabel.setOutputMarkupPlaceholderTag(true);
	fromDateLabel.setOutputMarkupId(true);
	fromDateLabel.setVisible(false);
	final DateTextField fromDateTxt = (DateTextField) new DateTextField(
		"fromDate", new PropertyModel<Date>(this, "fromDate"),
		new PatternDateConverter(Constants.DATE_FORMAT_PATTERN_PARSE,
			false));
	fromDateTxt.add(new ErrorIndicator());
	fromDateTxt.setOutputMarkupPlaceholderTag(true);
	fromDateTxt.setMarkupId("fromDate");
	fromDateLabel.add(fromDateTxt);

	final WebMarkupContainer toDateLabel = new WebMarkupContainer(
		"toDateLabel", new Model());
	toDateLabel.setOutputMarkupPlaceholderTag(true);
	toDateLabel.setOutputMarkupId(true);
	toDateLabel.setVisible(false);
	final DateTextField toDateTxt = (DateTextField) new DateTextField(
		"toDate", new PropertyModel<Date>(this, "toDate"),
		new PatternDateConverter(Constants.DATE_FORMAT_PATTERN_PARSE,
			false));
	toDateTxt.add(new ErrorIndicator());
	toDateTxt.setMarkupId("toDate");
	toDateTxt.setOutputMarkupPlaceholderTag(true);
	toDateLabel.add(toDateTxt);

	filteTypeMonth.add(new AjaxEventBehavior("onclick") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onEvent(AjaxRequestTarget target) {
		monthLbl.setVisible(true);
		target.addComponent(monthLbl);
		fromDateLabel.setVisible(false);
		target.addComponent(fromDateLabel);
		toDateLabel.setVisible(false);
		target.addComponent(toDateLabel);
	    }
	});

	filteTypeDate.add(new AjaxEventBehavior("onclick") {
	    private static final long serialVersionUID = 2L;

	    @Override
	    protected void onEvent(AjaxRequestTarget target) {
		monthLbl.setVisible(false);
		target.addComponent(monthLbl);
		fromDateLabel.setVisible(true);
		target.addComponent(fromDateLabel);
		toDateLabel.setVisible(true);
		target.addComponent(toDateLabel);
		target.appendJavascript("$j('#fromDate').datepicker( { \n"
			+ "	'changeMonth' : true, \n" + "	'showOn': 'both', \n"
			+ "	'dateFormat' : '"
			+ Constants.DATE_FORMAT_PATTERN_PICKER + "', \n"
			+ "	'buttonImage': 'images/calendar.gif', \n"
			+ "	'buttonOnlyImage': true} ); \n"
			+ "$j('#toDate').datepicker( { \n"
			+ "	'changeMonth' : true,  \n"
			+ "	'showOn': 'both', \n" + "	'dateFormat' : '"
			+ Constants.DATE_FORMAT_PATTERN_PICKER + "', \n"
			+ "	'buttonImage': 'images/calendar.gif', \n"
			+ "	'buttonOnlyImage': true} ); \n");
	    }
	});

	form.add(monthLbl);
	form.add(fromDateLabel);
	form.add(toDateLabel);
	form.add(rg);

	form.add(new KeyValueDropDownChoice<Integer, String>("fileType",
		getMimeTypeList()).setNullValid(true));
	form.add(new TextField<String>("fileName").add(new ErrorIndicator()));
	form.add(new Button("Search") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleSubmit();
	    }

	});
	createFileSearchTable(false);
	add(form);

	LOG.debug("PatternDateConverter format: "
		+ Constants.DATE_FORMAT_PATTERN_PARSE + " DatePicker format: "
		+ Constants.DATE_FORMAT_PATTERN_PICKER);
    }

    private void handleSubmit() {
	validateInputs();
	createFileSearchTable(true);
    }

    private List<KeyValue<Integer, String>> getMimeTypeList() {
	LOG.debug("# ConfirmFilePanel.getMimeTypeList()");
	List<KeyValue<Integer, String>> mimeTypes = new ArrayList<KeyValue<Integer, String>>();
	try {
	    GetBulkFileTypeRequest request = mobBasePage
		    .getNewMobiliserRequest(GetBulkFileTypeRequest.class);
	    GetBulkFileTypeResponse response = mobBasePage.wsBulkProcessingClient
		    .getBulkFileType(request);
	    if (mobBasePage.evaluateMobiliserResponse(response)) {
		List<BulkFileType> typeList = response.getWrkBulkFileType();
		for (BulkFileType type : typeList)
		    mimeTypes.add(new KeyValue<Integer, String>(type
			    .getBulkTypeId(), type.getFileTypeDesc()));
	    }
	} catch (Exception e) {
	    LOG.error("An error occurred in getting the mime type list", e);
	    error(getLocalizer().getString(
		    "bulk.processing.load.filetypes.error", this));
	}
	return mimeTypes;
    }

    private void createFileSearchTable(boolean isVisible) {

	WebMarkupContainer filesDataViewContainer = new WebMarkupContainer(
		"filesDataViewContainer");
	filesDataViewContainer.setVisible(isVisible);
	dataProvider = new ConfirmFileDataProvider(WICKET_ID_LID, mobBasePage,
		true);
	fileList = new ArrayList<BulkFile>();
	final DataView<BulkFile> dataView = new DataView<BulkFile>(
		WICKET_ID_pageable, dataProvider) {
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onBeforeRender() {

		try {
		    FindPendingBulkProcessingFilesRequest req = mobBasePage
			    .getNewMobiliserRequest(FindPendingBulkProcessingFilesRequest.class);
		    if (PortalUtils.exists(fileName)) {
			req.setFileName(fileName);
		    }
		    if (PortalUtils.exists(bulkFileType)) {
			req.setBulkFileType(bulkFileType);
		    }
		    if (PortalUtils.exists(fromDateXml)) {
			req.setStartDate(fromDateXml);
		    }
		    if (PortalUtils.exists(toDateXml)) {
			req.setEndDate(toDateXml);
		    }

		    dataProvider.searchFiles(req, forceReload);
		    forceReload = false;
		    refreshTotalItemCount();
		    // reset rowIndex
		    rowIndex = 1;
		} catch (Exception e) {
		    LOG.error("An error occurred while searching for files", e);
		    error(getLocalizer().getString(
			    "search.confirm.files.error", this));
		}
		if (dataProvider.size() > 0) {
		    setVisible(true);
		} else {
		    setVisible(false);
		}
		refreshTotalItemCount();

		super.onBeforeRender();
	    }

	    private void refreshTotalItemCount() {
		totalItemString = String.valueOf(dataProvider.size());
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

	    protected void populateItem(final Item<BulkFile> item) {
		final BulkFile entry = item.getModelObject();
		fileList.add(entry);
		Link idLink = new Link<BulkFile>("details", item.getModel()) {
		    private static final long serialVersionUID = 1L;

		    @Override
		    public void onClick() {
			BulkFile cModel = (BulkFile) item.getModelObject();
			if (mobBasePage.getMobiliserWebSession().hasPrivilege(
				Constants.PRIV_CST_LOGIN))
			    setResponsePage(new ConfirmBulkFileDetailsPage(
				    entry));
			else
			    setResponsePage(new com.sybase365.mobiliser.web.distributor.pages.bulkprocessing.ConfirmBulkFileDetailsPage(
				    entry));

		    }
		};
		idLink.add(new Label(WICKET_ID_LID, String.valueOf(entry
			.getTaskId())));
		item.add(idLink);
		item.add(new Label("name", String.valueOf(entry
			.getBulkFileName())));
		item.add(new Label("type",
			mobBasePage.getDisplayValue(
				String.valueOf(entry.getBulkFileType()),
				"bulkFileType")));
		item.add(new Label("date", PortalUtils.getFormattedDate(
			entry.getUploadDate(), Locale.getDefault())));

	    };

	};

	dataView.setItemsPerPage(10);
	filesDataViewContainer.addOrReplace(dataView);
	filesDataViewContainer.addOrReplace(new OrderByBorder(
		WICKET_ID_orderById, WICKET_ID_orderById, dataProvider) {
	    @Override
	    protected void onSortChanged() {
		// For some reasons the dataView can be null
		// when the
		// page is
		// loading
		// and the sort is clicked (clicking the name
		// header),
		// so handle
		// it
		if (dataView != null) {
		    dataView.setCurrentPage(0);
		}
	    }
	});

	filesDataViewContainer.addOrReplace(new MultiLineLabel(
		WICKET_ID_noItemsMsg, getLocalizer().getString(
			"search.files.noItemMsg", this)) {
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
	filesDataViewContainer.addOrReplace(new CustomPagingNavigator(
		WICKET_ID_navigator, dataView));

	filesDataViewContainer.addOrReplace(new Label(WICKET_ID_totalItems,
		new PropertyModel<String>(this, "totalItemString")));

	filesDataViewContainer.addOrReplace(new Label(WICKET_ID_startIndex,
		new PropertyModel(this, "startIndex")));

	filesDataViewContainer.addOrReplace(new Label(WICKET_ID_endIndex,
		new PropertyModel(this, "endIndex")));
	filesDataViewContainer.addOrReplace(dataView);
	addOrReplace(filesDataViewContainer);

    }

    private void validateInputs() {

	LOG.debug("#ConfirmFilePanel.validateInputs()");

	fromDateXml = null;
	toDateXml = null;

	if (filterType.equals(Constants.TXN_FILTERTYPE_TIMEFRAME)
		&& PortalUtils.exists(fromDate) && PortalUtils.exists(toDate)) {
	    if (PortalUtils.exists(fromDate)) {
		fromDateXml = PortalUtils.getSaveXMLGregorianCalendarFromDate(
			fromDate, mobBasePage.getMobiliserWebSession()
				.getTimeZone());
	    }
	    if (PortalUtils.exists(toDate)) {
		toDateXml = PortalUtils.getSaveXMLGregorianCalendarFromDate(
			toDate, mobBasePage.getMobiliserWebSession()
				.getTimeZone());
	    }
	    // set the two dates in correct order...
	    if (fromDateXml != null && toDateXml != null) {
		if (fromDateXml.compare(toDateXml) == DatatypeConstants.GREATER) {
		    XMLGregorianCalendar temp = fromDateXml;
		    fromDateXml = toDateXml;
		    toDateXml = temp;
		}
		fromDateXml.setHour(0);
		fromDateXml.setMinute(0);
		fromDateXml.setSecond(0);
		fromDateXml.setMillisecond(0);
		toDateXml.setHour(23);
		toDateXml.setMinute(59);
		toDateXml.setSecond(59);
		toDateXml.setMillisecond(999);
	    }

	    LOG.debug("From [{}] to [{}]", (fromDateXml == null ? "NOT SET"
		    : fromDateXml.toString()), (toDateXml == null ? "NOT SET"
		    : toDateXml.toString()));
	} else if (filterType.equals(Constants.TXN_FILTERTYPE_MONTH)
		&& PortalUtils.exists(fromMonth)) {

	    LOG.debug("Filter by month and year..");
	    String _month = fromMonth.substring(0, fromMonth.indexOf('-'));
	    String _year = fromMonth.substring(fromMonth.indexOf('-') + 1);
	    fromDateXml = PortalUtils.getXmlFromDateOfMonth(mobBasePage
		    .getMobiliserWebSession().getTimeZone(), _month, _year);
	    toDateXml = PortalUtils.getXmlToDateOfMonth(mobBasePage
		    .getMobiliserWebSession().getTimeZone(), _month, _year);
	    LOG.debug("From [{}] to [{}]", fromDateXml.toString(),
		    toDateXml.toString());

	}
    }

    public Integer getFileType() {
	return bulkFileType;
    }

    public void setFileType(Integer fileType) {
	this.bulkFileType = fileType;
    }

    public String getFileName() {
	return fileName;
    }

    public void setFileName(String fileName) {
	this.fileName = fileName;
    }

}
