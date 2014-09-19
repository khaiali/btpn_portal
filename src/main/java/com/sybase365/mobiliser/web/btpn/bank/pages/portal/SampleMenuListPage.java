package com.sybase365.mobiliser.web.btpn.bank.pages.portal;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.web.btpn.bank.beans.SampleMenuBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.TestSampleMenuPage;

public class SampleMenuListPage extends BtpnBaseBankPortalSelfCarePage {
	private static final long serialVersionUID = 1L;

	SampleMenuBean sampleMenuBean;

	/**
	 * Default Constructor for this page.
	 */
	public SampleMenuListPage() {
		super();
	}

	/**
	 * Constructor for this page.
	 * 
	 * @param customer customer Object
	 */
	public SampleMenuListPage(PageParameters parameters) {
		super();
		constructPage();
	}

	public SampleMenuListPage(SampleMenuBean sampleMenuBean) {
		super();
		this.sampleMenuBean = sampleMenuBean;
		constructPage();
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	private void constructPage() {
		final Form<SampleMenuListPage> form = new Form<SampleMenuListPage>("sampleResultForm",
			new CompoundPropertyModel<SampleMenuListPage>(this));
		form.add(new FeedbackPanel("errorMessages"));
		form.add(new Label("username", sampleMenuBean.getUsername()));
		
		// Add Back button
		form.add(new Button("submitBack") {
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit() {
				setResponsePage(new TestSampleMenuPage());
			};
		}.setDefaultFormProcessing(false));
		
		add(form);
	}

	
}
