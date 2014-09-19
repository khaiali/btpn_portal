package com.sybase365.mobiliser.web.cst.pages.systemconfig.mbanking;

import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.sybase365.mobiliser.mbanking.contract.v5_0.beans.OptInSetting;
import com.sybase365.mobiliser.util.tools.wicketutils.security.PrivilegedBehavior;
import com.sybase365.mobiliser.web.application.clients.MBankingClientLogic;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.dataproviders.BankTermAndConditionsOptInSettingsDataProvider;
import com.sybase365.mobiliser.web.common.dataproviders.CarrierOptInSettingsDataProvider;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.cst.pages.systemconfig.BaseSystemConfigurationPage;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * @author sagraw03
 */
@AuthorizeInstantiation(Constants.PRIV_CST_MBANKING)
public class OptinSettingsPage extends BaseSystemConfigurationPage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(OptinSettingsPage.class);

    @SpringBean(name = "systemAuthMBankingClientLogic")
    protected MBankingClientLogic mBankingClientLogic;

    private BankTermAndConditionsOptInSettingsDataProvider optInSettingsDataProvider;
    private CarrierOptInSettingsDataProvider carrierOptinRequirementsDataProvider;
    WebMarkupContainer bankTermAndConditionsListContainer;
    boolean pageViewedByAgent;
    private Integer highestPriority;
    private String btcTotalItemString = null;
    private int btcStartIndex = 0;
    private int btcEndIndex = 0;

    private String corTotalItemString = null;
    private int corStartIndex = 0;
    private int corEndIndex = 0;

    private boolean forceReload = true;
    private PrivilegedBehavior cstPriv;

    private static final String WICKET_ID_btcNavigator = "btcNavigator";
    private static final String WICKET_ID_btcTotalItems = "btcTotalItems";
    private static final String WICKET_ID_btcStartIndex = "btcStartIndex";
    private static final String WICKET_ID_btcEndIndex = "btcEndIndex";
    private static final String WICKET_ID_btcNoItemsMsg = "btcNoItemsMsg";
    private static final String WICKET_ID_btcPageable = "btcPageable";
    private static final String WICKET_ID_btcBank = "btcBank";
    private static final String WICKET_ID_btcOrderByBank = "btcOrderByBank";
    private static final String WICKET_ID_btcResetDate = "btcResetDate";
    private static final String WICKET_ID_btcResetNotify = "btcResetNotify";
    private static final String WICKET_ID_btcDisableAlert = "btcDisableAlert";
    private static final String WICKET_ID_btcDisableNotify = "btcDisableNotify";
    private static final String WICKET_ID_btcEditBankTermAndConditions = "btcEditBankTermAndConditions";

    private static final String WICKET_ID_corNavigator = "corNavigator";
    private static final String WICKET_ID_corTotalItems = "corTotalItems";
    private static final String WICKET_ID_corStartIndex = "corStartIndex";
    private static final String WICKET_ID_corEndIndex = "corEndIndex";
    private static final String WICKET_ID_corNoItemsMsg = "corNoItemsMsg";
    private static final String WICKET_ID_corPageable = "corPageable";
    private static final String WICKET_ID_corCarrier = "corCarrier";
    private static final String WICKET_ID_corResetDate = "corResetDate";
    private static final String WICKET_ID_corResetNotify = "corResetNotify";
    private static final String WICKET_ID_corDisableAlert = "corDisableAlert";
    private static final String WICKET_ID_corOrderByCarrier = "corOrderByCarrier";
    private static final String WICKET_ID_corDisableNotify = "corDisableNotify";
    private static final String WICKET_ID_corTokenRequired = "corTokenRequired";
    private static final String WICKET_ID_corEditAction = "corEditAction";

    public OptinSettingsPage() {
	super();
    }

    public OptinSettingsPage(PageParameters parameters) {
	super(parameters);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void initOwnPageComponents() {

	super.initOwnPageComponents();

	LOG.debug("# Inside OptinSettingsPage initOwnPageComponents");

	final Form<?> form = new Form<OptinSettingsPage>("optinSettingsForm",
		new CompoundPropertyModel<OptinSettingsPage>(this));
	form.add(new FeedbackPanel("errorMessages"));

	bankTermAndConditionsListContainer = new WebMarkupContainer(
		"bankTermAndConditionsListContainer");
	form.add(bankTermAndConditionsListContainer);
	this
		.createBankTermAndConditionsDataView(bankTermAndConditionsListContainer);

	WebMarkupContainer carrierOptinRequirmentListContainer = new WebMarkupContainer(
		"carrierOptinRequirmentListContainer");
	form.add(carrierOptinRequirmentListContainer);
	this
		.createCarrierOptinRequirmentListContainerDataView(carrierOptinRequirmentListContainer);
	add(form);

    }

    @SuppressWarnings("unchecked")
    private void createBankTermAndConditionsDataView(WebMarkupContainer parent) {

	optInSettingsDataProvider = new BankTermAndConditionsOptInSettingsDataProvider(
		WICKET_ID_btcBank, mBankingClientLogic);
	final DataView<OptInSetting> dataView = new DataView<OptInSetting>(
		WICKET_ID_btcPageable, optInSettingsDataProvider) {

	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onBeforeRender() {
		try {
		    optInSettingsDataProvider.loadBankTermAndConditions();

		    refreshTotalItemCount();

		    if (optInSettingsDataProvider.size() > 0) {
			setVisible(true);
		    } else {
			setVisible(false);
		    }
		} catch (DataProviderLoadException dple) {
		    LOG
			    .error(
				    "# An error occurred while loading bank term and condition  list",
				    dple);
		    error(getLocalizer().getString(
			    "bank.term.conditions.load.error", this));
		}
		refreshTotalItemCount();
		super.onBeforeRender();
	    }

	    @Override
	    protected void populateItem(final Item<OptInSetting> item) {

		final OptInSetting entry = item.getModelObject();
		item.add(new Label(WICKET_ID_btcBank, entry.getCompanyName()));

		item.add(new Label(WICKET_ID_btcResetDate, PortalUtils
			.getFormattedDate(entry.getResetDate(),
				getMobiliserWebSession().getLocale())));
		item.add(new Label(WICKET_ID_btcResetNotify, displayValue(entry
			.isResetNotify())));
		item.add(new Label(WICKET_ID_btcDisableAlert, entry
			.getDisableDeviceAlertsDays() != null ? entry
			.getDisableDeviceAlertsDays().toString() : ""));
		item.add(new Label(WICKET_ID_btcDisableNotify,
			displayValue(entry.isDisableNotify())));

		AjaxLink<OptInSetting> reset = new AjaxLink<OptInSetting>(
			"reset", item.getModel()) {

		    private static final long serialVersionUID = 1L;

		    @Override
		    public void onClick(AjaxRequestTarget arg0) {
			XMLGregorianCalendar resetDateXml = PortalUtils
				.getSaveXMLGregorianCalendarFromDate(
					new Date(), getMobiliserWebSession()
						.getTimeZone());
			entry.setResetDate(resetDateXml);
			if (mBankingClientLogic.updateOptInSettings(entry) != -1) {
			    getSession()
				    .info(
					    getLocalizer()
						    .getString(
							    "reset.date.update.success.message",
							    this));
			    setResponsePage(OptinSettingsPage.class);
			}
		    }
		};
		item.add(reset);
		AjaxLink<OptInSetting> editBankTermAndConditions = new AjaxLink<OptInSetting>(
			WICKET_ID_btcEditBankTermAndConditions, item.getModel()) {

		    private static final long serialVersionUID = 1L;

		    @Override
		    public void onClick(AjaxRequestTarget arg0) {
			setResponsePage(new EditBankTermAndConditionPage(entry));
		    }
		};
		item.add(editBankTermAndConditions);

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
		btcTotalItemString = new Integer(optInSettingsDataProvider
			.size()).toString();
		int total = optInSettingsDataProvider.size();
		if (total > 0) {
		    btcStartIndex = getCurrentPage() * getItemsPerPage() + 1;
		    btcEndIndex = btcStartIndex + getItemsPerPage() - 1;
		    if (btcEndIndex > total)
			btcEndIndex = total;
		} else {
		    btcStartIndex = 0;
		    btcEndIndex = 0;
		}
	    }
	};

	dataView.setItemsPerPage(5);

	parent.addOrReplace(dataView);

	parent.addOrReplace(new OrderByBorder(WICKET_ID_btcOrderByBank,
		WICKET_ID_btcBank, optInSettingsDataProvider) {
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

	parent.addOrReplace(new MultiLineLabel(WICKET_ID_btcNoItemsMsg,
		getLocalizer()
			.getString("bankTerm.conditions.noItemsMsg", this)) {
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
	parent.addOrReplace(new CustomPagingNavigator(WICKET_ID_btcNavigator,
		dataView));

	parent.addOrReplace(new Label(WICKET_ID_btcTotalItems,
		new PropertyModel<String>(this, "btcTotalItemString")));

	parent.addOrReplace(new Label(WICKET_ID_btcStartIndex,
		new PropertyModel(this, "btcStartIndex")));

	parent.addOrReplace(new Label(WICKET_ID_btcEndIndex, new PropertyModel(
		this, "btcEndIndex")));
    }

    @SuppressWarnings("unchecked")
    private void createCarrierOptinRequirmentListContainerDataView(
	    WebMarkupContainer parent) {

	carrierOptinRequirementsDataProvider = new CarrierOptInSettingsDataProvider(
		WICKET_ID_corResetDate, mBankingClientLogic);

	final DataView<OptInSetting> dataView = new DataView<OptInSetting>(
		WICKET_ID_corPageable, carrierOptinRequirementsDataProvider) {

	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onBeforeRender() {

		try {
		    carrierOptinRequirementsDataProvider
			    .loadCarrierOptInSettings();

		    refreshTotalItemCount();

		    if (carrierOptinRequirementsDataProvider.size() > 0) {
			setVisible(true);
		    } else {
			setVisible(false);

		    }
		} catch (DataProviderLoadException dple) {
		    LOG
			    .error(
				    "# An error occurred while loading carrier opt in requirment  list",
				    dple);
		    error(getLocalizer().getString(
			    "carrier.optin.requirment.load.error", this));
		}

		refreshTotalItemCount();

		super.onBeforeRender();
	    }

	    @Override
	    protected void populateItem(final Item<OptInSetting> item) {

		final OptInSetting entry = item.getModelObject();
		item.add(new Label(WICKET_ID_corResetDate, PortalUtils
			.getFormattedDate(entry.getResetDate(),
				getMobiliserWebSession().getLocale())));

		item.add(new Label(WICKET_ID_corTokenRequired,
			displayValue(entry.isTokenRequired())));

		item
			.add(new Label(WICKET_ID_corCarrier, entry
				.getCompanyName()));

		item.add(new Label(WICKET_ID_corResetNotify, displayValue(entry
			.isResetNotify())));
		item.add(new Label(WICKET_ID_corDisableAlert, entry
			.getDisableDeviceAlertsDays() != null ? entry
			.getDisableDeviceAlertsDays().toString() : ""));
		item.add(new Label(WICKET_ID_corDisableNotify,
			displayValue(entry.isDisableNotify())));
		AjaxLink<OptInSetting> reset = new AjaxLink<OptInSetting>(
			"reset") {

		    private static final long serialVersionUID = 1L;

		    @Override
		    public void onClick(AjaxRequestTarget arg0) {
			XMLGregorianCalendar resetDateXml = PortalUtils
				.getSaveXMLGregorianCalendarFromDate(
					new Date(), getMobiliserWebSession()
						.getTimeZone());
			entry.setResetDate(resetDateXml);
			if (mBankingClientLogic.updateOptInSettings(entry) != -1) {
			    getSession()
				    .info(
					    getLocalizer()
						    .getString(
							    "reset.date.update.success.message",
							    this));
			    setResponsePage(OptinSettingsPage.class);
			}
		    }

		};
		item.add(reset);

		// Edit Action
		Link editLink = new Link<OptInSetting>(WICKET_ID_corEditAction,
			item.getModel()) {
		    @Override
		    public void onClick() {
			setResponsePage(new EditOptinRequirmentsPage(entry));

		    }
		};

		item.add(editLink);
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
		corTotalItemString = new Integer(
			carrierOptinRequirementsDataProvider.size()).toString();
		int total = carrierOptinRequirementsDataProvider.size();
		if (total > 0) {
		    corStartIndex = getCurrentPage() * getItemsPerPage() + 1;
		    corEndIndex = corStartIndex + getItemsPerPage() - 1;
		    if (corEndIndex > total)
			corEndIndex = total;
		} else {
		    corStartIndex = 0;
		    corEndIndex = 0;
		}
	    }
	};

	dataView.setItemsPerPage(3);

	parent.add(dataView);

	parent.add(new OrderByBorder(WICKET_ID_corOrderByCarrier,
		WICKET_ID_corResetDate, carrierOptinRequirementsDataProvider) {
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

	parent.add(new MultiLineLabel(WICKET_ID_corNoItemsMsg, getLocalizer()
		.getString("carrier.optin.requirments.noItemsMsg", this)) {
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
	parent.add(new CustomPagingNavigator(WICKET_ID_corNavigator, dataView));

	parent.add(new Label(WICKET_ID_corTotalItems,
		new PropertyModel<String>(this, "corTotalItemString")));

	parent.add(new Label(WICKET_ID_corStartIndex, new PropertyModel(this,
		"corStartIndex")));

	parent.add(new Label(WICKET_ID_corEndIndex, new PropertyModel(this,
		"corEndIndex")));
    }

    public boolean isPageViewedByAgent() {
	return pageViewedByAgent;
    }

    public void setPageViewedByAgent(boolean pageViewedByAgent) {
	this.pageViewedByAgent = pageViewedByAgent;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Class<OptinSettingsPage> getActiveMenu() {
	return OptinSettingsPage.class;
    }

    private String displayValue(Boolean value) {
	if (PortalUtils.exists(value)) {
	    if (value) {
		return "YES";
	    }
	    return "INACTIVE";
	} else {
	    return "NA";
	}
    }

}
