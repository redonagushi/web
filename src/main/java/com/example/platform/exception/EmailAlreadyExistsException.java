//Hidhet kur tenton të regjistrosh user me email që ekziston.
package com.example.platform.exception;

public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}

