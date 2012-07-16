package eu.trentorise.smartcampus.presentation.storage.sync.mongo;

import org.springframework.data.mongodb.core.MongoOperations;

public class BasicObjectSyncMongoStorage extends GenericObjectSyncMongoStorage<SyncObjectBean> {

	public BasicObjectSyncMongoStorage(MongoOperations mongoTemplate) {
		super(mongoTemplate);
	}

	@Override
	public Class<SyncObjectBean> getObjectClass() {
		return SyncObjectBean.class;
	}

}
