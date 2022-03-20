package com.chartographer.controllers;

import com.chartographer.exceptions.BadFolderException;
import com.chartographer.exceptions.ChartaNotFoundException;
import com.chartographer.exceptions.FragmentNotFoundException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.ServletException;
import javax.validation.ValidationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ControllerAdvice
public class ExcpetionAdvice {

	/**
	 * Обработчик исключений
	 * @param exception
	 * @return
	 */
	@ExceptionHandler({BadFolderException.class, ChartaNotFoundException.class})
	public ResponseEntity notFoundHandler(Exception exception) {
		return ResponseEntity
				.status(HttpStatus.NOT_FOUND)
				.body(exception.getMessage());
	}

	@ExceptionHandler({FragmentNotFoundException.class})
	public ResponseEntity badRequestByFragmentPosition(FragmentNotFoundException fragmentNotFoundException) {
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(fragmentNotFoundException.getMessage());
	}

	@ExceptionHandler({IOException.class, ValidationException.class, ServletException.class,
			HttpMessageConversionException.class, TypeMismatchException.class})
	public ResponseEntity badRequestHandler() {
		return ResponseEntity.badRequest().build();
	}
}
