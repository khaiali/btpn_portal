package com.sybase365.mobiliser.web.cst.pages.couponadmin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.coupon.AssignCouponsRequest;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.AssignCouponsResponse;
import com.sybase365.mobiliser.web.common.components.LocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class CouponTypeAssignPage extends CouponTypeMenuGroup {

    private static final Logger LOG = LoggerFactory
	    .getLogger(CouponTypeAssignPage.class);

    private String customerIdsString;

    private Integer identificationType;

    public CouponTypeAssignPage() {
	super();
	initPageComponents();
    }

    private void initPageComponents() {
	final Form<?> form = new Form("assignPageForm",
		new CompoundPropertyModel<CouponTypeAssignPage>(this));
	form.add(new FeedbackPanel("errorMessages"));
	List<Integer> keyList = null;
	try {
	    keyList = getKeysFromPreferences("assignCpnSupportedIdent",
		    Integer.class);
	} catch (Exception e) {
	    LOG.error(
		    "An error occurred in loading the key lisy of suuported identification type for assign coupon",
		    e);
	}
	form.add(new LocalizableLookupDropDownChoice<Integer>(
		"identificationType", Integer.class,
		Constants.RESOURCE_BUNDLE_IDENTIFICATION_TYPE, this,
		Boolean.TRUE, true, keyList).setRequired(true));
	form.add(new TextArea<String>("customerIdsString").setRequired(true));
	form.add(new Button("Save") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleSubmit();
	    }

	    private void handleSubmit() {
		String[] customerIds = customerIdsString.split(",");
		List<String> allCustomerIds = new ArrayList<String>();
		allCustomerIds.addAll(Arrays.asList(customerIds));
		try {
		    AssignCouponsRequest request = getNewMobiliserRequest(AssignCouponsRequest.class);
		    if (customerIds.length > 1000) {
			error(getLocalizer().getString(
				"assign.customerIds.max", this));
			return;
		    }
		    for (String customerId : customerIds) {
			if (PortalUtils.exists(customerId))
			    request.getIdentifications().add(customerId.trim());
		    }
		    request.setCouponTypeId(getMobiliserWebSession()
			    .getCouponType().getId());
		    request.setIdentificationTypeId(identificationType);
		    AssignCouponsResponse response = wsCouponsClient
			    .assignCoupons(request);
		    if (evaluateMobiliserResponse(response)) {
			info(getLocalizer().getString(
				"assing.couponType.success", this));
			List<String> failed = response.getFailed();
			allCustomerIds.removeAll(failed);
			setResponsePage(new CouponTypeAssignPageResult(
				allCustomerIds, failed));
		    }
		} catch (Exception e) {
		    LOG.error("error in assigning coupon type", e);
		    error(getLocalizer().getString("assign.couponType.error",
			    this));
		}

	    }
	});
	form.add(new Button("Cancel") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		setResponsePage(new EditCouponTypePage(getMobiliserWebSession()
			.getCouponType()));
	    }
	}.setDefaultFormProcessing(false));

	add(form);

    }

    public void setCustomerIdsString(String customerIdsString) {
	this.customerIdsString = customerIdsString;
    }

    public String getCustomerIdsString() {
	return customerIdsString;
    }

    public Integer getIdentificationType() {
	return identificationType;
    }

    public void setIdentificationType(Integer identificationType) {
	this.identificationType = identificationType;
    }
}
