package com.sybase365.mobiliser.web.btpn.util;

import org.apache.wicket.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.TargetSource;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.Authentication;
import org.springframework.util.ClassUtils;

import com.sybase365.mobiliser.util.tools.clientutils.api.IClientConfiguration;
import com.sybase365.mobiliser.util.tools.clientutils.api.IServiceClientFactory;
import com.sybase365.mobiliser.util.tools.wicketutils.security.Customer;
import com.sybase365.mobiliser.web.btpn.bank.beans.Credential;
import com.sybase365.mobiliser.web.util.SmartClientInterceptor;

/**
 * {@link TargetSource} which creates a web service client using the credentials from Spring Security's thread local
 * bound {@link Authentication} object.
 * <p>
 * &copy; 2012 by Sybase, Inc.
 * </p>
 * 
 * @author <a href='mailto:vikram.gunda@sap.com'>Vikram Gunda</a>
 */
public final class BtpnRoleAwareClientTargetSource implements TargetSource, InitializingBean, BeanClassLoaderAware {

	/** logger. */
	private static final Logger LOG = LoggerFactory.getLogger(BtpnRoleAwareClientTargetSource.class);

	private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

	private IServiceClientFactory clientFactory;

	private Class<?>[] clientInterfaces;

	private IClientConfiguration configuration;

	private String endpointSuffix;

	@Override
	public void afterPropertiesSet() {
		if (this.clientFactory == null) {
			throw new IllegalStateException("clientFactory is required");
		}

		if (this.clientInterfaces == null) {
			throw new IllegalStateException("clientInterfaces array is required");
		}

		if (this.configuration == null) {
			throw new IllegalStateException("configuration is required");
		}

	}

	@Override
	public Object getTarget() throws Exception {
		final boolean forced = SmartClientInterceptor.isSelfAuthenticationForced();

		if (!forced && !Session.exists()) {
			throw new IllegalStateException("Application misconfigured. This client requires a logged in user.");
		}

		if (forced
				|| (Session.exists() && Session.get() instanceof BtpnMobiliserWebSession && ((BtpnMobiliserWebSession) Session
					.get()).isSelfAuthenticationRequired())) {

			final Authentication authentication = ((BtpnMobiliserWebSession) Session.get()).getAuthentication();

			if (authentication == null || !authentication.isAuthenticated()
					|| !(authentication.getPrincipal() instanceof Customer)) {
				throw new IllegalStateException("User has not been authenticated");
			}

			final BtpnCustomer token = (BtpnCustomer) authentication.getPrincipal();
			//Credentials
			Object credentials = authentication.getCredentials();
			final String pin;
			if(credentials instanceof Credential){
				pin = ((Credential) credentials).getCredential();
			}else{
				pin = (String) credentials;
			}

			return this.clientFactory.createClient(this.clientInterfaces, this.configuration.getMobiliserEndpointUrl()
					+ (this.endpointSuffix == null ? "" : this.endpointSuffix), token.getUsername(),
				String.valueOf(pin), this.beanClassLoader);
		}

		throw new IllegalStateException("Application misconfigured. This client requires a logged in user.");

	}

	@Override
	public Class<?> getTargetClass() {
		return null;
	}

	@Override
	public boolean isStatic() {
		return false;
	}

	@Override
	public void releaseTarget(final Object target) throws Exception {
		LOG.debug("destroy() called");

		if (target instanceof DisposableBean) {
			LOG.debug("current target is destroyable...");
			((DisposableBean) target).destroy();
		} else {
			LOG.debug("current target is not destroyable...");
		}
	}

	@Override
	public void setBeanClassLoader(final ClassLoader classLoader) {
		this.beanClassLoader = classLoader;
	}

	/**
	 * @param clientFactory the clientFactory to set
	 */
	public void setClientFactory(final IServiceClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

	/**
	 * @param clientInterface the clientInterface to set
	 */
	public void setClientInterface(final Class<?> clientInterface) {
		setClientInterfaces(new Class[] { clientInterface });
	}

	/**
	 * @param clientInterfaces the clientInterfaces to set
	 */
	public void setClientInterfaces(final Class<?>[] clientInterfaces) {
		this.clientInterfaces = clientInterfaces;
	}

	/**
	 * @param configuration the configuration to set
	 */
	public void setConfiguration(final IClientConfiguration configuration) {
		this.configuration = configuration;
	}

	/**
	 * @param endpointSuffix the endpointSuffix to set
	 */
	public void setEndpointSuffix(final String endpointSuffix) {
		this.endpointSuffix = endpointSuffix;
	}
}
