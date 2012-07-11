package eu.trentorise.smartcampus.presentation.storage;


import java.util.Collection;
import java.util.List;
import java.util.Map;

import eu.trentorise.smartcampus.presentation.common.exception.NotFoundException;
import eu.trentorise.smartcampus.presentation.data.BasicObject;

public interface BasicObjectStorage {

	public <T extends BasicObject> void storeObject(T object);

	public <T extends BasicObject> void storeAllObjects(Collection<T> objects);

	public <T extends BasicObject> void updateObject(T object) throws NotFoundException;

	public <T extends BasicObject> void deleteObject(T object) throws NotFoundException;

	public void deleteObjectById(String id);

	public List<? extends BasicObject> getAllObjects();
	public List<? extends BasicObject> getAllObjects(String user);

	public <T extends BasicObject> T getObjectById(String id, Class<T> cls) throws NotFoundException;
	public BasicObject getObjectById(String id) throws NotFoundException;

	public <T extends BasicObject> List<T>  getObjectsByType(Class<T> cls);
	public <T extends BasicObject> List<T> getObjectsByType(Class<T> cls, String user);

	public <T extends BasicObject> List<T> searchObjects(Class<T> cls, Map<String, Object> criteria); 
	public <T extends BasicObject> List<T> searchObjects(Class<T> cls, Map<String, Object> criteria, String user); 

}
