package eu.trentorise.smartcampus.presentation.storage.sync.mongo;

import java.util.Collection;
import java.util.List;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import eu.trentorise.smartcampus.presentation.common.exception.DataException;
import eu.trentorise.smartcampus.presentation.common.exception.NotFoundException;
import eu.trentorise.smartcampus.presentation.data.PreferenceObject;
import eu.trentorise.smartcampus.presentation.storage.PreferencesStorage;

public class PreferenceMongoStorage implements PreferencesStorage {

	private MongoOperations mongoTemplate = null;

	public PreferenceMongoStorage(MongoOperations mongoTemplate) {
		super();
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public void store(PreferenceObject preference) throws DataException {
		mongoTemplate.save(preference);

	}

	@Override
	public void storeAll(Collection<PreferenceObject> preferences)
			throws DataException {
		try {
			for (PreferenceObject o : preferences) {
				mongoTemplate.save(o);
			}
		} catch (NullPointerException e) {
			throw new DataException();
		}

	}

	@Override
	public void delete(PreferenceObject preference) throws DataException {
		mongoTemplate.remove(preference);

	}

	@Override
	public void update(PreferenceObject preference) throws DataException {
		mongoTemplate.save(preference);

	}

	@Override
	public PreferenceObject getById(String id) throws NotFoundException,
			DataException {
		PreferenceObject result = mongoTemplate.findById(id,
				PreferenceObject.class);
		if (result == null) {
			throw new NotFoundException();
		} else {
			return result;
		}
	}

	@Override
	public List<PreferenceObject> getByLabel(String user, String labelName)
			throws DataException {
		if (user == null || user.isEmpty() || labelName == null
				|| labelName.isEmpty()) {
			throw new DataException();
		}

		Criteria criteria = new Criteria();
		criteria.and("user").is(user);
		criteria.and("name").is(labelName);
		return mongoTemplate.find(new Query(criteria), PreferenceObject.class);
	}

	@Override
	public List<PreferenceObject> getAll(String user) throws DataException {
		if (user == null || user.isEmpty()) {
			throw new DataException();
		}

		Criteria criteria = new Criteria();
		criteria.and("user").is(user);
		return mongoTemplate.find(new Query(criteria), PreferenceObject.class);
	}

}
