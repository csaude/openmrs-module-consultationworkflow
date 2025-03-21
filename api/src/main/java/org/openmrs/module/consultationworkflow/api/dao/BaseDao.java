package org.openmrs.module.consultationworkflow.api.dao;

import org.openmrs.Auditable;
import org.openmrs.OpenmrsObject;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

public interface BaseDao<T extends OpenmrsObject & Auditable> {
	
	Optional<T> get(@NotNull int id);
	
	Optional<T> get(@NotNull String uuid);
	
	T createOrUpdate(T object);
	
	void delete(T object);
	
	void delete(@NotNull String uuid);
	
	List<T> findAll();
	
	List<T> findAll(boolean includeVoided);
}
