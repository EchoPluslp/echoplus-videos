package com.echoplus.utils;

public enum VideoStatus {
    SUCCESS(1),
    FORBID(2);
    public final int value;

    VideoStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
