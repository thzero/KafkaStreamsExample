package com.example.kafka.service.load.kafka;

import java.io.File;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.example.kafka.config.AppConfig;
import com.example.kafka.data.ProviderWorkforceData;
import com.example.kafka.data.WorkforceData;
import com.example.kafka.response.load.LoadWorkforceResponse;
import com.example.kafka.service.BaseService;
import com.example.kafka.service.load.IProducerLoadWorkforceService;

@Component
public class LoadProducerService extends BaseService implements IProducerLoadWorkforceService {
    private static final Logger logger = LoggerFactory.getLogger(LoadProducerService.class);

    public LoadWorkforceResponse loadJson() throws Exception {
        try {
            logger.debug("loadJson - Starting load of workforce data from json...");

            LoadWorkforceResponse response = new LoadWorkforceResponse();
            File resource = new ClassPathResource("data.json").getFile();
            ObjectMapper mapper = new ObjectMapper();
            response.workforce = mapper.readValue(resource, ProviderWorkforceData.class);
            for (WorkforceData data : response.workforce.data) {
                logger.debug("id: '{}' | data: {}", data.id, data.toString());
                _kafkaTemplate.send(_appConfig.loadTopic, data.id, data);
                Thread.sleep(_appConfig.waitDelay);
            }

            return response;
        }
        catch (Exception ex) {
            logger.error(TAG, ex);
            throw ex;
        }
        finally {
            logger.debug("loadJson - Finished load of workforce data from json");
        }
    }

    public LoadWorkforceResponse loadRandom() throws Exception {
        try {
            logger.debug("loadRandom - Starting load of random workforce...");

            LoadWorkforceResponse response = new LoadWorkforceResponse();
            response.workforce = new ProviderWorkforceData();
//            final NameGenerator generator = new NameGenerator();
//            IntStream.range(0, _appConfig.messagesPerRequest)
//                .forEach(i -> {
//                    UUID id = UUID.randomUUID();
//                    Name name = generator.generateName();
//                    WorkforceData data = new WorkforceData(name.toString(), id.toString(), i);
//                    response.workforce.data.add(data);
//                    logger.debug("index: {} | id: '{}' | data: {}", i, id.toString(), data.toString());
//                    _kafkaTemplate.send(_appConfig.loadTopic, id.toString(), data);
//                    Thread.sleep(_appConfig.waitDelay);
//                });

            return response;
        }
        catch (Exception ex) {
            logger.error(TAG, ex);
            throw ex;
        }
        finally {
            logger.debug("loadRandom - Finished load of random workforce");
        }
    }

    @Autowired
    private KafkaTemplate<String, Object> _kafkaTemplate;

    @Autowired
    private AppConfig _appConfig;

    private static final String TAG = LoadProducerService.class.getName();
}
