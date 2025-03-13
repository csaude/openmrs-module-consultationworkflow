package org.openmrs.module.consultationworkflow.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openmrs.BaseChangeableOpenmrsData;

import javax.persistence.*;
import java.util.Set;

@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "consultationworkflow_workflow_config")
public class WorkflowConfig extends BaseChangeableOpenmrsData {
	
	@OneToMany(mappedBy = "workflowConfig", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	Set<EligibilityCriteria> criteriaSet;
	
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
	
	@Column(nullable = false)
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
