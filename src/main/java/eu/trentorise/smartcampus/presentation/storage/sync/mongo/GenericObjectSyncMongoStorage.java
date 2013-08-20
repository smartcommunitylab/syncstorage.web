/**
 *    Copyright 2012-2013 Trento RISE
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package eu.trentorise.smartcampus.presentation.storage.sync.mongo;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

import eu.trentorise.smartcampus.presentation.common.exception.DataException;
import eu.trentorise.smartcampus.presentation.common.exception.NotFoundException;
import eu.trentorise.smartcampus.presentation.common.util.Util;
import eu.trentorise.smartcampus.presentation.data.BasicObject;
import eu.trentorise.smartcampus.presentation.data.SyncData;
import eu.trentorise.smartcampus.presentation.storage.sync.BasicObjectSyncStorage;

public abstract class GenericObjectSyncMongoStorage<S extends SyncObjectBean> implements BasicObjectSyncStorage {

	protected MongoOperations mongoTemplate = null;
	
	public GenericObjectSyncMongoStorage(MongoOperations mongoTemplate) {
		super();
		this.mongoTemplate = mongoTemplate;
		initVersion();
	}

	public abstract Class<S> getObjectClass();

	private final Query versionQuery = Query.query(Criteria.where("_id").is(getObjectClass().getCanonicalName()));
	private final Update versionUpdate = new Update().inc("value", 1);
	
	private long getVersion() {
		DBObject o = mongoTemplate.findAndModify(versionQuery, versionUpdate, DBObject.class, "counters");
		return (Long)o.get("value");
	}
	
	private synchronized void initVersion() {
		String counterId = getObjectClass().getCanonicalName();
		DBObject counter = mongoTemplate.findOne(Query.query(Criteria.where("_id").is(counterId)), DBObject.class, "counters");
		if (counter == null) {
			mongoTemplate.save(BasicDBObjectBuilder.start("_id", counterId).add("value", 1L).get(), "counters");
		}
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
		if (object.getUpdateTime() <= 0) object.setUpdateTime(System.currentTimeMillis());
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
//		List<S> list = searchWithType(null, null, null, getObjectClass(), null, false, false);
//		return convert(list);
		Criteria criteria = createSearchWithTypeCriteria(null, null, null, null, false, false);
		return find(Query.query(criteria), BasicObject.class);
	}

	@SuppressWarnings("unchecked")
	private <T extends BasicObject> List<T> convert(List<S> list, Class<T> cls) {
		if (list != null && ! list.isEmpty()) {
			List<T> result = new ArrayList<T>();
			for (S sob : list)
				try {
					result.add((T)
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
		if (bean != null && !bean.isDeleted()) return Util.convertBeanToBasicObject(bean,cls);
		throw new NotFoundException(id);
	}

	@SuppressWarnings("unchecked")
	public BasicObject getObjectById(String id) throws NotFoundException, DataException {
		S bo = mongoTemplate.findById(id, getObjectClass()); 
		if (bo == null || bo.isDeleted()) throw new NotFoundException();
		try {
			Class<? extends BasicObject> cls = (Class<? extends BasicObject>)Thread.currentThread().getContextClassLoader().loadClass(bo.getType());
			return Util.convertBeanToBasicObject(bo,cls);
		} catch (ClassNotFoundException e) {
			throw new DataException(e);
		}
	}

	public <T extends BasicObject> List<T> getObjectsByType(Class<T> cls) throws DataException{
//		List<S> list = searchWithType(null, cls.getCanonicalName(), null, getObjectClass(), null, false, false);
//		return (List<T>)convert(list);
		Criteria criteria = createSearchWithTypeCriteria(null, cls.getCanonicalName(), null, null, false, false);
		return find(Query.query(criteria), cls);
	}

	public <T extends BasicObject> List<T> getObjectsByType(Class<T> cls, String user) throws DataException{
		Criteria criteria = createSearchWithTypeCriteria(null, cls.getCanonicalName(), null, user, false, true);
		return find(Query.query(criteria), cls);
	}

	public <T extends BasicObject> List<T> searchObjects(Class<T> cls, Map<String, Object> criteriaMap) throws DataException{
		Criteria criteria = createSearchWithTypeCriteria(null, cls.getCanonicalName(), criteriaMap, null, false, false);
		return find(Query.query(criteria), cls);
	}

	public <T extends BasicObject> List<T> searchObjects(Class<T> cls, Map<String, Object> criteriaMap, String user) throws DataException {
		Criteria criteria = createSearchWithTypeCriteria(null, cls.getCanonicalName(), criteriaMap, user, false, true);
		return find(Query.query(criteria), cls);
	}
	
	protected <T extends BasicObject> List<T> find(Query query, Class<T> cls) {
		List<S> result = mongoTemplate.find(query, getObjectClass()); 
		return (List<T>)convert(result, cls);
	}

	public List<BasicObject> getAllObjects(String user) throws DataException{
		Criteria criteria = createSearchWithTypeCriteria(null, null, null, user, false, true);
		return find(Query.query(criteria), BasicObject.class);
	}

	@Override
	public SyncData getSyncData(long since, String user, boolean userDataOnly) throws DataException {
		return retrieveSyncData(since, user, userDataOnly, null, null);
	}

	public SyncData getSyncData(long since, String user) throws DataException {
		return retrieveSyncData(since, user, false, null, null);
	}
	
	@Override
	public SyncData getSyncData(long since, String user, Map<String, Object> include, Map<String, Object> exclude) throws DataException {
		return retrieveSyncData(since, user, false, include, exclude);
	}

	@Override
	public SyncData getSyncData(long since, String user, boolean userDataOnly, Map<String, Object> include, Map<String, Object> exclude) throws DataException {
		return retrieveSyncData(since, user, userDataOnly, include, exclude);
	}

	@SuppressWarnings("unchecked")
	private SyncData retrieveSyncData(long since, String user, boolean userDataOnly, Map<String, Object> include, Map<String, Object> exclude) {
		long newVersion = getVersion();
		SyncData syncData = new SyncData();
		syncData.setVersion(newVersion);
		List<S> list = searchWithVersion(user, since, newVersion, userDataOnly, include, exclude);
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
						BasicObject b = Util.convertBeanToBasicObject(sob, (Class<? extends BasicObject>)Thread.currentThread().getContextClassLoader().loadClass(sob.getType()));
						updatedList.add(b);
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

	private Criteria createSearchWithTypeCriteria(String id, String type, Map<String, Object> c, String user, boolean all, boolean withUser) {
		Criteria criteria = new Criteria();
		if (type != null) {
			criteria.and("type").is(type);
		}
		if (!all) {
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
		return criteria;
	}

	private List<S> searchWithVersion(String user, long fromVersion, long toVersion, boolean userDataOnly, Map<String, Object> include, Map<String, Object> exclude) {
		Criteria criteria = new Criteria();
		if (user != null && !userDataOnly) {
			criteria.and("user").in(user, null);
		} else {
			criteria.and("user").is(user);
		}
		criteria.and("version").gt(fromVersion).lt(toVersion); 
		if (include != null && !include.isEmpty()) {
			for (String key : include.keySet()) {
				Object value = include.get(key);
				if (value instanceof Collection) {
					criteria.and("content."+key).in((Collection<?>)value);
				} else {
					criteria.and("content."+key).is(value);
				}
			}
		}
		if (exclude != null && !exclude.isEmpty()) {
			for (String key : exclude.keySet()) {
				Object value = exclude.get(key);
				if (value instanceof Collection) {
					criteria.and("content."+key).nin((Collection<?>)value);
				} else {
					criteria.and("content."+key).ne(value);
				}
			}
		}
		
		return mongoTemplate.find(Query.query(criteria), getObjectClass());
	}

	public static void main(String[] args) throws UnknownHostException, MongoException {

		MongoTemplate mongoTemplate = new MongoTemplate(new Mongo(),"discovertrento"); 
		
		String counterId = "discovertrento";
		DBObject counter = mongoTemplate.findOne(Query.query(Criteria.where("_id").is(counterId)), DBObject.class, "counters");
		if (counter == null) {
			mongoTemplate.save(BasicDBObjectBuilder.start("_id", counterId).add("value", 1L).get(), "counters");
		}
		
		DBObject o = mongoTemplate.findAndModify(
				Query.query(Criteria.where("_id").is(counterId)), 
				new Update().inc("value", 1), 
				DBObject.class, 
				"counters");
		o = mongoTemplate.findAndModify(
				Query.query(Criteria.where("_id").is(counterId)), 
				new Update().inc("value", 1), 
				DBObject.class, 
				"counters");

		System.err.println((Long)o.get("value"));
	}
}
