package com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.web.btpn.bank.beans.BankCashinBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.BankStaffBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.SampleMenuBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.SampleMenuListPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.registration.BankAdminRegistrationConfirmPage;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the Manage Products page for bank portals.
 * 
 * @author Vikram Gunda
 */
public class TestSampleMenuPage extends BtpnBaseBankPortalSelfCarePage {

	private SampleMenuBean sampleMenuBean;

	private static final Logger LOG = LoggerFactory.getLogger(TestSampleMenuPage.class);

	/**
	 * Constructor for this page.
	 */
	public TestSampleMenuPage() {
		super();
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	@Override
	protected void initOwnPageComponents() {
		super.initOwnPageComponents();
		constructPage();
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	private void constructPage() {
		final Form<TestSampleMenuPage> form = new Form<TestSampleMenuPage>("sampleForm",
			new CompoundPropertyModel<TestSampleMenuPage>(this));
		form.add(new FeedbackPanel("errorMessages"));
		form.add(new Label("sampleMenuBean.value"));
		form.add(new RequiredTextField<String>("sampleMenuBean.username"));
		
		form.add(new AjaxButton("submitButton") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				if (!PortalUtils.exists(sampleMenuBean)) {
					sampleMenuBean = new SampleMenuBean();
				}
				setResponsePage( new SampleMenuListPage(sampleMenuBean));
			};

			
		});
		
		add(form);
	}

}
