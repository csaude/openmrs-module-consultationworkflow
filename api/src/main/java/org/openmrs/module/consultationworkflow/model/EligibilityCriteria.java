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
@Table(name = "workflow_eligibility_criteria")
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
    @JoinColumn(name = "workflow_id", nullable = false)
    private ConsultationWorkflowConfig workflow;

    @Override
    public Integer getId() {
        return getEligibilityCriteriaId();
    }

    @Override
    public void setId(Integer id) {
        setEligibilityCriteriaId(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EligibilityCriteria that = (EligibilityCriteria) o;
        return eligibilityCriteriaId.equals(that.eligibilityCriteriaId);
    }

    @Override
    public int hashCode() {
        return eligibilityCriteriaId.hashCode();
    }
}
