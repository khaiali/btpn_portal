package com.sybase365.mobiliser.web.cst.pages.customercare;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.money.contract.v5_0.audit.beans.Note;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class ContactDetailsPage extends CustomerCareMenuGroup {
	private static final long serialVersionUID = 1L;

	private Note note;

	private WebPage backPage;

	public ContactDetailsPage(Note note, WebPage backPage) {
		super();
		this.note = note;
		this.backPage = backPage;
		initPageComponents();
	}

	protected void initPageComponents() {
		Form<ContactDetailsPage> form = new Form<ContactDetailsPage>("contactDetailsForm",
			new CompoundPropertyModel<ContactDetailsPage>(this));
		form.add(new Label("noteId", String.valueOf(getNote().getId())));
		form.add(new Label("id", String.valueOf(getNote().getId())));
		form.add(new Label("category", getDisplayValue(String.valueOf(getNote().getCategory()),
			Constants.RESOURCE_BUNDLE_NOTE_CATEGORY)));
		form.add(new Label("status", getDisplayValue(String.valueOf(getNote().getStatus()),
			Constants.RESOURCE_BUNDLE_NOTE_STATUS)));
		form.add(new Label("date", PortalUtils.getFormattedDateTime(getNote().getCreated(), getMobiliserWebSession()
			.getLocale(), getMobiliserWebSession().getTimeZone())));
		form.add(new Label("agent", String.valueOf(getNote().getCreatedBy())));
		form.add(new Label("subject", getNote().getSubject()));
		form.add(new Label("text", getNote().getBody()));
		form.add(new Button("back") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(backPage);
			}
		});
		add(form);
	}

	public Note getNote() {
		return note;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Class getActiveMenu() {
		return ShowContactsPage.class;
	}
}
