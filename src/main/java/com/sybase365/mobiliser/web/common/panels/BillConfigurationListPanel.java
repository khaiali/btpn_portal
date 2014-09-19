package com.sybase365.mobiliser.web.common.panels;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.ComponentTag;
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
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import com.sybase365.mobiliser.money.contract.v5_0.invoice.beans.InvoiceConfiguration;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.components.KeyValue;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.common.dataproviders.InvoiceConfigurationDataProvider;
import com.sybase365.mobiliser.web.consumer.pages.portal.billpayment.BillConfigurationListPage;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

public abstract class BillConfigurationListPanel extends Panel {

    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(BillConfigurationListPanel.class);

    Long customerId;
    MobiliserBasePage basePage;

    // Data Model for table list
    private InvoiceConfigurationDataProvider dataProvider;

    List<InvoiceConfiguration> selectedBillConfigurations = new ArrayList<InvoiceConfiguration>();
    List<KeyValue<Long, String>> invoiceTypeList = new ArrayList<KeyValue<Long, String>>();

    // Table Items
    private String totalItemString = null;
    private int startIndex = 0;
    private int endIndex = 0;

    private boolean forceReload = true;

    private int rowIndex = 1;

    private static final String WICKET_ID_navigator = "navigator";
    private static final String WICKET_ID_totalItems = "totalItems";
    private static final String WICKET_ID_startIndex = "startIndex";
    private static final String WICKET_ID_endIndex = "endIndex";
    private static final String WICKET_ID_orderByName = "orderByName";
    private static final String WICKET_ID_pageable = "pageable";
    private static final String WICKET_ID_selected = "selected";
    private static final String WICKET_ID_name = "name";
    private static final String WICKET_ID_type = "type";
    private static final String WICKET_ID_reference = "reference";
    private static final String WICKET_ID_editAction = "editAction";
    private static final String WICKET_ID_noItemsMsg = "noItemsMsg";
    private static final String WICKET_ID_removeBillConfiguration = "removeBillConfiguration";

    public BillConfigurationListPanel(String id, MobiliserBasePage basePage,
	    Long customerId) {
	super(id);
	this.basePage = basePage;
	this.customerId = customerId;
	constructPanel();
    }

    private void constructPanel() {
	Form<?> form = new Form("billConfigurationListForm",
		new CompoundPropertyModel<BillConfigurationListPage>(this));

	add(form);

	form.add(new FeedbackPanel("errorMessages"));

	createListDataView(form);
    }

    private void createListDataView(Form form) {

	dataProvider = new InvoiceConfigurationDataProvider(WICKET_ID_name,
		this.basePage);

	form.add(new Button("addBillConfiguration") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		setResponsePage(getAddBillConfigurationPage());
	    };
	});

	final DataView<InvoiceConfiguration> dataView = new DataView<InvoiceConfiguration>(
		WICKET_ID_pageable, dataProvider) {

	    @Override
	    protected void onBeforeRender() {

		try {
		    dataProvider.loadList(customerId, forceReload);
		    forceReload = false;
		    refreshTotalItemCount();
		    invoiceTypeList = basePage
			    .getCustomerAddsInvoiceTypes(true);
		    // reset rowIndex
		    rowIndex = 1;

		} catch (DataProviderLoadException dple) {
		    LOG.error(
			    "# An error occurred while loading bill configuration list",
			    dple);
		    error(getLocalizer().getString(
			    "billConfigurationList.load.error", this));
		}

		refreshTotalItemCount();

		super.onBeforeRender();
	    }

	    @Override
	    protected void populateItem(final Item<InvoiceConfiguration> item) {

		final InvoiceConfiguration entry = item.getModelObject();

		// Select box
		AjaxCheckBox selectedCheckBox = new AjaxCheckBoxImpl(
			WICKET_ID_selected, new Model(isSelected(entry)), entry);

		selectedCheckBox.setOutputMarkupId(true);
		selectedCheckBox.setMarkupId(WICKET_ID_selected + rowIndex++);
		item.add(selectedCheckBox);

		item.add(new Label(WICKET_ID_name, entry.getAlias()));
		item.add(new Label(WICKET_ID_type, getInvoiceTypeName(entry
			.getInvoiceTypeId())));

		item.add(new Label(WICKET_ID_reference, entry.getReference()));

		// Edit Action
		Link<InvoiceConfiguration> editLink = new Link<InvoiceConfiguration>(
			WICKET_ID_editAction, item.getModel()) {
		    @Override
		    public void onClick() {
			InvoiceConfiguration entry = (InvoiceConfiguration) getModelObject();

			setResponsePage(getEditBillConfigurationPage(entry));
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

	    class AjaxCheckBoxImpl extends AjaxCheckBox {

		private final InvoiceConfiguration entry;

		public AjaxCheckBoxImpl(String id, IModel<Boolean> model,
			InvoiceConfiguration entry) {
		    super(id, model);
		    this.entry = entry;
		}

		@Override
		public boolean isEnabled() {
		    return true;
		}

		@Override
		protected void onComponentTag(final ComponentTag tag) {
		    super.onComponentTag(tag);
		    if (getModelObject()) {
			tag.put("checked", "checked");
		    }
		}

		@Override
		protected void onUpdate(AjaxRequestTarget target) {
		    boolean checkBoxSelected = getModelObject();
		    if (checkBoxSelected) {
			LOG.info("Added {} to deletion list", entry.getId());
			selectedBillConfigurations.add(entry);
		    } else {
			LOG.info("Removed {} from deletion list", entry.getId());
			selectedBillConfigurations.remove(entry);
		    }
		}
	    }

	};

	dataView.setItemsPerPage(10);
	form.add(dataView);

	form.add(new OrderByBorder(WICKET_ID_orderByName, WICKET_ID_name,
		dataProvider) {
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

	Button removeButton = new Button(WICKET_ID_removeBillConfiguration) {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleRemove();
	    };

	    @Override
	    public boolean isVisible() {
		if (dataView.getItemCount() > 0) {
		    return super.isVisible();
		} else {
		    return false;
		}
	    }

	}.setDefaultFormProcessing(false);

	removeButton.add(new SimpleAttributeModifier("onclick",
		"return confirm('"
			+ getLocalizer().getString(
				"billConfigurationList.remove.confirm", this)
			+ "');"));

	form.add(removeButton);

	form.add(new MultiLineLabel(WICKET_ID_noItemsMsg, getLocalizer()
		.getString("billConfigurationList.noItemsMsg", this)
		+ "\n"
		+ getLocalizer().getString(
			"billConfigurationList.addBilConfigurationHelp", this)) {
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
	form.add(new CustomPagingNavigator(WICKET_ID_navigator, dataView));

	form.add(new Label(WICKET_ID_totalItems, new PropertyModel<String>(
		this, "totalItemString")));

	form.add(new Label(WICKET_ID_startIndex, new PropertyModel(this,
		"startIndex")));

	form.add(new Label(WICKET_ID_endIndex, new PropertyModel(this,
		"endIndex")));
    }

    public List<InvoiceConfiguration> getSelectedBillConfigurations() {
	return selectedBillConfigurations;
    }

    public boolean isSelected(InvoiceConfiguration entry) {
	return selectedBillConfigurations.contains(entry);
    }

    public void setSelectedBillConfigurations(
	    List<InvoiceConfiguration> selectedBillConfigurations) {
	this.selectedBillConfigurations = selectedBillConfigurations;
    }

    private String getInvoiceTypeName(Long id) {
	if (id == null)
	    return null;
	String invoiceTypeName = null;
	for (KeyValue<Long, String> kv : invoiceTypeList) {
	    if (kv.getKey().equals(id)) {
		invoiceTypeName = kv.getValue();
		break;
	    }
	}
	return invoiceTypeName;
    }

    private void handleRemove() {
	if (!PortalUtils.exists(getSelectedBillConfigurations())) {
	    error(getLocalizer().getString(
		    "billConfigurationList.nothing.selected.remove", this));
	    return;
	}
	InvoiceConfiguration tempIc = null;
	try {
	    for (InvoiceConfiguration ic : getSelectedBillConfigurations()) {
		tempIc = ic;
		if (!basePage.deleteInvoiceConfiguration(ic.getId())) {
		    LOG.error(
			    "# Not able to delete bill configuration [{}] from the list",
			    tempIc.getId());
		}
	    }
	    getSelectedBillConfigurations().clear();

	} catch (Exception e) {
	    if (tempIc != null)
		LOG.error(
			"# An error occurred while deleting bill configuration [{}] from the list",
			tempIc.getId(), e);

	    error(getLocalizer().getString(
		    "billConfigurationList.remove.error", this));
	}

	setResponsePage(getBillConfigurationListPage());
    }

    public abstract MobiliserBasePage getAddBillConfigurationPage();

    public abstract MobiliserBasePage getBillConfigurationListPage();

    public abstract MobiliserBasePage getEditBillConfigurationPage(
	    InvoiceConfiguration entry);

}
