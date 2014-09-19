package com.sybase365.mobiliser.web.cst.pages.systemconfig.mbanking;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;

import com.sybase365.mobiliser.mbanking.contract.v5_0.beans.OptInSetting;
import com.sybase365.mobiliser.web.common.panels.mbanking.EditOptinSettingsPanel;
import com.sybase365.mobiliser.web.consumer.pages.portal.selfcare.BaseSelfCarePage;
import com.sybase365.mobiliser.web.util.Constants;

/**
 * @author sagraw03
 */
@AuthorizeInstantiation(Constants.PRIV_CST_MBANKING)
public class EditBankTermAndConditionPage extends BaseSelfCarePage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(EditBankTermAndConditionPage.class);
    private OptInSetting optInSettings;
    private static String ACTION = "Edit";

    private EditOptinSettingsPanel editOptinSettingsPanel;

    public EditBankTermAndConditionPage(OptInSetting optInSettings) {
	super();
	LOG.debug("# Inside EditBankTermAndConditionPage initOwnPageComponents");
	add(new Label("help", ACTION
		+ " "
		+ optInSettings.getCompanyName()
		+ " "
		+ getLocalizer().getString(
			"bank.optin.requirments.action.edit.help", this)));
	editOptinSettingsPanel = new EditOptinSettingsPanel(
		"editOptinSettingsPanel", this, optInSettings, getLocalizer()
			.getString("bank.optin.requirments.action.edit.help",
				this));

	add(editOptinSettingsPanel);
    }

    public EditBankTermAndConditionPage(PageParameters parameters) {
	super(parameters);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Class<EditBankTermAndConditionPage> getActiveMenu() {
	return EditBankTermAndConditionPage.class;
    }

    public void setOptInSettings(OptInSetting optInSettings) {
	this.optInSettings = optInSettings;
    }

    public OptInSetting getOptInSettings() {
	return optInSettings;
    }

}
