package org.openmrs.module.consultationworkflow.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openmrs.BaseChangeableOpenmrsData;
import org.openmrs.Patient;
import org.openmrs.Visit;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "consultationworkflow_workflow_data")
public class WorkflowData extends BaseChangeableOpenmrsData {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "workflow_data_id", nullable = false)
	private Integer workflowDataId;
	
	@OneToOne
	@JoinColumn(name = "workflow_config_id", nullable = false)
	private WorkflowConfig workflowConfig;
	
	@ManyToOne
	@JoinColumn(name = "patient_id", nullable = false)
	private Patient patient;
	
	@ManyToOne
	@JoinColumn(name = "visit_id")
	private Visit visit;
	
	@OneToMany(mappedBy = "workflowData", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<WorkflowStepData> steps;
	
	public Set<WorkflowStepData> getCompletedSteps() {
        if (steps == null) {
            return new HashSet<>();
        }
        return steps.stream().filter(WorkflowStepData::isCompleted).collect(Collectors.toSet());
    }
	
	@Override
	public Integer getId() {
		return getWorkflowDataId();
	}
	
	@Override
	public void setId(Integer id) {
		setWorkflowDataId(id);
	}
	
}
