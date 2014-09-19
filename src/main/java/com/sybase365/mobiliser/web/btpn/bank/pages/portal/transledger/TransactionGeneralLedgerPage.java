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

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.transactiongl.GetAllTransactionGLRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.transactiongl.GetAllTransactionGLResponse;
import com.sybase365.mobiliser.web.btpn.bank.beans.TransactionGeneralLedgerBean;
import com.sybase365.mobiliser.web.btpn.bank.common.dataproviders.TransactionGeneralLedgerDataProvider;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.generalledger.ManageGeneralLedgerPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.components.BtpnCustomPagingNavigator;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnOrderByOrder;
import com.sybase365.mobiliser.web.btpn.util.ConverterUtils;

/**
 * This is the Transaction General Ledger Page for Bank Portals. It shows the list of Transaction GL codes and descriptions.
 * 
 * @author Vikram Gunda
 */
public class TransactionGeneralLedgerPage extends BtpnBaseBankPortalSelfCarePage {

	private static final String WICKET_ID_PAGEABLE = "pageable";

	private static final String WICKET_ID_LINK = "detailsLink";

	private static final String WICKET_ID_GLNAVIGATOR = "glNavigator";

	private static final String WICKET_ID_GLTOTALITEMS = "glHeader";

	private String glTotalItemString;

	private int glStartIndex = 0;

	private int glEndIndex = 0;

	private Label glHeader;

	private BtpnCustomPagingNavigator navigator;

	private static final Logger LOG = LoggerFactory.getLogger(ManageGeneralLedgerPage.class);

	/**
	 * Constructor for this page.
	 */
	public TransactionGeneralLedgerPage() {
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
		Form<TransactionGeneralLedgerPage> form = new Form<TransactionGeneralLedgerPage>("glForm",
			new CompoundPropertyModel<TransactionGeneralLedgerPage>(this));
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
				final String displayTotalItemsText = TransactionGeneralLedgerPage.this.getLocalizer().getString(
					"gl.totalitems.header", TransactionGeneralLedgerPage.this);
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

		dataViewContainer.add(new BtpnOrderByOrder("orderByGlCode", "glCode", dataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByGlDesc", "glDescription", dataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByUseCaseName", "useCaseName", dataProvider, dataView));

		dataViewContainer.addOrReplace(dataView);
	}

	/**
	 * This is the Transaction GeneralLedgerView for Transaction General Ledger.
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
			item.add(new Label("glCode", entry.getCurrentGL().getId()));
			// Add the GL Description
			item.add(new Label("glDescription", entry.getCurrentGL().getValue()));
			// Add the Use Case Name
			item.add(new Label("useCaseDescription", entry.getUseCaseName()));
			// Add the details Link
			final AjaxLink<TransactionGeneralLedgerBean> detailsLink = new AjaxLink<TransactionGeneralLedgerBean>(
				WICKET_ID_LINK, item.getModel()) {

				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					setResponsePage(new TransactionGeneralLedgerEditPage(item.getModelObject()));
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
	 * This method fetches the list of Transaction General Ledger beans.
	 * 
	 * @return List<TransactionGeneralLedgerBean> returns the list of TransactionGeneralLedgerBean beans
	 */
	private List<TransactionGeneralLedgerBean> fetchTransactionGLCodeList() {
		List<TransactionGeneralLedgerBean> detailList = new ArrayList<TransactionGeneralLedgerBean>();
		try {
			// Transaction GL Request
			final GetAllTransactionGLRequest request = this.getNewMobiliserRequest(GetAllTransactionGLRequest.class);
			final GetAllTransactionGLResponse response = this.transactionGLEndPoint.getAllTransactionGL(request);
			if (evaluateBankPortalMobiliserResponse(response)) {
				return ConverterUtils.convertToTransactionGeneralLedgerBeanList(response.getTransactiongl());
			} else {
				error(getLocalizer().getString("error.generalledger", this));
			}
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			LOG.error("Exception occured while fetching Transaction GL Code List  ===> ", e);
		}
		return detailList;
	}
}
