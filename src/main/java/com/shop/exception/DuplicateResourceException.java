package com.shop.exception;

public class DuplicateResourceException extends LocalizedException {

    public DuplicateResourceException(String messageKey, Object... args) {
        super(messageKey, args);
    }
}
