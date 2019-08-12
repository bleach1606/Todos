public class TaskModel {
    private String username;
    private String taskName;
    private String action;

    public TaskModel() {
    }

    public TaskModel(String username, String taskName, String action) {
        this.username = username;
        this.taskName = taskName;
        this.action = action;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
