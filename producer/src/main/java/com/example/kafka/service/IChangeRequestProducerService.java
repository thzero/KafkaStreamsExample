package com.example.kafka.service;

import java.util.List;

import com.example.kafka.data.WorkforceChangeRequestData;
import com.example.kafka.response.UpdateProducerResponse;

public interface IChangeRequestProducerService extends IService {
    UpdateProducerResponse submit(List<WorkforceChangeRequestData> changesRequests) throws Exception;
}
