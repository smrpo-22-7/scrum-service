package si.smrpo.scrum.integrations.auth.models.errors;

public class ServletException extends RuntimeException {
    
    private final int status;
    
    private final String error;
    
    private Throwable cause;
    
    public ServletException(int status, String error) {
        this.status = status;
        this.error = error;
    }
    
    public ServletException(int status, String error, Throwable cause) {
        this.status = status;
        this.error = error;
        this.cause = cause;
    }
    
    public int getStatus() {
        return status;
    }
    
    public String getError() {
        return error;
    }
    
    @Override
    public Throwable getCause() {
        return cause;
    }
}
