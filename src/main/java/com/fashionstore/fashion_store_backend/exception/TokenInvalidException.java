package com.fashionstore.fashion_store_backend.exception;

public class TokenInvalidException extends  RuntimeException{
    public TokenInvalidException(String message) {
        super(message);
    }
}
