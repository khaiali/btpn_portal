package com.sybase365.mobiliser.web.common.panels;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.money.contract.v5_0.customer.AddUmgrCustomerPrivilegeRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.AddUmgrCustomerPrivilegeResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.AddUmgrCustomerRoleRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.AddUmgrCustomerRoleResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetUmgrCustomerPrivilegesRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetUmgrCustomerPrivilegesResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetUmgrCustomerRolesRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetUmgrCustomerRolesResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.RemoveUmgrCustomerPrivilegeRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.RemoveUmgrCustomerPrivilegeResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.RemoveUmgrCustomerRoleRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.RemoveUmgrCustomerRoleResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.CustomerPrivilege;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.CustomerRole;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.util.Constants;

public class EditRolePrivilegePanel extends Panel {
    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(EditRolePrivilegePanel.class);

    private MobiliserBasePage mobBasePage;
    private List<String> assignedRoleList;
    private List<String> assignedPrivList;
    private List<String> availableRoles;
    private List<String> availablePrivileges;
    private List<String> selectedRolesToAdd;
    private List<String> selectedRolesToRemove;
    private List<String> selectedPrivilegesToAdd;
    private List<String> selectedPrivilegesToRemove;
    Class<? extends WebPage> responseClass;

    public EditRolePrivilegePanel(String id, MobiliserBasePage mobBasePage,
	    Class<? extends WebPage> responseClass) {
	super(id);
	this.mobBasePage = mobBasePage;
	this.responseClass = responseClass;
	constructPanel();
    }

    @SuppressWarnings("unchecked")
    private void constructPanel() {
	this.assignedRoleList = getAssignedRolesList();
	this.assignedPrivList = getAssignedPrivilegesList();
	getAvailablePrivilegesList().removeAll(assignedPrivList);
	getAvailableRolesList().removeAll(this.assignedRoleList);

	add(new FeedbackPanel("errorMessages"));
	Form<?> editRolePrivForm = new Form("editRolePrivForm",
		new CompoundPropertyModel<EditRolePrivilegePanel>(this));

	editRolePrivForm.add(new ListMultipleChoice<String>(
		"selectedRolesToAdd", availableRoles));

	editRolePrivForm.add(new ListMultipleChoice<String>(
		"selectedRolesToRemove", assignedRoleList));

	editRolePrivForm.add(new ListMultipleChoice<String>(
		"selectedPrivilegesToAdd", availablePrivileges));

	editRolePrivForm.add(new ListMultipleChoice<String>(
		"selectedPrivilegesToRemove", assignedPrivList));

	editRolePrivForm.add(new Button("addRole") {
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
		    setResponsePage(responseClass);
		}
	    };
	});
	editRolePrivForm.add(new Button("removeRole") {
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
		    setResponsePage(responseClass);
		}
	    };
	});
	editRolePrivForm.add(new Button("addPrivilege") {
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
		    setResponsePage(responseClass);
		}
	    };
	});
	editRolePrivForm.add(new Button("removePrivilege") {
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
		    setResponsePage(responseClass);
		}
	    };
	});

	add(editRolePrivForm);
    }

    private List<String> getAvailableRolesList() {
	this.availableRoles = new ArrayList<String>();
	try {
	    GetUmgrCustomerRolesRequest umgrReq = getMobBasePage()
		    .getNewMobiliserRequest(GetUmgrCustomerRolesRequest.class);
	    umgrReq.setCustomerId(getMobBasePage().getMobiliserWebSession()
		    .getLoggedInCustomer().getCustomerId());
	    GetUmgrCustomerRolesResponse umgrResp = getMobBasePage().wsRolePrivilegeClient
		    .getUmgrCustomerRoles(umgrReq);
	    if (!getMobBasePage().evaluateMobiliserResponse(umgrResp))
		return null;
	    List<CustomerRole> roleList = umgrResp.getUmgrRoles();
	    for (CustomerRole role : roleList) {

		if (role.isGrantOption()) {
		    this.availableRoles.add(role.getRole());
		}

	    }
	} catch (Exception e) {
	    LOG.error("# An error occurred while getting available roles.", e);
	}
	return this.availableRoles;

    }

    private List<String> getAssignedRolesList() {
	assignedRoleList = new ArrayList<String>();
	try {
	    GetUmgrCustomerRolesRequest umgrReq = getMobBasePage()
		    .getNewMobiliserRequest(GetUmgrCustomerRolesRequest.class);
	    umgrReq.setCustomerId(getMobBasePage().getMobiliserWebSession()
		    .getCustomer().getId());
	    GetUmgrCustomerRolesResponse umgrResp = getMobBasePage().wsRolePrivilegeClient
		    .getUmgrCustomerRoles(umgrReq);
	    if (!getMobBasePage().evaluateMobiliserResponse(umgrResp))
		return null;
	    List<CustomerRole> roleList = umgrResp.getUmgrRoles();

	    for (CustomerRole role : roleList) {
		assignedRoleList.add(role.getRole());

	    }
	} catch (Exception e) {
	    LOG.error("# An error occurred while getting assigned roles.", e);
	}
	return assignedRoleList;

    }

    private List<String> getAvailablePrivilegesList() {
	this.availablePrivileges = new ArrayList<String>();
	try {

	    List<CustomerPrivilege> privList = getMobBasePage()
		    .getCustomerPrivilegesList(
			    getMobBasePage().getMobiliserWebSession()
				    .getLoggedInCustomer().getCustomerId());
	    for (CustomerPrivilege priv : privList) {
		// TODO : need to discuss validFrom and ValidTo date
		/*
		 * GregorianCalendar c = new GregorianCalendar(); c.setTime(new
		 * Date()); XMLGregorianCalendar currentDate =
		 * DatatypeFactory.newInstance().newXMLGregorianCalendar(c); if
		 * (priv.getValidFromDate().compare(currentDate) !=
		 * DatatypeConstants.GREATER &&
		 * priv.getValidToDate().compare(currentDate) !=
		 * DatatypeConstants.LESSER && priv.isGrantOption()) {
		 */
		if (priv.isGrantOption()) {
		    this.availablePrivileges.add(priv.getPrivilege());
		}

	    }
	} catch (Exception e) {
	    LOG.error("# An error occurred while getting available privileges.");
	}

	return this.availablePrivileges;

    }

    private List<String> getAssignedPrivilegesList() {
	assignedPrivList = new ArrayList<String>();
	try {
	    GetUmgrCustomerPrivilegesRequest umgrReq = getMobBasePage()
		    .getNewMobiliserRequest(
			    GetUmgrCustomerPrivilegesRequest.class);
	    umgrReq.setCustomerId(getMobBasePage().getMobiliserWebSession()
		    .getCustomer().getId());
	    GetUmgrCustomerPrivilegesResponse umgrResp = getMobBasePage().wsRolePrivilegeClient
		    .getUmgrCustomerPrivileges(umgrReq);
	    if (!getMobBasePage().evaluateMobiliserResponse(umgrResp))
		return null;
	    List<CustomerPrivilege> privList = umgrResp.getUmgrPrivileges();
	    for (CustomerPrivilege priv : privList) {
		/*
		 * GregorianCalendar c = new GregorianCalendar(); c.setTime(new
		 * Date()); XMLGregorianCalendar currentDate =
		 * DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		 * if(priv.getValidFromDate().compare(currentDate) !=
		 * DatatypeConstants.GREATER &&
		 * priv.getValidToDate().compare(currentDate
		 * )!=DatatypeConstants.LESSER){
		 */
		assignedPrivList.add(priv.getPrivilege());

	    }
	} catch (Exception e) {
	    LOG.error("# An error occurred while getting assigned privileges.",
		    e);
	}

	return assignedPrivList;
    }

    private void addRole(String roleToAdd) throws Exception {
	LOG.debug("# EditRolePrivilegePanel.addRole()");

	CustomerRole custRole = new CustomerRole();
	Long editCustId = getMobBasePage().getMobiliserWebSession()
		.getCustomer().getId();
	custRole.setCustomerId(editCustId);
	custRole.setRole(roleToAdd);
	custRole.setOrgUnit(Constants.DEFAULT_ORGUNIT);
	AddUmgrCustomerRoleRequest addRoleReq = getMobBasePage()
		.getNewMobiliserRequest(AddUmgrCustomerRoleRequest.class);
	addRoleReq.setCustomerRole(custRole);
	AddUmgrCustomerRoleResponse addRoleResp = getMobBasePage().wsRolePrivilegeClient
		.addUmgrCustomerRole(addRoleReq);
	if (!getMobBasePage().evaluateMobiliserResponse(addRoleResp))
	    return;

    }

    private void removeRole(String roleToRemove) throws Exception {

	LOG.debug("# EditRolePrivilegePanel.removeRole()");

	RemoveUmgrCustomerRoleRequest remRoleReq = getMobBasePage()
		.getNewMobiliserRequest(RemoveUmgrCustomerRoleRequest.class);
	remRoleReq.setUmgrRole(roleToRemove);
	remRoleReq.setCustomerId(getMobBasePage().getMobiliserWebSession()
		.getCustomer().getId());
	remRoleReq.setOrgUnit(Constants.DEFAULT_ORGUNIT);
	RemoveUmgrCustomerRoleResponse remRoleResp = getMobBasePage().wsRolePrivilegeClient
		.removeUmgrCustomerRole(remRoleReq);
	if (!getMobBasePage().evaluateMobiliserResponse(remRoleResp))
	    return;

    }

    private void addPrivilege(String privilegeToAdd) throws Exception {
	LOG.debug("# EditRolePrivilegePanel.addPrivilege()");
	AddUmgrCustomerPrivilegeRequest addPrevReq = getMobBasePage()
		.getNewMobiliserRequest(AddUmgrCustomerPrivilegeRequest.class);
	CustomerPrivilege custPriv = new CustomerPrivilege();
	custPriv.setCustomerId(getMobBasePage().getMobiliserWebSession()
		.getCustomer().getId());
	custPriv.setPrivilege(privilegeToAdd);
	custPriv.setOrgUnit(Constants.DEFAULT_ORGUNIT);
	addPrevReq.setCustomerPrivilege(custPriv);
	AddUmgrCustomerPrivilegeResponse addPrivResp = getMobBasePage().wsRolePrivilegeClient
		.addUmgrCustomerPrivilege(addPrevReq);
	if (!getMobBasePage().evaluateMobiliserResponse(addPrivResp))
	    return;

    }

    private void removePrivilege(String privilegeToRemove) throws Exception {
	LOG.debug("# EditRolePrivilegePanel.removePrivilege()");
	RemoveUmgrCustomerPrivilegeRequest remPrevReq = getMobBasePage()
		.getNewMobiliserRequest(
			RemoveUmgrCustomerPrivilegeRequest.class);
	remPrevReq.setCustomerId(getMobBasePage().getMobiliserWebSession()
		.getCustomer().getId());
	remPrevReq.setOrgUnit(Constants.DEFAULT_ORGUNIT);
	remPrevReq.setUmgrPrivilege(privilegeToRemove);
	RemoveUmgrCustomerPrivilegeResponse remPrevResp = getMobBasePage().wsRolePrivilegeClient
		.removeUmgrCustomerPrivilege(remPrevReq);
	if (!getMobBasePage().evaluateMobiliserResponse(remPrevResp))
	    return;

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

}
