package com.sybase365.mobiliser.web.dashboard.pages.servers.beans;

import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import com.sybase365.mobiliser.util.contract.v5_0.management.beans.AttributeBean;
import com.sybase365.mobiliser.util.contract.v5_0.management.beans.AttributeListBean;
import com.sybase365.mobiliser.util.tools.formatutils.FormatUtils;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.dashboard.base.GroupedRemoteManagedValues;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class EnvironmentValues extends GroupedRemoteManagedValues {

    public static String JAVA_LANG_OS = "java.lang:type=OperatingSystem";
    public static String JAVA_LANG_RUNTIME = "java.lang:type=Runtime";
    public static String JAVA_LANG_COMPILATION = "java.lang:type=Compilation";

    private AttributeListBean alb = new AttributeListBean();

    public EnvironmentValues(MobiliserBasePage mobBasePage) {
	super(mobBasePage);

	// operating system environment
	// addAttributeBean(JAVA_LANG_OS, "MaxFileDescriptorCount");
	// addAttributeBean(JAVA_LANG_OS, "OpenFileDescriptorCount");
	addAttributeBean(JAVA_LANG_OS, "CommittedVirtualMemorySize");
	addAttributeBean(JAVA_LANG_OS, "FreePhysicalMemorySize");
	addAttributeBean(JAVA_LANG_OS, "FreeSwapSpaceSize");
	addAttributeBean(JAVA_LANG_OS, "ProcessCpuTime");
	addAttributeBean(JAVA_LANG_OS, "TotalPhysicalMemorySize");
	addAttributeBean(JAVA_LANG_OS, "TotalSwapSpaceSize");
	addAttributeBean(JAVA_LANG_OS, "Name");
	addAttributeBean(JAVA_LANG_OS, "Version");
	addAttributeBean(JAVA_LANG_OS, "AvailableProcessors");
	addAttributeBean(JAVA_LANG_OS, "Arch");
	addAttributeBean(JAVA_LANG_OS, "SystemLoadAverage");

	// java runtime environment
	addAttributeBean(JAVA_LANG_RUNTIME, "Name");
	addAttributeBean(JAVA_LANG_RUNTIME, "StartTime");
	addAttributeBean(JAVA_LANG_RUNTIME, "Uptime");
	addAttributeBean(JAVA_LANG_RUNTIME, "VmName");
	addAttributeBean(JAVA_LANG_RUNTIME, "VmVendor");
	addAttributeBean(JAVA_LANG_RUNTIME, "VmVersion");
	addAttributeBean(JAVA_LANG_COMPILATION, "Name");

	// paths
	addAttributeBean(JAVA_LANG_RUNTIME, "ClassPath");
	addAttributeBean(JAVA_LANG_RUNTIME, "BootClassPath");
	addAttributeBean(JAVA_LANG_RUNTIME, "LibraryPath");

	refreshValues();
    }

    protected void refresh() {
	refreshValues();
    }

    private void addAttributeBean(String objectName, String attributeName) {
	AttributeBean attrBean = new AttributeBean();
	attrBean.setObjectName(objectName);
	attrBean.setAttributeName(attributeName);
	alb.getAttributeBean().add(attrBean);
    }

    @Override
    protected AttributeListBean getAttributeNames() {
	return alb;
    }

    public String getOs() {
	return getValue(JAVA_LANG_OS, "Name").getValue();
    }

    public String getVersion() {
	return getValue(JAVA_LANG_OS, "Version").getValue();
    }

    public String getArchitecture() {
	return getValue(JAVA_LANG_OS, "Arch").getValue();
    }

    public String getProcessors() {
	return getValue(JAVA_LANG_OS, "AvailableProcessors").getValue();
    }

    // public String getMaxFileDescriptorCount() {
    // return getValue(JAVA_LANG_OS, "MaxFileDescriptorCount").getValue();
    // }
    //
    // public String getOpenFileDescriptorCount() {
    // return getValue(JAVA_LANG_OS, "OpenFileDescriptorCount").getValue();
    // }

    public String getCommittedVirtualMemorySize() {
	return formatBytesAsMb(getValue(JAVA_LANG_OS,
		"CommittedVirtualMemorySize").getValue());
    }

    public String getFreePhysicalMemorySize() {
	return formatBytesAsMb(getValue(JAVA_LANG_OS, "FreePhysicalMemorySize")
		.getValue());
    }

    public String getFreeSwapSpaceSize() {
	return formatBytesAsMb(getValue(JAVA_LANG_OS, "FreeSwapSpaceSize")
		.getValue());
    }

    public String getProcessCpuTime() {
	return getValue(JAVA_LANG_OS, "ProcessCpuTime").getValue();
    }

    public String getTotalPhysicalMemorySize() {
	return formatBytesAsMb(getValue(JAVA_LANG_OS, "TotalPhysicalMemorySize")
		.getValue());
    }

    public String getTotalSwapSpaceSize() {
	return formatBytesAsMb(getValue(JAVA_LANG_OS, "TotalSwapSpaceSize")
		.getValue());
    }

    public String getSystemLoadAverage() {
	return getValue(JAVA_LANG_OS, "SystemLoadAverage").getValue();
    }

    public String getName() {
	return getValue(JAVA_LANG_RUNTIME, "Name").getValue();
    }

    public String getClassPath() {
	return addLineBreaksToPath(getValue(JAVA_LANG_RUNTIME, "ClassPath")
		.getValue());
    }

    public String getStartTime() {

	String strValue = getValue(JAVA_LANG_RUNTIME, "StartTime").getValue();

	if (PortalUtils.exists(strValue)) {

	    Date startTime = new Date(Long.parseLong(strValue));

	    XMLGregorianCalendar cal = FormatUtils
		    .getSaveXMLGregorianCalendar(startTime);

	    return PortalUtils.getFormattedDateTime(cal, mobBasePage
		    .getMobiliserWebSession().getLocale());
	} else {
	    return "";
	}
    }

    public String getUpTime() {
	String strValue = getValue(JAVA_LANG_RUNTIME, "Uptime").getValue();
	if (PortalUtils.exists(strValue)) {
	    return FormatUtils.formatDuration(Long.parseLong(strValue));
	}
	return "";
    }

    public String getVmName() {
	return getValue(JAVA_LANG_RUNTIME, "VmName").getValue();
    }

    public String getVmVendor() {
	return getValue(JAVA_LANG_RUNTIME, "VmVendor").getValue();
    }

    public String getVmVersion() {
	return getValue(JAVA_LANG_RUNTIME, "VmVersion").getValue();
    }

    public String getBootClassPath() {
	return addLineBreaksToPath(getValue(JAVA_LANG_RUNTIME, "BootClassPath")
		.getValue());
    }

    public String getLibraryPath() {
	return addLineBreaksToPath(getValue(JAVA_LANG_RUNTIME, "LibraryPath")
		.getValue());
    }

    public String getCompiler() {
	return getValue(JAVA_LANG_COMPILATION, "Name").getValue();
    }

    private String addLineBreaksToPath(String inStr) {
	if (PortalUtils.exists(inStr)) {
	    if (inStr.contains(";")) {
		return inStr.replace(';', '\n');
	    } else {
		return inStr.replace(':', '\n');
	    }
	} else {
	    return "";
	}
    }

    private String formatBytesAsGb(String inStr) {
	if (PortalUtils.exists(inStr)) {
	    long inLong = Long.parseLong(inStr);
	    return insertCommas(String.valueOf(inLong / (1000 * 1000 * 1024)))
		    + " GB";
	} else {
	    return "";
	}
    }

    private String formatBytesAsMb(String inStr) {
	if (PortalUtils.exists(inStr)) {
	    long inLong = Long.parseLong(inStr);
	    return insertCommas(String.valueOf(inLong / (1000 * 1024))) + " MB";
	} else {
	    return "";
	}
    }

    private String formatBytesAsKb(String inStr) {
	if (PortalUtils.exists(inStr)) {
	    long inLong = Long.parseLong(inStr);
	    return insertCommas(String.valueOf(inLong / 1024)) + " kB";
	} else {
	    return "";
	}
    }

    private String insertCommas(String str) {
	if (PortalUtils.exists(str)) {
	    if (str.length() < 4) {
		return str;
	    }
	    return insertCommas(str.substring(0, str.length() - 3)) + ","
		    + str.substring(str.length() - 3, str.length());
	} else {
	    return "";
	}
    }
}
