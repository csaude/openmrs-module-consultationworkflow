package org.openmrs.module.consultationworkflow.model;

import lombok.EqualsAndHashCode;
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
@Table(name = "consultation_workflow_config")
public class ConsultationWorkflow extends BaseChangeableOpenmrsData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "consultation_workflow_id", nullable = false)
    private Integer consultationWorkflowId;

    @OneToOne
    @JoinColumn(name = "config_id", nullable = false)
    private ConsultationWorkflowConfig config;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "visit_id")
    private Visit visit;

    @OneToMany(mappedBy = "consultationWorkflow", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ConsultationWorkflowStep> steps;

    public Set<ConsultationWorkflowStep> getCompletedSteps() {
        if (steps == null) {
            return new HashSet<>();
        }
        return steps.stream().filter(ConsultationWorkflowStep::isCompleted).collect(Collectors.toSet());
    }

    @Override
    public Integer getId() {
        return getConsultationWorkflowId();
    }

    @Override
    public void setId(Integer id) {
        setConsultationWorkflowId(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConsultationWorkflow that = (ConsultationWorkflow) o;
        return consultationWorkflowId.equals(that.consultationWorkflowId);
    }

    @Override
    public int hashCode() {
        return consultationWorkflowId.hashCode();
    }
}