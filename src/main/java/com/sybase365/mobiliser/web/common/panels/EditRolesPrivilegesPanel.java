package com.sybase365.mobiliser.web.common.panels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.string.AppendingStringBuffer;
import org.apache.wicket.util.string.Strings;

import com.sybase365.mobiliser.money.contract.v5_0.customer.CreateUmgrRolePrivilegeRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.CreateUmgrRolePrivilegeResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.DeleteUmgrRolePrivilegeRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.DeleteUmgrRolePrivilegeResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetUmgrRolePrivilegesRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetUmgrRolePrivilegesResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.UmgrPrivilege;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.UmgrRole;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.UmgrRolePrivilege;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.cst.pages.usermanager.EditPrivilegePage;
import com.sybase365.mobiliser.web.cst.pages.usermanager.EditRolePage;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;
import com.sybase365.mobiliser.web.util.WildCardSearch;

public class EditRolesPrivilegesPanel extends Panel {
    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(EditRolesPrivilegesPanel.class);

    WebMarkupContainer privilegesDiv;
    WebMarkupContainer rolesDiv;

    private MobiliserBasePage mobBasePage;
    private List<String> assignedRoleList;
    private List<String> assignedPrivList;
    private List<String> assignRoleList;
    private List<String> assignPrivList;
    private List<String> availableRoles;
    private List<String> availRoles;
    private List<String> availablePrivileges;
    private List<String> availPrivileges;
    private List<UmgrRole> roleList;
    private List<UmgrPrivilege> privilegesList;
    private List<String> selectedRolesToAdd;
    private List<String> selectedRolesToRemove;
    private List<String> selectedPrivilegesToAdd;
    private List<String> selectedPrivilegesToRemove;
    Class<? extends WebPage> responseClass;
    private String type;
    private Object obj;
    private String filterAvail = null;
    private String filterAssign = null;

    public EditRolesPrivilegesPanel(String id, MobiliserBasePage mobBasePage,
	    Class<? extends WebPage> responseClass, String type, Object obj) {
	super(id);
	this.mobBasePage = mobBasePage;
	this.responseClass = responseClass;
	this.type = type;
	this.obj = obj;
	constructPanel();
    }

    @SuppressWarnings("unchecked")
    private void constructPanel() {

	if (Constants.ROLE_ASSIGN_PRIVILEGE.equals(type)) {
	    getAssignedPrivilegesList();
	    getAvailablePrivilegesList();

	}

	if (Constants.PRIVILEGE_ASSIGN_ROLE.equals(type)) {
	    getAssignedRolesList();
	    getAvailableRolesList();

	}

	addOrReplace(new FeedbackPanel("errorMessages"));
	Form<?> editRolePrivForm = new Form("editRolePrivForm",
		new CompoundPropertyModel<EditRolesPrivilegesPanel>(this));

	editRolePrivForm.addOrReplace(new TextField<String>("filterAvail")
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	editRolePrivForm.addOrReplace(new Button("applyAvail") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {

		if (Constants.ROLE_ASSIGN_PRIVILEGE.equals(type)) {
		    availablePrivileges.clear();
		    availablePrivileges.addAll(availPrivileges);
		    availablePrivileges = refreshRolesPrivs(
			    availablePrivileges, filterAvail);
		    showAvailPrivs();

		}

		if (Constants.PRIVILEGE_ASSIGN_ROLE.equals(type)) {
		    availableRoles.clear();
		    availableRoles.addAll(availRoles);
		    availableRoles = refreshRolesPrivs(availableRoles,
			    filterAvail);
		    showAvailRoles();

		}
	    }
	});

	editRolePrivForm.addOrReplace(new TextField<String>("filterAssign")
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	editRolePrivForm.addOrReplace(new Button("applyAssign") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		if (Constants.ROLE_ASSIGN_PRIVILEGE.equals(type)) {
		    assignedPrivList.clear();
		    assignedPrivList.addAll(assignPrivList);
		    assignedPrivList = refreshRolesPrivs(assignedPrivList,
			    filterAssign);
		    showAssignPrivs();
		}

		if (Constants.PRIVILEGE_ASSIGN_ROLE.equals(type)) {
		    assignedRoleList.clear();
		    assignedRoleList.addAll(assignRoleList);
		    assignedRoleList = refreshRolesPrivs(assignedRoleList,
			    filterAssign);
		    showAssignRoles();
		}
	    }
	});

	rolesDiv = new WebMarkupContainer("rolesDiv");

	ListMultipleChoice<String> availRolesChoice = createMultiList(
		"selectedRolesToAdd", availableRoles);

	ListMultipleChoice<String> assignRolesChoice = createMultiList(
		"selectedRolesToRemove", assignedRoleList);

	rolesDiv.addOrReplace(availRolesChoice);

	rolesDiv.addOrReplace(assignRolesChoice);

	privilegesDiv = new WebMarkupContainer("privilegesDiv");

	ListMultipleChoice<String> availPrivsChoice = createMultiList(
		"selectedPrivilegesToAdd", availablePrivileges);

	ListMultipleChoice<String> assignPrivsChoice = createMultiList(
		"selectedPrivilegesToRemove", assignedPrivList);

	privilegesDiv.addOrReplace(availPrivsChoice);

	privilegesDiv.addOrReplace(assignPrivsChoice);

	rolesDiv.addOrReplace(new Button("addRole") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		if (getSelectedRolesToAdd() == null
			|| getSelectedRolesToAdd().isEmpty()) {
		    error(getLocalizer().getString("availableRoles.Required",
			    this));
		    return;
		} else {
		    for (String role : getSelectedRolesToAdd()) {
			try {
			    addRole(role);
			} catch (Exception e) {
			    LOG.error("# An error occurred while adding role.",
				    e);
			    error(getLocalizer().getString("ERROR.ADD_ROLE",
				    this));
			    return;
			}
		    }
		    setResponsePage(new EditPrivilegePage((UmgrPrivilege) obj));
		}
	    };
	});

	rolesDiv.addOrReplace(new Button("removeRole") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		if (getSelectedRolesToRemove() == null
			|| getSelectedRolesToRemove().isEmpty()) {
		    error(getLocalizer().getString("assignedRoles.Required",
			    this));
		    return;
		} else {
		    for (String role : getSelectedRolesToRemove()) {
			try {
			    removeRole(role);
			} catch (Exception e) {
			    LOG.error(
				    "# An error occurred while removing role.",
				    e);
			    error(getLocalizer().getString("ERROR.REMOVE_ROLE",
				    this));
			    return;
			}
		    }
		}
	    };
	});

	editRolePrivForm.addOrReplace(rolesDiv);

	privilegesDiv.addOrReplace(new Button("addPrivilege") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		if (getSelectedPrivilegesToAdd() == null
			|| getSelectedPrivilegesToAdd().isEmpty()) {
		    error(getLocalizer().getString(
			    "availablePrivileges.Required", this));
		    return;
		} else {
		    for (String privilege : getSelectedPrivilegesToAdd()) {
			try {
			    addPrivilege(privilege);
			} catch (Exception e) {
			    LOG.error(
				    "# An error occurred while adding privileges.",
				    e);
			    error(getLocalizer().getString(
				    "ERROR.ADD_PRIVILEGE", this));
			    return;
			}
		    }
		    setResponsePage(new EditRolePage((UmgrRole) obj));
		}
	    };
	});

	privilegesDiv.addOrReplace(new Button("removePrivilege") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		if (getSelectedPrivilegesToRemove() == null
			|| getSelectedPrivilegesToRemove().isEmpty()) {
		    error(getLocalizer().getString(
			    "assignedPrivileges.Required", this));
		    return;
		} else {
		    for (String privilege : getSelectedPrivilegesToRemove()) {
			try {
			    removePrivilege(privilege);
			} catch (Exception e) {
			    LOG.error(
				    "# An error occurred while removing privileges.",
				    e);
			    error(getLocalizer().getString(
				    "ERROR.REMOVE_PRIVILEGE", this));
			    return;
			}
		    }
		}
	    };
	});

	editRolePrivForm.addOrReplace(privilegesDiv);

	if (Constants.ROLE_ASSIGN_PRIVILEGE.equals(type)) {
	    rolesDiv.setVisible(false);
	    privilegesDiv.setVisible(true);
	}

	if (Constants.PRIVILEGE_ASSIGN_ROLE.equals(type)) {
	    rolesDiv.setVisible(true);
	    privilegesDiv.setVisible(false);
	}

	addOrReplace(editRolePrivForm);
    }

    private void getAvailableRolesList() {
	LOG.debug("# EditRolesPrivilegesPanel.getAvailableRolesList()");
	this.availableRoles = new ArrayList<String>();
	this.availRoles = new ArrayList<String>();
	try {

	    roleList = getMobBasePage().getAvailRolesList();
	    if (roleList != null) {
		Collections.sort(roleList, new Comparator<UmgrRole>() {
		    @Override
		    public int compare(UmgrRole arg0, UmgrRole arg1) {

			int result = new String(arg0.getRole())
				.compareTo(new String(arg1.getRole()));

			return result;
		    }
		});

		for (UmgrRole role : roleList) {

		    availableRoles.add(role.getRole());

		}
		availableRoles.removeAll(assignedRoleList);
		availRoles.addAll(availableRoles);
	    }
	} catch (Exception e) {
	    LOG.error("# An error occurred while getting available roles.", e);
	}

    }

    private void getAssignedRolesList() {
	LOG.debug("# EditRolesPrivilegesPanel.getAssignedRolesList()");
	this.assignedRoleList = new ArrayList<String>();
	this.assignRoleList = new ArrayList<String>();
	try {
	    GetUmgrRolePrivilegesRequest umgrReq = getMobBasePage()
		    .getNewMobiliserRequest(GetUmgrRolePrivilegesRequest.class);
	    UmgrPrivilege umgrPrivilege = (UmgrPrivilege) obj;
	    umgrReq.setPrivilegeId(umgrPrivilege.getPrivilege());
	    GetUmgrRolePrivilegesResponse umgrResp = getMobBasePage().wsRolePrivilegeClient
		    .getUmgrRolePrivileges(umgrReq);
	    if (!getMobBasePage().evaluateMobiliserResponse(umgrResp)) {
		LOG.warn("#An error occurred while getting assigned roles");
		return;
	    }
	    List<UmgrRolePrivilege> rolePrivList = umgrResp.getRolePrivileges();

	    if (rolePrivList != null) {
		Collections.sort(rolePrivList,
			new Comparator<UmgrRolePrivilege>() {
			    @Override
			    public int compare(UmgrRolePrivilege arg0,
				    UmgrRolePrivilege arg1) {

				int result = new String(arg0.getRole())
					.compareTo(new String(arg1.getRole()));

				return result;
			    }
			});

		for (UmgrRolePrivilege role : rolePrivList) {
		    assignedRoleList.add(role.getRole());

		}
	    }

	    assignRoleList.addAll(assignedRoleList);

	} catch (Exception e) {
	    LOG.error("# An error occurred while getting assigned roles.", e);
	}

    }

    private void getAvailablePrivilegesList() {
	LOG.debug("# EditRolesPrivilegesPanel.getAvailablePrivilegesList()");
	this.availablePrivileges = new ArrayList<String>();
	this.availPrivileges = new ArrayList<String>();
	try {

	    privilegesList = getMobBasePage().getAvailPrivilegesList();
	    if (privilegesList != null) {
		Collections.sort(privilegesList,
			new Comparator<UmgrPrivilege>() {
			    @Override
			    public int compare(UmgrPrivilege arg0,
				    UmgrPrivilege arg1) {

				int result = new String(arg0.getPrivilege())
					.compareTo(new String(arg1
						.getPrivilege()));

				return result;
			    }
			});

		for (UmgrPrivilege privilege : privilegesList) {

		    availablePrivileges.add(privilege.getPrivilege());

		}
		availablePrivileges.removeAll(assignedPrivList);
		availPrivileges.addAll(availablePrivileges);
	    }
	} catch (Exception e) {
	    LOG.error("# An error occurred while getting available privileges.");
	}

    }

    private void getAssignedPrivilegesList() {
	LOG.debug("# EditRolesPrivilegesPanel.getAssignedPrivilegesList()");
	this.assignedPrivList = new ArrayList<String>();
	this.assignPrivList = new ArrayList<String>();
	try {
	    GetUmgrRolePrivilegesRequest umgrReq = getMobBasePage()
		    .getNewMobiliserRequest(GetUmgrRolePrivilegesRequest.class);
	    UmgrRole umgrRole = (UmgrRole) obj;
	    umgrReq.setRoleId(umgrRole.getRole());
	    GetUmgrRolePrivilegesResponse umgrResp = getMobBasePage().wsRolePrivilegeClient
		    .getUmgrRolePrivileges(umgrReq);

	    if (!getMobBasePage().evaluateMobiliserResponse(umgrResp)) {
		LOG.warn("#An error occurred while getting assigned privileges");
		return;
	    }
	    List<UmgrRolePrivilege> privRoleList = umgrResp.getRolePrivileges();
	    if (privRoleList != null) {
		Collections.sort(privRoleList,
			new Comparator<UmgrRolePrivilege>() {
			    @Override
			    public int compare(UmgrRolePrivilege arg0,
				    UmgrRolePrivilege arg1) {

				int result = new String(arg0.getPrivilege())
					.compareTo(new String(arg1
						.getPrivilege()));

				return result;
			    }
			});

		for (UmgrRolePrivilege privilege : privRoleList) {
		    assignedPrivList.add(privilege.getPrivilege());
		}
	    }
	    assignPrivList.addAll(assignedPrivList);

	} catch (Exception e) {
	    LOG.error("# An error occurred while getting assigned privileges.",
		    e);
	}

    }

    private void addRole(String roleToAdd) throws Exception {
	LOG.debug("# EditRolesPrivilegesPanel.addRole()");

	UmgrPrivilege umgrPrivilege = (UmgrPrivilege) obj;
	CreateUmgrRolePrivilegeRequest addRoleReq = getMobBasePage()
		.getNewMobiliserRequest(CreateUmgrRolePrivilegeRequest.class);
	UmgrRolePrivilege umgrRole = new UmgrRolePrivilege();
	umgrRole.setRole(roleToAdd);
	umgrRole.setPrivilege(umgrPrivilege.getPrivilege());

	addRoleReq.setRolePrivilege(umgrRole);
	CreateUmgrRolePrivilegeResponse addRoleResp = getMobBasePage().wsRolePrivilegeClient
		.createUmgrRolePrivilege(addRoleReq);
	if (!getMobBasePage().evaluateMobiliserResponse(addRoleResp)) {
	    LOG.warn("#An error occurred while adding role");
	    return;
	}

    }

    private void removeRole(String roleToRemove) throws Exception {

	LOG.debug("# EditRolesPrivilegesPanel.removeRole()");

	UmgrPrivilege umgrPrivilege = (UmgrPrivilege) obj;
	DeleteUmgrRolePrivilegeRequest remRoleReq = getMobBasePage()
		.getNewMobiliserRequest(DeleteUmgrRolePrivilegeRequest.class);
	remRoleReq.setRoleId(roleToRemove);
	remRoleReq.setPrivilegeId(umgrPrivilege.getPrivilege());
	DeleteUmgrRolePrivilegeResponse remRoleResp = getMobBasePage().wsRolePrivilegeClient
		.deleteUmgrRolePrivilege(remRoleReq);
	if (!getMobBasePage().evaluateMobiliserResponse(remRoleResp)) {
	    LOG.warn("# An error occurred while removing role");
	    return;
	}
	assignRoleList.remove(roleToRemove);
	availRoles.add(roleToRemove);
	assignedRoleList = addList(assignedRoleList, assignRoleList,
		filterAssign);
	showAssignRoles();
	availableRoles = addList(availableRoles, availRoles, filterAvail);
	showAvailRoles();
    }

    private void addPrivilege(String privilegeToAdd) throws Exception {
	LOG.debug("# EditRolesPrivilegesPanel.addPrivilege()");

	UmgrRole umgrRole = (UmgrRole) obj;
	CreateUmgrRolePrivilegeRequest addPrevReq = getMobBasePage()
		.getNewMobiliserRequest(CreateUmgrRolePrivilegeRequest.class);
	UmgrRolePrivilege umgrPrivilege = new UmgrRolePrivilege();
	umgrPrivilege.setPrivilege(privilegeToAdd);
	umgrPrivilege.setRole(umgrRole.getRole());
	addPrevReq.setRolePrivilege(umgrPrivilege);
	CreateUmgrRolePrivilegeResponse addPrivResp = getMobBasePage().wsRolePrivilegeClient
		.createUmgrRolePrivilege(addPrevReq);
	if (!getMobBasePage().evaluateMobiliserResponse(addPrivResp)) {
	    LOG.warn("# An error occurred while adding privilege");
	    return;
	}
    }

    private void removePrivilege(String privilegeToRemove) throws Exception {
	LOG.debug("# EditRolesPrivilegesPanel.removePrivilege()");

	UmgrRole umgrRole = (UmgrRole) obj;
	DeleteUmgrRolePrivilegeRequest remPrevReq = getMobBasePage()
		.getNewMobiliserRequest(DeleteUmgrRolePrivilegeRequest.class);
	remPrevReq.setPrivilegeId(privilegeToRemove);
	remPrevReq.setRoleId(umgrRole.getRole());
	DeleteUmgrRolePrivilegeResponse remPrevResp = getMobBasePage().wsRolePrivilegeClient
		.deleteUmgrRolePrivilege(remPrevReq);
	if (!getMobBasePage().evaluateMobiliserResponse(remPrevResp)) {
	    LOG.warn("#  An error occurred while removing privilege");
	    return;
	}
	assignPrivList.remove(privilegeToRemove);
	availPrivileges.add(privilegeToRemove);
	assignedPrivList = addList(assignedPrivList, assignPrivList,
		filterAssign);
	showAssignPrivs();
	availablePrivileges = addList(availablePrivileges, availPrivileges,
		filterAvail);
	showAvailPrivs();

    }

    private List<String> addList(List<String> descList, List<String> srcList,
	    String filter) {
	Collections.sort(srcList);
	descList.clear();
	descList.addAll(srcList);
	return (refreshRolesPrivs(descList, filter));
    }

    private List<String> refreshRolesPrivs(List<String> rolesPrivs,
	    String filter) {

	WildCardSearch wildCardSearch = new WildCardSearch();
	if (!rolesPrivs.isEmpty()) {
	    if (PortalUtils.exists(filter))
		rolesPrivs = wildCardSearch.getFilteredList(filter, rolesPrivs);
	}

	return rolesPrivs;
    }

    private ListMultipleChoice<String> createMultiList(final String wicketID,
	    List<String> choiceList) {
	ListMultipleChoice<String> multiList = new ListMultipleChoice<String>(
		wicketID, choiceList) {
	    @Override
	    protected void appendOptionHtml(AppendingStringBuffer buffer,
		    String choice, int index, String selected) {
		Object objectValue = (Object) getChoiceRenderer()
			.getDisplayValue(choice);
		Class<String> objectClass = (Class<String>) (objectValue == null ? null
			: objectValue.getClass());

		String displayValue = "";
		if (objectClass != null && objectClass != String.class) {
		    final IConverter converter = getConverter(objectClass);

		    displayValue = converter.convertToString(objectValue,
			    getLocale());
		} else if (objectValue != null) {
		    displayValue = objectValue.toString();
		}
		buffer.append("\n<option ");
		if (isSelected(choice, index, selected)) {
		    buffer.append("selected=\"selected\" ");
		}

		buffer.append("title=\"");
		if ("selectedRolesToAdd".equals(wicketID)
			|| "selectedRolesToRemove".equals(wicketID)) {
		    buffer.append(getMobBasePage().getRoleDescription(
			    displayValue)
			    + "\" ");
		}
		if ("selectedPrivilegesToAdd".equals(wicketID)
			|| "selectedPrivilegesToRemove".equals(wicketID)) {
		    buffer.append(getMobBasePage().getPrivilegeDescription(
			    displayValue)
			    + "\" ");
		}
		buffer.append("value=\"");
		buffer.append(Strings.escapeMarkup(getChoiceRenderer()
			.getIdValue(choice, index)));
		buffer.append("\">");

		String display = displayValue;
		if (localizeDisplayValues()) {
		    display = getLocalizer().getString(displayValue, this,
			    displayValue);
		}
		CharSequence escaped = display;
		if (getEscapeModelStrings()) {
		    escaped = escapeOptionHtml(display);
		}
		buffer.append(escaped);
		buffer.append("</option>");
	    }

	};
	return multiList;
    }

    private void showAvailPrivs() {
	privilegesDiv.addOrReplace(createMultiList("selectedPrivilegesToAdd",
		availablePrivileges));
    }

    private void showAvailRoles() {
	rolesDiv.addOrReplace(createMultiList("selectedRolesToAdd",
		availableRoles));
    }

    private void showAssignPrivs() {
	privilegesDiv.addOrReplace(createMultiList(
		"selectedPrivilegesToRemove", assignedPrivList));
    }

    private void showAssignRoles() {
	rolesDiv.addOrReplace(createMultiList("selectedRolesToRemove",
		assignedRoleList));
    }

    public MobiliserBasePage getMobBasePage() {
	return mobBasePage;

    }

    public List<String> getSelectedRolesToAdd() {
	return selectedRolesToAdd;
    }

    public List<String> getSelectedRolesToRemove() {
	return selectedRolesToRemove;
    }

    public List<String> getSelectedPrivilegesToAdd() {
	return selectedPrivilegesToAdd;
    }

    public List<String> getSelectedPrivilegesToRemove() {
	return selectedPrivilegesToRemove;
    }

    public String getFilterAvail() {
	return filterAvail;
    }

    public void setFilterAvail(String filterAvail) {
	this.filterAvail = filterAvail;
    }

    public String getFilterAssign() {
	return filterAssign;
    }

    public void setFilterAssign(String filterAssign) {
	this.filterAssign = filterAssign;
    }

}
