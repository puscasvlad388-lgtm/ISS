package com.bitewise.exception;

/** Exceptie pentru reguli de business incalcate (ex: stoc insuficient). */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
