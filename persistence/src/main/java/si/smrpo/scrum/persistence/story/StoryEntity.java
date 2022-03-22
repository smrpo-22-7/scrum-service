package si.smrpo.scrum.persistence.story;

import org.w3c.dom.Text;
import si.smrpo.scrum.lib.enums.SimpleStatus;
import si.smrpo.scrum.lib.enums.StoryPriority;
import si.smrpo.scrum.persistence.BaseEntity;

import javax.persistence.*;

@Entity
@Table(name = "stories")
public class StoryEntity extends BaseEntity {

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private SimpleStatus status;

    @Column(name = "business_value", nullable = false)
    private int businessValue;

    @Column(name = "priority", nullable = false)
    @Enumerated(EnumType.STRING)
    private StoryPriority priority;


}

