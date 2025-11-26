package org.example.remedy.global.error;

import lombok.extern.slf4j.Slf4j;
import org.example.remedy.global.error.exception.BusinessBaseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ErrorResponse> handle(HttpRequestMethodNotSupportedException e) {
        logError(ErrorCode.METHOD_NOT_ALLOWED);
        return createErrorResponseEntity(ErrorCode.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handle(MethodArgumentNotValidException e) {
		String errorMessage = e.getFieldError().getDefaultMessage();

        logError(ErrorCode.INVALID_INPUT_VALUE, errorMessage);
        return createErrorResponseEntity(ErrorCode.INVALID_INPUT_VALUE, errorMessage);
    }

    @ExceptionHandler(BusinessBaseException.class)
    protected ResponseEntity<ErrorResponse> handle(BusinessBaseException e) {
        logError(e);
        return createErrorResponseEntity(e.getErrorCode());
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handle(Exception e) {
		logError(ErrorCode.INTERNAL_SERVER_ERROR);
        return createErrorResponseEntity(ErrorCode.INTERNAL_SERVER_ERROR);
    }

	private void logError(ErrorCode errorCode) {
		log.error("[ERROR] : { errorMessage : {}, errorCode : {} }", errorCode.getMessage(), errorCode.getCode());
	}

	private void logError(BusinessBaseException e) {
		log.error("[ERROR] : { errorMessage : {}, errorCode : {} }", e.getMessage(), e.getErrorCode());
	}

	private void logError(ErrorCode errorCode, String errorMessage) {
		log.error("[ERROR] : { errorMessage : {}, errorCode : {} }", errorMessage, errorCode.getCode());
	}

    private ResponseEntity<ErrorResponse> createErrorResponseEntity(ErrorCode errorCode) {
        return new ResponseEntity<>(
                ErrorResponse.of(errorCode),
                errorCode.getStatus());
    }

	private ResponseEntity<ErrorResponse> createErrorResponseEntity(ErrorCode errorCode, String message) {
		return new ResponseEntity<>(
			ErrorResponse.of(errorCode, message),
			errorCode.getStatus());
	}
}
