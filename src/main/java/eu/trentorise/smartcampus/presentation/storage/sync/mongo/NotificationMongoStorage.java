package eu.trentorise.smartcampus.presentation.storage.sync.mongo;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

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

	public void storeAllNotifications(Collection<NotificationObject> notifications) {
		if (notifications != null) {
			for (NotificationObject no : notifications) {
				storeNotification(no);
			}
		}
	}

	public void deleteNotification(NotificationObject notification) throws NotFoundException {
		mongoTemplate.remove(notification);
	}

	public NotificationObject getNotificationById(String id) throws NotFoundException {
		return mongoTemplate.findById(id, NotificationObject.class);
	}

	public List<NotificationObject> getAllNotifications(String user) {
		return searchWithTypeAndUser(null, user, null, NotificationObject.class);
	}

	public List<NotificationObject> searchNotifications(String type, String user, Map<String, Object> criteria) {
		return searchWithTypeAndUser(type, user, criteria, NotificationObject.class);
	}

	private <T> List<T> searchWithTypeAndUser(String type, String user, Map<String, Object> c, Class<T> cls) {
		Criteria criteria = new Criteria();
		if (type != null) {
			criteria.and("type").is(type);
		}
		if (user != null) {
			criteria.and("user").is(user);
		}
		if (c != null) {
			for (String key : c.keySet()) {
				criteria.and("content."+key).is(c.get(key));
			}
		}
		return mongoTemplate.find(Query.query(criteria), cls);
	}

	
}
