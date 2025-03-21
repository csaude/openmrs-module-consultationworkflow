package org.openmrs.module.consultationworkflow.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.openmrs.BaseChangeableOpenmrsData;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "consultationworkflow_eligibility_criteria")
public class EligibilityCriteria extends BaseChangeableOpenmrsData {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "eligibility_criteria_id")
	private Integer eligibilityCriteriaId;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "criteria_type", nullable = false)
	private EligibilityCriteriaType criteriaType;
	
	// Quoting condition as it is a reserved word in mysql
	@Column(name = "\"condition\"", nullable = false, length = 1000)
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
