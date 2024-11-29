package me.universi.job.exceptions;

public class JobException extends RuntimeException {
    private static final long serialVersionUID = -6235234248428238689L;

    public JobException(String message) {
        super(message);
    }
}
