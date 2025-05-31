package com.xantrix.webapp.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntityHasReservationsException extends Exception
{
    private static final long serialVersionUID = -8729169303699924451L;

    public EntityHasReservationsException()
    {
        super();
    }

    public EntityHasReservationsException(String messaggio)
    {
        super(messaggio);
    }
}
