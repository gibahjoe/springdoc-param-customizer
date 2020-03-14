package com.devappliance.springdocparamcustomixer.operationcustomizer;

import java.util.List;

/**
 * @author Gibah Joseph
 * Email: gibahjoe@gmail.com
 * Mar, 2020
 **/
public class DummyEntity {
    private String name;
    private String code;
    private QdslPredicateCustomizerTest.Status status;
    private ChildEntity child;
    private List<QdslPredicateCustomizerTest.Status> notStatuses;

    public List<QdslPredicateCustomizerTest.Status> getNotStatuses() {
        return notStatuses;
    }

    public DummyEntity setNotStatuses(List<QdslPredicateCustomizerTest.Status> notStatuses) {
        this.notStatuses = notStatuses;
        return this;
    }

    public QdslPredicateCustomizerTest.Status getStatus() {
        return status;
    }

    public DummyEntity setStatus(QdslPredicateCustomizerTest.Status status) {
        this.status = status;
        return this;
    }

    public ChildEntity getChild() {
        return child;
    }

    public DummyEntity setChild(ChildEntity child) {
        this.child = child;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
