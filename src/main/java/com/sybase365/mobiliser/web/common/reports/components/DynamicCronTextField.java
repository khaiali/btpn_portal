package com.sybase365.mobiliser.web.common.reports.components;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.model.Model;

/**
 * 
 * <p>
 * &copy; 2011, Sybase Inc.
 * </p>
 * 
 * @author Allen Lau <alau@sybase.com>
 */
public class DynamicCronTextField extends BaseDynamicField 
	implements DynamicComponent {
    
    private static final long serialVersionUID = 1L;
    
    private Component valueComp; 

    public DynamicCronTextField(String id, Component comp) {

	super(id, comp);

	String key = "" + comp.hashCode();
	comp.add(new AttributeModifier("id", new Model<String>(key)));
	add(comp);
    }

}
