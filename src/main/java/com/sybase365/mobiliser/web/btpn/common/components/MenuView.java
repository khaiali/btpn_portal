package com.sybase365.mobiliser.web.btpn.common.components;

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
import com.sybase365.mobiliser.util.tools.wicketutils.menu.MenuEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.SybaseMenu;

/**
 * This is Top Menu View Component for the BTPN Portals. All the top menu layout can be placed in this container.
 * 
 * @author Vikram Gunda
 */
public class MenuView extends WebMarkupContainer {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor the menu view
	 */
	public MenuView(String id, IModel<SybaseMenu> model, final Roles roles) {
		super(id, model);

		final SybaseMenu menu = model.getObject();

		ListView<IMenuEntry> menuLevel1items = new ListView<IMenuEntry>("menuLevel1items", menu.getEntries()) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<IMenuEntry> listItem) {
				final IMenuEntry entry = listItem.getModelObject();
				WebMarkupContainer menuLevel1item = new WebMarkupContainer("menuLevel1item") {

					private static final long serialVersionUID = 1L;

					@Override
					protected void onComponentTag(final ComponentTag tag) {
						super.onComponentTag(tag);
						if (entry.isActive()) {
							tag.append("class", "selected", " ");
						}
					}
				};

				final Link<MenuEntry> link1 = new Link<MenuEntry>("menuLevel1link", new Model<MenuEntry>(
					(MenuEntry) entry)) {

					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						for (IMenuEntry other : menu.getEntries()) {
							other.setActive(false);
						}
						((BaseWebSession) getSession()).setLeftMenu(null);
						getModelObject().setActive(true);
						getModelObject().setComponentResponsePage(this);
					}

				};
				link1.add(new Label("menuLevel1label", getLocalizer().getString(entry.getName(), this))
					.setRenderBodyOnly(true));
				menuLevel1item.add(link1);
				listItem.add(menuLevel1item);
				if (entry.getRequiredPrivilege() != null) {
					listItem.setVisible(roles.hasRole(entry.getRequiredPrivilege()));
				} else {
					listItem.setVisible(false);
				}
			}
		};
		menuLevel1items.setRenderBodyOnly(true);
		add(menuLevel1items);
	}
}