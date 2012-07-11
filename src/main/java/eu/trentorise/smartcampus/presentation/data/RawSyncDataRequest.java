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
