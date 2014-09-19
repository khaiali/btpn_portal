package com.sybase365.mobiliser.web.cst.pages.usermanager;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.money.contract.v5_0.customer.CreateUmgrRoleRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.CreateUmgrRoleResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.UpdateUmgrRoleRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.UpdateUmgrRoleResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.UmgrRole;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.util.Constants;

public class CreateRolePage extends BaseUserManagerPage {
    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(CreateRolePage.class);
    private UmgrRole umgrRole;
    private boolean isEdit = false;

    public CreateRolePage() {
	super();

    }

    public CreateRolePage(UmgrRole umgrRole) {
	super();
	this.umgrRole = umgrRole;
	if (umgrRole != null) {
	    isEdit = true;
	}
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();

	Form<?> form = new Form("createRoleForm",
		new CompoundPropertyModel<CreateRolePage>(this));
	form.add(new FeedbackPanel("errorMessages"));

	form.add(new RequiredTextField<String>("umgrRole.role")
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	form.add(new RequiredTextField<String>("umgrRole.description")
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	form.add(new Button("createRole") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		createRole();
	    }
	});
	add(form);

    }

    private void createRole() {
	if (!isEdit) {
	    LOG.debug("# CreateRolePage.createRole()");
	    try {
		CreateUmgrRoleResponse response;
		CreateUmgrRoleRequest request = getNewMobiliserRequest(CreateUmgrRoleRequest.class);
		String role = umgrRole.getRole().toUpperCase();
		umgrRole.setRole(role);

		request.setRole(umgrRole);
		response = wsRolePrivilegeClient.createUmgrRole(request);
		if (!evaluateMobiliserResponse(response)) {
		    LOG.warn("#An error occurred while creating a new role");
		    return;
		}
		LOG.info("# Successfully created a new role");
		setResponsePage(new EditRolePage(umgrRole));
	    } catch (Exception e) {
		error(getLocalizer().getString("ERROR.CREATE_ROLE_FAILURE",
			this));
		LOG.error("#An error occurred while creating a new role");
	    }

	} else {
	    LOG.debug("# CreateRolePage.updateRole()");
	    try {
		UpdateUmgrRoleResponse res;
		UpdateUmgrRoleRequest req = getNewMobiliserRequest(UpdateUmgrRoleRequest.class);

		req.setRole(umgrRole);
		res = wsRolePrivilegeClient.updateUmgrRole(req);
		if (!evaluateMobiliserResponse(res)) {
		    LOG.warn("#An error occurred while updating a new role");
		    return;
		}
		LOG.info("# Successfully created a new role");
		setResponsePage(new EditRolePage(umgrRole));
	    } catch (Exception e) {
		error(getLocalizer().getString("ERROR.UPDATE_ROLE_FAILURE",
			this));
		LOG.error("#An error occurred while updating a new role");
	    }
	}
    }

    public UmgrRole getUmgrRole() {
	return umgrRole;
    }

    public void setUmgrRole(UmgrRole umgrRole) {
	this.umgrRole = umgrRole;
    }
}
