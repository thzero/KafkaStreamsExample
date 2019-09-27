package com.example.kafka.service;

import com.example.kafka.response.ErrorResponse;
import com.example.kafka.response.ISuccessResponse;
import com.example.kafka.response.SuccessResponse;

public abstract class BaseService {
    protected ISuccessResponse error() {
        return new ErrorResponse();
    }
    protected ISuccessResponse error(String message) {
        return new ErrorResponse(message);
    }
    protected ISuccessResponse error(ISuccessResponse response) {
        return new ErrorResponse(response);
    }
    protected <T extends SuccessResponse> T error(T response) {
        response.setSuccess(true);
        return response;
    }
    protected <T extends SuccessResponse> T error(T response, String message) {
        response.setError(message);
        return response;
    }
    protected <T extends SuccessResponse> T error(T response, ISuccessResponse responseError) {
        response.setResponse(responseError);
        return response;
    }

    protected ISuccessResponse success() {
        return new SuccessResponse();
    }
}
