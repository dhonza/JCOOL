package org.ytoh.configurations;

/**
 * An exception signifying programming errors in using Configuration concepts.
 *
 * @author ytoh
 */
public class ConfigurationException extends RuntimeException {

    /**
     * Creates a new instance of <code>ConfigurationException</code> without
     * detail message.
     */
    public ConfigurationException() { }

    /**
     * Constructs an instance of <code>ConfigurationException</code> with
     * the specified detail message.
     * @param msg the detail message.
     */
    public ConfigurationException(String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of <code>ConfigurationException</code> with
     * the specified detail message and cause
     * @param message the detail message
     * @param cause the cause of this exception
     */
    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
