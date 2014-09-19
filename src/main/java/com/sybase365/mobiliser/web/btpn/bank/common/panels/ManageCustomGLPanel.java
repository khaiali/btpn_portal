package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.validator.PatternValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.btpnwow.core.gl.facade.contract.FindGLRequest;
import com.btpnwow.core.gl.facade.contract.FindGLResponse;
import com.btpnwow.core.gl.facade.contract.GetGLRequest;
import com.btpnwow.core.gl.facade.contract.GetGLResponse;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageCustomGeneralLedgerBean;
import com.sybase365.mobiliser.web.btpn.bank.common.dataproviders.ManageCustomGeneralLedgerDataProvider;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.generalledger.ManageCustomGLAddPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.generalledger.ManageCustomGLDetailPage;
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
public class ManageCustomGLPanel extends Panel {

	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(ManageCustomGLPanel.class);
	
	private static final String WICKET_ID_PAGEABLE = "pageable";
	private static final String WICKET_ID_GLNAVIGATOR = "glNavigator";
	private static final String WICKET_ID_GLTOTALITEMS = "glHeader";
	private static final String WICKET_ID_LINK = "idLink";
	
	private String glTotalItemString;
	private int glStartIndex = 0;
	private int glEndIndex = 0;
	
	private FeedbackPanel feedBackPanel;
	protected BtpnMobiliserBasePage basePage;
	private ManageCustomGeneralLedgerBean cusLedgerBean;
	private WebMarkupContainer glContainer;
	private ManageCustomGeneralLedgerDataProvider glDataProvider;
	
	private Component glCodeComp;
	private Component parentGLCodeComp;
	private Component typeComp;
	private Component glDescComp;
	private Component noteComp;

	
	public ManageCustomGLPanel(String id, BtpnBaseBankPortalSelfCarePage basePage) {
		super(id);
		this.basePage = basePage;
		constructPanel();
	}
	
	protected void constructPanel() {
		
		log.info(" ### (ManageCustomGLPanel) constructPanel ### "); 
		final Form<ManageCustomGLPanel> form = new Form<ManageCustomGLPanel>("cusGLForm",
			new CompoundPropertyModel<ManageCustomGLPanel>(this));
		
		// Add feedback panel for Error Messages
		feedBackPanel = new FeedbackPanel("errorMessages");
		feedBackPanel.setOutputMarkupId(true);
		feedBackPanel.setOutputMarkupPlaceholderTag(true);
		form.add(feedBackPanel);
		
		final IChoiceRenderer<CodeValue> renderChoice = new ChoiceRenderer<CodeValue>(
				BtpnConstants.DISPLAY_EXPRESSION_ID_VALUE, BtpnConstants.ID_EXPRESSION);
		
		form.add(glCodeComp = new TextField<String>("cusLedgerBean.glCode")
				.add(new PatternValidator(BtpnConstants.GL_CODE_REGEX)).add(BtpnConstants.GL_MINIMUM_LENGTH)
				.add(new ErrorIndicator()));
		glCodeComp.setOutputMarkupId(true);
	
		form.add(parentGLCodeComp = new BtpnLocalizableLookupDropDownChoice<CodeValue>(
				"cusLedgerBean.selectedParentGlCode", CodeValue.class, BtpnConstants.RESOURCE_BUNDLE_PARENT_GENERAL_LEDGER_CODES, this)
				.setNullValid(true).setChoiceRenderer(renderChoice).add(new ErrorIndicator()));
		parentGLCodeComp.setOutputMarkupId(true);
	
		form.add(typeComp = new BtpnLocalizableLookupDropDownChoice<CodeValue>("cusLedgerBean.selectedType", CodeValue.class,
				BtpnConstants.RESOURCE_BUNDLE_GENERAL_LEDGER_TYPES, this).add(new ErrorIndicator()));
		typeComp.setOutputMarkupId(true);
	
		form.add(glDescComp = new TextField<String>("cusLedgerBean.glDescription").add(new ErrorIndicator()));
			glDescComp.setOutputMarkupId(true);
		
		form.add(noteComp = new TextField<String>("cusLedgerBean.note").add(new ErrorIndicator()));
		noteComp.setOutputMarkupId(true);
		
		glContainer = new WebMarkupContainer("glContainer");
		manageGeneralLedgerDataView(glContainer);
		glContainer.setOutputMarkupId(true);
		glContainer.setOutputMarkupPlaceholderTag(true);
		glContainer.setVisible(false);
		form.add(glContainer);
		
		// Add add button
		Button addButton = new Button("addBtn"){
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit(){
				if (!PortalUtils.exists(cusLedgerBean)){
					cusLedgerBean = new ManageCustomGeneralLedgerBean();
				}
				setResponsePage(ManageCustomGLAddPage.class);
			}
		};
		form.add(addButton);
		
		
		form.add(new AjaxButton("searchBtn"){
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form){
				if (!PortalUtils.exists(cusLedgerBean)){
					cusLedgerBean = new ManageCustomGeneralLedgerBean();
					log.info(" ### MASUK SINI ### ");
				}
				cusLedgerBean.setGeneralLedgerList(new ArrayList<ManageCustomGeneralLedgerBean>());
				log.info(" ### KLICK SEARCH GL_CODE ### "+cusLedgerBean.getGlCode());
				log.info(" ### KLICK SEARCH GL DESC ### "+cusLedgerBean.getGlDescription());
				handleSearchGL(target);
			};
			
			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.addComponent(feedBackPanel);
				target.addComponent(glCodeComp);
				target.addComponent(parentGLCodeComp);
				target.addComponent(typeComp);
				target.addComponent(glDescComp);
				target.addComponent(noteComp);
				target.addComponent(glContainer);
				super.onError(target, form);
			}
			
		});

		add(form);
	}
		
	private void manageGeneralLedgerDataView(final WebMarkupContainer dataViewContainer){
		
		log.info(" ### (ManageCustomGLPanel) manageGeneralLedgerDataView ### ");
		glDataProvider = new ManageCustomGeneralLedgerDataProvider("glCode");
		
		final DataView<ManageCustomGeneralLedgerBean> dataView = new DataView<ManageCustomGeneralLedgerBean>(WICKET_ID_PAGEABLE, glDataProvider){
			private static final long serialVersionUID = 1L;
			@Override
			protected void onBeforeRender(){
				final ManageCustomGeneralLedgerDataProvider dataProvider = (ManageCustomGeneralLedgerDataProvider) internalGetDataProvider();
				dataProvider.setGeneralLedgerList(cusLedgerBean.getGeneralLedgerList());
				refreshTotalItemCount();
				super.onBeforeRender();
			}
			
			@Override
			protected void populateItem(final Item<ManageCustomGeneralLedgerBean> item) {
				final ManageCustomGeneralLedgerBean entry = item.getModelObject();
				
				final AjaxLink<ManageCustomGeneralLedgerBean> glLink = new AjaxLink<ManageCustomGeneralLedgerBean>(WICKET_ID_LINK, item.getModel()) {
					private static final long serialVersionUID = 1L;
					@Override
					public void onClick(AjaxRequestTarget target) {
						handleGLDetailClick((ManageCustomGeneralLedgerBean)item.getModelObject());
					}
				};
				
				glLink.add(new Label("glLink", entry.getGlCode()));
				item.add(glLink);
				item.add(new Label("glDescription", entry.getGlDescription()));
				item.add(new Label("note", entry.getNote()));
				
				final String cssStyle = item.getIndex() % 2 == 0 ? BtpnConstants.DATA_VIEW_EVEN_ROW_CSS
						: BtpnConstants.DATA_VIEW_ODD_ROW_CSS;
				item.add(new SimpleAttributeModifier("class", cssStyle));
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
			
			@Override
			public boolean isVisible(){
				final ManageCustomGeneralLedgerDataProvider dataProvider = (ManageCustomGeneralLedgerDataProvider) internalGetDataProvider();
				dataProvider.setGeneralLedgerList(cusLedgerBean.getGeneralLedgerList());
				return cusLedgerBean.getGeneralLedgerList().size() != 0;
			}
		};
		
		// Add The navigation
		final BtpnCustomPagingNavigator navigator = new BtpnCustomPagingNavigator(WICKET_ID_GLNAVIGATOR, dataView){
			private static final long serialVersionUID = 1L;
			@Override
			public boolean isVisible(){
				return cusLedgerBean != null && cusLedgerBean.getGeneralLedgerList().size() != 0;
			}
		};
		navigator.setOutputMarkupId(true);
		navigator.setOutputMarkupPlaceholderTag(true);
		dataViewContainer.add(navigator);
		dataView.setItemsPerPage(20);
		
		// Add the header
		IModel<String> headerDisplayModel = new AbstractReadOnlyModel<String>() {
			private static final long serialVersionUID = 1L;
			@Override
			public String getObject() {
				final String displayTotalItemsText = ManageCustomGLPanel.this.getLocalizer().getString(
					"gl.totalitems.header", ManageCustomGLPanel.this);
				return String.format(displayTotalItemsText, glTotalItemString, glStartIndex, glEndIndex);
			}
		};
		
		final Label glsHeader = new Label(WICKET_ID_GLTOTALITEMS, headerDisplayModel) {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean isVisible() {
				return cusLedgerBean != null && cusLedgerBean.getGeneralLedgerList().size() != 0;
			}
		};
		glsHeader.setOutputMarkupId(true);
		glsHeader.setOutputMarkupPlaceholderTag(true);
		dataViewContainer.add(glsHeader);
		
		// Add the no items label.
		dataViewContainer.add(new Label("no.items", getLocalizer().getString(
				"gl.emptyRecordsMessage", this)) {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean isVisible() {
				return cusLedgerBean != null && cusLedgerBean.getGeneralLedgerList().size() == 0;
			}
		}.setRenderBodyOnly(true).setOutputMarkupPlaceholderTag(true));
		
		// Add the sort providers
		dataViewContainer.add(new BtpnOrderByOrder("orderByGLCode", "glCode", glDataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByGLDesc", "glDescription", glDataProvider, dataView));
		dataViewContainer.addOrReplace(dataView);
		
	}
	
	private void handleSearchGL(AjaxRequestTarget target) {
		if (!PortalUtils.exists(cusLedgerBean.getGlCode())){
			log.info(" ### handleSearchGL GL CODE ### "+cusLedgerBean.getGlCode());
		}
		getGeneralLedgerList();
		glContainer.setVisible(true);
		target.addComponent(glContainer);
		target.addComponent(feedBackPanel);
		target.addComponent(glCodeComp);
		target.addComponent(parentGLCodeComp);
		target.addComponent(glDescComp);
		target.addComponent(noteComp);
	}
	
	private void getGeneralLedgerList(){
		
		log.info(" ### (ManageCustomGLPanel) getGeneralLedgerList ### ");
		List<ManageCustomGeneralLedgerBean> gLedgerList = new ArrayList<ManageCustomGeneralLedgerBean>();
		final FindGLRequest request;
		
		try {
			
			request = basePage.getNewMobiliserRequest(FindGLRequest.class);
			request.setSessionId(basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getSessionId());
			
			if (StringUtils.hasText(cusLedgerBean.getGlCode()))
				request.setCode(cusLedgerBean.getGlCode());
			else
				request.setCode(null);
			
			if (StringUtils.hasText(cusLedgerBean.getGlDescription()))
				request.setDescription(cusLedgerBean.getGlDescription());
			else
				request.setDescription(null);
			
			if (cusLedgerBean.getSelectedParentGlCode() != null){
				if (cusLedgerBean.getSelectedParentGlCode().getId() != null){
					request.setParent(cusLedgerBean.getSelectedParentGlCode().getId());
				}else{
					request.setParent(null);
				}
			}
				
			request.setStart(0);
			request.setLength(Integer.MAX_VALUE);
			
			final FindGLResponse response = this.basePage.getGlClient().find(request);
			log.info(" ### ((ManageCustomGLPanel) getGeneralLedgerList STATUS RESPONSE ### "+response.getStatus().getCode());
			if (this.basePage.evaluateBankPortalMobiliserResponse(response)) {
				gLedgerList =  ConverterUtils.convertToCustomGeneralLedger(response.getItem(), basePage.getLookupMapUtility(), this);
			}else{
				handleSpecificErrorMessage(response.getStatus().getCode());
			}
			
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			log.error("Exception occured while fetching All General Ledger ===> ", e);
		}
		
		cusLedgerBean.setGeneralLedgerList(gLedgerList);
	}
	
	/**
	 * This method handleGLDetailClick the list of Manage Custom General Ledger details.
	 * 
	 * @return ManageCustomGeneralLedgerBean returns the list of ManageCustomGeneralLedgerBean beans
	 */
	private void handleGLDetailClick(final ManageCustomGeneralLedgerBean cusLedgerBean) {
		
		final GetGLRequest request;
		
		try {
			log.info(" ### (ManageCustomGLPanel) handleGLDetailClick ### "); 
			request = this.basePage.getNewMobiliserRequest(GetGLRequest.class);
			request.setSessionId(basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getSessionId());
			log.info(" ### (ManageCustomGLPanel) handleGLDetailClick GL CODE ### "+ cusLedgerBean.getGlCode());
			request.setCode(Long.valueOf(cusLedgerBean.getGlCode()));
			final GetGLResponse response = this.basePage.getGlClient().get(request);
			if (this.basePage.evaluateBankPortalMobiliserResponse(response)) {
				ManageCustomGeneralLedgerBean bean = new ManageCustomGeneralLedgerBean();
				bean.setGlCode(Long.toString(response.getCode()));
				bean.setGlDescription(response.getDescription());
				bean.setSelectedParentGlCode(new CodeValue(response.getParent().toString()));
				log.info(" ### (ManageCustomGLPanel) handleGLDetailClick TYPE ### "+ response.getType());
				Map<String, String> map = new HashMap<String, String>();
				map.put("C", "Credit");
				map.put("D", "Debit");
				if (response.getType().equalsIgnoreCase("C"))
					bean.setSelectedType(new CodeValue(response.getType(), map.get("C").toString()));
				else
					bean.setSelectedType(new CodeValue(response.getType(), map.get("D").toString()));
				
				setResponsePage(new ManageCustomGLDetailPage(bean));
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
