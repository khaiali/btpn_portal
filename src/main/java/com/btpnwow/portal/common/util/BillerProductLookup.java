package com.btpnwow.portal.common.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import au.com.bytecode.opencsv.CSVParser;

import com.sybase365.mobiliser.util.tools.wicketutils.resourceloader.ILookupMapUtility;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;

@Service("billerProductLookup")
@Scope("singleton")
public class BillerProductLookup {

	@Resource(name = "btpnLookupMapUtilitiesImpl")
	public ILookupMapUtility lookupMapUtility;

	private Map<String, String> raw;
	
	private final Map<String, BillerProduct> products;
	
	private final Map<String, BillerProduct> productByBillerIdAndProductId;
	
	private final Map<String, List<CodeValue>> hcv;
	
	private final CSVParser parser;
	
	public BillerProductLookup() {
		this.raw = null;
		
		this.products = new HashMap<String, BillerProductLookup.BillerProduct>();
		
		this.productByBillerIdAndProductId = new HashMap<String, BillerProductLookup.BillerProduct>();
		
		this.hcv = new HashMap<String, List<CodeValue>>();
		
		parser = new CSVParser();
	}
	
	private String nullIfEmpty(String s) {
		return (s == null || s.isEmpty() || s.trim().isEmpty()) ? null : s.trim();
	}
	
	private String emptyIfNull(String s) {
		return s == null ? "" : s;
	}
	
	private Long asLong(String s) {
		if (s == null) {
			return null;
		}
		
		try {
			return Long.valueOf(s);
		} catch (Throwable ex) {
			return null;
		}
	}
	
	private Class<?> asClass(String s) {
		if (s == null) {
			return null;
		}
		
		try {
			return Class.forName(s);
		} catch (Throwable ex) {
			return null;
		}
	}
	
	private BillerProduct parse(String id, String value) {
		String[] values;
		
		try {
			values = parser.parseLine(value);
		} catch (Throwable ex) {
			values = null;
		}
		
		if ((values == null) || values.length < 9) {
			return null;
		}
		
		String label = nullIfEmpty(values[0]);
		String description = nullIfEmpty(values[1]);
		String billerId = nullIfEmpty(values[2]);
		String productId = nullIfEmpty(values[3]);
		String currency = nullIfEmpty(values[4]);
		Long amount = asLong(nullIfEmpty(values[5]));
		// skip values[6] as used only by brand mobiliser
		String promptLabel = nullIfEmpty(values[7]);
		if ((promptLabel != null) && promptLabel.endsWith(":")) {
			promptLabel = nullIfEmpty(promptLabel.substring(0, promptLabel.length() - 1));
		}
		Class<?> gotoPage = asClass(nullIfEmpty(values[8]));
		
		String strBiller = nullIfEmpty(values[9]);
				
		String parentId = id.substring(0, id.lastIndexOf('.'));
		if ((parentId == null) || !parentId.contains(".")) {
			parentId = null;
		}
		
		String language = id.substring(0, id.indexOf("."));
		if ((language == null)) {
			language = null;
		}
		
		return new BillerProduct(
				language,
				parentId,
				id,
				label,
				description,
				billerId,
				productId,
				currency,
				amount,
				promptLabel,
				gotoPage,
				strBiller);
	}
	
	private void refresh() {
		Map<String, String> raw = lookupMapUtility.getLookupEntriesMap("billerProducts");
		
		boolean reloadNeeded = false;
		
		if (this.raw == null) {
			this.raw = raw;
			reloadNeeded = true;
		} else if (!this.raw.equals(raw)) {
			this.raw = raw;
			reloadNeeded = true;
		}
		
		if (!reloadNeeded) {
			return;
		}
		
		products.clear();
		hcv.clear();
		
		for (Map.Entry<String, String> re : raw.entrySet()) {
			String id = re.getKey();
			BillerProduct value = parse(id, re.getValue());
			
			products.put(id, value);
			
			productByBillerIdAndProductId.put(emptyIfNull(value.language.toLowerCase()).concat("|").concat(emptyIfNull(value.billerId)).concat("|").concat(emptyIfNull(value.productId)), value);
			
			String parentId = id.substring(0, id.lastIndexOf('.'));
			
			List<CodeValue> lcv = hcv.get(parentId);
			if (lcv == null) {
				lcv = new ArrayList<CodeValue>();
				hcv.put(parentId, lcv);
			}
			
			lcv.add(new CodeValue(id,  value.getLabel()));
		}
	}
	
	public synchronized BillerProduct get(String id) {
		refresh();
		return products.get(id);
	}
	
	public synchronized List<CodeValue> getChildrenAsCodeValue(String id) {
		refresh();
		return hcv.get(id);
	}
	
	public synchronized BillerProduct get(String language, String billerId, String productId) {
		refresh();
		return productByBillerIdAndProductId.get(emptyIfNull(language).concat("|").concat(emptyIfNull(billerId)).concat("|").concat(emptyIfNull(productId)));
	}
	
	public static class BillerProduct implements Serializable {
		
		private static final long serialVersionUID = 1L;

		private final String language;
		
		private final String parentId;
		
		private final String id;
		
		private final String label;
		
		private final String description;
		
		private final String billerId;
		
		private final String productId;
		
		private final String currencyCode;
		
		private final Long amount;
		
		private final String promptLabel;

		private final Class<?> gotoPage;

		private final String strBiller;
		
		public BillerProduct(String language, String parentId, String id,
				String label, String description, String billerId,
				String productId, String currencyCode, Long amount,
				String promptLabel, Class<?> gotoPage, String strBiller) {
			
			this.language = language;
			this.parentId = parentId;
			this.id = id;
			this.label = label;
			this.description = description;
			this.billerId = billerId;
			this.productId = productId;
			this.currencyCode = currencyCode;
			this.amount = amount;
			this.promptLabel = promptLabel;
			this.gotoPage = gotoPage;
			this.strBiller = strBiller;
		}
		
		public String getLanguage(){
			return language;
		}
		
		public String getParentId() {
			return parentId;
		}

		public String getId() {
			return id;
		}
		
		public String getLabel() {
			return label;
		}

		public String getDescription() {
			return description;
		}

		public String getBillerId() {
			return billerId;
		}

		public String getProductId() {
			return productId;
		}

		public String getCurrencyCode() {
			return currencyCode;
		}

		public Long getAmount() {
			return amount;
		}

		public String getPromptLabel() {
			return promptLabel;
		}

		public Class<?> getGotoPage() {
			return gotoPage;
		}
		
		public String getStrBiller() {
			return strBiller;
		}

	}
}
