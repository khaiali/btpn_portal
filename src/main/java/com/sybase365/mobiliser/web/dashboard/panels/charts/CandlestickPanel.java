package com.sybase365.mobiliser.web.dashboard.panels.charts;

import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.dashboard.pages.trackers.beans.TrackerBean;

public class CandlestickPanel extends ChartPanel {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(CandlestickPanel.class);

    public CandlestickPanel(String id, TrackerBean trackerBean,
	    MobiliserBasePage mobBasePage, FeedbackPanel feedbackPanel) {

	super(id, trackerBean, mobBasePage, feedbackPanel);

	final String trackerId = trackerBean.getId();

	LOG.debug("Created new BarPanel");

	add(new HeaderContributor(new IHeaderContributor() {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public void renderHead(IHeaderResponse response) {

		response.renderJavascript("\n" +
			"jQuery(document).ready(function($){\n" +
			"\n" +
			"	var ohlc = "+renderDataSets()+";\n"+
			"\n" +
			"	$.jqplot('tracker2Holder',[ohlc],{\n"+
			"	    seriesDefaults:{yaxis:'yaxis'},\n"+
			"	    axes: {\n"+
			"		xaxis: {\n"+
			"		    renderer:$.jqplot.DateAxisRenderer,\n"+
			"		    tickOptions:{formatString:'%H'},\n"+
			"		    min: '12-31-2011 22:00',\n"+
			"		    max: '01-01-2012 02:00',\n"+
			"		    tickInterval: '30 minutes'\n"+
			"		},\n"+
			"		yaxis: {\n"+
			"		    tickOptions:{formatString:'%dK'}\n"+
			"		}\n"+
			"	    },\n"+
			"	    series: [\n"+
			"		{\n"+
			"		    renderer:$.jqplot.OHLCRenderer,\n"+
			"		    rendererOptions:{ candleStick:true }\n"+
			"		}\n"+
			"	    ],\n"+
			"	    highlighter: {\n"+
			"		show: true,\n"+
			"		showMarker:false,\n"+
			"		tooltipAxes: 'xy',\n"+
			"		yvalues: 4,\n"+
			"		formatString:'<table class=\"jqplot-highlighter\"> \\ \n"+
			"      <tr><td>Hour:</td><td>%s</td></tr> \\ \n"+
			"      <tr><td>open:</td><td>%s</td></tr> \\ \n"+
			"      <tr><td>hi:</td><td>%s</td></tr> \\ \n"+
			"      <tr><td>low:</td><td>%s</td></tr> \\ \n"+
			"      <tr><td>close:</td><td>%s</td></tr></table>'\n"+
			"	    } \n"+
			"	});\n",
			"trackerScript-"+trackerId
		    );
		}
	    }));

	constructPanel();
    }

    @SuppressWarnings("unchecked")
    private void constructPanel() {

    }

}
