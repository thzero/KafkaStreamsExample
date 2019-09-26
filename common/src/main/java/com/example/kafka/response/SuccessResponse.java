package com.example.kafka.response;

import org.springframework.lang.NonNull;

public class SuccessResponse implements ISuccessResponse {
    public SuccessResponse() { }
    public SuccessResponse(boolean success) {
        _success = success;
    }

    public Error getError() {
        return error;
    }

    public boolean isSuccess() { return _success; }

    public void setError(@NonNull Error error) {
        this.error = error != null ? error : new Error();
        _success = false;
    }

    public void setResponse(@NonNull ISuccessResponse response) {
        this.error = response != null ? response.getError() != null ? response.getError() : new Error() : new Error();
        _success = false;
    }

    public void setSuccess(boolean success) { _success = success; }

    private Error error;
    private boolean _success = true;
}
