package com.example.kafka.response;

public class Error {
    public Error() { }
    public Error(String message) {
        this.message = message;
    }

    public String message;

    @Override
    public String toString() {
        return message;
    }
}
