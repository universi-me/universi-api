package me.universi.subject.exception;

public class SubjectExistsException extends RuntimeException {

    private static final String SUBJECT_EXISTS = "Subject already exists!";
    public SubjectExistsException(){
        super(SUBJECT_EXISTS);
    }
}
