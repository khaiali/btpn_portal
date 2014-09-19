package com.sybase365.mobiliser.web.common.panels.alerts;

/**
 * @author sagraw03
 */

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;

import com.sybase365.mobiliser.framework.contract.v5_0.base.Locale;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Identification;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerOtherIdentification;
import com.sybase365.mobiliser.util.tools.wicketutils.security.PrivilegedBehavior;
import com.sybase365.mobiliser.web.application.clients.AlertsClientLogic;
import com.sybase365.mobiliser.web.application.clients.MBankingClientLogic;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.dataproviders.CustomerAlertsOtherIdentificationsDataProvider;
import com.sybase365.mobiliser.web.common.dataproviders.CustomerIdentificationsDataProvider;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.util.Constants;

public class ContactPointsPanel extends Panel {

    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(ContactPointsPanel.class);

    private MobiliserBasePage basePage;
    private Class addPage;
    private Class editPage;
    private AlertsClientLogic alertsClientLogic;
    private MBankingClientLogic mBankingClientLogic;
    private Long customerId;
    private String orgUnitId;
    private Locale locale;
    private CustomerIdentificationsDataProvider primaryIdentDataProvider;
    private CustomerAlertsOtherIdentificationsDataProvider otherIdentDataProvider;

    boolean pageViewedByAgent;
    private Integer highestPriority;
    private String cpTotalItemString = null;
    private int cpStartIndex = 0;
    private int cpEndIndex = 0;

    private String ocpTotalItemString = null;
    private int ocpStartIndex = 0;
    private int ocpEndIndex = 0;

    private boolean forceReload = true;
    private PrivilegedBehavior cstPriv;

    private static final String WICKET_ID_cpNavigator = "cpNavigator";
    private static final String WICKET_ID_cpTotalItems = "cpTotalItems";
    private static final String WICKET_ID_cpStartIndex = "cpStartIndex";
    private static final String WICKET_ID_cpEndIndex = "cpEndIndex";
    private static final String WICKET_ID_cpNoItemsMsg = "cpNoItemsMsg";
    private static final String WICKET_ID_cpPageable = "cpPageable";
    private static final String WICKET_ID_cpContact = "cpContact";
    private static final String WICKET_ID_cpOrderByContact = "cpOrderByContact";
    private static final String WICKET_ID_cpContactType = "cpContactType";
    private static final String WICKET_ID_cpSendTextMessage = "cpSendTextMessage";

    private static final String WICKET_ID_ocpNavigator = "ocpNavigator";
    private static final String WICKET_ID_ocpTotalItems = "ocpTotalItems";
    private static final String WICKET_ID_ocpStartIndex = "ocpStartIndex";
    private static final String WICKET_ID_ocpEndIndex = "ocpEndIndex";
    private static final String WICKET_ID_ocpNoItemsMsg = "ocpNoItemsMsg";
    private static final String WICKET_ID_ocpPageable = "ocpPageable";
    private static final String WICKET_ID_ocpContact = "ocpContact";
    private static final String WICKET_ID_ocpNickname = "ocpNickname";
    private static final String WICKET_ID_ocpOrderByNickname = "ocpOrderByNickname";
    private static final String WICKET_ID_ocpContactType = "ocpContactType";
    private static final String WICKET_ID_ocpEditAction = "ocpEditAction";
    private static final String WICKET_ID_ocpRemoveAction = "ocpRemoveAction";
    private static final String WICKET_ID_ocpSendTextMessage = "ocpSendTextMessage";

    public ContactPointsPanel(String id, MobiliserBasePage mobBasePage,
	    AlertsClientLogic alertsClientLogic,
	    MBankingClientLogic mBankingClientLogic, long customerId,
	    String orgUnitId, Locale locale, boolean pageViewedByAgent,
	    Class addPage, Class editPage) {
	super(id);
	this.basePage = mobBasePage;
	this.addPage = addPage;
	this.editPage = editPage;
	this.orgUnitId = orgUnitId;
	this.locale = locale;
	this.alertsClientLogic = alertsClientLogic;
	this.mBankingClientLogic = mBankingClientLogic;
	this.customerId = customerId;
	this.pageViewedByAgent = pageViewedByAgent;
	cstPriv = new PrivilegedBehavior(basePage, Constants.PRIV_CST_LOGIN);

	this.constructPanel();
    }

    private void constructPanel() {

	final Form<?> form = new Form<ContactPointsPanel>("contactPointsForm",
		new CompoundPropertyModel<ContactPointsPanel>(this));
	form.add(new FeedbackPanel("errorMessages"));

	WebMarkupContainer contactPointListContainer = new WebMarkupContainer(
		"contactPointListContainer");
	form.add(contactPointListContainer);
	this.createPrimaryContactPointsDataView(contactPointListContainer);

	WebMarkupContainer otherContactListContainer = new WebMarkupContainer(
		"otherContactListContainer");
	form.add(otherContactListContainer);
	this.createOtherContactPointsDataView(otherContactListContainer);
	add(form);

    }

    private void createPrimaryContactPointsDataView(WebMarkupContainer parent) {

	primaryIdentDataProvider = new CustomerIdentificationsDataProvider(
		WICKET_ID_cpContact, basePage);
	final DataView<Identification> dataView = new DataView<Identification>(
		WICKET_ID_cpPageable, primaryIdentDataProvider) {

	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onBeforeRender() {
		try {
		    primaryIdentDataProvider
			    .loadContactableIdentifications(customerId);

		    refreshTotalItemCount();

		    if (primaryIdentDataProvider.size() > 0) {
			setVisible(true);
		    } else {
			setVisible(false);
		    }
		} catch (DataProviderLoadException dple) {
		    LOG.error(
			    "# An error occurred while loading primary contact points list",
			    dple);
		    error(getLocalizer().getString("contactPoints.load.error",
			    this));
		}
		refreshTotalItemCount();
		super.onBeforeRender();
	    }

	    @Override
	    protected void populateItem(final Item<Identification> item) {

		final Identification entry = item.getModelObject();

		item.add(new Label(WICKET_ID_cpContact, entry
			.getIdentification()));

		item.add(new Label(WICKET_ID_cpContactType,
			getLocalizer()
				.getString(
					"lookup.contTypes."
						+ String.valueOf(entry
							.getType()), this)));

		AjaxLink<Identification> sendTextMessage = new AjaxLink<Identification>(
			WICKET_ID_cpSendTextMessage, item.getModel()) {

		    private static final long serialVersionUID = 1L;

		    @Override
		    public void onClick(AjaxRequestTarget arg0) {
			// To Do message id need to be set
			int status = mBankingClientLogic.sendTestNotification(
				"consumer.balance.alert", customerId,
				entry.getIdentification(), entry.getType(),
				orgUnitId, locale);
			if (status == 0) {
			    getSession()
				    .info(getLocalizer()
					    .getString(
						    "contactPoints.sendTestNotification.message",
						    this)
					    + entry.getIdentification());
			    setResponsePage(basePage);
			}

		    }
		};
		item.add(sendTextMessage);

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
		cpTotalItemString = new Integer(primaryIdentDataProvider.size())
			.toString();
		int total = primaryIdentDataProvider.size();
		if (total > 0) {
		    cpStartIndex = getCurrentPage() * getItemsPerPage() + 1;
		    cpEndIndex = cpStartIndex + getItemsPerPage() - 1;
		    if (cpEndIndex > total)
			cpEndIndex = total;
		} else {
		    cpStartIndex = 0;
		    cpEndIndex = 0;
		}
	    }
	};

	dataView.setItemsPerPage(5);

	parent.add(dataView);

	parent.add(new OrderByBorder(WICKET_ID_cpOrderByContact,
		WICKET_ID_cpContact, primaryIdentDataProvider) {
	    @Override
	    protected void onSortChanged() {
		// For some reasons the dataView can be null when the page is
		// loading
		// and the sort is clicked (clicking the name header), so handle
		// it
		if (dataView != null) {
		    dataView.setCurrentPage(0);
		}
	    }
	});

	parent.add(new MultiLineLabel(WICKET_ID_cpNoItemsMsg, getLocalizer()
		.getString("contactPoints.primaryIdent.noItemsMsg", this)) {
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
	parent.add(new CustomPagingNavigator(WICKET_ID_cpNavigator, dataView));

	parent.add(new Label(WICKET_ID_cpTotalItems, new PropertyModel<String>(
		this, "cpTotalItemString")));

	parent.add(new Label(WICKET_ID_cpStartIndex, new PropertyModel(this,
		"cpStartIndex")));

	parent.add(new Label(WICKET_ID_cpEndIndex, new PropertyModel(this,
		"cpEndIndex")));
    }

    private void createOtherContactPointsDataView(WebMarkupContainer parent) {

	otherIdentDataProvider = new CustomerAlertsOtherIdentificationsDataProvider(
		WICKET_ID_ocpNickname, alertsClientLogic);

	Button addOtherContactPoint = new Button("addOtherContactPoint") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		setResponsePage(addPage);
	    };
	};

	if (isPageViewedByAgent()) {
	    addOtherContactPoint.add(new PrivilegedBehavior(basePage,
		    Constants.PRIV_CUST_WRITE));
	}

	parent.add(addOtherContactPoint);

	final DataView<CustomerOtherIdentification> dataView = new DataView<CustomerOtherIdentification>(
		WICKET_ID_ocpPageable, otherIdentDataProvider) {

	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onBeforeRender() {

		try {
		    otherIdentDataProvider.loadOtherIdentifications(customerId);

		    refreshTotalItemCount();

		    if (otherIdentDataProvider.size() > 0) {
			setVisible(true);
		    } else {
			setVisible(false);

		    }
		} catch (DataProviderLoadException dple) {
		    LOG.error(
			    "# An error occurred while loading primary contact points list",
			    dple);
		    error(getLocalizer().getString("contactPoints.load.error",
			    this));
		}

		refreshTotalItemCount();

		super.onBeforeRender();
	    }

	    @Override
	    protected void populateItem(
		    final Item<CustomerOtherIdentification> item) {

		final CustomerOtherIdentification entry = item.getModelObject();

		item.add(new Label(WICKET_ID_ocpNickname, entry.getNickname()));

		item.add(new Label(WICKET_ID_ocpContactType,
			getLocalizer()
				.getString(
					"lookup.contTypes."
						+ String.valueOf(entry
							.getType()), this)));

		item.add(new Label(WICKET_ID_ocpContact, entry
			.getIdentification()));

		// Edit Action
		Link editLink = new Link<CustomerOtherIdentification>(
			WICKET_ID_ocpEditAction, item.getModel()) {
		    @Override
		    public void onClick() {
			CustomerOtherIdentification entry = (CustomerOtherIdentification) getModelObject();
			try {
			    setResponsePage(((Class<MobiliserBasePage>) editPage)
				    .getConstructor(
					    CustomerOtherIdentification.class)
				    .newInstance(entry));
			} catch (Exception e) {
			    LOG.error(
				    "Couldn't create edit other contact point page ",
				    e);
			}
		    }
		};

		item.add(editLink);

		// Remove Action
		Link removeLink = new Link<CustomerOtherIdentification>(
			WICKET_ID_ocpRemoveAction, item.getModel()) {
		    @Override
		    public void onClick() {
			CustomerOtherIdentification entry = (CustomerOtherIdentification) getModelObject();

			int status = alertsClientLogic
				.deleteCustomerOtherIdentification(entry
					.getId());

			if (status == 0) {
			    info(getLocalizer().getString(
				    "contactPoints.actionDelete.message", this));

			    otherIdentDataProvider.getOtherIdentifications()
				    .remove(entry);

			    setResponsePage(basePage);
			}
		    }
		};

		removeLink
			.add(new SimpleAttributeModifier(
				"onclick",
				"return confirm('"
					+ getLocalizer()
						.getString(
							"contactPoints.contactPointsTable.remove.confirm",
							this) + "');"));

		item.add(removeLink);

		AjaxLink<CustomerOtherIdentification> sendTextMessageLink = new AjaxLink<CustomerOtherIdentification>(
			WICKET_ID_ocpSendTextMessage, item.getModel()) {

		    @Override
		    public void onClick(AjaxRequestTarget arg0) {
			// To Do message id need to be set
			int status = mBankingClientLogic.sendTestNotification(
				"consumer.balance.alert", customerId,
				entry.getIdentification(), entry.getType(),
				orgUnitId, locale);
			if (status == 0) {
			    getSession()
				    .info(getLocalizer()
					    .getString(
						    "contactPoints.sendTestNotification.message",
						    this)
					    + entry.getIdentification());
			    setResponsePage(basePage);
			}
		    }
		};
		item.add(sendTextMessageLink);

		if (!(entry.getType() == Constants.IDENT_TYPE_MSISDN || entry
			.getType() == Constants.IDENT_TYPE_EMAIL)) {
		    sendTextMessageLink.setVisible(false);
		}

		if (isPageViewedByAgent()) {
		    editLink.add(new PrivilegedBehavior(basePage,
			    Constants.PRIV_CUST_WRITE));
		    removeLink.add(new PrivilegedBehavior(basePage,
			    Constants.PRIV_CUST_WRITE));
		}

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
		ocpTotalItemString = new Integer(otherIdentDataProvider.size())
			.toString();
		int total = otherIdentDataProvider.size();
		if (total > 0) {
		    ocpStartIndex = getCurrentPage() * getItemsPerPage() + 1;
		    ocpEndIndex = ocpStartIndex + getItemsPerPage() - 1;
		    if (ocpEndIndex > total)
			ocpEndIndex = total;
		} else {
		    ocpStartIndex = 0;
		    ocpEndIndex = 0;
		}
	    }
	};

	dataView.setItemsPerPage(3);

	parent.add(dataView);

	parent.add(new OrderByBorder(WICKET_ID_ocpOrderByNickname,
		WICKET_ID_ocpNickname, otherIdentDataProvider) {
	    @Override
	    protected void onSortChanged() {
		// For some reasons the dataView can be null when the page is
		// loading
		// and the sort is clicked (clicking the name header), so handle
		// it
		if (dataView != null) {
		    dataView.setCurrentPage(0);
		}
	    }
	});

	parent.add(new MultiLineLabel(
		WICKET_ID_ocpNoItemsMsg,
		getLocalizer().getString(
			"contactPoints.otherContactsTable.noItemsMsg", this)
			+ "\n"
			+ getLocalizer()
				.getString(
					"contactPoints.otherContactsTable.addContactHelp",
					this)) {
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
	parent.add(new CustomPagingNavigator(WICKET_ID_ocpNavigator, dataView));

	parent.add(new Label(WICKET_ID_ocpTotalItems,
		new PropertyModel<String>(this, "ocpTotalItemString")));

	parent.add(new Label(WICKET_ID_ocpStartIndex, new PropertyModel(this,
		"ocpStartIndex")));

	parent.add(new Label(WICKET_ID_ocpEndIndex, new PropertyModel(this,
		"ocpEndIndex")));
    }

    public boolean isPageViewedByAgent() {
	return pageViewedByAgent;
    }

    public void setPageViewedByAgent(boolean pageViewedByAgent) {
	this.pageViewedByAgent = pageViewedByAgent;
    }

}