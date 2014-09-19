package com.sybase365.mobiliser.web.common.panels;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.PropertyModel;

import com.sybase365.mobiliser.money.contract.v5_0.customer.GetCustomerHistoryRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.HistoryEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.security.PrivilegedBehavior;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.common.dataproviders.HistoryEntryDataProvider;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class HistoryPanel extends Panel {
    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(HistoryPanel.class);

    MobiliserBasePage basePage;
    GetCustomerHistoryRequest historyReq;
    HistoryEntryDataProvider dataProvider;
    private boolean forceReload = true;

    // Table Items
    private String totalItemString = null;
    private int startIndex = 0;
    private int endIndex = 0;

    private static final String WICKET_ID_navigator = "navigator";
    private static final String WICKET_ID_totalItems = "totalItems";
    private static final String WICKET_ID_startIndex = "startIndex";
    private static final String WICKET_ID_endIndex = "endIndex";
    private static final String WICKET_ID_pageable = "pageable";
    private static final String WICKET_ID_creationDate = "creationDate";
    private static final String WICKET_ID_fieldName = "fieldName";
    private static final String WICKET_ID_creatorId = "creatorId";
    private static final String WICKET_ID_oldValue = "oldValue";
    private static final String WICKET_ID_newValue = "newValue";
    private static final String WICKET_ID_noItemsMsg = "noItemsMsg";

    private PrivilegedBehavior cstBehavior;

    public HistoryPanel(String id, MobiliserBasePage basePage,
	    GetCustomerHistoryRequest historyReq) {
	super(id);
	this.basePage = basePage;
	this.historyReq = historyReq;
	cstBehavior = new PrivilegedBehavior(basePage,
		Constants.PRIV_CST_LOGIN, Constants.PRIV_CUST_READ);
	cstBehavior.setMatchAllPrivileges(true);
	if (historyReq != null)
	    constructPanel();
    }

    private void constructPanel() {
	add(new Label("h_creatorId", getLocalizer().getString(
		"showHistory.creatorId", this)).add(cstBehavior));
	dataProvider = new HistoryEntryDataProvider(WICKET_ID_creationDate,
		basePage);
	final DataView<HistoryEntry> dataView = new DataView<HistoryEntry>(
		WICKET_ID_pageable, dataProvider) {

	    @Override
	    protected void onBeforeRender() {

		try {
		    dataProvider.loadHistory(historyReq, forceReload);
		    forceReload = false;
		    refreshTotalItemCount();
		} catch (DataProviderLoadException dple) {
		    LOG.error(
			    "# An error occurred while loading customer history list",
			    dple);
		    error(getLocalizer().getString("history.load.error", this));
		} catch (Exception e) {
		    LOG.error(
			    "# An error occurred while loading customer history list",
			    e);
		    error(getLocalizer().getString("history.load.error", this));
		}

		refreshTotalItemCount();
		if (dataProvider.size() > 0) {
		    setVisible(true);
		} else {
		    setVisible(false);
		}

		super.onBeforeRender();
	    }

	    @Override
	    protected void populateItem(final Item<HistoryEntry> item) {

		final HistoryEntry entry = item.getModelObject();

		item.add(new Label(WICKET_ID_creationDate, PortalUtils
			.getFormattedDateTime(entry.getCreationDate(), basePage
				.getMobiliserWebSession().getLocale(), basePage
				.getMobiliserWebSession().getTimeZone())));
		item.add(new Label(WICKET_ID_creatorId, entry.getCreatorId()
			.toString()).add(cstBehavior));
		item.add(new Label(WICKET_ID_fieldName, basePage
			.getDisplayValue(
				entry.getTableName() + "."
					+ entry.getFieldName(),
				Constants.RESOURCE_BUNDLE_HISTORY_ENTRIES)));
		item.add(new Label(WICKET_ID_oldValue, entry.getOldValue()));

		item.add(new Label(WICKET_ID_newValue, entry.getNewValue()));

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

	dataView.setItemsPerPage(20);
	add(dataView);

	// Navigator example: << < 1 2 > >>
	add(new CustomPagingNavigator(WICKET_ID_navigator, dataView));

	add(new Label(WICKET_ID_totalItems, new PropertyModel<String>(this,
		"totalItemString")));

	add(new Label(WICKET_ID_startIndex, new PropertyModel(this,
		"startIndex")));

	add(new Label(WICKET_ID_endIndex, new PropertyModel(this, "endIndex")));

	add(new MultiLineLabel(WICKET_ID_noItemsMsg, getLocalizer().getString(
		"showHistory.noItemsMsg", this)) {
	    @Override
	    public boolean isVisible() {
		if (dataView.getItemCount() > 0) {
		    return false;
		} else {
		    return super.isVisible();
		}
	    }
	});

    }
}
