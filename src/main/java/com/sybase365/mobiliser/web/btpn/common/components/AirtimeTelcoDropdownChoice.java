package com.sybase365.mobiliser.web.btpn.common.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.util.WildcardListModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.sybase365.mobiliser.custom.btpn.services.contract.api.IAirtimeTopupEndpoint;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.topup.GetTelcosRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.topup.GetTelcosResponse;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.CodeValueCodeComparator;
import com.sybase365.mobiliser.web.btpn.util.CodeValueValueComparator;


public class AirtimeTelcoDropdownChoice extends DropDownChoice<CodeValue> {

	private static final long serialVersionUID = 1L;

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AirtimeTelcoDropdownChoice.class);

	private boolean sortKeys;

	private boolean sortAsc;

	@SpringBean(name = "airTimeClient")
	public IAirtimeTopupEndpoint airTimeClient;

	/**
	 * Constructor for this DropDownChoice.
	 * 
	 * @param id id for the DropDownChoice
	 */
	public AirtimeTelcoDropdownChoice(String id, boolean sortKeys, boolean sortAsc) {
		super(id);
		this.sortKeys = sortKeys;
		this.sortAsc = sortAsc;
		// Add Default Choice Render
		setChoiceRenderer(new ChoiceRenderer<CodeValue>(BtpnConstants.DISPLAY_EXPRESSION, BtpnConstants.ID_EXPRESSION));

	}

	/**
	 * Override the before render method. This method fethces the values to be populated in this dropdown before
	 * rendering the dropdown and sets the choices list.
	 */
	@Override
	protected void onBeforeRender() {

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

	/**
	 * This is used to fetch the choices for dropdown
	 */
	protected List<CodeValue> getChoiceList() throws Exception {
		
		List<CodeValue> choicesList = new ArrayList<CodeValue>();
		
		try {
			
			final GetTelcosRequest request = GetTelcosRequest.class.newInstance();
			request.setCallback(null);
			request.setConversationId(UUID.randomUUID().toString());
			request.setOrigin("mobiliser-web");
			request.setRepeat(Boolean.FALSE);
			request.setTraceNo(UUID.randomUUID().toString());
			final GetTelcosResponse response = airTimeClient.getTelcos(request);
			if (response.getStatus().getCode() == 0) {
				choicesList = getTelcoList(response.getTelecos());
				log.debug("AirtimeOperatorDropdownChoice:getChoiceList() ==> Airtime Telcos Count : " 
						+ choicesList.size());
			} else {
				error(getLocalizer().getString("error.telco", this));
			}
			
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			log.error("AirtimeTelcoDropdownChoice:onBeforeRender() ===> Could not retrieve drop down choices", e);
		}

		if (sortKeys) {
			Collections.sort(choicesList, new CodeValueCodeComparator(sortAsc));
		} else {
			Collections.sort(choicesList, new CodeValueValueComparator(sortAsc));
		}
		
		return choicesList;
	}

	
	private List<CodeValue> getTelcoList(

		List<com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.topup.Result> list) {
		List<CodeValue> telcos = new ArrayList<CodeValue>();
		for (com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.topup.Result result : list) {
			telcos.add(new CodeValue(result.getCode(), result.getDescription()));
		}
		
		return telcos;
	}

}
