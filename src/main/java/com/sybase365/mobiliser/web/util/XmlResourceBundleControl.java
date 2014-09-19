/*
 * Copyright 2012, Sybase Inc.
 * 
 */
package com.sybase365.mobiliser.web.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.servlet.ServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Based on http://docs.oracle.com/javase/6/docs/api/java/util/ResourceBundle.Control.html
 * <p>
 * &copy; 2012, Sybase Inc.
 * </p>
 * @author Allen Lau <alau@sybase.com>
 */
public class XmlResourceBundleControl extends ResourceBundle.Control {

    private final static Logger LOG = LoggerFactory.getLogger(XmlResourceBundleControl.class);

    ServletContext context;

    public XmlResourceBundleControl(ServletContext context) {
        this.context = context;
    }

    @Override
    public List<String> getFormats(String baseName) {
        if (baseName == null) {
            throw new NullPointerException();
        }
        return Arrays.asList("xml");
    }

    @Override
    public ResourceBundle newBundle(String baseName,
        Locale locale,
        String format,
        ClassLoader loader,
        boolean reload)
        throws IllegalAccessException,
        InstantiationException,
        IOException {
        if (baseName == null || locale == null
            || format == null || loader == null) {
            throw new NullPointerException();
        }
        ResourceBundle bundle = null;

        if (format.equals("xml")) {
            String bundleName = toBundleName(baseName, locale);
            String resourceName = toResourceName(bundleName, format);
            if (LOG.isDebugEnabled()) {
                LOG.info("checking for resourceName: {}", resourceName);
            }
            InputStream stream = null;
            if (reload) {
                URL url = null;
                if (context != null) {
                    url = context.getResource(resourceName);
                } else {
                    url = loader.getResource(resourceName);
                }
                if (url != null) {
                    URLConnection connection = url.openConnection();
                    if (connection != null) {
                        // Disable caches to get fresh data for
                        // reloading.
                        connection.setUseCaches(false);
                        stream = connection.getInputStream();
                    }
                }
            } else {
                if (context != null) {
                    stream = context.getResourceAsStream(resourceName);
                } else {
                    stream = loader.getResourceAsStream(resourceName);
                }
            }
            if (stream != null) {
                BufferedInputStream bis = new BufferedInputStream(stream);
                bundle = new XmlResourceBundle(bis);
                bis.close();
            }
        }
        return bundle;
    }
}