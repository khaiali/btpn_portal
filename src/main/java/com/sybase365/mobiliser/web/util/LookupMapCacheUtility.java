package com.sybase365.mobiliser.web.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.Localizer;
import org.apache.wicket.resource.Properties;
import org.apache.wicket.util.value.ValueMap;
import org.springframework.beans.factory.InitializingBean;

import com.googlecode.ehcache.annotations.Cacheable;
import com.sybase365.mobiliser.money.contract.v5_0.system.GetLookupsRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.GetLookupsResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.LookupEntity;
import com.sybase365.mobiliser.money.services.api.ISystemEndpoint;
import com.sybase365.mobiliser.util.tools.wicketutils.components.BasePage;
import com.sybase365.mobiliser.util.tools.wicketutils.resourceloader.ILookupMapUtility;
import com.sybase365.mobiliser.util.tools.wicketutils.resourceloader.LookupResourceLoader;

public class LookupMapCacheUtility implements ILookupMapUtility, InitializingBean {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(LookupMapCacheUtility.class);

	public ISystemEndpoint wsSystemConfClient;

	@Override
	public void afterPropertiesSet() throws Exception {

		if (wsSystemConfClient == null) {
			throw new IllegalStateException("wsSystemConfClient is required");
		}
	}

	@Override
	@Cacheable(cacheName = "lookupNameMaps")
	public Map<String, String> getLookupNamesMap(String lookupMapName) {
		return getLookupNamesMap(lookupMapName, null);
	}

	@Override
	@Cacheable(cacheName = "lookupNameMaps")
	public Map<String, String> getLookupNamesMap(String lookupMapName, Component component) {

		Map<String, String> lookupMap = null;

		try {
			if (LOG.isTraceEnabled()) {
				LOG.trace("Loading lookup names from service");
			}

			GetLookupsResponse response = getLookups(lookupMapName);
			if (response.getStatus().getCode() != 0) {
				Properties props = component.getApplication().getResourceSettings().getPropertiesFactory()
					.load(BasePage.class, BasePage.class.getName().replace('.', '/') + '.');

				if (PortalUtils.exists(props)) {
					ValueMap map = props.getAll();
					if (PortalUtils.exists(map)) {
						lookupMap = new HashMap<String, String>();
						Set<String> keySet = map.keySet();
						for (String key : keySet) {
							if (key.startsWith(LookupResourceLoader.LOOKUP_INDICATOR + lookupMapName + ".")) {
								lookupMap.put(cutLookupIndicator(key),
									component.getLocalizer().getString(key, component));
							}
						}
					}
				}
			} else {
				lookupMap = new HashMap<String, String>();
				for (LookupEntity le : response.getLookupEntities()) {
					lookupMap.put(lookupMapName + "." + le.getId(), le.getName());
				}
			}
			if (LOG.isTraceEnabled()) {
				LOG.trace("Loaded " + lookupMap.size() + " lookup name entries for \"" + lookupMapName + "\"");
			}

		} catch (Exception e) {
			if (LOG.isWarnEnabled()) {
				LOG.warn("Unable to compile lookup values", e);
			}
		}

		return lookupMap;
	}

	@Override
	@Cacheable(cacheName = "lookupMaps")
	public Map<String, String> getLookupEntriesMap(String lookupMapName) {
		return getLookupEntriesMap(lookupMapName, null, null);
	}

	@Override
	@Cacheable(cacheName = "lookupMaps")
	public Map<java.lang.String, java.lang.String> getLookupEntriesMap(java.lang.String lookupMapName,
		Localizer localizer, Component component) {

		Map<String, String> lookupMap = null;

		try {
			if (LOG.isTraceEnabled()) {
				LOG.trace("Loading String lookup values from service");
			}

			GetLookupsResponse response = getLookups(lookupMapName);
			if (response.getStatus().getCode() != 0) {

				Properties props = component.getApplication().getResourceSettings().getPropertiesFactory()
					.load(BasePage.class, BasePage.class.getName().replace('.', '/') + '.');

				if (PortalUtils.exists(props)) {
					ValueMap map = props.getAll();
					if (PortalUtils.exists(map)) {
						lookupMap = new HashMap<String, String>();
						Set<String> keySet = map.keySet();
						for (String key : keySet) {
							if (key.startsWith(LookupResourceLoader.LOOKUP_INDICATOR + lookupMapName + ".")) {
								lookupMap.put(cutLookupIndicatorAndLookupName(key, lookupMapName), component
									.getLocalizer().getString(key, component));
							}
						}
					}
				}
			} else {
				lookupMap = new HashMap<String, String>();
				for (LookupEntity le : response.getLookupEntities()) {

					String name = le.getName();
					String lookupString = LookupResourceLoader.LOOKUP_INDICATOR + lookupMapName + "." + le.getId();

					// see if we have to localize the entry
					if (localizer != null && component != null) {
						String localizedName = localizer.getString(lookupString, component);
						if (!StringUtils.isEmpty(localizedName) && !lookupString.equals(localizedName)) {
							name = localizedName;
						}
					}

					lookupMap.put(le.getId(), name);
				}
			}
			if (LOG.isTraceEnabled()) {
				LOG.trace("Loaded " + lookupMap.size() + " String lookup entries for \"" + lookupMapName + "\"");
			}

		} catch (Exception e) {
			if (LOG.isWarnEnabled()) {
				LOG.warn("Unable to compile lookup entries", e);
			}
		}

		return lookupMap;
	}

	private String cutLookupIndicatorAndLookupName(String key, String lookupName) {
		String lookupString = LookupResourceLoader.LOOKUP_INDICATOR + lookupName + ".";
		return key.substring(key.indexOf(lookupString) + lookupString.length());
	}

	private String cutLookupIndicator(String key) {
		return key.substring(key.indexOf(LookupResourceLoader.LOOKUP_INDICATOR)
				+ LookupResourceLoader.LOOKUP_INDICATOR.length());
	}

	private GetLookupsResponse getLookups(String lookupName) throws Exception {

		GetLookupsRequest request = GetLookupsRequest.class.newInstance();
		request.setCallback(null);
		request.setConversationId(UUID.randomUUID().toString());
		request.setOrigin("mobiliser-web");
		request.setRepeat(Boolean.FALSE);
		request.setTraceNo(UUID.randomUUID().toString());
		request.setEntityName(lookupName);

		return getWsSystemConfClient().getLookups(request);
	}

	protected ISystemEndpoint getWsSystemConfClient() {
		return wsSystemConfClient;
	}

	public void setWsSystemConfClient(ISystemEndpoint wsSystemConfClient) {
		this.wsSystemConfClient = wsSystemConfClient;
	}

}
