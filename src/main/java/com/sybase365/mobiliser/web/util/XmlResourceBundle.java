/*
 * Copyright 2012, Sybase Inc.
 * 
 */
package com.sybase365.mobiliser.web.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Based on http://docs.oracle.com/javase/6/docs/api/java/util/ResourceBundle.Control.html
 * <p>
 * &copy; 2012, Sybase Inc.
 * </p>
 * @author Allen Lau <alau@sybase.com>
 */
public class XmlResourceBundle extends ResourceBundle {

    private Properties props;

    public XmlResourceBundle(InputStream stream) throws IOException {
        props = new Properties();
        props.loadFromXML(stream);
    }

    @Override
    protected Object handleGetObject(String key) {
        return props.getProperty(key);
    }

    @Override
    public Enumeration<String> getKeys() {
        Set<String> propertyNames = props.stringPropertyNames();
        return Collections.enumeration(propertyNames);
    }
}