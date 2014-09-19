package com.sybase365.mobiliser.web.common.reports.panels;

import com.sybase365.mobiliser.web.common.reports.panels.helpers.ParameterHelperPanel;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.Panel;


/**
 * 
 * <p>
 * &copy; 2011, Sybase Inc.
 * </p>
 * 
 * @author Allen Lau <alau@sybase.com>
 */
public class ParameterEntryPanel extends Panel implements ParameterEntry {

    private Component nameComponent;
    private Component valueComponent;
    private Component helperComponent;

    public ParameterEntryPanel(String id, Component name, Component value) {

	this(id, name, value, null);
    }

    public ParameterEntryPanel(String id, Component name, Component value, 
	    Component helper) {

	super(id);

	add(name);
	add(value);


	nameComponent = name;
	valueComponent = value;
    }

    public void updateValue(Component newValueComponent) {
	addOrReplace(newValueComponent);
    }
}
