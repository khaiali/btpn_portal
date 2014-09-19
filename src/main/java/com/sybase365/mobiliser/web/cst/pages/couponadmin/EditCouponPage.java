package com.sybase365.mobiliser.web.cst.pages.couponadmin;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.coupon.UpdateCouponInstanceRequest;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.UpdateCouponInstanceResponse;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.beans.Coupon;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.beans.CouponInstance;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.util.tools.wicketutils.security.PrivilegedBehavior;
import com.sybase365.mobiliser.web.common.components.LocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class EditCouponPage extends BaseCouponAdminPage {
    private static final Logger LOG = LoggerFactory
	    .getLogger(EditCouponPage.class);
    private Coupon coupon;
    private CouponInstance instance;
    private String couponTypeString;
    private boolean status;

    public EditCouponPage(Coupon coupon, CouponInstance instance,
	    String couponTypeString) {
	super();
	this.coupon = coupon;
	this.instance = instance;
	this.couponTypeString = couponTypeString;
	initPageComponents();

    }

    private void initPageComponents() {
	final Form<?> form = new Form("editCouponForm",
		new CompoundPropertyModel<EditCouponPage>(this));
	form.add(new FeedbackPanel("errorMessages"));
	form.add(new Label("coupon.id"));
	form.add(new Label("couponTypeString"));
	form.add(new Label("coupon.serialNumber"));
	// tODO set proper value for issue date and status
	form.add(new Label("coupon.validTo", PortalUtils.getFormattedDate(
		getCoupon().getValidTo(), getMobiliserWebSession().getLocale())));
	form.add(new Label("instance.customer"));
	form.add(new LocalizableLookupDropDownChoice<String>("instance.status",
		String.class, Constants.RESOURCE_BUNDLE_COUPON_STATUS, this)
		.add(new ErrorIndicator()));

	form.add(new Button("editCoupon") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleSubmit();
	    }
	}.add(new PrivilegedBehavior(this, Constants.PRIV_EDIT_COUPON)));
	form.add(new Button("cancelCoupon") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		setResponsePage(SearchCouponPage.class);
	    }
	});
	add(form);

    }

    protected void handleSubmit() {
	try {
	    UpdateCouponInstanceRequest request = getNewMobiliserRequest(UpdateCouponInstanceRequest.class);
	    request.setInstance(instance);
	    UpdateCouponInstanceResponse response = wsCouponsClient
		    .updateCouponInstance(request);
	    if (evaluateMobiliserResponse(response)) {
		info(getLocalizer().getString("update.coupon.success", this));
	    }
	} catch (Exception e) {
	    LOG.error("An error occurred in updating coupon", e);
	    error(getLocalizer().getString("update.coupon.error", this));
	}
    }

    public void setCoupon(Coupon coupon) {
	this.coupon = coupon;
    }

    public Coupon getCoupon() {
	return coupon;
    }

    public void setCouponTypeString(String couponTypeString) {
	this.couponTypeString = couponTypeString;
    }

    public String getCouponTypeString() {
	return couponTypeString;
    }

    public void setStatus(boolean status) {
	this.status = status;
    }

    public boolean isStatus() {
	return status;
    }
}
