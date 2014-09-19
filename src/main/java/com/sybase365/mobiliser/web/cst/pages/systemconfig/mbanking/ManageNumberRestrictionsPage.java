/**
 * 
 */
package com.sybase365.mobiliser.web.cst.pages.systemconfig.mbanking;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.PatternValidator;

import com.sybase365.mobiliser.mbanking.contract.v5_0.beans.RestrictedNumber;
import com.sybase365.mobiliser.mbanking.contract.v5_0.beans.RestrictedNumberList;
import com.sybase365.mobiliser.mbanking.contract.v5_0.beans.ServicePackage;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.application.clients.MBankingClientLogic;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.components.LocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.common.dataproviders.NumberRestrictionsDataProvider;
import com.sybase365.mobiliser.web.common.panels.mbanking.ServicePackagesPanel;
import com.sybase365.mobiliser.web.cst.pages.systemconfig.BaseSystemConfigurationPage;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * @author Sushil.agrawala
 */
@AuthorizeInstantiation(Constants.PRIV_CST_MBANKING)
public class ManageNumberRestrictionsPage extends BaseSystemConfigurationPage {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(ServicePackagesPanel.class);

    private LocalizableLookupDropDownChoice<String> countryList;

    private String fileUpload;
    Form<ManageNumberRestrictionsPage> form;
    private RestrictedNumber restrictedNumber = null;
    private boolean pageViewedByAgent;

    private Integer highestPriority;
    private String mnrTotalItemString = null;
    private int mrnStartIndex = 0;
    private int mrnEndIndex = 0;

    FileUploadField fileUploadField;

    private static final String WICKET_ID_mrnNumber = "mrnNumber";
    private static final String WICKET_ID_mrnNavigator = "mrnNavigator";
    private static final String WICKET_ID_mrnTotalItems = "mrnTotalItems";
    private static final String WICKET_ID_mrnStartIndex = "mrnStartIndex";
    private static final String WICKET_ID_mrnEndIndex = "mrnEndIndex";
    private static final String WICKET_ID_mrnNoItemsMsg = "mrnNoItemsMsg";
    private static final String WICKET_ID_mrnPageable = "mrnPageable";
    private static final String WICKET_ID_mrnRemoveAction = "mrnRemoveAction";

    private static final String WICKET_ID_errorMessages = "errorMessages";
    private static final String WICKET_ID_addRestrictedNumberForm = "addRestrictedNumberForm";
    private static final String WICKET_ID_addRestrictedNumber = "addRestrictedNumber";

    private static final String WICKET_ID_fileUpload = "fileUpload";
    private static final String WICKET_ID_UploadRestrictedNumber = "UploadRestrictedNumber";
    private NumberRestrictionsDataProvider numberRestrictionsDataProvider;

    @SpringBean(name = "smartAuthMBankingClientLogic")
    protected MBankingClientLogic mBankingClientLogic;

    @SuppressWarnings("unchecked")
    public ManageNumberRestrictionsPage()

    {
	super();
	add(new FeedbackPanel(WICKET_ID_errorMessages));

	final WebMarkupContainer restrictedNumberViewContainer = new WebMarkupContainer(
		"restrictedNumberViewContainer");
	add(restrictedNumberViewContainer);
	createRestrictedNumberDataView(restrictedNumberViewContainer);

	final WebMarkupContainer addNumberRestrictionContainer = new WebMarkupContainer(
		"addNumberRestrictionContainer");
	addNumberRestrictionContainer.setVisible(false);
	addNumberRestrictionContainer.setOutputMarkupId(true);
	addNumberRestrictionContainer.setOutputMarkupPlaceholderTag(true);
	createAddNumberRestrictionContainer(addNumberRestrictionContainer);

	final WebMarkupContainer uploadNumberRestrictionContainer = new WebMarkupContainer(
		"uploadNumberRestrictionContainer");
	uploadNumberRestrictionContainer.setVisible(false);
	uploadNumberRestrictionContainer.setOutputMarkupId(true);
	uploadNumberRestrictionContainer.setOutputMarkupPlaceholderTag(true);
	createUploadNumberRestrictionContainer(uploadNumberRestrictionContainer);

	add(new AjaxLink("numberRestrictionAddLink") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onClick(final AjaxRequestTarget target) {
		addNumberRestrictionContainer.setVisible(true);
		uploadNumberRestrictionContainer.setVisible(false);
		target.addComponent(addNumberRestrictionContainer);
		target.addComponent(uploadNumberRestrictionContainer);
	    }
	});

	add(new AjaxLink("uploadRestrictionNumber") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onClick(final AjaxRequestTarget target) {
		addNumberRestrictionContainer.setVisible(false);
		uploadNumberRestrictionContainer.setVisible(true);
		target.addComponent(addNumberRestrictionContainer);
		target.addComponent(uploadNumberRestrictionContainer);
	    }
	});
    }

    private void createRestrictedNumberDataView(WebMarkupContainer parent) {

	numberRestrictionsDataProvider = new NumberRestrictionsDataProvider(
		WICKET_ID_mrnNumber, mBankingClientLogic);
	new ArrayList<ServicePackage>();
	final DataView<RestrictedNumber> dataView = new DataView<RestrictedNumber>(
		WICKET_ID_mrnPageable, numberRestrictionsDataProvider) {

	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onBeforeRender() {

		try {
		    numberRestrictionsDataProvider
			    .loadRestrictionsNumber(Constants.DEFAULT_ORGUNIT);
		    refreshTotalItemCount();
		    if (numberRestrictionsDataProvider.size() > 0) {
			setVisible(true);
		    } else {
			setVisible(false);
		    }
		} catch (DataProviderLoadException dple) {
		    LOG
			    .error(
				    "# An error occurred while loading Restricted number list",
				    dple);
		    error(getLocalizer().getString(
			    "restricted.number.load.error", this));
		}
		refreshTotalItemCount();
		super.onBeforeRender();
	    }

	    @Override
	    protected void populateItem(final Item<RestrictedNumber> item) {
		final RestrictedNumber entry = item.getModelObject();
		item.add(new Label("mrnNumber", entry.getMsisdn()));
		item.add(new Label("mrnCountry", entry.getCountry()));

		// Remove Action
		Link<RestrictedNumber> removeLink = new Link<RestrictedNumber>(
			WICKET_ID_mrnRemoveAction, item.getModel()) {

		    private static final long serialVersionUID = -5648391221008413258L;

		    @Override
		    public void onClick() {
			int status = -1;
			RestrictedNumberList restrictedNumbers = new RestrictedNumberList();
			restrictedNumbers.getRestrictedNumber().add(entry);
			status = mBankingClientLogic
				.removeRestrictedNumbers(restrictedNumbers);
			if (status == 0) {
			    getSession()
				    .info(
					    getLocalizer()
						    .getString(
							    "restrictedNumber.actionDelete.message",
							    this));
			    setResponsePage(ManageNumberRestrictionsPage.class);
			}
		    }
		};
		removeLink
			.add(new SimpleAttributeModifier(
				"onclick",
				"return confirm('"
					+ getLocalizer()
						.getString(
							"restrictedNumber.restrictedNumberTable.remove.confirm",
							this) + "');"));

		item.add(removeLink);
		// set items in even/odd rows to different css style classes
		item.add(new AttributeModifier(Constants.CSS_KEYWARD_CLASS,
			true, new AbstractReadOnlyModel<String>() {

			    private static final long serialVersionUID = 7540125567994387876L;

			    @Override
			    public String getObject() {
				return (item.getIndex() % 2 == 1) ? Constants.CSS_STYLE_ODD
					: Constants.CSS_STYLE_EVEN;
			    }
			}));
	    }

	    private void refreshTotalItemCount() {
		mnrTotalItemString = new Integer(numberRestrictionsDataProvider
			.size()).toString();
		int total = numberRestrictionsDataProvider.size();
		if (total > 0) {
		    mrnStartIndex = getCurrentPage() * getItemsPerPage() + 1;
		    mrnEndIndex = mrnStartIndex + getItemsPerPage() - 1;
		    if (mrnEndIndex > total)
			mrnEndIndex = total;
		} else {
		    mrnStartIndex = 0;
		    mrnEndIndex = 0;
		}
	    }
	};

	dataView.setItemsPerPage(5);
	parent.add(dataView);
	parent.add(new OrderByBorder("mrnOrderByNumber", WICKET_ID_mrnNumber,
		numberRestrictionsDataProvider) {
	    private static final long serialVersionUID = -1696556756649214998L;

	    @Override
	    protected void onSortChanged() {
		// For some reasons the dataView can be null when the page is
		// loading
		// and the sort is clicked (clicking the name header), so handle
		// it
		if (dataView != null) {
		    dataView.setCurrentPage(0);
		}
	    }
	});

	parent
		.add(new MultiLineLabel(
			WICKET_ID_mrnNoItemsMsg,
			getLocalizer()
				.getString(
					"restrictedNumber.restrictedNumberTable.noServicePackage",
					this)) {
		    private static final long serialVersionUID = -8884269132391923639L;

		    @Override
		    public boolean isVisible() {
			if (dataView.getItemCount() > 0) {
			    return false;
			} else {
			    return super.isVisible();
			}
		    }
		});

	// Navigator example: << < 1 2 > >>
	parent.add(new CustomPagingNavigator(WICKET_ID_mrnNavigator, dataView));
	parent.add(new Label(WICKET_ID_mrnTotalItems,
		new PropertyModel<String>(this, "mnrTotalItemString")));
	parent.add(new Label(WICKET_ID_mrnStartIndex, new PropertyModel(this,
		"mrnStartIndex")));
	parent.add(new Label(WICKET_ID_mrnEndIndex, new PropertyModel(this,
		"mrnEndIndex")));
	add(parent);
    }

    @SuppressWarnings("unchecked")
    private void createAddNumberRestrictionContainer(
	    WebMarkupContainer addNumberRestrictionContainer) {
	form = new Form(WICKET_ID_addRestrictedNumberForm,
		new CompoundPropertyModel<ManageNumberRestrictionsPage>(this));

	countryList = new LocalizableLookupDropDownChoice<String>(
		"restrictedNumber.country", String.class, "countries", this,
		false, true);

	countryList.setRequired(true);
	form.add(countryList);

	form.add(new TextField<String>("restrictedNumber.msisdn").setRequired(
		true).add(new PatternValidator(Constants.REGEX_PHONE_NUMBER))
		.add(Constants.mediumStringValidator).add(
			Constants.mediumSimpleAttributeModifier).add(
			new ErrorIndicator()));
	form.add(new Button(WICKET_ID_addRestrictedNumber) {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		RestrictedNumberList restrictedNumbers = new RestrictedNumberList();
		restrictedNumbers.getRestrictedNumber().add(restrictedNumber);
		int status = mBankingClientLogic
			.addRestrictedNumbers(restrictedNumbers);
		if (status == 0) {
		    getSession()
			    .info(
				    getLocalizer()
					    .getString(
						    "manageRestrictedNumbers.restricted.number.uploaded.sucessfully",
						    this));
		    setResponsePage(ManageNumberRestrictionsPage.class);
		}
	    };
	});

	addNumberRestrictionContainer.add(form);
	add(addNumberRestrictionContainer);

    }

    private void createUploadNumberRestrictionContainer(
	    WebMarkupContainer uploadNumberRestrictionContainer) {
	form = new Form("uploadRestrictedNumberForm",
		new CompoundPropertyModel<ManageNumberRestrictionsPage>(this));

	form.setMultiPart(true);

	fileUploadField = new FileUploadField(WICKET_ID_fileUpload);
	form.add(fileUploadField);

	form.add(new Button(WICKET_ID_UploadRestrictedNumber) {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		uploadRestrictionNumber();
	    };
	}.setDefaultFormProcessing(false));
	uploadNumberRestrictionContainer.add(form);
	add(uploadNumberRestrictionContainer);

    }

    private void uploadRestrictionNumber() {
	if (PortalUtils.exists(fileUploadField)
		&& PortalUtils.exists(fileUploadField.getFileUpload())) {
	    String inputFromFile = new String(fileUploadField.getFileUpload()
		    .getBytes());
	    StringTokenizer stringTokenizer = new StringTokenizer(
		    inputFromFile, "\n");
	    RestrictedNumberList restrictedNumbers = new RestrictedNumberList();

	    while (stringTokenizer.hasMoreTokens()) {
		RestrictedNumber restrictedNumber = new RestrictedNumber();
		String msisdnAndCountry = stringTokenizer.nextToken();
		if (!"".equalsIgnoreCase(msisdnAndCountry.trim())) {

		    String[] msisdnCountry = msisdnAndCountry.split(",");

		    if (msisdnCountry.length != 2) {
			error(getLocalizer()
				.getString(
					"manageRestrictedNumbers.wrong.formate.file.error",
					this));
			return;
		    } else {
			if (msisdnCountry[0].trim().equalsIgnoreCase("")
				|| msisdnCountry[1].trim().equalsIgnoreCase("")) {
			    error(getLocalizer()
				    .getString(
					    "manageRestrictedNumbers.wrong.formate.file.error",
					    this));
			    return;
			}
			restrictedNumber.setMsisdn(msisdnCountry[0].trim());
			restrictedNumber.setCountry(msisdnCountry[1].trim());
		    }
		    restrictedNumbers.getRestrictedNumber().add(
			    restrictedNumber);
		}
	    }

	    int status = mBankingClientLogic
		    .addRestrictedNumbers(restrictedNumbers);
	    if (status == 0) {
		getSession()
			.info(
				getLocalizer()
					.getString(
						"manageRestrictedNumbers.restricted.number.uploaded.sucessfully",
						this));
		setResponsePage(ManageNumberRestrictionsPage.class);
	    }
	} else {
	    error(getLocalizer().getString(
		    "manageRestrictedNumbers.upload.file.error", this));
	    return;
	}

    }

    @Override
    protected Class<ManageNumberRestrictionsPage> getActiveMenu() {
	return ManageNumberRestrictionsPage.class;
    }

    public void setRestrictedNumber(RestrictedNumber restrictedNumber) {
	this.restrictedNumber = restrictedNumber;
    }

    public RestrictedNumber getRestrictedNumber() {
	return restrictedNumber;
    }

    public void setFileUpload(String fileUpload) {
	this.fileUpload = fileUpload;
    }

    public String getFileUpload() {
	return fileUpload;
    }

    public boolean isPageViewedByAgent() {
	return pageViewedByAgent;
    }

    public void setPageViewedByAgent(boolean pageViewedByAgent) {
	this.pageViewedByAgent = pageViewedByAgent;
    }

}
