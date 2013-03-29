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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import eu.trentorise.smartcampus.presentation.common.exception.DataException;
import eu.trentorise.smartcampus.presentation.common.exception.NotFoundException;
import eu.trentorise.smartcampus.presentation.data.NotificationObject;
import eu.trentorise.smartcampus.presentation.storage.NotificationStorage;

public class NotificationMongoStorage implements NotificationStorage {

	private MongoOperations mongoTemplate = null;

	public NotificationMongoStorage(MongoOperations mongoTemplate) {
		super();
		this.mongoTemplate = mongoTemplate;
	}

	public void storeNotification(NotificationObject notification) {
		mongoTemplate.save(notification);
	}

	public void storeAllNotifications(
			Collection<NotificationObject> notifications) {
		if (notifications != null) {
			for (NotificationObject no : notifications) {
				storeNotification(no);
			}
		}
	}

	public void deleteNotification(NotificationObject notification) {
		mongoTemplate.remove(notification);
	}

	public NotificationObject getNotificationById(String id)
			throws NotFoundException {
		NotificationObject result = mongoTemplate.findById(id,
				NotificationObject.class);
		if (result == null) {
			throw new NotFoundException();
		} else {
			markReaded(result);
			return result;
		}
	}

	public List<NotificationObject> getAllNotifications(String user,
			Long since, Integer position, Integer count, boolean onlyUnreaded) {
		List<NotificationObject> result = searchWithTypeAndUser(null, user,
				null, null, since, position, count, NotificationObject.class,
				onlyUnreaded);
		markReaded(result);
		return result;
	}

	public List<NotificationObject> searchNotifications(String type,
			String user, Map<String, Object> criteria, Long since,
			Integer position, Integer count, boolean onlyUnreaded) {
		List<NotificationObject> result = searchWithTypeAndUser(type, user,
				criteria, null, since, null, null, NotificationObject.class,
				onlyUnreaded);
		markReaded(result);
		return result;
	}

	private <T extends NotificationObject> List<T> searchWithTypeAndUser(
			String type, String user, Map<String, Object> c,
			Map<String, Object> metadata, Long since, Integer position,
			Integer count, Class<T> cls, boolean onlyUnreaded) {
		Criteria criteria = new Criteria();
		if (onlyUnreaded) {
			criteria.and("readed").is(false);
		}
		if (type != null) {
			criteria.and("type").is(type);
		}
		if (user != null) {
			criteria.and("user").is(user);
		}
		if (c != null) {
			for (String key : c.keySet()) {
				criteria.and("content." + key).is(c.get(key));
			}
		}

		if (since != null) {
			criteria.and("timestamp").gte(since);
		}

		if (metadata != null) {
			for (String key : metadata.keySet()) {
				criteria.and(key).is(metadata.get(key));
			}
		}

		Query query = Query.query(criteria);
		if (position != null && position > 0) {
			query.skip(position);
		}

		if (count != null && count > position) {
			query.limit(count);
		}

		List<T> result = mongoTemplate.find(query, cls);
		markReaded(result);
		return result;
	}

	@Override
	public List<NotificationObject> searchNotificationsByMetadata(String type,
			String user, Map<String, Object> criteria, Long since,
			Integer position, Integer count, boolean onlyUnreaded)
			throws DataException {
		return searchWithTypeAndUser(type, user, null, criteria, since,
				position, count, NotificationObject.class, onlyUnreaded);
	}

	@Override
	public void updateNotification(NotificationObject notification)
			throws DataException {
		mongoTemplate.save(notification);

	}

	@Override
	public List<NotificationObject> getAllNotifications(String user,
			boolean onlyUnreaded) throws DataException {
		return searchWithTypeAndUser(null, user, null, null, null, null, null,
				NotificationObject.class, onlyUnreaded);
	}

	@Override
	public List<NotificationObject> searchNotifications(String type,
			String user, Map<String, Object> criteria, boolean onlyUnreaded)
			throws DataException {
		return searchWithTypeAndUser(type, user, criteria, null, null, null,
				null, NotificationObject.class, onlyUnreaded);
	}

	private <T extends NotificationObject> void markReaded(T object) {
		object.setReaded(true);
		mongoTemplate.save(object);
	}

	private <T extends NotificationObject> void markReaded(List<T> objects) {
		for (T obj : objects) {
			markReaded(obj);
		}
	}

}
