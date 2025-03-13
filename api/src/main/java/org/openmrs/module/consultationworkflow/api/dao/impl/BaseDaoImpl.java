package org.openmrs.module.consultationworkflow.api.dao.impl;

import liquibase.pro.packaged.Q;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.openmrs.Auditable;
import org.openmrs.OpenmrsObject;
import org.openmrs.Retireable;
import org.openmrs.Voidable;
import org.openmrs.module.consultationworkflow.api.dao.BaseDao;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;

import static org.hibernate.criterion.Restrictions.eq;

@Slf4j
@Getter
@Setter(AccessLevel.MODULE)
@SuppressWarnings("unchecked")
public class BaseDaoImpl<T extends OpenmrsObject & Auditable> implements BaseDao<T> {
	
	private final SessionFactory sessionFactory;
	
	private final Class<Q> clazz;
	
	public BaseDaoImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		this.clazz = (Class<Q>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}
	
	protected Session getCurrentSession() {
		return this.getSessionFactory().getCurrentSession();
	}
	
	@Override
	public Optional<T> get(int id) {
		return Optional.ofNullable((T) getCurrentSession().get(this.clazz, id));
	}
	
	@Override
	public Optional<T> get(String uuid) {
		Criteria criteria = getCurrentSession().createCriteria(getClazz());
		includeVoidedObjects(criteria, false);
		criteria.add(eq("uuid", uuid)).uniqueResult();
		return Optional.ofNullable((T) criteria.add(eq("uuid", uuid)).uniqueResult());
	}
	
	@Override
	public T createOrUpdate(T object) {
		return null;
	}
	
	@Override
	public void delete(T object) {
		this.getCurrentSession().delete(object);
	}
	
	@Override
    public void delete(String uuid) {
        this.get(uuid).ifPresent(this::delete);
    }
	
	@Override
	public List<T> findAll() {
		return this.findAll(false);
	}
	
	@Override
	public List<T> findAll(boolean includeVoided) {
		Criteria criteria = getCurrentSession().createCriteria(clazz);
		includeVoidedObjects(criteria, includeVoided);
		return criteria.list();
	}
	
	protected void includeVoidedObjects(Criteria criteria, boolean includeRetired) {
		if (!includeRetired) {
			if (isVoidable()) {
				handleVoidable(criteria);
			} else if (isRetireable()) {
				handleRetireable(criteria);
			}
		}
	}
	
	protected boolean isVoidable() {
		return Voidable.class.isAssignableFrom(clazz);
	}
	
	protected boolean isRetireable() {
		return Retireable.class.isAssignableFrom(clazz);
	}
	
	protected void handleVoidable(Criteria criteria) {
		criteria.add(eq("voided", false));
	}
	
	protected void handleRetireable(Criteria criteria) {
		criteria.add(eq("retired", false));
	}
}
