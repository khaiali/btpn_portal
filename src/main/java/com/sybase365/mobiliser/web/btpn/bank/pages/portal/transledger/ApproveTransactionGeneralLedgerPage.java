package com.sybase365.mobiliser.web.btpn.bank.pages.portal.transledger;

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

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.transactiongl.FindPendingApprovalTransactionGLRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.transactiongl.FindPendingApprovalTransactionGLResponse;
import com.sybase365.mobiliser.web.btpn.bank.beans.TransactionGeneralLedgerBean;
import com.sybase365.mobiliser.web.btpn.bank.common.dataproviders.TransactionGeneralLedgerDataProvider;
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
public class ApproveTransactionGeneralLedgerPage extends BtpnBaseBankPortalSelfCarePage {

	private static final String WICKET_ID_PAGEABLE = "pageable";

	private static final String WICKET_ID_LINK = "detailsLink";

	private static final String WICKET_ID_GLNAVIGATOR = "glNavigator";

	private static final String WICKET_ID_GLTOTALITEMS = "glHeader";

	private String glTotalItemString;

	private int glStartIndex = 0;

	private int glEndIndex = 0;

	private Label glHeader;

	private BtpnCustomPagingNavigator navigator;

	private static final Logger LOG = LoggerFactory.getLogger(ApproveTransactionGeneralLedgerPage.class);

	/**
	 * Constructor for this page.
	 */
	public ApproveTransactionGeneralLedgerPage() {
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
		Form<ApproveTransactionGeneralLedgerPage> form = new Form<ApproveTransactionGeneralLedgerPage>("glForm",
			new CompoundPropertyModel<ApproveTransactionGeneralLedgerPage>(this));
		// Add the Manage GL container
		WebMarkupContainer productsContainer = new WebMarkupContainer("glContainer");
		// Error messages panel
		productsContainer.add(new FeedbackPanel("errorMessages"));
		manageLimitDataView(productsContainer);
		productsContainer.setOutputMarkupId(true);
		productsContainer.setRenderBodyOnly(true);
		form.add(productsContainer);
		// Add add Button
		add(form);

	}

	/**
	 * This method adds the GL data view and also adds the sorting logic for data view.
	 * 
	 * @param dataViewContainer Container for General Ledger.
	 */
	protected void manageLimitDataView(final WebMarkupContainer dataViewContainer) {

		// Fetch GL Code list
		final List<TransactionGeneralLedgerBean> manageGlList = fetchTransactionGLCodeList();

		final TransactionGeneralLedgerDataProvider dataProvider = new TransactionGeneralLedgerDataProvider("glCode",
			manageGlList);

		final DataView<TransactionGeneralLedgerBean> dataView = new TransactionGeneralLedgerView(WICKET_ID_PAGEABLE,
			dataProvider);
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
				final String displayTotalItemsText = ApproveTransactionGeneralLedgerPage.this.getLocalizer().getString(
					"gl.totalitems.header", ApproveTransactionGeneralLedgerPage.this);
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

		dataViewContainer.add(new BtpnOrderByOrder("orderByCreatedBy", "createdBy", dataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByGlCode", "glCode", dataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByUseCaseName", "useCaseName", dataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByStatus", "status", dataProvider, dataView));

		dataViewContainer.addOrReplace(dataView);
	}

	/**
	 * This is the GeneralLedgerView for General Ledger.
	 * 
	 * @author Vikram Gunda
	 */
	private class TransactionGeneralLedgerView extends DataView<TransactionGeneralLedgerBean> {

		private static final long serialVersionUID = 1L;

		protected TransactionGeneralLedgerView(String id, IDataProvider<TransactionGeneralLedgerBean> dataProvider) {
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
		protected void populateItem(final Item<TransactionGeneralLedgerBean> item) {

			final TransactionGeneralLedgerBean entry = item.getModelObject();
			// Add the GL Code
			item.add(new Label("createdBy", entry.getCreatedBy()));
			// Add the GL Code
			item.add(new Label("glCode", entry.getNewGL().getId()));
			// Add the Use Case Name
			item.add(new Label("useCaseDescription", entry.getUseCaseName()));
			// Add the GL Description
			item.add(new Label("status", entry.getStatus()));
			// Add the details Link
			final AjaxLink<TransactionGeneralLedgerBean> detailsLink = new AjaxLink<TransactionGeneralLedgerBean>(
				WICKET_ID_LINK, item.getModel()) {

				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					setResponsePage(new ApproveTransactionGeneralLedgerConfirmPage(item.getModelObject()));
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
	private List<TransactionGeneralLedgerBean> fetchTransactionGLCodeList() {
		List<TransactionGeneralLedgerBean> listBean = new ArrayList<TransactionGeneralLedgerBean>();
		try {
			final FindPendingApprovalTransactionGLRequest request = this
				.getNewMobiliserRequest(FindPendingApprovalTransactionGLRequest.class);
			request.setCheckerId(this.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			final FindPendingApprovalTransactionGLResponse response = this.transactionGLEndPoint
				.findPendingApprovalTransactionGL(request);
			if (evaluateBankPortalMobiliserResponse(response)) {
				listBean = ConverterUtils.convertToTransactionGeneralLedgerApproveBeanList(response.getTransactiongl());
			} else {
				this.getWebSession().error(getLocalizer().getString("gl.details.error", this));
			}
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			LOG.error("Exception occured while fetching GL Code Details  ===> ", e);
		}
		return listBean;
	}
}
