package com.sybase365.mobiliser.web.common.reports.panels.helpers;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.util.contract.v5_0.report.beans.ReportRequestParameter;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.common.components.LocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.common.reports.components.DynamicComponent;
import com.sybase365.mobiliser.web.common.reports.panels.ParameterEntryPanel;

/**
 * 
 * <p>
 * &copy; 2012, Sybase Inc.
 * </p>
 * 
 * @author Mark White <msw@sybase.com>
 */
public class UseCaseHelperPanel extends Panel {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(UseCaseHelperPanel.class);

    private MobiliserBasePage basePage;
    private ParameterEntryPanel parameterEntryPanel;
    private ReportRequestParameter parameter;
    private DynamicComponent dynamicComp;

    private String useCase;

    public UseCaseHelperPanel(final String id,
	    final MobiliserBasePage basePageValue,
	    final ParameterEntryPanel parameterEntryPanelValue,
	    final ReportRequestParameter parameterValue,
	    final DynamicComponent dynamicCompValue) {

	super(id);

	this.basePage = basePageValue;
	this.parameterEntryPanel = parameterEntryPanelValue;
	this.parameter = parameterValue;
	this.dynamicComp = dynamicCompValue;

	final Form<?> form = new Form<UseCaseHelperPanel>("lookupUseCaseForm",
		new CompoundPropertyModel<UseCaseHelperPanel>(this));

	form.add(new LocalizableLookupDropDownChoice<String>("useCase",
		String.class, "usecases", this, Boolean.FALSE, true)
		.setRequired(true).add(new ErrorIndicator())
		.add(new AjaxFormComponentUpdatingBehavior("onchange") {
		    private static final long serialVersionUID = 1L;

		    @Override
		    protected void onUpdate(AjaxRequestTarget target) {
			parameter.setValue(getUseCase());
			dynamicComp.getComponent().setDefaultModel(
				new Model<String>(getUseCase()));
			target.addComponent(dynamicComp.getComponent());
		    }
		}));

	add(form);
    }

    public String getUseCase() {
	return useCase;
    }

    public void setUseCase(String useCase) {
	this.useCase = useCase;
    }
}