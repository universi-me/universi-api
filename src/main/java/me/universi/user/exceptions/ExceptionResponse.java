package me.universi.user.exceptions;

public class ExceptionResponse extends RuntimeException {

    public String redirectTo;
    public Integer status;

    public ExceptionResponse(String m) {
        super(m);
    }

    public ExceptionResponse(String m, String redirectTo) {
        super(m);
        this.redirectTo = redirectTo;
    }

    public ExceptionResponse(String m, String redirectTo, Integer status) {
        super(m);
        this.redirectTo = redirectTo;
        this.status = status;
    }
}