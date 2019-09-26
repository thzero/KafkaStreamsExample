package com.example.kafka.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.lang.NonNull;

public class WorkforceData extends IdData {
    @Override
    public String toString() {
        return String.format("Workforce::toString() { firstName='%s', lastName='%s', type='%s', id='%s' }", firstName, lastName, typeEntCd, id);
    }

    public List<AddressData> addresses = new ArrayList<>();
    public String company;
    public List<EmailData> emails = new ArrayList<>();
    @NonNull @NotBlank
    public String firstName;
    @NonNull @NotBlank
    public String lastName;
    public Date lastUpdatedDate;
    public long lastUpdatedTimestamp;
    public String networkDomain;
    public String networkId;
    public StatusType status = StatusType.Active;
    public String typeEntCd = "01";
    public List<PhoneData> phones = new ArrayList<>();
}