package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.SalaryDataErrorBean;
import com.sybase365.mobiliser.web.btpn.bank.common.dataproviders.SalaryDataErrorProvider;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.regupload.SearchRegDataPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.salaryupload.SearchSalaryDataPage;
import com.sybase365.mobiliser.web.btpn.common.components.BtpnCustomPagingNavigator;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnOrderByOrder;

public class SalaryDataErrorPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(SalaryDataErrorPanel.class);

	protected BtpnMobiliserBasePage basePage;

	private static final String WICKET_ID_PAGEABLE = "pageable";

	private static final String WICKET_ID_approvalNavigator = "approvalNavigator";

	private static final String WICKET_ID_approvalTotalItems = "approvalHeader";

	private String approvalTotalItemString;

	private int approvalStartIndex = 0;

	private int approvalEndIndex = 0;

	private Label approvalHeader;

	private BtpnCustomPagingNavigator navigator;

	private String fileName;

	private int fileTypeId;

	private List<SalaryDataErrorBean> salaryErrorData;

	/**
	 * Constructor of this page.
	 * 
	 * @param id id of the panel
	 * @param basePage basePage of the mobiliser
	 */
	public SalaryDataErrorPanel(final String id, final BtpnMobiliserBasePage basePage, final String fileName,
		final List<SalaryDataErrorBean> salaryErrorData, int fileTypeId) {
		super(id);
		this.basePage = basePage;
		this.fileName = fileName;
		this.salaryErrorData = salaryErrorData;
		this.fileTypeId = fileTypeId;
		constructPanel();
	}

	/**
	 * Constructs the file
	 */
	protected void constructPanel() {
		final Form<SalaryDataErrorPanel> form = new Form<SalaryDataErrorPanel>("salaryDataErrorForm",
			new CompoundPropertyModel<SalaryDataErrorPanel>(this));
		// From date
		form.add(new Label("fileName"));
		// Add the search container
		final WebMarkupContainer salaryDataContainer = new WebMarkupContainer("salaryDataContainer");
		showSearchSalaryDataView(salaryDataContainer);
		salaryDataContainer.setOutputMarkupId(true);
		salaryDataContainer.setOutputMarkupPlaceholderTag(true);
		form.add(salaryDataContainer);
		form.add(new AjaxButton("btnOk") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				setResponsePage(fileTypeId == BtpnConstants.FILE_TYPE_SALARY_UPLOAD ? SearchSalaryDataPage.class : SearchRegDataPage.class);
			}
		});
		add(form);
	}

	/**
	 * This method adds the approveTxnReversalDataView for the transaction reversal, and also adds the sorting logic for
	 * data view
	 */
	protected void showSearchSalaryDataView(final WebMarkupContainer dataViewContainer) {
		// Create the Salary Data provider
		final SalaryDataErrorProvider salaryDataProvider = new SalaryDataErrorProvider("lineNo", salaryErrorData);

		final DataView<SalaryDataErrorBean> dataView = new SalaryDataView(WICKET_ID_PAGEABLE, salaryDataProvider);
		dataView.setItemsPerPage(20);

		// Add the navigation
		navigator = new BtpnCustomPagingNavigator(WICKET_ID_approvalNavigator, dataView) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return salaryDataProvider.size() != 0;
			}
		};
		navigator.setOutputMarkupId(true);
		navigator.setOutputMarkupPlaceholderTag(true);
		dataViewContainer.add(navigator);
		dataViewContainer.add(new Label("no.items", getLocalizer().getString("no.items", this)) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return salaryDataProvider.size() == 0;

			}
		}.setRenderBodyOnly(true));
		// Add the header
		IModel<String> headerDisplayModel = new AbstractReadOnlyModel<String>() {

			private static final long serialVersionUID = 1L;

			@Override
			public String getObject() {
				final String displayTotalItemsText = SalaryDataErrorPanel.this.getLocalizer().getString(
					"approval.totalitems.header", SalaryDataErrorPanel.this);
				return String.format(displayTotalItemsText, approvalTotalItemString, approvalStartIndex,
					approvalEndIndex);
			}
		};
		// Add the approval header
		approvalHeader = new Label(WICKET_ID_approvalTotalItems, headerDisplayModel) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return salaryDataProvider.size() != 0;
			}
		};
		dataViewContainer.add(approvalHeader);
		approvalHeader.setOutputMarkupId(true);
		approvalHeader.setOutputMarkupPlaceholderTag(true);
		// Add the sort providers
		dataViewContainer.add(new BtpnOrderByOrder("orderByLineNo", "lineNo", salaryDataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByErrorRecord", "errorRecord", salaryDataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByErrorDescription", "errorDescription", salaryDataProvider,
			dataView));
		// Add the Data View
		dataViewContainer.addOrReplace(dataView);
	}

	/**
	 * This is the Data View for Salary Data Bean.
	 * 
	 * @author Vikram Gunda
	 */
	private class SalaryDataView extends DataView<SalaryDataErrorBean> {

		private static final long serialVersionUID = 1L;

		protected SalaryDataView(String id, IDataProvider<SalaryDataErrorBean> dataProvider) {
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
		protected void populateItem(final Item<SalaryDataErrorBean> item) {
			SalaryDataErrorBean dataBean = item.getModelObject();
			item.add(new Label("lineNo", String.valueOf(dataBean.getLineNo())));
			item.add(new Label("errorRecord", dataBean.getErrorRecord()));
			item.add(new Label("errorDescription", dataBean.getErrorDescription()));

			// Add the UseCase
			final String cssStyle = item.getIndex() % 2 == 0 ? BtpnConstants.DATA_VIEW_EVEN_ROW_CSS : BtpnConstants.DATA_VIEW_ODD_ROW_CSS;
			item.add(new SimpleAttributeModifier("class", cssStyle));
		}

		@Override
		public boolean isVisible() {
			return ((SalaryDataErrorProvider) internalGetDataProvider()).size() != 0;

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

}
