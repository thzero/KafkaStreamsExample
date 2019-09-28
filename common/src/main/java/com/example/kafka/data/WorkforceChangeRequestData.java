package com.example.kafka.data;

import javax.validation.constraints.NotBlank;

import org.springframework.lang.NonNull;

public class WorkforceChangeRequestData extends ChangeRequestData<WorkforceData> {
    public WorkforceChangeRequestData() {
    }
    public WorkforceChangeRequestData(@NonNull WorkforceData request, ChangeTypes changeTypeCd, ChangeSubTypes changeSubTypeCd, int index) {
        super(request, changeTypeCd, changeSubTypeCd, index);
    }
    public WorkforceChangeRequestData(@NonNull @NotBlank String id, @NonNull WorkforceChangeRequestData change, SplitTypes splitType) {
        super(id, change, splitType);
        this.request = change.request;
        this.snapshot = change.snapshot;
        this.processDate = change.processDate;
        this.processTimestamp = change.processTimestamp;
        this.requestedDate = change.requestedDate;
        this.requestedTimestamp = change.requestedTimestamp;
    }

    @Override
    public String toString() {
        return String.format("WorkforceChangeRequest::toString() { firstName='{}', lastName='{}', type='{}', id='{}', index='{}', requestId='{}' }",
                request != null ? request.firstName : null,
                request != null ? request.lastName : null,
                id,
                index,
                getWorkforceRequestId());
    }
}
