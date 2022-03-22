package si.smrpo.scrum.persistence.story;

import si.smrpo.scrum.persistence.BaseEntity;

import javax.persistence.*;

@Entity
@Table(name = "acceptance_tests")

public class AcceptanceTestEntity extends BaseEntity {

    @Column(name = "result", columnDefinition = "TEXT", nullable = false)
    private String result;

    @ManyToOne
    @JoinColumn(name = "story_id", nullable = false)
    private StoryEntity story;


}
