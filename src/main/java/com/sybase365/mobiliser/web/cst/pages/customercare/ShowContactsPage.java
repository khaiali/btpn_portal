package com.sybase365.mobiliser.web.cst.pages.customercare;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.wicket.Session;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
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
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;

import com.sybase365.mobiliser.money.contract.v5_0.audit.GetNotesByCustomerRequest;
import com.sybase365.mobiliser.money.contract.v5_0.audit.beans.Note;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.components.KeyValueDropDownChoice;
import com.sybase365.mobiliser.web.common.components.LocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.common.dataproviders.NoteDataProvider;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

@AuthorizeInstantiation(Constants.PRIV_NOTE_READ)
public class ShowContactsPage extends CustomerCareMenuGroup {
    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(ShowContactsPage.class);
    private Date fromDate;
    private Date toDate;
    private XMLGregorianCalendar fromDateXml;
    private XMLGregorianCalendar toDateXml;
    private Integer timeframe;
    private Integer category;
    private Integer contactId;
    private String agentName;
    private Integer status;
    private String subject;

    private String totalItemString = null;
    private int startIndex = 0;
    private int endIndex = 0;

    private boolean forceReload = true;

    private int rowIndex = 1;
    private NoteDataProvider dataProvider;
    List<Note> notesList;

    private static final String WICKET_ID_navigator = "navigator";
    private static final String WICKET_ID_totalItems = "totalItems";
    private static final String WICKET_ID_startIndex = "startIndex";
    private static final String WICKET_ID_endIndex = "endIndex";
    private static final String WICKET_ID_orderById = "orderById";
    private static final String WICKET_ID_detailsLink = "detailsLink";
    private static final String WICKET_ID_pageable = "pageable";
    private static final String WICKET_ID_contactCategory = "contactCategory";
    private static final String WICKET_ID_contactSubject = "contactSubject";
    private static final String WICKET_ID_contactDate = "contactDate";
    private static final String WICKET_ID_contactAgent = "contactAgent";
    private static final String WICKET_ID_contactId = "contactId";
    private static final String WICKET_ID_contactStatus = "contactStatus";
    private static final String WICKET_ID_noItemsMsg = "noItemsMsg";

    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();

	final String chooseDtTxt = this.getLocalizer().getString(
		"datepicker.chooseDate", this);

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
			+ "	'buttonText' : '" + chooseDtTxt + "', \n"
			+ "	'changeMonth' : true, \n" + "	'showOn': 'both', \n"
			+ "	'dateFormat' : '"
			+ Constants.DATE_FORMAT_PATTERN_PICKER + "', \n"
			+ "	'buttonImage': 'images/calendar.gif', \n"
			+ "	'buttonOnlyImage': true} ); \n"

			+ "  $('#toDate').datepicker( { \n"
			+ "	'buttonText' : '" + chooseDtTxt + "', \n"
			+ "	'changeMonth' : true, \n" + "	'showOn': 'both', \n"
			+ "	'dateFormat' : '"
			+ Constants.DATE_FORMAT_PATTERN_PICKER + "', \n"
			+ "	'buttonImage': 'images/calendar.gif', \n"
			+ "	'buttonOnlyImage': true} ); \n" + "});\n",
			"datePicker");
	    }
	}));

	final Form<?> form = new Form("findContactsForm",
		new CompoundPropertyModel<ShowContactsPage>(this));
	form.add(new FeedbackPanel("errorMessages"));
	form.add(new DateTextField("fromDate", new PropertyModel<Date>(this,
		"fromDate"), new PatternDateConverter(
		Constants.DATE_FORMAT_PATTERN_PARSE, false)));
	form.add(new KeyValueDropDownChoice<Integer, String>("timeframe",
		getSelectableTimeFrame()).setNullValid(true));
	form.add(new LocalizableLookupDropDownChoice<Integer>("category",
		Integer.class, Constants.RESOURCE_BUNDLE_NOTE_CATEGORY, this,
		true, true).setNullValid(true));
	form.add(new TextField<String>("agentName").add(
		Constants.mediumStringValidator).add(
		Constants.mediumSimpleAttributeModifier));
	form.add(new DateTextField("toDate", new PropertyModel<Date>(this,
		"toDate"), new PatternDateConverter(
		Constants.DATE_FORMAT_PATTERN_PARSE, false)));
	form.add(new LocalizableLookupDropDownChoice<Integer>("status",
		Integer.class, Constants.RESOURCE_BUNDLE_NOTE_STATUS, this,
		true, true).setNullValid(true));

	form.add(new TextField<String>("subject").add(
		Constants.mediumStringValidator).add(
		Constants.mediumSimpleAttributeModifier));
	form.add(new Button("search") {
	    @Override
	    public void onSubmit() {
		searchContacts();
	    }
	});
	add(form);
	createNoteDataView(false);

    }

    private void searchContacts() {
	if (PortalUtils.exists(getTimeframe())) {
	    setFromDateXml(PortalUtils.getTimeFrameFromDate(getTimeframe(),
		    getMobiliserWebSession().getTimeZone()));
	    setToDateXml(PortalUtils
		    .getTimeFrameToDate(getMobiliserWebSession().getTimeZone()));
	} else if (PortalUtils.exists(getFromDate())
		|| PortalUtils.exists(getToDate())) {
	    checkDateOptions();
	}

	if (Session.get().getFeedbackMessages().hasErrorMessageFor(getPage())) {
	    return;
	}

	createNoteDataView(true);
    }

    protected void checkDateOptions() {
	try {
	    if (PortalUtils.exists(fromDate)) {
		fromDateXml = PortalUtils.getSaveXMLGregorianCalendarFromDate(
			fromDate, getMobiliserWebSession().getTimeZone());
	    }
	    if (PortalUtils.exists(toDate)) {
		toDateXml = PortalUtils.getSaveXMLGregorianCalendarFromDate(
			toDate, getMobiliserWebSession().getTimeZone());
	    }
	    // set the two dates in correct order...
	    if (fromDateXml != null && toDateXml != null) {
		if (fromDateXml.compare(toDateXml) == DatatypeConstants.GREATER) {
		    XMLGregorianCalendar temp = fromDateXml;
		    fromDateXml = toDateXml;
		    toDateXml = temp;
		}
	    }
	    if (fromDateXml != null) {
		// cFrom's time is 00:00:00.000
		fromDateXml.setHour(0);
		fromDateXml.setMinute(0);
		fromDateXml.setSecond(0);
		fromDateXml.setMillisecond(0);
		setFromDateXml(fromDateXml);
	    }

	    if (toDateXml != null) {
		// set cTo's time to 23:59:59.999
		toDateXml.setHour(23);
		toDateXml.setMinute(59);
		toDateXml.setSecond(59);
		toDateXml.setMillisecond(999);
		setToDateXml(toDateXml);
	    } else {
		toDateXml = PortalUtils
			.getTimeFrameToDate(getMobiliserWebSession()
				.getTimeZone());
		setToDateXml(toDateXml);
	    }
	    LOG.debug("From [{}] to [{}]", (fromDateXml == null ? "NOT SET"
		    : fromDateXml.toString()), (toDateXml == null ? "NOT SET"
		    : toDateXml.toString()));
	} catch (Exception e) {
	    LOG.error("# An error occurred while converting date to Calender",
		    e);
	    error(getLocalizer().getString("contactNotes.list.failure", this));

	}

    }

    private void createNoteDataView(boolean isVisible) {
	WebMarkupContainer notesDataViewContainer = new WebMarkupContainer(
		"notesDataViewContainer");
	notesDataViewContainer.setVisible(isVisible);
	dataProvider = new NoteDataProvider(WICKET_ID_pageable, this);
	notesList = new ArrayList<Note>();
	final DataView<Note> dataView = new DataView<Note>(WICKET_ID_pageable,
		dataProvider) {
	    @Override
	    protected void onBeforeRender() {

		try {
		    dataProvider.findContactNotes(createFilterNoteRequest());
		    forceReload = false;
		    refreshTotalItemCount();
		    // reset rowIndex
		    rowIndex = 1;
		} catch (DataProviderLoadException dple) {
		    LOG.error("An error occurred while loading balance alerts",
			    dple);
		    error(getLocalizer().getString("contactNotes.list.failure",
			    this));
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

	    protected void populateItem(Item<Note> item) {
		final Note entry = item.getModelObject();
		notesList.add(entry);
		item.add(new Label(WICKET_ID_contactId, String.valueOf(entry
			.getId())));
		item.add(new Label(WICKET_ID_contactCategory, getDisplayValue(
			String.valueOf(entry.getCategory()),
			Constants.RESOURCE_BUNDLE_NOTE_CATEGORY)));
		item.add(new Label(WICKET_ID_contactSubject, entry.getSubject()));
		item.add(new Label(WICKET_ID_contactStatus, getDisplayValue(
			String.valueOf(entry.getStatus()),
			Constants.RESOURCE_BUNDLE_NOTE_STATUS)));
		item.add(new Label(WICKET_ID_contactDate, PortalUtils
			.getFormattedDateTime(entry.getCreated(),
				getMobiliserWebSession().getLocale(),
				getMobiliserWebSession().getTimeZone())));
		item.add(new Label(WICKET_ID_contactAgent, String.valueOf(entry
			.getCreatedBy())));
		Link detailsLink = (Link) new Link<Note>(WICKET_ID_detailsLink,
			item.getModel()) {
		    @Override
		    public void onClick() {
			Note entry = (Note) getModelObject();
			setResponsePage(new ContactDetailsPage(entry,
				this.getWebPage()));
		    }
		};
		item.add(detailsLink);

	    };

	};

	dataView.setItemsPerPage(10);
	notesDataViewContainer.addOrReplace(dataView);
	notesDataViewContainer.addOrReplace(new OrderByBorder(
		WICKET_ID_orderById, WICKET_ID_orderById, dataProvider) {
	    @Override
	    protected void onSortChanged() {
		// For some reasons the dataView can be null when the
		// page is
		// loading
		// and the sort is clicked (clicking the name header),
		// so handle
		// it
		if (dataView != null) {
		    dataView.setCurrentPage(0);
		}
	    }
	});

	notesDataViewContainer.addOrReplace(new MultiLineLabel(
		WICKET_ID_noItemsMsg, getLocalizer().getString(
			"contact.note.noItemsMsg", this)) {
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
	notesDataViewContainer.addOrReplace(new CustomPagingNavigator(
		WICKET_ID_navigator, dataView));

	notesDataViewContainer.addOrReplace(new Label(WICKET_ID_totalItems,
		new PropertyModel<String>(this, "totalItemString")));

	notesDataViewContainer.addOrReplace(new Label(WICKET_ID_startIndex,
		new PropertyModel(this, "startIndex")));

	notesDataViewContainer.addOrReplace(new Label(WICKET_ID_endIndex,
		new PropertyModel(this, "endIndex")));
	notesDataViewContainer.addOrReplace(dataView);
	addOrReplace(notesDataViewContainer);

    }

    private GetNotesByCustomerRequest createFilterNoteRequest() {
	GetNotesByCustomerRequest request = null;
	try {
	    request = getNewMobiliserRequest(GetNotesByCustomerRequest.class);
	    request.setFromDate(getFromDateXml());
	    request.setToDate(getToDateXml());
	    request.setNoteType(getCategory());
	    request.setStatus(getStatus());
	    request.setSubject(getSubject());
	    request.setCreatedBy(getAgentName());
	    request.setCustomerId(getMobiliserWebSession().getCustomer()
		    .getId());
	    LOG.debug("Date range for filter: {} to {}", request.getFromDate(),
		    request.getToDate());

	} catch (Exception e) {
	    LOG.error("#An error occurred while creating FilterNoteRequest");
	}
	return request;
    }

    public Date getFromDate() {
	return fromDate;
    }

    public Date getToDate() {
	return toDate;
    }

    public XMLGregorianCalendar getFromDateXml() {
	return fromDateXml;
    }

    public void setFromDateXml(XMLGregorianCalendar fromDateXml) {
	this.fromDateXml = fromDateXml;
    }

    public XMLGregorianCalendar getToDateXml() {
	return toDateXml;
    }

    public void setToDateXml(XMLGregorianCalendar toDateXml) {
	this.toDateXml = toDateXml;
    }

    public Integer getTimeframe() {
	return timeframe;
    }

    public Integer getCategory() {
	return category;
    }

    public Integer getContactId() {
	return contactId;
    }

    public String getAgentName() {
	return agentName;
    }

    public Integer getStatus() {
	return status;
    }

    public String getSubject() {
	return subject;
    }

}
