package com.sybase365.mobiliser.web.common.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.util.WildcardListModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.sybase365.mobiliser.util.tools.wicketutils.resourceloader.ILookupMapUtility;
import com.sybase365.mobiliser.util.tools.wicketutils.resourceloader.LookupResourceLoader;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This class allows to auto-populate a dropdown choice box intended to display
 * "lookup" key - value pairs. In order to do so the items which are to be
 * filled in need to fulfill the following list of requirements in order to find
 * a matching display name
 * <ul>
 * <li>Either have an entry in a *.xml properties file which maps the lookup
 * items value to a localized string. This requires the resource key to have the
 * following format <b>lookup.[lookupName].[lookupValue]</b> (e.g.
 * lookup.usecases.196)</li>
 * <li>There exists a lookup query for those items in the database, which can be
 * addressed by the reource loader. This requires that the lookup name is also
 * the primary key for the lookup query (e.g. usecases) in the according
 * database table</li>
 * <li>If there is no lookup query in the database, then the key value pairs
 * must be added in <b>BasePage.xml</b> in above format</li>
 * <li>In case data in the drop down needs to be restricted, then the key list
 * should be passed in the constructor</li>
 * </ul>
 * 
 * @author sschweit
 * 
 * @param <K>
 *            the class of the objects used for the "value" attribute of the
 *            rendered drop down
 */
public class LocalizableLookupDropDownChoice<K> extends DropDownChoice<K> {

    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(LocalizableLookupDropDownChoice.class);

    @SpringBean(name = "lookupMapUtilitiesImpl")
    public ILookupMapUtility lookupMapUtility;

    private String lookupName;
    private Class<K> dropDownValueClass;
    private Boolean sortKeys;
    private boolean sortAscending;
    private List<K> keyList;

    /**
     * In order to use this constructor the provided
     * <code>dropDownValueClass</code> must provide a constructor taking a
     * single <code>String</code> object in order to be able to create a new
     * instance of that class
     * 
     * @param id
     *            the wicket ID of this component
     * @param dropDownValueClass
     *            the class of the objects used to fill the "value" attribute of
     *            this component
     * @param lookupName
     *            the name of the lookups you are using, which will be used to
     *            compose the resource key used to localize the display name for
     *            each choice item
     * @param lookupMapUtility
     *            an instance of the <code>ILookupMapUtility</code> interface
     * @param component
     *            the surrounding component
     * @param sortKeys
     *            defines if the drop down choices should be sorted
     *            <ul>
     *            <li><code>null</code> => No sorting</li>
     *            <li><code>true</code> => Items are sorted by their values</li>
     *            <li><code>false</code> => Items are sorted by their names</li>
     *            </ul>
     * @param sortAscending
     *            defines the sort order in case <code>sortKeys</code> has been
     *            set to a value != <code>null</code>
     * @param keyList
     *            the subset of keys in DB or xml file. If not null, only these
     *            key-value pair will be shown in drop down
     */
    public LocalizableLookupDropDownChoice(final String id,
	    final Class<K> dropDownValueClass, final String lookupName,
	    final Component component, final Boolean sortKeys,
	    final boolean sortAscending, List<K> keyList) {

	super(id);

	this.lookupName = lookupName;
	this.dropDownValueClass = dropDownValueClass;
	this.sortKeys = sortKeys;
	this.sortAscending = sortAscending;
	this.keyList = keyList;

	// NOTE: adding the choices is posponed until render time

	// define a custom ChoiceRenderer to get the display value for our
	// lookup IDs
	setChoiceRenderer(new IChoiceRenderer<K>() {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public Object getDisplayValue(K object) {

		// this way we do not neccessarily need to override
		// localizeDisplayValues()
		// this also allows the drop down box to display the correct
		// localized value in case it was changed "on the fly" in a
		// properties file
		// TODO if not found we end up with lookup.countries.X
		return getLocalizer().getString(
			LookupResourceLoader.LOOKUP_INDICATOR + lookupName
				+ "." + object, component);
	    }

	    @Override
	    public String getIdValue(K object, int index) {
		return object == null ? null : object.toString();
	    }
	});
    }

    public LocalizableLookupDropDownChoice(final String id,
	    final Class<K> dropDownValueClass, final String lookupName,
	    final Component component) {
	this(id, dropDownValueClass, lookupName, component, null, false, null);
    }

    public LocalizableLookupDropDownChoice(final String id,
	    final Class<K> dropDownValueClass, final String lookupName,
	    final Component component, final Boolean sortKeys,
	    final boolean sortAscending) {
	this(id, dropDownValueClass, lookupName, component, sortKeys,
		sortAscending, null);
    }

    @Override
    protected void onBeforeRender() {

	// this is the place where we can determine the order of the choices
	getChoices().clear();

	// get the choices for our drop down list
	try {
	    setChoices(new WildcardListModel<K>(getChoiceList(
		    this.dropDownValueClass, this.lookupName,
		    this.lookupMapUtility, this, this.sortKeys,
		    this.sortAscending, this.keyList)));
	} catch (Exception e) {
	    // not much we can do here but logg the exception
	    LOG.warn("Could not retrieve drop down choices", e);
	}

	super.onBeforeRender();
    }

    protected List<K> getChoiceList(Class<K> dropDownValueClass,
	    String lookupName, ILookupMapUtility lookupMapUtility,
	    final Component component, final Boolean sortKeys,
	    final boolean sortAscending, List<K> keys) throws Exception {
	// Creating a copy of key list so that the original this.keylist object
	// is not modified by wicket
	List<K> keyList = new ArrayList<K>();
	if (PortalUtils.exists(keys)) {
	    for (K key : keys)
		keyList.add(key);
	}
	final Map<String, String> lookupEntries = lookupMapUtility
		.getLookupEntriesMap(lookupName, getLocalizer(), component);
	if (lookupEntries != null && !lookupEntries.isEmpty()) {
	    if (!PortalUtils.exists(keyList)) {
		for (Entry<String, String> entry : lookupEntries.entrySet()) {
		    keyList.add(dropDownValueClass.getConstructor(String.class)
			    .newInstance(entry.getKey()));
		}
	    }
	    if (sortKeys != null && sortKeys.booleanValue()) {
		Collections.sort(keyList, new Comparator<K>() {

		    @Override
		    @SuppressWarnings({ "rawtypes", "unchecked" })
		    public int compare(K o1, K o2) {
			if (!(o1 instanceof Comparable)
				|| !(o2 instanceof Comparable)) {
			    return 0;
			}

			int result = ((Comparable) o1)
				.compareTo((Comparable) o2);

			return sortAscending ? result : (-1) * result;
		    }
		});
	    } else if (sortKeys != null && !sortKeys.booleanValue()) {
		Collections.sort(keyList, new Comparator<K>() {

		    @Override
		    public int compare(K o1, K o2) {
			if (!(o1 instanceof Comparable)
				|| !(o2 instanceof Comparable)) {
			    return 0;
			}

			// to successfully lookup the map entry for the given
			// objects we have to consider their string value or
			// else we'll have a miss
			String name1 = lookupEntries.get(String.valueOf(o1));
			String name2 = lookupEntries.get(String.valueOf(o2));
			if (name1 == null) {
			    if (name2 == null) {
				return 0;
			    } else {
				return sortAscending ? -1 : 1;
			    }
			} else if (name2 == null) {
			    return sortAscending ? 1 : -1;
			} else {
			    int result = name1.compareTo(name2);
			    return sortAscending ? result : (-1) * result;
			}
		    }
		});
	    }
	}

	return keyList;
    }

}
