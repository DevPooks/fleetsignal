package dev.devpooks.fleetsignal.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiError> notFound(ResourceNotFoundException exception, HttpServletRequest request) {
		return response(HttpStatus.NOT_FOUND, exception.getCode(), exception.getMessage(), Map.of(), request);
	}

	@ExceptionHandler(ConflictException.class)
	public ResponseEntity<ApiError> conflict(ConflictException exception, HttpServletRequest request) {
		return response(HttpStatus.CONFLICT, exception.getCode(), exception.getMessage(), Map.of(), request);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> validation(MethodArgumentNotValidException exception, HttpServletRequest request) {
		Map<String, String> details = new LinkedHashMap<>();
		exception.getBindingResult().getFieldErrors()
				.forEach(error -> details.putIfAbsent(error.getField(), error.getDefaultMessage()));
		return response(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "Request validation failed.", details, request);
	}

	@ExceptionHandler({IllegalArgumentException.class, HttpMessageNotReadableException.class})
	public ResponseEntity<ApiError> badRequest(Exception exception, HttpServletRequest request) {
		String message = exception instanceof IllegalArgumentException
				? exception.getMessage()
				: "Request body is malformed or contains an invalid value.";
		return response(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", message, Map.of(), request);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> unexpected(Exception exception, HttpServletRequest request) {
		log.error("Unhandled request failure", exception);
		return response(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "The request could not be completed.",
				Map.of(), request);
	}

	private ResponseEntity<ApiError> response(HttpStatus status, String code, String message,
			Map<String, String> details, HttpServletRequest request) {
		String requestId = (String) request.getAttribute("requestId");
		return ResponseEntity.status(status).body(ApiError.of(code, message, details, requestId));
	}
}
