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

package eu.trentorise.smartcampus.presentation.storage.sync;

import java.util.Map;

import eu.trentorise.smartcampus.presentation.common.exception.DataException;
import eu.trentorise.smartcampus.presentation.data.SyncData;
import eu.trentorise.smartcampus.presentation.storage.BasicObjectStorage;

public interface BasicObjectSyncStorage extends BasicObjectStorage {

	SyncData getSyncData(long since, String user) throws DataException;
	SyncData getSyncData(long since, String user, boolean userDataOnly) throws DataException;

	SyncData getSyncData(long since, String user, Map<String,Object> include, Map<String,Object> exclude) throws DataException;
	SyncData getSyncData(long since, String user, boolean userDataOnly, Map<String,Object> include, Map<String,Object> exclude) throws DataException;

	void cleanSyncData(SyncData data, String user) throws DataException;
}
