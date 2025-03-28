/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.consultationworkflow.api.dao.impl;

import org.hibernate.SessionFactory;
import org.openmrs.api.APIException;
import org.openmrs.module.consultationworkflow.api.dao.WorkflowDataDao;
import org.openmrs.module.consultationworkflow.api.dao.search.WorkflowDataSearchCriteria;
import org.openmrs.module.consultationworkflow.model.WorkflowData;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Repository
public class WorkflowDataDaoImpl extends BaseDaoImpl<WorkflowData> implements WorkflowDataDao {
	
	public WorkflowDataDaoImpl(SessionFactory sessionFactory) {
		super(sessionFactory);
	}
	
	@Override
	public List<WorkflowData> getWorkflowDataByCriteria(WorkflowDataSearchCriteria searchCriteria) throws APIException {
		CriteriaBuilder builder = getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<WorkflowData> query = builder.createQuery(WorkflowData.class);
		Root<WorkflowData> root = query.from(WorkflowData.class);
		query.select(root);
		
		if (searchCriteria.getPatient() != null) {
			query.where(builder.equal(root.get("patient"), searchCriteria.getPatient()));
		}
		
		return getCurrentSession().createQuery(query).getResultList();
	}
}
