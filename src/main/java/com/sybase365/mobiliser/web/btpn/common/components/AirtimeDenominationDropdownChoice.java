package com.sybase365.mobiliser.web.btpn.common.components;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.util.WildcardListModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.sybase365.mobiliser.custom.btpn.services.contract.api.IAirtimeTopupEndpoint;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.topup.GetDenominationsRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.topup.GetDenominationsResponse;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This class displays the denomination for the Airtime topup telcos. This dropdown displays the Airtime denominations
 * based on the telco operator selected.
 * 
 * @author Vikram Gunda
 */
public class AirtimeDenominationDropdownChoice extends DropDownChoice<CodeValue> {

	private static final long serialVersionUID = 1L;

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
		.getLogger(AirtimeDenominationDropdownChoice.class);

	private boolean sortKeys;

	private boolean sortAsc;

	private String telco;

	@SpringBean(
			name = "airTimeClient")
	public IAirtimeTopupEndpoint airTimeClient;

	/**
	 * Constructor for this DropDownChoice.
	 * 
	 * @param id id for the DropDownChoice
	 */
	public AirtimeDenominationDropdownChoice(String id, boolean sortKeys, boolean sortAsc, final String telco) {
		super(id);
		this.sortKeys = sortKeys;
		this.sortAsc = sortAsc;
		this.telco = telco;
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
			LOG.warn("AirtimeTelcoDropdownChoice:onBeforeRender() ===> Could not retrieve drop down choices", e);
		}

		super.onBeforeRender();
	}

	/**
	 * This is used to fetch the choices for dropdown
	 */
	protected List<CodeValue> getChoiceList() throws Exception {
		if (!PortalUtils.exists(telco)) {
			return new ArrayList<CodeValue>();
		}
		List<CodeValue> denominationsList = new ArrayList<CodeValue>();
		try {
			final GetDenominationsRequest request = GetDenominationsRequest.class.newInstance();
			request.setCallback(null);
			request.setConversationId(UUID.randomUUID().toString());
			request.setOrigin("mobiliser-web");
			request.setRepeat(Boolean.FALSE);
			request.setTraceNo(UUID.randomUUID().toString());
			request.setTelcoId(telco);
			final GetDenominationsResponse response = airTimeClient.getDenominations(request);
			if (response.getStatus().getCode() == 0) {
				// telcoList = response.getBiller();
				denominationsList = getTelcoList(response.getTelecos());
				LOG.debug("AirtimeDenominationDropdownChoice:getChoiceList() ==> Airtime Denominations Count : "
						+ denominationsList.size());
			} else {
				error(getLocalizer().getString("error.denomination", this));
			}
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			LOG.error("AirtimeDenominationDropdownChoice:onBeforeRender() ===> Could not retrieve drop down choices", e);
		}
		return denominationsList;
	}

	private List<CodeValue> getTelcoList(
		List<com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.topup.Result> list) {
		List<CodeValue> telcos = new ArrayList<CodeValue>();
		for (com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.topup.Result result : list) {
			telcos.add(new CodeValue(result.getCode(), result.getDescription()));
		}
		return telcos;
	}

	public String getTelco() {
		return telco;
	}

	public void setTelco(String telco) {
		this.telco = telco;
	}
}
