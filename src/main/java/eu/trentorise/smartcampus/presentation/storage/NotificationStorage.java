package eu.trentorise.smartcampus.presentation.storage;


import java.util.Collection;
import java.util.List;
import java.util.Map;

import eu.trentorise.smartcampus.presentation.common.exception.NotFoundException;
import eu.trentorise.smartcampus.presentation.data.NotificationObject;

public interface NotificationStorage {

	public void storeNotification(NotificationObject notification);
	public void storeAllNotifications(Collection<NotificationObject> notifications);
	public void deleteNotification(NotificationObject notification) throws NotFoundException;

	public NotificationObject getNotificationById(String id) throws NotFoundException;
	public List<NotificationObject> getAllNotifications(String user);
	public List<NotificationObject> searchNotifications(String type, String user, Map<String, Object> criteria); 

}