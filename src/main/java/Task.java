public class Task {
    private String taskName;

    public Task(String taskName) {
        setTaskName(taskName);
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskName() {
        return taskName;
    }
}
