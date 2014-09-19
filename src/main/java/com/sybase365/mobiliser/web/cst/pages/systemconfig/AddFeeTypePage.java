package com.sybase365.mobiliser.web.cst.pages.systemconfig;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Customer;
import com.sybase365.mobiliser.money.contract.v5_0.system.CreateFeeTypeRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.CreateFeeTypeResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.FeeType;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.common.components.KeyValueDropDownChoice;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class AddFeeTypePage extends BaseSystemConfigurationPage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(AddFeeTypePage.class);

    private FeeType feeType;
    String beneficiary;

    public FeeType getFeeType() {
	return feeType;
    }

    public void setFeeType(FeeType feeType) {
	this.feeType = feeType;
    }

    private KeyValueDropDownChoice<Boolean, String> processByTxnOptions;
    private KeyValueDropDownChoice<Boolean, String> includeVatOptions;
    private KeyValueDropDownChoice<Boolean, String> commisionFeeOptions;

    public AddFeeTypePage() {
	super();
	initPageComponents();
    }

    @SuppressWarnings({ "unchecked", "serial" })
    protected void initPageComponents() {
	Form<?> addFeeTypeForm = new Form("addFeeTypeForm",
		new CompoundPropertyModel<AddFeeTypePage>(this));

	addFeeTypeForm.add(new RequiredTextField<Integer>("feeType.id").add(
		Constants.isShortAttributeModifier).add(new ErrorIndicator()));

	addFeeTypeForm.add(new RequiredTextField<String>("feeType.name")
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	addFeeTypeForm.add(new RequiredTextField<Integer>("beneficiary").add(
		Constants.idLongAttributeModifier).add(new ErrorIndicator()));

	processByTxnOptions = (KeyValueDropDownChoice<Boolean, String>) new KeyValueDropDownChoice<Boolean, String>(
		"feeType.processWithTxn", getYesNoOptions()).setNullValid(true)
		.add(new ErrorIndicator());
	processByTxnOptions.setRequired(true);

	includeVatOptions = (KeyValueDropDownChoice<Boolean, String>) new KeyValueDropDownChoice<Boolean, String>(
		"feeType.includeVat", getYesNoOptions()).setNullValid(true)
		.add(new ErrorIndicator());
	includeVatOptions.setRequired(true);

	commisionFeeOptions = (KeyValueDropDownChoice<Boolean, String>) new KeyValueDropDownChoice<Boolean, String>(
		"feeType.commission", getYesNoOptions()).setNullValid(true)
		.add(new ErrorIndicator());
	commisionFeeOptions.setRequired(true);

	addFeeTypeForm.add(processByTxnOptions);
	addFeeTypeForm.add(includeVatOptions);
	addFeeTypeForm.add(commisionFeeOptions);

	addFeeTypeForm.add(new Button("save") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleSubmit();
	    };
	});

	addFeeTypeForm.add(new Button("cancel") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		setResponsePage(FeeTypeConfigurationPage.class);
	    };
	}.setDefaultFormProcessing(false));

	addFeeTypeForm.add(new FeedbackPanel("errorMessages"));
	add(addFeeTypeForm);

    }

    @Override
    protected Class getActiveMenu() {
	return this.getClass();
    }

    private void handleSubmit() {
	boolean feeTypeCreated = false;

	Customer customer;
	customer = getCustomerByIdentification(Constants.IDENT_TYPE_CUST_ID,
		beneficiary);
	if (!PortalUtils.exists(customer)) {
	    LOG.warn("# An user with entered customer id does not exist");
	    error(getLocalizer().getString(
		    "addFeeType.customer.not.found.error", this));
	    return;
	}
	try {
	    if (!createFeeType(getFeeType(), customer.getId()))
		return;
	} catch (Exception e) {
	    error(getLocalizer().getString("create.feetype.error", this));
	    LOG.error("# Error occurred while creating a new fee type");
	    return;
	}

	LOG.info("# Successfully created a new fee type");
	setResponsePage(FeeTypeConfigurationPage.class);

    }

    private boolean createFeeType(FeeType feeType, Long customerId)
	    throws Exception {
	LOG.debug("# MobiliserBasePage.createFeeType()");

	CreateFeeTypeResponse response;
	CreateFeeTypeRequest request = getNewMobiliserRequest(CreateFeeTypeRequest.class);

	request.setBeneficiaryId(customerId);

	request.setFeeType(feeType);
	response = wsFeeConfClient.createFeeType(request);
	if (!evaluateMobiliserResponse(response)) {
	    LOG.warn("# Error occurred while creating a new fee type");
	    return false;
	}
	return true;
    }

}
