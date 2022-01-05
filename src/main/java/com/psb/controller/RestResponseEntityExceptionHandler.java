package com.psb.controller;

import com.psb.exception.AWSS3ClientNotFoundException;
import com.psb.exception.LimitTooHighException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.psb.exception.AWSS3ClientException;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(value = { AWSS3ClientException.class })
	protected ResponseEntity<Object> handleS3Exception(AWSS3ClientException ex) {
		ExceptionResponse res = new ExceptionResponse("Error calling S3. Try again later. " + ex.getMessage(),
				HttpStatus.SERVICE_UNAVAILABLE);
		return new ResponseEntity<>(res, HttpStatus.SERVICE_UNAVAILABLE);
	}

	@ExceptionHandler(value = { AWSS3ClientNotFoundException.class })
	protected ResponseEntity<Object> handleS3NotFoundException(AWSS3ClientNotFoundException ex) {
		ExceptionResponse res = new ExceptionResponse("Error calling S3 404 Not Found. " + ex.getMessage(),
				HttpStatus.NOT_FOUND);
		return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(value = { LimitTooHighException.class })
	protected ResponseEntity<Object> handleLimitTooHighException(LimitTooHighException ex) {
		ExceptionResponse res = new ExceptionResponse(ex.getMessage(),
				HttpStatus.BAD_REQUEST);
		return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
	}
}
