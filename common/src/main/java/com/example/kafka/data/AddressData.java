package com.example.kafka.data;

import org.springframework.lang.NonNull;

public class AddressData extends IdData {
    public void update(@NonNull AddressData value) {
        this.address = value.address;
        this.city = value.city;
        this.county = value.county;
        this.state = value.state;
        this.zip = value.zip;
    }

    public String address;
    public String city;
    public String county;
    public String state;
    public String zip;
}
