package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;

import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageFeeBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageFeeDetailsBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee.ManageFeePage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnLocalizableLookupDropDownChoice;

/**
 * This is the Manage Fee page for bank portals.
 * 
 * @author Vikram Gunda
 */
public class ManageFeeSubDetailsPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private BtpnMobiliserBasePage mobBasePage;

	private ManageFeeBean feeBean;

	private ManageFeeDetailsBean detailsBean;

	/**
	 * Constructor for this page.
	 */
	public ManageFeeSubDetailsPanel(String id, BtpnMobiliserBasePage mobBasePage, final ManageFeeBean feeBean,
		final ManageFeeDetailsBean detailsBean) {
		super(id);
		this.mobBasePage = mobBasePage;
		this.feeBean = feeBean;
		this.detailsBean = detailsBean;
		constructPanel();
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	private void constructPanel() {
		Form<ManageFeeSubDetailsPanel> form = new Form<ManageFeeSubDetailsPanel>("feeForm",
			new CompoundPropertyModel<ManageFeeSubDetailsPanel>(this));
		form.add(new FeedbackPanel("errorMessages"));
		form.add(new Label("feeBean.useCaseName"));
		form.add(new CheckBox("feeBean.applyToPayee"));
		form.add(new Label("feeBean.feeType"));
		form.add(new Label("feeBean.productName"));
		form.add(new RequiredTextField<String>("feeBean.transactionAmount"));
		form.add(new Label("detailsBean.feeName"));
		RadioGroup<String> rg = new RadioGroup<String>("detailsBean.amountType");
		rg.add(new Radio<String>("radio.fixed", Model.of(getLocalizer().getString("label.fixed", this))));
		rg.add(new Radio<String>("radio.percent", Model.of(getLocalizer().getString("label.percentage", this))));
		form.add(rg);
		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("detailsBean.glCode", CodeValue.class,
			BtpnConstants.RESOURCE_BUNDLE_BTPN_GL_CODES, this, Boolean.FALSE, true)
			.setNullValid(false)
			.setChoiceRenderer(
				new ChoiceRenderer<CodeValue>(BtpnConstants.DISPLAY_EXPRESSION, BtpnConstants.ID_EXPRESSION))
			.setRequired(true).add(new ErrorIndicator()));
		form.add(new RequiredTextField<String>("detailsBean.amount"));
		form.add(addUpdateButton());
		form.add(addCancelButton());
		// Add add Button
		add(form);
	}

	/**
	 * This method adds the Add button for the Manage Products
	 */
	protected Button addUpdateButton() {
		Button submitButton = new Button("btnUpdate") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				mobBasePage.getWebSession().info(getLocalizer().getString("feesubdetails.update.success", this));
				setResponsePage(ManageFeePage.class);
			}
		};
		return submitButton;
	}

	/**
	 * This method adds the Add button for the Manage Products
	 */
	protected Button addCancelButton() {
		Button submitButton = new Button("btnCancel") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(ManageFeePage.class);
			}
		};
		submitButton.setDefaultFormProcessing(false);
		return submitButton;
	}
}
