package com.psb.exception;

public class LimitTooHighException extends Exception{

    private static final long serialVersionUID = 6934161939578268655L;
    private final String errorMessage;

    public LimitTooHighException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "Error: " + this.errorMessage;
    }

}
