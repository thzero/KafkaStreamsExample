package com.example.kafka.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.simpleflatmapper.csv.CsvParser;

import org.springframework.core.io.ClassPathResource;

import com.example.kafka.data.SourceWorkforceData;
import com.example.kafka.data.WorkforceData;

public class CsvToJson {
    public List<WorkforceData> generate() {
        try {
            File resource = new ClassPathResource("us-500.csv").getFile();
            CsvParser
                    .mapTo(SourceWorkforceData.class)
                    .stream(
                            resource,
                            (s) -> {
                                s.forEach(this::add);
                                return null;
                            }
                    );
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        WorkforceData data;
        List<WorkforceData> output = new ArrayList<>();
        for (SourceWorkforceData input : _sourceInput) {
            input.id = UUID.randomUUID().toString();
            data = input.convert(new WorkforceData());
            output.add(data);
        }

        return output;
    }

    private void add(SourceWorkforceData value) {
        _sourceInput.add(value);
    }

    private List<SourceWorkforceData> _sourceInput = new ArrayList<>();
}