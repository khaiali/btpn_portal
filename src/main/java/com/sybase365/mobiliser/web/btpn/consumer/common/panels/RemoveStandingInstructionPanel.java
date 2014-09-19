package com.sybase365.mobiliser.web.btpn.consumer.common.panels;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.core.schedtxn.facade.api.SchedTxnFacade;
import com.btpnwow.core.schedtxn.facade.contract.RemoveSchedTxnRequest;
import com.btpnwow.core.schedtxn.facade.contract.RemoveSchedTxnResponse;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.consumer.beans.StandingInstructionsBean;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.standing.StandingInstructionsPage;


public class RemoveStandingInstructionPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(RemoveStandingInstructionPanel.class);

	protected BtpnMobiliserBasePage basePage;

	StandingInstructionsBean instructionsBean;
	
	@SpringBean(name="siClient")
	private SchedTxnFacade siClient;
	

	public RemoveStandingInstructionPanel(String id, BtpnMobiliserBasePage basePage,
		StandingInstructionsBean instructionsBean) {
		super(id);
		this.basePage = basePage;
		this.instructionsBean = instructionsBean;
		constructPanel();
	}

	protected void constructPanel() {
		
		final Form<RemoveStandingInstructionPanel> form = new Form<RemoveStandingInstructionPanel>(
			"removeStandingInstructionForm", new CompoundPropertyModel<RemoveStandingInstructionPanel>(this));

		// Add feedback panel for Error Messages
		form.add(new FeedbackPanel("errorMessages"));

		form.add(new Label("instructionsBean.name"));
		form.add(new Label("instructionsBean.payer"));
		form.add(new Label("instructionsBean.payee"));
		form.add(new Label("instructionsBean.startDate"));
		form.add(new Label("instructionsBean.expiryDate"));
		form.add(new Label("instructionsBean.frequency"));
		form.add(new Label("instructionsBean.weekDay"));
		form.add(new Label("instructionsBean.type"));
		form.add(new Label("instructionsBean.amount"));

		form.add(new Button("confirmButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				if (removeStandingInstruction(instructionsBean.getName())) {
					basePage.getWebSession().info(getLocalizer().getString("remove.successMessage", this));
					setResponsePage(new StandingInstructionsPage(instructionsBean));
				}
			};
		});

		form.add(new Button("cancelButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(new StandingInstructionsPage());
			};
		});

		add(form);
	}
	
	
	private boolean removeStandingInstruction(String siName) {
		
		RemoveSchedTxnResponse response = null;
		boolean isRemoved = false;
		
		try {
			
			final RemoveSchedTxnRequest request = basePage.getNewMobiliserRequest(RemoveSchedTxnRequest.class);
			request.setId(Long.parseLong(instructionsBean.getId()));
			request.setFlags(0);
			response = siClient.remove(request);
			if (basePage.evaluateConsumerPortalMobiliserResponse(response)) {
				isRemoved = true;
			} else {
				error(response.getStatus().getValue());
			}
		} catch (Exception ex) {
			log.error("#An error occurred while calling removeStandingInstruction service", ex);
			error(getLocalizer().getString("error.exception", this));
		}
		return isRemoved;
	}

}
