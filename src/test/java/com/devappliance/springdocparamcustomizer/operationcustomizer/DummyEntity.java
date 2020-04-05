package com.devappliance.springdocparamcustomizer.operationcustomizer;

import java.util.List;

/**
 * @author Gibah Joseph
 * Email: gibahjoe@gmail.com
 * Mar, 2020
 **/
public class DummyEntity {
    private String name;
    private String code;
    private QdslPredicateOperationCustomizerTest.Status status;
    private ChildEntity child;
    private List<QdslPredicateOperationCustomizerTest.Status> notStatuses;

    public List<QdslPredicateOperationCustomizerTest.Status> getNotStatuses() {
        return notStatuses;
    }

    public DummyEntity setNotStatuses(List<QdslPredicateOperationCustomizerTest.Status> notStatuses) {
        this.notStatuses = notStatuses;
        return this;
    }

    public QdslPredicateOperationCustomizerTest.Status getStatus() {
        return status;
    }

    public DummyEntity setStatus(QdslPredicateOperationCustomizerTest.Status status) {
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
