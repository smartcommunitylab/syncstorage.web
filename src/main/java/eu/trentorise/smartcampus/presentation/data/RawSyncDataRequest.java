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

package eu.trentorise.smartcampus.presentation.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RawSyncDataRequest {

	private long version;
	private Map<String,List<Object>> updated = new HashMap<String, List<Object>>();
	private Map<String,List<String>> deleted = new HashMap<String, List<String>>();

	private long since;
	
	public RawSyncDataRequest(long version, Map<String,List<Object>> updated, Map<String,List<String>> deleted, long since) {
		super();
		this.version = version;
		this.updated = updated;
		this.deleted = deleted;
		this.since = since;
	}

	
	public long getVersion() {
		return version;
	}


	public Map<String, List<Object>> getUpdated() {
		return updated;
	}


	public Map<String, List<String>> getDeleted() {
		return deleted;
	}

	public long getSince() {
		return since;
	}
	
	
	
}
