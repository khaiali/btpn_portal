package com.sybase365.mobiliser.web.common.reports.panels.helpers;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;

import com.sybase365.mobiliser.util.contract.v5_0.report.beans.ReportRequestParameter;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.common.reports.components.DynamicComponent;
import com.sybase365.mobiliser.web.common.reports.panels.ParameterEntryPanel;
import com.sybase365.mobiliser.web.util.Constants;

/**
 * 
 * <p>
 * &copy; 2012, Sybase Inc.
 * </p>
 * 
 * @author Mark White <msw@sybase.com>
 */
public class FindMerchantAgentHelperPanel 
	extends AbstractFindCustomerHelperPanel {

    public FindMerchantAgentHelperPanel(final String id, 
	    final MobiliserBasePage basePage, 
	    final ParameterEntryPanel parameterEntryPanel,
	    final ReportRequestParameter parameter,
	    final DynamicComponent dynamicComp) {

	super(id, basePage, parameterEntryPanel, parameter, dynamicComp);
    }

    @Override
    protected void addFormComponents(Form form) {

	form.add(new TextField<String>("firstName")
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	form.add(new TextField<String>("lastName")
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));
    }

    @Override
    protected boolean isHierarchical() {
	return Boolean.FALSE;
    }

    @Override
    protected String getCustomerSearchType() {
	return Constants.SEARCH_TYPE_MERCHANT_AGENT;
    }

}