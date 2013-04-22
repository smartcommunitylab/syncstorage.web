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
package eu.trentorise.smartcampus.presentation.test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.Mongo;

import eu.trentorise.smartcampus.presentation.common.exception.DataException;
import eu.trentorise.smartcampus.presentation.data.SyncData;
import eu.trentorise.smartcampus.presentation.storage.sync.BasicObjectSyncStorage;
import eu.trentorise.smartcampus.presentation.storage.sync.mongo.BasicObjectSyncMongoStorage;
import eu.trentorise.smartcampus.presentation.storage.sync.mongo.SyncObjectBean;

public class SyncStorageTest {

	private static final String TEST_DB = "testdb";
	private BasicObjectSyncStorage storage = null;
	private MongoOperations template = null;
	
	@Before
	public void setUp() throws Exception {
		template = new MongoTemplate(new Mongo(), TEST_DB);
		storage = new BasicObjectSyncMongoStorage(template);
		template.dropCollection(SyncObjectBean.class);
	}

	private void prepareData() throws DataException {
		TestBean bean = new TestBean();
		Map<String,Object> content = new HashMap<String, Object>();
		content.put("key", "value1");
		bean.setContent(content);
		storage.storeObject(bean);
		
		content = new HashMap<String, Object>();
		content.put("key", "value2");
		bean = new TestBean();
		bean.setContent(content);
		storage.storeObject(bean);
		
		content = new HashMap<String, Object>();
		content.put("key", "value3");
		bean = new TestBean();
		bean.setContent(content);
		storage.storeObject(bean);
	}
	
	@Test
	public void testSync() throws DataException {
		prepareData();
		SyncData syncData =  storage.getSyncData(0, null, false);
		assertTrue(syncData != null && 
				   syncData.getUpdated() != null &&
				   syncData.getUpdated().size() == 1 &&
				   syncData.getUpdated().get(TestBean.class.getCanonicalName()) != null &&
				   syncData.getUpdated().get(TestBean.class.getCanonicalName()).size() == 3);
	}
	
	@Test
	public void testSyncExclude() throws DataException {
		prepareData();
		Map<String,Object> exclude = new HashMap<String, Object>();
		exclude.put("content.key", "value1");
		SyncData syncData =  storage.getSyncData(0, null, false, null, exclude);
		assertTrue(syncData != null && 
				   syncData.getUpdated() != null &&
				   syncData.getUpdated().size() == 1 &&
				   syncData.getUpdated().get(TestBean.class.getCanonicalName()) != null &&
				   syncData.getUpdated().get(TestBean.class.getCanonicalName()).size() == 2);
	}
	
	@Test
	public void testSyncInclude() throws DataException {
		prepareData();
		Map<String,Object> include = new HashMap<String, Object>();
//		include.put("content.key", Collections.singletonMap("$nin", new String[]{"value1","value2"}));
		include.put("content.key", Arrays.asList("value1","value2"));
		SyncData syncData =  storage.getSyncData(0, null, false, include, null);
		assertTrue(syncData != null && 
				   syncData.getUpdated() != null &&
				   syncData.getUpdated().size() == 1 &&
				   syncData.getUpdated().get(TestBean.class.getCanonicalName()) != null &&
				   syncData.getUpdated().get(TestBean.class.getCanonicalName()).size() == 2);
	}
}
