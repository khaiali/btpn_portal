package com.sybase365.mobiliser.web.cst.pages.usermanager;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.money.contract.v5_0.customer.CreateUmgrPrivilegeRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.CreateUmgrPrivilegeResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.UpdateUmgrPrivilegeRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.UpdateUmgrPrivilegeResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.UmgrPrivilege;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.util.Constants;

public class CreatePrivilegePage extends BaseUserManagerPage {
    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(CreatePrivilegePage.class);
    private UmgrPrivilege umgrPrivilege;
    private boolean isEdit = false;

    public CreatePrivilegePage() {
	super();

    }

    public CreatePrivilegePage(UmgrPrivilege umgrPrivilege) {
	super();
	this.umgrPrivilege = umgrPrivilege;
	if (umgrPrivilege != null) {
	    isEdit = true;
	}

    }

    @SuppressWarnings("unchecked")
    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();

	Form<?> form = new Form("createPrivilegeForm",
		new CompoundPropertyModel<CreateRolePage>(this));
	form.add(new FeedbackPanel("errorMessages"));

	form.add(new RequiredTextField<String>("umgrPrivilege.privilege")
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	form.add(new RequiredTextField<String>("umgrPrivilege.description")
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	form.add(new Button("createPrivilege") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		createPrivilege();
	    }
	});
	add(form);

    }

    private void createPrivilege() {
	if (!isEdit) {
	    LOG.debug("# CreatePrivilegePage.createPrivilege()");

	    try {
		CreateUmgrPrivilegeResponse response;
		CreateUmgrPrivilegeRequest request = getNewMobiliserRequest(CreateUmgrPrivilegeRequest.class);
		String privilege = umgrPrivilege.getPrivilege().toUpperCase();
		umgrPrivilege.setPrivilege(privilege);

		request.setPrivilege(umgrPrivilege);
		response = wsRolePrivilegeClient.createUmgrPrivilege(request);
		if (!evaluateMobiliserResponse(response)) {
		    LOG.warn("#An error occurred while creating a new role");
		    return;
		}
		LOG.info("# Successfully created a new privilege");
		setResponsePage(new EditPrivilegePage(umgrPrivilege));
	    } catch (Exception ex) {
		error(getLocalizer().getString(
			"ERROR.CREATE_PRIVILEGE_FAILURE", this));
		LOG.error("#An error occurred while creating a new privilege");
	    }

	} else {
	    LOG.debug("# CreateRolePage.updatePrivilege()");

	    try {
		UpdateUmgrPrivilegeResponse res;
		UpdateUmgrPrivilegeRequest req = getNewMobiliserRequest(UpdateUmgrPrivilegeRequest.class);

		req.setPrivilege(umgrPrivilege);
		res = wsRolePrivilegeClient.updateUmgrPrivilege(req);
		if (!evaluateMobiliserResponse(res)) {
		    LOG.warn("#An error occurred while updating a new role");
		    return;
		}
		LOG.info("# Successfully updated a new privilege");
		setResponsePage(new EditPrivilegePage(umgrPrivilege));
	    } catch (Exception ex) {
		error(getLocalizer().getString(
			"ERROR.UPDATE_PRIVILEGE_FAILURE", this));
		LOG.error("#An error occurred while updating a new privilege");
	    }

	}
    }

    public UmgrPrivilege getUmgrPrivilege() {
	return umgrPrivilege;
    }

    public void setUmgrPrivilege(UmgrPrivilege umgrPrivilege) {
	this.umgrPrivilege = umgrPrivilege;
    }
}
