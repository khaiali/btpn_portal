package com.sybase365.mobiliser.web.common.panels.alerts;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import com.sybase365.mobiliser.web.beans.SelectBean;
import com.sybase365.mobiliser.web.util.Constants;

/**
 * @author sagraw03 AlertFrequencyChooserPanel need to be added where we need to
 *         show frequency radio button.
 */
public class AlertMessageTypeChooserPanel extends Panel {
    /**
     * 
     */
    private static final long serialVersionUID = 9084969205565399137L;
    @SuppressWarnings("unchecked")
    private RadioGroup messageTypeGroup;
    private SelectBean selectedMessageTypeBean;
    private SelectBean messageTypeBean;

    private static final String WICKET_ID_messageTypeGroup = "messageTypeGroup";
    private static final String WICKET_ID_messageTypeResult = "messageTypeResult";
    private static final String WICKET_ID_messageTypeRadio = "messageTypeRadio";
    private static final String WICKET_ID_messageTypeName = "messageTypeName";

    @SuppressWarnings("unchecked")
    public AlertMessageTypeChooserPanel(String id,
	    SelectBean selectedMessageBean) {

	super(id);
	this.selectedMessageTypeBean = selectedMessageBean;

	messageTypeGroup = new RadioGroup(WICKET_ID_messageTypeGroup,
		new PropertyModel(this, "messageTypeBean"));
	messageTypeGroup.setRequired(true);
	messageTypeGroup.add(new AjaxFormChoiceComponentUpdatingBehavior() {
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onUpdate(AjaxRequestTarget arg0) {

	    }
	});

	ListView messageTypes = new ListView(WICKET_ID_messageTypeResult,
		getMessageTypes()) {
	    private static final long serialVersionUID = 1L;

	    protected void populateItem(ListItem item) {

		final SelectBean entry = (SelectBean) item.getModelObject();

		Radio selectRow = new Radio(WICKET_ID_messageTypeRadio,
			new Model(entry)) {
		    private static final long serialVersionUID = 1L;

		    @Override
		    protected void onComponentTag(ComponentTag tag) {

			if (entry.getId().equalsIgnoreCase(
				selectedMessageTypeBean.getId())) {
			    tag.put("checked", "checked");
			}
			super.onComponentTag(tag);
		    }
		};

		item.add(selectRow);
		item.add(new Label(WICKET_ID_messageTypeName,
			new PropertyModel(item.getModel(), "name")));
	    }
	};
	messageTypeGroup.add(messageTypes);
	add(messageTypeGroup);

    }

    public void setMessageTypeBean(SelectBean messageTypeBean) {
	this.messageTypeBean = messageTypeBean;
    }

    public SelectBean getMessageTypeBean() {
	return messageTypeBean;
    }

    /**
     * 
     * @return List<SelectBean>
     */
    public List<SelectBean> getMessageTypes() {
	List<SelectBean> result = new ArrayList<SelectBean>();
	SelectBean beanOne = new SelectBean(
		Constants.ALERT_NOTIFICATION_MSG_TYPE_TEXT, getLocalizer()
			.getString("MessageType.0", this));
	SelectBean beanTwo = new SelectBean(
		Constants.ALERT_NOTIFICATION_MSG_TYPE_CONV, getLocalizer()
			.getString("MessageType.1", this));
	result.add(beanOne);
	result.add(beanTwo);
	return result;
    }
}
