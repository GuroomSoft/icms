package com.guroomsoft.icms.http.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.sql.Timestamp;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionResponse {
    private Timestamp timestamp;
    private Integer status;
    private String error;
    private String path;
    private String message;

    public ExceptionResponse(HttpStatus httpStatus, BindingResult bindingResult, String path) {
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.status = httpStatus.value();
        this.error = httpStatus.name();
        this.path = path;
        this.message = createErrorMessage(bindingResult);
    }

    public ExceptionResponse(HttpStatus httpStatus, String reason, String path) {
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.status = httpStatus.value();
        this.error = httpStatus.name();
        this.path = path;
        this.message = reason;
    }

    private static String createErrorMessage(BindingResult bindingResult){
        StringBuilder stringBuilder = new StringBuilder();
        boolean isFirst = true;

        for(FieldError fieldError: bindingResult.getFieldErrors()) {
            if (!isFirst) {
                stringBuilder.append(", ");
            } else {
                isFirst = false;
            }

            stringBuilder.append("[");
            stringBuilder.append(fieldError.getField());
            stringBuilder.append("]");
            stringBuilder.append(fieldError.getDefaultMessage());
            stringBuilder.append(" ");
        }

        return stringBuilder.toString();
    }
}
