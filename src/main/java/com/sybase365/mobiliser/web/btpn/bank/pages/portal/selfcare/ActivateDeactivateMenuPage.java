package com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.util.WildcardListModel;
import org.slf4j.Logger;

import com.sybase365.mobiliser.money.contract.v5_0.customer.CreateUmgrRolePrivilegeRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.CreateUmgrRolePrivilegeResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.DeleteUmgrRolePrivilegeRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.DeleteUmgrRolePrivilegeResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetUmgrPrivilegesRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetUmgrPrivilegesResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetUmgrRolePrivilegesRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetUmgrRolePrivilegesResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.UmgrPrivilege;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.UmgrRolePrivilege;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnCustomer;
import com.sybase365.mobiliser.web.btpn.util.BtpnLocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.btpn.util.CodeValueValueComparator;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the Page for Activating and Deactivating a Menu for Bank User
 * 
 * @author Narasa Reddy
 */
public class ActivateDeactivateMenuPage extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	private CodeValue roleSelected;

	private Component rolesDrodpdown;

	private CodeValue taskSelected;

	private Component allPrivsDrodpdown;

	private CodeValue privSelected;

	private DropDownChoice<CodeValue> rolePrivsDrodpdown;

	private FeedbackPanel feedbackPanel;

	private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(ActivateDeactivateMenuPage.class);

	/**
	 * Default Constructor for this page.
	 */
	public ActivateDeactivateMenuPage() {
		super();
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	@Override
	protected void initOwnPageComponents() {
		super.initOwnPageComponents();
		constructPage();

	}

	@SuppressWarnings("unchecked")
	private void constructPage() {
		final Form<ActivateDeactivateMenuPage> form = new Form<ActivateDeactivateMenuPage>("activateForm",
			new CompoundPropertyModel<ActivateDeactivateMenuPage>(this));

		form.add(feedbackPanel = new FeedbackPanel("errorMessages"));
		feedbackPanel.setOutputMarkupId(true);

		// Current customer.
		BtpnCustomer btpnCustomer = this.getMobiliserWebSession().getBtpnLoggedInCustomer();
		final IChoiceRenderer<CodeValue> codeValueChoiceRender = new ChoiceRenderer<CodeValue>(
			BtpnConstants.DISPLAY_EXPRESSION, BtpnConstants.DISPLAY_EXPRESSION);

		// Roles dropdown choice
		final String prefsUserName = this.getBankPortalPrefsConfig().getDefaultSuperAdmin();
		final String allRoles;
		allRoles = prefsUserName.equals(btpnCustomer.getUsername()) ? BtpnConstants.RESOURCE_BUNDLE_BANK_ADMIN : BtpnConstants.RESOURCE_BUNDLE_BANK_USER_ALL_ROLES;
		form.add(rolesDrodpdown = new BtpnLocalizableLookupDropDownChoice<CodeValue>("roleSelected", CodeValue.class,
			allRoles, this, false, true).setChoiceRenderer(codeValueChoiceRender).add(new ErrorIndicator())
			.setOutputMarkupId(true));

		// All priviliges dropdown choice
		form.add(allPrivsDrodpdown = new DropDownChoice<CodeValue>("taskSelected", fetchAllPrivileges(),
			codeValueChoiceRender).add(new ErrorIndicator()).setOutputMarkupId(true));

		// Priviliges for the selected role dropdown
		form.add(rolePrivsDrodpdown = (DropDownChoice<CodeValue>) new DropDownChoice<CodeValue>("privSelected",
			new ArrayList<CodeValue>(), codeValueChoiceRender).setChoiceRenderer(codeValueChoiceRender)
			.add(new ErrorIndicator()).setOutputMarkupId(true));
		form.add(addFindTaskButton());
		form.add(addAssignTaskButton());
		form.add(addRemoveTaskButton());
		add(form);
	}

	/**
	 * This method find the privileges for the selected role and populates the role privileges dropdown.
	 */
	protected Button addFindTaskButton() {
		AjaxButton submitButton = new AjaxButton("findTask") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				handleFindTask(target);
			}
		};
		return submitButton;
	}

	/**
	 * This method assigns a privilege to a role.
	 */
	protected Button addAssignTaskButton() {
		AjaxButton submitButton = new AjaxButton("addTask") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				handleAssignTask(target);
			}
		};
		return submitButton;
	}

	/**
	 * This method removes a privilege to a role.
	 */
	protected Button addRemoveTaskButton() {
		AjaxButton submitButton = new AjaxButton("removeTask") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				handleRemoveTask(target);
			}
		};
		return submitButton;
	}

	/**
	 * Fetch all the privileges from database.
	 * 
	 * @return List<CodeValue> list of privs.
	 */
	private List<CodeValue> fetchAllPrivileges() {
		final List<CodeValue> allPrivList = new ArrayList<CodeValue>();
		try {
			final GetUmgrPrivilegesRequest request = getNewMobiliserRequest(GetUmgrPrivilegesRequest.class);
			GetUmgrPrivilegesResponse response = getUmgrRolesPrivsEndPoint().getUmgrPrivileges(request);
			if (evaluateBankPortalMobiliserResponse(response)) {
				final List<UmgrPrivilege> umgrPrivList = response.getPrivileges();
				for (UmgrPrivilege priv : umgrPrivList) {
					allPrivList.add(new CodeValue(priv.getPrivilege(), priv.getPrivilege()));
				}
				Collections.sort(allPrivList, new CodeValueValueComparator(true));
			} else {
				error(getLocalizer().getString("error.all.privs", this));
			}
		} catch (Exception e) {
			LOG.error("An error occured while fetching all privileges == >", e);
			error(getLocalizer().getString("error.exception", this));
		}
		return allPrivList;
	}

	/**
	 * Find tasks for selected role..
	 */
	private void handleFindTask(AjaxRequestTarget target) {
		// Check role selected
		if (!checkRoleSelected(target)) {
			return;
		}
		// get privs for that role
		final List<CodeValue> allPrivList = new ArrayList<CodeValue>();
		try {
			final GetUmgrRolePrivilegesRequest request = getNewMobiliserRequest(GetUmgrRolePrivilegesRequest.class);
			request.setRoleId(roleSelected.getValue());
			GetUmgrRolePrivilegesResponse response = getUmgrRolesPrivsEndPoint().getUmgrRolePrivileges(request);
			if (evaluateBankPortalMobiliserResponse(response)) {
				final List<UmgrRolePrivilege> umgrRolePrivList = response.getRolePrivileges();
				for (UmgrRolePrivilege rolePriv : umgrRolePrivList) {
					allPrivList.add(new CodeValue(rolePriv.getPrivilege(), rolePriv.getPrivilege()));
				}
				Collections.sort(allPrivList, new CodeValueValueComparator(true));
				info(getLocalizer().getString("success.find.privs", this));
			} else {
				error(getLocalizer().getString("error.find.privs", this));
			}
		} catch (Exception e) {
			LOG.error("An error occured while fetching role privileges == >", e);
			error(getLocalizer().getString("error.exception", this));
		}
		rolePrivsDrodpdown.getChoices().clear();
		rolePrivsDrodpdown.setChoices(new WildcardListModel<CodeValue>(allPrivList));
		target.addComponent(rolesDrodpdown);
		target.addComponent(rolePrivsDrodpdown);
		target.addComponent(allPrivsDrodpdown);
		target.addComponent(feedbackPanel);
	}

	/**
	 * Assign task for selected
	 */
	private void handleRemoveTask(AjaxRequestTarget target) {
		if (!checkRoleSelected(target) || !checkRemovePrivSelected(target)) {
			return;
		}
		try {
			final DeleteUmgrRolePrivilegeRequest request = getNewMobiliserRequest(DeleteUmgrRolePrivilegeRequest.class);
			request.setRoleId(roleSelected.getValue());
			request.setPrivilegeId(privSelected.getId());
			final DeleteUmgrRolePrivilegeResponse response = getUmgrRolesPrivsEndPoint().deleteUmgrRolePrivilege(
				request);
			if (evaluateBankPortalMobiliserResponse(response)) {
				info(getLocalizer().getString("success.remove.privs", this));
			} else {
				error(getLocalizer().getString("error.remove.privs", this));
			}
		} catch (Exception e) {
			LOG.error("An error occured while removing role privileges == >", e);
			error(getLocalizer().getString("error.exception", this));
		}
		target.addComponent(rolesDrodpdown);
		target.addComponent(rolePrivsDrodpdown);
		target.addComponent(allPrivsDrodpdown);
		target.addComponent(feedbackPanel);
	}

	/**
	 * Assign task for selected
	 */
	private void handleAssignTask(AjaxRequestTarget target) {
		if (!checkRoleSelected(target) || !checkAddPrivSelected(target)) {
			return;
		}
		try {
			final CreateUmgrRolePrivilegeRequest request = getNewMobiliserRequest(CreateUmgrRolePrivilegeRequest.class);
			final UmgrRolePrivilege umgrRolePriv = new UmgrRolePrivilege();
			umgrRolePriv.setRole(roleSelected.getValue());
			umgrRolePriv.setPrivilege(taskSelected.getId());
			request.setRolePrivilege(umgrRolePriv);
			final CreateUmgrRolePrivilegeResponse response = getUmgrRolesPrivsEndPoint().createUmgrRolePrivilege(
				request);
			if (evaluateBankPortalMobiliserResponse(response)) {
				info(getLocalizer().getString("success.add.privs", this));
			} else {
				handleSpecificErrorMessage(response.getStatus().getCode());
			}
		} catch (Exception e) {
			LOG.error("An error occured while adding role privileges == >", e);
			error(getLocalizer().getString("error.exception", this));
		}
		target.addComponent(rolesDrodpdown);
		target.addComponent(rolePrivsDrodpdown);
		target.addComponent(allPrivsDrodpdown);
		target.addComponent(feedbackPanel);
	}

	/**
	 * Check role selected.
	 * 
	 * @return true selected
	 */
	private boolean checkAddPrivSelected(AjaxRequestTarget target) {
		if (!PortalUtils.exists(taskSelected)) {
			allPrivsDrodpdown.error(getLocalizer().getString("taskSelected.Required", this));
			target.addComponent(rolesDrodpdown);
			target.addComponent(rolePrivsDrodpdown);
			target.addComponent(allPrivsDrodpdown);
			target.addComponent(feedbackPanel);
			return false;
		}
		return true;
	}

	/**
	 * Check role selected.
	 * 
	 * @return true selected
	 */
	private boolean checkRoleSelected(AjaxRequestTarget target) {
		if (!PortalUtils.exists(roleSelected)) {
			rolesDrodpdown.error(getLocalizer().getString("roleSelected.Required", this));
			target.addComponent(rolesDrodpdown);
			target.addComponent(rolePrivsDrodpdown);
			target.addComponent(allPrivsDrodpdown);
			target.addComponent(feedbackPanel);
			return false;
		}
		return true;
	}

	/**
	 * Check role selected.
	 * 
	 * @return true selected
	 */
	private boolean checkRemovePrivSelected(AjaxRequestTarget target) {
		if (!PortalUtils.exists(privSelected)) {
			rolePrivsDrodpdown.error(getLocalizer().getString("privSelected.Required", this));
			target.addComponent(rolesDrodpdown);
			target.addComponent(rolePrivsDrodpdown);
			target.addComponent(allPrivsDrodpdown);
			target.addComponent(feedbackPanel);
			return false;
		}
		return true;
	}

	public CodeValue getRoleSelected() {
		return roleSelected;
	}

	public void setRoleSelected(CodeValue roleSelected) {
		this.roleSelected = roleSelected;
	}

	public CodeValue getTaskSelected() {
		return taskSelected;
	}

	public void setTaskSelected(CodeValue taskSelected) {
		this.taskSelected = taskSelected;
	}

	public CodeValue getPrivSelected() {
		return privSelected;
	}

	public void setPrivSelected(CodeValue privSelected) {
		this.privSelected = privSelected;
	}

	/**
	 * This method handles the specific error message.
	 */
	private void handleSpecificErrorMessage(final int errorCode) {
		// Specific error message handling
		final String messageKey = "error.assign." + errorCode;
		String message = getLocalizer().getString(messageKey, this);
		// Generice error messages
		if (messageKey.equals(message)) {
			message = getLocalizer().getString("error.assign.privs", this);
		}
		error(message);
	}

}
