package com.sybase365.mobiliser.web.common.reports.components;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.model.Model;

import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * 
 * <p>
 * &copy; 2011, Sybase Inc.
 * </p>
 * 
 * @author Allen Lau <alau@sybase.com>
 */
public class DynamicDateTextField extends BaseDynamicField 
	implements DynamicComponent {

    private static final long serialVersionUID = 1L;

    public DynamicDateTextField(String id, Component comp, boolean showsTime) {

	super(id, comp);

	String key = "" + comp.hashCode();
	comp.add(new AttributeModifier("id", new Model<String>(key)));

	add(new HeaderContributor(new IHeaderContributor() {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public void renderHead(IHeaderResponse response) {

		// localize the jquery datepicker based on users locale setting
		// locale specific js includes for datepicker are available at
		// http://jquery-ui.googlecode.com/svn/trunk/ui/i18n/
		String localeLang = getLocale().getLanguage().toLowerCase();

		if (PortalUtils.exists(localeLang)) {
		    response.renderJavascriptReference("scripts/jquery/i18n/jquery.ui.datepicker-"
			    + localeLang + ".js");
		}

		response.renderJavascript("\n"
			+ "jQuery(document).ready(function($) { \n"
			+ "  $('.dateCal').datepicker( { \n"
			+ "	'changeMonth' : true, \n"
			+ "	'changeYear' : true, \n" + "	'showOn': 'both', \n"
			+ "	'dateFormat' : '"
			+ Constants.DATE_FORMAT_PATTERN_PICKER + "', \n"
			+ "	'buttonImage': 'images/calendar.gif', \n"
			+ "	'buttonOnlyImage': true} ); \n" + "});\n",
			"datePicker");
	    }
	}));

	add(comp);
    }

}
