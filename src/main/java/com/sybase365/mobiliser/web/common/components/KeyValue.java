package com.sybase365.mobiliser.web.common.components;

import java.io.Serializable;

public class KeyValue<K, V> implements Serializable {

    private static final long serialVersionUID = 1L;

    private K key;
    private V value;

    public KeyValue() {

    }

    public KeyValue(K key, V value) {
	this.key = key;
	this.value = value;
    }

    @Override
    public String toString() {
	return "[Key=" + getKey() + ", Value=" + getValue() + "]";
    }

    public K getKey() {
	return key;
    }

    public void setKey(K key) {
	this.key = key;
    }

    public V getValue() {
	return value;
    }

    public void setValue(V value) {
	this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
	if (obj instanceof KeyValue<?, ?>) {
	    KeyValue<?, ?> kvObj = (KeyValue<?, ?>) obj;
	    if (this.getKey() == null && kvObj.getKey() == null) {
		// if key is equal to null check for value
		if (this.getValue() == null && kvObj.getValue() == null)
		    return true;
		else if (this.getValue() != null && kvObj.getValue() != null
			&& this.getValue().equals(kvObj.getValue()))
		    return true;
		else
		    return false;
	    } else if (this.getKey() != null && kvObj.getKey() != null
		    && this.getKey().equals(kvObj.getKey())) {
		// if key is same check for value
		if (this.getValue() == null && kvObj.getValue() == null)
		    return true;
		else if (this.getValue() != null && kvObj.getValue() != null
			&& this.getValue().equals(kvObj.getValue()))
		    return true;
		else
		    return false;
	    } else
		return false;
	} else
	    return false;
    }

    @Override
    public int hashCode() {
	int hash = 42;
	if (this.key != null)
	    hash = hash ^ this.key.hashCode();
	if (this.value != null)
	    hash = hash ^ this.value.hashCode();
	return hash;
    }
}
