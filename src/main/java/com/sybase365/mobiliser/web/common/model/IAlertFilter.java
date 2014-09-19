package com.sybase365.mobiliser.web.common.model;

import java.util.List;

import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.AlertType;

/**
 * Interface to allow logic for filtering of alert types for a specific customer
 * 
 * @author msw
 */
public interface IAlertFilter {

    public void filterAlerts(List<AlertType> alertTypeList, long customerId);

}
