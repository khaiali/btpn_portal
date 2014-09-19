package com.sybase365.mobiliser.web.common.panels;

import java.math.BigDecimal;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.validation.validator.RangeValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.coupon.CreateCouponTypeLocationRequest;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.CreateCouponTypeLocationResponse;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.UpdateCouponTypeLocationRequest;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.UpdateCouponTypeLocationResponse;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.beans.CouponTypeLocation;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.cst.pages.couponadmin.CouponTypeLocationsPage;
import com.sybase365.mobiliser.web.cst.pages.couponadmin.EditCouponTypeLocationsPage;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class CouponTypeLocationsPanel extends Panel {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory
	    .getLogger(CouponTypeLocationsPanel.class);
    private CouponTypeLocation couponTypeLocation;
    private MobiliserBasePage basePage;

    public CouponTypeLocationsPanel(String id, MobiliserBasePage basePage,
	    CouponTypeLocation couponTypeLocation) {
	super(id);
	this.basePage = basePage;
	this.couponTypeLocation = couponTypeLocation;
	constructPanel();
    }

    private void constructPanel() {
	final Form<?> form = new Form("couponTypeLocationForm",
		new CompoundPropertyModel<CouponTypeLocationsPanel>(this));

	form.add(new FeedbackPanel("errorMessages"));

	form.add(new TextField<BigDecimal>("couponTypeLocation.longitude")
		.add(new RangeValidator<BigDecimal>(BigDecimal.valueOf(-180L),
			BigDecimal.valueOf(180L))).setRequired(true)
		.add(new ErrorIndicator()));
	form.add(new TextField<BigDecimal>("couponTypeLocation.latitude")
		.add(new RangeValidator<BigDecimal>(BigDecimal.valueOf(-180L),
			BigDecimal.valueOf(180L))).setRequired(true)
		.add(new ErrorIndicator()));

	form.add(new TextField<Long>("couponTypeLocation.radius").setRequired(
		false).add(new ErrorIndicator()));
	form.add(new Button("Save") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		if (PortalUtils.exists(couponTypeLocation)
			&& PortalUtils.exists(couponTypeLocation.getId())) {
		    try {
			UpdateCouponTypeLocationRequest request = basePage
				.getNewMobiliserRequest(UpdateCouponTypeLocationRequest.class);
			request.setLocation(couponTypeLocation);
			UpdateCouponTypeLocationResponse response = basePage.wsCouponsClient
				.updateCouponTypeLocation(request);
			if (basePage.evaluateMobiliserResponse(response)) {
			    info(getLocalizer().getString(
				    "update.coupon.location.success", this));
			}
		    } catch (Exception e) {
			LOG.error("An error occurred in updating location", e);
			error(getLocalizer().getString(
				"update.coupon.location.error", this));
		    }

		} else {
		    try {
			CreateCouponTypeLocationRequest request = basePage
				.getNewMobiliserRequest(CreateCouponTypeLocationRequest.class);
			couponTypeLocation.setCouponType(basePage
				.getMobiliserWebSession().getCouponType()
				.getId());
			request.setLocation(couponTypeLocation);
			CreateCouponTypeLocationResponse response = basePage.wsCouponsClient
				.createCouponTypeLocation(request);
			if (basePage.evaluateMobiliserResponse(response)) {
			    info(getLocalizer().getString(
				    "create.coupon.location.success", this));

			    couponTypeLocation.setId(response.getLocationId());
			    setResponsePage(new EditCouponTypeLocationsPage(
				    couponTypeLocation));
			}
		    } catch (Exception e) {
			LOG.error("An error occurred in creating location", e);
			error(getLocalizer().getString(
				"create.coupon.location.error", this));
		    }
		}

	    }
	});
	form.add(new Button("Cancel") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		setResponsePage(CouponTypeLocationsPage.class);
	    }
	}.setDefaultFormProcessing(false));

	add(form);
    }

    public void setCouponType(CouponTypeLocation couponTypeLocation) {
	this.couponTypeLocation = couponTypeLocation;
    }

    public CouponTypeLocation getCouponType() {
	return couponTypeLocation;
    }

}