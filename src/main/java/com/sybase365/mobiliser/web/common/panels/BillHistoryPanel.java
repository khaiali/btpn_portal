package com.sybase365.mobiliser.web.common.panels;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.datetime.PatternDateConverter;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import com.sybase365.mobiliser.money.contract.v5_0.invoice.beans.Invoice;
import com.sybase365.mobiliser.money.contract.v5_0.invoice.beans.InvoiceConfiguration;
import com.sybase365.mobiliser.money.contract.v5_0.invoice.beans.InvoiceType;
import com.sybase365.mobiliser.util.tools.formatutils.FormatUtils;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.beans.SelectBean;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.components.KeyValue;
import com.sybase365.mobiliser.web.common.components.KeyValueDropDownChoice;
import com.sybase365.mobiliser.web.common.components.LocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.common.dataproviders.InvoiceDataProvider;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class BillHistoryPanel extends Panel {

    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(BillConfigurationListPanel.class);

    Long customerId;
    MobiliserBasePage basePage;

    InvoiceDataProvider dataProvider;

    List<InvoiceConfiguration> invoiceConfigurationList = new ArrayList<InvoiceConfiguration>();
    List<InvoiceType> invoiceTypeList = new ArrayList<InvoiceType>();

    // Table Items
    private String totalItemString = null;
    private int startIndex = 0;
    private int endIndex = 0;

    private boolean forceReload = false;

    private int rowIndex = 1;

    private static final String WICKET_ID_navigator = "navigator";
    private static final String WICKET_ID_totalItems = "totalItems";
    private static final String WICKET_ID_startIndex = "startIndex";
    private static final String WICKET_ID_endIndex = "endIndex";
    private static final String WICKET_ID_pageable = "pageable";
    private static final String WICKET_ID_noItemsMsg = "noItemsMsg";

    Long invoiceType = null;
    Integer invoiceStatus = null;
    Date fromDate = null;
    Date toDate = null;
    private String fromMonth;

    private String filterType = Constants.TXN_FILTERTYPE_MONTH;

    DataView<Invoice> dataView;

    public BillHistoryPanel(String id, MobiliserBasePage basePage,
	    Long customerId) {

	super(id);

	this.basePage = basePage;
	this.customerId = customerId;

	final String clearTxt = this.basePage.getLocalizer().getString(
		"datepicker.clear", basePage);
	final String chooseDtTxt = this.basePage.getLocalizer().getString(
		"datepicker.chooseDate", basePage);

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

		response.renderJavascript(
			"\n"
				+ "function clearDate(dtPickerObj){ \n"
				+ "  setTimeout(function () { \n"
				+ "      var buttonPane = dtPickerObj.datepicker('widget').find('.ui-datepicker-close').click(function () { \n"
				+ "            $j.datepicker._clearDate(dtPickerObj); \n"
				+ "         }); \n" + "  }, 1); \n" + "} \n"

				+ "jQuery(document).ready(function($) { \n"
				+ "  $('#fromDate').datepicker( { \n"
				+ "	'buttonText' : '"
				+ chooseDtTxt
				+ "', \n"
				+ "	'showButtonPanel' : true, \n"
				+ "	'closeText' : '"
				+ clearTxt
				+ "', \n"
				+ "     'beforeShow' : function (input) {  clearDate($(this)); }, \n"
				+ "     'onChangeMonthYear' : function(year, month, input){ clearDate($(this)); }, \n"
				+ "	'changeMonth' : true, \n"
				+ "	'showOn': 'both', \n"
				+ "	'dateFormat' : '"
				+ Constants.DATE_FORMAT_PATTERN_PICKER
				+ "', \n"
				+ "	'buttonImage': 'images/calendar.gif', \n"
				+ "	'buttonOnlyImage': true} ); \n"

				+ "  $('#toDate').datepicker( { \n"
				+ "	'buttonText' : '"
				+ chooseDtTxt
				+ "', \n"
				+ "	'showButtonPanel' : true, \n"
				+ "	'closeText' : '"
				+ clearTxt
				+ "', \n"
				+ "     'beforeShow' : function (input) {  clearDate($(this)); }, \n"
				+ "     'onChangeMonthYear' : function(year, month, input){ clearDate($(this)); }, \n"
				+ "	'changeMonth' : true, \n"
				+ "	'showOn': 'both', \n"
				+ "	'dateFormat' : '"
				+ Constants.DATE_FORMAT_PATTERN_PICKER
				+ "', \n"
				+ "	'buttonImage': 'images/calendar.gif', \n"
				+ "	'buttonOnlyImage': true} ); \n" + "});\n"

			, "datePicker");
	    }
	}));

	constructPanel();
    }

    private void constructPanel() {

	invoiceConfigurationList = basePage
		.getInvoiceConfigurationList(customerId);
	invoiceTypeList = basePage.getAllInvoiceTypes();

	final WebMarkupContainer searchResults = new WebMarkupContainer(
		"searchResults");

	searchResults.setOutputMarkupId(true);
	searchResults.setOutputMarkupPlaceholderTag(true);
	searchResults.setVisible(false);
	add(searchResults);

	dataProvider = new InvoiceDataProvider("id", this.basePage);

	dataView = new DataView<Invoice>(WICKET_ID_pageable, dataProvider) {

	    @Override
	    protected void onBeforeRender() {

		try {
		    if (forceReload) {
			dataProvider.loadListByFilter(customerId, forceReload,
				invoiceType, invoiceStatus, fromDate, toDate);

			forceReload = false;
		    }

		    refreshTotalItemCount();

		    if (dataProvider.size() > 0) {
			setVisible(super.isVisible());
		    } else {
			setVisible(false);
		    }

		    // reset rowIndex
		    rowIndex = 1;

		} catch (DataProviderLoadException dple) {
		    LOG.error(
			    "# An error occurred while loading bill configuration list",
			    dple);
		    error(getLocalizer().getString("openBillList.load.error",
			    this));
		}

		refreshTotalItemCount();

		super.onBeforeRender();
	    }

	    @Override
	    protected void populateItem(final Item<Invoice> item) {

		final Invoice entry = item.getModelObject();

		final InvoiceConfiguration ic = getInvoiceConfiguration(entry
			.getInvoiceConfigurationId());

		final String icName = (ic == null) ? "" : ic.getAlias();
		final String itName = (ic == null) ? "" : getInvoiceTypeName(ic
			.getInvoiceTypeId());
		item.add(new Label("name", icName));
		item.add(new Label("type", itName));

		item.add(new Label("reference", ic == null ? "" : ic
			.getReference()));
		item.add(new Label("billReference", entry.getReference()));
		item.add(new Label("date", entry.getDate() == null ? ""
			: new SimpleDateFormat("MM/dd/yyyy").format(entry
				.getDate().toGregorianCalendar().getTime())));
		item.add(new Label("amount", basePage
			.convertAmountToStringWithCurrency(entry.getAmount(),
				entry.getCurrency())));

		item.add(new Label("status", basePage.getDisplayValue(
			String.valueOf(entry.getStatus()),
			Constants.RESOURCE_BUNDLE_INVOICE_STATUS)));

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
	searchResults.add(dataView);

	searchResults
		.add(new OrderByBorder("orderByDate", "date", dataProvider) {
		    @Override
		    protected void onSortChanged() {
			if (dataView != null) {
			    dataView.setCurrentPage(0);
			}
		    }
		});

	searchResults.add(new OrderByBorder("orderByStatus", "status",
		dataProvider) {
	    @Override
	    protected void onSortChanged() {
		if (dataView != null) {
		    dataView.setCurrentPage(0);
		}
	    }
	});

	// Navigator example: << < 1 2 > >>
	searchResults.add(new CustomPagingNavigator(WICKET_ID_navigator,
		dataView));

	searchResults.add(new Label(WICKET_ID_totalItems,
		new PropertyModel<String>(this, "totalItemString")));

	searchResults.add(new Label(WICKET_ID_startIndex, new PropertyModel(
		this, "startIndex")));

	searchResults.add(new Label(WICKET_ID_endIndex, new PropertyModel(this,
		"endIndex")));

	searchResults.add(new MultiLineLabel(WICKET_ID_noItemsMsg,
		getLocalizer().getString("billHistory.noItemsMsg", this)) {
	    @Override
	    public boolean isVisible() {
		if (dataView.size() > 0) {
		    return false;
		} else {
		    return super.isVisible();
		}
	    }
	});

	final FeedbackPanel feedback = new FeedbackPanel("errorMessages");
	feedback.setOutputMarkupId(true);
	add(feedback);

	Form form = new Form("filterForm",
		new CompoundPropertyModel<BillHistoryPanel>(this));

	form.add(new KeyValueDropDownChoice<Long, String>("invoiceType",
		getListableInvoiceTypeList()).setNullValid(true));

	LocalizableLookupDropDownChoice llddc = new LocalizableLookupDropDownChoice<Integer>(
		"invoiceStatus", Integer.class, "invoicestatus", this, false,
		true) {
	    @Override
	    protected void onBeforeRender() {

		super.onBeforeRender();

		List choices = getChoices();
		choices.remove(new Integer(Constants.INVOICE_STATUS_NEW));
		choices.remove(new Integer(Constants.INVOICE_STATUS_ACTIVE));
		setChoices(choices);

	    }
	};

	llddc.setNullValid(true).add(new ErrorIndicator());
	form.add(llddc);

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
		"fromMonth", getSelectableMonth());
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
	fromDateTxt.setRequired(true);
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

	form.add(new AjaxButton("search", form) {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit(AjaxRequestTarget target, Form<?> form) {

		if (PortalUtils.exists(filterType)
			&& filterType.equals(Constants.TXN_FILTERTYPE_MONTH)
			&& PortalUtils.exists(fromMonth)) {
		    String _month = fromMonth.substring(0,
			    fromMonth.indexOf('-'));
		    String _year = fromMonth.substring(fromMonth.indexOf('-') + 1);
		    XMLGregorianCalendar fromDateXML = PortalUtils
			    .getXmlFromDateOfMonth(basePage
				    .getMobiliserWebSession().getTimeZone(),
				    _month, _year);

		    XMLGregorianCalendar toDateXML = PortalUtils
			    .getXmlToDateOfMonth(basePage
				    .getMobiliserWebSession().getTimeZone(),
				    _month, _year);

		    fromDate = FormatUtils.getSaveDate(fromDateXML);

		    toDate = FormatUtils.getSaveDate(toDateXML);
		}

		forceReload = true;
		if (!searchResults.isVisible()) {
		    searchResults.setVisible(true);
		}

		dataView.setVisible(true);

		target.addComponent(feedback);
		target.addComponent(searchResults);

	    }

	    @Override
	    protected void onError(AjaxRequestTarget target, Form<?> form) {
		target.addComponent(feedback);
	    }
	});

	add(form);
    }

    private String getInvoiceTypeName(Long id) {
	if (id == null)
	    return null;
	String invoiceTypeName = null;
	for (InvoiceType it : invoiceTypeList) {
	    if (it.getId().equals(id)) {
		invoiceTypeName = it.getName();
		break;
	    }
	}
	return invoiceTypeName;
    }

    private InvoiceConfiguration getInvoiceConfiguration(
	    long invoiceConfigurationId) {
	InvoiceConfiguration invoiceConfiguration = null;
	for (InvoiceConfiguration ic : invoiceConfigurationList) {
	    if (ic.getId() == invoiceConfigurationId) {
		invoiceConfiguration = ic;
		break;
	    }
	}
	return invoiceConfiguration;
    }

    public Long getInvoiceType() {
	return invoiceType;
    }

    public void setInvoiceType(Long invoiceType) {
	this.invoiceType = invoiceType;
    }

    public Integer getInvoiceStatus() {
	return invoiceStatus;
    }

    public void setInvoiceStatus(Integer invoiceStatus) {
	this.invoiceStatus = invoiceStatus;
    }

    public Date getFromDate() {
	return fromDate;
    }

    public void setFromDate(Date fromDate) {
	this.fromDate = fromDate;
    }

    public Date getToDate() {
	return toDate;
    }

    public void setToDate(Date toDate) {
	this.toDate = toDate;
    }

    private boolean isDemandForPaymentInvoiceTypeRelatedtoCustomer(Long itId) {
	boolean isRelated = false;

	for (InvoiceConfiguration ic : invoiceConfigurationList) {
	    if (ic.getInvoiceTypeId() == itId.longValue()) {
		isRelated = true;
		break;
	    }
	}

	return isRelated;
    }

    public List<KeyValue<Long, String>> getListableInvoiceTypeList() {

	List<KeyValue<Long, String>> resultList = new ArrayList<KeyValue<Long, String>>();

	for (InvoiceType it : invoiceTypeList) {
	    if (it.getGroupId() == Constants.DEMAND_FORPAYMENT_INV_GRP_TYPE) {
		if (isDemandForPaymentInvoiceTypeRelatedtoCustomer(it.getId())) {
		    resultList.add(new KeyValue(it.getId(), it.getName()));
		}
	    } else {
		resultList.add(new KeyValue(it.getId(), it.getName()));
	    }
	}
	return resultList;
    }

    public List<KeyValue<String, String>> getSelectableMonth() {

	List<KeyValue<String, String>> list = new ArrayList<KeyValue<String, String>>();

	// current date
	GregorianCalendar systemRefDate = new GregorianCalendar();

	GregorianCalendar greg = new GregorianCalendar(TimeZone.getDefault());

	SelectBean sb = new SelectBean(
		String.valueOf(greg.get(Calendar.MONTH) + 1),
		String.valueOf(greg.get(Calendar.YEAR)));

	list.add(new KeyValue<String, String>(sb.getId() + "-" + sb.getName(),
		getLocalizer().getString("calendar.months." + sb.getId(), this)
			+ "-" + sb.getName()));

	// current month
	while (!(systemRefDate.get(Calendar.MONTH) + 1 == 1)
		|| !(systemRefDate.get(Calendar.YEAR) == 2009)) {

	    systemRefDate.add(Calendar.MONTH, -1);

	    greg.add(Calendar.MONTH, -1);

	    sb = new SelectBean(String.valueOf(greg.get(Calendar.MONTH) + 1),
		    String.valueOf(greg.get(Calendar.YEAR)));

	    list.add(new KeyValue<String, String>(sb.getId() + "-"
		    + sb.getName(), getLocalizer().getString(
		    "calendar.months." + sb.getId(), this)
		    + "-" + sb.getName()));
	}

	return list;
    }

}
