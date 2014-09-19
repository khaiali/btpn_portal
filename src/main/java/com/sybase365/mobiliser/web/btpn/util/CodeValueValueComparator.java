package com.sybase365.mobiliser.web.btpn.util;

import java.util.Comparator;

import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;

/**
 * This class is Value comparator for CodeValue.
 * 
 * @author Vikram Gunda
 */
public class CodeValueValueComparator implements Comparator<CodeValue> {
	
	private boolean sortAsc;
	
	public CodeValueValueComparator(boolean sortAsc){
		this.sortAsc = sortAsc;
	}

	@Override
	public int compare(CodeValue obj1, CodeValue obj2) {
		int result =  obj1.getValue().compareTo(obj2.getValue());			
		return sortAsc ? result : (-1) * result;
	}
}
