package eu.trentorise.smartcampus.presentation.storage.sync.mongo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;

import eu.trentorise.smartcampus.presentation.common.exception.DataException;
import eu.trentorise.smartcampus.presentation.common.exception.NotFoundException;
import eu.trentorise.smartcampus.presentation.common.util.Util;
import eu.trentorise.smartcampus.presentation.data.BasicObject;
import eu.trentorise.smartcampus.presentation.data.SyncData;
import eu.trentorise.smartcampus.presentation.storage.sync.BasicObjectSyncStorage;

public abstract class GenericObjectSyncMongoStorage<S extends SyncObjectBean> implements BasicObjectSyncStorage {

	private MongoOperations mongoTemplate = null;
	
	private static Long version = 0L;
	
	public GenericObjectSyncMongoStorage(MongoOperations mongoTemplate) {
		super();
		this.mongoTemplate = mongoTemplate;
		version = initVersion(mongoTemplate, getObjectClass());
	}

	public abstract Class<S> getObjectClass();

	private static synchronized long getVersion() {
		return version++;
	}
	
	private static synchronized long initVersion(MongoOperations mongoTemplate, Class<?> cls) {
		DBCursor cursor = mongoTemplate.getCollection(mongoTemplate.getCollectionName(cls))
		.find()
		.sort(new BasicDBObject("version", -1)).limit(1);
		if (cursor.hasNext()) return (Long)cursor.next().get("version");
		return 0;
	}

	public <T extends BasicObject> void storeObject(T object) throws DataException {
		try {
			storeObject(object, getVersion());
		} catch (Exception e) {
			throw new DataException("Failed to store data", e);
		}
	}

	private <T extends BasicObject> void storeObject(T object, long version) throws InstantiationException, IllegalAccessException {
		object.setVersion(version);
		if (object.getId() == null) {
			object.setId(new ObjectId().toString());
		}
		S sob = convertToObjectBean(object);
		mongoTemplate.save(sob);
	}

	protected <T extends BasicObject> S convertToObjectBean(T object) throws InstantiationException, IllegalAccessException {
		return Util.convertToObjectBean(object, getObjectClass());
	}

	protected BasicObject convertBeanToBasicObject(S object, Class<? extends BasicObject> cls) {
		return Util.convertBeanToBasicObject(object, cls);
	}
	
	public <T extends BasicObject> void storeAllObjects(Collection<T> objects) throws DataException {
		if (objects != null) {
			for (BasicObject ob : objects) {
				storeObject(ob);
			}
		}
	}

	public <T extends BasicObject> void updateObject(T object) throws NotFoundException, DataException {
		storeObject(object);
	}
	
	public <T extends BasicObject> void deleteObject(T object) throws DataException {
		object.setVersion(getVersion());
		S sob;
		try {
			sob = convertToObjectBean(object);
		} catch (Exception e) {
			throw new DataException("Failed to store data object", e);
		}
		sob.setDeleted(true);
		mongoTemplate.save(sob);
	}

	public void deleteObjectById(String id) throws DataException{
		deleteObjectById(id, getVersion());
	}
	
	private void deleteObjectById(String id, long version) {
		mongoTemplate.findAndModify(Query.query(Criteria.where("id").is(id)),new Update().set("deleted", true).set("version", version), getObjectClass());
	}

	public List<BasicObject> getAllObjects() throws DataException{
		List<S> list = searchWithType(null, null, null, getObjectClass(), null, false, false);
		return convert(list);
	}

	@SuppressWarnings("unchecked")
	private List<BasicObject> convert(List<S> list) {
		if (list != null && ! list.isEmpty()) {
			List<BasicObject> result = new ArrayList<BasicObject>();
			for (S sob : list)
				try {
					result.add(
						convertBeanToBasicObject(sob, 
								(Class<? extends BasicObject>)Thread.currentThread().getContextClassLoader().loadClass(sob.getType())));
				} catch (ClassNotFoundException e) {
					continue;
				}
			return result;
		}
		return Collections.emptyList();
	}

	public <T extends BasicObject> T getObjectById(String id, Class<T> cls) throws NotFoundException, DataException {
		S bean = mongoTemplate.findById(id, getObjectClass());
		if (bean != null) return Util.convertBeanToBasicObject(bean,cls);
		throw new NotFoundException(id);
	}

	@SuppressWarnings("unchecked")
	public BasicObject getObjectById(String id) throws NotFoundException, DataException {
		List<S> list = searchWithType(id, null, null, getObjectClass(), null, false, false);
		if (list != null && list.isEmpty()) {
			try {
				Class<? extends BasicObject> cls = (Class<? extends BasicObject>)Thread.currentThread().getContextClassLoader().loadClass(list.get(0).getType());
				return Util.convertBeanToBasicObject(list.get(0),cls);
			} catch (ClassNotFoundException e) {
				return null;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T extends BasicObject> List<T> getObjectsByType(Class<T> cls) throws DataException{
		List<S> list = searchWithType(null, cls.getCanonicalName(), null, getObjectClass(), null, false, false);
		return (List<T>)convert(list);
	}

	@SuppressWarnings("unchecked")
	public <T extends BasicObject> List<T> getObjectsByType(Class<T> cls, String user) throws DataException{
		List<S> list = searchWithType(null, cls.getCanonicalName(), null, getObjectClass(), user, false, true);
		return (List<T>)convert(list);
	}

	@SuppressWarnings("unchecked")
	public <T extends BasicObject> List<T> searchObjects(Class<T> cls, Map<String, Object> criteria) throws DataException{
		List<S> list = searchWithType(null, cls.getCanonicalName(), criteria, getObjectClass(), null, false, false);
		return (List<T>)convert(list);
	}

	@SuppressWarnings("unchecked")
	public <T extends BasicObject> List<T> searchObjects(Class<T> cls, Map<String, Object> criteria, String user) throws DataException {
		List<S> list = searchWithType(null, cls.getCanonicalName(), criteria, getObjectClass(), user, false, true);
		return (List<T>)convert(list);
	}

	public List<BasicObject> getAllObjects(String user) throws DataException{
		List<S> list = searchWithType(null, null, null, getObjectClass(), user, false, true);
		return convert(list);
	}

	@SuppressWarnings("unchecked")
	public SyncData getSyncData(long since, String user) throws DataException {
		long newVersion = getVersion();
		SyncData syncData = new SyncData();
		syncData.setVersion(newVersion);
		List<S> list = searchWithVersion(user, since, newVersion);
		if (list != null && !list.isEmpty()) {
			Map<String,List<BasicObject>> updated = new HashMap<String, List<BasicObject>>();
			Map<String,List<String>> deleted = new HashMap<String, List<String>>();
			for (S sob : list) {
				if (sob.isDeleted()) {
					List<String> deletedList = deleted.get(sob.getType());
					if (deletedList == null) {
						deletedList = new ArrayList<String>();
						deleted.put(sob.getType(), deletedList);
					}
					deletedList.add(sob.getId());
				} else {
					List<BasicObject> updatedList = updated.get(sob.getType());
					if (updatedList == null) {
						updatedList = new ArrayList<BasicObject>();
						updated.put(sob.getType(), updatedList);
					}
					try {
						updatedList.add(Util.convertBeanToBasicObject(sob, (Class<? extends BasicObject>)Thread.currentThread().getContextClassLoader().loadClass(sob.getType())));
					} catch (ClassNotFoundException e) {
						continue;
					}
					
				}
			}
			syncData.setDeleted(deleted);
			syncData.setUpdated(updated);
		}
		return syncData;
	}

	public void cleanSyncData(SyncData data, String user) throws DataException {
		long oldVersion = data.getVersion();
		if (data.getDeleted() != null) {
			for (String key : data.getDeleted().keySet()) {
				for (String id : data.getDeleted().get(key)) {
					deleteObjectById(id, oldVersion);
				}
			}
		}
		if (data.getUpdated() != null) {
			for (String key : data.getUpdated().keySet()) {
				for (BasicObject o : data.getUpdated().get(key)) {
					try {
						storeObject(o, oldVersion);
					} catch (Exception e) {
						throw new DataException("Failed to sync data", e);
					}
				}
			}
		}
	}

	private <T> List<T> searchWithType(String id, String type, Map<String, Object> c, Class<T> cls, String user, boolean all, boolean withUser) {
		Criteria criteria = new Criteria();
		if (type != null) {
			criteria.and("type").is(type);
		}
		if (all) {
			criteria.and("deleted").is(false);
		}
		if (user != null) {
			criteria.and("user").in(user, null);
		} else if (withUser) {
			criteria.and("user").is(user);
		}
		
		if (c != null) {
			for (String key : c.keySet()) {
				criteria.and("content."+key).is(c.get(key));
			}
		}
		return mongoTemplate.find(Query.query(criteria), cls);
	}

	private List<S> searchWithVersion(String user, long fromVersion, long toVersion) {
		Criteria criteria = new Criteria();
		if (user != null) {
			criteria.and("user").in(user, null);
		} else {
			criteria.and("user").is(user);
		}
		criteria.and("version").gt(fromVersion).lt(toVersion); 
		
		return mongoTemplate.find(Query.query(criteria), getObjectClass());
	}

	public static void main(String[] args) {
		System.err.println(new Update().set("deleted", true).set("version", 1).getUpdateObject());
	}
}
