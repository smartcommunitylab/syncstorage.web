package eu.trentorise.smartcampus.presentation.data;

public class SyncDataRequest {

	private SyncData syncData;
	private long since;
	
	public SyncDataRequest(SyncData syncData, long since) {
		super();
		this.syncData = syncData;
		this.since = since;
	}

	public SyncData getSyncData() {
		return syncData;
	}

	public long getSince() {
		return since;
	}
	
	
	
}
