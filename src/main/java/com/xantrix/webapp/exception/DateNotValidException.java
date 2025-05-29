package com.xantrix.webapp.exception;

public class DateNotValidException extends Exception {

    private static final long serialVersionUID = -5198072630604345819L;

    private String messaggio;

    public DateNotValidException()
    {
        super();
    }

    public DateNotValidException(String messaggio)
    {
        super(messaggio);
        this.messaggio = messaggio;
    }
}
