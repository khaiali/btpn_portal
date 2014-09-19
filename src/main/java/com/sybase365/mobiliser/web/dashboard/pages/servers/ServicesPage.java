package com.sybase365.mobiliser.web.dashboard.pages.servers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.tree.table.ColumnLocation;
import org.apache.wicket.extensions.markup.html.tree.table.ColumnLocation.Alignment;
import org.apache.wicket.extensions.markup.html.tree.table.ColumnLocation.Unit;
import org.apache.wicket.extensions.markup.html.tree.table.IColumn;
import org.apache.wicket.extensions.markup.html.tree.table.PropertyTreeColumn;
import org.apache.wicket.extensions.markup.html.tree.table.TreeTable;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

import com.sybase365.mobiliser.web.dashboard.pages.servers.beans.AuditManagerValues;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_DASHBOARD_SERVERS)
public class ServicesPage extends ServersMenuGroup {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(ServicesPage.class);

    AuditManagerValues auditManagerValues;

    @SuppressWarnings("unchecked")
    @Override
    protected void initOwnPageComponents() {

	super.initOwnPageComponents();

	add(new FeedbackPanel("errorMessages"));

	AuditManagerValues auditManagerValues = new AuditManagerValues(this);

	addRequestsTreeDiv(
		createTreeModel(auditManagerValues.getRequestTypes()),
		auditManagerValues);

    }

    private void addRequestsTreeDiv(TreeModel treeModel,
	    final AuditManagerValues auditManagerValues) {
	WebMarkupContainer requestsTreeDiv = new WebMarkupContainer(
		"requestsTreeDiv");

	requestsTreeDiv.setVisible(false);

	try {
	    List<IColumn> columns = new ArrayList<IColumn>();

	    columns.add(new PropertyTreeColumn(new ColumnLocation(
		    Alignment.LEFT, 100, Unit.PERCENT), "Request Type",
		    "userObject"));

	    TreeTable reqTreeTable = new TreeTable("requestsTree", treeModel,
		    columns.toArray(new IColumn[columns.size()])) {

		private static final long serialVersionUID = 1L;

		@Override
		protected void onNodeLinkClicked(AjaxRequestTarget target,
			TreeNode node) {

		    DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) node;
		    if (!treeNode.getAllowsChildren()) {
			setResponsePage(new ServiceDetailPage(
				ServicesPage.this,
				(String) treeNode.getUserObject(),
				auditManagerValues));
		    }

		}
	    };

	    reqTreeTable.setRootLess(true);
	    reqTreeTable.getTreeState().setAllowSelectMultiple(false);
	    reqTreeTable.getTreeState().collapseAll();

	    requestsTreeDiv.setVisible(true);
	    requestsTreeDiv.addOrReplace(reqTreeTable);

	} catch (Exception e) {
	    LOG.error(
		    "# An error occurred while loading service request types tree",
		    e);
	    error(getLocalizer().getString(
		    "dashboard.services.tree.error.loading", this));
	}

	add(requestsTreeDiv);

    }

    protected TreeModel createTreeModel(List<String> requestTypes) {
	// grouping
	Map<String, List<String>> groups = new HashMap<String, List<String>>();
	for (String requestType : requestTypes) {
	    String group = getGroup(requestType);
	    if (!groups.containsKey(group)) {
		// new group
		groups.put(group, new ArrayList<String>());
	    }
	    groups.get(group).add(requestType);
	}

	return convertToTreeModel(groups);
    }

    private TreeModel convertToTreeModel(Map<String, List<String>> groups) {
	TreeModel model = null;
	DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(
		new String("mobiliser"));

	for (String group : groups.keySet()) {
	    DefaultMutableTreeNode child = new DefaultMutableTreeNode(group);
	    rootNode.add(child);
	    add(child, groups.get(group));
	}

	model = new DefaultTreeModel(rootNode);
	return model;
    }

    private void add(DefaultMutableTreeNode parent, List<String> sub) {
	for (String item : sub) {
	    DefaultMutableTreeNode child = new DefaultMutableTreeNode(item);
	    child.setAllowsChildren(false);
	    parent.add(child);
	}
    }

    private String getGroup(String requestType) {
	int lastDot = requestType.lastIndexOf(".");
	String group = requestType.substring(
		requestType.lastIndexOf(".", lastDot - 1) + 1, lastDot);
	return group;
    }
}
