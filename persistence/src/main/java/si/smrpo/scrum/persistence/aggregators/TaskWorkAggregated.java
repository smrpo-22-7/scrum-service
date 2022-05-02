package si.smrpo.scrum.persistence.aggregators;

public class TaskWorkAggregated {
    
    private String taskId;
    
    private double hoursSum;
    
    private double remainingHoursSum;
    
    public TaskWorkAggregated() {
    
    }
    
    public TaskWorkAggregated(String taskId, double hoursSum, double remainingHoursSum) {
        this.taskId = taskId;
        this.hoursSum = hoursSum;
        this.remainingHoursSum = remainingHoursSum;
    }
    
    public String getTaskId() {
        return taskId;
    }
    
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
    
    public double getHoursSum() {
        return hoursSum;
    }
    
    public void setHoursSum(double hoursSum) {
        this.hoursSum = hoursSum;
    }
    
    public double getRemainingHoursSum() {
        return remainingHoursSum;
    }
    
    public void setRemainingHoursSum(double remainingHoursSum) {
        this.remainingHoursSum = remainingHoursSum;
    }
}
