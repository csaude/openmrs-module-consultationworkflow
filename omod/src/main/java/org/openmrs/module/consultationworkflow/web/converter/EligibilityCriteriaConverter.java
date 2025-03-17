package org.openmrs.module.consultationworkflow.web.converter;

import org.openmrs.annotation.Handler;
import org.openmrs.module.consultationworkflow.model.EligibilityCriteria;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingConverter;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;

@Handler(supports = EligibilityCriteria.class)
public class EligibilityCriteriaConverter extends BaseDelegatingConverter<EligibilityCriteria> {
	
	@Override
	public EligibilityCriteria newInstance(String type) {
		return new EligibilityCriteria();
	}
	
	@Override
	public EligibilityCriteria getByUniqueId(String string) {
		return null;
	}
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("criteriaType");
		description.addProperty("condition");
		return description;
	}
	
}
