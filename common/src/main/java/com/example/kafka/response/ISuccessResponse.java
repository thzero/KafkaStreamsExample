package com.example.kafka.response;

public interface ISuccessResponse extends IResponse {
     boolean isSuccess();
     Error getError();
}
