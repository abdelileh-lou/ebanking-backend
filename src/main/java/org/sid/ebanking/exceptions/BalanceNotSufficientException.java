package org.sid.ebanking.exceptions;

import ch.qos.logback.core.encoder.EchoEncoder;

public class BalanceNotSufficientException extends Exception {
    public BalanceNotSufficientException(String message) {
         super(message);
    }
}
