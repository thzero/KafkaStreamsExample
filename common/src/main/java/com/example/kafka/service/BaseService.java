package com.example.kafka.service;

import com.example.kafka.response.ErrorResponse;
import com.example.kafka.response.IErrorResponse;
import com.example.kafka.response.ISuccessResponse;
import com.example.kafka.response.SuccessResponse;

public abstract class BaseService {
    protected IErrorResponse error() {
        return new ErrorResponse();
    }
    protected IErrorResponse error(String message) {
        return new ErrorResponse(message);
    }
    protected IErrorResponse error(ISuccessResponse response) {
        return new ErrorResponse(response);
    }

    protected ISuccessResponse success() {
        return new SuccessResponse();
    }
}
