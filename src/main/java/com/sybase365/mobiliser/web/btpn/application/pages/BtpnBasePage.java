package com.sybase365.mobiliser.web.btpn.application.pages;

import java.util.Locale;

import org.apache.wicket.PageParameters;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebRequestCycle;
import org.apache.wicket.protocol.http.WebResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.framework.contract.v5_0.base.MobiliserRequestType;
import com.sybase365.mobiliser.util.tools.wicketutils.BaseWebSession;

/**
 * This class is the base page for all pages. Here we have initialization functionality and locale functionality
 * 
 * @author Vikram Gunda
 */
public abstract class BtpnBasePage extends WebPage {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(BtpnBasePage.class);

	/**
	 * Constructor that is invoked.
	 */
	public BtpnBasePage() {
		super();
		initPageComponents(null);
	}

	/**
	 * Constructor that is invoked.
	 * 
	 * @param parameters PageParameters for this page
	 */
	public BtpnBasePage(final PageParameters parameters) {
		super(parameters);
		initPageComponents(parameters);
	}

	/**
	 * Constructor that is invoked.
	 * 
	 * @return BaseWebSession webSession for Client
	 */
	public BaseWebSession getWebSession() {
		return (BaseWebSession) super.getSession();
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created. If privileges decide which elements are shown or not, this is the place
	 * to filter and manipulate the components. E.G. a group of input boxes should not be visible for customers lacking
	 * a certain privilege: group those boxes into a MarkupContainer component (if they are hierarchically composed) and
	 * set the visibility of this container component to false if a customer without the needed privileges logged in.
	 */
	protected final void initPageComponents(final PageParameters parameters) {
		LOG.debug("###BtpnBasePage:initPageComponents====> Start");
		initOwnPageComponents();
		LOG.debug("###BtpnBasePage:initPageComponents====> End");
	}

	protected abstract void initOwnPageComponents();

	/**
	 * Default behaviour method during page initialization.
	 * 
	 * @param parameters Page Paramteres that we send from one page to another.
	 */
	protected void initOwnPageComponents(final PageParameters parameters) {
		// no default behaviour
	}

	/**
	 * Disable browser back button cache.
	 */
	@Override
	protected void configureResponse() {
		super.configureResponse();
		final WebResponse response = (WebResponse) (((WebRequestCycle) RequestCycle.get()).getResponse());
		response.disableCaching();
		response.setHeader("Cache-Control", "no-cache, max-age=0, must-revalidate, no-store");
	}

	/**
	 * Method which sets the parameters in request i.e. in MobiliserRequestType
	 * 
	 * @param req Mobiliser Request Object for preparing the request
	 */
	protected void prepareMobiliserRequest(MobiliserRequestType req) {

	}

	/**
	 * Gets the locale for the given country and language
	 * 
	 * @param language Language for the locale
	 * @param country Country for the locale
	 * @param variant Variant for the locale
	 */
	protected Locale getLocale(String language, String country, String variant) {

		if (language == null || language.isEmpty()) {
			return null;
		}
		if (country == null || country.isEmpty()) {
			return new Locale(language);
		}
		if (variant == null || variant.isEmpty()) {
			return new Locale(language, country);
		}

		return new Locale(language, country, variant);
	}

	/**
	 * Updates the locale for the given language
	 * 
	 * @param language Mobiliser Request Object for preparing the request
	 * @return Locale Updated locale is returned
	 */
	protected Locale getUpdatedLocale(String lang) {
		String language = "";
		String country = "";
		String variant = "";
		final Locale oldLocale = getWebSession().getLocale();
		final Locale newLocale = parseLocale(lang);
		// check all field
		language = exists(newLocale.getLanguage()) ? newLocale.getLanguage() : oldLocale.getLanguage();
		country = exists(newLocale.getCountry()) ? newLocale.getCountry() : oldLocale.getCountry();
		variant = exists(newLocale.getVariant()) ? newLocale.getVariant() : oldLocale.getVariant();
		return new Locale(language, country, variant);
	}

	/**
	 * Updates the locale for the given language
	 * 
	 * @param locale Locale to be parsed
	 * @return Locale Updated locale is returned
	 */
	protected Locale parseLocale(String locale) {
		final String[] parts = locale.split("_|-");
		if (parts.length > 3) {
			throw new IllegalArgumentException("The String [" + locale + "] cannot be parsed as a Locale!");
		}
		return new Locale(parts[0], (parts.length > 1) ? parts[1] : "", (parts.length > 2) ? parts[2] : "");
	}

	/**
	 * Updates the locale for the given language
	 * 
	 * @param locale Locale to be parsed
	 * @return returns true if string is not null or not empty, or else returns false
	 */

	private boolean exists(String s) {
		return ((s != null) && (s.trim().length() > 0));
	}

}
