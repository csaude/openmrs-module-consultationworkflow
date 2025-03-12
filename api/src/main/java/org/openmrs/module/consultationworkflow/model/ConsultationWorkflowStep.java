package org.openmrs.module.consultationworkflow.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openmrs.BaseChangeableOpenmrsData;

import javax.persistence.*;
import java.util.Objects;

@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "workflow_eligibility_criteria")
public class ConsultationWorkflowStep extends BaseChangeableOpenmrsData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "consultation_workflow_step_id", nullable = false)
    private Integer consultationWorkflowStepId;

    @ManyToOne
    @JoinColumn(name = "consultation_workflow_id", nullable = false)
    private ConsultationWorkflow consultationWorkflow;

    private String stepId;

    private String stepName;

    private String renderType;

    private String dataReference;

    private boolean completed;

    @Override
    public Integer getId() {
        return getConsultationWorkflowStepId();
    }

    @Override
    public void setId(Integer id) {
        setConsultationWorkflowStepId(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConsultationWorkflowStep that = (ConsultationWorkflowStep) o;
        return Objects.equals(consultationWorkflowStepId, that.consultationWorkflowStepId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(consultationWorkflowStepId);
    }
}
