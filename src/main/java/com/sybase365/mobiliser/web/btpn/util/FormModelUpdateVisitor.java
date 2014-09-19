package com.sybase365.mobiliser.web.btpn.util;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IFormModelUpdateListener;

/**
 * Visitor used to update component models
 */
public class FormModelUpdateVisitor implements Component.IVisitor<Component> {
	private final Form<?> formFilter;

	/**
	 * Constructor
	 * 
	 * @param formFilter
	 */
	public FormModelUpdateVisitor(Form<?> formFilter) {
		this.formFilter = formFilter;
	}

	/** {@inheritDoc} */
	public Object component(Component component) {
		if (component instanceof IFormModelUpdateListener) {
			final Form<?> form = Form.findForm(component);
			if (form != null) {
				if (this.formFilter == null || this.formFilter == form) {
					if (form.isEnabledInHierarchy()) {
						if (component.isVisibleInHierarchy() && component.isEnabledInHierarchy()) {
							((IFormModelUpdateListener) component).updateModel();
						}
					}
				}
			}
		}
		return Component.IVisitor.CONTINUE_TRAVERSAL;
	}
}