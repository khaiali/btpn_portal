package com.sybase365.mobiliser.web.dashboard.panels.charts;

import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.dashboard.pages.trackers.beans.TrackerBean;

public class GaugePanel extends ChartPanel {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(GaugePanel.class);

    public GaugePanel(String id, TrackerBean trackerBean,
	    MobiliserBasePage mobBasePage, FeedbackPanel feedbackPanel) {

	super(id, trackerBean, mobBasePage, feedbackPanel);

	final String trackerId = trackerBean.getId();

	LOG.debug("Created new GaugePanel");

	add(new HeaderContributor(new IHeaderContributor() {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public void renderHead(IHeaderResponse response) {

		response.renderJavascript("\n" +
			"jQuery(document).ready(function($){\n" +
			"\n" +
			"    $.jqplot ('trackerHolder-"+trackerId+"', \n" +
			"	"+renderDataSets()+", {\n"+
			"		seriesDefaults: {\n"+
		    	"			renderer: $.jqplot.MeterGaugeRenderer,\n"+
			"				rendererOptions: {\n"+
			"				    padding: 0,\n"+
			"				    min: 00,\n"+
			"				    max: 1000,\n"+
			"				    intervals:[250, 500, 750, 1000],\n"+
			"				    intervalColors:['#66cc66', '#93b75f', '#E7E658', '#cc6666']\n"+
			"				}\n"+
			"		}\n"+
	    		"	});\n"+
			"});",
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
