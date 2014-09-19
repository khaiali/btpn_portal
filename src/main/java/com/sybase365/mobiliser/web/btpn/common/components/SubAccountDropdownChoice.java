package com.sybase365.mobiliser.web.btpn.common.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.util.WildcardListModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.btpnwow.core.account.facade.api.AccountFacade;
import com.btpnwow.core.account.facade.contract.AccountInformationType;
import com.btpnwow.core.account.facade.contract.CustomerIdentificationType;
import com.btpnwow.core.account.facade.contract.FindCustomerAccountRequest;
import com.btpnwow.core.account.facade.contract.FindCustomerAccountResponse;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.CodeValueCodeComparator;
import com.sybase365.mobiliser.web.btpn.util.CodeValueValueComparator;


public class SubAccountDropdownChoice extends DropDownChoice<CodeValue> {

	private static final long serialVersionUID = 1L;

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SubAccountDropdownChoice.class);

	protected BtpnMobiliserBasePage basePage;
	
	private boolean sortKeys;

	private boolean sortAsc;
	
	private String userName;
	
	/* End Point */
	@SpringBean(name="accountClient")
	private AccountFacade accountClient;
	

	/**
	 * Constructor for this DropDownChoice.
	 * 
	 * @param id id for the DropDownChoice
	 */
	public SubAccountDropdownChoice(String id, boolean sortKeys, boolean sortAsc, String userName) {
		super(id);
		log.info("### SubAccountDropdownChoice ###");
		this.sortKeys = sortKeys;
		this.sortAsc = sortAsc;
		this.userName=  userName;
		// Add Default Choice Render
		setChoiceRenderer(new ChoiceRenderer<CodeValue>(BtpnConstants.DISPLAY_EXPRESSION, BtpnConstants.ID_EXPRESSION));

	}

	/**
	 * Override the before render method. This method fethces the values to be populated in this dropdown before
	 * rendering the dropdown and sets the choices list.
	 */
	@Override
	protected void onBeforeRender() {
		log.info("### onBeforeRender ###");
		// this is the place where we can determine the order of the choices
		getChoices().clear();
		// get the choices for our drop down list
		try {
			setChoices(new WildcardListModel<CodeValue>(getChoiceList()));
		} catch (final Exception e) {
			// not much we can do here but logg the exception
			log.warn("AirtimeTelcoDropdownChoice:onBeforeRender() ===> Could not retrieve drop down choices", e);
		}

		super.onBeforeRender();
	}
	
	
	protected List<CodeValue> getChoiceList() throws Exception {
		
		log.info("### (SubAccountDropdownChoice::getChoiceList) ###");
		final List<CodeValue> choicesList = new ArrayList<CodeValue>();
		
		try {
			
			final FindCustomerAccountRequest req = FindCustomerAccountRequest.class.newInstance();
			req.setCallback(null);
			req.setConversationId(UUID.randomUUID().toString());
			req.setOrigin("mobiliser-web");
			req.setRepeat(Boolean.FALSE);
			req.setTraceNo(UUID.randomUUID().toString());
			CustomerIdentificationType obj = new CustomerIdentificationType();
			obj.setType(0);
			obj.setValue(userName);
			req.setIdentification(obj);
			req.getPaymentInstrumentType().add(1);
			req.setFlags(0);
			log.info("### (SubAccountDropdownChoice::getChoiceList) BEFORE SERVICE ###");
			final FindCustomerAccountResponse response = accountClient.find(req);
			log.info("### (SubAccountDropdownChoice::getChoiceList) AFTER SERVICE ###" + response.getStatus().getCode());
			if (response.getStatus().getCode() == BtpnConstants.STATUS_CODE) {
				final List<AccountInformationType> responseBeanList = response.getAccount();
				for (AccountInformationType res : responseBeanList) {
					log.info(" ### (SubAccountDropdownChoice::getChoiceList) ID  AND ALIAS ### " +res.getId()+ " - " +res.getAlias());
					if (res.getAlias() != null)
					choicesList.add(new CodeValue(String.valueOf(res.getId()), res.getAlias()));
				}
			} else {
				error(getLocalizer().getString("error.subaccount", this));
			}
		
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			log.error("### SubAccountDropdownChoice::onBeforeRender() ===> Could not retrieve drop down choices ###", e);
		}
		
		if (sortKeys) {
			Collections.sort(choicesList, new CodeValueCodeComparator(sortAsc));
		} else {
			Collections.sort(choicesList, new CodeValueValueComparator(sortAsc));
		}
		
		return choicesList;
	}

}
