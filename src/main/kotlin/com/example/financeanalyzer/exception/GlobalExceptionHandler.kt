package com.example.financeanalyzer.exception

import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(
        exception: NotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {
        return buildResponse(HttpStatus.NOT_FOUND, exception.message ?: "Resource not found", request)
    }

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequest(
        exception: BadRequestException,
        request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {
        return buildResponse(HttpStatus.BAD_REQUEST, exception.message ?: "Bad request", request)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationError(
        exception: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {
        val validationErrors = exception.bindingResult.allErrors
            .filterIsInstance<FieldError>()
            .associate { it.field to (it.defaultMessage ?: "Invalid value") }

        return buildResponse(
            status = HttpStatus.BAD_REQUEST,
            message = "Validation failed",
            request = request,
            validationErrors = validationErrors
        )
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(
        exception: ConstraintViolationException,
        request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {
        val validationErrors = exception.constraintViolations.associate {
            it.propertyPath.toString() to it.message
        }

        return buildResponse(
            status = HttpStatus.BAD_REQUEST,
            message = "Validation failed",
            request = request,
            validationErrors = validationErrors
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(
        exception: Exception,
        request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {
        return buildResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR,
            message = "Unexpected server error",
            request = request
        )
    }

    private fun buildResponse(
        status: HttpStatus,
        message: String,
        request: HttpServletRequest,
        validationErrors: Map<String, String> = emptyMap()
    ): ResponseEntity<ApiErrorResponse> {
        return ResponseEntity.status(status).body(
            ApiErrorResponse(
                timestamp = LocalDateTime.now(),
                status = status.value(),
                error = status.reasonPhrase,
                message = message,
                path = request.requestURI,
                validationErrors = validationErrors.takeIf { it.isNotEmpty() }
            )
        )
    }
}

data class ApiErrorResponse(
    val timestamp: LocalDateTime,
    val status: Int,
    val error: String,
    val message: String,
    val path: String,
    val validationErrors: Map<String, String>? = null
)
