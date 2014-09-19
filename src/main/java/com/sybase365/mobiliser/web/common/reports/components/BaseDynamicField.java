package com.sybase365.mobiliser.web.common.reports.components;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * 
 * <p>
 * &copy; 2012, Sybase Inc.
 * </p>
 * 
 * @author Mark White <msw@sybase.com>
 */
public class BaseDynamicField extends Panel 
	implements DynamicComponent {
    
    private static final long serialVersionUID = 1L;
    
    private Component valueComp; 

    public BaseDynamicField(String id, Component comp) {

	super(id);

	this.valueComp = comp;

	this.setOutputMarkupId(true);
    }

    @Override
    public Component getComponent() {
	return this.valueComp;
    }

}
