package com.sybase365.mobiliser.web.btpn.consumer.common.panels;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.core.preregistered.facade.contract.AddPreRegisteredRequest;
import com.btpnwow.core.preregistered.facade.contract.AddPreRegisteredResponse;
import com.btpnwow.core.preregistered.facade.contract.RemovePreRegisteredRequest;
import com.btpnwow.core.preregistered.facade.contract.RemovePreRegisteredResponse;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.consumer.beans.ManageFavoritesBean;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.ManageFavoritesPage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.util.PortalUtils;


public class AddFavoriteConfirmPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(AddFavoriteConfirmPanel.class);

	protected BtpnMobiliserBasePage basePage;

	ManageFavoritesBean favoritesBean;

	private FeedbackPanel feedBack;

	public AddFavoriteConfirmPanel(String id, BtpnMobiliserBasePage basePage, ManageFavoritesBean favoritesBean) {
		super(id);
		this.basePage = basePage;
		this.favoritesBean = favoritesBean;
		constructPanel();
	}

	protected void constructPanel() {
		final Form<AddFavoriteConfirmPanel> form = new Form<AddFavoriteConfirmPanel>("addFavoriteConfirmForm",
			new CompoundPropertyModel<AddFavoriteConfirmPanel>(this));

		// Add feedback panel for Error Messages
		feedBack = new FeedbackPanel("errorMessages");
		feedBack.setOutputMarkupId(true);
		form.add(feedBack);

		Button okButton = new Button("okButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(ManageFavoritesPage.class);
			};
		};
		okButton.setOutputMarkupId(true);
		okButton.setVisible(false);
		form.add(okButton);

		Button confirmButton = new Button("confirmButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				if (PortalUtils.exists(favoritesBean.getSelectedLink())) {
					if (favoritesBean.getSelectedLink().equals("Remove")) {
						removeFavoriteByName(favoritesBean);
					}
				} else {
					addFavoriteRequest(favoritesBean);
				}
			};
		};
		confirmButton.setOutputMarkupId(true);
		form.add(confirmButton);

		// header message
		String message = "";
		if (PortalUtils.exists(favoritesBean)) {
			if (PortalUtils.exists(favoritesBean.getSelectedLink())) {
				if (favoritesBean.getSelectedLink().equals("Remove")) {
					message = getLocalizer().getString("headLine.confirmRemove", AddFavoriteConfirmPanel.this);
				} else if (favoritesBean.getSelectedLink().equals("Details")) {
					message = getLocalizer().getString("headLine.addFavorite", AddFavoriteConfirmPanel.this);
					okButton.setVisible(true);
					confirmButton.setVisible(false);
				}
			} else {
				message = getLocalizer().getString("headLine.addFavorite", AddFavoriteConfirmPanel.this);
			}

		}
		form.add(new Label("headermessage", message));
		form.add(new Label("favoritesBean.favoriteName"));
		form.add(new Label("favoritesBean.favoritesType.value"));
		form.add(new Label("favoritesBean.favoriteValue"));
		form.add(new Label("favoritesBean.description"));
		add(form);
	}
	
	
	private void addFavoriteRequest(ManageFavoritesBean favoritesBean) {
		
		try {
			
			log.info(" ### (AddFavoriteConfirmPanel::addFavoriteRequest) ### ");
			
			final AddPreRegisteredRequest request = basePage.getNewMobiliserRequest(AddPreRegisteredRequest.class);
			
			long customerId = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId();
			request.setCustomerId(customerId);
			String favName = favoritesBean.getFavoriteName();
			log.info(" ### (AddFavoriteConfirmPanel::addFavoriteRequest) FAV NAME ### " +favName);
			request.setName(favName);
			log.info(" ### (AddFavoriteConfirmPanel::addFavoriteRequest) TYPE ### " +favoritesBean.getFavoritesType().getId());
			request.setType(Integer.parseInt(favoritesBean.getFavoritesType().getId()));
			String favValue = favoritesBean.getFavoriteValue();
			log.info(" ### (AddFavoriteConfirmPanel::addFavoriteRequest) FAV VALUE ### " +favValue);
			request.setValue(favValue);
			request.setDescription(favoritesBean.getDescription());
			request.setFlags(0);
			
			AddPreRegisteredResponse addFavoriteResponse = basePage.getFavClient().add(request);
			log.info(" ### (AddFavoriteConfirmPanel::addFavoriteRequest) RESPONSE ### " +addFavoriteResponse.getStatus().getCode()); 
			if (basePage.evaluateConsumerPortalMobiliserResponse(addFavoriteResponse)) {
				basePage.getWebSession().info(
					getLocalizer().getString("favorite.add.success", AddFavoriteConfirmPanel.this));
				setResponsePage(new ManageFavoritesPage());
			} else {
				error(addFavoriteResponse.getStatus().getValue());
			}
			
		} catch (Exception ex) {
			log.error("#An error occurred while calling addFavorite service.", ex);
			error(getLocalizer().getString("error.exception", this));
		}
	}
	

	private void removeFavoriteByName(ManageFavoritesBean favoritesBean) {
		
		try {
			
			log.info(" ### (AddFavoriteConfirmPanel::removeFavoriteByName) ### ");
			
			final RemovePreRegisteredRequest request = basePage.getNewMobiliserRequest(RemovePreRegisteredRequest.class);
			log.info(" ### (AddFavoriteConfirmPanel::removeFavoriteByName) ID ### " +favoritesBean.getId());
			request.setId(favoritesBean.getId());
			request.setFlags(0);
			
			RemovePreRegisteredResponse removeFavoriteResponse = basePage.getFavClient().remove(request);
			log.info(" ### (AddFavoriteConfirmPanel::removeFavoriteByName) RESPONSE ### " +removeFavoriteResponse.getStatus().getCode());
			if (basePage.evaluateConsumerPortalMobiliserResponse(removeFavoriteResponse)
					&& removeFavoriteResponse.getStatus().getCode() == BtpnConstants.STATUS_CODE) {
				basePage.getWebSession().info(
					getLocalizer().getString("favorite.remove.success", AddFavoriteConfirmPanel.this));
				setResponsePage(new ManageFavoritesPage());
			} else {
				error(removeFavoriteResponse.getStatus().getValue());
			}
		
		} catch (Exception ex) {
			log.error("#An error occurred while calling removeFavoriteByName service.", ex);
			error(getLocalizer().getString("error.exception", this));
		}
	}
	
}
