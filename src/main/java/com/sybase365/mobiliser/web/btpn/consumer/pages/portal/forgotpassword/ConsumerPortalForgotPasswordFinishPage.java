package com.sybase365.mobiliser.web.btpn.consumer.pages.portal.forgotpassword;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.application.pages.ConsumerPortalApplicationLoginPage;

/**
 * This class is the page which need to forgot password Success.
 * 
 * @author Narasa Reddy
 */
public class ConsumerPortalForgotPasswordFinishPage extends BtpnMobiliserBasePage {

	private static final long serialVersionUID = 1L;

	public ConsumerPortalForgotPasswordFinishPage() {
		super();
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	@Override
	protected void initOwnPageComponents() {
		createLoginHeader();
		createForm();
	}

	private void createLoginHeader() {
		add(new Link<String>("loginLogoLink") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(ConsumerPortalApplicationLoginPage.class);
			}
		});
	}

	/**
	 * This method should be used to create the forgot password Success forms and its components.
	 */
	private void createForm() {
		Form<ConsumerPortalForgotPasswordFinishPage> form = new Form<ConsumerPortalForgotPasswordFinishPage>(
			"forgotPasswordFinishForm", new CompoundPropertyModel<ConsumerPortalForgotPasswordFinishPage>(this));

		// Add Error Messages
		form.add(new FeedbackPanel("errorMessages"));

		// Add submit button
		form.add(new Button("submitButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(ConsumerPortalApplicationLoginPage.class);
			}
		});

		add(form);
	}

}
