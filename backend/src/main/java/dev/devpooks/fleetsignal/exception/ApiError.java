package dev.devpooks.fleetsignal.exception;

import java.time.Instant;
import java.util.Map;

public record ApiError(ErrorBody error) {
	public record ErrorBody(String code, String message, Map<String, String> details, String requestId,
			Instant timestamp) {
	}

	public static ApiError of(String code, String message, Map<String, String> details, String requestId) {
		return new ApiError(new ErrorBody(code, message, details, requestId, Instant.now()));
	}
}
