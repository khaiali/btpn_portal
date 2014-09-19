package com.sybase365.mobiliser.web.dashboard.panels.charts;

import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.dashboard.pages.trackers.beans.TrackerBean;

public class LinePanel extends ChartPanel {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(LinePanel.class);

    public LinePanel(String id, TrackerBean trackerBean,
	    MobiliserBasePage mobBasePage, FeedbackPanel feedbackPanel) {

	super(id, trackerBean, mobBasePage, feedbackPanel);

	final String trackerId = trackerBean.getId();

	LOG.debug("Created new LinePanel");

	add(new HeaderContributor(new IHeaderContributor() {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public void renderHead(IHeaderResponse response) {

		// min not currently used
		long yMin = Math.round(getMinValue()*0.9);

		// 10% headroom for y max - the 'and add 10' is for small 
		// max values (say < 10) where 10% may not give any difference
		long yMax = Math.round(getMaxValue()*1.1) + 10L;

		if (getMinValue() == getMaxValue()) {
		    yMax = yMax + 10;
		}

		response.renderJavascript("\n" +
			"jQuery(document).ready(function($){\n" +
			"\n" +
			"    $.jqplot ('trackerHolder-"+trackerId+"', \n" +
			"	"+renderDataSets()+", {\n"+
			"	axesDefaults: {\n" +
			"	    labelRenderer: $.jqplot.CanvasAxisLabelRenderer,\n"+
			"	    tickRenderer: $.jqplot.CanvasAxisTickRenderer,\n"+
			"	    labelOptions: { fontSize: '8pt' },\n" +
			"	    tickOptions: { formatString: '%d', fontSize: '8pt' }\n" +
			"	},\n" +
			"\n" +
			"	seriesDefaults: {\n" +
			"	    rendererOptions: {\n" +
			"		smooth: true\n" +
			"	    }\n" +
			"	},\n" +
			"\n" +
			"	axes: {\n" +
			"	    xaxis: {\n" +
			"		pad: 0\n" +
			"	    }\n," +
			"	    yaxis: {\n" +
			"		padMax: 1.1,\n" +
			"		min: 0\n" +
			"	    }\n" +
			"	}\n" +
			"    });\n" +
			"});\n", 
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
