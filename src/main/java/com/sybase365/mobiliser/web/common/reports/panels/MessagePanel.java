package com.sybase365.mobiliser.web.common.reports.panels;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.Panel;

/**
 *
 * <p>
 * &copy; 2011, Sybase Inc.
 * </p>
 * @author Allen Lau <alau@sybase.com>
 */
public class MessagePanel extends Panel implements ParameterEntry {

    public MessagePanel(String id, Component component) {

        super(id);

        add(component);
    }
}
