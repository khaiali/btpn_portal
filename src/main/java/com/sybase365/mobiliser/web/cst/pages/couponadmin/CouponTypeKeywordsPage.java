package com.sybase365.mobiliser.web.cst.pages.couponadmin;

import java.util.List;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.coupon.GetAllCouponTagsByCouponTypeRequest;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.GetAllCouponTagsByCouponTypeResponse;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.ReplaceAllCouponTagsRequest;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.ReplaceAllCouponTagsResponse;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.beans.CouponTypeTag;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class CouponTypeKeywordsPage extends CouponTypeMenuGroup {

    private static final Logger LOG = LoggerFactory
	    .getLogger(CouponTypeKeywordsPage.class);

    private String keywordString;

    public CouponTypeKeywordsPage() {
	super();
	if (getAllCouponTypeKeywords())
	    initPageComponents();
    }

    private boolean getAllCouponTypeKeywords() {
	try {
	    GetAllCouponTagsByCouponTypeRequest req = getNewMobiliserRequest(GetAllCouponTagsByCouponTypeRequest.class);
	    req.setCouponType(getMobiliserWebSession().getCouponType().getId());
	    GetAllCouponTagsByCouponTypeResponse response = wsCouponsClient
		    .getAllCouponTagsByCouponType(req);
	    if (evaluateMobiliserResponse(response)) {
		List<CouponTypeTag> tags = response.getTag();
		if (PortalUtils.exists(tags)) {
		    keywordString = "";
		    for (int i = 0; i < tags.size(); i++) {
			CouponTypeTag tag = tags.get(i);
			keywordString += tag.getTag();
			if (i < tags.size() - 1)
			    keywordString += ", ";
		    }
		}
	    } else {
		return false;
	    }
	} catch (Exception e) {
	    LOG.error("An error occurred in getAllCouponTypeKeywords", e);
	    error(getLocalizer().getString("keyword.load.error", this));
	    return false;
	}
	return true;
    }

    private void initPageComponents() {
	final Form<?> form = new Form("keywordForm",
		new CompoundPropertyModel<CouponTypeKeywordsPage>(this));
	form.add(new FeedbackPanel("errorMessages"));
	form.add(new TextArea<String>("keywordString").setRequired(true));
	form.add(new Button("Save") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleSubmit();
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

    protected void handleSubmit() {
	boolean success = true;
	String[] keywords = keywordString.split(",");
	try {
	    ReplaceAllCouponTagsRequest req = getNewMobiliserRequest(ReplaceAllCouponTagsRequest.class);
	    req.setCouponType(getMobiliserWebSession().getCouponType().getId());
	    for (String keyword : keywords) {
		if (PortalUtils.exists(keyword) && keyword.length() > 0)
		    req.getTag().add(keyword);
	    }
	    ReplaceAllCouponTagsResponse res = wsCouponsClient
		    .replaceAllCouponTags(req);
	    if (evaluateMobiliserResponse(res)) {
		info(getLocalizer().getString("create.keyword.success", this));
		setResponsePage(new CouponTypeKeywordsPage());
	    }
	} catch (Exception e) {
	    LOG.error("error in creating coupon type tag", e);
	    success = false;
	    error(getLocalizer().getString("create.keyword.error", this));
	}
    }

    public void setKeywordString(String keywordString) {
	this.keywordString = keywordString;
    }

    public String getKeywordString() {
	return keywordString;
    }
}
