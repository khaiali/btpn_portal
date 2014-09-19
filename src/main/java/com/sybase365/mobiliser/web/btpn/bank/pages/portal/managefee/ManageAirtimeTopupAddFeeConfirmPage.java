package com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee;

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

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.fee.CreateFeeRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.fee.CreateFeeResponse;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageAirtimeTopupFeeBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageFeeDetailsBean;
import com.sybase365.mobiliser.web.btpn.bank.common.dataproviders.ManageFeeDetailsDataProvider;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;
import com.sybase365.mobiliser.web.btpn.common.components.BtpnCustomPagingNavigator;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.ConverterUtils;

/**
 * This is the Manage Fee Airtime Topup Confirm Page for bank portals.
 * 
 * @author Vikram Gunda
 */
public class ManageAirtimeTopupAddFeeConfirmPage extends BtpnBaseBankPortalSelfCarePage {

	private static final Logger LOG = LoggerFactory.getLogger(ManageAirtimeTopupAddFeeConfirmPage.class);

	private ManageAirtimeTopupFeeBean feeBean;

	private static final String WICKET_ID_PAGEABLE = "pageable";

	private static final String WICKET_ID_FEESNAVIGATOR = "feeNavigator";

	private static final String WICKET_ID_FEESTOTALITEMS = "feeHeader";

	private String feesTotalItemString;

	private int feeStartIndex = 0;

	private int feeEndIndex = 0;

	private Label feeHeader;

	private BtpnCustomPagingNavigator navigator;

	private WebMarkupContainer airtimeFeeContainer;

	private DataView<ManageFeeDetailsBean> dataView;

	/**
	 * Constructor for this page.
	 */
	public ManageAirtimeTopupAddFeeConfirmPage(final ManageAirtimeTopupFeeBean feeBean) {
		super();
		this.feeBean = feeBean;
		initThisPageComponents();
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	@Override
	protected void initOwnPageComponents() {
		super.initOwnPageComponents();
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 * 
	 * @param feeBean feeBean for Manage beans
	 * @param feeDetailsBean feeDetailsBean for Manage beans
	 */
	private void initThisPageComponents() {
		final Form<ManageAirtimeTopupAddFeeConfirmPage> form = new Form<ManageAirtimeTopupAddFeeConfirmPage>("feeForm",
			new CompoundPropertyModel<ManageAirtimeTopupAddFeeConfirmPage>(this));
		form.add(new Label("feeBean.telco.id"), new Label("feeBean.productName.id"), new Label(
			"feeBean.denomination.value"), new AmountLabel("feeBean.transactionAmount"), new FeedbackPanel(
			"errorMessages"));
		airtimeFeeContainer = new WebMarkupContainer("airtimeFeeContainer");
		form.add(airtimeFeeContainer);
		notificationManageFeesDataView(airtimeFeeContainer);
		form.add(addCreateButton());
		form.add(addCancelButton());
		add(form);
	}

	/**
	 * This method adds the Create button for creating the fee.
	 */
	private Button addCreateButton() {
		final Button createButton = new Button("btnCreate") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				handleCreateAirtimeFee();
			}

		};
		createButton.setDefaultFormProcessing(false);
		return createButton;
	}

	/**
	 * This method adds the Create button for creating the fee.
	 */
	private Button addCancelButton() {
		final Button cancelButton = new Button("btnCancel") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(ManageAirtimeTopupFeePage.class);
			}
		};
		cancelButton.setDefaultFormProcessing(false);
		return cancelButton;
	}

	/**
	 * This method adds the AttachmentsDataView for the file upload and also adds the sorting logic for data view.
	 * 
	 * @param dataViewContainer dataViewContainer for the fee.
	 * @param isSlabFee isSlabFee for the container.
	 */
	protected void notificationManageFeesDataView(final WebMarkupContainer dataViewContainer) {
		final ManageFeeDetailsDataProvider dataProvider = new ManageFeeDetailsDataProvider("feeName",
			feeBean.getManageDetailsList());
		dataView = new ManageFeeDetailsView(WICKET_ID_PAGEABLE, dataProvider);
		dataView.setItemsPerPage(20);

		// Add the navigation
		navigator = new BtpnCustomPagingNavigator(WICKET_ID_FEESNAVIGATOR, dataView) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return feeBean.getManageDetailsList().size() != 0;

			}

		};
		dataViewContainer.add(navigator);
		dataViewContainer.add(new Label("no.items", getLocalizer().getString("no.items", this)) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return feeBean.getManageDetailsList().size() == 0;

			}
		}.setRenderBodyOnly(true));

		final String displayTotalItemsText = this.getLocalizer().getString("fees.totalitems.header", this);

		// Add the header
		IModel<String> headerDisplayModel = new AbstractReadOnlyModel<String>() {

			private static final long serialVersionUID = 1L;

			@Override
			public String getObject() {
				return String.format(displayTotalItemsText, feesTotalItemString, feeStartIndex, feeEndIndex);
			}

		};

		// Add the fee header
		feeHeader = new Label(WICKET_ID_FEESTOTALITEMS, headerDisplayModel) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return feeBean.getManageDetailsList().size() != 0;

			}
		};
		dataViewContainer.add(feeHeader);
		dataViewContainer.addOrReplace(dataView);
	}

	/**
	 * This is the ManageFeeDetailsView for Manage Fees of Sharing and Slab Fee
	 * 
	 * @author Vikram Gunda
	 */
	private class ManageFeeDetailsView extends DataView<ManageFeeDetailsBean> {

		private static final long serialVersionUID = 1L;

		protected ManageFeeDetailsView(String id, IDataProvider<ManageFeeDetailsBean> dataProvider) {
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
		protected void populateItem(final Item<ManageFeeDetailsBean> item) {
			final ManageFeeDetailsBean entry = (ManageFeeDetailsBean) item.getModelObject();
			item.setModel(new CompoundPropertyModel<ManageFeeDetailsBean>(entry));
			// Add the File name
			item.add(new Label("feeName"));
			item.add(new Label("glCode"));
			item.add(new AmountLabel("fixedFee"));
			item.add(new Label("percentageFee", entry.getPercentageFee().toString() + " %"));
			final String cssStyle = item.getIndex() % 2 == 0 ? BtpnConstants.DATA_VIEW_EVEN_ROW_CSS : BtpnConstants.DATA_VIEW_ODD_ROW_CSS;
			item.add(new SimpleAttributeModifier("class", cssStyle));
		}

		@Override
		public boolean isVisible() {
			return feeBean.getManageDetailsList().size() != 0;

		}

		private void refreshTotalItemCount() {
			final int size = internalGetDataProvider().size();
			feesTotalItemString = new Integer(size).toString();
			if (size > 0) {
				feeStartIndex = getCurrentPage() * getItemsPerPage() + 1;
				feeEndIndex = feeStartIndex + getItemsPerPage() - 1;
				if (feeEndIndex > size) {
					feeEndIndex = size;
				}
			} else {
				feeStartIndex = 0;
				feeEndIndex = 0;
			}
		}
	}

	/**
	 * This method handles the Creation of Fee Bean.
	 */
	private void handleCreateAirtimeFee() {
		try {
			final CreateFeeRequest request = this.getNewMobiliserRequest(CreateFeeRequest.class);
			request.setVendorFeeSharing(ConverterUtils.convertToAirtimeVendorFeeSharing(feeBean));
			request.setMakerId(this.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			final CreateFeeResponse response = this.feeClient.createFee(request);
			if (this.evaluateBankPortalMobiliserResponse(response)) {
				this.getWebSession().info(getLocalizer().getString("create.success", this));
				setResponsePage(ManageAirtimeTopupFeePage.class);
			} else {
				handleSpecificErrorMessage(response.getStatus().getCode());
			}
		} catch (Exception e) {
			this.getWebSession().error(getLocalizer().getString("error.exception", this));
			LOG.error("Exception occured while creating airtime topup fee ===> ", e);
		}
		setResponsePage(ManageAirtimeTopupFeePage.class);
	}

	/**
	 * This method handles the specific error message.
	 */
	private void handleSpecificErrorMessage(final int errorCode) {
		// Specific error message handling
		final String messageKey = "error." + errorCode;
		String message = getLocalizer().getString(messageKey, this);
		// Generice error messages
		if (messageKey.equals(message)) {
			message = getLocalizer().getString("error.create.fees", this);
		}
		this.getWebSession().error(message);
	}

}
