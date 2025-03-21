package org.openmrs.module.consultationworkflow.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.openmrs.module.consultationworkflow.api.impl.PatientAttributesEvaluator;
import org.openmrs.module.consultationworkflow.api.impl.PatientDemographicsEvaluator;
import org.openmrs.module.consultationworkflow.api.impl.ProgramEvaluator;
import org.openmrs.module.consultationworkflow.api.impl.VisitTypeEvaluator;
import org.openmrs.module.consultationworkflow.model.EligibilityCriteria;
import org.openmrs.module.consultationworkflow.model.EligibilityCriteriaType;
import org.openmrs.module.consultationworkflow.util.ExpressionEvaluationHelper;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class EligibilityCriteriaEvaluatorFactoryTest {
	
	private PatientDemographicsEvaluator demographicsEvaluator;
	
	private PatientAttributesEvaluator attributesEvaluator;
	
	private ProgramEvaluator programEvaluator;
	
	private VisitTypeEvaluator visitTypeEvaluator;
	
	@InjectMocks
	private EligibilityCriteriaEvaluatorFactory factory;
	
	private EligibilityCriteria demographicsCriteria;
	
	private EligibilityCriteria attributesCriteria;
	
	private EligibilityCriteria programCriteria;
	
	private EligibilityCriteria visitTypeCriteria;
	
	@BeforeEach
	public void setup() {
		// Initialize test criteria
		demographicsCriteria = new EligibilityCriteria();
		demographicsCriteria.setCriteriaType(EligibilityCriteriaType.PATIENT_DEMOGRAPHICS);
		
		attributesCriteria = new EligibilityCriteria();
		attributesCriteria.setCriteriaType(EligibilityCriteriaType.PATIENT_ATTRIBUTES);
		
		programCriteria = new EligibilityCriteria();
		programCriteria.setCriteriaType(EligibilityCriteriaType.PROGRAM);
		
		visitTypeCriteria = new EligibilityCriteria();
		visitTypeCriteria.setCriteriaType(EligibilityCriteriaType.VISIT_TYPE);
		
		ExpressionEvaluationHelper expressionHelper = new ExpressionEvaluationHelper();
		
		demographicsEvaluator = new PatientDemographicsEvaluator(expressionHelper);
		attributesEvaluator = new PatientAttributesEvaluator(expressionHelper);
		programEvaluator = new ProgramEvaluator();
		visitTypeEvaluator = new VisitTypeEvaluator();
		
		// Set up evaluators list
		List<EligibilityCriteriaEvaluator> evaluators = Arrays.asList(demographicsEvaluator, attributesEvaluator,
		    programEvaluator, visitTypeEvaluator);
		factory.setEvaluators(evaluators);
	}
	
	@Test
	public void getEvaluator_ShouldReturnNullForNullCriteria() {
		// When
		EligibilityCriteriaEvaluator result = factory.getEvaluator(null);
		
		// Then
		assertNull(result);
	}
	
	@Test
	public void getEvaluator_ShouldReturnDemographicsEvaluator() {
		// When
		EligibilityCriteriaEvaluator result = factory.getEvaluator(demographicsCriteria);
		
		// Then
		assertSame(demographicsEvaluator, result);
		assertTrue(result.canHandle(demographicsCriteria));
	}
	
	@Test
	public void getEvaluator_ShouldReturnAttributesEvaluator() {
		// When
		EligibilityCriteriaEvaluator result = factory.getEvaluator(attributesCriteria);
		
		// Then
		assertSame(attributesEvaluator, result);
		assertTrue(result.canHandle(attributesCriteria));
	}
	
	@Test
	public void getEvaluator_ShouldReturnProgramEvaluator() {
		// When
		EligibilityCriteriaEvaluator result = factory.getEvaluator(programCriteria);
		
		// Then
		assertSame(programEvaluator, result);
		assertTrue(result.canHandle(programCriteria));
	}
	
	@Test
	public void getEvaluator_ShouldReturnVisitTypeEvaluator() {
		// When
		EligibilityCriteriaEvaluator result = factory.getEvaluator(visitTypeCriteria);
		
		// Then
		assertSame(visitTypeEvaluator, result);
		assertTrue(result.canHandle(visitTypeCriteria));
	}
	
	@Test
	public void getEvaluator_ShouldReturnNullWhenNoEvaluatorCanHandle() {
		// Given
		EligibilityCriteria unknownCriteria = new EligibilityCriteria();
		unknownCriteria.setCriteriaType(EligibilityCriteriaType.PROVIDER_ROLE);
		
		// When
		EligibilityCriteriaEvaluator result = factory.getEvaluator(unknownCriteria);
		
		// Then
		assertNull(result);
		assertFalse(demographicsEvaluator.canHandle(unknownCriteria));
		assertFalse(attributesEvaluator.canHandle(unknownCriteria));
		assertFalse(programEvaluator.canHandle(unknownCriteria));
		assertFalse(visitTypeEvaluator.canHandle(unknownCriteria));
	}
}
