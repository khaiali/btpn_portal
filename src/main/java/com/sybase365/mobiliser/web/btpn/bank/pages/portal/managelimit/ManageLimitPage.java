package com.sybase365.mobiliser.web.btpn.bank.pages.portal.managelimit;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.limit.GetAllLimitsRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.limit.GetAllLimitsResponse;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.limit.GetLimitDetailsRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.limit.GetLimitDetailsResponse;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageLimitBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.components.BtpnCustomPagingNavigator;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.ConverterUtils;

/**
 * This is the Manage Limit page for bank portals.
 * 
 * @author Vikram Gunda
 */
public class ManageLimitPage extends BtpnBaseBankPortalSelfCarePage {

	private static final String WICKET_ID_PAGEABLE = "pageable";

	private static final String WICKET_ID_LINK = "detailsLink";

	private static final String WICKET_ID_LIMITNAVIGATOR = "limitNavigator";

	private static final String WICKET_ID_LIMITTOTALITEMS = "limitHeader";

	private String limitsTotalItemString;

	private int limitsStartIndex = 0;

	private int limitsEndIndex = 0;

	private Label limitsHeader;

	private FeedbackPanel feedBackPanel;

	private BtpnCustomPagingNavigator navigator;

	private static final Logger LOG = LoggerFactory.getLogger(ManageLimitPage.class);

	/**
	 * Constructor for this page.
	 */
	public ManageLimitPage() {
		super();
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	@Override
	protected void initOwnPageComponents() {
		super.initOwnPageComponents();
		constructPage();

	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	private void constructPage() {
		Form<ManageLimitPage> form = new Form<ManageLimitPage>("limitForm", new CompoundPropertyModel<ManageLimitPage>(
			this));
		// Add the Manage Products container
		WebMarkupContainer productsContainer = new WebMarkupContainer("limitContainer");
		productsContainer.add(feedBackPanel = new FeedbackPanel("errorMessages"));
		manageLimitDataView(productsContainer);
		productsContainer.setOutputMarkupId(true);
		productsContainer.setRenderBodyOnly(true);
		productsContainer.add(addAddButton());
		form.add(productsContainer);
		// Add add Button
		add(form);

	}

	/**
	 * This method adds the Add button for the Manage Products
	 */
	protected Button addAddButton() {
		Button submitButton = new Button("btnAdd") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(ManageLimitAddPage.class);
			}
		};
		return submitButton;
	}

	/**
	 * This method adds the Manage Limit data view and also adds the sorting logic for data view
	 */
	protected void manageLimitDataView(final WebMarkupContainer dataViewContainer) {

		final List<ManageLimitBean> manageLimitList = fetchmanageLimitList();

		final DataView<ManageLimitBean> dataView = new ManageLimitDataView(WICKET_ID_PAGEABLE,
			new ListDataProvider<ManageLimitBean>(manageLimitList));
		dataView.setItemsPerPage(20);

		// Add the navigation
		navigator = new BtpnCustomPagingNavigator(WICKET_ID_LIMITNAVIGATOR, dataView) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return manageLimitList.size() != 0;
			}
		};
		navigator.setOutputMarkupId(true);
		navigator.setOutputMarkupPlaceholderTag(true);
		dataViewContainer.add(navigator);

		dataViewContainer.add(new Label("no.items", getLocalizer().getString("no.items", this)) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return manageLimitList.size() == 0;

			}
		}.setRenderBodyOnly(true));

		// Add the header
		IModel<String> headerDisplayModel = new AbstractReadOnlyModel<String>() {

			private static final long serialVersionUID = 1L;

			@Override
			public String getObject() {
				final String displayTotalItemsText = ManageLimitPage.this.getLocalizer().getString(
					"limit.totalitems.header", ManageLimitPage.this);
				return String.format(displayTotalItemsText, limitsTotalItemString, limitsStartIndex, limitsEndIndex);
			}

		};
		limitsHeader = new Label(WICKET_ID_LIMITTOTALITEMS, headerDisplayModel) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return manageLimitList.size() != 0;

			}
		};
		dataViewContainer.add(limitsHeader);
		limitsHeader.setOutputMarkupId(true);
		limitsHeader.setOutputMarkupPlaceholderTag(true);

		dataViewContainer.addOrReplace(dataView);
	}

	/**
	 * This is the ManageLimitDataView for Managing Limits.
	 * 
	 * @author Vikram Gunda
	 */
	private class ManageLimitDataView extends DataView<ManageLimitBean> {

		private static final long serialVersionUID = 1L;

		protected ManageLimitDataView(String id, IDataProvider<ManageLimitBean> dataProvider) {
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
		protected void populateItem(final Item<ManageLimitBean> item) {

			final ManageLimitBean entry = item.getModelObject();
			// Add the File name
			item.add(new Label("useCase", entry.getUseCaseId().getValue()));
			// Add the uploaded date
			item.add(new Label("productId", entry.getProductId() != null ? entry.getProductId().getId() : null));

			// Add the details Link
			final AjaxLink<ManageLimitBean> detailsLink = new AjaxLink<ManageLimitBean>(WICKET_ID_LINK, item.getModel()) {

				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					handleLimitDetailsClick(item.getModelObject(), target);
				}
			};
			item.add(detailsLink);
			final String cssStyle = item.getIndex() % 2 == 0 ? BtpnConstants.DATA_VIEW_EVEN_ROW_CSS : BtpnConstants.DATA_VIEW_ODD_ROW_CSS;
			item.add(new SimpleAttributeModifier("class", cssStyle));

		}

		@Override
		public boolean isVisible() {
			return internalGetDataProvider().size() != 0;

		}

		private void refreshTotalItemCount() {
			final int size = internalGetDataProvider().size();
			limitsTotalItemString = new Integer(size).toString();
			if (size > 0) {
				limitsStartIndex = getCurrentPage() * getItemsPerPage() + 1;
				limitsEndIndex = limitsStartIndex + getItemsPerPage() - 1;
				if (limitsEndIndex > size) {
					limitsEndIndex = size;
				}
			} else {
				limitsStartIndex = 0;
				limitsEndIndex = 0;
			}
		}
	}

	/**
	 * This method fetches the list of Manage Limit beans.
	 * 
	 * @return List<ManageLimitBean> returns the list of ManageLimitBean beans
	 */
	private List<ManageLimitBean> fetchmanageLimitList() {
		final List<ManageLimitBean> manageLimitList = new ArrayList<ManageLimitBean>();
		GetAllLimitsRequest request;
		try {
			request = this.getNewMobiliserRequest(GetAllLimitsRequest.class);
			final GetAllLimitsResponse response = this.limitClient.getAllLimits(request);
			if (evaluateBankPortalMobiliserResponse(response)) {
				return ConverterUtils.convertToManageLimitBeanList(response.getSummary());
			} else {
				error(getLocalizer().getString("limit.error", this));
			}
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			LOG.error("Exception occured while fetching All Limits ===> ", e);
		}
		return manageLimitList;
	}

	/**
	 * This method fetches the list of Manage Limit details.
	 * 
	 * @param ManageLimitBean returns the ManageLimitBean.
	 */
	private void handleLimitDetailsClick(final ManageLimitBean limitBean, final AjaxRequestTarget target) {
		GetLimitDetailsRequest request;
		try {
			request = this.getNewMobiliserRequest(GetLimitDetailsRequest.class);
			request.setLimitClassId(limitBean.getLimitClassId());
			request.setCustomerTypeId(limitBean.getProductId() != null ? Integer.valueOf(limitBean.getProductId()
				.getId()) : 0);
			final GetLimitDetailsResponse response = this.limitClient.getLimitDetails(request);
			if (evaluateBankPortalMobiliserResponse(response)) {
				final ManageLimitBean limitBeanDetails = ConverterUtils.convertToManageLimitBean(response
					.getLimitDetail());
				setResponsePage(new ManageLimitDetailsPage(limitBeanDetails));
			} else {
				error(getLocalizer().getString("limit.details.error", this));
				target.addComponent(feedBackPanel);
			}
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			LOG.error("Exception occured while fetching Limit Details  ===> ", e);
			target.addComponent(feedBackPanel);
		}
	}
}
