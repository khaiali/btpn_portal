package com.sybase365.mobiliser.web.dashboard.pages.servers.beans;

import java.util.List;

import com.sybase365.mobiliser.util.contract.v5_0.management.beans.AttributeBean;
import com.sybase365.mobiliser.util.contract.v5_0.management.beans.AttributeListBean;
import com.sybase365.mobiliser.util.contract.v5_0.management.beans.ObjectInstanceBean;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.dashboard.base.GroupedRemoteManagedValues;

public class EhcacheValues extends GroupedRemoteManagedValues {

    public static final String GROUP_ID = "net.sf.ehcache.hibernate";
    public static final String HIBERNATE_STATS = "EhcacheHibernateStats";

    String hibernateStatsObjectName;

    private AttributeListBean alb = new AttributeListBean();

    public EhcacheValues(MobiliserBasePage mobBasePage) {
	super(mobBasePage);

	List<ObjectInstanceBean> hibernateStatsBeans = getRemoteManagedObjects(GROUP_ID
		+ ":*,type=" + HIBERNATE_STATS);

	for (ObjectInstanceBean bean : hibernateStatsBeans) {
	    hibernateStatsObjectName = bean.getObjectName();
	    addAttributeBean(hibernateStatsObjectName, "CloseStatementCount");
	    addAttributeBean(hibernateStatsObjectName, "FlushCount");
	    addAttributeBean(hibernateStatsObjectName,
		    "HibernateStatisticsSupported");
	    addAttributeBean(hibernateStatsObjectName, "MaxGetTimeMillis");
	    addAttributeBean(hibernateStatsObjectName, "MinGetTimeMillis");
	    addAttributeBean(hibernateStatsObjectName, "OptimisticFailureCount");
	    addAttributeBean(hibernateStatsObjectName, "PrepareStatementCount");
	    addAttributeBean(hibernateStatsObjectName, "QueryExecutionCount");
	    addAttributeBean(hibernateStatsObjectName, "QueryExecutionRate");
	    addAttributeBean(hibernateStatsObjectName, "RegionCachesEnabled");
	    addAttributeBean(hibernateStatsObjectName, "SessionCloseCount");
	    addAttributeBean(hibernateStatsObjectName, "SessionOpenCount");
	    addAttributeBean(hibernateStatsObjectName, "StatisticsEnabled");
	    addAttributeBean(hibernateStatsObjectName,
		    "SuccessfulTransactionCount");
	    addAttributeBean(hibernateStatsObjectName, "TransactionCount");
	}

	refreshValues();

    }

    @Override
    protected AttributeListBean getAttributeNames() {
	return alb;
    }

    private void addAttributeBean(String objectName, String attributeName) {
	AttributeBean attrBean = new AttributeBean();
	attrBean.setObjectName(objectName);
	attrBean.setAttributeName(attributeName);
	alb.getAttributeBean().add(attrBean);
    }

    private String getAttributeValue(String objectName, String attributesName) {
	return getValue(objectName, attributesName) == null ? "" : getValue(
		objectName, attributesName).getValue();
    }

    public String getCloseStatementCount() {
	return getAttributeValue(hibernateStatsObjectName,
		"CloseStatementCount");
    }

    public String getFlushCount() {
	return getAttributeValue(hibernateStatsObjectName, "FlushCount");
    }

    public String getHibernateStatisticsSupported() {
	return getAttributeValue(hibernateStatsObjectName,
		"HibernateStatisticsSupported");
    }

    public String getMaxGetTimeMillis() {
	return getAttributeValue(hibernateStatsObjectName, "MaxGetTimeMillis");
    }

    public String getMinGetTimeMillis() {
	return getAttributeValue(hibernateStatsObjectName, "MinGetTimeMillis");
    }

    public String getOptimisticFailureCount() {
	return getAttributeValue(hibernateStatsObjectName,
		"OptimisticFailureCount");
    }

    public String getPrepareStatementCount() {
	return getAttributeValue(hibernateStatsObjectName,
		"PrepareStatementCount");
    }

    public String getQueryExecutionCount() {
	return getAttributeValue(hibernateStatsObjectName,
		"QueryExecutionCount");
    }

    public String getQueryExecutionRate() {
	return getAttributeValue(hibernateStatsObjectName, "QueryExecutionRate");
    }

    public String getRegionCachesEnabled() {
	return getAttributeValue(hibernateStatsObjectName,
		"RegionCachesEnabled");
    }

    public String getSessionCloseCount() {
	return getAttributeValue(hibernateStatsObjectName, "SessionCloseCount");
    }

    public String getSessionOpenCount() {
	return getAttributeValue(hibernateStatsObjectName, "SessionOpenCount");
    }

    public String getStatisticsEnabled() {
	return getAttributeValue(hibernateStatsObjectName, "StatisticsEnabled");
    }

    public String getSuccessfulTransactionCount() {
	return getAttributeValue(hibernateStatsObjectName,
		"SuccessfulTransactionCount");
    }

    public String getTransactionCount() {
	return getAttributeValue(hibernateStatsObjectName, "TransactionCount");
    }

}
