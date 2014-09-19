package com.sybase365.mobiliser.web.common.panels.mbanking;

/**
 * @author sushil.agrawal
 */

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;

import com.sybase365.mobiliser.mbanking.contract.v5_0.beans.ServicePackage;
import com.sybase365.mobiliser.util.tools.wicketutils.security.PrivilegedBehavior;
import com.sybase365.mobiliser.web.application.clients.MBankingClientLogic;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.common.dataproviders.ServicePackagesDataProvider;
import com.sybase365.mobiliser.web.cst.pages.BaseCstPage;
import com.sybase365.mobiliser.web.cst.pages.systemconfig.mbanking.BankServicePackagePage;
import com.sybase365.mobiliser.web.util.Constants;

public class ServicePackagesPanel extends Panel {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(ServicePackagesPanel.class);

    private MBankingClientLogic clientLogic;
    private String spNamee;
    private BaseCstPage baseCstPage;
    private String orgUnitId;
    private boolean pageViewedByAgent;
    private List<ServicePackage> servicePackageEntries;
    private Integer highestPriority;
    private String cpTotalItemString = null;
    private ServicePackagesDataProvider spDataProvider;
    private List<ServicePackage> servicePackageList;
    private int cpStartIndex = 0;
    private int cpEndIndex = 0;

    private static final String WICKET_ID_servicePackageForm = "servicePackageForm";
    private static final String WICKET_ID_servicePackageListContainer = "servicePackageListContainer";

    private static final String WICKET_ID_spName = "spName";
    private static final String WICKET_ID_spServiceCode = "spServiceCode";
    private static final String WICKET_ID_spEditAction = "spEditAction";
    private static final String WICKET_ID_spRemoveAction = "spRemoveAction";

    private static final String WICKET_ID_spNavigator = "spNavigator";
    private static final String WICKET_ID_spTotalItems = "spTotalItems";
    private static final String WICKET_ID_spStartIndex = "spStartIndex";
    private static final String WICKET_ID_spEndIndex = "spEndIndex";
    private static final String WICKET_ID_spNoItemsMsg = "spNoItemsMsg";
    private static final String WICKET_ID_spPageable = "spPageable";
    private static final String WICKET_ID_spOrderByNickname = "spOrderByNickname";

    public ServicePackagesPanel(String id, BaseCstPage mobBasePage,
	    MBankingClientLogic clientLogic, String orgUnitId,
	    boolean pageViewedByAgent) {
	super(id);
	this.baseCstPage = mobBasePage;
	this.orgUnitId = orgUnitId;
	this.clientLogic = clientLogic;
	this.pageViewedByAgent = pageViewedByAgent;
	new PrivilegedBehavior(baseCstPage, Constants.PRIV_CST_LOGIN);
	this.constructPanel();
    }

    private void constructPanel() {

	final Form<?> form = new Form<ServicePackagesPanel>(
		WICKET_ID_servicePackageForm,
		new CompoundPropertyModel<ServicePackagesPanel>(this));

	WebMarkupContainer servicePackageListContainer = new WebMarkupContainer(
		WICKET_ID_servicePackageListContainer);
	servicePackageListContainer.add(new FeedbackPanel("errorMessages"));
	form.add(servicePackageListContainer);
	this.createservicePackageListDataView(servicePackageListContainer);
	servicePackageListContainer.add(new AjaxLink("addServicePackage") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onClick(final AjaxRequestTarget target) {
		ServicePackage entry = new ServicePackage();
		setResponsePage(new BankServicePackagePage(entry, "Add"));
	    }
	});
	add(form);
    }

    private void createservicePackageListDataView(WebMarkupContainer parent) {

	spDataProvider = new ServicePackagesDataProvider(WICKET_ID_spName,
		clientLogic);
	servicePackageList = new ArrayList<ServicePackage>();
	final DataView<ServicePackage> dataView = new DataView<ServicePackage>(
		WICKET_ID_spPageable, spDataProvider) {

	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onBeforeRender() {

		try {
		    spDataProvider.loadAllServicePackages(orgUnitId);
		    refreshTotalItemCount();
		    if (spDataProvider.size() > 0) {
			setVisible(true);
		    } else {
			setVisible(false);
		    }
		} catch (DataProviderLoadException dple) {
		    LOG
			    .error(
				    "# An error occurred while loading Service Package list",
				    dple);
		    error(getLocalizer().getString(
			    "manageAccounts.bank.load.error", this));
		}
		refreshTotalItemCount();
		super.onBeforeRender();
	    }

	    @Override
	    protected void populateItem(final Item<ServicePackage> item) {
		final ServicePackage entry = item.getModelObject();
		servicePackageList.add(entry);
		item.add(new Label(WICKET_ID_spName, entry.getName()));
		item.add(new Label(WICKET_ID_spServiceCode, entry
			.getServicePackageId()));
		// Edit Action
		Link<ServicePackage> editLink = new Link<ServicePackage>(
			WICKET_ID_spEditAction, item.getModel()) {

		    private static final long serialVersionUID = -5360677761374811392L;

		    @Override
		    public void onClick() {
			setResponsePage(new BankServicePackagePage(entry,
				"Edit"));
		    }
		};
		// Remove Action
		Link<ServicePackage> removeLink = new Link<ServicePackage>(
			WICKET_ID_spRemoveAction, item.getModel()) {

		    private static final long serialVersionUID = -5648391221008413258L;

		    @Override
		    public void onClick() {
			int status = clientLogic
				.removeBankServicePackage(entry);
			if (status == 0) {
			    getSession()
				    .info(
					    getLocalizer()
						    .getString(
							    "servicePackage.actionDelete.message",
							    this));
			}
		    }
		};
		removeLink
			.add(new SimpleAttributeModifier(
				"onclick",
				"return confirm('"
					+ getLocalizer()
						.getString(
							"servicePackage.servicePackageTable.remove.confirm",
							this) + "');"));

		if (isPageViewedByAgent()) {
		    editLink.add(new PrivilegedBehavior(baseCstPage,
			    Constants.PRIV_CUST_WRITE));
		    removeLink.add(new PrivilegedBehavior(baseCstPage,
			    Constants.PRIV_CUST_WRITE));
		}
		item.add(editLink);
		item.add(removeLink);
		removeLink.setVisible(false);
		// set items in even/odd rows to different css style classes
		item.add(new AttributeModifier(Constants.CSS_KEYWARD_CLASS,
			true, new AbstractReadOnlyModel<String>() {

			    private static final long serialVersionUID = 7540125567994387876L;

			    @Override
			    public String getObject() {
				return (item.getIndex() % 2 == 1) ? Constants.CSS_STYLE_ODD
					: Constants.CSS_STYLE_EVEN;
			    }
			}));
	    }

	    private void refreshTotalItemCount() {
		cpTotalItemString = new Integer(spDataProvider.size())
			.toString();
		int total = spDataProvider.size();
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
	parent.add(new OrderByBorder(WICKET_ID_spOrderByNickname,
		WICKET_ID_spName, spDataProvider) {
	    private static final long serialVersionUID = -1696556756649214998L;

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

	parent.add(new MultiLineLabel(WICKET_ID_spNoItemsMsg, getLocalizer()
		.getString(
			"servicePackage.servicePackageTable.noServicePackage",
			this)) {
	    private static final long serialVersionUID = -8884269132391923639L;

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
	parent.add(new CustomPagingNavigator(WICKET_ID_spNavigator, dataView));
	parent.add(new Label(WICKET_ID_spTotalItems, new PropertyModel<String>(
		this, "cpTotalItemString")));
	parent.add(new Label(WICKET_ID_spStartIndex, new PropertyModel(this,
		"cpStartIndex")));
	parent.add(new Label(WICKET_ID_spEndIndex, new PropertyModel(this,
		"cpEndIndex")));
    }

    public boolean isPageViewedByAgent() {
	return pageViewedByAgent;
    }

    public void setPageViewedByAgent(boolean pageViewedByAgent) {
	this.pageViewedByAgent = pageViewedByAgent;
    }

    public void setSpNamee(String spNamee) {
	this.spNamee = spNamee;
    }

    public String getSpNamee() {
	return spNamee;
    }
}
