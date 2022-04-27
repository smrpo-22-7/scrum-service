package si.smrpo.scrum.persistence.story;

import si.smrpo.scrum.persistence.BaseEntity;
import si.smrpo.scrum.persistence.users.UserEntity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "task_work_spent")
@NamedQueries({
    @NamedQuery(name = TaskWorkSpentEntity.GET_BY_TASK_ID,
        query = "SELECT t FROM TaskWorkSpentEntity t WHERE t.user.id = :userId AND " +
            "t.task.id = :taskId")
})
public class TaskWorkSpentEntity extends BaseEntity {
    
    public static final String GET_BY_TASK_ID = "TaskWorkSpentEntity.getByTaskId";
    
    @Column(name = "work_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date workDate;
    
    @Column(name = "amount")
    private double amount;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
    
    @ManyToOne
    @JoinColumn(name = "task_id")
    private TaskEntity task;
    
    public Date getWorkDate() {
        return workDate;
    }
    
    public void setWorkDate(Date workDate) {
        this.workDate = workDate;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
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
