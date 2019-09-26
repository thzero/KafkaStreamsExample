package com.example.kafka.data;

import java.util.UUID;

import javax.validation.constraints.NotBlank;

import org.springframework.lang.NonNull;

public abstract class IdData extends BaseData {
    public IdData() {
        id = UUID.randomUUID().toString();
    }

    @NonNull
    @NotBlank
    public String id;
}
