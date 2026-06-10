package com.shop.exception;

public class ResourceNotFoundException extends LocalizedException {

    public ResourceNotFoundException(String messageKey, Object... args) {
        super(messageKey, args);
    }
}
