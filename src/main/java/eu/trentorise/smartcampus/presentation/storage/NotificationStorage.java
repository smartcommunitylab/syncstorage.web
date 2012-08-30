package eu.trentorise.smartcampus.presentation.storage;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import eu.trentorise.smartcampus.presentation.common.exception.DataException;
import eu.trentorise.smartcampus.presentation.common.exception.NotFoundException;
import eu.trentorise.smartcampus.presentation.data.NotificationObject;

public interface NotificationStorage {

	public void storeNotification(NotificationObject notification)
			throws DataException;

	public void storeAllNotifications(
			Collection<NotificationObject> notifications) throws DataException;

	public void deleteNotification(NotificationObject notification)
			throws DataException;

	public void updateNotification(NotificationObject notification)
			throws DataException;

	public NotificationObject getNotificationById(String id)
			throws NotFoundException, DataException;

	// for compatibility
	public List<NotificationObject> getAllNotifications(String user)
			throws DataException;

	public List<NotificationObject> getAllNotifications(String user,
			Long since, Integer position, Integer count) throws DataException;

	// for compatibility
	public List<NotificationObject> searchNotifications(String type,
			String user, Map<String, Object> criteria) throws DataException;

	public List<NotificationObject> searchNotifications(String type,
			String user, Map<String, Object> criteria, Long since,
			Integer position, Integer count) throws DataException;

	public List<NotificationObject> searchNotificationsByMetadata(String type,
			String user, Map<String, Object> criteria, Long since,
			Integer position, Integer count) throws DataException;
}