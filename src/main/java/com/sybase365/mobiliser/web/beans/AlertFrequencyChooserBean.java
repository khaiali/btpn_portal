/**
 * 
 */
package com.sybase365.mobiliser.web.beans;

import java.io.Serializable;

/**
 * @author sagraw03
 * 
 */
public class AlertFrequencyChooserBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean frequencyNoLimitCheck;
    private String durationFrequency;
    private String maxFrequency;

    public void setFrequencyNoLimitCheck(boolean frequencyNoLimitCheck) {
	this.frequencyNoLimitCheck = frequencyNoLimitCheck;
    }

    public boolean isFrequencyNoLimitCheck() {
	return frequencyNoLimitCheck;
    }

    public void setDurationFrequency(String durationFrequency) {
	this.durationFrequency = durationFrequency;
    }

    public String getDurationFrequency() {
	return durationFrequency;
    }

    public void setMaxFrequency(String maxFrequency) {
	this.maxFrequency = maxFrequency;
    }

    public String getMaxFrequency() {
	return maxFrequency;
    }

}
