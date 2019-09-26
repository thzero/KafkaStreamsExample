package com.example.kafka.response;

public class ErrorResponse extends SuccessResponse implements IErrorResponse {
    public ErrorResponse() {
        super(false);
    }
    public ErrorResponse(String message) {
        setError(new Error(message));
    }
    public ErrorResponse(ISuccessResponse response) {
        setResponse(response);
    }
}
