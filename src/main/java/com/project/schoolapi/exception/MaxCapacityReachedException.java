package com.project.schoolapi.exception;

public class MaxCapacityReachedException extends RuntimeException {
    public MaxCapacityReachedException(String message) {
        super(message);
    }
}
