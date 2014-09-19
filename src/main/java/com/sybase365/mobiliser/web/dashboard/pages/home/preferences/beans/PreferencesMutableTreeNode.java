package com.sybase365.mobiliser.web.dashboard.pages.home.preferences.beans;

import com.sybase365.mobiliser.web.dashboard.pages.home.preferences.model.IPreferencesTreeEntry;
import java.io.Serializable;

import javax.swing.tree.DefaultMutableTreeNode;

public class PreferencesMutableTreeNode extends DefaultMutableTreeNode 
        implements Serializable {

    public String name;
    public IPreferencesTreeEntry prefsNode;

    public PreferencesMutableTreeNode(String name) {
        super(null);
	this.name = name;
    }

    public PreferencesMutableTreeNode(String name, IPreferencesTreeEntry node) {
        super(node);
	this.name = name;
        this.prefsNode = node;
    }

    public IPreferencesTreeEntry getPreferencesNode() {
        return this.prefsNode;
    }

    public String getName() {
	return this.name;
    }

    @Override
    public boolean isLeaf() {
        return !getAllowsChildren();
    }
}
