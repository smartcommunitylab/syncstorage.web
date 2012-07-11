package eu.trentorise.smartcampus.presentation.storage.sync;

import eu.trentorise.smartcampus.presentation.data.SyncData;
import eu.trentorise.smartcampus.presentation.storage.BasicObjectStorage;

public interface BasicObjectSyncStorage extends BasicObjectStorage {

	SyncData getSyncData(long since, String user);
	void cleanSyncData(SyncData data, String user);
}
