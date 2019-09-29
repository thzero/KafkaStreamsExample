package com.example.kafka.data;

import java.util.Date;
import java.util.UUID;

import javax.validation.constraints.NotBlank;

import org.springframework.lang.NonNull;

public abstract class ChangeRequestData<T extends IdData> extends IdData {
    public ChangeRequestData() {
        this.id = UUID.randomUUID().toString();
    }
    public ChangeRequestData(@NonNull T request, ChangeTypes changeTypeCd, ChangeSubTypes changeSubTypeCd, int index) {
        this();
        this.changeTypeCd = changeTypeCd;
        this.changeSubTypeCd = changeSubTypeCd;
        this.index = index;
        this.request = request;
    }
    public ChangeRequestData(@NonNull @NotBlank String id, @NonNull ChangeRequestData<T> changeRequest, SplitTypes splitType) {
        this();
        this.id = id;
        this.changeSubTypeCd = changeRequest.changeSubTypeCd;
        this.changeTypeCd = changeRequest.changeTypeCd;
        this.index = changeRequest.index;
        this.request = changeRequest.request;
        this.splitType = splitType;
    }

    public String getWorkforceRequestId() {
        return request != null ? request.id : null;
    }

    @Override
    public String toString() {
        return String.format("WorkforceChangeRequest::toString() { id='%s', changeType='%s', changeSubType='%s', index='%s' }", request.id, changeTypeCd, changeSubTypeCd, index);
    }

    public T request;
    public T snapshot;

    public ChangeSubTypes changeSubTypeCd; // what allowed data type update?
    public ChangeTypes changeTypeCd; // add? delete? update?
    public int index;
    public Date processDate;
    public ProcessStatus processStatus = ProcessStatus.Initial;
    public long processTimestamp;
    public Date requestedDate;
    public long requestedTimestamp;
    public Status status = Status.Initial;
    public SplitTypes splitType;

    public enum ProcessStatus {
        Initial,
        Deleted,
        Failed,
        Found,
        Merged,
        MergeFailed,
        NotFound,
        Success,
        Stored,
        StoreFailed
    }

    public enum Status {
        Initial,
        MergeFailed,
        NotFound
    }
}
