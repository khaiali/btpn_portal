package com.sybase365.mobiliser.web.btpn.common.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.util.WildcardListModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.sybase365.mobiliser.custom.btpn.services.contract.api.IBillPaymentEndpoint;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.billpayment.Result;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.billpayment.GetBillersRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.billpayment.GetBillersResponse;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.CodeValueCodeComparator;
import com.sybase365.mobiliser.web.btpn.util.CodeValueValueComparator;

/**
 * This class allows us display the biller codes from the biller service. These biller are populated in the dropddown
 * from a mobiliser service.
 * 
 * @author Vikram Gunda
 */
public class BillerCodeDropdownChoice extends DropDownChoice<CodeValue> {

	private static final long serialVersionUID = 1L;

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(BillerCodeDropdownChoice.class);

	private boolean sortKeys;

	private boolean sortAsc;

	@SpringBean(name = "billPaymentClient")
	public IBillPaymentEndpoint billPaymentClient;

	/**
	 * Constructor for this DropDownChoice.
	 * 
	 * @param id id for the DropDownChoice
	 * @param sortKeys sortKeys for dropdown
	 */
	public BillerCodeDropdownChoice(String id, boolean sortKeys, boolean sortAsc) {
		super(id);
		this.sortKeys = sortKeys;
		this.sortAsc = sortAsc;
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
			LOG.warn("BillerCodeDropdownChoice:onBeforeRender() ===> Could not retrieve drop down choices", e);
		}

		super.onBeforeRender();
	}

	/**
	 * This is used to fetch the choices for dropdown
	 */
	protected List<CodeValue> getChoiceList() throws Exception {
		
		List<Result> billerList = new ArrayList<Result>();
		
		try {
			
			GetBillersRequest request = GetBillersRequest.class.newInstance();
			request.setCallback(null);
			request.setConversationId(UUID.randomUUID().toString());
			request.setOrigin("mobiliser-web");
			request.setRepeat(Boolean.FALSE);
			request.setTraceNo(UUID.randomUUID().toString());
			GetBillersResponse response = billPaymentClient.getBillers(request);
			if (response.getStatus().getCode() == 0) {
				billerList = response.getBiller();
				LOG.debug("BillerCodeDropdownChoice:getChoiceList() ==> Billers Count : " + billerList.size());
			} else {
				error(getLocalizer().getString("error.biller", this));
			}
			
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			LOG.error("BillerCodeDropdownChoice:onBeforeRender() ===> Could not retrieve drop down choices", e);
		}
		
		final List<CodeValue> choicesList = new ArrayList<CodeValue>();
		for (Result result : billerList) {
			choicesList.add(new CodeValue(result.getCode(), result.getDescription()));
		}
		
		if (sortKeys) {
			Collections.sort(choicesList, new CodeValueCodeComparator(sortAsc));
		} else {
			Collections.sort(choicesList, new CodeValueValueComparator(sortAsc));
		}
		
		return choicesList;
	}
	
}
