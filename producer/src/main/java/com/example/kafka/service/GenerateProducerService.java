package com.example.kafka.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.example.kafka.config.AppConfig;
import com.example.kafka.data.WorkforceData;
import com.example.kafka.data.ProviderWorkforceData;
import com.example.kafka.response.GenerateProducerResponse;

@Component
public class GenerateProducerService implements IGenerateProducerService {
    private static final Logger logger = LoggerFactory.getLogger(GenerateProducerService.class);

    public GenerateProducerResponse generateFromCsv() {
       try {
           logger.debug("loadRandom - Starting generate from csv...");

           GenerateProducerResponse response = new GenerateProducerResponse();
           response.workforce = new ProviderWorkforceData();
           CsvToJson generator = new CsvToJson();
           response.workforce.data = generator.generate();

           return response;
       }
       catch (Exception ex) {
           logger.error(TAG, ex);
           throw ex;
       }
       finally {
           logger.debug("loadRandom - Finished generate from csv");
       }
    }

    @Autowired
    private KafkaTemplate<String, Object> _kafkaTemplate;

    @Autowired
    private AppConfig _appConfig;

    private static final String TAG = GenerateProducerService.class.getName();
}
