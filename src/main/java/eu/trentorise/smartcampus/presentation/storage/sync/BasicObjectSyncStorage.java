package eu.trentorise.smartcampus.presentation.storage.sync;

import eu.trentorise.smartcampus.presentation.common.exception.DataException;
import eu.trentorise.smartcampus.presentation.data.SyncData;
import eu.trentorise.smartcampus.presentation.storage.BasicObjectStorage;

public interface BasicObjectSyncStorage extends BasicObjectStorage {

	SyncData getSyncData(long since, String user) throws DataException;
	void cleanSyncData(SyncData data, String user) throws DataException;
}
