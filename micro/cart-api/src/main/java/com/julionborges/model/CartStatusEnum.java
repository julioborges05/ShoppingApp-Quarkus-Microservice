package com.julionborges.model;

public enum CartStatusEnum {
    PENDING("Pending"),
    FINISHED("Finished"),
    ABORTED("Aborted");

    final String value;

    CartStatusEnum(String value) {
        this.value = value;
    }
}
