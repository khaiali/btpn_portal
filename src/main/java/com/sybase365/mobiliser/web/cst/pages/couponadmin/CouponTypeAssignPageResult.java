package com.sybase365.mobiliser.web.cst.pages.couponadmin;

import java.util.List;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CouponTypeAssignPageResult extends CouponTypeMenuGroup {

    private static final Logger LOG = LoggerFactory
	    .getLogger(CouponTypeAssignPageResult.class);

    private String sussessfullyAssigned;
    private String failed;

    public CouponTypeAssignPageResult(List<String> successList,
	    List<String> failedList) {
	super();
	sussessfullyAssigned = "";
	failed = "";
	for (int i = 0; i < successList.size(); i++) {
	    sussessfullyAssigned += successList.get(i);
	    if (i < successList.size() - 1)
		sussessfullyAssigned += ", ";
	}
	for (int i = 0; i < failedList.size(); i++) {
	    failed += failedList.get(i);
	    if (i < failedList.size() - 1)
		failed += ", ";
	}
	initPageComponents();
    }

    private void initPageComponents() {
	final Form<?> form = new Form("assignPageResultForm",
		new CompoundPropertyModel<CouponTypeAssignPageResult>(this));
	form.add(new TextArea<String>("sussessfullyAssigned")
		.setRequired(false));
	form.add(new TextArea<String>("failed").setRequired(false));

	add(form);

    }

    @Override
    protected Class getActiveMenu() {
	return CouponTypeAssignPage.class;
    }

    public String getSussessfullyAssigned() {
	return sussessfullyAssigned;
    }

    public void setSussessfullyAssigned(String sussessfullyAssigned) {
	this.sussessfullyAssigned = sussessfullyAssigned;
    }

    public String getFailed() {
	return failed;
    }

    public void setFailed(String failed) {
	this.failed = failed;
    }

}
