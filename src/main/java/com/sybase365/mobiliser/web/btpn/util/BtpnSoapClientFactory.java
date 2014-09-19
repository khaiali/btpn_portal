package com.sybase365.mobiliser.web.btpn.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.util.StringUtils;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapMessageFactory;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.transport.WebServiceMessageSender;
import org.springframework.ws.transport.http.CommonsHttpMessageSender;
import org.springframework.ws.transport.http.HttpUrlConnectionMessageSender;

import com.sybase365.mobiliser.util.tools.clientutils.api.ICookieConfiguration;
import com.sybase365.mobiliser.util.tools.clientutils.api.impl.AbstractClientFactory;
import com.sybase365.mobiliser.util.tools.clientutils.soap.impl.CookieAwareCommonsHttpMessageSender;
import com.sybase365.mobiliser.util.tools.clientutils.soap.impl.CookieExtractingSoapMessageFactory;
import com.sybase365.mobiliser.util.tools.clientutils.soap.impl.DynamicJaxb2Marshaller;
import com.sybase365.mobiliser.util.tools.clientutils.soap.impl.DynamicJaxb2Unmarshaller;
import com.sybase365.mobiliser.util.tools.clientutils.soap.impl.SoapMethodInterceptor;
import com.sybase365.mobiliser.web.util.Configuration;

/**
 * This implementation makes use of Spring-WS to create a <tt>WebServiceOperations</tt> which is used by a proxy object
 * to delegate method calls to the soap backend. You may configure a {@link SoapMessageFactory} to use with this factory
 * or a default one will be configured.
 * <p>
 * &copy; 2011 by Sybase Inc.
 * </p>
 * 
 * @author <a href='mailto:Andrew.Clemons@sybase.com'>Andrew Clemons</a>
 */
public final class BtpnSoapClientFactory extends AbstractClientFactory {

	private static final Logger LOG = LoggerFactory.getLogger(BtpnSoapClientFactory.class);

	private boolean acceptGzipEncoding = true;

	private boolean authenticationPreemptive = true;

	private final ConcurrentMap<Class<?>, Jaxb2Marshaller> jaxbMarshallers = new ConcurrentHashMap<Class<?>, Jaxb2Marshaller>();

	private final ThreadLocal<Marshaller> marshallers = new ThreadLocal<Marshaller>();

	private final DynamicJaxb2Marshaller marshaller = new DynamicJaxb2Marshaller(this.marshallers);

	private final SoapMessageFactory soapMessageFactory;

	private final ThreadLocal<Unmarshaller> unmarshallers = new ThreadLocal<Unmarshaller>();

	private final DynamicJaxb2Unmarshaller unmarshaller = new DynamicJaxb2Unmarshaller(this.unmarshallers);

	private Charset charset = Charset.forName("UTF-8");
	
	private Configuration configuration;

	/**
    */
	public BtpnSoapClientFactory() {
		super();
		final SaajSoapMessageFactory factory = new SaajSoapMessageFactory();
		factory.afterPropertiesSet();
		this.soapMessageFactory = factory;
	}

	/**
	 * @param soapMessageFactory
	 */
	public BtpnSoapClientFactory(final SoapMessageFactory soapMessageFactory) {
		super();
		this.soapMessageFactory = soapMessageFactory;
	}

	private void checkAfterPropertiesSet(final InitializingBean bean) {
		try {
			bean.afterPropertiesSet();
		} catch (final RuntimeException e) {
			throw e;
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected MethodInterceptor createMethodInterceptor(final String endpointUrl, final String userName,
		final String password, final ClassLoader classLoader, final ICookieConfiguration cookieConfiguration,
		final Map<String, String> requestParameters) {

		final WebServiceTemplate template = getWebServiceTemplate(endpointUrl, userName, password, cookieConfiguration,
			requestParameters);

		final SoapMethodInterceptor interceptor = new SoapMethodInterceptor(classLoader, this.jaxbMarshallers,
			this.marshaller, template, this.unmarshaller);

		final Logger log = LOG;
		return new MethodInterceptor() {

			@Override
			public Object invoke(final MethodInvocation invocation) throws Throwable {

				if (DisposableBean.class.isAssignableFrom(invocation.getMethod().getDeclaringClass())) {

					log.debug("Destroying WebServiceMessageSenders...");

					final WebServiceMessageSender[] messageSenders = template.getMessageSenders();

					if (messageSenders != null) {

						for (final WebServiceMessageSender sender : messageSenders) {
							if (sender instanceof DisposableBean) {
								try {
									((DisposableBean) sender).destroy();
								} catch (final Exception e) {
									log.warn("Failed to dispose of WebServiceMessageSender", e);
								}
							}
						}
					}

					return null;
				}

				return interceptor.invoke(invocation);
			}
		};

	}

	/**
	 * Create the actual template for calling the web service.
	 * 
	 * @param endpointUrl the url of the endpoint
	 * @param userName the user name
	 * @param password the password
	 * @return the template
	 */
	private WebServiceTemplate getWebServiceTemplate(final String endpointUrl, final String userName,
		final String password, final ICookieConfiguration cookieConfiguration,
		final Map<String, String> requestParameters) {

		final boolean useCookies = cookieConfiguration != null && cookieConfiguration.isUseCookies();

		final WebServiceTemplate template;
		if (useCookies) {

			LOG.trace("Cookies activated. Wrapping SoapFactory {} as a CookieExtractingSoapMessageFactory",
				this.soapMessageFactory);

			template = new WebServiceTemplate(new CookieExtractingSoapMessageFactory(cookieConfiguration,
				this.soapMessageFactory));
		} else {
			LOG.trace("Cookies not activated.");

			template = new WebServiceTemplate(this.soapMessageFactory);
		}

		final WebServiceMessageSender sender;
		if (StringUtils.hasLength(userName) || useCookies) {

			final CommonsHttpMessageSender messageSender;
			if (useCookies) {
				messageSender = new CookieAwareCommonsHttpMessageSender(cookieConfiguration);
				LOG.trace("Cookies activated. Generated message sender {}", messageSender);
			} else {
				messageSender = new CommonsHttpMessageSender();
				messageSender.setConnectionTimeout(this.configuration.getConnectionTimeOutInMillis());
				messageSender.setReadTimeout(this.configuration.getReadTimeOutInMillis());
			}

			if (StringUtils.hasLength(userName)) {
				messageSender.setCredentials(new UsernamePasswordCredentials(userName, password));
			}

			messageSender.setAcceptGzipEncoding(this.acceptGzipEncoding);

			checkAfterPropertiesSet(messageSender);

			if (useCookies) {

				LOG.trace("Deactivated preemptive authentication to allow " + "for cookie authentication.");

				// unset forced preemption with cookies
				messageSender.getHttpClient().getParams().setAuthenticationPreemptive(false);
			} else {
				// allow unsetting forced preemption
				messageSender.getHttpClient().getParams().setAuthenticationPreemptive(this.authenticationPreemptive);
			}

			sender = messageSender;
		} else {
			final HttpUrlConnectionMessageSender messageSender = new HttpUrlConnectionMessageSender();
			sender = messageSender;
		}

		template.setMessageSender(sender);

		final StringBuilder builder = new StringBuilder();

		final Iterator<Entry<String, String>> it = requestParameters.entrySet().iterator();

		while (it.hasNext()) {
			final Entry<String, String> entry = it.next();

			final String name = entry.getKey();
			final String value = entry.getValue();

			try {
				builder.append(URLEncoder.encode(name, this.charset.name()));

				if (value != null) {
					builder.append('=');
					builder.append(URLEncoder.encode(value, this.charset.name()));
				}

			} catch (UnsupportedEncodingException e) {
				throw new IllegalArgumentException("Charset: " + this.charset + " unsupported", e);
			}

			if (it.hasNext()) {
				builder.append('&');
			}
		}

		final String additionalParameters = builder.toString();

		if (StringUtils.hasLength(additionalParameters)) {
			template.setDefaultUri(endpointUrl + '?' + additionalParameters);
		} else {
			template.setDefaultUri(endpointUrl);
		}

		template.setMarshaller(this.marshaller);
		template.setUnmarshaller(this.unmarshaller);

		checkAfterPropertiesSet(template);

		return template;
	}

	/**
	 * @param acceptGzipEncoding the acceptGzipEncoding to set
	 */
	public void setAcceptGzipEncoding(final boolean acceptGzipEncoding) {
		this.acceptGzipEncoding = acceptGzipEncoding;
	}

	/**
	 * @param authenticationPreemptive the authenticationPreemptive to set
	 */
	public void setAuthenticationPreemptive(boolean authenticationPreemptive) {
		this.authenticationPreemptive = authenticationPreemptive;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}	
}
