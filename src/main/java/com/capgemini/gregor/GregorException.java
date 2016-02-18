package com.capgemini.gregor;

/**
 * Generic gregor exception.
 * 
 * @author craigwilliams84
 *
 */
public class GregorException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 2913922929166435340L;

    public GregorException(String message) {
        super(message);
    }

    public GregorException(String message, Throwable cause) {
        super(message, cause);
    }
}
