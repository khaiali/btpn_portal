package com.sybase365.mobiliser.web.btpn.common.behaviours;

import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;

import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This date header contributor will add the jquery script that is required for date fields.
 * 
 * @author Vikram Gunda
 */
public class DateHeaderContributor implements IHeaderContributor {

	private static final long serialVersionUID = 1L;

	private String locale;

	private String datePattern;

	private String chooseDtTxt;

	public DateHeaderContributor(final String locale, final String datePattern, final String chooseDtTxt) {
		this.locale = locale;
		this.datePattern = datePattern;
		this.chooseDtTxt = chooseDtTxt;
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		
		if (PortalUtils.exists(locale)) {
			response.renderJavascriptReference("../scripts/jquery/i18n/jquery.ui.datepicker-" + this.locale + ".js");
		}

		response.renderJavascript("\n" + "jQuery(document).ready(function($) { \n"
				+ "  $('#birthDate').datepicker( { \n" + "	'buttonText' : '" + this.chooseDtTxt + "', \n"
				+ "	'changeMonth' : true, \n" + "	'changeYear' : true, \n" + "     'yearRange' : '-100:+100', \n"
				+ "	'showOn': 'both', \n" + "	'dateFormat' : '" + this.datePattern + "', \n"
				+ "	'buttonImage': '../images/calendar.gif', \n" + "	'buttonOnlyImage': true} ); \n" + "});\n",
			"datePicker");

		response.renderJavascript("\n" + "jQuery(document).ready(function($) { \n"
				+ "  $('#expirationDate').datepicker( { \n" + "	'buttonText' : '" + chooseDtTxt + "', \n"
				+ "	'changeMonth' : true, \n" + "	'changeYear' : true, \n" + "     'yearRange' : '-100:+100', \n"
				+ "	'showOn': 'both', \n" + "	'dateFormat' : '" + this.datePattern + "', \n"
				+ "	'buttonImage': '../images/calendar.gif', \n" + "	'buttonOnlyImage': true} ); \n" + "});\n",
			"datePicker");
		
		response.renderJavascript("\n" + "jQuery(document).ready(function($) { \n"
				+ "  $('#fromDate').datepicker( { \n" + "	'buttonText' : '" + chooseDtTxt + "', \n"
				+ "	'changeMonth' : true, \n" + "	'changeYear' : true, \n" + "     'yearRange' : '-100:+100', \n"
				+ "	'showOn': 'both', \n" + "	'dateFormat' : '" + this.datePattern + "', \n"
				+ "	'buttonImage': '../images/calendar.gif', \n" + "	'buttonOnlyImage': true} ); \n" + "});\n",
			"datePicker");
		
		response.renderJavascript("\n" + "jQuery(document).ready(function($) { \n"
				+ "  $('#toDate').datepicker( { \n" + "	'buttonText' : '" + chooseDtTxt + "', \n"
				+ "	'changeMonth' : true, \n" + "	'changeYear' : true, \n" + "     'yearRange' : '-100:+100', \n"
				+ "	'showOn': 'both', \n" + "	'dateFormat' : '" + this.datePattern + "', \n"
				+ "	'buttonImage': '../images/calendar.gif', \n" + "	'buttonOnlyImage': true} ); \n" + "});\n",
			"datePicker");

	}

}
