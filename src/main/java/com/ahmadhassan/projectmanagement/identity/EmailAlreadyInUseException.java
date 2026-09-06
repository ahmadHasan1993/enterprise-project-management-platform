package com.ahmadhassan.projectmanagement.identity;

public class EmailAlreadyInUseException extends RuntimeException {

    public EmailAlreadyInUseException() {
        super("Email address is already in use");
    }
}