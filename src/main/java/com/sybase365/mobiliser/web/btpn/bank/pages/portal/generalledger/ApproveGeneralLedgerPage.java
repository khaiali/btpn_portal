package com.sybase365.mobiliser.web.btpn.bank.pages.portal.generalledger;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
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

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.generalledger.FindPendingApprovalGeneralLedgerRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.generalledger.FindPendingApprovalGeneralLedgerResponse;
import com.sybase365.mobiliser.web.btpn.bank.beans.ApproveGeneralLedgerBean;
import com.sybase365.mobiliser.web.btpn.bank.common.dataproviders.ApproveGeneralLedgerDataProvider;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.components.BtpnCustomPagingNavigator;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnOrderByOrder;
import com.sybase365.mobiliser.web.btpn.util.ConverterUtils;

/**
 * This pages shows all the General Ledger list that needs to be approved or rejected.
 * 
 * @author Vikram Gunda
 */
public class ApproveGeneralLedgerPage extends BtpnBaseBankPortalSelfCarePage {

	private static final String WICKET_ID_PAGEABLE = "pageable";

	private static final String WICKET_ID_LINK = "detailsLink";

	private static final String WICKET_ID_GLNAVIGATOR = "glNavigator";

	private static final String WICKET_ID_GLTOTALITEMS = "glHeader";

	private String glTotalItemString;

	private int glStartIndex = 0;

	private int glEndIndex = 0;

	private Label glHeader;

	private BtpnCustomPagingNavigator navigator;

	private static final Logger LOG = LoggerFactory.getLogger(ApproveGeneralLedgerPage.class);

	/**
	 * Constructor for this page.
	 */
	public ApproveGeneralLedgerPage() {
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
		Form<ApproveGeneralLedgerPage> form = new Form<ApproveGeneralLedgerPage>("glForm",
			new CompoundPropertyModel<ApproveGeneralLedgerPage>(this));
		// Add the Manage Products container
		WebMarkupContainer productsContainer = new WebMarkupContainer("glContainer");
		productsContainer.add(new FeedbackPanel("errorMessages"));
		manageGLDataView(productsContainer);
		productsContainer.setOutputMarkupId(true);
		productsContainer.setRenderBodyOnly(true);
		form.add(productsContainer);
		// Add add Button
		add(form);
	}

	/**
	 * This method adds the AttachmentsDataView for the file upload and also adds the sorting logic for data view
	 */
	protected void manageGLDataView(final WebMarkupContainer dataViewContainer) {

		final List<ApproveGeneralLedgerBean> manageGlList = fetchGLCodeApproveList();

		final ApproveGeneralLedgerDataProvider dataProvider = new ApproveGeneralLedgerDataProvider("createdBy",
			manageGlList);

		final DataView<ApproveGeneralLedgerBean> dataView = new GeneralLedgerView(WICKET_ID_PAGEABLE, dataProvider);
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
				final String displayTotalItemsText = ApproveGeneralLedgerPage.this.getLocalizer().getString(
					"gl.totalitems.header", ApproveGeneralLedgerPage.this);
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
		dataViewContainer.add(new BtpnOrderByOrder("orderByStatus", "status", dataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByCreatedBy", "createdBy", dataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByglCode", "glCode", dataProvider, dataView));

		dataViewContainer.addOrReplace(dataView);
	}

	/**
	 * This is the ManageLimitDataView for Managing Limits.
	 * 
	 * @author Vikram Gunda
	 */
	private class GeneralLedgerView extends DataView<ApproveGeneralLedgerBean> {

		private static final long serialVersionUID = 1L;

		protected GeneralLedgerView(String id, IDataProvider<ApproveGeneralLedgerBean> dataProvider) {
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
		protected void populateItem(final Item<ApproveGeneralLedgerBean> item) {

			final ApproveGeneralLedgerBean entry = item.getModelObject();
			// Add the Created By
			item.add(new Label("createdBy", entry.getCreatedBy()));
			// Add the GL Code
			item.add(new Label("glCode", entry.getNewGlCode()));
			// Add the Status
			item.add(new Label("status", entry.getStatus()));

			// Add the details Link
			final AjaxLink<ApproveGeneralLedgerBean> detailsLink = new AjaxLink<ApproveGeneralLedgerBean>(
				WICKET_ID_LINK, item.getModel()) {

				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					setResponsePage(new ApproveGeneralLedgerDetailsPage(item.getModelObject()));
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
	 * This method fetches the list of Manage General Ledger beans for approval.
	 * 
	 * @return List<ApproveGeneralLedgerBean> returns the list of ApproveGeneralLedgerBean beans
	 */
	private List<ApproveGeneralLedgerBean> fetchGLCodeApproveList() {
		List<ApproveGeneralLedgerBean> generalLedgerList = new ArrayList<ApproveGeneralLedgerBean>();
		FindPendingApprovalGeneralLedgerRequest request;
		try {
			request = this.getNewMobiliserRequest(FindPendingApprovalGeneralLedgerRequest.class);
			final FindPendingApprovalGeneralLedgerResponse response = this.generalLedgerClient
				.findPendingApprovalGeneralLedger(request);
			if (evaluateBankPortalMobiliserResponse(response)) {
				generalLedgerList = ConverterUtils.convertToApproveGeneralLedgerBeanList(response.getGeneralLedger(),
					this.lookupMapUtility, this);
			} else {
				error(getLocalizer().getString("gl.error", this));
			}
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			LOG.error("Exception occured while fetching GL Code list ===> ", e);
		}
		return generalLedgerList;
	}
}
