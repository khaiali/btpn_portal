package com.sybase365.mobiliser.web.consumer.pages.portal.selfcare;

import java.util.LinkedList;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.sybase365.mobiliser.money.services.api.ISecurityEndpoint;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.IMenuEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.MenuEntry;
import com.sybase365.mobiliser.web.consumer.pages.portal.BaseConsumerPortalPage;
import com.sybase365.mobiliser.web.consumer.pages.portal.selfcare.alerts.ContactPointsPage;
import com.sybase365.mobiliser.web.consumer.pages.portal.selfcare.alerts.MobileAlertsPage;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_CONSUMER_LOGIN)
public class BaseSelfCarePage extends BaseConsumerPortalPage {

    @SpringBean(name = "systemAuthCustomerContextClient")
    protected ISecurityEndpoint wsSystemAuthSecurityClient;

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(BaseSelfCarePage.class);

    public BaseSelfCarePage() {
	super();
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    public BaseSelfCarePage(final PageParameters parameters) {
	super(parameters);
    }

    @SuppressWarnings("unchecked")
    @Override
    public LinkedList<IMenuEntry> buildLeftMenu() {

	LOG.debug("#BaseSelfCarePage.buildLeftMenu()");

	LinkedList<IMenuEntry> entries = new LinkedList<IMenuEntry>();

	entries.add(new MenuEntry(getLocalizer().getString(
		"consumer.portal.leftmenu.home", this),
		Constants.PRIV_CONSUMER_LOGIN, ConsumerHomePage.class));
	entries.add(new MenuEntry(getLocalizer().getString(
		"consumer.portal.leftmenu.friendslist", this),
		Constants.PRIV_FRIENDS_LIST, FriendsListPage.class));
	entries.add(new MenuEntry(getLocalizer().getString(
		"consumer.portal.leftmenu.changepassword", this),
		Constants.PRIV_CHANGE_PASSWORD, ChangePasswordPage.class));
	entries.add(new MenuEntry(getLocalizer().getString(
		"consumer.portal.leftmenu.changesecquestion", this),
		Constants.PRIV_CHANGE_SECQANDA, ChangeSeqQuestionPage.class));
	entries.add(new MenuEntry(getLocalizer().getString(
		"consumer.portal.leftmenu.changepin", this),
		Constants.PRIV_CHANGE_PIN, ChangePinPage.class));
	entries.add(new MenuEntry(getLocalizer().getString(
		"consumer.portal.leftmenu.changeaddress", this),
		Constants.PRIV_CHANGE_ADDRESS, ChangeAddressPage.class));
	entries.add(new MenuEntry(getLocalizer().getString(
		"consumer.portal.leftmenu.changepreferences", this),
		Constants.PRIV_CHANGE_PREFERENCES, ChangePreferencesPage.class));
	entries.add(new MenuEntry(getLocalizer().getString(
		"consumer.portal.leftmenu.showhistory", this),
		Constants.PRIV_SHOW_HISTORY, ShowHistoryPage.class));
	entries.add(new MenuEntry(getLocalizer().getString(
		"consumer.portal.leftmenu.contactpoints", this),
		Constants.PRIV_FRIENDS_LIST, ContactPointsPage.class));
	entries.add(new MenuEntry(getLocalizer().getString(
		"consumer.portal.leftmenu.mobilealerts", this),
		Constants.PRIV_FRIENDS_LIST, MobileAlertsPage.class));
	for (IMenuEntry entry : entries) {
	    if (entry instanceof MenuEntry) {
		if (((MenuEntry) entry).getPage().equals(getActiveMenu())) {
		    entry.setActive(true);
		}
	    }
	}

	return entries;
    }

}
