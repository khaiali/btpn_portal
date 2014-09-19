package com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.billpayment.GetManualAdviceRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.billpayment.GetManualAdviceResponse;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.billpayment.PerformManualAdviceRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.billpayment.PerformManualAdviceResponse;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;
import com.sybase365.mobiliser.web.btpn.common.components.BtpnCustomPagingNavigator;
import com.sybase365.mobiliser.web.btpn.consumer.beans.ManualAdviceBean;
import com.sybase365.mobiliser.web.btpn.consumer.common.dataproviders.ManualAdvicesDataProvider;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnOrderByOrder;
import com.sybase365.mobiliser.web.btpn.util.ConverterUtils;

/**
 * This class is the Manual Advice Page for Consumer. This shows the list of manual advice.
 * 
 * @author Vikram Gunda
 */
public class ManualAdvicePage extends BtpnBaseConsumerPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	private List<ManualAdviceBean> manualAdvicesList;

	private static final String WICKET_ID_PAGEABLE = "pageable";

	private static final String WICKET_ID_LINK = "retryLink";

	private static final String WICKET_ID_LINK_NAME = "linkName";

	private static final String WICKET_ID_ATTACHMENTNAVIGATOR = "manualAdviceNavigator";

	private static final String WICKET_ID_ATTACHMENTTOTALITEMS = "manualAdviceHeader";

	private String attachmentTotalItemString;

	private int attachmentStartIndex = 0;

	private int attachmentEndIndex = 0;

	private static final Logger LOG = LoggerFactory.getLogger(ManualAdvicePage.class);
	
	/**
	 * Constructor for the Home Page.
	 */
	public ManualAdvicePage() {
		manualAdvicesList = fetchManualAdvices();
		initPageComponents();
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
	 * Initialize page components.
	 */
	protected void initPageComponents() {
		final Form<ManualAdvicePage> form = new Form<ManualAdvicePage>("manualAdviceForm",
			new CompoundPropertyModel<ManualAdvicePage>(this));
		// Add the Manual Advice container
		final WebMarkupContainer manualAdviceContainer = new WebMarkupContainer("advicesContainer");
		manualAdviceContainer.add(new FeedbackPanel("errorMessages"));
		notificationManualAdvicesDataView(manualAdviceContainer);
		form.add(manualAdviceContainer);
		add(form);
	}

	/**
	 * This method adds the AttachmentsDataView for the file upload and also adds the sorting logic for data view
	 */
	protected void notificationManualAdvicesDataView(final WebMarkupContainer dataViewContainer) {
		// Create the Attachment View
		final ManualAdvicesDataProvider manualAdvicesDataProvider = new ManualAdvicesDataProvider("fileName",
			manualAdvicesList);

		final DataView<ManualAdviceBean> dataView = new ManualAdvicesDataView(WICKET_ID_PAGEABLE,
			manualAdvicesDataProvider);
		dataView.setItemsPerPage(20);

		// Add the navigation
		BtpnCustomPagingNavigator navigator = new BtpnCustomPagingNavigator(WICKET_ID_ATTACHMENTNAVIGATOR, dataView) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return manualAdvicesList.size() != 0;

			}

		};
		dataViewContainer.add(navigator);

		// Add no items label
		dataViewContainer.add(new Label("no.items", getLocalizer().getString("label.no.items.found", this)) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return manualAdvicesList.size() == 0;

			}
		}.setRenderBodyOnly(true));

		// Add the header
		IModel<String> headerDisplayModel = new AbstractReadOnlyModel<String>() {

			private static final long serialVersionUID = 1L;

			@Override
			public String getObject() {
				final String displayTotalItemsText = ManualAdvicePage.this.getLocalizer().getString(
					"advice.totalitems.header", ManualAdvicePage.this);
				return String.format(displayTotalItemsText, attachmentTotalItemString, attachmentStartIndex,
					attachmentEndIndex);
			}

		};
		final Label manualAdviceHeader = new Label(WICKET_ID_ATTACHMENTTOTALITEMS, headerDisplayModel) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return manualAdvicesList.size() != 0;

			}
		};
		dataViewContainer.add(manualAdviceHeader);
		// Add the sort providers
		dataViewContainer.add(new BtpnOrderByOrder("orderByTransactionId", "transactionId", manualAdvicesDataProvider,
			dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByTransactionDate", "transactionDate",
			manualAdvicesDataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByAmount", "amount", manualAdvicesDataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByBillerId", "billerId", manualAdvicesDataProvider, dataView));
		dataViewContainer
			.add(new BtpnOrderByOrder("orderByFeeAmount", "feeAmount", manualAdvicesDataProvider, dataView));
		dataViewContainer.addOrReplace(dataView);
	}

	/**
	 * This is the AttachmentsDataView for registering Bank consumer.
	 * 
	 * @author Vikram Gunda
	 */
	private class ManualAdvicesDataView extends DataView<ManualAdviceBean> {

		private static final long serialVersionUID = 1L;

		protected ManualAdvicesDataView(String id, IDataProvider<ManualAdviceBean> dataProvider) {
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
		protected void populateItem(final Item<ManualAdviceBean> item) {
			final ManualAdviceBean entry = item.getModelObject();
			item.setModel(new CompoundPropertyModel<ManualAdviceBean>(entry));
			// Add the File name
			item.add(new Label("transactionId"));
			// Add the uploaded date
			final DateFormat dateFormat = new SimpleDateFormat("MM/dd HH:MM:ss");
			final String uploadedDate = dateFormat.format(entry.getTransactionDate());
			item.add(new Label("transactionDate", uploadedDate));
			item.add(new AmountLabel("amount"));
			item.add(new Label("billerId"));
			item.add(new AmountLabel("feeAmount"));
			// Add the delete Link
			final Link<ManualAdviceBean> retryLink = new Link<ManualAdviceBean>(WICKET_ID_LINK, item.getModel()) {

				private static final long serialVersionUID = 1L;

				@Override
				public void onClick() {
					final ManualAdviceBean manualAdvicesBean = item.getModelObject();
					handleRetryAdvice(manualAdvicesBean);
					setResponsePage(ManualAdvicePage.class);
				}
			};
			retryLink.add(new Label(WICKET_ID_LINK_NAME, "Retry"));
			item.add(retryLink);
			final String cssStyle = item.getIndex() % 2 == 0 ? BtpnConstants.DATA_VIEW_EVEN_ROW_CSS : BtpnConstants.DATA_VIEW_ODD_ROW_CSS;
			item.add(new SimpleAttributeModifier("class", cssStyle));

		}

		@Override
		public boolean isVisible() {
			return manualAdvicesList.size() != 0;

		}

		private void refreshTotalItemCount() {
			final int size = internalGetDataProvider().size();
			attachmentTotalItemString = new Integer(size).toString();
			if (size > 0) {
				attachmentStartIndex = getCurrentPage() * getItemsPerPage() + 1;
				attachmentEndIndex = attachmentStartIndex + getItemsPerPage() - 1;
				if (attachmentEndIndex > size) {
					attachmentEndIndex = size;
				}
			} else {
				attachmentStartIndex = 0;
				attachmentEndIndex = 0;
			}
		}
	}

	/**
	 * This method fetches the manual advices for this logged in customer.
	 * 
	 * @return List<ManualAdviceBean>
	 */
	private List<ManualAdviceBean> fetchManualAdvices() {		
		manualAdvicesList = new ArrayList<ManualAdviceBean>();
		try {
			final GetManualAdviceRequest request = this.getNewMobiliserRequest(GetManualAdviceRequest.class);
			request.setMsisdn(this.getMobiliserWebSession().getBtpnLoggedInCustomer().getUsername());
			final GetManualAdviceResponse response = this.getBillPaymentClient().getManualAdvice(request);
			if (evaluateConsumerPortalMobiliserResponse(response)) {
				manualAdvicesList = ConverterUtils.convertToManualAdviceList(response.getBeans());
			} else {
				error(getLocalizer().getString("error.manual.advice", this));
			}
		} catch (Exception e) {
			LOG.error("Exception occured while fetching manual advice List ==> ", e);
			error(getLocalizer().getString("error.exception", this));
		}
		return manualAdvicesList;
	}

	/**
	 * This method handles the retry advice bean
	 * 
	 * @param manualAdvicesBean
	 */
	private void handleRetryAdvice(final ManualAdviceBean manualAdvicesBean) {
		try {
			final PerformManualAdviceRequest request = this.getNewMobiliserRequest(PerformManualAdviceRequest.class);
			request.setMsisdn(this.getMobiliserWebSession().getBtpnLoggedInCustomer().getUsername());
			final PerformManualAdviceResponse response = this.getBillPaymentClient().performManualAdvice(request);
			if (evaluateConsumerPortalMobiliserResponse(response)) {
				this.getMobiliserWebSession().info(getLocalizer().getString("retry.advice.success", this));
			} else {
				error(getLocalizer().getString("error.retry.manual.advice", this));
			}			
		} catch (Exception e) {
			LOG.error("Exception occured while performing manual advice ==> ", e);
			error(getLocalizer().getString("error.exception", this));
		}
	}

}
