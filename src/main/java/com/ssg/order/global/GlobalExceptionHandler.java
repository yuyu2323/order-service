package com.ssg.order.global;

import com.ssg.order.exception.OrderException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OrderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleCustomException(OrderException e) {
        return ApiResponse.error(e.getMessage());
    }

    // Optional: 다른 Exception도 처리
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<?> handleGenericException(Exception e) {
        return ApiResponse.error("서버 내부 오류가 발생했습니다.");
    }
}
