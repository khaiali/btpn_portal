package com.sybase365.mobiliser.web.btpn.consumer.common.panels;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.core.schedtxn.facade.api.SchedTxnFacade;
import com.btpnwow.core.schedtxn.facade.contract.FindSchedTxnRequest;
import com.btpnwow.core.schedtxn.facade.contract.FindSchedTxnResponse;
import com.btpnwow.core.schedtxn.facade.contract.SchedTxnFindViewType;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.common.components.BtpnCustomPagingNavigator;
import com.sybase365.mobiliser.web.btpn.consumer.beans.StandingInstructionsBean;
import com.sybase365.mobiliser.web.btpn.consumer.common.dataproviders.StandingInstructionsDataProvider;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.standing.AddStandingInstructionsPage;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.standing.RemoveStandingInstructionPage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnOrderByOrder;
import com.sybase365.mobiliser.web.util.PortalUtils;


public class StandingInstructionsPanel extends Panel {

	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(StandingInstructionsPanel.class);

	protected BtpnMobiliserBasePage basePage;

	private static final String WICKET_ID_PAGEABLE = "pageable";
	private static final String WICKET_ID_LINK = "removeLink";
	private static final String WICKET_ID_LINK_NAME = "linkName";
	private static final String WICKET_ID_instructionsNavigator = "instructionsNavigator";
	private static final String WICKET_ID_InstructionsTotalItems = "instructionsHeader";

	private String instructionsTotalItemString;
	private int instructionsStartIndex = 0;
	private int instructionsEndIndex = 0;

	private Label instructionsHeader;

	private BtpnCustomPagingNavigator navigator;
	StandingInstructionsBean instructionsBean;
	private Label noRecordsLabel;
	
	@SpringBean(name = "siClient")
	private SchedTxnFacade siClient;

	public StandingInstructionsPanel(String id, BtpnMobiliserBasePage basePage) {
		super(id);
		this.basePage = basePage;
		constructPanel();
	}

	protected void constructPanel() {

		final Form<StandingInstructionsPanel> form = new Form<StandingInstructionsPanel>("standingInatructionsForm",
			new CompoundPropertyModel<StandingInstructionsPanel>(this));

		// Add feedback panel for Error Messages
		form.add(new FeedbackPanel("errorMessages"));

		String message = getLocalizer().getString("label.noAccountsFound", StandingInstructionsPanel.this);
		noRecordsLabel = new Label("emptyRecordsMessage", message);
		noRecordsLabel.setOutputMarkupId(true);
		noRecordsLabel.setVisible(false);
		form.add(noRecordsLabel);

		// Add the Standing Instructions Container
		WebMarkupContainer instrictionsContainer = new WebMarkupContainer("instrictionsContainer");
		standingInstructionsDataView(instrictionsContainer);
		instrictionsContainer.setOutputMarkupId(true);
		form.add(instrictionsContainer);

		form.add(new Button("addButton") {
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit() {
				setResponsePage(new AddStandingInstructionsPage());
			};
		});
		add(form);
	}

	/**
	 * This method adds the standingInstructionsDataView
	 */
	protected void standingInstructionsDataView(final WebMarkupContainer dataViewContainer) {

		List<StandingInstructionsBean> instructionssList = getAllStandingInstructionsList();
		
		if (instructionssList.isEmpty()) {
			noRecordsLabel.setVisible(true);
		}
		
		final StandingInstructionsDataProvider standingDataProvider = new StandingInstructionsDataProvider("name",
			instructionssList);
		final DataView<StandingInstructionsBean> dataView = new StandingInstructionsDataView(WICKET_ID_PAGEABLE,
			standingDataProvider);
		dataView.setItemsPerPage(20);
		
		// Add the navigation
		navigator = new BtpnCustomPagingNavigator(WICKET_ID_instructionsNavigator, dataView) {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean isVisible() {
				return standingDataProvider.size() != 0;
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
				final String displayTotalItemsText = StandingInstructionsPanel.this.getLocalizer().getString(
					"instructions.totalitems.header", StandingInstructionsPanel.this);
				return String.format(displayTotalItemsText, instructionsTotalItemString, instructionsStartIndex,
					instructionsEndIndex);
			}
		};
		
		instructionsHeader = new Label(WICKET_ID_InstructionsTotalItems, headerDisplayModel) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return standingDataProvider.size() != 0;

			}
		};
		
		dataViewContainer.add(instructionsHeader);
		instructionsHeader.setOutputMarkupId(true);
		instructionsHeader.setOutputMarkupPlaceholderTag(true);
		
		// Add the sort providers
		dataViewContainer.add(new BtpnOrderByOrder("orderByName", "name", standingDataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByPayer", "payer", standingDataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByPayee", "payee", standingDataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByType", "type", standingDataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByStartDate", "startDate", standingDataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByExpiryDate", "expiryDate", standingDataProvider, dataView));
		
		dataViewContainer.addOrReplace(dataView);
	}

	/**
	 * This is the StandingInstructionsDataView for consumer Portal.
	 * 
	 * @author Narasa Reddy
	 */
	private class StandingInstructionsDataView extends DataView<StandingInstructionsBean> {
		private static final long serialVersionUID = 1L;

		protected StandingInstructionsDataView(String id, IDataProvider<StandingInstructionsBean> dataProvider) {
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
		protected void populateItem(final Item<StandingInstructionsBean> item) {

			final StandingInstructionsBean entry = item.getModelObject();
			item.setModel(new CompoundPropertyModel<StandingInstructionsBean>(entry));
			item.add(new Label("name"));
			item.add(new Label("payer"));
			item.add(new Label("payee"));
			item.add(new Label("type"));
			item.add(new Label("startDate"));
			item.add(new Label("expiryDate"));
			

			// Add the remove Link
			AjaxLink<StandingInstructionsBean> removeLink = new AjaxLink<StandingInstructionsBean>(WICKET_ID_LINK,
				item.getModel()) {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					setResponsePage(new RemoveStandingInstructionPage(item.getModelObject()));
				}
			};
			
			removeLink.add(new Label(WICKET_ID_LINK_NAME, "Remove"));
			item.add(removeLink);
			String cssStyle = item.getIndex() % 2 == 0 ? BtpnConstants.DATA_VIEW_EVEN_ROW_CSS : BtpnConstants.DATA_VIEW_ODD_ROW_CSS;
			item.add(new SimpleAttributeModifier("class", cssStyle));
		}

		@Override
		public boolean isVisible() {
			return ((StandingInstructionsDataProvider) internalGetDataProvider()).size() != 0;
		}

		private void refreshTotalItemCount() {
			final int size = internalGetDataProvider().size();
			instructionsTotalItemString = new Integer(size).toString();
			if (size > 0) {
				instructionsStartIndex = getCurrentPage() * getItemsPerPage() + 1;
				instructionsEndIndex = instructionsStartIndex + getItemsPerPage() - 1;
				if (instructionsEndIndex > size)
					instructionsEndIndex = size;
			} else {
				instructionsStartIndex = 0;
				instructionsEndIndex = 0;
			}
		}
	}

	
	
	public List<StandingInstructionsBean> getAllStandingInstructionsList() {
		
		List<StandingInstructionsBean> beanList = new ArrayList<StandingInstructionsBean>();
		
		try {
			
			final FindSchedTxnRequest request = basePage
				.getNewMobiliserRequest(FindSchedTxnRequest.class);
			
			long customerId = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId();
			request.setCustomerId(customerId);
			request.setStart(0);
			request.setLength(Integer.MAX_VALUE);
			
			FindSchedTxnResponse response = siClient.find(request);
			if (basePage.evaluateConsumerPortalMobiliserResponse(response)) {
				return convertToStandingInstructionsBean(response.getItem());
			}
			
		} catch (Exception ex) {
			log.error("#An error occurred while calling getAllStandingInstruction service.", ex);
			error(getLocalizer().getString("error.exception", this));
		}
		
		return beanList;
	}
	
	
	
	public List<StandingInstructionsBean> convertToStandingInstructionsBean(
			List<SchedTxnFindViewType> result) {
		
		final List<StandingInstructionsBean> beanList = new ArrayList<StandingInstructionsBean>();
		for (SchedTxnFindViewType siBean : result) {
			
			final StandingInstructionsBean bean = new StandingInstructionsBean();
			bean.setId(String.valueOf(siBean.getId()));
			bean.setName(siBean.getDescription());
			bean.setPayer("");
			bean.setPayee(siBean.getBeneficiaryNo());
			String processingCode = siBean.getProcessingCode();
//			String types = MobiliserUtils.getValue("processingCode", siBean.getProcessingCode(), basePage.getLookupMapUtility(), basePage);
			String type = getLocalizer().getString("lookup.processingCode."+processingCode+"", this);
			bean.setType(type);
			bean.setStartDate(PortalUtils.getFormattedDate(siBean.getStartDate(), Locale.getDefault()));
			bean.setExpiryDate(PortalUtils.getFormattedDate(siBean.getEndDate(), Locale.getDefault()));
			bean.setFrequency(siBean.getFrequency());
			bean.setAmount(String.valueOf(siBean.getAmount()));
			
			beanList.add(bean);
		}
		
		return beanList;
	}

}
