package com.example.kafka.data;

import org.springframework.lang.NonNull;

public class PhoneData extends IdData {
    public void update(@NonNull PhoneData value) {
        this.phone = value.phone;
    }

    public String phone;
}
