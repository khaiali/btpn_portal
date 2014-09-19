package com.sybase365.mobiliser.web.btpn.bank.common.panels;

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
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.validator.PatternValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.core.gl.facade.contract.FindGLRequest;
import com.btpnwow.core.gl.facade.contract.FindGLResponse;
import com.btpnwow.core.gl.facade.contract.GetGLRequest;
import com.btpnwow.core.gl.facade.contract.GetGLResponse;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageCustomGeneralLedgerBean;
import com.sybase365.mobiliser.web.btpn.bank.common.dataproviders.ManageCustomGeneralLedgerDataProvider;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.generalledger.ManageCustomGeneralLedgerAddPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.components.BtpnCustomPagingNavigator;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnLocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.btpn.util.BtpnOrderByOrder;
import com.sybase365.mobiliser.web.btpn.util.ConverterUtils;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * @author Andi Samallangi W
 *
 */
public class ManageCustomGeneralLedgerPanel extends Panel {

	private static final long serialVersionUID = -4350259396616681889L;
	
	private static final Logger log = LoggerFactory.getLogger(ManageCustomGeneralLedgerPanel.class);
	
	private static final String WICKET_ID_PAGEABLE = "pageable";
	private static final String WICKET_ID_GLNAVIGATOR = "glNavigator";
	private static final String WICKET_ID_GLTOTALITEMS = "glHeader";
	private static final String WICKET_ID_LINK = "detailsLink";
	
	private String glTotalItemString;
	private int glStartIndex = 0;
	private int glEndIndex = 0;
	private Label glsHeader;
	
	private FeedbackPanel feedBackPanel;
	protected BtpnMobiliserBasePage basePage;
	protected ManageCustomGeneralLedgerBean ledgerBean;
	private WebMarkupContainer glContainer;
	
	private Component glCodeComp;
	private Component parentGLCodeComp;
	private Component typeComp;
	private Component glDescComp;
	private Component noteComp;
	
	private BtpnCustomPagingNavigator navigator;
	
	public ManageCustomGeneralLedgerPanel(String id, BtpnBaseBankPortalSelfCarePage basePage) {
		super(id);
		this.basePage = basePage;
		constructPanel();
	}
	
	
	protected void constructPanel() {
		
		log.info(" ### (ManageCustomGeneralLedgerPanel) constructPanel ### "); 
		final Form<ManageCustomGeneralLedgerPanel> form = new Form<ManageCustomGeneralLedgerPanel>("customGLForm",
			new CompoundPropertyModel<ManageCustomGeneralLedgerPanel>(this));
		
		// Add feedback panel for Error Messages
		feedBackPanel = new FeedbackPanel("errorMessages");
		feedBackPanel.setOutputMarkupId(true);
		feedBackPanel.setOutputMarkupPlaceholderTag(true);
		
		form.add(glCodeComp = new TextField<String>("ledgerBean.glCode").setRequired(true)
				.add(new PatternValidator(BtpnConstants.GL_CODE_REGEX)).add(BtpnConstants.GL_MINIMUM_LENGTH)
				.add(new ErrorIndicator()));
		glCodeComp.setOutputMarkupId(true);
	
		form.add(parentGLCodeComp = new BtpnLocalizableLookupDropDownChoice<CodeValue>(
				"ledgerBean.selectedParentGlCode", CodeValue.class, BtpnConstants.RESOURCE_BUNDLE_PARENT_GENERAL_LEDGER_CODES, this)
				.setNullValid(true).add(new ErrorIndicator()));
		parentGLCodeComp.setOutputMarkupId(true);
	
		form.add(typeComp = new BtpnLocalizableLookupDropDownChoice<CodeValue>("ledgerBean.selectedType", CodeValue.class,
				BtpnConstants.RESOURCE_BUNDLE_GENERAL_LEDGER_TYPES, this).setRequired(true).add(new ErrorIndicator()));
		typeComp.setOutputMarkupId(true);
	
		form.add(glDescComp = new TextField<String>("ledgerBean.glDescription").setRequired(true).add(new ErrorIndicator()));
			glDescComp.setOutputMarkupId(true);
		
		form.add(noteComp = new TextField<String>("ledgerBean.note").add(new ErrorIndicator()));
		noteComp.setOutputMarkupId(true);
		
		glContainer = new WebMarkupContainer("glContainer");
		glContainer.add(feedBackPanel);
		manageGeneralLedgerDataView(glContainer);
		glContainer.setOutputMarkupId(true);
		glContainer.setOutputMarkupPlaceholderTag(true);
		glContainer.setRenderBodyOnly(true);
		glContainer.add(glCodeComp);
		glContainer.add(parentGLCodeComp);
		glContainer.add(typeComp);
		glContainer.add(glDescComp);
		glContainer.add(noteComp);
		glContainer.add(addButton);
		glContainer.add(srcButton);
		form.add(glContainer);

		add(form);
	}
	
	Button addButton = new Button("addBtn"){
		private static final long serialVersionUID = 1L;
		public void onSubmit(){
			if (!PortalUtils.exists(ledgerBean)){
				ledgerBean = new ManageCustomGeneralLedgerBean();
			}
			setResponsePage(new ManageCustomGeneralLedgerAddPage(ledgerBean, "add"));
		}
	};
	
	Button srcButton = new Button("searchBtn"){
		private static final long serialVersionUID = 1L;
		@Override
		public void onSubmit() {
			if (!PortalUtils.exists(ledgerBean)){
				ledgerBean = new ManageCustomGeneralLedgerBean();
			}
		}
	}.setDefaultFormProcessing(false);
	
		
	private void manageGeneralLedgerDataView(final WebMarkupContainer dataViewContainer){
		
		log.info(" ### (ManageCustomGeneralLedgerPanel) manageGeneralLedgerDataView ### ");
		
		final List<ManageCustomGeneralLedgerBean> manageGlList = fetchManageGlList();
		
		final ManageCustomGeneralLedgerDataProvider dataProvider = new ManageCustomGeneralLedgerDataProvider("glCode", manageGlList);
		
		final DataView<ManageCustomGeneralLedgerBean> dataView = new ManageGeneralLedgerDataView(WICKET_ID_PAGEABLE, dataProvider);
			dataView.setItemsPerPage(10);
			
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
			IModel<String> headerDisplayModel = new AbstractReadOnlyModel<String>() {
				private static final long serialVersionUID = 1L;

				@Override
				public String getObject() {
					final String displayTotalItemsText = ManageCustomGeneralLedgerPanel.this.getLocalizer().getString("gl.totalitems.header", ManageCustomGeneralLedgerPanel.this);
					return String.format(displayTotalItemsText, glTotalItemString, glStartIndex, glEndIndex);
				}
			};
			
			glsHeader = new Label(WICKET_ID_GLTOTALITEMS, headerDisplayModel){
				private static final long serialVersionUID = 1L;
				public boolean isVisible(){
					return manageGlList.size() != 0;
				}
			};
			dataViewContainer.add(glsHeader);
			glsHeader.setOutputMarkupId(true);
			glsHeader.setOutputMarkupPlaceholderTag(true);
			
			// Add the sort providers
			dataViewContainer.add(new BtpnOrderByOrder("orderByGLCode", "glCode", dataProvider, dataView));
			dataViewContainer.add(new BtpnOrderByOrder("orderByGLDesc", "glDescription", dataProvider, dataView));
			
			dataViewContainer.addOrReplace(dataView);
			
			log.info(" ### (ManageCustomGeneralLedgerPanel) manageGeneralLedgerDataView END ### ");
		
	}
	
	
	private class ManageGeneralLedgerDataView extends DataView<ManageCustomGeneralLedgerBean>{

		private static final long serialVersionUID = 8500864172482968174L;
		
		protected ManageGeneralLedgerDataView(String id,
				IDataProvider<ManageCustomGeneralLedgerBean> dataProvider) {
			super(id, dataProvider);
			setOutputMarkupId(true);
			setOutputMarkupPlaceholderTag(true);
		}
		
		@Override
		protected void onBeforeRender(){
			refreshTotalItemCount();
			super.onBeforeRender();
		}

		@Override
		protected void populateItem(final Item<ManageCustomGeneralLedgerBean> item) {
			
			final ManageCustomGeneralLedgerBean entry = item.getModelObject();
			item.add(new Label("glCode", entry.getGlCode()));
//			item.add(new Label("parentGlCode", entry.getSelectedParentGlCode().getValue()));
//			item.add(new Label("type", entry.getSelectedType().getValue()));
			item.add(new Label("glDescription", entry.getGlDescription()));
			item.add(new Label("note", entry.getNote()));
			
			final AjaxLink<ManageCustomGeneralLedgerBean> detailsLink = new AjaxLink<ManageCustomGeneralLedgerBean>(WICKET_ID_LINK, item.getModel()) {

				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					handleGLDetailsClick((ManageCustomGeneralLedgerBean)item.getModelObject());
					target.addComponent(feedBackPanel);
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
	
	
	private List<ManageCustomGeneralLedgerBean> fetchManageGlList(){
		
		log.info(" ### (ManageCustomGeneralLedgerPanel) feetchManageGlList ### ");
		
		final List<ManageCustomGeneralLedgerBean> manageGlList = new ArrayList<ManageCustomGeneralLedgerBean>();
		FindGLRequest request;
		
		try {
			
			request = basePage.getNewMobiliserRequest(FindGLRequest.class);
			request.setSessionId(basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getSessionId());
			
//			if (StringUtils.hasText(ledgerBean.getGlCode())) {
//				request.setCode(ledgerBean.getGlCode());
//			} else {
				request.setCode(null);
//			}
			
//			if (StringUtils.hasText(ledgerBean.getGlDescription())){
//				request.setDescription(ledgerBean.getGlDescription());
//			}else{
				request.setDescription(null);
//			}
			
//			if (StringUtils.hasText(ledgerBean.getSelectedParentGlCode().getId())){
//				request.setParent(ledgerBean.getSelectedParentGlCode().getId());
//			}else{
				request.setParent(null);
//			}	
				
			request.setStart(0);
//			request.setLength(Integer.MAX_VALUE);
			request.setLength(10);
			
			final FindGLResponse response = this.basePage.getGlClient().find(request);
			log.info(" ### ((ManageCustomGeneralLedgerPanel) feetchManageGlList STATUS RESPONSE ### "+response.getStatus().getCode());
			if (this.basePage.evaluateBankPortalMobiliserResponse(response)) {
				log.info("### (ManageCustomGeneralLedgerPanel) feetchManageGlList ITEM ### " +response.getItem());
//				return ConverterUtils.convertToCustomGeneralLedger(response.getItem());
			}else{
				handleSpecificErrorMessage(response.getStatus().getCode());
			}
			
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			log.error("Exception occured while fetching All General Ledger ===> ", e);
		}
		
		return manageGlList;
	}
	
	/**
	 * This method handleGLDetailsClick the list of Manage Custom General Ledger details.
	 * 
	 * @return ManageCustomGeneralLedgerBean returns the list of ManageCustomGeneralLedgerBean beans
	 */
	private void handleGLDetailsClick(final ManageCustomGeneralLedgerBean ledgerBean) {
		
		GetGLRequest request;
		
		try {
			log.info(" ### (ManageCustomGeneralLedgerPanel) handleGLDetailsClick ### "); 
			request = this.basePage.getNewMobiliserRequest(GetGLRequest.class);
			log.info(" ### (ManageCustomGeneralLedgerPanel) handleGLDetailsClick SESSION ID ### " 
			+ basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getSessionId());
			request.setSessionId(basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getSessionId());
			log.info(" ### (ManageCustomGeneralLedgerPanel) handleGLDetailsClick GL CODE ### "+ ledgerBean.getGlCode());
			request.setCode(Long.valueOf(ledgerBean.getGlCode()));
			final GetGLResponse response = this.basePage.getGlClient().get(request);
			if (this.basePage.evaluateBankPortalMobiliserResponse(response)) {
				ManageCustomGeneralLedgerBean bean = new ManageCustomGeneralLedgerBean();
				bean.setGlCode(Long.toString(response.getCode()));
				bean.setGlDescription(response.getDescription());
//				bean.setParentGlCode(response.getParent().toString());
//				bean.setType(response.getType());
//				bean.setFlag("update");
				setResponsePage(new ManageCustomGeneralLedgerAddPage(bean, "update"));
			} else {
				handleSpecificErrorMessage(response.getStatus().getCode());
			}
			
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			log.error("Exception occured while fetching GL Code Details  ===> ", e);
		}
	}
	
	
	/**
	 * This method handles the specific error message.
	 */
	private String handleSpecificErrorMessage(final int errorCode) {
		// Specific error message handling
		final String messageKey = "error.gl." + errorCode;
		String message = getLocalizer().getString(messageKey, this);
		// Generic error messages
		if (messageKey.equals(message)) {
			message = getLocalizer().getString("gl.error", this);
		}
		return message;
	}

}
