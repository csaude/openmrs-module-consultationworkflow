package org.openmrs.module.consultationworkflow.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openmrs.BaseChangeableOpenmrsData;

import javax.persistence.*;


@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "consultation_workflow_config")
public class ConsultationWorkflowConfig extends BaseChangeableOpenmrsData {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer workflowId;
	
	private String name;
	
	private String description;
	
	private Boolean published = false;

	private String version;
	
	@Override
	public Integer getId() {
		return getWorkflowId();
	}
	
	@Override
	public void setId(Integer id) {
		setWorkflowId(id);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ConsultationWorkflowConfig that = (ConsultationWorkflowConfig) o;
		return workflowId.equals(that.workflowId);
	}

	@Override
	public int hashCode() {
		return workflowId.hashCode();
	}
}
