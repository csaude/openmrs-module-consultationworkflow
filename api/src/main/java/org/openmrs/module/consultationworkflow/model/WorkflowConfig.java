package org.openmrs.module.consultationworkflow.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.openmrs.BaseChangeableOpenmrsData;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "consultationworkflow_workflow_config")
public class WorkflowConfig extends BaseChangeableOpenmrsData {
	
	@OneToMany(mappedBy = "workflowConfig", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	Set<EligibilityCriteria> criteria;
	
	@Id
	@Column(name = "workflow_config_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer workflowConfigId;
	
	@Column(nullable = false)
	private String name;
	
	@Column(nullable = false, length = 1000)
	private String description;
	
	private Boolean published = false;
	
	@Column(nullable = false)
	private String version;
	
	@Column
	private String resourceValueReference;
	
	@Override
	public Integer getId() {
		return getWorkflowConfigId();
	}
	
	@Override
	public void setId(Integer id) {
		setWorkflowConfigId(id);
	}
}
