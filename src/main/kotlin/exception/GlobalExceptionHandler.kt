package org.example.exception

import jakarta.servlet.http.HttpServletRequest
import org.example.dto.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.sql.SQLException
import java.time.ZonedDateTime
import java.util.function.Consumer


@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(ex: RuntimeException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val body = ErrorResponse(
            ZonedDateTime.now(),
            HttpStatus.NOT_FOUND.value(),
            HttpStatus.NOT_FOUND.reasonPhrase,
            ex.message,
            request.requestURI
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(
        ex: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val errors: MutableMap<String, String> = HashMap()

        ex.bindingResult.allErrors.forEach(Consumer { error: ObjectError? ->
            if (error is FieldError) {
                val fieldName = error.field
                val errorMessage = error.defaultMessage
                errors[fieldName] = errorMessage ?: "-"
            }
        })

        val body = ErrorResponse(
            ZonedDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.reasonPhrase,
            errors.toString(),
            request.requestURI
        )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(body)
    }

    @ExceptionHandler(SQLException::class)
    fun handleSqlException(ex: SQLException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val status = when (ex.sqlState) {
            "23505" -> {
                HttpStatus.CONFLICT
            }

            "23503" -> {
                HttpStatus.CONFLICT
            }

            else -> {
                HttpStatus.INTERNAL_SERVER_ERROR
            }
        }
        val body = ErrorResponse(
            timestamp = ZonedDateTime.now(),
            status.value(),
            status.reasonPhrase,
            ex.localizedMessage?.lines()?.firstOrNull() ?: "",
            request.requestURI
        )
        if (status == HttpStatus.INTERNAL_SERVER_ERROR) {
            return ResponseEntity.status(status).body(null)
        }
        return ResponseEntity.status(status)
            .body(body)
    }

    @ExceptionHandler(NegativeQuantityException::class)
    fun handleNegativeQuantityException(ex: NegativeQuantityException, request: HttpServletRequest)
            : ResponseEntity<ErrorResponse>{
        val status = HttpStatus.UNPROCESSABLE_ENTITY
        val body = ErrorResponse(
            timestamp = ZonedDateTime.now(),
            status.value(),
            status.reasonPhrase,
            ex.message ?: "",
            request.requestURI
        )
        return ResponseEntity.status(status).body(body)
    }
}