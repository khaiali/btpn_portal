package com.sybase365.mobiliser.web.cst.pages.customercare;

import java.util.Iterator;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.money.contract.v5_0.wallet.UpdatePaymentInstrumentRequest;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.UpdatePaymentInstrumentResponse;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.UpdateWalletEntryRequest;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.UpdateWalletEntryResponse;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.ExternalAccount;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.PaymentInstrument;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.PendingWalletEntry;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.WalletEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.util.tools.wicketutils.components.BasePage;
import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class ExternalAccountDataPage extends CustomerCareMenuGroup {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(ExternalAccountDataPage.class);
    private WalletEntry walletEntry;
    private String oldAlias;
    Class<? extends BasePage> returnPage = ManageAccountPage.class;
    private boolean isForApproval;
    private String subTitle1 = getLocalizer().getString("manageAccounts.title",
	    this);

    private String subTitle2 = getLocalizer().getString(
	    "menu.cst.approve.wallets", this);

    private String subTitle2BrCrumbSep = getLocalizer().getString(
	    "application.breadcrumb.separator", this);

    private String subTitle3 = getLocalizer().getString(
	    "externalAccountData.title", this);

    public ExternalAccountDataPage(WalletEntry walletEntry,
	    Class<? extends BasePage> returnPage) {

	this.isForApproval = walletEntry instanceof PendingWalletEntry;
	this.returnPage = returnPage;
	this.walletEntry = walletEntry;
	if (PortalUtils.exists(walletEntry))
	    this.oldAlias = walletEntry.getAlias();

	initOwnComponents();
    }

    protected void initOwnComponents() {

	if (isForApproval) {
	    subTitle1 = getLocalizer().getString("menu.cst.approvals", this);
	    subTitle3 = getLocalizer().getString(
		    "pending.externalAccountData.title", this);

	}
	if (PortalUtils.exists(walletEntry)) {
	    subTitle3 = getLocalizer().getString(
		    "externalAccountEditData.title", this);
	}

	add(new Label("h3", getLocalizer().getString(
		"externalAccountData.help", this)).setVisible(!isForApproval));

	Form<?> form = new Form("externalAccountDataForm",
		new CompoundPropertyModel<ExternalAccountDataPage>(this));

	form.add(
		new TextField<String>("walletEntry.alias").add(
			Constants.mediumStringValidator).add(
			Constants.mediumSimpleAttributeModifier)).add(
		new ErrorIndicator());
	form.add(
		new TextField<String>("walletEntry.externalAccount.id1").add(
			Constants.mediumStringValidator).add(
			Constants.mediumSimpleAttributeModifier)).add(
		new ErrorIndicator());
	form.add(
		new TextField<String>("walletEntry.externalAccount.id2").add(
			Constants.mediumStringValidator).add(
			Constants.mediumSimpleAttributeModifier)).add(
		new ErrorIndicator());
	form.add(
		new TextField<String>("walletEntry.externalAccount.id3").add(
			Constants.mediumStringValidator).add(
			Constants.mediumSimpleAttributeModifier)).add(
		new ErrorIndicator());
	form.add(
		new TextField<String>("walletEntry.externalAccount.id4").add(
			Constants.mediumStringValidator).add(
			Constants.mediumSimpleAttributeModifier)).add(
		new ErrorIndicator());
	form.add(
		new TextField<String>("walletEntry.externalAccount.id5").add(
			Constants.mediumStringValidator).add(
			Constants.mediumSimpleAttributeModifier)).add(
		new ErrorIndicator());
	form.add(
		new TextField<String>("walletEntry.externalAccount.id6").add(
			Constants.mediumStringValidator).add(
			Constants.mediumSimpleAttributeModifier)).add(
		new ErrorIndicator());
	form.add(
		new TextField<String>("walletEntry.externalAccount.id7").add(
			Constants.mediumStringValidator).add(
			Constants.mediumSimpleAttributeModifier)).add(
		new ErrorIndicator());
	form.add(
		new TextField<String>("walletEntry.externalAccount.id8").add(
			Constants.mediumStringValidator).add(
			Constants.mediumSimpleAttributeModifier)).add(
		new ErrorIndicator());
	add(new Label("subTitle1", subTitle1));

	add(new Label("subTitle2", subTitle2).setVisible(isForApproval));

	add(new Label("subTitle2BrCrumbSep", subTitle2BrCrumbSep)
		.setVisible(isForApproval));

	add(new Label("subTitle3", subTitle3));

	// adding the submit buttons
	// the back button should not validate the form's input
	form.add(new Button("cancel") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		cancel();
	    };
	}.setDefaultFormProcessing(false).setVisible(!isForApproval));

	form.add(new Button("save") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		save();
	    };
	}.setVisible(!isForApproval));

	form.add(new Button("approve") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		approve(walletEntry, true);
	    };
	}.setDefaultFormProcessing(false).setVisible(isForApproval));

	form.add(new Button("reject") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		approve(walletEntry, false);
	    };
	}.setDefaultFormProcessing(false).setVisible(isForApproval));

	form.add(new FeedbackPanel("errorMessages"));

	if (isForApproval) {
	    Iterator iter = form.iterator();
	    Component component;
	    for (int i = 0; iter.hasNext(); i++) {
		component = (Component) iter.next();

		if (component instanceof TextField) {
		    component.setEnabled(false);
		    component.add(new SimpleAttributeModifier("readonly",
			    "readonly"));
		    component.add(new SimpleAttributeModifier("style",
			    "background-color: #E6E6E6;"));

		}
	    }
	}

	add(form);

    }

    protected void save() {
	CustomerBean customer = getMobiliserWebSession().getCustomer();
	if (!PortalUtils.exists(walletEntry)) {
	    walletEntry = new WalletEntry();
	}
	if (!PortalUtils.exists(walletEntry.getExternalAccount())) {
	    ExternalAccount acc = new ExternalAccount();
	    walletEntry.setExternalAccount(acc);
	}
	walletEntry.getExternalAccount().setActive(true);
	walletEntry.getExternalAccount().setCustomerId(customer.getId());
	walletEntry.getExternalAccount().setCurrency(
		getConfiguration().getCurrency());
	ExternalAccount extAccount = walletEntry.getExternalAccount();
	if (walletEntry.getId() == null) {
	    try {
		extAccount.setType(Constants.PI_TYPE_CST_EXTERNAL_ACC);

		Long weId = createWallet(walletEntry.getAlias(), extAccount,
			customer.getId());
		if (weId != null) {
		    if (weId.longValue() == -1L) {
			getMobiliserWebSession().info(
				getLocalizer().getString("pendingApproval.msg",
					this));

		    } else
			LOG.debug("# Successfully created WalletEntry");
		} else
		    return;
		setResponsePage(returnPage);
	    } catch (Exception e) {
		LOG.error(
			"# An error occurred while creating External Account",
			e);
		error(getLocalizer().getString("create.external.account.error",
			this));
	    }
	} else {
	    try {
		if (updatePaymentInstrument(extAccount))
		    LOG.debug("# Successfully updated External Account");
		else
		    return;
		// if old alias is not null then check it with new alias
		// OR if old alias is null then update wallet entry if new alias
		// is not null
		if ((PortalUtils.exists(oldAlias) && !oldAlias
			.equals(walletEntry.getAlias()))
			|| (!PortalUtils.exists(oldAlias) && PortalUtils
				.exists(walletEntry.getAlias()))) {
		    if (updateWalletEntry(walletEntry))
			LOG.debug("# Successfully updated WalletEntry");
		    else
			return;
		}
		setResponsePage(returnPage);
	    } catch (Exception e) {
		LOG.error(
			"# An error occurred while updating External Account",
			e);
		error(getLocalizer().getString("update.external.account.error",
			this));
	    }
	}

    }

    private boolean updatePaymentInstrument(PaymentInstrument pi)
	    throws Exception {
	UpdatePaymentInstrumentRequest updatePiRequest = getNewMobiliserRequest(UpdatePaymentInstrumentRequest.class);
	updatePiRequest.setPaymentInstrument(pi);
	UpdatePaymentInstrumentResponse updatePiResponse = wsWalletClient
		.updatePaymentInstrument(updatePiRequest);
	if (!evaluateMobiliserResponse(updatePiResponse))
	    return false;
	return true;

    }

    private boolean updateWalletEntry(WalletEntry walletEntry) throws Exception {
	UpdateWalletEntryRequest updateWeRequest = getNewMobiliserRequest(UpdateWalletEntryRequest.class);
	updateWeRequest.setWalletEntry(walletEntry);
	walletEntry.setExternalAccount(null);
	UpdateWalletEntryResponse upWalletResp = wsWalletClient
		.updateWalletEntry(updateWeRequest);

	if (!evaluateMobiliserResponse(upWalletResp))
	    return false;

	return true;

    }

    protected void cancel() {
	setResponsePage(returnPage);
    }

    @Override
    protected Class getActiveMenu() {
	return ManageAccountPage.class;
    }

    public void setWalletEntry(WalletEntry walletEntry) {
	this.walletEntry = walletEntry;
    }

    public WalletEntry getWalletEntry() {
	return walletEntry;
    }
}