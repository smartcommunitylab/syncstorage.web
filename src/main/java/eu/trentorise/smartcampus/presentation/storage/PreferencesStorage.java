package eu.trentorise.smartcampus.presentation.storage;

import java.util.Collection;
import java.util.List;

import eu.trentorise.smartcampus.presentation.common.exception.DataException;
import eu.trentorise.smartcampus.presentation.common.exception.NotFoundException;
import eu.trentorise.smartcampus.presentation.data.PreferenceObject;

public interface PreferencesStorage {

	public void store(PreferenceObject preference) throws DataException;

	public void storeAll(Collection<PreferenceObject> preferences)
			throws DataException;

	public void delete(PreferenceObject preference) throws DataException;

	public void update(PreferenceObject preference) throws DataException;

	public PreferenceObject getById(String id) throws NotFoundException,
			DataException;

	public List<PreferenceObject> getByLabel(String user, String labelName)
			throws DataException;

	public List<PreferenceObject> getAll(String user) throws DataException;

}
