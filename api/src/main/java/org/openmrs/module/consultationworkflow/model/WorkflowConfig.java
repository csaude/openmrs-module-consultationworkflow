package org.openmrs.module.consultationworkflow.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openmrs.BaseChangeableOpenmrsData;
import org.openmrs.FormResource;

import javax.persistence.*;
import java.util.Set;


@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "consultationworkflow_workflow_config")
public class WorkflowConfig extends BaseChangeableOpenmrsData {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer workflowConfigId;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false, length = 1000)
	private String description;

	private Boolean published = false;

	@Column(nullable = false)
	private String version;

	private FormResource formResource;

	@OneToMany(mappedBy = "workflowConfig", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	Set<EligibilityCriteria> criteriaSet;
	
	@Override
	public Integer getId() {
		return getWorkflowConfigId();
	}
	
	@Override
	public void setId(Integer id) {
		setWorkflowConfigId(id);
	}

}
