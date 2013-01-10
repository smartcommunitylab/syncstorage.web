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
	public List<NotificationObject> getAllNotifications(String user,
			boolean onlyUnreaded) throws DataException;

	public List<NotificationObject> getAllNotifications(String user,
			Long since, Integer position, Integer count, boolean onlyUnreaded)
			throws DataException;

	// for compatibility
	public List<NotificationObject> searchNotifications(String type,
			String user, Map<String, Object> criteria, boolean onlyUnreaded)
			throws DataException;

	public List<NotificationObject> searchNotifications(String type,
			String user, Map<String, Object> criteria, Long since,
			Integer position, Integer count, boolean onlyUnreaded)
			throws DataException;

	public List<NotificationObject> searchNotificationsByMetadata(String type,
			String user, Map<String, Object> criteria, Long since,
			Integer position, Integer count, boolean onlyUnreaded)
			throws DataException;
}