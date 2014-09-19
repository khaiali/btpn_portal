package com.sybase365.mobiliser.web.common.panels.mbanking;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.mbanking.contract.v5_0.beans.ServicePackage;
import com.sybase365.mobiliser.mbanking.contract.v5_0.beans.SupportedAlert;
import com.sybase365.mobiliser.mbanking.contract.v5_0.beans.SupportedAlertList;
import com.sybase365.mobiliser.mbanking.contract.v5_0.beans.SupportedChannel;
import com.sybase365.mobiliser.mbanking.contract.v5_0.beans.SupportedChannelList;
import com.sybase365.mobiliser.mbanking.contract.v5_0.beans.SupportedOperation;
import com.sybase365.mobiliser.mbanking.contract.v5_0.beans.SupportedOperationList;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.application.clients.MBankingClientLogic;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.common.components.KeyValue;
import com.sybase365.mobiliser.web.common.components.KeyValueDropDownChoice;
import com.sybase365.mobiliser.web.common.components.LocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.cst.pages.systemconfig.mbanking.ManageServicePackagesPage;

public class BankServicePackagePanel extends Panel {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(BankServicePackagePanel.class);

    private MBankingClientLogic mBankingClientLogic;

    private ServicePackage servicePackage;
    private String action;
    private boolean esDefaultServicePackage;
    private KeyValueDropDownChoice<Integer, Integer> supportedDevicesNumber;
    private MobiliserBasePage basePage;

    private List<SupportedAlert> previousAlertList;
    private List<SupportedAlert> newAlertList;
    private List<SupportedAlert> allAlertList;

    private List<SupportedChannel> previousSupportedChannelList;
    private List<SupportedChannel> newSupportedChannelList;
    private List<SupportedChannel> allSupportedChannelList;

    private List<SupportedOperation> previousSupportedOperationList;
    private List<SupportedOperation> newSupportedOperationList;
    private List<SupportedOperation> allSupportedOperationList;

    private static final String SPACE = " ";

    private static final String WICKET_ID_esPackageDiv = "esPackageDiv";
    private static final String WICKET_ID_esPackageForm = "esPackageForm";

    private static final String WICKET_ID_esPackageInfoContainer = "esPackageInfoContainer";
    private static final String WICKET_ID_esSupportedChanelContainer = "esSupportedChanelContainer";
    private static final String WICKET_ID_esSupportedChanelDiv = "esSupportedChanelDiv";
    private static final String WICKET_ID_esChannelList = "esChannelList";
    private static final String WICKET_ID_esSupportedChanelName = "esSupportedChanelName";
    private static final String WICKET_ID_esSupportedOperationContainer = "esSupportedOperationContainer";
    private static final String WICKET_ID_esSupportedOperationDiv = "esSupportedOperationDiv";
    private static final String WICKET_ID_esOperationList = "esOperationList";
    private static final String WICKET_ID_esSupportedOperationName = "esSupportedOperationName";

    private static final String WICKET_ID_esSupportedAlertContainer = "esSupportedAlertContainer";
    private static final String WICKET_ID_esSupportedAlertDiv = "esSupportedAlertDiv";
    private static final String WICKET_ID_esAlertList = "esAlertList";
    private static final String WICKET_ID_esAlertlName = "esAlertlName";

    public BankServicePackagePanel(String id,
	    MBankingClientLogic mBankingClientLogic,
	    MobiliserBasePage basePage, ServicePackage servicePackage,
	    String action) {
	super(id);
	this.mBankingClientLogic = mBankingClientLogic;
	this.servicePackage = servicePackage;
	this.action = action;
	this.basePage = basePage;
	this.constructPanel();
    }

    private void constructPanel() {
	WebMarkupContainer markupContainer = new WebMarkupContainer(
		WICKET_ID_esPackageDiv);
	final Form<BankServicePackagePanel> form = new Form<BankServicePackagePanel>(
		WICKET_ID_esPackageForm,
		new CompoundPropertyModel<BankServicePackagePanel>(this));
	form.add(new FeedbackPanel("errorMessages"));
	form.add(new Label("title", action + SPACE
		+ getLocalizer().getString("servicePackage.title", this)));
	String helpMessage;
	if (servicePackage.getName() != null) {
	    helpMessage = action + SPACE
		    + getLocalizer().getString("servicePackage.title", this)
		    + SPACE + servicePackage.getName();
	} else {
	    helpMessage = action + SPACE
		    + getLocalizer().getString("servicePackage.title", this);

	}
	WebMarkupContainer servicePackageInfoContainer = new WebMarkupContainer(
		WICKET_ID_esPackageInfoContainer);
	servicePackageInfoContainer.add(new Label("help", helpMessage));
	this.createServicePackageInfoContainerView(servicePackageInfoContainer,
		servicePackage);
	form.add(servicePackageInfoContainer);

	WebMarkupContainer supportedChanelContainer = new WebMarkupContainer(
		WICKET_ID_esSupportedChanelContainer);
	this.createSupportedChanelContainerView(supportedChanelContainer);

	WebMarkupContainer supportedOperationContainer = new WebMarkupContainer(
		WICKET_ID_esSupportedOperationContainer);
	this.createSupportedOperationContainerView(supportedOperationContainer);

	WebMarkupContainer supportedAlertContainer = new WebMarkupContainer(
		WICKET_ID_esSupportedAlertContainer);
	this.createSupportedAlertContainerView(supportedAlertContainer);

	Button editBankServicePackage = new Button("esConfirm") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleEditBankService();
	    }
	};
	editBankServicePackage.setVisible(false);
	Button addBankServicePackage = new Button("asConfirm") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleEditBankService();
	    }
	};
	addBankServicePackage.setVisible(false);
	form.add(addBankServicePackage);

	form.add(editBankServicePackage);
	if ("add".equalsIgnoreCase(action)) {
	    addBankServicePackage.setVisible(true);
	} else {
	    editBankServicePackage.setVisible(true);
	}

	form.add(new Button("esBack") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		setResponsePage(ManageServicePackagesPage.class);
	    };
	}.setDefaultFormProcessing(false));

	form.add(supportedChanelContainer);
	form.add(supportedOperationContainer);
	form.add(supportedAlertContainer);
	markupContainer.add(form);
	add(markupContainer);
    }

    private void createServicePackageInfoContainerView(
	    WebMarkupContainer parent, ServicePackage servicePackage) {
	parent.add(new TextField<String>("servicePackage.name").setRequired(
		true).add(new ErrorIndicator()));
	TextField<String> servicePackageId = new TextField<String>(
		"servicePackage.servicePackageId");
	servicePackageId.setRequired(true).setEnabled(false).add(
		new ErrorIndicator());
	if ("add".equalsIgnoreCase(action)) {
	    servicePackageId.setEnabled(true);
	}

	parent.add(servicePackageId);
	parent.add(new CheckBox("servicePackage.default").setEnabled(true));

    }

    @SuppressWarnings("unchecked")
    private void createSupportedAlertContainerView(WebMarkupContainer parent) {
	WebMarkupContainer supportedAlertContainer = new WebMarkupContainer(
		WICKET_ID_esSupportedAlertDiv);
	parent.add(supportedAlertContainer);
	newAlertList = new ArrayList<SupportedAlert>();
	CheckGroup<SupportedAlert> supportedAlertcheckGroup = new CheckGroup<SupportedAlert>(
		"checkgroup", newAlertList);

	final ListView supportedAlertList = new ListView(WICKET_ID_esAlertList,
		getAllAlertList()) {

	    private static final long serialVersionUID = 1L;

	    protected void populateItem(ListItem item) {

		final SupportedAlert supportedAlert = (SupportedAlert) item
			.getModelObject();
		item.add(new Check<SupportedAlert>("check", item.getModel()) {

		    private static final long serialVersionUID = 1L;

		    @Override
		    protected void onComponentTag(ComponentTag tag) {
			if (isAlertSelected(supportedAlert)) {
			    tag.put("checked", "checked");
			}
			super.onComponentTag(tag);
		    }
		});

		item
			.add(new Label(
				WICKET_ID_esAlertlName,
				getLocalizer()
					.getString(
						"servicePackage."
							+ supportedAlert
								.getAlertName(),
						this)));
	    }
	};
	supportedAlertcheckGroup.add(supportedAlertList);
	supportedAlertContainer.add(supportedAlertcheckGroup);

    }

    @SuppressWarnings("unchecked")
    private void createSupportedOperationContainerView(WebMarkupContainer parent) {
	WebMarkupContainer supportedOperationContainer = new WebMarkupContainer(
		WICKET_ID_esSupportedOperationDiv);
	parent.add(supportedOperationContainer);
	newSupportedOperationList = new ArrayList<SupportedOperation>();
	CheckGroup<SupportedOperation> supportedOperationcheckGroup = new CheckGroup<SupportedOperation>(
		"checkgroup", newSupportedOperationList);

	final ListView supportedOperationList = new ListView(
		WICKET_ID_esOperationList, getAllSupportedOperationList()) {

	    private static final long serialVersionUID = 1L;

	    protected void populateItem(ListItem item) {

		final SupportedOperation supportedOperation = (SupportedOperation) item
			.getModelObject();
		item.add(new Check<String>("check", item.getModel()) {

		    private static final long serialVersionUID = 1L;

		    @Override
		    protected void onComponentTag(ComponentTag tag) {
			if (isSupportedOperationSelected(supportedOperation)) {
			    tag.put("checked", "checked");
			}
			super.onComponentTag(tag);
		    }
		});
		item.add(new Label(WICKET_ID_esSupportedOperationName,
			supportedOperation.getOperationName()));
	    }
	};
	supportedOperationcheckGroup.add(supportedOperationList);
	supportedOperationContainer.add(supportedOperationcheckGroup);
    }

    @SuppressWarnings("unchecked")
    private void createSupportedChanelContainerView(WebMarkupContainer parent) {

	WebMarkupContainer supportedChanelContainer = new WebMarkupContainer(
		WICKET_ID_esSupportedChanelDiv);

	parent.add(supportedChanelContainer);
	newSupportedChannelList = new ArrayList<SupportedChannel>();
	CheckGroup<SupportedChannel> supportedChanelCheckGroup = new CheckGroup<SupportedChannel>(
		"checkgroup", newSupportedChannelList);
	final ListView supportedChannelList = new ListView(
		WICKET_ID_esChannelList, getAllChannelList()) {

	    private static final long serialVersionUID = 1L;

	    protected void populateItem(ListItem item) {

		final SupportedChannel supportedChannel = (SupportedChannel) item
			.getModelObject();
		item.add(new Check<SupportedChannel>("check", item.getModel()) {

		    private static final long serialVersionUID = 1L;

		    @Override
		    protected void onComponentTag(ComponentTag tag) {
			if (isSupportedChannelSelected(supportedChannel)) {
			    tag.put("checked", "checked");
			}
			super.onComponentTag(tag);
		    }
		});

		item.add(new Label(WICKET_ID_esSupportedChanelName,
			supportedChannel.getChannelName()));
	    }
	};

	supportedChanelCheckGroup.add(supportedChannelList);
	supportedChanelContainer.add(supportedChanelCheckGroup);
	parent.add(new LocalizableLookupDropDownChoice<Integer>(
		"servicePackage.supportedDevices", Integer.class,
		"supportedDevices", this, true, true).setRequired(true).add(
		new ErrorIndicator()));

    }

    public List<SupportedAlert> getPreviousAlertList() {
	if (servicePackage.getSupportedAlertList() != null) {
	    previousAlertList = servicePackage.getSupportedAlertList()
		    .getSupportedAlert();
	} else {
	    return null;
	}
	return previousAlertList;
    }

    public List<SupportedAlert> getAllAlertList() {
	allAlertList = new ArrayList<SupportedAlert>();
	List<KeyValue<String, String>> listKeyValue = basePage
		.fetchLookupEntries("alerttypes", null);
	for (KeyValue<String, String> keyValue : listKeyValue) {
	    SupportedAlert supportedAlert = new SupportedAlert();
	    supportedAlert.setAlertName(keyValue.getValue());
	    supportedAlert.setServicePackageId(servicePackage
		    .getServicePackageId());
	    allAlertList.add(supportedAlert);
	}
	return allAlertList;

    }

    public List<SupportedChannel> getPreviousChannelList() {
	if (servicePackage.getSupportedChannelList() != null) {
	    previousSupportedChannelList = servicePackage
		    .getSupportedChannelList().getSupportedChannel();
	} else {
	    return null;
	}
	return previousSupportedChannelList;
    }

    public List<SupportedChannel> getAllChannelList() {
	allSupportedChannelList = new ArrayList<SupportedChannel>();
	List<KeyValue<String, String>> listKeyValue = basePage
		.fetchLookupEntries("servicechannels", null);

	for (KeyValue<String, String> keyValue : listKeyValue) {
	    SupportedChannel supportedChannel = new SupportedChannel();
	    supportedChannel.setChannelId(keyValue.getKey());
	    supportedChannel.setChannelName(keyValue.getValue());
	    supportedChannel.setServicePackageId(servicePackage
		    .getServicePackageId());
	    allSupportedChannelList.add(supportedChannel);
	}
	return allSupportedChannelList;
    }

    public List<SupportedOperation> getPreviousSupportedOperationList() {
	if (servicePackage.getSupportedOperationList() != null) {
	    previousSupportedOperationList = servicePackage
		    .getSupportedOperationList().getSupportedOperation();
	} else {
	    return null;
	}
	return previousSupportedOperationList;
    }

    public List<SupportedOperation> getAllSupportedOperationList() {
	allSupportedOperationList = new ArrayList<SupportedOperation>();
	List<KeyValue<String, String>> listKeyValue = basePage
		.fetchLookupEntries("serviceoperations", null);

	for (KeyValue<String, String> keyValue : listKeyValue) {
	    SupportedOperation supportedOperation = new SupportedOperation();
	    supportedOperation.setOperationId(Long.valueOf(keyValue.getKey()));
	    supportedOperation.setOperationName(keyValue.getValue());
	    supportedOperation.setServicePackageId(servicePackage
		    .getServicePackageId());
	    allSupportedOperationList.add(supportedOperation);
	}
	return allSupportedOperationList;
    }

    protected boolean isSupportedOperationSelected(
	    SupportedOperation supportedOperation) {
	if (getPreviousSupportedOperationList() != null) {
	    for (SupportedOperation so : getPreviousSupportedOperationList()) {
		if (so.getOperationName() != null
			&& supportedOperation.getOperationName() != null) {
		    if (so.getOperationName().equals(
			    supportedOperation.getOperationName())) {
			return Boolean.TRUE;
		    }
		}
	    }
	}
	return Boolean.FALSE;
    }

    protected boolean isAlertSelected(SupportedAlert supportedAlert) {
	if (getPreviousAlertList() != null) {
	    for (SupportedAlert sa : getPreviousAlertList()) {
		if (sa.getAlertName() != null
			&& supportedAlert.getAlertName() != null) {
		    if (sa.getAlertName().equals(supportedAlert.getAlertName())) {
			return Boolean.TRUE;
		    }
		}
	    }
	}
	return Boolean.FALSE;
    }

    protected boolean isSupportedChannelSelected(
	    SupportedChannel supportedChannel) {
	if (getPreviousChannelList() != null) {
	    for (SupportedChannel sc : getPreviousChannelList()) {
		if (sc.getChannelId() != null
			&& supportedChannel.getChannelId() != null) {
		    if (sc.getChannelId().equals(
			    supportedChannel.getChannelId())) {
			return Boolean.TRUE;
		    }
		}
	    }
	}
	return Boolean.FALSE;
    }

    private void handleEditBankService() {

	SupportedAlertList supportedAlertList = new SupportedAlertList();
	supportedAlertList.getSupportedAlert().addAll(newAlertList);
	servicePackage.setSupportedAlertList(supportedAlertList);

	SupportedChannelList supportedChannelList = new SupportedChannelList();
	supportedChannelList.getSupportedChannel().addAll(
		newSupportedChannelList);
	servicePackage.setSupportedChannelList(supportedChannelList);

	SupportedOperationList supportedOperationList = new SupportedOperationList();
	supportedOperationList.getSupportedOperation().addAll(
		newSupportedOperationList);
	servicePackage.setSupportedOperationList(supportedOperationList);

	if ("add".equalsIgnoreCase(action)) {
	    int status = mBankingClientLogic
		    .addBankServicePackage(servicePackage);
	    if (status == 0) {
		getSession().info(
			servicePackage.getName()
				+ SPACE
				+ getLocalizer().getString(
					"addServicePackage.addServiceSuccess",
					this));
		setResponsePage(ManageServicePackagesPage.class);
	    }
	} else {
	    int status = mBankingClientLogic
		    .updateBankServicePackage(servicePackage);
	    if (status == 0) {
		getSession()
			.info(
				servicePackage.getName()
					+ getLocalizer()
						.getString(
							"editServicePackage.editServiceSuccess",
							this));
		setResponsePage(ManageServicePackagesPage.class);
	    }

	}

    };

    public void setEsDefaultServicePackage(boolean esDefaultServicePackage) {
	this.esDefaultServicePackage = esDefaultServicePackage;
    }

    public boolean isEsDefaultServicePackage() {
	return esDefaultServicePackage;
    }

    public void setServicePackage(ServicePackage servicePackage) {
	this.servicePackage = servicePackage;
    }

    public ServicePackage getServicePackage() {
	return servicePackage;
    }

}
