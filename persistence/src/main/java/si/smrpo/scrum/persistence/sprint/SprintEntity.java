package si.smrpo.scrum.persistence.sprint;

import si.smrpo.scrum.lib.enums.SimpleStatus;
import si.smrpo.scrum.persistence.BaseEntity;
import si.smrpo.scrum.persistence.project.ProjectEntity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "sprints")
public class SprintEntity extends BaseEntity {

    public static final String GET_BY_SPRINT_NAME = "SprintEntity.getByName";
    public static final String GET_SPRINTS_IN_PROJECT = "SprintEntity.getSprintsInProject";

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "start_date", nullable = false)
    private Date startDate;

    @Column(name = "end_date", nullable = false)
    private Date endDate;

    @Column(name = "expected_speed")
    private int expectedSpeed;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private SimpleStatus status;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectEntity project;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getExpectedSpeed() {
        return expectedSpeed;
    }

    public void setExpectedSpeed(int expectedSpeed) {
        this.expectedSpeed = expectedSpeed;
    }

    public SimpleStatus getStatus() {
        return status;
    }

    public void setStatus(SimpleStatus status) {
        this.status = status;
    }

    public ProjectEntity getProject() {
        return project;
    }

    public void setProject(ProjectEntity project) {
        this.project = project;
    }
}
