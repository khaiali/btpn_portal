package com.sybase365.mobiliser.web.btpn.consumer.pages.portal.standing;

import java.util.Collections;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.portal.common.util.BillerProductLookup;
import com.btpnwow.portal.common.util.BillerProductLookup.BillerProduct;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.common.components.FavouriteDropdownChoice;
import com.sybase365.mobiliser.web.btpn.consumer.beans.BillPaymentBean;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;


public class BillPaymentPage extends BtpnBaseConsumerPortalSelfCarePage {

	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(BillPaymentPage.class);
	
	private static final int N = 7;
	private String[] labels;
	private CodeValue[] opts;
	private WebMarkupContainer[] containers;
	private DropDownChoice<CodeValue>[] choices;

	private BillPaymentBean billPayBean;
	
	@SpringBean(name = "billerProductLookup")
	private BillerProductLookup billerProductLookup;

	private FeedbackPanel feedback;
	private boolean isTelePhonaBillPay;
	private Component billNumberField;
	private Component favouriteField;
	
	private WebMarkupContainer favouriteContainer;
	private WebMarkupContainer manualContainer;
	

	public BillPaymentPage() {
		super();
	}
	
	@Override
	protected void initOwnPageComponents() {
		
		super.initOwnPageComponents();
		
		this.labels = new String[N];
		this.opts = new CodeValue[N];
		
		this.containers = new WebMarkupContainer[N];
		this.choices = new DropDownChoice[N];
		
		this.billPayBean = new BillPaymentBean();
		
		final Form<BillPaymentPage> form = new Form<BillPaymentPage>(
				"billPaymentForm", new CompoundPropertyModel<BillPaymentPage>(this));
		
		form.add(new FeedbackPanel("errorMessages"));
		
		final IChoiceRenderer<CodeValue> choiceRenderer = new ChoiceRenderer<CodeValue>(
			BtpnConstants.DISPLAY_EXPRESSION, BtpnConstants.ID_EXPRESSION);
		
		String rootId;
		
		if ("IN".equalsIgnoreCase(getLocale().getLanguage())) {
			rootId = "in.100";
		} else {
			rootId = "en.150";
		}
		
		for (int i = 0; i < N; ++i) {
			constructOneLevel(form, i, choiceRenderer, rootId);
		}
		
		// Add the radio button for manual or favourite
		billPayBean.setManualOrFavourite(BtpnConstants.BILLPAYMENT_MANUALLY);
		
		form.add(new RadioGroup<String>("billPayBean.manualOrFavourite")
				.add(new Radio<String>("radio.manually", Model.of(BtpnConstants.BILLPAYMENT_MANUALLY)))
				.add(new Radio<String>("radio.favourite", Model.of(BtpnConstants.BILLPAYMENT_FAVLIST)))
				.add(new FavouriteViewChoiceComponentUpdatingBehavior()));

		// Add the favourite container.	
		favouriteContainer = new WebMarkupContainer("favouriteContainer");
		favouriteContainer.add(favouriteField = new FavouriteDropdownChoice("billPayBean.selectedBillerId", false,
				true, this.getMobiliserWebSession().getBtpnLoggedInCustomer()
				.getCustomerId(), 2).setNullValid(false).add(new ErrorIndicator()));
		favouriteContainer.setOutputMarkupPlaceholderTag(true);
		favouriteContainer.setVisible(false);
		favouriteContainer.setOutputMarkupId(true);
		form.add(favouriteContainer);
		
		
		// Add the manual container.
		manualContainer = new WebMarkupContainer("manualContainer");
		final String billNumberLabel = isTelePhonaBillPay ? "label.phoneNumber" : "label.billNumber";
		manualContainer.add(new Label("label.billNumber", getLocalizer().getString(billNumberLabel, this)));
		manualContainer.add(billNumberField = new TextField<String>("billPayBean.selectedBillerId.id")
			.add(new ErrorIndicator()));
		manualContainer.setOutputMarkupPlaceholderTag(true);
		manualContainer.setOutputMarkupId(true);
		form.add(manualContainer);

		// Submit button for form.
		addSubmitButton(form);
		
		add(form);
	}
	
	private void constructOneLevel(Form<?> frm, final int i, IChoiceRenderer<CodeValue> renderer, String rootId) {
		String si = Integer.toString(i);
		
		WebMarkupContainer container = new WebMarkupContainer("containers.".concat(si));
		container.setOutputMarkupPlaceholderTag(true);
		container.setVisible(i == 0);
		
		Label label = new Label("labels.".concat(si));
		label.setOutputMarkupPlaceholderTag(true);
		
		DropDownChoice<CodeValue> choice = new DropDownChoice<CodeValue>("opts.".concat(si));
		
		if (i == 0) {
			choice.setChoices(billerProductLookup.getChildrenAsCodeValue(rootId));
			labels[i] = billerProductLookup.get(rootId).getLabel();
			
		} else {
			choice.setChoices(Collections.<CodeValue> emptyList());
			
			labels[i] = ""; 
		}
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
				
				//temp selected Label
				if(i==0)
				{	
					isTelePhonaBillPay = (root.getLabel().equals("Handphone") || root.getLabel().equals("Telepon"));
				}	
			}
		});
		
		opts[i] = null;
		
		container.add(label);
		container.add(choice);
		
		containers[i] = container;
		choices[i] = choice;
		
		frm.add(container);
	}
	
	/**
	 * This is class for displaying favourite list or not based on selection.
	 */
	private class FavouriteViewChoiceComponentUpdatingBehavior extends AjaxFormChoiceComponentUpdatingBehavior {

		private static final long serialVersionUID = 1L;

		@Override
		protected void onUpdate(AjaxRequestTarget target) {
			final boolean isManual = billPayBean.getManualOrFavourite().equals(BtpnConstants.BILLPAYMENT_MANUALLY);
			manualContainer.setVisible(isManual);
			favouriteContainer.setVisible(!isManual);
			target.addComponent(favouriteContainer);
			target.addComponent(manualContainer);
		}
	}
	
	/**
	 * This is used to add submit button.
	 */
	private void addSubmitButton(final Form<BillPaymentPage> form) {
		form.add(new Button("btnSubmit") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
//				/String rootId = null;
				String finalId = null;
				
				for (int i = 0; i < N; ++i) {
					if (opts[i] != null) {
						//rootId = opts[0].getId(); 
						List<CodeValue> children = billerProductLookup.getChildrenAsCodeValue(opts[i].getId());
						
						if ((children == null) || children.isEmpty()) {
							finalId = opts[i].getId();
						}
					}
				}
				
				if (finalId == null) {
					error(BillPaymentPage.this.getLocalizer().getString("product.required", BillPaymentPage.this));
				} 
				else if (!performBillPayValidations()) {
					return;
				}
				else {
					
					BillerProduct billerProduct = billerProductLookup.get(finalId);
					billPayBean.setBillerId(billerProduct.getBillerId());
					billPayBean.setProductId(billerProduct.getProductId());
					billPayBean.setLabel(billerProduct.getLabel());
					billPayBean.setProductName(billerProduct.getStrBiller());
					
					setResponsePage(new ConfirmBillPaymentPage(billPayBean));
				}
			}
		}.setDefaultFormProcessing(true));
	}
	
	
	/**
	 * This method is used for performing bill payment validations
	 */
	public boolean performBillPayValidations() {
		return true;
	}

}
