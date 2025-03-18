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
@Table(name = "consultationworkflow_workflow_step_data")
public class WorkflowStepData extends BaseChangeableOpenmrsData {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "workflow_step_data_id", nullable = false)
	private Integer workflowStepDataId;
	
	@ManyToOne
	@JoinColumn(name = "workflow_data_id", nullable = false)
	private WorkflowData workflowData;
	
	@Column(name = "step_id", nullable = false)
	private String stepId;
	
	@Column(name = "stepName", nullable = false)
	private String stepName;
	
	@Column(name = "renderType", nullable = false)
	private String renderType;
	
	@Column(name = "data_reference")
	private String dataReference;
	
	private Boolean completed = false;
	
	@Override
	public Integer getId() {
		return getWorkflowStepDataId();
	}
	
	@Override
	public void setId(Integer id) {
		setWorkflowStepDataId(id);
	}
}
