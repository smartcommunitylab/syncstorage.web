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
			return result;
		}
	}

	public List<NotificationObject> getAllNotifications(String user,
			Long since, Integer position, Integer count) {
		return searchWithTypeAndUser(null, user, null, null, since, position,
				count, NotificationObject.class);
	}

	public List<NotificationObject> searchNotifications(String type,
			String user, Map<String, Object> criteria, Long since,
			Integer position, Integer count) {
		return searchWithTypeAndUser(type, user, criteria, null, since, null,
				null, NotificationObject.class);
	}

	private <T> List<T> searchWithTypeAndUser(String type, String user,
			Map<String, Object> c, Map<String, Object> metadata, Long since,
			Integer position, Integer count, Class<T> cls) {
		Criteria criteria = new Criteria();
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

		return mongoTemplate.find(query, cls);
	}

	@Override
	public List<NotificationObject> searchNotificationsByMetadata(String type,
			String user, Map<String, Object> criteria, Long since,
			Integer position, Integer count) throws DataException {
		return searchWithTypeAndUser(type, user, null, criteria, since,
				position, count, NotificationObject.class);
	}

	@Override
	public void updateNotification(NotificationObject notification)
			throws DataException {
		mongoTemplate.save(notification);

	}

	@Override
	public List<NotificationObject> getAllNotifications(String user)
			throws DataException {
		return searchWithTypeAndUser(null, user, null, null, null, null, null,
				NotificationObject.class);
	}

	@Override
	public List<NotificationObject> searchNotifications(String type,
			String user, Map<String, Object> criteria) throws DataException {
		return searchWithTypeAndUser(type, user, criteria, null, null, null,
				null, NotificationObject.class);
	}

}
