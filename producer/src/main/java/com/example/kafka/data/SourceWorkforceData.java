package com.example.kafka.data;

import javax.validation.constraints.NotBlank;

import org.springframework.lang.NonNull;

public class SourceWorkforceData extends IdData {
    public WorkforceData convert(@NonNull WorkforceData data) {
        data.id = id;

        data.firstName = firstName;
        data.lastName = lastName;
        data.networkDomain = networkDomain;
        data.networkId = networkId;

        AddressData addressData = new AddressData();
        addressData.address = address;
        addressData.city = city;
        addressData.county = county;
        addressData.state = state;
        addressData.zip = zip;
        data.addresses.add(addressData);

        EmailData emailData = new EmailData();
        emailData.email = email;
        data.emails.add(emailData);

        PhoneData phoneData = new PhoneData();
        phoneData.phone = phone1;
        data.phones.add(phoneData);

        phoneData = new PhoneData();
        phoneData.phone = phone2;
        data.phones.add(phoneData);

        return data;
    }

    public String address;
    public String city;
    public String county;
    public String company;
    public String email;
    @NonNull @NotBlank
    public String firstName;
    @NonNull @NotBlank
    public String id;
    @NonNull @NotBlank
    public String lastName;
    public String networkDomain;
    public String networkId;
    public String phone1;
    public String phone2;
    public String state;
    public String zip;
}
