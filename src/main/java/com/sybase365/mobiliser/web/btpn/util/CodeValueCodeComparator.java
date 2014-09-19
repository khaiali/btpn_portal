package com.sybase365.mobiliser.web.btpn.util;

import java.util.Comparator;

import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;

/**
 * This class is Code comparator for CodeValue.
 * 
 * @author Vikram Gunda
 */
public class CodeValueCodeComparator implements Comparator<CodeValue> {
	
	private boolean sortAsc;
	
	public CodeValueCodeComparator(boolean sortAsc){
		this.sortAsc = sortAsc;
	}

	@Override
	public int compare(CodeValue obj1, CodeValue obj2) {
		int result =  obj1.getId().compareTo(obj1.getId());			
		return sortAsc ? result : (-1) * result;
	}
}
