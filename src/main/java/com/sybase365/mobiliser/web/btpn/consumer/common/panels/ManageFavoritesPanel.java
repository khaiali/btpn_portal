package com.sybase365.mobiliser.web.btpn.consumer.common.panels;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.core.preregistered.facade.contract.FindPreRegisteredRequest;
import com.btpnwow.core.preregistered.facade.contract.FindPreRegisteredResponse;
import com.btpnwow.core.preregistered.facade.contract.PreRegisteredFindViewType;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.common.components.BtpnCustomPagingNavigator;
import com.sybase365.mobiliser.web.btpn.consumer.beans.ManageFavoritesBean;
import com.sybase365.mobiliser.web.btpn.consumer.common.dataproviders.ManageFavoritesDataProvider;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.AddFavoriteConfirmPage;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.AddFavoritePage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnOrderByOrder;
import com.sybase365.mobiliser.web.btpn.util.BtpnUtils;
import com.sybase365.mobiliser.web.util.PortalUtils;


public class ManageFavoritesPanel extends Panel {

	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(ManageFavoritesPanel.class);

	protected BtpnMobiliserBasePage basePage;
	ManageFavoritesBean favoritesBean;
	private FeedbackPanel feedBack;

	private static final String WICKET_ID_PAGEABLE = "pageable";
	private static final String WICKET_ID_DETAILS_LINK = "detailsLink";
	private static final String WICKET_ID_REMOVE_LINK = "removeLink";
	private static final String WICKET_ID_DETAILS_LINK_NAME = "detailsLinkName";
	private static final String WICKET_ID_REMOVE_LINK_NAME = "removeLinkName";
	private static final String WICKET_ID_approvalNavigator = "approvalNavigator";
	private static final String WICKET_ID_approvalTotalItems = "approvalHeader";

	private String approvalTotalItemString;
	private int approvalStartIndex = 0;
	private int approvalEndIndex = 0;
	private Label approvalHeader;
	private Label noRecordsLabel;

	private BtpnCustomPagingNavigator navigator;

	public ManageFavoritesPanel(String id, BtpnMobiliserBasePage basePage) {
		super(id);
		this.basePage = basePage;
		constructPanel();
	}

	protected void constructPanel() {
		
		final Form<ManageFavoritesPanel> form = new Form<ManageFavoritesPanel>("manageFavoritesForm",
			new CompoundPropertyModel<ManageFavoritesPanel>(this));
		
		feedBack = new FeedbackPanel("errorMessages");
		feedBack.setOutputMarkupId(true);
		form.add(feedBack);
		String message = getLocalizer().getString("label.noDataFound", ManageFavoritesPanel.this);
		noRecordsLabel = new Label("emptyRecordsMessage", message);
		noRecordsLabel.setOutputMarkupId(true);
		noRecordsLabel.setVisible(false);
		form.add(noRecordsLabel);

		List<ManageFavoritesBean> favoritesList = getAvailableFavoritesList();
		if (!PortalUtils.exists(favoritesList)) {
			noRecordsLabel.setVisible(true);
		} else {
			noRecordsLabel.setVisible(false);
		}

		// Add the favorites container
		final WebMarkupContainer approvalContainer = new WebMarkupContainer("manageFavoritesContainer");
		approvalsMsisdnDataView(approvalContainer);
		approvalContainer.setOutputMarkupId(true);
		form.add(approvalContainer);

		form.add(new Button("addButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(new AddFavoritePage());
			};
		});
		add(form);
	}

	/**
	 * This method adds the approvalsDataView for the MSISDN and also adds the sorting logic for data view
	 */
	protected void approvalsMsisdnDataView(final WebMarkupContainer dataViewContainer) {
		final ManageFavoritesDataProvider approvalDataProvider = new ManageFavoritesDataProvider("name",
			getAvailableFavoritesList());
		final DataView<ManageFavoritesBean> dataView = new FavoritesDataView(WICKET_ID_PAGEABLE, approvalDataProvider);
		dataView.setItemsPerPage(20);

		navigator = new BtpnCustomPagingNavigator(WICKET_ID_approvalNavigator, dataView) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return approvalDataProvider.size() != 0;
			}
		};
		navigator.setOutputMarkupId(true);
		navigator.setOutputMarkupPlaceholderTag(true);
		dataViewContainer.add(navigator);

		// Add the header
		IModel<String> headerDisplayModel = new AbstractReadOnlyModel<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getObject() {
				final String displayTotalItemsText = ManageFavoritesPanel.this.getLocalizer().getString(
					"approval.totalitems.header", ManageFavoritesPanel.this);
				return String.format(displayTotalItemsText, approvalTotalItemString, approvalStartIndex,
					approvalEndIndex);
			}

		};
		approvalHeader = new Label(WICKET_ID_approvalTotalItems, headerDisplayModel) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return approvalDataProvider.size() != 0;
			}
		};
		dataViewContainer.add(approvalHeader);
		approvalHeader.setOutputMarkupId(true);
		approvalHeader.setOutputMarkupPlaceholderTag(true);

		// Add the sort providers
		dataViewContainer.add(new BtpnOrderByOrder("orderByName", "name", approvalDataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByValue", "value", approvalDataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByType", "type", approvalDataProvider, dataView));
		dataViewContainer.addOrReplace(dataView);
	}

	/**
	 * This is the favoritesDataView for consumer portal
	 * 
	 * @author Narasa Reddy
	 */
	private class FavoritesDataView extends DataView<ManageFavoritesBean> {
		private static final long serialVersionUID = 1L;

		protected FavoritesDataView(String id, IDataProvider<ManageFavoritesBean> dataProvider) {
			super(id, dataProvider);
			setOutputMarkupId(true);
			setOutputMarkupPlaceholderTag(true);
		}

		@Override
		protected void onBeforeRender() {
			refreshTotalItemCount();
			super.onBeforeRender();
		}

		@Override
		protected void populateItem(final Item<ManageFavoritesBean> item) {
			final ManageFavoritesBean entry = item.getModelObject();

			// Add the Name
			item.add(new Label("favoriteName", entry.getFavoriteName()));
			// Add the Value
			item.add(new Label("favoriteValue", entry.getFavoriteValue()));
			// Add the Type
			log.info(" ### (ManageFavoritePanel::populateItem) FAV TYPE ### " +entry.getFavoritesType().getValue());
			item.add(new Label("favoriteType", entry.getFavoritesType().getValue()));

			// Add the Details Link
			final AjaxLink<ManageFavoritesBean> detailsLink = new AjaxLink<ManageFavoritesBean>(WICKET_ID_DETAILS_LINK,
				item.getModel()) {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					final ManageFavoritesBean favoritesBean = (ManageFavoritesBean) item.getModelObject();
					favoritesBean.setSelectedLink("Details");
					setResponsePage(new AddFavoriteConfirmPage(favoritesBean));
				}
			};
			detailsLink.add(new Label(WICKET_ID_DETAILS_LINK_NAME, "Details"));
			item.add(detailsLink);

			// Add the Remove Link
			final AjaxLink<ManageFavoritesBean> removeLink = new AjaxLink<ManageFavoritesBean>(WICKET_ID_REMOVE_LINK,
				item.getModel()) {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					final ManageFavoritesBean favoritesBean = (ManageFavoritesBean) item.getModelObject();
					favoritesBean.setSelectedLink("Remove");
					setResponsePage(new AddFavoriteConfirmPage(favoritesBean));
				}
			};
			removeLink.add(new Label(WICKET_ID_REMOVE_LINK_NAME, "Remove"));
			item.add(removeLink);

			final String cssStyle = item.getIndex() % 2 == 0 ? BtpnConstants.DATA_VIEW_EVEN_ROW_CSS : BtpnConstants.DATA_VIEW_ODD_ROW_CSS;
			item.add(new SimpleAttributeModifier("class", cssStyle));
		}

		@Override
		public boolean isVisible() {
			return ((ManageFavoritesDataProvider) internalGetDataProvider()).size() != 0;
		}

		private void refreshTotalItemCount() {
			final int size = internalGetDataProvider().size();
			approvalTotalItemString = new Integer(size).toString();
			if (size > 0) {
				approvalStartIndex = getCurrentPage() * getItemsPerPage() + 1;
				approvalEndIndex = approvalStartIndex + getItemsPerPage() - 1;
				if (approvalEndIndex > size) {
					approvalEndIndex = size;
				}
			} else {
				approvalStartIndex = 0;
				approvalEndIndex = 0;
			}
		}
	}
	

	public List<ManageFavoritesBean> getAvailableFavoritesList() {
		
		List<ManageFavoritesBean> favoritesList = new ArrayList<ManageFavoritesBean>();
		
		try {
			
			log.info(" ### (ManageFavoritePanel::getAvailableFavoritesList) ### ");
			
			final FindPreRegisteredRequest request = basePage.getNewMobiliserRequest(FindPreRegisteredRequest.class);
			long customerId = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId();
			request.setCustomerId(customerId);
			request.setStart(0);
			request.setLength(Integer.MAX_VALUE);
			
			FindPreRegisteredResponse response = basePage.getFavClient().find(request);
			log.info(" ### (ManageFavoritePanel::getAvailableFavoritesList) RESPONSE ### " +response.getStatus().getCode());
			if (basePage.evaluateConsumerPortalMobiliserResponse(response)) {
				return convertToManageFavoritesBean(response.getItem());
			}
			
		} catch (Exception ex) {
			log.error("#An error occurred while calling getAllFavorites service.", ex);
			error(getLocalizer().getString("error.exception", this));
		}
		
		return favoritesList;
	}
	
	
	public List<ManageFavoritesBean> convertToManageFavoritesBean(List<PreRegisteredFindViewType> favList) {
		
		List<ManageFavoritesBean> favoriteList = new ArrayList<ManageFavoritesBean>();
		
		for (PreRegisteredFindViewType bean : favList) {
			ManageFavoritesBean favoritesBean = new ManageFavoritesBean();
			log.info(" ### (ManageFavoritesPanel::convertToManageFavoritesBean) ID ### " + bean.getId());
			favoritesBean.setId(bean.getId());
			log.info(" ### (ManageFavoritesPanel::convertToManageFavoritesBean) NAME ### " + bean.getName());
			favoritesBean.setFavoriteName(bean.getName());
			log.info(" ### (ManageFavoritesPanel::convertToManageFavoritesBean) VALUE ### " + bean.getValue());
			favoritesBean.setFavoriteValue(bean.getValue());
			favoritesBean.setDescription(bean.getDescription());
			
			/* FAVORITE TYPE */
			CodeValue cd = new CodeValue();
			String favoriteType = basePage.getDisplayValue(String.valueOf(bean.getType()),
				BtpnConstants.RESOURCE_BUBDLE_MANAGE_FAVORITE_TYPES);
			cd.setId(String.valueOf(bean.getType()));
			log.info(" ### (ManageFavoritesPanel::convertToManageFavoritesBean) TYPE ### " + favoriteType);
			cd.setValue(favoriteType);
			favoritesBean.setFavoritesType(cd);
							
			favoriteList.add(favoritesBean);
		}
		
		return favoriteList;
	}
}
