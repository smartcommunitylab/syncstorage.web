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
import eu.trentorise.smartcampus.presentation.data.BasicObject;

public interface BasicObjectStorage {

	public <T extends BasicObject> void storeObject(T object) throws DataException;

	public <T extends BasicObject> void storeAllObjects(Collection<T> objects)throws DataException;

	public <T extends BasicObject> void updateObject(T object) throws NotFoundException, DataException;

	public <T extends BasicObject> void deleteObject(T object) throws DataException;

	public void deleteObjectById(String id)throws DataException;

	public List<? extends BasicObject> getAllObjects() throws DataException;
	public List<? extends BasicObject> getAllObjects(String user) throws DataException;

	public <T extends BasicObject> T getObjectById(String id, Class<T> cls) throws NotFoundException, DataException;
	public BasicObject getObjectById(String id) throws NotFoundException, DataException;

	public <T extends BasicObject> List<T>  getObjectsByType(Class<T> cls) throws DataException;
	public <T extends BasicObject> List<T> getObjectsByType(Class<T> cls, String user) throws DataException;

	public <T extends BasicObject> List<T> searchObjects(Class<T> cls, Map<String, Object> criteria) throws DataException; 
	public <T extends BasicObject> List<T> searchObjects(Class<T> cls, Map<String, Object> criteria, String user) throws DataException; 

}
