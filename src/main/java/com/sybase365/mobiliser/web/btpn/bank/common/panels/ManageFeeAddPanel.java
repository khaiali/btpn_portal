package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import java.util.Collections;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.portal.common.util.BillerProductLookup;
import com.btpnwow.portal.common.util.BillerProductLookup.BillerProduct;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageFeeBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageFeeDetailsBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee.ManageFeeAddDetailsPage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnLocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the Manage Fee Add Panel for bank portals. This panel consists of adding fee as fixed, slab and sharing.
 * 
 * @author Vikram Gunda
 */
public class ManageFeeAddPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(ChooseBillerProductPanel.class);
	
	private static final int N = 5;

	@SpringBean(name = "billerProductLookup")
	private BillerProductLookup billerProductLookup;
	
	private final BtpnMobiliserBasePage basePage;

	private ManageFeeBean feeBean;

	private String[] labels;
	
	private CodeValue[] opts;
	
	private FeedbackPanel feedback;
	
	private WebMarkupContainer[] containers;
	
	private DropDownChoice<CodeValue>[] choices;
	
	/**
	 * Constructor for this page.
	 * 
	 * @param id id for the panel
	 */
	public ManageFeeAddPanel(final String id, BtpnMobiliserBasePage basePage, ManageFeeBean feeBean) {
		super(id);
		
		this.basePage = basePage;
		
		if (feeBean == null) {
			this.feeBean = new ManageFeeBean();
		} else {
			this.feeBean = feeBean;
		}
		
		constructPanel();
	}

	private void useCaseOnChange(AjaxRequestTarget target) {
		String rootId;
		
		if (feeBean.getUseCase() == null) {
			rootId = null;
		} else {
			String useCaseId = feeBean.getUseCase().getId();
			String lang = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getLanguage();
			
			if ("223".equals(useCaseId)) { // Airtime Topup
				rootId = "in".equalsIgnoreCase(lang) ? "in.201" : "en.251";
			} else if ("224".equals(useCaseId)) { // Bill Payment
				rootId = "in".equalsIgnoreCase(lang) ? "in.100" : "en.150";
			} else {
				rootId = null;
			}
		}
		
		BillerProduct root = null;
		List<CodeValue> children = null;
		
		if (rootId != null) {
			root = billerProductLookup.get(rootId);
			
			if (root != null) {
				children = billerProductLookup.getChildrenAsCodeValue(rootId);
			}
		}
		
		if (root == null) {
			labels[0] = "";
			opts[0] = null;
		} else {
			labels[0] = root.getPromptLabel();
			opts[0] = null;
		}
		
		if ((children == null) || children.isEmpty()) {
			feeBean.setProduct(null);
			
			choices[0].setChoices(Collections.<CodeValue> emptyList());
			containers[0].setVisible(false);
		} else {
			choices[0].setChoices(children);
			containers[0].setVisible(true);
		}
		
		target.addComponent(choices[0]);
		target.addComponent(containers[0]);
		
		for (int i = 1; i < N; ++i) {
			labels[i] = "";
			opts[i] = null;
			
			choices[0].setChoices(Collections.<CodeValue> emptyList());
			containers[0].setVisible(false);
			
			target.addComponent(choices[0]);
			target.addComponent(containers[0]);
		}
	}
	
	private void constructOneLevel(Form<?> frm, final int i, IChoiceRenderer<CodeValue> renderer) {
		String si = Integer.toString(i);
		
		WebMarkupContainer container = new WebMarkupContainer("containers.".concat(si));
		container.setOutputMarkupPlaceholderTag(true);
		container.setVisible(i == 0);
		
		Label label = new Label("labels.".concat(si));
		label.setOutputMarkupPlaceholderTag(true);
		
		DropDownChoice<CodeValue> choice = new DropDownChoice<CodeValue>("opts.".concat(si));
		choice.setChoices(Collections.<CodeValue> emptyList());
			
		choice.setChoiceRenderer(renderer);
		choice.setOutputMarkupPlaceholderTag(true);
		
		choice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			
			private static final long serialVersionUID = 1L;

			protected void onUpdate(AjaxRequestTarget target) {
				if (i + 1 >= N) {
					return;
				}
				
				String rootId;
				
				BillerProduct root;
				List<CodeValue> children;
				
				if (opts[i] == null) {
					rootId = null;
					
					root = null;
					children = null;
				} else {
					rootId = opts[i].getId();
					
					root = billerProductLookup.get(rootId);
					children = billerProductLookup.getChildrenAsCodeValue(rootId);
				}
				
				if ((root == null) || (children == null) || children.isEmpty()) {
					opts[i + 1] = null;
					labels[i + 1] = "";
					
					choices[i + 1].setChoices(Collections.<CodeValue> emptyList());
					containers[i + 1].setVisible(false);
				} else {
					opts[i + 1] = null;
					labels[i + 1] = root.getPromptLabel();
					
					choices[i + 1].setChoices(children);
					containers[i + 1].setVisible(true);
				}
				
				target.addComponent(choices[i + 1]);
				target.addComponent(containers[i + 1]);
			
				for (int j = i + 2; j < N; ++j) {
					opts[j] = null;
					labels[j] = "";
					
					choices[j].setChoices(Collections.<CodeValue> emptyList());
					containers[j].setVisible(false);
					
					target.addComponent(choices[j]);
					target.addComponent(containers[j]);
				}
			}
		});
		
		labels[i] = ""; 
		opts[i] = null;
		
		container.add(label);
		container.add(choice);
		
		containers[i] = container;
		choices[i] = choice;
		
		frm.add(container);
	}
	
	private void constructPanel() {
		final Form<ManageFeeAddPanel> form = new Form<ManageFeeAddPanel>(
				"feeAddForm", new CompoundPropertyModel<ManageFeeAddPanel>(this));
		
		// Error Messages
		form.add(new FeedbackPanel("errorMessages"));
		
		// Add the Choice Renderer
		final IChoiceRenderer<CodeValue> choiceRenderer = new ChoiceRenderer<CodeValue>(
			BtpnConstants.DISPLAY_EXPRESSION, BtpnConstants.ID_EXPRESSION);
		
		// use case
		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>(
				"feeBean.useCase", CodeValue.class, BtpnConstants.RESOURCE_BUNDLE_USE_CASES,
				this, Boolean.FALSE, true)
				.setChoiceRenderer(choiceRenderer)
				.setRequired(true)
				.add(new ErrorIndicator())
				.add(new AjaxFormComponentUpdatingBehavior("onchange") {
					
					private static final long serialVersionUID = 1L;

					protected void onUpdate(AjaxRequestTarget target) {
						ManageFeeAddPanel.this.useCaseOnChange(target);
					}
				}));
		
		// product
		for (int i = 0; i < N; ++i) {
			constructOneLevel(form, i, choiceRenderer);
		}
		
		// Add check box apply to payee
		form.add(new CheckBox("feeBean.applyToPayee"));

		// Radio Group for feeTypes
		final RadioGroup<String> rg = new RadioGroup<String>("feeBean.feeType");
		rg.add(new Radio<String>("radio.fixed", Model.of(BtpnConstants.USECASE_FIXED_RADIO)));
		rg.add(new Radio<String>("radio.slab", Model.of(BtpnConstants.USECASE_SLAB_RADIO)));
		rg.add(new Radio<String>("radio.sharing", Model.of(BtpnConstants.USECASE_SHARING_RADIO)));

		// Add the radio butoon
		form.add(rg);

		// Dropdown choice for use case name
		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("feeBean.productName", CodeValue.class,
			BtpnConstants.RESOURCE_BUNDLE_PRODUCTS_TYPES, this, false, true).setChoiceRenderer(codeValueChoiceRender)
			.add(new ErrorIndicator()));
		// add Slab Fee Container;
		final WebMarkupContainer slabFeeContainer = addSlabFeeContainer(codeValueChoiceRender);
		slabFeeContainer.setOutputMarkupId(true);
		slabFeeContainer.setOutputMarkupPlaceholderTag(true);
		slabFeeContainer.setVisibilityAllowed(true);
		form.add(slabFeeContainer);
		slabFeeContainer.setVisible(false);

		// Form component Choice Updating behaviour
		rg.setMarkupId("feeTypeMarkupId");
		rg.add(new SlabFormChoiceComponentUpdatingBehavior(slabFeeContainer));

		form.add(addAddButton());
		// Add add Button
		add(form);
	}

	/**
	 * This method adds the slab fee container
	 */
	protected WebMarkupContainer addSlabFeeContainer(IChoiceRenderer<CodeValue> codeValueChoiceRender) {
		final WebMarkupContainer slabFeeContainer = new WebMarkupContainer("slabFeeContainer");
		// Text Field for use case name
		slabFeeContainer.add(new RequiredTextField<String>("feeDetailsBean.feeName").setRequired(true).add(
			new ErrorIndicator()));
		final IChoiceRenderer<CodeValue> codeValueChoiceRenderGL = new ChoiceRenderer<CodeValue>(
				BtpnConstants.DISPLAY_EXPRESSION_ID_VALUE, BtpnConstants.ID_EXPRESSION);		// GL Codes
		slabFeeContainer.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("feeDetailsBean.glCode",
			CodeValue.class, BtpnConstants.RESOURCE_BUNDLE_GL_CODES, this, true, false)
			.setChoiceRenderer(codeValueChoiceRenderGL).setRequired(true).add(new ErrorIndicator()));
		return slabFeeContainer;
	}

	/**
	 * This method adds the Add button for the Manage Products
	 */
	protected Button addAddButton() {
		Button submitButton = new Button("btnAdd") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(new ManageFeeAddDetailsPage(feeBean, feeDetailsBean));
			}
		};
		return submitButton;
	}

	/**
	 * This class defines the SlabFormChoiceComponentUpdatingBehavior. For slab component this is component is visible.
	 */
	private class SlabFormChoiceComponentUpdatingBehavior extends AjaxFormChoiceComponentUpdatingBehavior {

		private static final long serialVersionUID = 1L;

		private WebMarkupContainer slabWebMarkupContainer;

		public SlabFormChoiceComponentUpdatingBehavior(final WebMarkupContainer slabWebMarkupContainer) {
			super();
			this.slabWebMarkupContainer = slabWebMarkupContainer;
		}

		@Override
		protected void onUpdate(AjaxRequestTarget target) {
			final String feeType = feeBean.getFeeType();
			if (PortalUtils.exists(feeType) && feeType.equals(BtpnConstants.USECASE_SLAB_RADIO)) {
				slabWebMarkupContainer.setVisible(true);
			} else {
				slabWebMarkupContainer.setVisible(false);
			}
			target.addComponent(slabWebMarkupContainer);
		}

	}
}
