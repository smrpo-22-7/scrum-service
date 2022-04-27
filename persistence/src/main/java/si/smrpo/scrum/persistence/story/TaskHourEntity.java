package si.smrpo.scrum.persistence.story;

import si.smrpo.scrum.persistence.BaseEntity;
import si.smrpo.scrum.persistence.users.UserEntity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tasks_hours", indexes = {
    @Index(name = "INDEX_TASK_HOURS_USER_ID", columnList = "user_id")
})
@NamedQueries({
    @NamedQuery(name = TaskHourEntity.GET_ACTIVE_TASK, query = "SELECT t FROM TaskHourEntity t WHERE t.endDate IS NULL AND t.user.id = :userId")
})
public class TaskHourEntity extends BaseEntity {
    
    public static final String GET_ACTIVE_TASK = "TaskHourEntity.getActiveTask";
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_date")
    private Date startDate;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_date")
    private Date endDate;
    
    @Column(name = "amount")
    private Double amount;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
    
    @ManyToOne
    @JoinColumn(name = "task_id")
    private TaskEntity task;
    
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
    
    public Double getAmount() {
        return amount;
    }
    
    public void setAmount(Double amount) {
        this.amount = amount;
    }
    
    public UserEntity getUser() {
        return user;
    }
    
    public void setUser(UserEntity user) {
        this.user = user;
    }
    
    public TaskEntity getTask() {
        return task;
    }
    
    public void setTask(TaskEntity task) {
        this.task = task;
    }
}
