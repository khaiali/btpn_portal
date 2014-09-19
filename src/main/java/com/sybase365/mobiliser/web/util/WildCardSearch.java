package com.sybase365.mobiliser.web.util;

import java.util.ArrayList;
import java.util.List;

public class WildCardSearch {

    private List<String> result = new ArrayList<String>();

    /**
     * @param pattern
     * @param searchList
     * @return
     */
    public List<String> getFilteredList(String pattern, List<String> searchList) {

	for (String searchItem : searchList) {
	    if (searchItem != null) {
		if (wildCardMatch(searchItem.toUpperCase(),
			pattern.toUpperCase())) {
		    result.add(searchItem);
		}
	    }
	}
	return result;
    }

    /**
     * @param searchItem
     * @param pattern
     * @return
     */
    private boolean wildCardMatch(String searchItem, String pattern) {

	String[] tokens = pattern.split("\\*");

	int index = pattern.indexOf("*");

	// Pattern doesn't starts with *
	if (index != 0) {
	    if (!searchItem.startsWith(tokens[0])) {
		return false;
	    }
	}
	// Pattern starts with *
	else {
	    // Pattern doesn't ends with *
	    if (!(pattern.substring(index + 1).indexOf("*") == (pattern
		    .substring(index + 1)).length() - 1)) {
		if (searchItem.startsWith(tokens[1])
			|| !searchItem.endsWith(tokens[1])) {
		    return false;
		}
	    }
	}

	for (String token : tokens) {
	    int idx = searchItem.indexOf(token);

	    if (idx == -1) {
		return false;
	    }

	    searchItem = searchItem.substring(idx + token.length());
	}

	return true;
    }

}
