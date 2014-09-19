package com.sybase365.mobiliser.web.common.reports.panels.helpers;

import java.util.List;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.validation.validator.PatternValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.util.contract.v5_0.report.beans.ReportRequestParameter;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.common.dataproviders.CustomerBeanDataProvider;
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
public class FindCustomerHelperPanel 
	extends AbstractFindCustomerHelperPanel {

    private static final Logger LOG = LoggerFactory
	    .getLogger(FindCustomerHelperPanel.class);

    private MobiliserBasePage basePage;
    private ParameterEntryPanel parameterEntryPanel;
    private ReportRequestParameter parameter;
    private DynamicComponent dynamicComp;

    private String firstName;
    private String lastName;
    private String msisdn;

    private List<CustomerBean> customerList;
    private CustomerBeanDataProvider dataProvider;

    public FindCustomerHelperPanel(final String id, 
	    final MobiliserBasePage basePage, 
	    final ParameterEntryPanel parameterEntryPanel,
	    final ReportRequestParameter parameter,
	    final DynamicComponent dynamicComp) {

	super(id, basePage, parameterEntryPanel, parameter, dynamicComp);
    }

    @Override
    protected void addFormComponents(Form form) {

	form.add(new TextField<String>("msisdn")
		.add(new PatternValidator(Constants.REGEX_MSISDN))
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));

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
	return Constants.SEARCH_TYPE_CUSTOMER;
    }

}