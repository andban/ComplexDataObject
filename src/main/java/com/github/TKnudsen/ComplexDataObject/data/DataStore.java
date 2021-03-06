package com.github.TKnudsen.ComplexDataObject.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import com.github.TKnudsen.ComplexDataObject.data.interfaces.IDObject;
import com.github.TKnudsen.ComplexDataObject.data.interfaces.IKeyValueProvider;
import com.github.TKnudsen.ComplexDataObject.data.interfaces.IMasterProvider;
import com.github.TKnudsen.ComplexDataObject.data.interfaces.ISelfDescription;

/**
 * <p>
 * Title: DataStore
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012-2016
 * </p>
 * 
 * @author Juergen Bernard
 */
public class DataStore<T extends IDObject> implements IDObject, ISelfDescription, Iterable<T> {

	private long ID;
	private String name;
	protected int hash = -1;

	/**
	 * Map<Long, T> dataMap: quick access by objectID
	 */
	private transient Map<Long, T> dataMap = new HashMap<Long, T>();

	/**
	 * List<T> dataList: access the data via an List. Consistent dataMap
	 * representation
	 */
	private transient List<T> dataList = new ArrayList<>();

	/**
	 * 
	 */
	private Set<String> attributes = new TreeSet<>();

	public DataStore(List<T> data) {
		this.ID = getRandomLong();
		if (data != null && data.size() > 0)
			this.name = "Data Store for Objects of Type: " + data.get(0).getClass();
		else
			return;

		// initialize data
		for (T dataObject : data) {
			dataList.add(dataObject);
			dataMap.put(dataObject.getID(), dataObject);
		}

		initializeAttributes();
	}

	public DataStore(Map<Long, T> data) {
		this.ID = getRandomLong();
		if (data != null && data.size() > 0)
			this.name = "Data Store for Objects of Type: " + data.values().iterator().next().getClass();
		else
			return;

		// initialize data
		for (Long l : data.keySet()) {
			dataMap.put(l, data.get(l));
			dataList.add(data.get(l));
		}

		initializeAttributes();
	}

	/**
	 * Adds a List of objects to the data store
	 * 
	 * @param objects
	 */
	public void add(List<T> objects) {
		for (T t : objects)
			add(t);
	}

	/**
	 * Adds an object to the data store.
	 * 
	 * @param dobj
	 * @return
	 */
	public boolean add(T dataObject) {
		if (dataMap.containsKey(dataObject.getID()))
			return false;

		dataMap.put(dataObject.getID(), dataObject);
		if (!dataList.contains(dataObject))
			dataList.add(dataObject);

		addAttributes(dataObject);

		return true;
	}

	@Override
	public int hashCode() {
		if (hash == -1) {

			hash = 19;

			for (Long i : dataMap.keySet())
				hash += 31 * hash + (int) dataMap.get(i).hashCode();
		}
		return hash;
	}

	/**
	 * Get all data objects that have an equal name. Compares the name to the
	 * getName method when an object of the store is an instance of
	 * ITextDescription, else the toString value.
	 * 
	 * @param name
	 * @return
	 */
	public List<T> getDataByName(String name) {
		List<T> ret = new ArrayList<T>();

		for (T dataObject : dataList) {
			if (dataObject instanceof ISelfDescription)
				if (((ISelfDescription) dataObject).getName().equals(name))
					ret.add(dataObject);
				else if (dataObject.toString().equals(name))
					ret.add(dataObject);
		}

		return ret;
	}

	@Override
	public long getID() {
		return ID;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<Long, T> getDataMap() {
		return dataMap;
	}

	public List<T> getDataList() {
		return dataList;
	}

	public List<T> getDataByMaster(T master) {
		List<T> data = new ArrayList<>();
		for (T e : dataList)
			if (e instanceof IMasterProvider)
				if (((IMasterProvider) e).getMaster() != null)
					if (((IMasterProvider) e).getMaster().equals(master))
						data.add(e);
		return data;
	}

	/**
	 * Adds new attributes of a given object.
	 */
	private void addAttributes(T dataObject) {
		if (attributes == null)
			initializeAttributes();

		if (dataObject instanceof IKeyValueProvider) {
			IKeyValueProvider<?> keyValueStore = (IKeyValueProvider<?>) dataObject;
			Map<String, Class<?>> types = keyValueStore.getTypes();
			for (String s : types.keySet())
				attributes.add(s);
		}
	}

	/**
	 * Initializes the attribute keys. Use addAttributes(T dataObject) when
	 * adding new 0bjects.
	 */
	private void initializeAttributes() {
		if (attributes == null)
			attributes = new TreeSet<>();
		for (T dataObject : dataList)
			if (dataObject instanceof IKeyValueProvider) {
				IKeyValueProvider<?> keyValueStore = (IKeyValueProvider<?>) dataObject;
				Map<String, Class<?>> types = keyValueStore.getTypes();
				for (String s : types.keySet())
					attributes.add(s);
			}
	}

	public T getDataByID(long ID) {
		return dataMap.get(ID);
	}

	public Set<String> getAttributes() {
		return attributes;
	}

	public boolean contains(T t) {
		if (dataMap.containsKey(t.getID()))
			return true;
		return false;
	}

	public int size() {
		return dataMap.size();
	}

	@Override
	public String getDescription() {
		return getName();
	}

	@Override
	public Iterator<T> iterator() {
		return dataMap.values().iterator();
	}

	/**
	 * Little helper for the generation of a unique identifier.
	 * 
	 * @return unique ID
	 */
	private long getRandomLong() {
		return UUID.randomUUID().getMostSignificantBits();
	}
}
