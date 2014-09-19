package com.sybase365.mobiliser.web.btpn.common.components;

import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.authorization.strategies.role.Roles;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.sybase365.mobiliser.util.tools.wicketutils.BaseWebSession;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.IMenuEntry;

/**
 * This is LeftMenu View Component for the BTPN Portals. All the left menu layout can be placed in this container.
 * 
 * @author Vikram Gunda
 */
public class LeftMenuView extends WebMarkupContainer {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor the left menu
	 */
	public LeftMenuView(String id, IModel<? extends List<IMenuEntry>> model, final Roles roles) {
		super(id, model);

		final LinkedList<IMenuEntry> menu = (LinkedList<IMenuEntry>) model.getObject();

		boolean anyActive = false;
		if (menu != null) {
			for (IMenuEntry entry : menu) {
				if (entry.isActive()) {
					anyActive = true;
				}
			}
			if (!anyActive && menu.size() > 0) {
				menu.get(0).setActive(true);
			}
		}


		// Use a list view to populate the menu items
		ListView<IMenuEntry> leftMenuItems = new ListView<IMenuEntry>("leftMenuItems", menu) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<IMenuEntry> listItem) {
				final IMenuEntry entry = listItem.getModelObject();

				WebMarkupContainer leftMenuItem = new WebMarkupContainer("leftMenuItem") {

					private static final long serialVersionUID = 1L;

					@Override
					protected void onComponentTag(final ComponentTag tag) {
						super.onComponentTag(tag);
						if (entry.isActive()) {
							tag.append("class", "selected", " ");
						}
					}
				};

				final Link<IMenuEntry> link = new Link<IMenuEntry>("leftMenuLink", new Model<IMenuEntry>(entry)) {

					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						for (IMenuEntry other : menu) {
							other.setActive(false);
						}
						entry.setActive(true);
						((BaseWebSession) getSession()).setLeftMenu(menu);
						entry.setComponentResponsePage(this);
					}
				};
				link.add(new Label("leftMenuLabel", getLocalizer().getString(entry.getName(), this))
					.setRenderBodyOnly(true));
				leftMenuItem.add(link);
				listItem.add(leftMenuItem);
				if (entry.getRequiredPrivilege() != null) {
					listItem.setVisible(roles.hasRole(entry.getRequiredPrivilege()));
				}else{
					listItem.setVisible(false);
				}
			}
		};
		leftMenuItems.setRenderBodyOnly(true);
		add(leftMenuItems);
	}

}
