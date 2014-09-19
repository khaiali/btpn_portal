package com.btpnwow.portal.common.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.wicket.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.sybase365.mobiliser.util.tools.wicketutils.menu.MenuEntry;

public class MenuBuilder implements InitializingBean, ResourceLoaderAware {
	
	private static final Logger LOG = LoggerFactory.getLogger(MenuBuilder.class);

	private Map<String, Resource> rawMenuList;
	
	private ResourceLoader resourceLoader;
	
	private Map<String, List<MenuEntry>> builtMenuList;
	
	public void setRawMenuList(Map<String, Resource> rawMenuList) {
		this.rawMenuList = rawMenuList;
	}

	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}
	
	@SuppressWarnings("unchecked")
	private List<MenuEntry> loadMenuList(ClassLoader cl, Resource url) {
		List<MenuEntry> menus = new ArrayList<MenuEntry>();
		
		BufferedReader reader = null;
		
		String delimiter = Pattern.quote("|");
		
		try {
			reader = new BufferedReader(new InputStreamReader(url.getInputStream(), "UTF-8"));
			
			String line;
			
			while ((line = reader.readLine()) != null) {
				String[] values = line.split(delimiter);
				
				if (values.length < 4) {
					continue;
				}
				
				Class<?> clazz = cl.loadClass(values[2].trim());
				
				try {
					clazz = cl.loadClass(values[2].trim());
				} catch (Throwable ex) {
					LOG.info("An exception was thrown while loading class " + values[2].trim(), ex);
					
					continue;
				}
				
				if (!Page.class.isAssignableFrom(clazz)) {
					LOG.info("Class " + clazz + " is not a wicket Page class");
					continue;
				}
				
				MenuEntry entry = new MenuEntry(values[0].trim(), values[1].trim(), (Class<Page>) clazz);
				entry.setActive(Boolean.parseBoolean(values[3].trim()));
				
				menus.add(entry);
			}
		} catch (Throwable ex) {
			LOG.error("An exception was thrown while loading menus from " + url, ex);
			
			menus = Collections.emptyList();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Throwable ex) {
					// do nothing
				}
			}
		}
		
		return menus;
	}

	public void afterPropertiesSet() throws Exception {
		builtMenuList = new HashMap<String, List<MenuEntry>>();
		
		if ((rawMenuList != null) && !rawMenuList.isEmpty()) {
			
			ClassLoader cl = resourceLoader.getClassLoader();
			
			for (Map.Entry<String, Resource> e : rawMenuList.entrySet()) {
				builtMenuList.put(e.getKey(), Collections.unmodifiableList(loadMenuList(cl, e.getValue())));
			}
		}
	}

	public List<MenuEntry> getMenuList(String category) {
		return builtMenuList.get(category);
	}
}
