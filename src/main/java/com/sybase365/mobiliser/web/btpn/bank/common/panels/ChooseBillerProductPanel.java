package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import java.util.Collections;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.portal.common.util.BillerProductLookup;
import com.btpnwow.portal.common.util.BillerProductLookup.BillerProduct;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;

/**
 * This is the Manage Fee Add Details Panel for fixed, slab, sharing fee.
 * 
 * @author Vikram Gunda
 */
public class ChooseBillerProductPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(ChooseBillerProductPanel.class);
	
	private static final int N = 5;

	@SpringBean(name = "billerProductLookup")
	private BillerProductLookup billerProductLookup;
	
	private final BtpnMobiliserBasePage basePage;

	private final Class<Page> nextPage;

	private String[] labels;
	
	private CodeValue[] opts;
	
	private FeedbackPanel feedback;
	
	private WebMarkupContainer[] containers;
	
	private DropDownChoice<CodeValue>[] choices;

	private Button btnNext;
	
	@SuppressWarnings("unchecked")
	public ChooseBillerProductPanel(String id, BtpnMobiliserBasePage basePage, Class<Page> nextPage) {
		super(id);
		
		this.basePage = basePage;
		this.nextPage = nextPage;
		
		this.labels = new String[N];
		this.opts = new CodeValue[N];
		
		this.containers = new WebMarkupContainer[N];
		this.choices = new DropDownChoice[N];
		
		constructPanel();
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
			
			labels[i] = billerProductLookup.get(rootId).getPromptLabel();
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
					
					btnNext.setVisible(opts[i] != null);
				} else {
					opts[i + 1] = null;
					labels[i + 1] = root.getPromptLabel();
					
					choices[i + 1].setChoices(children);
					containers[i + 1].setVisible(true);
					
					btnNext.setVisible(false);
				}
				
				target.addComponent(btnNext);
				
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
		
		opts[i] = null;
		
		container.add(label);
		container.add(choice);
		
		containers[i] = container;
		choices[i] = choice;
		
		frm.add(container);
	}
	
	private void constructPanel() {
		final Form<ChooseBillerProductPanel> form = new Form<ChooseBillerProductPanel>(
				"chooseBillerProductForm", new CompoundPropertyModel<ChooseBillerProductPanel>(this));

		form.add((feedback = new FeedbackPanel("errorMessages"))
				.setOutputMarkupId(true));
		
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
		
		(btnNext = new AjaxButton("btnNext") {
			
			private static final long serialVersionUID = 1L;

			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				String finalId = null;
				
				for (int i = 0; i < N; ++i) {
					if (opts[i] != null) {
						List<CodeValue> children = billerProductLookup.getChildrenAsCodeValue(opts[i].getId());
						
						if ((children == null) || children.isEmpty()) {
							finalId = opts[i].getId();
						}
					}
				}
				
				if (finalId == null) {
					error(ChooseBillerProductPanel.this.getLocalizer().getString("product.required", ChooseBillerProductPanel.this));
					
					target.addComponent(feedback);
				} else {
					basePage.getMobiliserWebSession().put("billerProduct", billerProductLookup.get(finalId));
					
					setResponsePage(nextPage);
				}
			}
		}
		.setDefaultFormProcessing(true))
		.setOutputMarkupPlaceholderTag(true)
		.setVisible(false);
		
		form.add(btnNext);
				
		add(form);
	}
}
