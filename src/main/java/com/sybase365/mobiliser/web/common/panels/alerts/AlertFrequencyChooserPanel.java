package com.sybase365.mobiliser.web.common.panels.alerts;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;

import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.beans.AlertFrequencyChooserBean;
import com.sybase365.mobiliser.web.common.components.LocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.util.AmountValidator;
import com.sybase365.mobiliser.web.util.Constants;

/**
 * @author sagraw03 AlertFrequencyChooserPanel need to be added where we need to
 *         show frequency radio button.
 */
public class AlertFrequencyChooserPanel extends Panel {
    /**
     * 
     */
    private static final long serialVersionUID = 9084969205565399137L;
    WebMarkupContainer frequencyDurationContainer = null;

    @SuppressWarnings("unchecked")
    public AlertFrequencyChooserPanel(String id,
	    final AlertFrequencyChooserBean alertFrequencyChooserBean,
	    MobiliserBasePage basePage) {

	super(id);

	AjaxCheckBox frequencyNoLimitCheck = new AjaxCheckBox(
		"alertFrequencyChooserBean.frequencyNoLimitCheck") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onUpdate(AjaxRequestTarget target) {
		if (alertFrequencyChooserBean.isFrequencyNoLimitCheck()) {
		    frequencyDurationContainer.setVisible(true);
		} else {
		    frequencyDurationContainer.setVisible(false);
		}
		target.addComponent(frequencyDurationContainer);
	    }
	};
	add(frequencyNoLimitCheck);

	frequencyDurationContainer = new WebMarkupContainer(
		"frequencyDurationContainer");
	frequencyDurationContainer.setOutputMarkupId(true);
	frequencyDurationContainer.setOutputMarkupPlaceholderTag(true);
	frequencyDurationContainer.setVisible(alertFrequencyChooserBean
		.isFrequencyNoLimitCheck());

	frequencyDurationContainer
		.add(new LocalizableLookupDropDownChoice<String>(
			"alertFrequencyChooserBean.durationFrequency",
			String.class, "frequencyDurations", this, true, true)
			.setRequired(true).add(new ErrorIndicator()));

	frequencyDurationContainer.add(new TextField<String>(
		"alertFrequencyChooserBean.maxFrequency").add(
		new AmountValidator(basePage, Constants.REGEX_AMOUNT_12_0))
		.setRequired(true).add(new ErrorIndicator()));

	add(frequencyDurationContainer);
    }
}
