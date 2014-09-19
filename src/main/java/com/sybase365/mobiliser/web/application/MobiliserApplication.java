package com.sybase365.mobiliser.web.application;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebRequestCycleProcessor;
import org.apache.wicket.protocol.http.request.CryptedUrlWebRequestCodingStrategy;
import org.apache.wicket.protocol.http.request.WebRequestCodingStrategy;
import org.apache.wicket.request.IRequestCodingStrategy;
import org.apache.wicket.request.IRequestCycleProcessor;
import org.apache.wicket.resource.loader.IStringResourceLoader;
import org.apache.wicket.settings.IExceptionSettings;
import org.apache.wicket.settings.IResourceSettings;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.sybase365.mobiliser.util.tools.wicketutils.PathStripperLocator;
import com.sybase365.mobiliser.web.application.model.IMobiliserApplication;
import com.sybase365.mobiliser.web.application.model.IMobiliserAuthenticatedApplication;
import com.sybase365.mobiliser.web.application.model.IMobiliserSignupApplication;
import com.sybase365.mobiliser.web.application.model.IMobiliserUnauthenticatedApplication;
import com.sybase365.mobiliser.web.application.pages.ApplicationLoginPage;
import com.sybase365.mobiliser.web.application.pages.ApplicationStartPage;
import com.sybase365.mobiliser.web.application.pages.TechnicalErrorPage;
import com.sybase365.mobiliser.web.util.Configuration;
import com.sybase365.mobiliser.web.util.MobiliserWebSession;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * Application object for your web application. If you want to run this application without deploying, run the Start
 * class.
 * 
 * @see com.sybase.365.mobiliser.web.Start#main(String[])
 */
public class MobiliserApplication extends AuthenticatedWebApplication {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MobiliserApplication.class);

	private Class applicationSigninPage;

	private Class applicationHomePage;

	private List<IMobiliserAuthenticatedApplication> authenticatedApplications;

	private List<IMobiliserUnauthenticatedApplication> unauthenticatedApplications;

	private List<IMobiliserSignupApplication> signupApplications;

	private List<IStringResourceLoader> stringResourceLoaders;

	private Configuration configuration;

	private List<IMobiliserApplication> allApplications;

	public static Version VERSION;

	private Set<String> ownAuthenticationRequired;

	/**
	 * Constructor
	 */
	public MobiliserApplication() {
		VERSION = new Version();
	}

	public void setAuthenticatedApplications(List<IMobiliserAuthenticatedApplication> values) {
		this.authenticatedApplications = values;
		addToAllApplications(values);
	}

	public List<IMobiliserAuthenticatedApplication> getAuthenticatedApplications() {
		if (this.authenticatedApplications != null) {
			return this.authenticatedApplications;
		}
		return Collections.EMPTY_LIST;
	}

	public void setUnauthenticatedApplications(List<IMobiliserUnauthenticatedApplication> values) {
		this.unauthenticatedApplications = values;
		addToAllApplications(values);
	}

	public List<IMobiliserUnauthenticatedApplication> getUnauthenticatedApplications() {
		if (this.unauthenticatedApplications != null) {
			return this.unauthenticatedApplications;
		}
		return Collections.EMPTY_LIST;
	}

	public void setSignupApplications(List<IMobiliserSignupApplication> values) {
		this.signupApplications = values;
		addToAllApplications(values);
	}

	public List<IMobiliserSignupApplication> getSignupApplications() {
		if (this.signupApplications != null) {
			return this.signupApplications;
		}
		return Collections.EMPTY_LIST;
	}

	private void addToAllApplications(List values) {
		if (null == allApplications) {
			allApplications = new ArrayList<IMobiliserApplication>();
		}
		for (Object app : values) {
			allApplications.add((IMobiliserApplication) app);
		}
	}

	public List<IMobiliserApplication> getAllApplications() {
		return this.allApplications;
	}

	public void setApplicationSigninPage(Class value) {
		this.applicationSigninPage = value;
	}

	public Class getApplicationSigninPage() {
		if (this.applicationSigninPage != null) {
			return this.applicationSigninPage;
		}
		return ApplicationLoginPage.class;
	}

	public void setApplicationHomePage(Class value) {
		this.applicationHomePage = value;
	}

	public Class getApplicationHomePage() {
		if (this.applicationHomePage != null) {
			return this.applicationHomePage;
		}
		return ApplicationStartPage.class;
	}

	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	@Override
	public Class<? extends WebPage> getHomePage() {
		return getApplicationHomePage();
	}

	/**
	 * @see org.apache.wicket.authentication.AuthenticatedWebApplication#getSignInPageClass()
	 */
	@Override
	protected Class<? extends WebPage> getSignInPageClass() {
		return getApplicationSigninPage();
	}

	private void mountBookmarkablePages(List<IMobiliserApplication> apps) {

		for (IMobiliserApplication app : apps) {

			if (PortalUtils.exists(app.getBookmarkablePages())) {
				for (Map.Entry<String, Class> entry : app.getBookmarkablePages().entrySet()) {
					LOG.debug("Mounting bookmarkable page: {} -> {}", entry.getKey(), entry.getValue()
						.getCanonicalName());
					mountBookmarkablePage(entry.getKey(), entry.getValue());
				}
			} else {
				LOG.debug("No bookmarkable pages for this application");
			}

		}
	}

	/**
	 * @see org.apache.wicket.Application#init()
	 */
	@Override
	protected void init() {
		getDebugSettings().setComponentUseCheck(false);
		
		super.init();

		if (PortalUtils.exists(allApplications)) {
			mountBookmarkablePages(allApplications);
		}

		mountBookmarkablePage("/login", getSignInPageClass());

		getMarkupSettings().setStripWicketTags(true);

		// override some settings
		IResourceSettings settings = getResourceSettings();

		// add string resource loaders
		// e.g. to resolve the name of a lookup value or to show a missing
		// resource's key instead of throwing an exception
		Integer cacheDuration = getConfiguration().getResourceCacheDefaultDuration();
		if (cacheDuration != null) {
			settings.setDefaultCacheDuration(cacheDuration.intValue());
		}
		if (getStringResourceLoaders() != null) {
			for (IStringResourceLoader stringResourceLoader : getStringResourceLoaders()) {
				settings.addStringResourceLoader(stringResourceLoader);
			}
		}

		// reduce the application path to filename
		settings.setResourceStreamLocator(new PathStripperLocator());

		// change the web application's resource folders
		settings.addResourceFolder("html");
		settings.addResourceFolder("xml");

		// set the request and response encoding to UTF-8
		getRequestCycleSettings().setResponseRequestEncoding("utf-8");
		getApplicationSettings().setInternalErrorPage(TechnicalErrorPage.class);

		getExceptionSettings().setUnexpectedExceptionDisplay(IExceptionSettings.SHOW_INTERNAL_ERROR_PAGE);

		addComponentInstantiationListener(getSpringInjector());

		getApplicationSettings().setPageExpiredErrorPage(getSignInPageClass());

		getApplicationSettings().setAccessDeniedPage(getSignInPageClass());
	}

	protected SpringComponentInjector getSpringInjector() {
		return new SpringComponentInjector(this);
	}

	/**
	 * @see org.apache.wicket.authentication.AuthenticatedWebApplication#getWebSessionClass()
	 */
	@Override
	protected Class<? extends AuthenticatedWebSession> getWebSessionClass() {
		return MobiliserWebSession.class;
	}

	@Override
	protected IRequestCycleProcessor newRequestCycleProcessor() {
		return new WebRequestCycleProcessor() {
			@Override
			protected IRequestCodingStrategy newRequestCodingStrategy() {
				return new CryptedUrlWebRequestCodingStrategy(new WebRequestCodingStrategy());
			}
		};
	}

	/**
	 * Called when an AUTHENTICATED user tries to navigate to a page that they are not authorized to access. This could
	 * be a user logged into one application trying to access a bookmarkable page of another application, for example.
	 * 
	 * @param page The page
	 */
	@Override
	protected void onUnauthorizedPage(final Page page) {
		// restart at application login page which will decide the home page
		// of the applicaiton that the user IS allowed to access
		LOG.debug("UnauthorizedInstantiationException -> restart response at ApplicationStartPage");
		throw new RestartResponseAtInterceptPageException(ApplicationStartPage.class);
	}

	protected List<IStringResourceLoader> getStringResourceLoaders() {
		return stringResourceLoaders;
	}

	public void setStringResourceLoaders(List<IStringResourceLoader> stringResourceLoaders) {
		this.stringResourceLoaders = stringResourceLoaders;
	}

	protected Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	/**
	 * @return the ownAuthenticationRequired
	 */
	public Set<String> getOwnAuthenticationRequired() {
		return ownAuthenticationRequired;
	}

	/**
	 * @param ownAuthenticationRequired the ownAuthenticationRequired to set
	 */
	public void setOwnAuthenticationRequired(Set<String> ownAuthenticationRequired) {
		this.ownAuthenticationRequired = ownAuthenticationRequired;
	}

	public class Version {

		public String NAME = "unknown";

		public String VERSION = "unknown";

		public String TAG = "unknown";

		public String DATE = "unknown";

		public String REVISION = "unknown";

		private Version() {
			try {
				LOG.debug("Loading version information from version.xml...");

				URL versionUrl = MobiliserApplication.class.getResource("version.xml");

				if (versionUrl != null) {
					SAXReader versionReader = new SAXReader(false);
					Document versionDoc = versionReader.read(versionUrl);
					Element rootElement = versionDoc.getRootElement();

					NAME = rootElement.elementText("name");
					VERSION = rootElement.elementText("version");
					DATE = rootElement.elementText("date");
					TAG = rootElement.elementText("tag");
					REVISION = rootElement.elementText("revision");

					// if tag not supplied by hudson, indicate a developer build
					if (TAG.equals("${BUILD_TAG}")) {
						TAG = "Manual Build";
					}

					// if tag not supplied by hudson, indicate a developer build
					if (REVISION.equals("${SVN_REVISION}")) {
						REVISION = "Working Copy";
					}

					LOG.info("About: {} {} {} {} {}", new Object[] { NAME, VERSION, DATE, TAG, REVISION });
				}
			} catch (Throwable th) {
				LOG.error("Couldn't initialise application about info", th);
			}
		}
	}

}