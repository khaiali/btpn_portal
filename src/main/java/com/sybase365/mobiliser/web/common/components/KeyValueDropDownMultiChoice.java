package com.sybase365.mobiliser.web.common.components;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.model.IModel;

public class KeyValueDropDownMultiChoice<K, V> extends ListMultipleChoice<K> {

    private static final long serialVersionUID = 1L;

    private final Map<K, V> kvChoices = new HashMap<K, V>();

    public KeyValueDropDownMultiChoice(final String id,
	    IModel<Collection<K>> model, final List<KeyValue<K, V>> keyValues,
	    final SortProperties sortProperties) {

	super(id);

	if (model != null) {
	    super.setModel(model);
	}
	super.setChoices(getKeys(keyValues, sortProperties));
	super.setChoiceRenderer(new IChoiceRenderer<K>() {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public Object getDisplayValue(K object) {
		return kvChoices.get(object);
	    }

	    @Override
	    public String getIdValue(K object, int index) {
		return String.valueOf(object);
	    }
	});

    }

    public KeyValueDropDownMultiChoice(final String id,
	    final List<KeyValue<K, V>> keyValues) {

	this(id, null, keyValues, null);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<K> getKeys(final List<KeyValue<K, V>> keyValues,
	    final SortProperties sortProperties) {
	if (keyValues == null || keyValues.isEmpty()) {
	    // instead of returning null we return an empty list to prevent a
	    // NPE while choices are beeing rendered
	    return new ArrayList<K>();
	}

	// clean the choices map
	kvChoices.clear();

	List<K> keyList = new ArrayList<K>();
	if (sortProperties != null) {

	    Collections.sort(keyValues, new Comparator<KeyValue>() {

		@Override
		public int compare(KeyValue o1, KeyValue o2) {
		    if (!(o1.getValue() instanceof Comparable)
			    || !(o2.getValue() instanceof Comparable)) {
			return 0;
		    }

		    if (!sortProperties.isSortKeys()) {
			if (o1.getValue() == null && o2.getValue() == null) {
			    return 0;
			} else if (o1.getValue() != null
				&& o2.getValue() == null) {
			    return sortProperties.isSortAscending() ? 1 : -1;
			} else if (o1.getValue() == null
				&& o2.getValue() != null) {
			    return sortProperties.isSortAscending() ? -1 : 1;
			}
		    }

		    int result = sortProperties.isSortKeys() ? ((Comparable) o1
			    .getKey()).compareTo((Comparable) o2.getKey())
			    : ((Comparable) o1.getValue())
				    .compareTo((Comparable) o2.getValue());

		    return sortProperties.isSortAscending() ? result : (-1)
			    * result;
		}
	    });

	}

	// add the (sorted) key value items to the choices map
	for (int i = 0; i < keyValues.size(); i++) {
	    KeyValue<K, V> current = keyValues.get(i);
	    kvChoices.put(current.getKey(), current.getValue());
	    keyList.add(current.getKey());
	}

	return keyList;
    }

    public static class SortProperties implements Serializable {
	private static final long serialVersionUID = 1L;

	private boolean sortKeys;
	private boolean sortAscending;

	public SortProperties() {
	    this(true, true);
	}

	public SortProperties(boolean sortKeys, boolean sortAscending) {
	    this.sortKeys = sortKeys;
	    this.sortAscending = sortAscending;
	}

	public boolean isSortKeys() {
	    return sortKeys;
	}

	public void setSortKeys(boolean sortKeys) {
	    this.sortKeys = sortKeys;
	}

	public boolean isSortAscending() {
	    return sortAscending;
	}

	public void setSortAscending(boolean sortAscending) {
	    this.sortAscending = sortAscending;
	}

    }
}
