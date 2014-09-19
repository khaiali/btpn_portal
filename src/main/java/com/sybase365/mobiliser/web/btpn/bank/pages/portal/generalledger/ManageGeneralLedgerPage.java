package com.sybase365.mobiliser.web.btpn.bank.pages.portal.generalledger;

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
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.generalledger.GetAllGeneralLedgerRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.generalledger.GetAllGeneralLedgerResponse;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.generalledger.GetGeneralLedgerDetailRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.generalledger.GetGeneralLedgerDetailResponse;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageGeneralLedgerBean;
import com.sybase365.mobiliser.web.btpn.bank.common.dataproviders.ManageGeneralLedgerDataProvider;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.components.BtpnCustomPagingNavigator;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnOrderByOrder;
import com.sybase365.mobiliser.web.btpn.util.ConverterUtils;

/**
 * This is the Manage General Ledger Page for Bank Portals. It shows the list of GL codes and descriptions.
 * 
 * @author Vikram Gunda
 */
public class ManageGeneralLedgerPage extends BtpnBaseBankPortalSelfCarePage {

	private static final String WICKET_ID_PAGEABLE = "pageable";

	private static final String WICKET_ID_LINK = "detailsLink";

	private static final String WICKET_ID_GLNAVIGATOR = "glNavigator";

	private static final String WICKET_ID_GLTOTALITEMS = "glHeader";

	private String glTotalItemString;

	private int glStartIndex = 0;

	private int glEndIndex = 0;

	private Label glHeader;

	private BtpnCustomPagingNavigator navigator;

	private FeedbackPanel feedbackPanel;

	private static final Logger LOG = LoggerFactory.getLogger(ManageGeneralLedgerPage.class);

	/**
	 * Constructor for this page.
	 */
	public ManageGeneralLedgerPage() {
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
		Form<ManageGeneralLedgerPage> form = new Form<ManageGeneralLedgerPage>("glForm",
			new CompoundPropertyModel<ManageGeneralLedgerPage>(this));
		// Add the Manage GL container
		WebMarkupContainer productsContainer = new WebMarkupContainer("glContainer");
		// Error messages panel
		productsContainer.add(feedbackPanel = new FeedbackPanel("errorMessages"));
		feedbackPanel.setOutputMarkupId(true);
		manageLimitDataView(productsContainer);
		productsContainer.setOutputMarkupId(true);
		productsContainer.setRenderBodyOnly(true);
		productsContainer.add(addAddButton());
		form.add(productsContainer);
		// Add add Button
		add(form);

	}

	/**
	 * This method adds the Add button for the Manage General Ledger Pages.
	 */
	protected Button addAddButton() {
		Button submitButton = new Button("btnAdd") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				// Send the General Ledger Page
				setResponsePage(ManageGeneralLedgerAddPage.class);
			}
		};
		return submitButton;
	}

	/**
	 * This method adds the GL data view and also adds the sorting logic for data view.
	 * 
	 * @param dataViewContainer Container for General Ledger.
	 */
	protected void manageLimitDataView(final WebMarkupContainer dataViewContainer) {

		// Fetch GL Code list
		final List<ManageGeneralLedgerBean> manageGlList = fetchGLCodeList();

		final ManageGeneralLedgerDataProvider dataProvider = new ManageGeneralLedgerDataProvider("glCode", manageGlList);

		final DataView<ManageGeneralLedgerBean> dataView = new GeneralLedgerView(WICKET_ID_PAGEABLE, dataProvider);
		dataView.setItemsPerPage(20);

		// Add the navigation
		navigator = new BtpnCustomPagingNavigator(WICKET_ID_GLNAVIGATOR, dataView) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return manageGlList.size() != 0;
			}
		};
		navigator.setOutputMarkupId(true);
		navigator.setOutputMarkupPlaceholderTag(true);
		dataViewContainer.add(navigator);

		// Add the no items label
		dataViewContainer.add(new Label("no.items", getLocalizer().getString("no.items", this)) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return manageGlList.size() == 0;

			}
		}.setRenderBodyOnly(true));

		// Add the header
		final IModel<String> headerDisplayModel = new AbstractReadOnlyModel<String>() {

			private static final long serialVersionUID = 1L;

			@Override
			public String getObject() {
				final String displayTotalItemsText = ManageGeneralLedgerPage.this.getLocalizer().getString(
					"gl.totalitems.header", ManageGeneralLedgerPage.this);
				return String.format(displayTotalItemsText, glTotalItemString, glStartIndex, glEndIndex);
			}

		};
		glHeader = new Label(WICKET_ID_GLTOTALITEMS, headerDisplayModel) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return manageGlList.size() != 0;

			}
		};
		dataViewContainer.add(glHeader);
		glHeader.setOutputMarkupId(true);
		glHeader.setOutputMarkupPlaceholderTag(true);

		// Add the sort providers
		dataViewContainer.add(new BtpnOrderByOrder("orderByGLCode", "glCode", dataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByGLDesc", "glDescription", dataProvider, dataView));

		dataViewContainer.addOrReplace(dataView);
	}

	/**
	 * This is the GeneralLedgerView for General Ledger.
	 * 
	 * @author Vikram Gunda
	 */
	private class GeneralLedgerView extends DataView<ManageGeneralLedgerBean> {

		private static final long serialVersionUID = 1L;

		protected GeneralLedgerView(String id, IDataProvider<ManageGeneralLedgerBean> dataProvider) {
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
		protected void populateItem(final Item<ManageGeneralLedgerBean> item) {

			final ManageGeneralLedgerBean entry = item.getModelObject();
			// Add the GL Code
			item.add(new Label("glCode", entry.getGlCode()));
			// Add the GL Description
			item.add(new Label("glDescription", entry.getGlDescription()));
			// Add the details Link
			final AjaxLink<ManageGeneralLedgerBean> detailsLink = new AjaxLink<ManageGeneralLedgerBean>(WICKET_ID_LINK,
				item.getModel()) {

				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					handleGLDetailsClick((ManageGeneralLedgerBean) item.getModelObject());
					target.addComponent(feedbackPanel);
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
			glTotalItemString = new Integer(size).toString();
			if (size > 0) {
				glStartIndex = getCurrentPage() * getItemsPerPage() + 1;
				glEndIndex = glStartIndex + getItemsPerPage() - 1;
				if (glEndIndex > size) {
					glEndIndex = size;
				}
			} else {
				glStartIndex = 0;
				glEndIndex = 0;
			}
		}
	}

	/**
	 * This method fetches the list of Manage General Ledger beans.
	 * 
	 * @return List<ManageGeneralLedgerBean> returns the list of ManageGeneralLedgerBean beans
	 */
	private List<ManageGeneralLedgerBean> fetchGLCodeList() {
		final List<ManageGeneralLedgerBean> generalLedgerList = new ArrayList<ManageGeneralLedgerBean>();
		GetAllGeneralLedgerRequest request;
		try {
			request = this.getNewMobiliserRequest(GetAllGeneralLedgerRequest.class);
			final GetAllGeneralLedgerResponse response = this.generalLedgerClient.getAllGeneralLedger(request);
			if (evaluateBankPortalMobiliserResponse(response)) {
				return ConverterUtils.convertToManageGeneralLedgerBean(response.getGeneralledger());
			} else {
				error(getLocalizer().getString("gl.error", this));
			}
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			LOG.error("Exception occured while fetching GL Code list ===> ", e);
		}
		return generalLedgerList;
	}

	/**
	 * This method fetches the list of Manage General Ledger details.
	 * 
	 * @return ManageGeneralLedgerBean returns the list of ManageGeneralLedgerBean beans
	 */
	private void handleGLDetailsClick(final ManageGeneralLedgerBean ledgerBean) {
		GetGeneralLedgerDetailRequest request;
		try {
			request = this.getNewMobiliserRequest(GetGeneralLedgerDetailRequest.class);
			request.setIdGlCode(Long.valueOf(ledgerBean.getGlCode()));
			final GetGeneralLedgerDetailResponse response = this.generalLedgerClient.getGeneralLedgerDetail(request);
			if (evaluateBankPortalMobiliserResponse(response)) {
				final ManageGeneralLedgerBean ledgerBeanDetails = ConverterUtils
					.convertToManageGeneralLedgerDetailsBean(response.getGeneralledger(), ledgerBean,
						this.getLookupMapUtility(), this);
				setResponsePage(new ManageGeneralLedgerEditPage(ledgerBeanDetails));
			} else {
				error(getLocalizer().getString("gl.details.error", this));
			}
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			LOG.error("Exception occured while fetching GL Code Details  ===> ", e);
		}
	}
}
