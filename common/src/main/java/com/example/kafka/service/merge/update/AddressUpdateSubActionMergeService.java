package com.example.kafka.service.merge.update;

import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.example.kafka.data.ChangeSubTypes;
import com.example.kafka.data.WorkforceChangeRequestData;
import com.example.kafka.data.AddressData;
import com.example.kafka.data.WorkforceData;
import com.example.kafka.response.ISuccessResponse;
import com.example.kafka.service.merge.BaseActionMergeService;
import com.example.kafka.service.merge.IUpdateSubActionMergeService;

@Service
public class AddressUpdateSubActionMergeService extends BaseActionMergeService implements IUpdateSubActionMergeService {
    @Override
    public ChangeSubTypes getChangeSubTypeCd() {
        return ChangeSubTypes.Address;
    }

    @Override
    public ISuccessResponse update(WorkforceData workforce, @NonNull WorkforceChangeRequestData changeRequest) {
        ISuccessResponse response = valid(changeRequest);
        if (!response.isSuccess())
            return error(response);

        if (workforce == null)
            return error("Invalid workforce object.");

        Optional<AddressData> address;
        for (AddressData item : changeRequest.request.addresses) {
            address = workforce.addresses.stream().filter(l -> l.id.equalsIgnoreCase(item.id)).findFirst();
            if (!address.isPresent()) {
                workforce.addresses.add(item);
                continue;
            }

            address.get().update(item);
        }

        return success();
    }

    @Override
    public ISuccessResponse valid(@NonNull WorkforceChangeRequestData changeRequest) {
        ISuccessResponse isValid = super.valid(changeRequest);
        if (!isValid.isSuccess())
            return isValid;

        if ((changeRequest.request.addresses != null) && (changeRequest.request.addresses.size() > 0))
            return success();

        return error("Invalid 'request.address' element - most contain at least one address node.");
    }
}
