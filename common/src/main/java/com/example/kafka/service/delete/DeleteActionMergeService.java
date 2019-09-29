package com.example.kafka.service.delete;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.example.kafka.data.ChangeTypes;
import com.example.kafka.data.WorkforceChangeRequestData;
import com.example.kafka.data.WorkforceData;
import com.example.kafka.response.ISuccessResponse;
import com.example.kafka.response.service.MergeResponse;
import com.example.kafka.service.BaseService;
import com.example.kafka.service.IActionMergeService;
import com.example.kafka.service.IDeleteSubActionMergeService;

@Service
public class DeleteActionMergeService extends BaseService implements IActionMergeService {
    private static final Logger logger = LoggerFactory.getLogger(DeleteActionMergeService.class);

    @Override
    public ChangeTypes getChangeTypeCd() {
        return ChangeTypes.Delete;
    }

    @Override
    public MergeResponse merge(WorkforceData workforce, @NonNull WorkforceChangeRequestData changeRequest) {
        MergeResponse response = new MergeResponse();
        try {
            ISuccessResponse validResponse = valid(changeRequest);
            if (!validResponse.isSuccess())
                return error(response, validResponse);

            Optional<IDeleteSubActionMergeService> service = _services.stream().filter(l -> l.getChangeSubTypeCd() == changeRequest.changeSubTypeCd).findFirst();
            if (!service.isPresent()) {
                logger.error(String.format("Invalid service for changeType '%s', changeSubTypeCd '%s'.", changeRequest.changeTypeCd.toString(), changeRequest.changeSubTypeCd.toString()));
                return error(response);
            }

            service.get().delete(workforce, changeRequest);
            changeRequest.snapshot = workforce;
            response.changeRequest = changeRequest;
            return response;
        }
        catch (Exception ex) {
            logger.error(TAG, ex);
        }

        return error(response);
    }

    @Override
    public ISuccessResponse valid(@NonNull WorkforceChangeRequestData changeRequest) {
        try {
            if (changeRequest.request == null)
                return error("Invalid 'request' element.");

            Optional<IDeleteSubActionMergeService> service = _services.stream().filter(l -> l.getChangeSubTypeCd() == changeRequest.changeSubTypeCd).findFirst();
            if (!service.isPresent()) {
                logger.error(String.format("Invalid service for changeType '%s', changeSubTypeCd '%s'.", changeRequest.changeTypeCd.toString(), changeRequest.changeSubTypeCd.toString()));
                return error();
            }

            return service.get().valid(changeRequest);
        }
        catch (Exception ex) {
            logger.error(TAG, ex);
        }

        return error();
    }

    @Autowired
    private List<IDeleteSubActionMergeService> _services;

    private static final String TAG = DeleteActionMergeService.class.getName();
}
