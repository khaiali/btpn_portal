package com.sybase365.mobiliser.web.dashboard.pages.home.preferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxIndicatorAware;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.tree.table.ColumnLocation;
import org.apache.wicket.extensions.markup.html.tree.table.ColumnLocation.Alignment;
import org.apache.wicket.extensions.markup.html.tree.table.ColumnLocation.Unit;
import org.apache.wicket.extensions.markup.html.tree.table.IColumn;
import org.apache.wicket.extensions.markup.html.tree.table.PropertyTreeColumn;
import org.apache.wicket.extensions.markup.html.tree.table.TreeTable;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.tree.ITreeState;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.util.contract.v5_0.prefs.beans.DetailedPreference;
import com.sybase365.mobiliser.util.contract.v5_0.prefs.beans.DetailedPreferencesTree;
import com.sybase365.mobiliser.util.contract.v5_0.prefs.beans.PreferencesApplication;
import com.sybase365.mobiliser.web.dashboard.pages.home.HomeMenuGroup;
import com.sybase365.mobiliser.web.dashboard.pages.home.preferences.beans.PreferencesMutableTreeNode;
import com.sybase365.mobiliser.web.dashboard.pages.home.preferences.beans.PreferencesTreeApplicationEntry;
import com.sybase365.mobiliser.web.dashboard.pages.home.preferences.beans.PreferencesTreeEntry;
import com.sybase365.mobiliser.web.dashboard.pages.home.preferences.model.IPreferencesNavigator;
import com.sybase365.mobiliser.web.dashboard.pages.home.preferences.model.IPreferencesTreeApplicationEntry;
import com.sybase365.mobiliser.web.dashboard.pages.home.preferences.model.IPreferencesTreeEntry;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

@AuthorizeInstantiation(Constants.PRIV_DASHBOARD_PREFS)
public class PreferencesPage extends HomeMenuGroup 
        implements IPreferencesNavigator, IAjaxIndicatorAware {

    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(PreferencesPage.class);

    private TreeTable preferencesTreeTable;

    private HashMap<String, DetailedPreferencesTree> prefsAppMap;
	    
    private IPreferencesNavigator backPage; 

    private Form prefsForm;

    @SuppressWarnings("unchecked")
    @Override
    protected void initOwnPageComponents() {

        backPage = this;

        super.initOwnPageComponents();

        prefsForm = new Form("preferencesForm",
                new CompoundPropertyModel<PreferencesPage>(this));

        prefsForm.setOutputMarkupId(true);

        prefsForm.add(new FeedbackPanel("errorMessages"));

        prefsForm.add(new Button("addApplication") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
                setResponsePage(new PreferencesApplicationAddPage(backPage));
            };
        });

        prefsForm.add(new Button("addNode") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
                setResponsePage(new PreferencesNodeAddPage(backPage));
            };
        });

       prefsForm.add(new Button("importNode") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
                setResponsePage(new PreferencesNodeImportPage(backPage));
            };
        });

	this.refresh();

        add(prefsForm);
    }

    private void addPreferencesTreeDiv(TreeModel treeModel) {

        WebMarkupContainer preferencesTreeDiv = new WebMarkupContainer("preferencesTreeDiv");

        preferencesTreeDiv.setVisible(false);

        try {
            List<IColumn> columns = new ArrayList<IColumn>();

            columns.add(new PropertyTreeColumn(new ColumnLocation(
                    Alignment.LEFT, 100, Unit.PERCENT),
                    "Preferences Node", "userObject.name"));

            preferencesTreeTable = 
                    new TreeTable("preferencesTree",
                            treeModel, 
                            columns.toArray(new IColumn[columns.size()])) {

                private static final long serialVersionUID = 1L;

                @Override
                protected void onJunctionLinkClicked(AjaxRequestTarget target,
                    TreeNode node) {

                    IPreferencesTreeEntry prefsTreeEntry = 
                            ((PreferencesMutableTreeNode)node).getPreferencesNode();

                    LOG.debug("Clicked on junction: {}", prefsTreeEntry.getName());

                    ITreeState treeState = getTreeState();

                    if (treeState.isNodeExpanded(node) 
			    && node.getChildCount() > 0) {
                        treeState.expandNode(node);
                    } else {
                        treeState.collapseNode(node);
                    }

                    super.onJunctionLinkClicked(target, node);

                    updateTree(target);
                }

                @Override
                protected void onNodeLinkClicked(AjaxRequestTarget target,
                    TreeNode node) {

                    IPreferencesTreeEntry prefsTreeEntry = (IPreferencesTreeEntry)
                            ((PreferencesMutableTreeNode)node).getPreferencesNode();

		    if (node.isLeaf()) {
    	                LOG.debug("Clicked on leaf: {}", prefsTreeEntry.getName());

			ITreeState treeState = getTreeState();
			treeState.selectNode(node, true);

			setResponsePage(new PreferencesNodePage(prefsTreeEntry, backPage));

			super.onNodeLinkClicked(target, node);

			target.addComponent(prefsForm);
		    }
		    else if (prefsTreeEntry.getPath().equals("/")) {

    	                LOG.debug("Clicked on application: {}", prefsTreeEntry.getName());

			ITreeState treeState = getTreeState();
			treeState.selectNode(node, true);

			if (prefsTreeEntry instanceof IPreferencesTreeApplicationEntry) {
			    setResponsePage(
				    new PreferencesApplicationEditPage(
					    (IPreferencesTreeApplicationEntry)prefsTreeEntry, backPage));
			}

			super.onNodeLinkClicked(target, node);

			target.addComponent(prefsForm);
		    }
		    else {
	                LOG.debug("Clicked on junction name: {}", prefsTreeEntry.getName());

			ITreeState treeState = getTreeState();

			if (!treeState.isNodeExpanded(node) 
				&& node.getChildCount() > 0) {
			    treeState.expandNode(node);
			} else {
			    treeState.collapseNode(node);
			}

			updateTree(target);
		    }
                }
            };

            preferencesTreeTable.setRootLess(true);
            preferencesTreeTable.getTreeState().setAllowSelectMultiple(false);
            preferencesTreeTable.getTreeState().collapseAll();

            // add the tree table component to the page's component tree
            preferencesTreeDiv.setVisible(true);
            preferencesTreeDiv.addOrReplace(preferencesTreeTable);

        }
        catch (Exception e) {
            LOG.error("# An error occuurred while loading preferences tree",
                    e);
            error(getLocalizer().getString("preferences.tree.error.loading", 
                    this));
        }

        prefsForm.addOrReplace(preferencesTreeDiv);
    }

    private TreeModel getTreeModel() {

        LOG.debug("Loading preferences...");

        final DefaultMutableTreeNode treeRootDisplay = 
                new PreferencesMutableTreeNode("/");
      
	this.prefsAppMap = new HashMap<String, DetailedPreferencesTree>();

	try {
	    List<PreferencesApplication> prefsApps = 
		    this.getAllPreferencesApplicationNames();

	    Collections.sort(prefsApps, new Comparator<PreferencesApplication>() {
		    @Override
		    public int compare(PreferencesApplication o1,
			    PreferencesApplication o2) {
			return o1.getApplicationName().compareTo(o2.getApplicationName());
		    }
	    });

	    for (PreferencesApplication prefsApp : prefsApps) {

		LOG.debug("Got preferences application: {}", 
			prefsApp.getApplicationName());

		IPreferencesTreeEntry prefsAppRootEntry = new PreferencesTreeApplicationEntry(
			prefsApp.getApplicationName(),
			prefsApp.getReadPrivilege(),
			prefsApp.getWritePrivilege(),
			prefsApp.getDescription());

		final PreferencesMutableTreeNode prefsAppTreeRoot = 
			new PreferencesMutableTreeNode(
				prefsAppRootEntry.getApplicationName(),
				prefsAppRootEntry);

		treeRootDisplay.add(prefsAppTreeRoot);

		addNodes(prefsApp.getApplicationName(), 
			prefsAppTreeRoot, prefsAppRootEntry);
	    }
	}
	catch (Exception e) {
	    LOG.error("Got exception loading preferences", e);
	}

        final DefaultTreeModel tree = new DefaultTreeModel(treeRootDisplay);

        return tree;
    }

    private void addNodes(String applicationName, 
	    PreferencesMutableTreeNode parentTreeNode, IPreferencesTreeEntry prefsEntry) {

        LOG.debug("Loading preferences nodes for {}...", prefsEntry.getPath());

	try {
	    DetailedPreferencesTree prefsTree = 
		this.getDetailedPreferences( applicationName, "/");

	    prefsAppMap.put(applicationName, prefsTree);

	    List<DetailedPreference> prefs = prefsTree.getDetailedPreference();

	    if (PortalUtils.exists(prefs)) {

		Collections.sort(prefs, new Comparator<DetailedPreference>() {
		    @Override
		    public int compare(DetailedPreference o1,
			    DetailedPreference o2) {
			return o1.getPath().compareTo(o2.getPath());
		    }
	    	});

		for (DetailedPreference pref : prefs) {

		    LOG.debug("Found preference: {}", pref.getPath());

		    // ignore root as it has already been added by app entry
		    if (!pref.getPath().equals("/")) {
		    	addChildren(parentTreeNode, "", pref.getPath(), prefsEntry, pref);
		    }
		}
	    }
	    else {
		LOG.debug("No preferences nodes found");
	    }
	}
	catch (Exception e) {
	    LOG.error("Got exception loading preferences nodes", e);
	}

    }

    private void addChildren(PreferencesMutableTreeNode parentTreeNode, 
	    String parentPath, String remainingPath, IPreferencesTreeEntry appEntry, DetailedPreference pref) {

	LOG.debug("Remaining path: {}", remainingPath);

	if (remainingPath.indexOf("/",1) > 1) {

	    String thisNodeName = remainingPath.substring(1,remainingPath.indexOf("/",1));

	    parentPath = parentPath + "/" + thisNodeName;

	    remainingPath = remainingPath.substring(remainingPath.indexOf("/",1));

	    LOG.debug("Adding a tree node for path: {}", parentPath);

	    IPreferencesTreeEntry node = new PreferencesTreeEntry(
		    appEntry.getApplicationName(), 
		    parentPath,
		    thisNodeName,
		    null);

 	    PreferencesMutableTreeNode treeNode = findNode(parentTreeNode, thisNodeName);

	    if (treeNode == null) {
 	    	treeNode = new PreferencesMutableTreeNode(node.getName(), node);
	    }

	    treeNode.setAllowsChildren(Boolean.TRUE);

	    parentTreeNode.add(treeNode);

	    addChildren(treeNode, parentPath, remainingPath, node, pref);
	}
	else {
	    String thisLeafName = thisLeafName = remainingPath.substring(1);

	    LOG.debug("Adding a tree leaf for {}", thisLeafName);

	    IPreferencesTreeEntry node = new PreferencesTreeEntry(
		    appEntry.getApplicationName(), 
		    parentPath,
		    thisLeafName,
		    pref.getEntry());

 	    final PreferencesMutableTreeNode treeNode = 
		    new PreferencesMutableTreeNode(node.getName(), node);

	    treeNode.setAllowsChildren(Boolean.FALSE);

	    parentTreeNode.add(treeNode);
	}

    }

    private PreferencesMutableTreeNode findNode(
	    PreferencesMutableTreeNode parentNode, String nodeName) {

	Enumeration enm = parentNode.children();

	while (enm.hasMoreElements()) {
	    PreferencesMutableTreeNode treeNode = (PreferencesMutableTreeNode)enm.nextElement();
	    if (treeNode.getName().equals(nodeName)) {
		return treeNode;
	    }
	}

	return null;
    }

    @Override
    public void refresh() {

	LOG.debug("Refresh...");

	this.refreshPreferencesClientSource();

        final TreeModel treeModel = getTreeModel();

        addPreferencesTreeDiv(treeModel);
    }

    @Override
    public String getAjaxIndicatorMarkupId() {
        return "errorMessages";
    }

}
