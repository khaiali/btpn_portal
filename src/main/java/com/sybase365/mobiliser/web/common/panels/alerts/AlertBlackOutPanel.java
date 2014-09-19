package com.sybase365.mobiliser.web.common.panels.alerts;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;

import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerAlert;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerBlackoutSchedule;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerBlackoutTime;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerBlackoutToAlert;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerBlackoutToAlertList;
import com.sybase365.mobiliser.util.tools.wicketutils.security.PrivilegedBehavior;
import com.sybase365.mobiliser.web.application.clients.AlertsClientLogic;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.util.AlertsHelper;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class AlertBlackOutPanel extends Panel {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(AlertBlackOutPanel.class);

    private MobiliserBasePage basePage;
    private AlertsClientLogic clientLogic;

    private CustomerAlert customerAlert;
    private List<CustomerBlackoutSchedule> customerBlackoutScheduleList;
    ArrayList<java.sql.Date> dateSequenceExisting;
    private WebMarkupContainer alertBlackOutContainer;
    private AlertBlackOutSchedulePanel editPanel;

    private boolean pageViewedByAgent = false;

    // Data Models for table lists
    private String bosTotalItemString = null;
    private int bosStartIndex = 0;
    private int bosEndIndex = 0;

    private int rowIndex = 1;

    private static final String WICKET_ID_bosNavigator = "bosNavigator";
    private static final String WICKET_ID_bosTotalItems = "bosTotalItems";
    private static final String WICKET_ID_bosStartIndex = "bosStartIndex";
    private static final String WICKET_ID_bosEndIndex = "bosEndIndex";
    private static final String WICKET_ID_bosNoItemsMsg = "bosNoItemsMsg";
    private static final String WICKET_ID_bosPageable = "bosPageable";
    private static final String WICKET_ID_bosActive = "bosActive";
    private static final String WICKET_ID_bosDescription = "bosDescription";
    private static final String WICKET_ID_bosValidFrom = "bosValidFrom";
    private static final String WICKET_ID_bosValidTo = "bosValidTo";
    private static final String WICKET_ID_bosAltTimeZone = "bosAltTimeZone";
    private static final String WICKET_ID_bosTimePeriodList = "bosTimePeriodList";
    private static final String WICKET_ID_bosTimePeriod = "bosTimePeriod";
    private static final String WICKET_ID_bosEditAction = "bosEditAction";
    private static final String WICKET_ID_bosRemoveAction = "bosRemoveAction";

    private static final String WICKET_ID_bosEditPanel = "bosEditPanel";

    public AlertBlackOutPanel(String id, MobiliserBasePage basePage,
	    AlertsClientLogic clientLogic, CustomerAlert customerAlert) {

	super(id);

	this.basePage = basePage;
	this.clientLogic = clientLogic;
	this.customerAlert = customerAlert;
	LOG.debug("#AlertBlackOutPanel.init()");

	customerBlackoutScheduleList = this.clientLogic
		.getCustomerBlackoutScheduleByCustomer(this.customerAlert
			.getCustomerId());

	this.constructPanel();
    }

    private void constructPanel() {

	alertBlackOutContainer = new WebMarkupContainer(
		"alertBlackOutContainer");
	alertBlackOutContainer.setOutputMarkupId(true);
	this.createBlackoutScheduleListView(alertBlackOutContainer, true);

	this.createBlackoutScheduleEditPanel(alertBlackOutContainer, null,
		false, dateSequenceExisting);

	add(alertBlackOutContainer);
    }

    private void createBlackoutScheduleListView(WebMarkupContainer parent,
	    boolean enabled) {

	final IDataProvider<CustomerBlackoutSchedule> bosDataProvider = new IDataProvider<CustomerBlackoutSchedule>() {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public void detach() {
	    }

	    @Override
	    public Iterator iterator(int first, int count) {

		return customerBlackoutScheduleList.subList(first,
			first + count).iterator();
	    }

	    @Override
	    public int size() {
		return customerBlackoutScheduleList.size();
	    }

	    @Override
	    public IModel<CustomerBlackoutSchedule> model(
		    final CustomerBlackoutSchedule object) {
		IModel<CustomerBlackoutSchedule> model = new LoadableDetachableModel<CustomerBlackoutSchedule>() {

		    private static final long serialVersionUID = 1L;

		    @Override
		    protected CustomerBlackoutSchedule load() {
			CustomerBlackoutSchedule set = null;
			for (CustomerBlackoutSchedule obj : customerBlackoutScheduleList) {
			    if (obj.getId() == object.getId()) {
				set = obj;
				break;
			    }
			}
			return set;
		    }
		};
		return new CompoundPropertyModel<CustomerBlackoutSchedule>(
			model);
	    }
	};
	if (dateSequenceExisting != null) {
	    dateSequenceExisting.clear();
	}
	for (CustomerBlackoutSchedule obj : customerBlackoutScheduleList) {
	    dateSequenceExisting = AlertsHelper.getDateSequenceFromDateRange(
		    obj.getValidFrom().toGregorianCalendar().getTime(), obj
			    .getValidTo().toGregorianCalendar().getTime(),
		    dateSequenceExisting);
	}

	// just a holder for the add blackout schedule input submit button
	Form<AlertBlackOutPanel> form = new Form<AlertBlackOutPanel>(
		"addBlackoutScheduleForm",
		new CompoundPropertyModel<AlertBlackOutPanel>(this));

	Button addBlackoutSchedule = new Button("addBlackoutSchedule") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		CustomerBlackoutSchedule cbs = new CustomerBlackoutSchedule();
		createBlackoutScheduleEditPanel(alertBlackOutContainer, cbs,
			true, dateSequenceExisting);
	    };
	};

	if (isPageViewedByAgent()) {
	    addBlackoutSchedule.add(new PrivilegedBehavior(basePage,
		    Constants.PRIV_CUST_WRITE));
	}

	form.add(addBlackoutSchedule);

	parent.addOrReplace(form);

	final DataView<CustomerBlackoutSchedule> dataView = new DataView<CustomerBlackoutSchedule>(
		WICKET_ID_bosPageable, bosDataProvider) {

	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onBeforeRender() {

		refreshTotalItemCount();

		if (bosDataProvider.size() > 0) {
		    setVisible(true);
		} else {
		    setVisible(false);
		}

		refreshTotalItemCount();

		super.onBeforeRender();
	    }

	    @Override
	    protected void onComponentTag(ComponentTag arg0) {

	    };

	    @Override
	    protected void populateItem(
		    final Item<CustomerBlackoutSchedule> item) {

		final CustomerBlackoutSchedule entry = item.getModelObject();

		LOG
			.debug("adding blackout schedule #{} to list", entry
				.getId());

		entry.setActive(Boolean.valueOf(isActiveForAlert(customerAlert,
			entry)));

		AjaxCheckBox isActive = new AjaxCheckBox(WICKET_ID_bosActive,
			new PropertyModel<Boolean>(entry, "active")) {

		    private static final long serialVersionUID = 1L;

		    @Override
		    protected void onComponentTag(ComponentTag tag) {

			if (entry.isActive().booleanValue()) {
			    tag.put("checked", "checked");
			}
			super.onComponentTag(tag);
		    }

		    @Override
		    protected void onUpdate(AjaxRequestTarget target) {

			if (entry.isActive().booleanValue()) {
			    addBlackoutToAlert(customerAlert, entry);
			} else {
			    removeBlackoutFromAlert(customerAlert, entry);
			}
		    }

		};

		item.add(isActive);

		item.add(new Label(WICKET_ID_bosDescription, entry
			.getDescription()));

		item.add(new Label(WICKET_ID_bosValidFrom, PortalUtils
			.getFormattedDate(entry.getValidFrom(), basePage
				.getMobiliserWebSession().getLocale())));

		item.add(new Label(WICKET_ID_bosValidTo, PortalUtils
			.getFormattedDate(entry.getValidTo(), basePage
				.getMobiliserWebSession().getLocale())));

		item.add(new Label(WICKET_ID_bosAltTimeZone, entry
			.getAltTimezone()));

		List<CustomerBlackoutTime> botList = entry
			.getBlackoutTimeList().getBlackoutTime();

		final ListView<CustomerBlackoutTime> botListView = new ListView<CustomerBlackoutTime>(
			WICKET_ID_bosTimePeriodList, botList) {

		    private static final long serialVersionUID = 1L;

		    protected void populateItem(
			    ListItem<CustomerBlackoutTime> item) {

			final CustomerBlackoutTime bot = (CustomerBlackoutTime) item
				.getModelObject();

			StringBuilder label = new StringBuilder();

			label.append(addLeadingZero(bot.getStartHr())).append(
				":").append(addLeadingZero(bot.getStartMin()));
			label.append(" - ");
			label.append(addLeadingZero(bot.getEndHr()))
				.append(":").append(
					addLeadingZero(bot.getEndMin()));

			StringBuilder dow = new StringBuilder();
			String dowToRecur = bot.getWeekdaysToRecur();

			if (bot.isForAllDaysOfWeek()) {
			    dowToRecur = "1,2,3,4,5,6,7";
			}

			String[] dowToRecurArr = dowToRecur.split(",");

			if (PortalUtils.exists(dowToRecurArr)) {
			    for (int i = 0; i < dowToRecurArr.length; i++) {
				if (dowToRecurArr[i].trim().equals("1")) {
				    if (dow.length() > 0)
					dow.append(",");
				    dow
					    .append(getLocalizer()
						    .getString(
							    "alertBlackOut.schedule.selectedDaysOfWeek.monday",
							    this));
				} else if (dowToRecurArr[i].trim().equals("2")) {
				    if (dow.length() > 0)
					dow.append(",");
				    dow
					    .append(getLocalizer()
						    .getString(
							    "alertBlackOut.schedule.selectedDaysOfWeek.tuesday",
							    this));
				} else if (dowToRecurArr[i].trim().equals("3")) {
				    if (dow.length() > 0)
					dow.append(",");
				    dow
					    .append(getLocalizer()
						    .getString(
							    "alertBlackOut.schedule.selectedDaysOfWeek.wednesday",
							    this));
				} else if (dowToRecurArr[i].trim().equals("4")) {
				    if (dow.length() > 0)
					dow.append(",");
				    dow
					    .append(getLocalizer()
						    .getString(
							    "alertBlackOut.schedule.selectedDaysOfWeek.thursday",
							    this));
				} else if (dowToRecurArr[i].trim().equals("5")) {
				    if (dow.length() > 0)
					dow.append(",");
				    dow
					    .append(getLocalizer()
						    .getString(
							    "alertBlackOut.schedule.selectedDaysOfWeek.friday",
							    this));
				} else if (dowToRecurArr[i].trim().equals("6")) {
				    if (dow.length() > 0)
					dow.append(",");
				    dow
					    .append(getLocalizer()
						    .getString(
							    "alertBlackOut.schedule.selectedDaysOfWeek.saturday",
							    this));
				} else if (dowToRecurArr[i].trim().equals("7")) {
				    if (dow.length() > 0)
					dow.append(",");
				    dow
					    .append(getLocalizer()
						    .getString(
							    "alertBlackOut.schedule.selectedDaysOfWeek.sunday",
							    this));
				}
			    }
			    label.append(" / ").append(dow.toString());
			}

			item.add(new Label(WICKET_ID_bosTimePeriod, label
				.toString()));
		    }
		};
		item.add(botListView);

		// Edit Action
		Link<CustomerBlackoutSchedule> editLink = new Link<CustomerBlackoutSchedule>(
			WICKET_ID_bosEditAction, item.getModel()) {

		    private static final long serialVersionUID = 1L;

		    @Override
		    public void onClick() {
			CustomerBlackoutSchedule entry = (CustomerBlackoutSchedule) getModelObject();
			dateSequenceExisting = AlertsHelper
				.updateDateSequenceFromDateRange(entry
					.getValidFrom().toGregorianCalendar()
					.getTime(), entry.getValidTo()
					.toGregorianCalendar().getTime(),
					dateSequenceExisting);
			createBlackoutScheduleEditPanel(alertBlackOutContainer,
				entry, true, dateSequenceExisting);
		    }
		};

		// Remove Action
		Link<CustomerBlackoutSchedule> removeLink = new Link<CustomerBlackoutSchedule>(
			WICKET_ID_bosRemoveAction, item.getModel()) {

		    private static final long serialVersionUID = 1L;

		    @Override
		    public void onClick() {
			CustomerBlackoutSchedule entry = (CustomerBlackoutSchedule) getModelObject();

			if (clientLogic.deleteCustomerBlackoutSchedule(entry) != -1) {
			    customerBlackoutScheduleList.remove(entry);
			    AlertsHelper.updateDateSequenceFromDateRange(entry
				    .getValidFrom().toGregorianCalendar()
				    .getTime(), entry.getValidTo()
				    .toGregorianCalendar().getTime(),
				    dateSequenceExisting);
			    info(getLocalizer().getString(
				    "alertBlackOut.table.remove.success", this));
			} else {
			    error(getLocalizer().getString(
				    "alertBlackOut.table.remove.error", this));
			}
		    }
		};

		removeLink.add(new SimpleAttributeModifier("onclick",
			"return confirm('"
				+ getLocalizer().getString(
					"alertBlackOut.table.remove.confirm",
					this) + "');"));

		if (isPageViewedByAgent()) {
		    editLink.add(new PrivilegedBehavior(basePage,
			    Constants.PRIV_CUST_WRITE));
		    removeLink.add(new PrivilegedBehavior(basePage,
			    Constants.PRIV_CUST_WRITE));
		}

		item.add(editLink);

		item.add(removeLink);

		// set items in even/odd rows to different css style classes
		item.add(new AttributeModifier(Constants.CSS_KEYWARD_CLASS,
			true, new AbstractReadOnlyModel<String>() {

			    private static final long serialVersionUID = 1L;

			    @Override
			    public String getObject() {
				return (item.getIndex() % 2 == 1) ? Constants.CSS_STYLE_ODD
					: Constants.CSS_STYLE_EVEN;
			    }
			}));
	    }

	    private void addBlackoutToAlert(CustomerAlert customerAlert,
		    CustomerBlackoutSchedule blackoutSchedule) {

		if (!isActiveForAlert(customerAlert, blackoutSchedule)) {

		    CustomerBlackoutToAlertList cbaList = customerAlert
			    .getBlackoutList();

		    if (!PortalUtils.exists(cbaList)) {
			cbaList = new CustomerBlackoutToAlertList();
		    }

		    CustomerBlackoutToAlert cba = new CustomerBlackoutToAlert();
		    cba.setBlackoutSchedule(blackoutSchedule);
		    cba.setCustomerAlertId(customerAlert.getId());
		    cba.setActive(Boolean.TRUE);
		    cbaList.getBlackout().add(cba);
		}
	    }

	    private void removeBlackoutFromAlert(CustomerAlert customerAlert,
		    CustomerBlackoutSchedule blackoutSchedule) {

		if (isActiveForAlert(customerAlert, blackoutSchedule)) {

		    CustomerBlackoutToAlertList cbaList = customerAlert
			    .getBlackoutList();

		    Iterator<CustomerBlackoutToAlert> iter = cbaList
			    .getBlackout().iterator();

		    while (iter.hasNext()) {
			CustomerBlackoutToAlert cba = iter.next();
			if (cba.getBlackoutSchedule().getId().longValue() == blackoutSchedule
				.getId().longValue()) {
			    cbaList.getBlackout().remove(cba);
			    break;
			}
		    }
		}
	    }

	    private boolean isActiveForAlert(CustomerAlert customerAlert,
		    CustomerBlackoutSchedule blackoutSchedule) {
		CustomerBlackoutToAlertList cbaList = customerAlert
			.getBlackoutList();
		if (!PortalUtils.exists(cbaList)) {
		    cbaList = new CustomerBlackoutToAlertList();
		}

		for (CustomerBlackoutToAlert cba : cbaList.getBlackout()) {
		    if (cba.getBlackoutSchedule().getId().longValue() == blackoutSchedule
			    .getId().longValue()) {
			return true;
		    }
		}
		return false;
	    }

	    private String addLeadingZero(Integer value) {
		if (PortalUtils.exists(value)) {
		    if (value.intValue() < 10) {
			return "0" + String.valueOf(value);
		    }
		    return String.valueOf(value);
		} else {
		    return "NA";
		}
	    }

	    private void refreshTotalItemCount() {
		bosTotalItemString = new Integer(bosDataProvider.size())
			.toString();
		int total = bosDataProvider.size();
		if (total > 0) {
		    bosStartIndex = getCurrentPage() * getItemsPerPage() + 1;
		    bosEndIndex = bosStartIndex + getItemsPerPage() - 1;
		    if (bosEndIndex > total)
			bosEndIndex = total;
		} else {
		    bosStartIndex = 0;
		    bosEndIndex = 0;
		}
	    }
	};

	dataView.setItemsPerPage(10);

	parent.addOrReplace(dataView);

	parent.addOrReplace(new MultiLineLabel(WICKET_ID_bosNoItemsMsg,
		getLocalizer()
			.getString("alertBlackOut.table.noItemsMsg", this)
			+ "\n"
			+ getLocalizer().getString(
				"alertBlackOut.table.addItemHelp", this)) {

	    private static final long serialVersionUID = 1L;

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
	parent.addOrReplace(new CustomPagingNavigator(WICKET_ID_bosNavigator,
		dataView));

	parent.addOrReplace(new Label(WICKET_ID_bosTotalItems,
		new PropertyModel<String>(this, "bosTotalItemString")));

	parent.addOrReplace(new Label(WICKET_ID_bosStartIndex,
		new PropertyModel<Integer>(this, "bosStartIndex")));

	parent.addOrReplace(new Label(WICKET_ID_bosEndIndex,
		new PropertyModel<Integer>(this, "bosEndIndex")));
    }

    private void createBlackoutScheduleEditPanel(WebMarkupContainer parent,
	    CustomerBlackoutSchedule cbs, boolean enabled,
	    ArrayList<Date> dateSequenceExisting) {

	editPanel = new AlertBlackOutSchedulePanel(WICKET_ID_bosEditPanel,
		basePage, this, clientLogic, cbs, customerAlert,
		dateSequenceExisting);

	LOG.debug("showing blackout schedule edit panel");

	// wicket:enclosure will drive whether the whole panel
	// is displayed or not
	editPanel.setVisible(enabled);

	parent.addOrReplace(editPanel);
    }

    public void hideBlackoutScheduleEditPanel() {
	LOG.debug("hiding blackout schedule edit panel");
	editPanel.setVisible(false);
    }

    public void addBlackoutSchedule(CustomerBlackoutSchedule cbs) {
	this.customerBlackoutScheduleList.add(cbs);
	this.createBlackoutScheduleListView(alertBlackOutContainer, true);
    }

    public void setPageViewedByAgent(boolean value) {
	this.pageViewedByAgent = value;
    }

    public boolean isPageViewedByAgent() {
	return this.pageViewedByAgent;
    }
}
