package com.example.kafka.data;

public class ChangeTypeData extends IdData {
    public ChangeTypeData() {
    }
    public ChangeTypeData(ChangeTypes changeTypeCd) {
        this(changeTypeCd, null);
    }
    public ChangeTypeData(ChangeTypes changeTypeCd, ChangeSubTypes elementTypeCd) {
        this.changeTypeCd = changeTypeCd;
        this.elementTypeCd = elementTypeCd;
    }

    public ChangeTypes changeTypeCd; // add? delete? update?
    public ChangeSubTypes elementTypeCd; // what block of data?
}
