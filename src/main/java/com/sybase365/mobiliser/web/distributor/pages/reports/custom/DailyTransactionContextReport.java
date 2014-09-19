package com.sybase365.mobiliser.web.distributor.pages.reports.custom;

import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.util.MobiliserWebSession;

public class DailyTransactionContextReport extends TransactionContextReport {

    private static final long serialVersionUID = 1L;

    public DailyTransactionContextReport(final MobiliserBasePage page,
	    final MobiliserWebSession webSession) {

	super(page, webSession);
    }

    @Override
    public String getReportName() {

	return "Daily Transaction";
    }
}
