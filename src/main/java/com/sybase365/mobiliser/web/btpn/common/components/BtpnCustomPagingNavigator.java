package com.sybase365.mobiliser.web.btpn.common.components;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.behavior.IBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.Loop;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.navigation.paging.IPagingLabelProvider;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigation;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;

/**
 * This is the custom navigator for Btpn Pages lists.
 * 
 * @author Vikram Gunda
 */
public class BtpnCustomPagingNavigator extends PagingNavigator {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor for this page.
	 * 
	 * @param id id for this component
	 * @param pageable pageable for this component
	 */
	public BtpnCustomPagingNavigator(String id, IPageable pageable) {
		super(id, pageable);
	}

	/**
	 * Constructor for this page.
	 * 
	 * @param id id for this component
	 * @param pageable pageable for this component
	 * @param labelProvider labelProvider for this component
	 */
	public BtpnCustomPagingNavigator(String id, IPageable pageable, IPagingLabelProvider labelProvider) {
		super(id, pageable, labelProvider);
	}

	/**
	 * New Navigation method for BTPN.
	 * 
	 * @param labelProvider labelProvider for this component
	 * @param pageable pageable for this component
	 */
	@Override
	protected PagingNavigation newNavigation(IPageable pageable, IPagingLabelProvider labelProvider) {

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

			private static final long serialVersionUID = 1L;			

			@Override
			protected void populateItem(final Loop.LoopItem loopItem)
			{
				super.populateItem(loopItem);
				final int pageIndex = getStartIndex() + loopItem.getIteration();
				loopItem.add(new Label("comma", ", ").setVisible(pageable.getPageCount() != pageIndex + 1));
			}

			@Override
			protected AbstractLink newPagingNavigationLink(String id, IPageable pageable, int pageIndex) {
				AbstractLink link = super.newPagingNavigationLink(id, pageable, pageIndex);
				link.add(currentPageLinkClassifier);
				return link;
			}
		};

		return newNavigation;
	}
}
