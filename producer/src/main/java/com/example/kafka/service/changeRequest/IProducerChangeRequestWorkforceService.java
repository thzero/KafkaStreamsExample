package com.example.kafka.service.changeRequest;

import java.util.List;

import com.example.kafka.data.WorkforceChangeRequestData;
import com.example.kafka.response.changeRequest.UpdateProducerResponse;
import com.example.kafka.service.IService;

public interface IProducerChangeRequestWorkforceService extends IService {
    UpdateProducerResponse submit(List<WorkforceChangeRequestData> changesRequests) throws Exception;
}
