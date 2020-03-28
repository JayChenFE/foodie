package org.n3r.idworker;

/**
 * @author jaychenfe
 */
public class InvalidSystemClockException extends RuntimeException {
    public InvalidSystemClockException(String message) {
        super(message);
    }
}
