package com.sybase365.mobiliser.web.common.components;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.behavior.IBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.navigation.paging.IPagingLabelProvider;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigation;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;

/**
 * Reference from http://apache-wicket.1842946.n4.nabble.com/How-to-change-the-style-of-navigator-in-PageableViewList-td1870575.html
 * http://tomaszdziurko.pl/2010/12/custom-pagingnavigator-with-changing-items-per-page-in-wicket/
 * @author gabe
 *
 */
public class CustomPagingNavigator extends PagingNavigator {

    private static final long serialVersionUID = 1L;
	
    public CustomPagingNavigator (String id, IPageable pageable) { 
    	super(id, pageable);
    } 

    public CustomPagingNavigator (String id, IPageable pageable, IPagingLabelProvider labelProvider) { 
    	super(id, pageable, labelProvider); 
    }

    @Override
    protected PagingNavigation newNavigation(IPageable pageable,
		    IPagingLabelProvider labelProvider) {

	final IBehavior currentPageLinkClassifier = new AbstractBehavior() {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onComponentTag(Component c, ComponentTag tag) {
		if (((Link) c).isEnabled() == false) {
		    CharSequence current = tag.getString("class");
		    tag.put("class", "current " + (current == null ? "" : current));
		}
	    }
	};

	PagingNavigation newNavigation = new PagingNavigation("navigation", pageable) {

	    @Override
	    protected AbstractLink newPagingNavigationLink(String id,
					    IPageable pageable, int pageIndex) {
		    AbstractLink link = super.newPagingNavigationLink(id, pageable, pageIndex);
		    link.add(currentPageLinkClassifier);
		    return link;
	    }
	};

	return newNavigation;
    }
	
}
