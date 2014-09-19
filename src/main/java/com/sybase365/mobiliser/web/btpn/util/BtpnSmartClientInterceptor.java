package com.sybase365.mobiliser.web.btpn.util;

import java.lang.reflect.Method;

import javax.annotation.security.RolesAllowed;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.ws.client.WebServiceTransportException;

/**
 * {@link MethodInterceptor} which creates a web service client using the credentials from Spring Security's thread
 * local bound {@link Authentication} object. This is just a hack until the chaotic spring configuration of the wicket
 * components is properly handled. Each page should declare its dependencies and there is should be clear if it needs a
 * system client or an agent authenticated one.
 * <p>
 * &copy; 2012 by Sybase, Inc.
 * </p>
 * 
 * @author <a href='mailto:vikram.gunda@sybase.com'>Vikam Gunda</a>
 */
public final class BtpnSmartClientInterceptor implements MethodInterceptor, InitializingBean {

	/** logger. */
	private static final Logger LOG = LoggerFactory.getLogger(BtpnSmartClientInterceptor.class);

	private static final ThreadLocal<Boolean> selfAuthenticationMarker = new ThreadLocal<Boolean>();

	/**
	 * Force self authentication for the current thread.
	 */
	public static final void forceSelfAuthentication() {
		selfAuthenticationMarker.set(Boolean.TRUE);
	}

	/**
	 * Whether self authentication is currently forced for this thread even if the session does not contain a self
	 * authentication marker.
	 * 
	 * @return if self authentication is forced
	 */
	public static final boolean isSelfAuthenticationForced() {
		Boolean val = selfAuthenticationMarker.get();

		return val != null && val.booleanValue();
	}

	/**
	 * Force self authentication for the current thread.
	 */
	public static final void unsetForcedSelfAuthentication() {
		selfAuthenticationMarker.remove();
	}

	private Object selfAuthTarget;

	private Object systemAuthTarget;

	@Override
	public void afterPropertiesSet() {
		if (this.selfAuthTarget == null) {
			throw new IllegalStateException("selfAuthTarget is required");
		}

		if (this.systemAuthTarget == null) {
			throw new IllegalStateException("systemAuthTarget array is required");
		}
	}

	/**
	 * Equality means targets are equal.
	 */
	@Override
	public boolean equals(final Object other) {
		if (other == this) {
			return true;
		}

		if (other == null) {
			return false;
		}

		final BtpnSmartClientInterceptor otherProxy;
		if (other instanceof BtpnSmartClientInterceptor) {
			otherProxy = (BtpnSmartClientInterceptor) other;
		} else {
			// Not a valid comparison...
			return false;
		}

		return this.selfAuthTarget.equals(otherProxy.selfAuthTarget)
				&& this.systemAuthTarget.equals(otherProxy.systemAuthTarget);
	}

	/**
	 * Proxy uses the hash code of the targets.
	 */
	@Override
	public int hashCode() {
		return BtpnSmartClientInterceptor.class.hashCode() * 13 + this.selfAuthTarget.hashCode()
				+ this.systemAuthTarget.hashCode();
	}

	@Override
	public Object invoke(final MethodInvocation invocation) throws Throwable {
		if (AopUtils.isEqualsMethod(invocation.getMethod())) {
			return Boolean.valueOf(equals(invocation.getArguments()[0]));
		}

		if (AopUtils.isHashCodeMethod(invocation.getMethod())) {
			return Integer.valueOf(hashCode());
		}

		if (AopUtils.isToStringMethod(invocation.getMethod())) {
			return toString();
		}

		final boolean forced = isSelfAuthenticationForced();

		if (!forced && !Session.exists()) {
			return invoke(invocation, this.systemAuthTarget);
		}

		if (forced
				|| (Session.exists() && Session.get() instanceof BtpnMobiliserWebSession && ((BtpnMobiliserWebSession) Session
					.get()).isSelfAuthenticationRequired())) {

			final Authentication authentication = ((BtpnMobiliserWebSession) Session.get()).getAuthentication();

			if (authentication == null || !authentication.isAuthenticated()
					|| !(authentication.getPrincipal() instanceof BtpnCustomer)) {

				LOG.warn("Application requires self authentication but " + "customer has no authentication in the "
						+ "security context. Method: " + invocation.getMethod()
						+ " Will be called with a system client.", new RuntimeException());

				return invoke(invocation, this.systemAuthTarget);
			}

			try {
				return invoke(invocation, this.selfAuthTarget);
			} catch (final WebServiceTransportException e) {
				if (StringUtils.indexOf(e.getMessage(), "Access is denied [403]") == StringUtils.INDEX_NOT_FOUND
						&& StringUtils.indexOf(e.getMessage(), "Forbidden [403]") == StringUtils.INDEX_NOT_FOUND) {
					throw e;
				}

				warnAuthentication(invocation, authentication, e);

				return invoke(invocation, this.systemAuthTarget);
			} catch (final AccessDeniedException e) {
				warnAuthentication(invocation, authentication, e);

				return invoke(invocation, this.systemAuthTarget);
			}
		}

		return invoke(invocation, this.systemAuthTarget);

	}

	private Object invoke(final MethodInvocation invocation, final Object target) throws Throwable {

		return AopUtils.invokeJoinpointUsingReflection(target, invocation.getMethod(), invocation.getArguments());
	}

	/**
	 * @param selfAuthTarget the selfAuthTarget to set
	 */
	public void setSelfAuthTarget(final Object selfAuthTarget) {
		this.selfAuthTarget = selfAuthTarget;
	}

	/**
	 * @param systemAuthTarget the systemAuthTarget to set
	 */
	public void setSystemAuthTarget(final Object systemAuthTarget) {
		this.systemAuthTarget = systemAuthTarget;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("SmartClientInterceptor [selfAuthTarget=");
		stringBuilder.append(this.selfAuthTarget);
		stringBuilder.append(", systemAuthTarget=");
		stringBuilder.append(this.systemAuthTarget);
		stringBuilder.append(']');

		return stringBuilder.toString();
	}

	private void warnAuthentication(final MethodInvocation invocation, final Authentication authentication,
		final Exception e) {

		final Method method = invocation.getMethod();

		final RolesAllowed annotation = method.getAnnotation(RolesAllowed.class);

		if (annotation == null) {
			LOG.trace("WARNING!!!!! Agent ({}) does not possess required " + "privileges to invoke method {}"
					+ ". You should consider refactoring this " + "wicket page to properly configure the needed "
					+ "clients. Falling back to system auth client " + "[Exception: {}]",
				new Object[] { authentication.getName(), invocation.getMethod(), e.toString() });
		} else {
			LOG.trace("WARNING!!!!! Agent ({}) does not possess required " + "privileges ({}) to invoke method {}"
					+ ". You should consider refactoring this " + "wicket page to properly configure the needed "
					+ "clients. Falling back to system auth client " + "[Exception: {}]",
				new Object[] { authentication.getName(), StringUtils.join(annotation.value()), invocation.getMethod(),
						e.toString() });
		}
	}
}
