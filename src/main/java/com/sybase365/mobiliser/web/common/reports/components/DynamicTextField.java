package com.sybase365.mobiliser.web.common.reports.components;

import org.apache.wicket.Component;

/**
 *
 * <p>
 * &copy; 2011, Sybase Inc.
 * </p>
 * 
 * @author Allen Lau <alau@sybase.com>
 */
public class DynamicTextField extends BaseDynamicField 
	implements DynamicComponent {

    private static final long serialVersionUID = 1L;

    private Component valueComp; 

    public DynamicTextField(String id, Component comp) {

        super(id, comp);

	add(comp);
    }
}