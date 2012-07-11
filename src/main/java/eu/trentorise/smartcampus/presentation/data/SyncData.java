package eu.trentorise.smartcampus.presentation.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SyncData {

	private long version;
	private Map<String,List<BasicObject>> updated = new HashMap<String, List<BasicObject>>();
	private Map<String,List<String>> deleted = new HashMap<String, List<String>>();
	
	public SyncData() {
		super();
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public Map<String, List<BasicObject>> getUpdated() {
		return updated;
	}

	public void setUpdated(Map<String, List<BasicObject>> updated) {
		this.updated = updated;
	}

	public Map<String, List<String>> getDeleted() {
		return deleted;
	}

	public void setDeleted(Map<String, List<String>> deleted) {
		this.deleted = deleted;
	}

}
