package com.xantrix.webapp.exception;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ErrorResponse 
{
	private LocalDate date;
	private int code;
	private String message;
}
