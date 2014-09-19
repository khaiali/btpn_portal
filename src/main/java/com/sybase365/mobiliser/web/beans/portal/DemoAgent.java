package com.sybase365.mobiliser.web.beans.portal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.sybase365.mobiliser.util.tools.wicketutils.treetable.utils.TreeNodeBean;

public class DemoAgent implements TreeNodeBean, Serializable {

    private static final long serialVersionUID = -3231935107922836562L;

    private long id;
    private String name;

    private List<DemoAgent> childAgents;

    public DemoAgent(long id, String name) {
	this(id, name, null);
    }

    public DemoAgent(long id, String name, List<DemoAgent> childAgents) {
	this.id = id;
	this.name = name;
	this.childAgents = childAgents;
    }

    public String getId() {
	return String.valueOf(this.id);
    }

    public String getName() {
	return this.name;
    }

    @Override
    public List<DemoAgent> getChildren() {
	if (this.childAgents == null)
	    this.childAgents = new ArrayList<DemoAgent>();
	return this.childAgents;
    }
}