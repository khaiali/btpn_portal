/*
 * Copyright 2012, Sybase Inc.
 * 
 */
package com.sybase365.mobiliser.web.common.reports;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.util.UriUtils;

import com.sybase365.mobiliser.web.util.Configuration;
import com.sybase365.mobiliser.web.util.XmlResourceBundleControl;

/**
 * 
 * @author Allen Lau <alau@sybase.com>
 */
public class ReportProxyServlet extends HttpServlet {

    private final static Logger LOG = LoggerFactory
	    .getLogger(ReportProxyServlet.class);

    private final static String DEFAULT_CONTENTTYPE = "text/html;charset=UTF-8";

    public static final String PROXY_COOKIE_PREFIX = "ReportProxyCookie_";

    public static final String SET_COOKIE_HEADER = "Set-Cookie";

    public static final String REQUEST_COOKIE_HEADER = "cookie";

    public static final String ACCEPT_LANGUAGE_HEADER = "Accept-Language";

    public static final String REPORT_SESSION_PARAMETERS = "REPORT_PARAMETERS";

    private ApplicationContext springContext;

    @Override
    public void init() throws ServletException {
	springContext = WebApplicationContextUtils
		.getRequiredWebApplicationContext(getServletContext());
	super.init();
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     * 
     * @param request
     *            servlet request
     * @param response
     *            servlet response
     * @throws ServletException
     *             if a servlet-specific error occurs
     * @throws IOException
     *             if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request,
	    HttpServletResponse response) throws ServletException, IOException {

	HttpClient proxyClient = new HttpClient();
	HttpMethod method = null;

	Cookie[] cookies = request.getCookies();
	if (LOG.isDebugEnabled()) {
	    LOG.debug("processing method {}", request.getMethod());
	    LOG.debug("contextPath {}", request.getContextPath());
	    LOG.debug("servletPath {}", request.getServletPath());
	    LOG.debug("queryString {}", request.getQueryString());
	    LOG.debug("locale {}", getLocale(request));
	    if (cookies != null) {
		for (Cookie cookie : cookies) {
		    LOG.debug("cookie Domain {}", cookie.getDomain());
		    LOG.debug("cookie Name {}", cookie.getName());
		    LOG.debug("cookie Path {}", cookie.getPath());
		    LOG.debug("cookie Value {}", cookie.getValue());
		}
	    }
	}

	String servletPath = request.getServletPath();
	Configuration configuration = springContext
		.getBean(Configuration.class);
	LOG.debug("PROXY URL {}", configuration.getReportProxyServerUrl());
	if ("GET".equals(request.getMethod())) {
	    if (servletPath != null) {
		method = new GetMethod(configuration.getReportProxyServerUrl()
			+ servletPath);
	    } else {
		method = new GetMethod(configuration.getReportProxyServerUrl()
			+ configuration.getReportProxyServerPath());
	    }

	    processRequestHeaders(method, request);

	    // in case of a get, we have to add possible session parameters tp
	    // the query string
	    method.setQueryString(getCompleteQueryString(request));
	} else if ("POST".equals(request.getMethod())) {
	    method = new PostMethod(configuration.getReportProxyServerUrl()
		    + servletPath);

	    processRequestHeaders(method, request);

	    Map<String, String[]> parameters = (Map<String, String[]>) request
		    .getParameterMap();
	    List<NameValuePair> NVPairs = new ArrayList<NameValuePair>();
	    for (String key : parameters.keySet()) {
		if (LOG.isDebugEnabled()) {
		    LOG.debug("post key {}", key);
		}
		String[] values = parameters.get(key);
		for (String value : values) {
		    if (LOG.isDebugEnabled()) {
			LOG.debug("post value {}", value);
		    }
		    NameValuePair nameValuePair = new NameValuePair(key, value);
		    NVPairs.add(nameValuePair);
		}
	    }

	    // additionally retrieve the report parameters from the session
	    NVPairs.addAll(getSessionParameters(request));

	    ((PostMethod) method).setRequestBody(NVPairs
		    .toArray(new NameValuePair[] {}));
	} else {
	    response.setContentType(DEFAULT_CONTENTTYPE);
	    response.setStatus(HttpStatus.SC_METHOD_FAILURE);
	    return;
	}

	method.setFollowRedirects(false);
	int status = HttpStatus.SC_INTERNAL_SERVER_ERROR;

	try {
	    status = proxyClient.executeMethod(method);
	} catch (Exception e) {
	    LOG.warn(e.toString(), e);
	}

	processResponseHeaders(method, response, cookies);
	response.setStatus(status);

	if (HttpStatus.SC_OK == status) {

	    InputStream iStream = method.getResponseBodyAsStream();
	    BufferedInputStream bStream = new BufferedInputStream(iStream);
	    ServletOutputStream sos = response.getOutputStream();
	    int c;
	    while ((c = bStream.read()) != -1) {
		sos.write(c);
	    }
	    sos.flush();
	} else {
	    response.setContentType(DEFAULT_CONTENTTYPE);
	    PrintWriter out = response.getWriter();

	    //
	    // "/xml/" is specific to the R5 web ui application.
	    //
	    ResourceBundle bundle = ResourceBundle.getBundle("/xml/"
		    + this.getClass().getName(), getLocale(request),
		    new XmlResourceBundleControl(this.getServletContext()));

	    try {
		out.println("<html>");
		out.println("<head>");
		out.println("<title> " + bundle.getString("report.proxy.title")
			+ "</title>");
		out.println("</head>");
		out.println("<body>");
		out.println("<h4>" + bundle.getString("report.proxy.error")
			+ request.getContextPath() + "</h4>");
		out.println(bundle.getString("report.proxy.error.retry"));
		out.println("</body>");
		out.println("</html>");
	    } finally {
		out.flush();
		out.close();
	    }
	}
    }

    private List<NameValuePair> getSessionParameters(HttpServletRequest request) {

	List<NameValuePair> params = new ArrayList<NameValuePair>();

	@SuppressWarnings("unchecked")
	Map<String, String> reportSessionParameters = (Map<String, String>) request
		.getSession().getAttribute(REPORT_SESSION_PARAMETERS);
	if (reportSessionParameters != null) {
	    for (Entry<String, String> entry : reportSessionParameters
		    .entrySet()) {
		if (LOG.isDebugEnabled()) {
		    LOG.debug("session param key {}", entry.getKey());
		    LOG.debug("session param value {}", entry.getValue());
		}
		params.add(new NameValuePair(entry.getKey(), entry.getValue()));
	    }
	}

	return params;
    }

    private String getCompleteQueryString(HttpServletRequest request) {

	List<NameValuePair> sessionParams = getSessionParameters(request);
	if (sessionParams == null || sessionParams.isEmpty()) {
	    return request.getQueryString();
	}

	String completeQueryString = request.getQueryString();
	for (NameValuePair param : sessionParams) {
	    completeQueryString += "&" + param.getName() + "="
		    + param.getValue();
	}

	try {
	    return UriUtils.encodeQuery(completeQueryString, "UTF-8");
	} catch (UnsupportedEncodingException use) {
	    LOG.warn(use.toString(), use);
	}

	return request.getQueryString();
    }

    private void processResponseHeaders(HttpMethod method,
	    HttpServletResponse response, Cookie[] cookies) {
	Header[] headers = method.getResponseHeaders();
	for (Header header : headers) {
	    if (SET_COOKIE_HEADER.equals(header.getName())) {
		processResponseCookie(header, cookies);
	    } else {
		response.setHeader(header.getName(), header.getValue());
	    }

	    if (LOG.isDebugEnabled()) {
		LOG.debug("response header {}", header.toString());
	    }
	}
    }

    private void processRequestHeaders(HttpMethod method,
	    HttpServletRequest request) {
	Enumeration<String> headers = request.getHeaderNames();
	processRequestCookie(request.getSession(), method, request.getCookies());

	while (headers != null && headers.hasMoreElements()) {
	    String key = headers.nextElement();
	    String val = request.getHeader(key);
	    //
	    // skip the original request cookie
	    //
	    if (!REQUEST_COOKIE_HEADER.equals(key)) {
		method.addRequestHeader(key, val);
		if (LOG.isDebugEnabled()) {
		    LOG.debug("request header {} = {}", key, val);
		}
	    }
	}

	method.addRequestHeader(ACCEPT_LANGUAGE_HEADER, getLocale(request)
		.toString());

    }

    private void processRequestCookie(HttpSession session, HttpMethod method,
	    Cookie[] cookies) {

	if (null == cookies) {
	    if (LOG.isDebugEnabled()) {
		LOG.debug("Request has no cookies");
	    }
	    return;
	}

	if (LOG.isDebugEnabled()) {
	    LOG.debug("Request CookieMap: {}", ReportProxyCookieMap.getMap());
	}

	StringBuilder cookieBuilder = new StringBuilder();
	for (Cookie requestCookie : cookies) {
	    String cookieMapKey = PROXY_COOKIE_PREFIX
		    + requestCookie.getName();
	    String cookie = ReportProxyCookieMap.getMap().get(cookieMapKey);

	    if (cookie != null) {
		cookieBuilder.append(cookie).append(";");
	    }

	    if (LOG.isDebugEnabled()) {
		LOG.debug("request cookie {} from map value {}", cookie,
			requestCookie.getName());
		LOG.debug("set session attribute:" + cookieMapKey);
	    }
	    session.setAttribute(cookieMapKey, "marker");
	}
	method.addRequestHeader(REQUEST_COOKIE_HEADER, cookieBuilder.toString());
    }

    private void processResponseCookie(Header header, Cookie[] cookies) {
	if (LOG.isDebugEnabled()) {
	    LOG.debug("response header cookie {}, value {}", header.getName(),
		    header.getValue());
	    LOG.debug("CookieMap: {}", ReportProxyCookieMap.getMap());
	}

	if (null == cookies) {
	    if (LOG.isDebugEnabled()) {
		LOG.debug("cookies are null");
	    }
	    return;
	}

	for (Cookie cookie : cookies) {
	    if (LOG.isDebugEnabled()) {
		LOG.debug("adding to cookiemap: {}, {}", cookie.getName(),
			cookie.getValue());
	    }
	    String cookieMapKey = PROXY_COOKIE_PREFIX + cookie.getName();
	    ReportProxyCookieMap.getMap().put(cookieMapKey, cookie.getValue());
	}
    }

    private Locale getLocale(HttpServletRequest request) {
	if (null == request || null == request.getLocale()) {
	    return Locale.getDefault();
	}

	return request.getLocale();
    }

    // <editor-fold defaultstate="collapsed"
    // desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     * 
     * @param request
     *            servlet request
     * @param response
     *            servlet response
     * @throws ServletException
     *             if a servlet-specific error occurs
     * @throws IOException
     *             if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request,
	    HttpServletResponse response) throws ServletException, IOException {
	processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * 
     * @param request
     *            servlet request
     * @param response
     *            servlet response
     * @throws ServletException
     *             if a servlet-specific error occurs
     * @throws IOException
     *             if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request,
	    HttpServletResponse response) throws ServletException, IOException {
	processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     * 
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
	return "Report Proxy Servlet";
    }// </editor-fold>
}
