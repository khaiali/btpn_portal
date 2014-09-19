package com.sybase365.mobiliser.web.cst.pages.notificationmgr;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import com.sybase365.mobiliser.util.contract.v5_0.messaging.SendTemplateRequest;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.SendTemplateResponse;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.beans.Map;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.beans.Map.Entry;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.beans.MessageDetails;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.beans.MessageTemplate;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.beans.Receiver;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.beans.TemplateMessage;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.common.components.KeyValue;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class TestMessagePage extends BaseNotificationMgrPage {
    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(TestMessagePage.class);

    private MessageTemplate message;
    private String localeStr;
    private String receiver;
    private List<KeyValue<String, String>> paramsList;
    private final static String GET_PLACEHOLDER = "${get.";
    private final static String GET_CONFIDENTIAL_PLACEHOLDER = "${get-confidential.";
    private RequiredTextField<String> receiverTF;

    public TestMessagePage(final MessageTemplate message) {
	super();
	this.message = message;
	this.localeStr = convertLocale(message.getLocale());
	initPageComponent();
    }

    private final void initPageComponent() {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	final Form<?> form = new Form("testMessageForm",
		new CompoundPropertyModel<TestMessagePage>(this));
	form.add(new FeedbackPanel("errorMessages"));
	form.add(new TextField<String>("message.name").setEnabled(false));
	form.add(new TextField<String>("message.templateType")
		.setEnabled(false));
	form.add(new TextField<String>("localeStr").setEnabled(false));
	receiverTF = new RequiredTextField<String>("receiver");
	form.add(receiverTF.add(new ErrorIndicator()));
	paramsList = getParamsFromMessage(message);
	final ListView<KeyValue<String, String>> listView = new ListView<KeyValue<String, String>>(
		"paramsView", paramsList) {
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void populateItem(
		    final ListItem<KeyValue<String, String>> item) {
		Label label = new Label("paramName", item.getModelObject()
			.getKey() + "<span class=\"required\">*</span>");
		item.add(label.setEscapeModelStrings(false));
		item.add(new RequiredTextField<String>("paramValue",
			new PropertyModel<String>(item.getModelObject(),
				"value")).setLabel(
			new Model<String>(item.getModelObject().getKey())).add(
			new ErrorIndicator()));

	    }

	};
	form.add(listView);
	form.add(new Button("testMessage") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		if (validateReceiver())
		    sendTestMessage();
		else {
		    if (message.getTemplateType().equals(
			    Constants.TEMPLATE_TYPE_EMAIL_KEY))
			receiverTF.error(getLocalizer().getString(
				"invalid.receiver.email", this));
		    else
			receiverTF.error(getLocalizer().getString(
				"invalid.receiver.msisdn", this));
		}
	    }
	});

	add(form);

    }

    protected boolean validateReceiver() {
	if (message.getTemplateType().equals(Constants.TEMPLATE_TYPE_EMAIL_KEY))
	    return receiver.matches(Constants.REGEX_EMAIL);
	else
	    return receiver.matches(Constants.REGEX_MSISDN);
    }

    private List<KeyValue<String, String>> getParamsFromMessage(
	    final MessageTemplate message) {

	final List<KeyValue<String, String>> result = new ArrayList<KeyValue<String, String>>();
	String analyze = message.getContent().getContent();
	if (PortalUtils.exists(message.getAlternativeContent()))
	    analyze += message.getAlternativeContent().getContent();

	while (analyze.contains(GET_PLACEHOLDER)) {
	    analyze = analyze.substring(analyze.indexOf(GET_PLACEHOLDER)
		    + GET_PLACEHOLDER.length());
	    final String paramKey = analyze.substring(0, analyze.indexOf('}'));
	    final KeyValue<String, String> newParam = new KeyValue<String, String>(
		    paramKey, null);
	    if (!result.contains(newParam)) {
		result.add(newParam);
	    }
	}

	analyze = message.getContent().getContent();
	if (PortalUtils.exists(message.getAlternativeContent()))
	    analyze += message.getAlternativeContent().getContent();

	while (analyze.contains(GET_CONFIDENTIAL_PLACEHOLDER)) {
	    analyze = analyze.substring(analyze
		    .indexOf(GET_CONFIDENTIAL_PLACEHOLDER)
		    + GET_CONFIDENTIAL_PLACEHOLDER.length());
	    final String paramKey = analyze.substring(0, analyze.indexOf('}'));
	    final KeyValue<String, String> newParam = new KeyValue<String, String>(
		    paramKey, null);
	    if (!result.contains(newParam)) {
		result.add(newParam);
	    }
	}

	return result;
    }

    protected void sendTestMessage() {
	try {
	    SendTemplateRequest request = getNewMobiliserRequest(SendTemplateRequest.class);
	    TemplateMessage template = new TemplateMessage();
	    if (PortalUtils.exists(paramsList)) {
		Map paramMap = new Map();
		for (KeyValue<String, String> kvParam : paramsList) {
		    Entry entry = new Entry();
		    entry.setKey(kvParam.getKey());
		    entry.setValue(kvParam.getValue());
		    paramMap.getEntry().add(entry);
		}
		template.setParameters(paramMap);
	    }
	    template.setChannel(getConfiguration().getChannelForTestMessage());
	    template.setSender(message.getSender());
	    template.setSubject(message.getSubject());
	    template.setType(message.getTemplateType());
	    Receiver rcv = new Receiver();
	    rcv.setValue(receiver);
	    template.getReceiver().add(rcv);
	    MessageDetails detail = new MessageDetails();
	    detail.setLocale(message.getLocale());
	    detail.setTemplate(message.getName());
	    template.setDetails(detail);
	    request.setMessage(template);
	    SendTemplateResponse response = wsMsgClient.sendTemplate(request);
	    if (evaluateMobiliserResponse(response)) {
		info(getLocalizer()
			.getString("notification.send.success", this));
	    }
	} catch (Exception e) {
	    LOG.error("An error occured in sending test notification", e);
	    error(getLocalizer().getString("notification.send.error", this));
	}
    }

    public void setLocaleStr(final String localeStr) {
	this.localeStr = localeStr;
    }

    public String getLocaleStr() {
	return localeStr;
    }

    public void setMessage(final MessageTemplate message) {
	this.message = message;
    }

    public MessageTemplate getMessage() {
	return message;
    }

    public void setReceiver(final String receiver) {
	this.receiver = receiver;
    }

    public String getReceiver() {
	return receiver;
    }

}
