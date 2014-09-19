package com.sybase365.mobiliser.web.btpn.bank.pages.portal.registration;

import org.apache.wicket.PageParameters;

import com.sybase365.mobiliser.web.btpn.bank.beans.CustomerRegistrationBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.RegistrationMobileNumberPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;

/**
 * This is the RegistrationMobileNumberPage page for bank portals for registration of Consumers.
 * 
 * @author Vikram Gunda
 */
public class ConsumerRegistrationMobileNumberPage extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	private CustomerRegistrationBean customer;

	/**
	 * Default Constructor for this page.
	 */
	public ConsumerRegistrationMobileNumberPage() {
		super();
		initPageComponents();
	}

	/**
	 * Constructor for this page.
	 * 
	 * @param customer customer Object
	 */
	public ConsumerRegistrationMobileNumberPage(CustomerRegistrationBean customer) {
		super();
		this.customer = customer;
		initPageComponents();
	}

	/**
	 * Constructor for this page.
	 * 
	 * @param parameters Page Parameters for this page
	 */
	public ConsumerRegistrationMobileNumberPage(final PageParameters parameters) {
		super(parameters);
	}

	/**
	 * Initialize the Components.
	 */
	protected void initPageComponents() {
		initializeCustomerBean();
		add(new RegistrationMobileNumberPanel("registrationMobileNumberPanel", this, this.customer));
	}

	/**
	 * Initialize the Consumer bean.
	 */
	private void initializeCustomerBean() {
		customer = new CustomerRegistrationBean();
		// set default registration type as Consumer
//		customer.setRegistrationType(new CodeValue(getLocalizer().getString("registrationType.default.key", this),
//			getLocalizer().getString("registrationType.default.value", this)));
		// set default nationality
//		customer.setNationality(new CodeValue(getLocalizer().getString("nationality.default.key", this), getLocalizer()
//			.getString("nationality.default.value", this)));
		// set default Reciept Mode
//		customer.setReceiptMode(new CodeValue(getLocalizer().getString("recieptmode.default.key", this), getLocalizer()
//			.getString("recieptmode.default.value", this)));
//		customer.setOptimaActivated(new CodeValue(getLocalizer().getString("optimaactivated.default.key", this),
//			getLocalizer().getString("optimaactivated.default.value", this)));
		
		customer.setCustomerType(BtpnConstants.REG_CONSUMER);
	}
}
