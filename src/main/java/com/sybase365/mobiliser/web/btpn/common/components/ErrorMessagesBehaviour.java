package com.sybase365.mobiliser.web.btpn.common.components;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.ComponentTag;

public class ErrorMessagesBehaviour extends AbstractBehavior {

	private static final long serialVersionUID = 1L;

	Component componentToCheck = null;

	public ErrorMessagesBehaviour() {
		super();
	}

	public ErrorMessagesBehaviour(Component componentToCheck) {
		super();
		this.componentToCheck = componentToCheck;
	}

	@Override
	public void onComponentTag(Component component, ComponentTag tag) {
		super.onComponentTag(component, tag);
		if (componentToCheck == null) {
			componentToCheck = component;
		}
		if (componentToCheck.hasErrorMessage()) {
			FeedbackMessage message = componentToCheck.getFeedbackMessage();
			final String markupId = componentToCheck.getMarkupId();
			final String javascript = "var node = document.getElementsByName('"+ markupId +"')[0].parentNode;"
					+ "node.className = node.className + ' error';" + "var errorDiv = document.createElement('div');"
					+ "errorDiv.className = 'errorText';"
					+ "var errorText = document.createElement('span');errorText.innerHTML = " + message + "<br/> "
					+ ";errorDiv.appendChild(errorText);node.appendChild(errorDiv)";
		}
	}
}
