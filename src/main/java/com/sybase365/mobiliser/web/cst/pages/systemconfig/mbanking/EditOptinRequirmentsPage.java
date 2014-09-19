package com.sybase365.mobiliser.web.cst.pages.systemconfig.mbanking;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;

import com.sybase365.mobiliser.mbanking.contract.v5_0.beans.OptInSetting;
import com.sybase365.mobiliser.web.common.panels.mbanking.EditOptinSettingsPanel;
import com.sybase365.mobiliser.web.cst.pages.systemconfig.BaseSystemConfigurationPage;
import com.sybase365.mobiliser.web.util.Constants;

/**
 * @author sagraw03
 */
@AuthorizeInstantiation(Constants.PRIV_CST_MBANKING)
public class EditOptinRequirmentsPage extends BaseSystemConfigurationPage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(EditOptinRequirmentsPage.class);

    private EditOptinSettingsPanel editOptinSettingsPanel;
    private static String ACTION = "Edit";

    public EditOptinRequirmentsPage(OptInSetting optInSettings) {
	super();
	LOG.debug("# Inside EditOptinRequirmentsPage initOwnPageComponents");

	add(new Label("help", ACTION
		+ " "
		+ optInSettings.getCompanyName()
		+ " "
		+ getLocalizer().getString(
			"carrier.optin.requirments.action.edit.help", this)));
	editOptinSettingsPanel = new EditOptinSettingsPanel(
		"editOptinSettingsPanel", this, optInSettings, getLocalizer()
			.getString(
				"carrier.optin.requirments.action.edit.help",
				this));

	add(editOptinSettingsPanel);
    }

    public EditOptinRequirmentsPage(PageParameters parameters) {
	super(parameters);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Class<EditOptinRequirmentsPage> getActiveMenu() {
	return EditOptinRequirmentsPage.class;
    }

}
