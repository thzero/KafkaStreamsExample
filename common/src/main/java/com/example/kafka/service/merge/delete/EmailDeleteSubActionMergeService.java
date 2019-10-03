package com.example.kafka.service.merge.delete;

import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.example.kafka.data.EmailData;
import com.example.kafka.data.ChangeSubTypes;
import com.example.kafka.data.WorkforceChangeRequestData;
import com.example.kafka.data.WorkforceData;
import com.example.kafka.response.ISuccessResponse;
import com.example.kafka.service.merge.BaseActionMergeService;
import com.example.kafka.service.merge.IDeleteSubActionMergeService;

@Service
public class EmailDeleteSubActionMergeService extends BaseActionMergeService implements IDeleteSubActionMergeService {
    @Override
    public ChangeSubTypes getChangeSubTypeCd() {
        return ChangeSubTypes.Email;
    }

    @Override
    public ISuccessResponse delete(WorkforceData workforce, @NonNull WorkforceChangeRequestData changeRequest) {
        ISuccessResponse response = valid(changeRequest);
        if (!response.isSuccess())
            return error(response);

        if (workforce == null)
            return error("Invalid workforce object.");

        Optional<EmailData> email;
        for (EmailData item : changeRequest.request.emails) {
            email = workforce.emails.stream().filter(l -> l.id.equalsIgnoreCase(item.id)).findFirst();
            if (!email.isPresent())
                continue;;

            workforce.emails.remove(email.get());
        }

        return success();
    }

    @Override
    public ISuccessResponse valid(@NonNull WorkforceChangeRequestData changeRequest) {
        ISuccessResponse isValid = super.valid(changeRequest);
        if (!isValid.isSuccess())
            return isValid;

        if ((changeRequest.request.emails != null) && (changeRequest.request.emails.size() > 0))
            return success();

        return error("Invalid 'request.email' element - most contain at least one email node.");
    }
}
