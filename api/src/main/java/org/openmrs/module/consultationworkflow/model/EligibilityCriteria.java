package org.openmrs.module.consultationworkflow.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openmrs.BaseChangeableOpenmrsData;

import javax.persistence.*;

@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "consultationworkflow_eligibility_criteria")
public class EligibilityCriteria extends BaseChangeableOpenmrsData {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer eligibilityCriteriaId;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private EligibilityCriteriaType criteriaType;
	
	@Column(nullable = false, length = 1000)
	private String condition;
	
	@ManyToOne
	@JoinColumn(name = "workflow_config_id", nullable = false)
	private WorkflowConfig workflowConfig;
	
	@Override
	public Integer getId() {
		return getEligibilityCriteriaId();
	}
	
	@Override
	public void setId(Integer id) {
		setEligibilityCriteriaId(id);
	}
	
}
