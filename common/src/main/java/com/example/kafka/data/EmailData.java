package com.example.kafka.data;

import org.springframework.lang.NonNull;

public class EmailData extends IdData {
    public void update(@NonNull EmailData value) {
        this.email = value.email;
    }

    public String email;
}
